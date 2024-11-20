package com.picktartup.wallet.exception;

public class InvalidWalletPasswordException extends RuntimeException {
    public InvalidWalletPasswordException(String message) {
        super(message);
    }

    public InvalidWalletPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
