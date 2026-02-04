package strategy.masking;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* PhoneNumber masking utility.
*
* 목표:
* - 로그 메시지 내 한국 전화번호(휴대폰 + 유선)를 탐지
* - 평문 노출을 줄이기 위해 마스킹 형태로 변환
* - 외부에서 정책을 커스터마이징할 수 있도록 옵션(Policy) 제공
*
* 예)
*  - 010-1234-5678  -> 010-****-5678
*  - 01012345678    -> 010-****-5678
*  - 011-123-4567   -> 011-***-4567  (가운데 자리수에 맞게 처리)
*
*  - 02-1234-5678   -> 02-****-5678
*  - 02-123-4567    -> 02-***-4567
*  - 0311234567     -> 031-***-4567
*  - 051-1234-5678  -> 051-****-5678
*/
public final class PhoneRule implements MaskingRule {

  /**
   * 한국 전화번호 패턴(휴대폰 + 유선):
   *
   * [휴대폰]
   *  - 01[0,1,6,7,8,9] + (3~4) + (4)
   *
   * [유선]
   *  - 02 (서울):      02 + (3~4) + (4)
   *  - 지역번호(2~3자리): 0XX 또는 0XXX (예: 031, 051, 064 등) + (3~4) + (4)
   *
   * 구분자는 '-', ' ' 또는 없음
   *
   * 단어 경계(\b)는 하이픈 포함 시 애매할 수 있어
   * 앞/뒤가 숫자가 아닌 경우를 경계로 잡아 오탐을 줄임.
   */
  private static final Pattern KOREA_PHONE_PATTERN = Pattern.compile(
          "(?<!\\d)"                          // 앞이 숫자가 아니어야 함
          + "("
          + "01[016789]"                      // 휴대폰
          + "|02"                              // 서울
          + "|0\\d{2,3}"                       // 지역번호(031, 051, 064, 033, 042, 044 등)
          + ")"
          + "[-\\s]?"                          // 구분자 optional
          + "(\\d{3,4})"                       // 중간 3~4자리
          + "[-\\s]?"                          // 구분자 optional
          + "(\\d{4})"                         // 마지막 4자리
          + "(?!\\d)"                          // 뒤가 숫자가 아니어야 함
  );

  private final Policy policy;

  /** 기본 정책(디폴트)으로 PhoneRule 생성 */
  public PhoneRule() {
      this(Policy.defaultPolicy());
  }

  /** 커스텀 정책으로 PhoneRule 생성 */
  public PhoneRule(Policy policy) {
      this.policy = Objects.requireNonNull(policy, "policy must not be null");
  }

  /**
   * MaskingRule 인터페이스 구현 메서드
   * - DefaultMasker/Builder가 이 메서드를 호출해서 전체 메시지에 룰을 적용함
   */
  @Override
  public String apply(String input) {
      if (input == null) return null;

      // 정책이 "전화번호 출력 금지"라면, 탐지 시 통째로 [REDACTED] 처리
      if (policy.mode == Mode.REDACT) {
          return replaceAllMatches(input, policy.redactedToken);
      }

      // 기본: PARTIAL(부분 마스킹)
      Matcher m = KOREA_PHONE_PATTERN.matcher(input);
      StringBuffer sb = new StringBuffer();

      while (m.find()) {
          String areaOrMobile = m.group(1); // 010, 02, 031 ...
          String mid = m.group(2);          // 1234 or 123
          String last = m.group(3);         // 5678

          String midMasked = repeat(policy.maskChar, mid.length());
          String replacement = areaOrMobile + policy.separator + midMasked + policy.separator + last;

          // 정규식 치환 시 $ 같은 특수문자 안전 처리
          m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
      }
      m.appendTail(sb);

      return sb.toString();
  }

  /**
   * 내부적으로 "모든 전화번호 매치 부분"을 특정 토큰으로 치환.
   * (예: 개인정보는 저장은 하되 콘솔 출력은 안 하겠다 -> 출력용 정책에 REDACT 적용 가능)
   */
  private static String replaceAllMatches(String input, String token) {
      Matcher m = KOREA_PHONE_PATTERN.matcher(input);
      StringBuffer sb = new StringBuffer();

      while (m.find()) {
          m.appendReplacement(sb, Matcher.quoteReplacement(token));
      }
      m.appendTail(sb);
      return sb.toString();
  }

  private static String repeat(char c, int n) {
      if (n <= 0) return "";
      StringBuilder b = new StringBuilder(n);
      for (int i = 0; i < n; i++) b.append(c);
      return b.toString();
  }

  /** 마스킹 동작 모드 */
  public enum Mode {
      /** 부분 마스킹: 010-****-5678 / 02-***-4567 형태 */
      PARTIAL,
      /** 완전 치환: [REDACTED] 같은 토큰으로 통째로 바꿈 */
      REDACT
  }

  /**
   * 마스킹 정책(커스터마이징 포인트)
   * - 마스킹 문자
   * - 구분자(하이픈 등)
   * - 모드(PARTIAL/REDACT)
   * - REDACT 토큰
   */
  public static final class Policy {
      private final char maskChar;
      private final String separator;
      private final Mode mode;
      private final String redactedToken;

      private Policy(char maskChar, String separator, Mode mode, String redactedToken) {
          this.maskChar = maskChar;
          this.separator = separator;
          this.mode = mode;
          this.redactedToken = redactedToken;
      }

      public static Policy defaultPolicy() {
          return new Policy('*', "-", Mode.PARTIAL, "[REDACTED_PHONE]");
      }

      public static Builder builder() {
          return new Builder();
      }

      public static final class Builder {
          private char maskChar = '*';
          private String separator = "-";
          private Mode mode = Mode.PARTIAL;
          private String redactedToken = "[REDACTED_PHONE]";

          public Builder maskChar(char maskChar) {
              this.maskChar = maskChar;
              return this;
          }

          public Builder separator(String separator) {
              this.separator = separator;
              return this;
          }

          public Builder mode(Mode mode) {
              this.mode = mode;
              return this;
          }

          public Builder redactedToken(String redactedToken) {
              this.redactedToken = redactedToken;
              return this;
          }

          public Policy build() {
              return new Policy(maskChar, separator, mode, redactedToken);
          }
      }
  }
}
