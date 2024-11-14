package com.picktartup.wallet.exception;

public class WalletCreationException extends WalletException {
    public WalletCreationException(String message) {
        super(message, "WALLET_CREATION_FAILED");
    }
}
