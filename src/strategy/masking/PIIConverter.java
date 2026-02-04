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
	
	@Override
	public String convert(ILoggingEvent event) {
		String originalMessage = event.getFormattedMessage();
		String convertedMessage = engine.mask(originalMessage);
		
		return convertedMessage;
	}

}
