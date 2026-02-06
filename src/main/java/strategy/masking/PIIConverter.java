package strategy.masking;

import java.util.List;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import core.KeywordBasedLogMaskingEngine;
import core.PiiMasker;
import core.PiiType;
import dictionary.ForbiddenKeywordPolicy;
import dictionary.PiiKeywordDictionary;

public class PIIConverter extends ClassicConverter {

	static final KeywordBasedLogMaskingEngine engine = createEngine();
	
	private static KeywordBasedLogMaskingEngine createEngine() {
        // Properties 파일 우선 로드 시도, 없으면 기본값 사용
        PiiKeywordDictionary dict;
        try {
            dict = PiiKeywordDictionary.fromProperties("masking-keywords.properties");
        } catch (Exception e) {
            // Properties 파일이 없거나 로드 실패 시 기본값 사용
            dict = PiiKeywordDictionary.defaultDictionary();
        }
        
        ForbiddenKeywordPolicy forbidden = new ForbiddenKeywordPolicy(
                ForbiddenKeywordPolicy.defaultForbiddenKeys(),
                ForbiddenKeywordPolicy.Mode.REDACT
        );
        
        // 싱글톤 인스턴스 사용
        List<PiiMasker> maskers = List.of(
                new MaskConvertorPiiMaskerAdapter(PiiType.RRN, ResidentNumber.getInstance()),
                new MaskConvertorPiiMaskerAdapter(PiiType.PHONE, PhoneNumber.getInstance()),
                new MaskConvertorPiiMaskerAdapter(PiiType.ACCOUNT, AccountNumber.getInstance()),
                new MaskConvertorPiiMaskerAdapter(PiiType.CARD, PrimaryAccountNumber.getInstance())
        );
        return new KeywordBasedLogMaskingEngine(dict, forbidden, maskers);
    }
	
	@Override
	public String convert(ILoggingEvent event) {
		String originalMessage = event.getFormattedMessage();
		String convertedMessage = engine.mask(originalMessage);
		
		return convertedMessage;
	}

}
