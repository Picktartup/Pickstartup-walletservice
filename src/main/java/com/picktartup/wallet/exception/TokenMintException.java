package com.picktartup.wallet.exception;

import lombok.Getter;

import java.math.BigDecimal;

// TokenMintException.java
@Getter
public class TokenMintException extends TokenException {
    private final String orderId;  // 주문 ID
    private final BigDecimal amount;  // 발행 시도한 금액

    public TokenMintException(String message) {
        super(message);
        this.orderId = null;
        this.amount = null;
    }

    public TokenMintException(String message, String orderId, BigDecimal amount) {
        super(message);
        this.orderId = orderId;
        this.amount = amount;
    }

    public TokenMintException(String message, String orderId, BigDecimal amount, Throwable cause) {
        super(message, cause);
        this.orderId = orderId;
        this.amount = amount;
    }

    @Override
    public String getMessage() {
        if (orderId != null && amount != null) {
            return String.format("토큰 발행 실패 - 주문번호: %s, 금액: %s PKN, 메시지: %s",
                    orderId, amount, super.getMessage());
        }
        return super.getMessage();
    }
}