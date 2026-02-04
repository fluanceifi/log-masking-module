package core;

/**
 * 특정 PII 타입에 대한 마스킹 전략 규약.
 * 엔진은 key로 PiiType을 결정한 뒤, 이 인터페이스 구현체의 mask(value)를 호출한다.
 */
public interface PiiMasker {
	/** 이 마스커가 담당하는 PII 타입. */
	PiiType type();

	/** value가 이 마스커로 처리 가능한지 검사. 기본: null/공백이 아니면 true. */
	default boolean supportsValue(String value) {
		return value != null && !value.isBlank();
	}

	/** value를 마스킹한 문자열 반환. 처리 불가 시 null 가능. */
	String mask(String value);
}
