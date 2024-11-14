package com.picktartup.wallet.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class BalanceResponse {
    private String address;
    private BigDecimal balance;
    private LocalDateTime updatedAt;
}
