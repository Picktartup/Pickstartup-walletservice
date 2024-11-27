// 1. ErrorCode enum 정의
package com.picktartup.wallet.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Keystore
    KEYSTORE_FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "K001", "Keystore 파일을 찾을 수 없습니다."),
    KEYSTORE_READ_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "K002", "Keystore 파일을 읽을 수 없습니다."),
    PRIVATE_KEY_DECRYPT_FAILED(HttpStatus.BAD_REQUEST, "K003", "Private key 복호화에 실패했습니다."),
    INVALID_KEYSTORE_FORMAT(HttpStatus.BAD_REQUEST, "K004", "잘못된 Keystore 파일 형식입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "K005", "잘못된 비밀번호입니다."),

    // Token
    TOKEN_MINT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "T001", "토큰 발행에 실패했습니다."),
    TOKEN_BALANCE_CHECK_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "T002", "토큰 잔액 조회에 실패했습니다."),
    CONTRACT_EXECUTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "T003", "스마트 컨트랙트 실행에 실패했습니다."),
    INVALID_TOKEN_AMOUNT(HttpStatus.BAD_REQUEST, "T004", "유효하지 않은 토큰 금액입니다."),

    // Wallet
    WALLET_NOT_FOUND(HttpStatus.NOT_FOUND, "W001", "지갑을 찾을 수 없습니다."),
    WALLET_ALREADY_EXISTS(HttpStatus.CONFLICT, "W002", "이미 존재하는 지갑입니다."),
    WALLET_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "W003", "지갑 생성에 실패했습니다."),
    BALANCE_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "W004", "잔고 업데이트에 실패했습니다."),
    INVALID_WALLET_STATUS(HttpStatus.BAD_REQUEST, "W005", "유효하지 않은 지갑 상태입니다."),
    INVALID_WALLET_PASSWORD(HttpStatus.BAD_REQUEST, "W006", "잘못된 지갑 비밀번호입니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),
    USER_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "U002", "비활성화된 사용자입니다."),
    USER_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "U003", "사용자 서비스 오류가 발생했습니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "U004", "권한이 없는 사용자입니다."),

    // Coin
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "O001", "주문을 찾을 수 없습니다."),
    PAYMENT_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "O002", "주문 서비스 오류가 발생했습니다."),
    INVALID_PAYMENT_STATUS(HttpStatus.BAD_REQUEST, "O003", "유효하지 않은 주문 상태입니다."),
    PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "O004", "주문 금액이 일치하지 않습니다."),
    PAYMENT_USER_MISMATCH(HttpStatus.BAD_REQUEST, "O005", "주문 사용자가 일치하지 않습니다."),

    // 시스템 관련 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S001", "내부 서버 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "S002", "잘못된 입력값입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}