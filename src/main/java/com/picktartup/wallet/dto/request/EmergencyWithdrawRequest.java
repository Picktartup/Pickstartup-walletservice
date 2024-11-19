package com.picktartup.wallet.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmergencyWithdrawRequest {
    @NotNull
    private Long adminUserId;

    @NotNull
    private String toAddress;
}
