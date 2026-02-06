package util;

/** 로그 value 토큰 처리용 유틸(따옴표 제거/복원, 숫자만 추출). */
public final class StringUtils {
    private StringUtils() {}

    /** 쌍따옴표 또는 작은따옴표로 감싼 경우만 제거 후 반환. */
    public static String stripQuotes(String v) {
        if (v == null) return null;
        if (v.length() >= 2) {
            char a = v.charAt(0), b = v.charAt(v.length() - 1);
            if ((a == '"' && b == '"') || (a == '\'' && b == '\'')) {
                return v.substring(1, v.length() - 1);
            }
        }
        return v;
    }

    /** 원본 토큰이 따옴표로 감싸져 있었으면 마스킹 결과에도 동일 따옴표 적용. */
    public static String reapplyOriginalQuotes(String originalToken, String masked) {
        if (originalToken == null) return masked;
        if (originalToken.startsWith("\"") && originalToken.endsWith("\"")) return "\"" + masked + "\"";
        if (originalToken.startsWith("'") && originalToken.endsWith("'")) return "'" + masked + "'";
        return masked;
    }

    /** 숫자가 아닌 문자 제거. */
    public static String digitsOnly(String s) {
        if (s == null) return "";
        return s.replaceAll("\\D", "");
    }
}
