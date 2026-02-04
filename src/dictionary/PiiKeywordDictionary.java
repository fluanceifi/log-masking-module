package dictionary;

import core.PiiType;

import java.io.InputStream;
import java.util.*;

/**
 * 로그에서 추출한 key(정규화: trim + 소문자)를 PiiType으로 매핑하는 사전.
 * fromProperties로 properties 로드 시, 왼쪽 키는 PiiType 이름(rrn, phone 등), 오른쪽은 콤마 구분 alias 목록.
 */
public final class PiiKeywordDictionary {

    private final Map<String, PiiType> keyToType;

    public PiiKeywordDictionary(Map<String, PiiType> mappings) {
        Map<String, PiiType> tmp = new HashMap<>();
        mappings.forEach((k, v) -> tmp.put(normalize(k), v));
        this.keyToType = Collections.unmodifiableMap(tmp);
    }

    public PiiType resolve(String key) {
        return keyToType.get(normalize(key));
    }

    public boolean contains(String key) {
        return resolve(key) != null;
    }

    private static String normalize(String s) {
        return s == null ? "" : s.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * classpath의 properties 파일에서 키워드 사전을 로드.
     * 포맷 예:
     * rrn=rrn,residentNo,jumin
     * phone=phone,mobile,tel
     * account=account,acct,bankAccount
     */
    public static PiiKeywordDictionary fromProperties(String classpathResource) {
        Properties props = new Properties();

        try (InputStream in = PiiKeywordDictionary.class.getClassLoader()
                .getResourceAsStream(stripLeadingSlash(classpathResource))) {

            if (in == null) {
                throw new IllegalArgumentException("Resource not found: " + classpathResource);
            }
            props.load(in);

        } catch (Exception e) {
            throw new IllegalStateException("Failed to load keyword properties: " + classpathResource, e);
        }

        Map<String, PiiType> map = new HashMap<>();
        for (String typeKey : props.stringPropertyNames()) {
            PiiType type = PiiType.valueOf(typeKey.trim().toUpperCase(Locale.ROOT));
            String aliasesCsv = props.getProperty(typeKey, "");
            for (String alias : aliasesCsv.split(",")) {
                String a = alias.trim();
                if (!a.isEmpty()) map.put(a, type);
            }
        }
        return new PiiKeywordDictionary(map);
    }

    private static String stripLeadingSlash(String s) {
        if (s == null) return "";
        return s.startsWith("/") ? s.substring(1) : s;
    }

    /** 코드로 기본 사전을 만들고 싶을 때의 기본값 */
    public static PiiKeywordDictionary defaultDictionary() {
        Map<String, PiiType> map = new HashMap<>();

        // RRN
        for (String k : List.of("rrn", "residentno", "residentnumber", "jumin", "ssn_kr")) map.put(k, PiiType.RRN);

        // PHONE
        for (String k : List.of("phone", "mobile", "tel", "contact", "msisdn")) map.put(k, PiiType.PHONE);

        // ACCOUNT
        for (String k : List.of("account", "acct", "accountno", "bankaccount", "withdrawaccount", "depositaccount"))
            map.put(k, PiiType.ACCOUNT);

        // CARD
        for (String k : List.of("card", "cardno", "cardnumber", "pan")) map.put(k, PiiType.CARD);

        return new PiiKeywordDictionary(map);
    }
}
