package com.picktartup.wallet.exception;

import lombok.Getter;

@Getter
public class TokenBalanceException extends TokenException {
    private final String address;  // 잔액 조회 시도한 주소
    private final String errorReason;  // 상세 에러 사유

    public TokenBalanceException(String message) {
        super(message);
        this.address = null;
        this.errorReason = null;
    }

    public TokenBalanceException(String message, String address) {
        super(message);
        this.address = address;
        this.errorReason = null;
    }

    public TokenBalanceException(String message, String address, String errorReason) {
        super(message);
        this.address = address;
        this.errorReason = errorReason;
    }

    public TokenBalanceException(String message, String address, String errorReason, Throwable cause) {
        super(message, cause);
        this.address = address;
        this.errorReason = errorReason;
    }

    @Override
    public String getMessage() {
        StringBuilder messageBuilder = new StringBuilder("토큰 잔액 조회 실패");

        if (address != null) {
            messageBuilder.append(" - 주소: ").append(address);
        }

        if (errorReason != null) {
            messageBuilder.append(", 사유: ").append(errorReason);
        }

        if (super.getMessage() != null) {
            messageBuilder.append(", 메시지: ").append(super.getMessage());
        }

        return messageBuilder.toString();
    }
}
