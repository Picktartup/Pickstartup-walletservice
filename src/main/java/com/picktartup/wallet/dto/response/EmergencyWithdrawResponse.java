package com.picktartup.wallet.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmergencyWithdrawResponse {
    private Long campaignId;
    private String toAddress;
    private String transactionHash;
}
