package com.picktartup.wallet.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransferRequest {

    @NotNull(message = "보내는 사람의 사용자 ID는 필수입니다.")
    private Long userId;  // 새로 추가한 필드

    @NotNull(message = "받는 주소는 필수입니다.")
    private String toAddress;

    @NotNull(message = "전송할 금액은 필수입니다.")
    @Positive(message = "전송할 금액은 0보다 커야 합니다.")
    private BigDecimal amount;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @Builder
    public TransferRequest(Long userId, String toAddress, BigDecimal amount, String password) {
        this.userId = userId;
        this.toAddress = toAddress;
        this.amount = amount;
        this.password = password;
    }
}
