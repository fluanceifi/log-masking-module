package strategy.masking;

public class ResidentNumber implements MaskConvertor {

	private final String replacement = "******";

	// 싱글톤 패턴 적용
	private static final ResidentNumber INSTANCE = new ResidentNumber();

	public ResidentNumber() {
	}

	public static ResidentNumber getInstance() {
		return INSTANCE;
	}

	@Override
	public String convert(String target) {
		// null, " "
		if (target == null || target.isBlank()) {
			throw new IllegalArgumentException("유효하지 주민등록번호 형식입니다. 입력값: " + target);
		}

		// 정규식에 맞지 않으면 예외 발생 (하이픈 없음, 길이 안 맞음, 뒷자리 1~8 아님 등 모두 포함)
		// 정규식 설명: 앞6자리 숫자 + 하이픈 + 뒷자리 첫글자(1~8) + 뒷자리 나머지 6개
		if (!target.matches("\\d{6}-[1-8]\\d{6}")) {
			throw new IllegalArgumentException("유효하지 않은 주민등록번호 형식입니다. 입력값: " + target);
		}

		// "950101-1234567" 형태의 주민번호를 "950101-1******"로 마스킹
		if (target.matches("\\d{6}-\\d{7}")) {
			return target.substring(0, 8) + replacement;
		}

		return target;
	}
}
