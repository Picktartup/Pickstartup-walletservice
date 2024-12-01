package com.picktartup.wallet.dto;


import com.picktartup.wallet.utils.TokenUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

public class TransactionDto {
    @Getter
    @Setter
    @Builder
    public static class Request {
        @NotNull(message = "사용자 ID는 필수입니다")
        private Long userId;

        private Long transactionId;

        @NotBlank(message = "지갑 비밀번호는 필수입니다")
        private String walletPassword;

        @Positive(message = "투자 금액은 0보다 커야 합니다")
        private Double amount; //Picken 단위로 입력

        // Wei 단위로 변환하는 메서드
        public BigInteger getAmountInWei() {
            return TokenUtils.toWei(amount);
        }
    }

    @Getter
    public static class Response {
        private final String transactionHash;  // 트랜잭션 해시
        private final String from;            // 보내는 주소
        private final String to;              // 받는 주소
        private final BigDecimal amount;      // 전송 금액
        private final BigDecimal totalBalance; // 현재 총 잔액
        private final String status;          // 트랜잭션 상태
        private final LocalDateTime timestamp; // 전송 시간

        @Builder
        public Response(String transactionHash, String from, String to,
                        BigDecimal amount, BigDecimal totalBalance, String status) {
            this.transactionHash = transactionHash;
            this.from = from;
            this.to = to;
            this.amount = amount;
            this.totalBalance = totalBalance;
            this.status = status;
            this.timestamp = LocalDateTime.now();
        }
    }

    public enum Status {
        PENDING,
        SUCCESS,
        FAILED
    }
}
