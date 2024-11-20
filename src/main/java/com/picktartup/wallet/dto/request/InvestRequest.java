package com.picktartup.wallet.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvestRequest {

    @NotNull
    private Long userId;

    @Positive
    private Long amount;

    @NotNull
    @NotEmpty
    private String walletPassword;  // 지갑 비밀번호
}
