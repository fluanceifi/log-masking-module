package test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import strategy.masking.PhoneNumber;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PhoneNumberTest {

    private final PhoneNumber masker = new PhoneNumber();

    @Test
    @DisplayName("Masks middle 4 digits for mobile number")
    void masksMobileNumber() {
        String input = "phone=010-1234-5678";
        String actual = masker.convert(input);

        assertEquals("phone=010-****-5678", actual);
    }

    @Test
    @DisplayName("Normalizes and masks mobile number without separators")
    void masksMobileNumberWithoutSeparator() {
        String input = "01012345678";
        String actual = masker.convert(input);

        assertEquals("010-****-5678", actual);
    }

    @Test
    @DisplayName("Masks Seoul area number by middle length")
    void masksSeoulNumberByMidLength() {
        String input = "a=02-123-4567, b=02-1234-5678";
        String actual = masker.convert(input);

        assertEquals("a=02-***-4567, b=02-****-5678", actual);
    }

    @Test
    @DisplayName("Masks local area number and normalizes format")
    void masksLocalAreaNumber() {
        String input = "0311234567";
        String actual = masker.convert(input);

        assertEquals("031-***-4567", actual);
    }

    @Test
    @DisplayName("Masks space-separated phone number")
    void masksSpaceSeparatedNumber() {
        String input = "contact: 010 9999 8888";
        String actual = masker.convert(input);

        assertEquals("contact: 010-****-8888", actual);
    }

    @Test
    @DisplayName("Masks all phone numbers in one sentence")
    void masksMultiplePhoneNumbers() {
        String input = "mobile=010-1234-5678, office=02-123-4567";
        String actual = masker.convert(input);

        assertEquals("mobile=010-****-5678, office=02-***-4567", actual);
    }

    @Test
    @DisplayName("Keeps original text for non-phone pattern")
    void keepsNonPhonePatternUntouched() {
        String input = "order=123-456-7890";
        String actual = masker.convert(input);

        assertEquals("order=123-456-7890", actual);
    }

    @Test
    @DisplayName("Returns null for null input")
    void returnsNullForNullInput() {
        assertNull(masker.convert(null));
    }

    @Test
    @DisplayName("Replaces detected phone numbers with token in REDACT mode")
    void redactsAllDetectedPhoneNumbers() {
        PhoneNumber.Policy policy = PhoneNumber.Policy.builder()
                .mode(PhoneNumber.Mode.REDACT)
                .redactedToken("[PHONE_BLOCKED]")
                .build();
        PhoneNumber redactingMasker = new PhoneNumber(policy);

        String input = "m=010-1234-5678, o=02-1234-5678";
        String actual = redactingMasker.convert(input);

        assertEquals("m=[PHONE_BLOCKED], o=[PHONE_BLOCKED]", actual);
    }

    @Test
    @DisplayName("Applies custom mask char and separator")
    void appliesCustomMaskCharAndSeparator() {
        PhoneNumber.Policy policy = PhoneNumber.Policy.builder()
                .maskChar('#')
                .separator(" ")
                .build();
        PhoneNumber customMasker = new PhoneNumber(policy);

        String input = "01012345678";
        String actual = customMasker.convert(input);

        assertEquals("010 #### 5678", actual);
    }

    @Test
    @DisplayName("Throws exception when policy is null")
    void throwsWhenPolicyIsNull() {
        assertThrows(NullPointerException.class, () -> new PhoneNumber(null));
    }
}
