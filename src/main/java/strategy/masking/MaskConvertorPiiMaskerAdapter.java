package strategy.masking;

import core.PiiMasker;
import core.PiiType;

/**
 * MaskConvertor(엄격한 검증)를 PiiMasker(안전한 로깅) 규약에 맞게 연결하는 어댑터.
 * - 역할: 변환 중 발생하는 예외를 포착하여 로깅 시스템이 중단되지 않도록 방어함.
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
        try {
            // 1. 엄격한 변환 시도 (여기서 실패하면 IllegalArgumentException 발생)
            return delegate.convert(value);
        } catch (IllegalArgumentException e) {
            // 2. [실패 시 처리 정책]
            // 예외가 발생했다는 것은 포맷이 맞지 않는 데이터라는 뜻입니다.
            
            // 옵션 A: 원본을 그대로 반환 (디버깅 용이, 민감정보 노출 위험 있음)
            return value; 
            
            // 옵션 B: 에러 태그 반환 (가장 안전, 추천 ⭐)
            // return String.format("[%s_FORMAT_ERROR]", type.name());
        } catch (Exception e) {
            // 3. 예상치 못한 그 외 에러 방어
            return "[MASKING_ERROR]";
        }
    }
}