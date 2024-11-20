package com.picktartup.wallet.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InvestorStatusResponse {
    private Long campaignId;
    private String investorAddress;
    private Long investedAmount;
    private Long campaignTotal;
    private Long sharePercentage;
}
