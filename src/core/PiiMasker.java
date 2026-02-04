package core;

/*
 * 특정 PII 타입의 마스킹 전략(구현체)을 위한 최소 규약
 */
public interface PiiMasker {
	PiiType type();
	
	/*
	 * value 2차 검증
	 */
	default boolean supportsValue(String value) {
		return value != null && !value.isBlank();
	}
	
	/*
	 * value를 마스킹한 결과를 반환
	 */
	String mask(String value);
}
