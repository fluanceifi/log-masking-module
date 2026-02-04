package dictionary;

import java.util.Locale;
import java.util.Set;

public final class ForbiddenKeywordPolicy {

    public enum Mode {
        /** value를 <REDACTED>로 치환 */
        REDACT,
        /** 해당 key/value만 제거(빈 값으로) */
        DROP_VALUE,
        /** 아무 처리 안 함 */
        PASS
    }

    private final Set<String> forbiddenKeys;
    private final Mode mode;

    public ForbiddenKeywordPolicy(Set<String> forbiddenKeys, Mode mode) {
        this.forbiddenKeys = forbiddenKeys.stream()
                .map(ForbiddenKeywordPolicy::normalize)
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
        this.mode = mode == null ? Mode.REDACT : mode; // 모드 기본값: REDACT
    }

    /** 금지 키인지 확인 */ 
    public boolean isForbidden(String key) {
        return forbiddenKeys.contains(normalize(key));
    }

    public Mode mode() {
        return mode;
    }

    /** keys 정제 (소문자로 변환) */ 
    private static String normalize(String s) {
        return s == null ? "" : s.trim().toLowerCase(Locale.ROOT);
    }

    /** 권장 기본 금지 키 셋 */
    public static Set<String> defaultForbiddenKeys() {
        return Set.of(
                "password", "passwd", "pwd",
                "pin", "otp",
                "cvv", "cvc",
                "authcode", "auth_code", "verificationcode", "verification_code"
        );
    }
}
