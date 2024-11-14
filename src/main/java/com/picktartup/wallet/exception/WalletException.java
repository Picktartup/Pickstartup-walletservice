package com.picktartup.wallet.exception;

import lombok.Getter;

@Getter
public class WalletException extends RuntimeException {
    private final String errorCode;

    public WalletException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
