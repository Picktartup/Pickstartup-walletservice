package com.picktartup.wallet.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCampaignRequest {

    @NotNull
    private String name;

    @NotNull
    private String description;

    @NotNull
    private String startupWallet;

    @Positive
    private Long targetAmount;

    @NotNull
    private Long adminUserId;
}
