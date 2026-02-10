package strategy.masking;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PrimaryAccountNumberTest {

	private PrimaryAccountNumber primaryAccountNumber;

	@BeforeEach
	void setup() {
		this.primaryAccountNumber = PrimaryAccountNumber.getInstance();
	}

	@Test
	@DisplayName("하이픈이 포함된 16자리 카드 번호가 마스킹된다.")
	void testPANConvert1() {
		// Given
		String input = "3333-3333-3333-3333";

		String expect = "3333-33**-****-3333";

		// When
		String actual = primaryAccountNumber.convert(input);

		// Then
		assertEquals(expect, actual);
	}

	@Test
	@DisplayName("[실패] 형식이 맞지 않는 카드번호는 예외가 발생한다.")
	void testFailInvalidFormats() {
		// assertAll을 사용하면 여러 케이스를 한 번에 묶어서 깔끔하게 보여줄 수 있습니다.
		assertAll(
				// Case 1: 하이픈이 없는 경우 (16자리 숫자만)
				() -> assertThrows(IllegalArgumentException.class, () -> {
					primaryAccountNumber.convert("3333333333333333");
				}),

				// Case 2: 아메리칸 익스프레스 (15자리) - 현재 로직상 16자리 외에는 예외 처리
				() -> assertThrows(IllegalArgumentException.class, () -> {
					primaryAccountNumber.convert("3700-123456-54321");
				}),

				// Case 3: 다이너스 클럽 (14자리) - 예외 처리
				() -> assertThrows(IllegalArgumentException.class, () -> {
					primaryAccountNumber.convert("3700-123456-5432");
				}),

				// Case 4: 숫자가 아닌 문자가 섞인 경우
				() -> assertThrows(IllegalArgumentException.class, () -> {
					primaryAccountNumber.convert("1234-abcd-5678-0000");
				}));
	}

	@Test
	@DisplayName("[실패] null이나 빈 문자열 입력 시 예외가 발생한다.")
	void testFailNullOrEmpty() {
		assertAll(
				// Case: Null 입력
				() -> assertThrows(IllegalArgumentException.class, () -> {
					primaryAccountNumber.convert(null);
				}),

				// Case: 빈 문자열 입력
				() -> assertThrows(IllegalArgumentException.class, () -> {
					primaryAccountNumber.convert("");
				}));
	}


	@Test
	@DisplayName("[성공] 싱글톤 패턴 테스트")
	void testSingleton() {
		// Given
		PrimaryAccountNumber pan1 = PrimaryAccountNumber.getInstance();
		PrimaryAccountNumber pan2 = PrimaryAccountNumber.getInstance();

		// Then
		assertSame(pan1, pan2);
	}

}
