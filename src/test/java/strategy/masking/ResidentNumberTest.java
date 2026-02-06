package strategy.masking;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ResidentNumberTest {

	private ResidentNumber rm1;
	private ResidentNumber rm2;

	@BeforeEach
	void setUp() {
		rm1 = ResidentNumber.getInstance();
		rm2 = ResidentNumber.getInstance();
	}

	@Test
	@DisplayName("[성공케이스] 싱글톤 패턴 테스트")
	void 싱글톤_패턴_테스트() {
		assertSame(rm1, rm2);
	}

	@Test
	@DisplayName("[성공케이스] 유효한 주민등록번호가 입력되었을 때, 뒷부분이 정상적으로 마스킹된다.")
	void 정상_플로우_테스트() {

		assertAll(() -> assertEquals(rm1.convert("990203-1234567"), "990203-1******"),
				() -> assertEquals(rm1.convert("990203-2234567"), "990203-2******"),
				() -> assertEquals(rm1.convert("990203-3234567"), "990203-3******"),
				() -> assertEquals(rm1.convert("990203-4234567"), "990203-4******"),
				() -> assertEquals(rm1.convert("990203-5234567"), "990203-5******"),
				() -> assertEquals(rm1.convert("990203-6234567"), "990203-6******"),
				() -> assertEquals(rm1.convert("990203-7234567"), "990203-7******"),
				() -> assertEquals(rm1.convert("990203-8234567"), "990203-8******"));
	}


	// 문제점: 실패케이스에서 그대로 반환하면?
	// -> 문제점을 파악못함.. 따라서 예외를 던져주는 방식으로 리펙토링 해야함.
	@Test
	@DisplayName("[실패케이스] 경계 값 테스트")
	void 경계_값_테스트() {
		assertEquals(rm1.convert(null), null);
		assertEquals(rm1.convert(""), "");
		assertEquals(rm1.convert(" "), " ");
	}

	// 이것도 마찬가지로 예외를 던져야함. 근데 예외를 하나로 통일할까?
	@Test
	@DisplayName("[실패케이스] 실패 & 예외 케이스 ")
	void 실패_혹은_예외_4가지_테스트() {

		assertAll(
				// 케이스 1: 하이픈(-)이 없는 경우
				() -> assertEquals(rm1.convert("9501011234567"), "9501011234567"),

				// 케이스 2: 자릿수가 부족한 경우
				() -> assertEquals(rm1.convert("950101-123456"), "950101-123456"),

				// 케이스 3: 자릿수가 초과된 경우
				() -> assertEquals(rm1.convert("950101-12345678"), "950101-12345678"),

				// 케이스 4: 숫자가 아닌 문자가 포함된 경우
				() -> assertEquals(rm1.convert("950101-abcdefg"), "950101-abcdefg"));

	}

	// 이것도 예외로 던져야될듯?
	@Test
	@DisplayName("[실패케이스] 무효한 주민등록번호가 입력되었을 때, 뒷부분이 정상적으로 마스킹된다.")
	void 실패_플로우_테스트() {

		assertAll(() -> assertEquals(rm1.convert("990203-8234567"), "990203-8******"),
				() -> assertEquals(rm1.convert("990203-9234567"), "990203-9******"),
				() -> assertEquals(rm1.convert("990203-0234567"), "990203-0******"));
	}

}
