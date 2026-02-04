package test;

import core.KeywordBasedLogMaskingEngine;
import core.PiiMasker;
import core.PiiType;
import dictionary.ForbiddenKeywordPolicy;
import dictionary.PiiKeywordDictionary;
import strategy.masking.*;

import java.util.List;

/**
 * 로그 파싱 및 마스킹이 기대대로 동작하는지 검증하는 테스트.
 * main() 실행 시 모든 케이스를 돌리고, 실패 시 AssertionError로 종료한다.
 */
public class LogMaskingTest {

    public static void main(String[] args) {
        KeywordBasedLogMaskingEngine engine = createEngine();
        int passed = 0;

        // ---- 파싱·PII 마스킹 ----
        passed += expect(engine,
                "residentNo=950101-1234567",
                "residentNo=950101-1******",
                "RRN 마스킹");

        passed += expect(engine,
                "phone=010-1234-5678",
                "phone=010-****-5678",
                "전화번호 마스킹");

        passed += expect(engine,
                "account=123-456-789012",
                "account=123-***-***012",
                "계좌번호 마스킹");

        passed += expect(engine,
                "card=1234-5678-1234-5678",
                "card=1234-56**-****-5678",
                "카드번호 마스킹");

        // ---- key: value 형식 ----
        passed += expect(engine,
                "mobile: 010-9999-8888",
                "mobile: 010-****-8888",
                "key: value 형식 파싱");

        // ---- 대소문자 무관 key 매칭 ----
        passed += expect(engine,
                "RESIDENTNO=900101-2111111",
                "RESIDENTNO=900101-2******",
                "대문자 key 매칭");

        // ---- 금지 키 ----
        passed += expect(engine,
                "password=secret123",
                "password=<REDACTED>",
                "금지 키 REDACT");

        passed += expect(engine,
                "user=john pwd=abc123 role=admin",
                "user=john pwd=<REDACTED> role=admin",
                "한 줄에 여러 키(금지 키 포함)");

        // ---- PII 아님: 그대로 유지 ----
        passed += expect(engine,
                "level=INFO msg=started",
                "level=INFO msg=started",
                "PII가 아닌 key는 미변경");

        // ---- null ----
        if (engine.mask(null) == null) {
            passed++;
        } else {
            throw new AssertionError("null 입력 시 null 반환");
        }

        System.out.println("OK: " + passed + "개 케이스 통과");
    }

    private static KeywordBasedLogMaskingEngine createEngine() {
        PiiKeywordDictionary dict = PiiKeywordDictionary.defaultDictionary();
        ForbiddenKeywordPolicy forbidden = new ForbiddenKeywordPolicy(
                ForbiddenKeywordPolicy.defaultForbiddenKeys(),
                ForbiddenKeywordPolicy.Mode.REDACT
        );
        List<PiiMasker> maskers = List.of(
                new MaskConvertorPiiMaskerAdapter(PiiType.RRN, new ResidentNumber()),
                new MaskConvertorPiiMaskerAdapter(PiiType.PHONE, new PhoneNumber()),
                new MaskConvertorPiiMaskerAdapter(PiiType.ACCOUNT, new AccountNumber()),
                new MaskConvertorPiiMaskerAdapter(PiiType.CARD, new PrimaryAccountNumber())
        );
        return new KeywordBasedLogMaskingEngine(dict, forbidden, maskers);
    }

    private static int expect(KeywordBasedLogMaskingEngine engine, String input, String expected, String label) {
        String actual = engine.mask(input);
        if (!expected.equals(actual)) {
            throw new AssertionError(label + " 실패: expected=\"" + expected + "\" actual=\"" + actual + "\"");
        }
        return 1;
    }
}
