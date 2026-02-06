package core;

import dictionary.ForbiddenKeywordPolicy;
import dictionary.PiiKeywordDictionary;
import util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 평문 로그에서 key=value(또는 key: value) 형태를 파싱하고,
 * key에 따라 금지 키 처리 또는 PII 타입 판별 후 해당 마스커를 적용해 한 줄을 마스킹한다.
 */
public final class KeywordBasedLogMaskingEngine {

    /** key=value / key: value 토큰 파싱. key: 영문·숫자·밑줄·하이픈·한글. value: 공백/쉼표/괄호에서 종료. */
    private static final Pattern KV =
            Pattern.compile("(?i)([a-z0-9_\\-가-힣]+)\\s*(=|:)\\s*([^\\s,\\)\\]\\}]+)");

    private final PiiKeywordDictionary dict;
    private final ForbiddenKeywordPolicy forbiddenPolicy;
    private final Map<PiiType, PiiMasker> maskerByType;

    public KeywordBasedLogMaskingEngine(
            PiiKeywordDictionary dict,
            ForbiddenKeywordPolicy forbiddenPolicy,
            List<PiiMasker> maskers
    ) {
        this.dict = Objects.requireNonNull(dict);
        this.forbiddenPolicy = Objects.requireNonNull(forbiddenPolicy);

        EnumMap<PiiType, PiiMasker> tmp = new EnumMap<>(PiiType.class);
        for (PiiMasker m : maskers) tmp.put(m.type(), m);
        this.maskerByType = Collections.unmodifiableMap(tmp);
    }

    public String mask(String rawLog) {
        if (rawLog == null) return null;

        Matcher matcher = KV.matcher(rawLog);
        StringBuilder out = new StringBuilder(rawLog);
        int offset = 0;

        while (matcher.find()) {
            String key = matcher.group(1);
            String originalValueToken = matcher.group(3);

            int start = matcher.start(3) + offset;
            int end = matcher.end(3) + offset;

            // 1) 금지 키 처리
            if (forbiddenPolicy.isForbidden(key)) {
                String replacement = switch (forbiddenPolicy.mode()) {
                    case REDACT -> "<REDACTED>";
                    case DROP_VALUE -> "";
                    case PASS -> originalValueToken;
                };
                out.replace(start, end, replacement);
                offset += replacement.length() - (end - start);
                continue;
            }

            // 2) PII 키워드 사전으로 타입 판별
            PiiType type = dict.resolve(key);
            if (type == null) continue;

            // 3) 타입별 마스커 적용
            PiiMasker masker = maskerByType.get(type);
            if (masker == null) continue;

            String cleaned = StringUtils.stripQuotes(originalValueToken);
            if (!masker.supportsValue(cleaned)) continue;

            String masked = masker.mask(cleaned);
            if (masked == null) continue;

            String replacement = StringUtils.reapplyOriginalQuotes(originalValueToken, masked);

            out.replace(start, end, replacement);
            offset += replacement.length() - (end - start);
        }

        return out.toString();
    }
}
