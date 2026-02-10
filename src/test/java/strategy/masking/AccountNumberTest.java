package strategy.masking;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AccountNumberTest {
	
	private static final AccountNumber accountNumber = AccountNumber.getInstance();
	
	@ParameterizedTest(name = "{2}")  
	@CsvSource({
        "11111-11111, 111**-**111, 7자 이상의 긴 계좌번호를 입력하면 앞 3자리와 뒤 3자리만 노출하고 나머지는 마스킹될 것이다",
        "11-111-11-111, 11-1**-**-111, 하이픈이 포함된 계좌번호를 입력하면 하이픈은 유지되고 숫자만 마스킹될 것이다",
        "11 11 11 11, 11 1* *1 11, 공백이 포함된 계좌번호를 입력하면 공백은 유지되고 숫자만 마스킹될 것이다",
        "1111111111, 111****111, 숫자만으로 구성된 계좌번호를 입력하면 앞 3자리와 뒤 3자리만 노출될 것이다",
        "1234567, 123*567, 7자리 계좌번호를 입력하면 앞 3자리와 뒤 3자리만 노출될 것이다",
        "1111111111111111, 111**********111, 16자리 숫자 계좌번호는 앞 3자리/뒤 3자리만 노출될 것이다"
    })
	@DisplayName("정상 케이스 테스트")
	void normalCaseTest(String target, String expected, String description) {
	    // Given
	    
	    // When
	    String actual = accountNumber.convert(target);
	    
	    // Then
	    assertEquals(expected, actual);
	}
	
	@ParameterizedTest(name = "{2}자리 이하의 계좌번호를 입력하면 뒤 1자리만 노출될 것이다.")
    @CsvSource({
        "1, 1, 1",
        "11, *1, 2",
        "111, **1, 3",
        "1111, ***1, 4",
        "11111, ****1, 5",
        "111111, *****1, 6"
    })
    @DisplayName("짧은 계좌번호(6자리 이하) 마스킹 정책 테스트")
	void shortNumberTest(String target, String expected, int digit) {
		// Given
		
		// When
		String actual = accountNumber.convert(target);
		
		// Then
		assertEquals(expected, actual);
	}
	
	@Test
    @DisplayName("[실패] null 또는 숫자가 없는 입력은 예외가 발생한다.")
    void testFailInvalidInputs() {
        assertAll(
            // null 입력: 즉시 예외
            () -> assertThrows(IllegalArgumentException.class, () -> accountNumber.convert(null)),

            // 빈 문자열: digits length == 0 이므로 예외
            () -> assertThrows(IllegalArgumentException.class, () -> accountNumber.convert("")),

            // 공백만: digits length == 0 이므로 예외
            () -> assertThrows(IllegalArgumentException.class, () -> accountNumber.convert("   ")),

            // 숫자가 아예 없는 문자열: digits length == 0 이므로 예외
            () -> assertThrows(IllegalArgumentException.class, () -> accountNumber.convert("string")),

            // 구분자만 있는 경우도 숫자 없음 -> 예외
            () -> assertThrows(IllegalArgumentException.class, () -> accountNumber.convert("----")),
            () -> assertThrows(IllegalArgumentException.class, () -> accountNumber.convert("  -  -  "))
        );
    }
	
	@Test
	@DisplayName("getInstance()를 여러 번 호출하면 항상 같은 인스턴스가 반환될 것이다.")
	void singletonTest1() {
		// Given
		AccountNumber instance1 = AccountNumber.getInstance();
		AccountNumber instance2 = AccountNumber.getInstance();
		
		// When
		
		// Then
		assertSame(instance1, instance2);
	}

}
