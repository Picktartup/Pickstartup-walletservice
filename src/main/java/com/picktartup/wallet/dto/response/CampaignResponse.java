package com.picktartup.wallet.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CampaignResponse {
    private Long campaignId;
    private String name;
    private String description;
    private Long targetAmount;
    private String transactionHash;
}