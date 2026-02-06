package strategy.masking;

import core.PiiMasker;
import core.PiiType;

/**
 * MaskConvertor 구현체를 PiiMasker 규약에 맞게 감싸서
 * KeywordBasedLogMaskingEngine에서 사용할 수 있게 한다.
 */
public final class MaskConvertorPiiMaskerAdapter implements PiiMasker {

    private final PiiType type;
    private final MaskConvertor delegate;

    public MaskConvertorPiiMaskerAdapter(PiiType type, MaskConvertor delegate) {
        this.type = type;
        this.delegate = delegate;
    }

    @Override
    public PiiType type() {
        return type;
    }

    @Override
    public String mask(String value) {
        return delegate.convert(value);
    }
}
