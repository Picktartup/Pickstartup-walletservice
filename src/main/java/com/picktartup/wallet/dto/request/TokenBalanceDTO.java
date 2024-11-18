package com.picktartup.wallet.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class TokenBalanceDTO {
    private String address;
    private BigDecimal balance;
}
