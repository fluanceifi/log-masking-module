package strategy.masking;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import core.PiiType;

@ExtendWith(MockitoExtension.class)
class MaskConvertorPiiMaskerAdapterTest {

	private MaskConvertorPiiMaskerAdapter maskConvertorPiiMaskerAdapter;
	private AccountNumber accountNumberMock;
	private PhoneNumber phoneNumberMock;
	private PrimaryAccountNumber primaryAccountNumberMock;
	private ResidentNumber residentNumberMock;
	
	@Test
	@DisplayName("계좌번호 타입을 반환한다.")
	void testMaskConvertorPiiMaskerAdapter1() {
		// Given
		accountNumberMock = mock(AccountNumber.class);
		PiiType type = PiiType.ACCOUNT;
		
		maskConvertorPiiMaskerAdapter = new MaskConvertorPiiMaskerAdapter(type, accountNumberMock);	
		
		PiiType actualType = maskConvertorPiiMaskerAdapter.type();
		
		assertEquals(type, actualType);
	}
	
	@Test
	@DisplayName("계좌번호를 마스킹한다.")
	void testMaskConvertorPiiMaskerAdapter2() {
		// Given
		accountNumberMock = mock(AccountNumber.class);
		PiiType type = PiiType.ACCOUNT;
		
		maskConvertorPiiMaskerAdapter = new MaskConvertorPiiMaskerAdapter(type, accountNumberMock);
		
		String input = "466402-01-520624";
		
		String expectedNumber = "466***-**-***624";
		
		// When
		when(this.accountNumberMock.convert(input))
			.thenReturn("466***-**-***624");
		
		String actualNumber = maskConvertorPiiMaskerAdapter.mask(input);
		
		// Then
		assertEquals(expectedNumber, actualNumber);

	}
	
	@Test
	@DisplayName("전화번호 타입을 반환한다.")
	void testMaskConvertorPiiMaskerAdapter3() {
		// Given
		phoneNumberMock = mock(PhoneNumber.class);
		PiiType type = PiiType.PHONE;
		
		maskConvertorPiiMaskerAdapter = new MaskConvertorPiiMaskerAdapter(type, phoneNumberMock);
		
		// When
		PiiType actualType = maskConvertorPiiMaskerAdapter.type();
		
		// Then
		assertEquals(type, actualType);
	}
	
	@Test
	@DisplayName("전화번호를 마스킹한다.")
	void testMaskConvertorPiiMaskerAdapter4() {
		// Given
		phoneNumberMock = mock(PhoneNumber.class);
		PiiType type = PiiType.PHONE;
		
		maskConvertorPiiMaskerAdapter = new MaskConvertorPiiMaskerAdapter(type, phoneNumberMock);
		
		String input = "010-0000-0000";
		
		String expectedNumber = "010-****-0000";
		
		// When
		when(this.phoneNumberMock.convert(input))
			.thenReturn("010-****-0000");
		
		String actualNumber = maskConvertorPiiMaskerAdapter.mask(input);
		
		// Then
		assertEquals(expectedNumber, actualNumber);

	}
	
	@Test
	@DisplayName("카드번호 타입을 반환한다.")
	void testMaskConvertorPiiMaskerAdapter5() {
		// Given
		primaryAccountNumberMock = mock(PrimaryAccountNumber.class);
		PiiType type = PiiType.CARD;
		
		maskConvertorPiiMaskerAdapter = new MaskConvertorPiiMaskerAdapter(type, primaryAccountNumberMock);
		
		// When
		PiiType actualType = maskConvertorPiiMaskerAdapter.type();
		
		// Then
		assertEquals(type, actualType);
	}
	
	@Test
	@DisplayName("카드번호를 마스킹한다.")
	void testMaskConvertorPiiMaskerAdapter6() {
		// Given
		primaryAccountNumberMock = mock(PrimaryAccountNumber.class);
		PiiType type = PiiType.CARD;
		
		maskConvertorPiiMaskerAdapter = new MaskConvertorPiiMaskerAdapter(type, primaryAccountNumberMock);
		
		String input = "1234-5678-9012-3456";
		
		String expectedNumber = "1234-56**-****-3456";
		
		// When
		when(this.primaryAccountNumberMock.convert(input))
			.thenReturn("1234-56**-****-3456");
		
		String actualNumber = maskConvertorPiiMaskerAdapter.mask(input);
		
		// Then
		assertEquals(expectedNumber, actualNumber);
	}
	
	@Test
	@DisplayName("주민번호 타입을 반환한다.")
	void testMaskConvertorPiiMaskerAdapter7() {
		// Given
		residentNumberMock = mock(ResidentNumber.class);
		PiiType type = PiiType.RRN;
		
		maskConvertorPiiMaskerAdapter = new MaskConvertorPiiMaskerAdapter(type, residentNumberMock);
		
		// When
		PiiType actualType = maskConvertorPiiMaskerAdapter.type();
		
		// Then
		assertEquals(type, actualType);
	}
	
	@Test
	@DisplayName("주민번호를 마스킹한다.")
	void testMaskConvertorPiiMaskerAdapter8() {
		// Given
		residentNumberMock = mock(ResidentNumber.class);
		PiiType type = PiiType.RRN;
		
		maskConvertorPiiMaskerAdapter = new MaskConvertorPiiMaskerAdapter(type, residentNumberMock);
		
		String input = "000000-3000000";
		
		String expectedNumber = "000000-3******";
		
		// When
		when(this.residentNumberMock.convert(input))
			.thenReturn("000000-3******");
		
		String actualNumber = maskConvertorPiiMaskerAdapter.mask(input);
		
		// Then
		assertEquals(expectedNumber, actualNumber);
	}

}
