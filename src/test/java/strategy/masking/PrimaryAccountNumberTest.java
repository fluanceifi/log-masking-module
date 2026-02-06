package strategy.masking;

import static org.junit.jupiter.api.Assertions.*;

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
	@DisplayName("하이픈이 포함되지 않은 카드번호는 마스킹되지 않는다.")
	void testPANConvert2() {
		// Given
		String input = "3333333333333333";
		
		String expect = "3333333333333333";
		
		// When
		String actual = primaryAccountNumber.convert(input);
		
		// Then
		assertEquals(expect, actual);
	}
	
	@Test
	@DisplayName("아메리칸 익스프레스 카드번호(15자리)는 마스킹되지 않는다.")
	void testPANConvert3() {
		// Given
		String input = "3700-123456-54321";
		
		String expect = "3700-123456-54321";
		
		// When
		String actual = primaryAccountNumber.convert(input);
		
		// Then
		assertEquals(expect, actual);
	}
	
	@Test
	@DisplayName("다이너스 클럽 카드번호(14자리)는 마스킹되지 않는다.")
	void testPANConvert4() {
		// Given
		String input = "3700-123456-5432";
		
		String expect = "3700-123456-5432";
		
		// When
		String actual = primaryAccountNumber.convert(input);
		
		// Then
		assertEquals(expect, actual);
	}
	
	@Test
	@DisplayName("입력한 문자열이 null이면 null이 반환된다.")
	void testPANConvert5() {
		// Given
		String input = null;
		
		// When
		String actual = primaryAccountNumber.convert(input);
		
		// Then
		assertNull(actual);
	}
	
	@Test
	@DisplayName("입력한 문자열이 빈 문자열이면 입력한 문자열이 반환된다.")
	void testPANConvert6() {
		// Given
		String input = "";
		
		// When
		String actual = primaryAccountNumber.convert(input);
		
		//Then
		assertEquals(input, actual);
	}
	
	@Test
	@DisplayName("싱글톤 패턴 테스트")
	void testPANConvert7() {
		// Given
		PrimaryAccountNumber pan1 = PrimaryAccountNumber.getInstance();
		PrimaryAccountNumber pan2 = PrimaryAccountNumber.getInstance();
		// When
		
		// Then
		assertSame(pan1, pan2);
	}

}
