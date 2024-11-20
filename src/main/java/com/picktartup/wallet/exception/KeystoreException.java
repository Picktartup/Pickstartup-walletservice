package com.picktartup.wallet.exception;

public class KeystoreException extends RuntimeException {
    public KeystoreException(String message) {
        super(message);
    }

    public KeystoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
