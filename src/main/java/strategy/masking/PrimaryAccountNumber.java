package strategy.masking;

public class PrimaryAccountNumber implements MaskConvertor {
	
	private static final PrimaryAccountNumber INSTANCE = new PrimaryAccountNumber();

	public PrimaryAccountNumber() {}

	public static PrimaryAccountNumber getInstance() {
		return INSTANCE;
	}

	private static final String PAN_PATTERN = "(\\d{4}-\\d{2})\\d{2}-\\d{4}-(\\d{4})";

	/** PAN 문자열(16자리, 하이픈 포함)을 앞 6자리·뒤 4자리만 노출하도록 마스킹. */
	@Override
	public String convert(String target) {
		if (target == null || target.isBlank()) {
			return target;
		}
		return target.replaceAll(PAN_PATTERN, "$1**-****-$2");
	}
}
