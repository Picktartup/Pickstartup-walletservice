package com.picktartup.wallet.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class CreateWalletRequest {

    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;
}