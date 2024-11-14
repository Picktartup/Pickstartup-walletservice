package com.picktartup.wallet.exception;

public class BalanceUpdateException extends WalletException {
    public BalanceUpdateException(String message) {
        super(message, "BALANCE_UPDATE_FAILED");
    }
}
