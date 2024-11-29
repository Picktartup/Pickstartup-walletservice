package com.picktartup.wallet.utils;

import java.math.BigDecimal;
import java.math.BigInteger;

public class TokenUtils {
    private static final BigInteger TOKEN_DECIMALS = BigInteger.valueOf(10).pow(18);

    public static BigInteger toWei(Double tokenAmount) {
        if (tokenAmount == null) {
            throw new IllegalArgumentException("Token amount cannot be null");
        }
        // 소수점을 처리하기 위해 Double 값을 문자열로 변환한 뒤 BigDecimal로 생성
        BigDecimal tokenAmountDecimal = BigDecimal.valueOf(tokenAmount);

        // 10^TOKEN_DECIMALS 자릿수를 적용
        BigDecimal weiValue = tokenAmountDecimal.multiply(new BigDecimal(TOKEN_DECIMALS));

        // BigDecimal을 BigInteger로 변환하여 반환
        return weiValue.toBigIntegerExact(); // 정확한 변환
    }

    public static Long fromWei(BigInteger weiAmount) {
        return weiAmount.divide(TOKEN_DECIMALS).longValue();
    }
}
