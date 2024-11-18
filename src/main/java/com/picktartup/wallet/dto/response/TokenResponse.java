package com.picktartup.wallet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class TokenResponse {
    private String transactionHash;
    private BigDecimal amount;
    private String address;
    private LocalDateTime timestamp;
}
