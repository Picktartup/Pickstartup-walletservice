package com.picktartup.wallet.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CampaignDetailResponse {
    private Long campaignId;
    private Long targetAmount;
    private Long currentBalance;
    private Long remainingAmount;
}
