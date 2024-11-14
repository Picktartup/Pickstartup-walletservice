package com.picktartup.wallet.dto;

import com.picktartup.wallet.entity.WalletStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateWalletStatusRequest {

    @NotNull(message = "상태값은 필수입니다.")
    private WalletStatus status;
}
