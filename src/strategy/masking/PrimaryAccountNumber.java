package strategy.masking;

public class PrimaryAccountNumber implements MaskConvertor {
	
	private static final PrimaryAccountNumber INSATANCE = new PrimaryAccountNumber();
	
	public PrimaryAccountNumber() {}
	
	public PrimaryAccountNumber getInstance() {
		return INSATANCE;
	}
	
	private static final String PAN_PATTERN = "(\\d{4}-\\d{2})\\d{2}-\\d{4}-(\\d{4})";
	
	/**
	 * 
	 * @param target - PAN을 문자열로 입력받는다. '-'이 포함된 16자리의 번호이다.
	 * @return - 마스킹된 결과를 반환한다.
	 */
	@Override
	public String convert(String target) {
		if(target == null || target.isBlank()) {
			return target;
		}
		
		return target.replace(PAN_PATTERN, "$1**-****-$2");
	}
}
