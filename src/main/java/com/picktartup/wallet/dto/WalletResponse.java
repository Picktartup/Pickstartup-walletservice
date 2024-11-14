package com.picktartup.wallet.dto;

import com.picktartup.wallet.entity.WalletStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class WalletResponse {
    private Long walletId;
    private Long userId;
    private String address;
    private String keystoreFilename;
    private String temporaryPassword;
    private BigDecimal balance;
    private WalletStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

