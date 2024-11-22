package com.picktartup.wallet.utils;

import java.math.BigInteger;

public class TokenUtils {
    private static final BigInteger TOKEN_DECIMALS = BigInteger.valueOf(10).pow(18);

    public static BigInteger toWei(Long tokenAmount) {
        return BigInteger.valueOf(tokenAmount).multiply(TOKEN_DECIMALS);
    }

    public static Long fromWei(BigInteger weiAmount) {
        return weiAmount.divide(TOKEN_DECIMALS).longValue();
    }
}
