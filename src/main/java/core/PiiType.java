package core;

/** 로그 내 key 매칭 시 사용하는 PII 구분 타입. 사전(masking-keywords.properties)의 왼쪽 값과 일치해야 함. */
public enum PiiType {
	RRN,
	PHONE,
	ACCOUNT,
	CARD
}
