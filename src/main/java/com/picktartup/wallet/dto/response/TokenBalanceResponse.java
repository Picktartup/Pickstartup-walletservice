package com.picktartup.wallet.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenBalanceResponse {
    private Long balance;
}
