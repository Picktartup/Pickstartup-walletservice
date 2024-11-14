package com.picktartup.wallet.exception;

public class InvalidAddressException extends WalletException {
    public InvalidAddressException(String message) {
        super(message, "INVALID_ADDRESS");
    }
}
