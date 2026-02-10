package strategy.masking;

public class PrimaryAccountNumber implements MaskConvertor {
    
    private static final PrimaryAccountNumber INSTANCE = new PrimaryAccountNumber();

    // Singleton 유지를 위해 생성자는 private으로 막는 것을 권장합니다.
    private PrimaryAccountNumber() {}

    public static PrimaryAccountNumber getInstance() {
        return INSTANCE;
    }

    // 검증용 정규식 (16자리 카드번호 형식: 0000-0000-0000-0000)
    private static final String VALID_PATTERN = "^\\d{4}-\\d{4}-\\d{4}-\\d{4}$";
    
    // 마스킹용 정규식 (그룹핑을 위해 별도 분리)
    // 앞 6자리(BIN), 뒤 4자리 유지하고 중간을 마스킹
    private static final String MASKING_PATTERN = "(\\d{4}-\\d{2})\\d{2}-\\d{4}-(\\d{4})";

    /** * PAN 문자열(16자리, 하이픈 포함)을 앞 6자리·뒤 4자리만 노출하도록 마스킹. 
     * 예: 1234-5678-1234-5678 -> 1234-56**-****-5678
     */
    @Override
    public String convert(String target) {
        // 1. null 또는 빈 문자열 체크
        if (target == null || target.isBlank()) {
            throw new IllegalArgumentException("입력값이 null이거나 비어있습니다.");
        }

        // 2. 형식 검증 (0000-0000-0000-0000 형식이 아니면 예외 발생)
        if (!target.matches(VALID_PATTERN)) {
             throw new IllegalArgumentException("유효하지 않은 카드번호 형식입니다. 입력값: " + target);
        }

        // 3. 검증 통과 시 마스킹 수행
        return target.replaceAll(MASKING_PATTERN, "$1**-****-$2");
    }
}