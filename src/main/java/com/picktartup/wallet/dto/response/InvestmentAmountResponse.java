package com.picktartup.wallet.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InvestmentAmountResponse {
    private Long campaignId;
    private String investorAddress;
    private Long amount;
}
