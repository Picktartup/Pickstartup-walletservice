package com.picktartup.wallet.exception;

public class InsufficientTokenException extends RuntimeException {
    public InsufficientTokenException(String message) {
        super(message);
    }

    public InsufficientTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
