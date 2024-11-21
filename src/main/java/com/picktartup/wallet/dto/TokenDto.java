package com.picktartup.wallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TokenDto {

    public static class Transfer {
        @Getter
        @Builder
        public static class Request {
            @NotNull(message = "보내는 사람의 사용자 ID는 필수입니다.")
            private Long userId;  // 새로 추가한 필드

            @NotNull(message = "받는 주소는 필수입니다.")
            private String toAddress;

            @NotNull(message = "전송할 금액은 필수입니다.")
            @Positive(message = "전송할 금액은 0보다 커야 합니다.")
            private BigDecimal amount;

            @NotBlank(message = "비밀번호는 필수입니다.")
            private String password;
        }

        @Getter
        @Builder
        public static class Response {
            private String transactionHash;
            private String fromAddress;
            private String toAddress;
            private BigDecimal amount;
            private String status;
            private LocalDateTime transferredAt;
        }
    }

    public static class Balance {
        @Getter
        @Builder
        public static class Response {
            private String address;
            private BigDecimal balance;
        }
    }
}

