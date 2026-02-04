package strategy.masking;


//로그가 생성될 때마다 매번 객체가 생성되지않고, 하나의 객체로 동작하도록 싱글톤 패턴 적용.
//전략패턴으로 하나의 
public class ResidentNumberMaskingStrategy implements MaskingStrategy {
	
	//패턴 상수화
	private static final String RRN_PATTERN = "(\\d{6}-[1-8])\\d{6}";
	
	
	@Override
	public String mask(String target) {
		//필요조건
		if(target == null || target.isBlank()) return target;
		
		return target.replace(RRN_PATTERN, "$1******");
	}
	
}
