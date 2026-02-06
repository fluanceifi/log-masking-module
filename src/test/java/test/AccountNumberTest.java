package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import strategy.masking.AccountNumber;

class AccountNumberTest {
	
	private static final AccountNumber accountNumber = AccountNumber.getInstance();
	
	@ParameterizedTest(name = "{2}")  
	@CsvSource({
	    "11111-11111, 111**-**111, 7자 이상의 긴 계좌번호를 입력하면 앞 3자리와 뒤 3자리만 노출하고 나머지는 마스킹될 것이다",
	    "11-111-11-111, 11-1**-**-111, 하이픈이 포함된 계좌번호를 입력하면 하이픈은 유지되고 숫자만 마스킹될 것이다",
	    "11 11 11 11, 11 1* *1 11, 공백이 포함된 계좌번호를 입력하면 공백은 유지되고 숫자만 마스킹될 것이다",
	    "1111111111, 111****111, 숫자만으로 구성된 계좌번호를 입력하면 앞 3자리와 뒤 3자리만 노출될 것이다",
	    "1234567, 123*567, 7자리 계좌번호를 입력하면 앞 3자리와 뒤 3자리만 노출될 것이다"
	})
	@DisplayName("정상 케이스 테스트")
	void normalCaseTest(String target, String expected, String description) {
	    // Given
	    
	    // When
	    String actual = accountNumber.convert(target);
	    
	    // Then
	    assertEquals(expected, actual);
	}
	
	@ParameterizedTest(name = "{2}")
	@MethodSource("edgeCaseProvider")
	@DisplayName("경계 케이스 테스트")
	void edgeCaseTest(String target, String expected, String description) {
	    // Given
	    
	    // When
	    String actual = accountNumber.convert(target);
	    
	    // Then
	    if (target == null) {
	        assertNull(actual);
	    } else {
	        assertEquals(expected, actual);
	    }
	}

	static Stream<Arguments> edgeCaseProvider() {
	    return Stream.of(
	        Arguments.of(null, null, "null을 입력하면 null이 반환될 것이다"),
	        Arguments.of("", "", "빈 문자열을 입력하면 빈 문자열이 반환될 것이다"),
	        Arguments.of("string", "string", "숫자가 없는 문자열을 입력하면 원문이 그대로 반환될 것이다"),
	        Arguments.of("1111111111111111", "111**********111", "16자 이상의 긴 문자열을 입력하면 정상적으로 마스킹이 될 것이다")
	    );
	}
	
	@ParameterizedTest(name="{2}자리 이하의 계좌번호를 입력하면 뒤 1자리만 노출될 것이다.")
	@CsvSource({
		"1, 1, 1",
		"11, *1, 2",
		"111, **1, 3",
		"1111, ***1, 4",
		"11111, ****1, 5",
		"111111, *****1, 6"
	})
	@DisplayName("6자리 이하의 계좌번호를 입력하면 뒤 1자리만 노출될 것이다.")
	void shortNumberTest(String target, String expected, int digit) {
		// Given
		
		// When
		String actual = accountNumber.convert(target);
		
		// Then
		assertEquals(expected, actual);
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
