package strategy.masking;

public class ResidentNumber implements MaskConvertor {

    private final String replacement = "******";

    // 싱글톤 패턴 적용
    private static final ResidentNumber INSTANCE = new ResidentNumber();

    public ResidentNumber() {}

    public static ResidentNumber getInstance() {
        return INSTANCE;
    }

    @Override
    public String convert(String target) {
        if (target == null || target.isBlank()) {
            return target;
        }

        // "950101-1234567" 형태의 주민번호를 "950101-1******"로 마스킹
        if (target.matches("\\d{6}-\\d{7}")) {
            return target.substring(0, 8) + replacement;
        }

        return target;
    }
}