![Architecture of the project](https://github.com/user-attachments/assets/82a464e6-fd18-4447-abb1-d00f583fc01e)

# [우리 FIS 아카데미 6기] Logback PII Masking Module (Log-Masker)

> **금융권 프로젝트를 위한 개인정보(PII) 자동 마스킹 로그백 모듈**<br>
> **"보안은 선택이 아닌 필수입니다."**<br>
> 이 모듈은 개발자가 비즈니스 로직에서 별도의 마스킹 처리를 하지 않아도됩니다!<br>
> 저희 모듈을 사용하면, 로그가 기록되는 시점에 자동으로 민감 정보(주민번호, 계좌번호 등)를 감지하여 마스킹 처리합니다.

## 프로젝트 설명

금융 시스템 개발 과정에서 로그에 남는 민감 정보(PII: Personally Identifiable Information)는 보안 취약점이 될 수 있습니다.
이 프로젝트는 **Logback** 프레임워크를 확장하여, 로그 메시지 내의 민감 정보를 정규식(Regex) 기반으로 탐지하고 마스킹(Masking)하는 커스텀 컨버터입니다.

단순한 기능 구현을 넘어, **객체지향 설계 원칙(SOLID)**을 준수하고 **디자인 패턴(Adapter, Strategy, Factory 등)**을 실무에 적용하여 유지보수성과 확장성을 높이는 데 주력했습니다.

### ✨ 핵심 기능

* **원활한 통합**: 기존 `logback.xml` 설정에 한 줄만 추가하면 즉시 적용 가능.
* **다양한 타입 지원**: 주민등록번호, 휴대폰 번호, 계좌번호, 카드번호 등 다양한 PII 타입 지원.
* **디자인 패턴**: 전략 패턴과 어댑터 패턴을 적용하여 새로운 마스킹 정책 추가 용이.
* **키워드 감지**: 키워드 기반 감지 (`ssn`, `password` 등) 및 금지어 정책 설정 가능.

---

## 🚀 시작하기

이 모듈을 당신의 프로젝트에 적용하는 가장 빠르고 간단한 방법입니다.

### Prerequisites

* Java 17+
* Spring Boot (or pure Logback dependency)
* logback-classic-1.4.5.jar
* logback-core-1.4.5.jar
* slf4j-api-2.0.17.jar

### Installation

1. 프로젝트를 클론하고 빌드하여 JAR 파일을 생성합니다.

```shell
git clone https://github.com/fluanceifi/log-masking-module.git
cd log-masking-module

# 저희는 자바자체로만 구현하여 빌드 툴을 사용하진 않았습니다.
./gradlew clean build
```

2. 생성된 JAR를 로컬 라이브러리로 추가하거나, 소스 코드를 프로젝트 패키지에 포함시킵니다.

### Configuration (`logback.xml`)

1. resources 디렉토리에서 logback.xml 파일을 생성합니다.

2. Logback 설정 파일에 `conversionRule`을 등록하여 마스킹 컨버터를 활성화합니다.

```xml
<configuration>
    <conversionRule conversionWord="msg" 
                    converterClass="ch.qos.logback.classic.pattern.PiiConverter" />

    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="STDOUT" />
    </root>
</configuration>

```

---

## 🛠 개발과정

이 프로젝트의 구조를 이해하고 기능을 확장하고 싶은 개발자를 위한 가이드입니다.

### Project Structure

```text
src/main/java/ch/qos/logback
├── classic/pattern/PiiConverter.java       # Entry Point (Logback 연동)
├── masking/
│   ├── engine/KeywordBasedLogMaskingEngine.java # 마스킹 로직 제어
│   ├── strategy/                           # 마스킹 알고리즘 (Strategy Pattern)
│   │   ├── AccountNumber.java
│   │   ├── PhoneNumber.java
│   │   ├── PrimaryAccountNumber.java       # 카드
│   │   └── ResidentNumber.java
│   └── adapter/MaskConvertorPiiMaskerAdapter.java # 인터페이스 변환 (Adapter Pattern)

```

### 적용된 디자인 패턴

이 프로젝트는 다음과 같은 디자인 패턴을 활용하여 설계되었습니다.

1. **Adapter Pattern (`MaskConvertorPiiMaskerAdapter`)**
* 서로 다른 인터페이스인 `MaskConvertor`와 `PiiMasker`를 연결하여 호환성 확보.


2. **Strategy Pattern (`PiiMasker`)**
* 주민번호, 계좌번호 등 마스킹 대상에 따라 서로 다른 알고리즘을 런타임에 교체하여 사용.


3. **Static Factory Method**
* 설정 파일이나 기본값으로부터 객체를 생성하는 로직을 캡슐화.



### 실제 사용법

아래와 같이 기존 코드를 작성하는 것처럼 똑같이 사용하시면 됩니다.

```Java
package test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConveterTest {

	private static final Logger logger = LoggerFactory.getLogger(ConveterTest.class);
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		logger.info("mobile: 010-9999-8888, card=1234-5678-1234-5678");
	}

}

```

```console
17:47  [34m[INFO ] [0;39m [main]  [36m[test.ConveterTest] [0;39m - mobile: 010-****-8888, card=1234-56**-****-5678

```



---

## ⚙️ Configuration Details

마스킹 동작 방식을 커스터마이징 할 수 있는 설정입니다. 
(현재 버전은 `PiiKeywordDictionary`를 통해 관리지만, 추후 사용자가 직접 제어할 수 있도록 개선이 가능합니다.)

| Keyword | Type | Description | Example |
| --- | --- | --- | --- |
| `ssn`, `resident` | RRN | 주민등록번호 패턴 감지 (######-#######) | `990101-1******` |
| `phone`, `mobile` | PHONE | 휴대폰 번호 패턴 감지 (010-xxxx-xxxx) | `010-1234-****` |
| `account` | ACCOUNT | 계좌번호 패턴 감지 | `123-45-******` |
| `card` | CARD | 신용카드 번호 패턴 감지 | `1234-****-****-1234` |

---

## 🤝 Contributing

기여는 언제나 환영합니다! 버그 제보, 기능 제안, PR 모두 감사합니다.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 🔗 링크
* **Woori FIS Academy**: [https://github.com/woorifisa-service-dev-6th]

---

## 📝 License

Distributed under the MIT License. See `LICENSE` for more information.

---

## 👥 Contributors

| Name | GitHub Profile |
| :---: | :--- |
| **박찬혁** | [@PetterChanHyuk](https://github.com/PetterChanHyuk) |
| **전시원** | [@siiion](https://github.com/siiion) |
| **유승준** | [@fluanceifi](https://github.com/fluanceifi) |
| **김동욱** | [@aransword](https://github.com/aransword) |
