package strategy.masking;

/**
 * 금융 계좌번호 마스킹을 담당하는 클래스.
 *
 * <p>
 * 계좌번호 문자열에서 숫자만을 기준으로 마스킹을 수행하며,
 * 원문에 포함된 하이픈(-), 공백 등 구분자는 그대로 유지한다.
 * </p>
 *
 * <p>
 * 기본 마스킹 정책:
 * <ul>
 *   <li>숫자 길이가 충분한 경우: 앞 3자리와 뒤 3자리만 노출</li>
 *   <li>숫자 길이가 짧은 경우: 과도한 노출을 방지하기 위해 최소 식별만 허용</li>
 * </ul>
 * </p>
 *
 * <p>
 * 이 클래스는 계좌번호 후보에 대한 2차 검증 및 마스킹만 수행하며,
 * 입력 문자열 탐지 및 로깅 처리는 외부 공통 로직에서 수행하는 것을 전제로 한다.
 * </p>
 */
public class AccountMasking {

    /**
     * 계좌번호를 마스킹하여 반환한다.
     *
     * @param raw 원문 계좌번호 문자열 (숫자, 하이픈, 공백 포함 가능)
     * @return 마스킹된 계좌번호 문자열 (형식 유지)
     */
    public String maskAccountNumber(String raw) {
        if (raw == null) return null;

        // 숫자만 추출하여 계좌번호 길이 판단
        String digits = raw.replaceAll("\\D", "");
        int n = digits.length();

        // 숫자가 없는 경우 원문 그대로 반환
        if (n == 0) return raw;

        // 기본 정책: 앞 3자리 + 뒤 3자리 노출
        int prefix = 3, suffix = 3;

        // 숫자가 짧은 경우 과도한 노출 방지
        if (n <= prefix + suffix) {
            prefix = 0;
            suffix = Math.min(1, n);
        }

        // 숫자 기준 마스킹 문자열 생성
        String maskedDigits =
                digits.substring(0, prefix)
              + "*".repeat(n - prefix - suffix)
              + digits.substring(n - suffix);

        // 원문 형식(구분자)을 유지하면서 숫자 위치만 마스킹
        StringBuilder out = new StringBuilder(raw.length());
        int di = 0;

        for (int i = 0; i < raw.length(); i++) {
            char ch = raw.charAt(i);
            if (Character.isDigit(ch)) {
                out.append(maskedDigits.charAt(di++));
            } else {
                out.append(ch);
            }
        }

        return out.toString();
    }
}
