package com.picktartup.wallet.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class TransactionResponse {
    private String transactionHash;  // 트랜잭션 해시
    private String from;            // 보내는 주소
    private String to;              // 받는 주소
    private BigDecimal amount;      // 전송 금액
    private String status;          // 트랜잭션 상태 (PENDING, SUCCESS, FAILED)
    private LocalDateTime timestamp; // 전송 시간

    @Builder
    public TransactionResponse(String transactionHash, String from, String to,
                               BigDecimal amount, String status) {
        this.transactionHash = transactionHash;
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }
}
