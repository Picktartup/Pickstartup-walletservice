// 1. ErrorCode enum 정의
package com.picktartup.wallet.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Campaign
    CAMPAIGN_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "C001", "캠페인 생성에 실패했습니다."),
    CAMPAIGN_NOT_FOUND(HttpStatus.NOT_FOUND, "C002", "캠페인을 찾을 수 없습니다."),
    CAMPAIGN_STATUS_CHECK_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "C003", "캠페인 상태 조회에 실패했습니다."),
    CAMPAIGN_NOT_STARTED(HttpStatus.BAD_REQUEST, "C004", "캠페인이 아직 시작되지 않았습니다."),
    CAMPAIGN_ENDED(HttpStatus.BAD_REQUEST, "C005", "캠페인이 이미 종료되었습니다."),
    INVALID_CAMPAIGN_STATUS(HttpStatus.BAD_REQUEST, "C006", "유효하지 않은 캠페인 상태입니다."),
    CAMPAIGN_DETAIL_FETCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "C007", "캠페인 상세 정보 조회에 실패했습니다."),

    // Investment
    INVESTMENT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "I001", "투자에 실패했습니다."),
    INSUFFICIENT_TOKEN_BALANCE(HttpStatus.BAD_REQUEST, "I002", "토큰 잔액이 부족합니다."),
    TOKEN_APPROVAL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "I003", "토큰 승인에 실패했습니다."),
    INVESTOR_STATUS_FETCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "I004", "투자자 상태 조회에 실패했습니다."),

    // Refund
    REFUND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "R001", "환불 처리에 실패했습니다."),
    REFUND_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "R002", "환불이 불가능한 상태입니다."),
    NO_REFUND_AMOUNT(HttpStatus.BAD_REQUEST, "R003", "환불 가능한 금액이 없습니다."),

    // Admin
    ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, "A001", "관리자를 찾을 수 없습니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "A002", "권한이 없습니다."),
    EMERGENCY_WITHDRAW_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "A003", "긴급 출금에 실패했습니다."),

    // Contract 관련 에러
    CONTRACT_INTERACTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "T001", "컨트랙트 작업에 실패했습니다."),
    BALANCE_CHECK_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "T002", "잔액 조회에 실패했습니다."),

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

    // 시스템 관련 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S001", "내부 서버 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "S002", "잘못된 입력값입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}