package com.picktartup.wallet.dto;


import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDto {
    @Getter
    public static class Response {
        private final String transactionHash;  // 트랜잭션 해시
        private final String from;            // 보내는 주소
        private final String to;              // 받는 주소
        private final BigDecimal amount;      // 전송 금액
        private final String status;          // 트랜잭션 상태
        private final LocalDateTime timestamp; // 전송 시간

        @Builder
        public Response(String transactionHash, String from, String to,
                        BigDecimal amount, String status) {
            this.transactionHash = transactionHash;
            this.from = from;
            this.to = to;
            this.amount = amount;
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
