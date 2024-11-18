package com.picktartup.wallet.exception;

// TokenException.java (기본 예외 클래스)
public class TokenException extends RuntimeException {
    public TokenException(String message) {
        super(message);
    }

    public TokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
