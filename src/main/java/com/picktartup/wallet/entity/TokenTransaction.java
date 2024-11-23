package com.picktartup.wallet.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tokentransaction")
public class TokenTransaction{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private String orderId;

    @Column(nullable = false)
    private String walletAddress;

    @Column(nullable = false)
    private BigDecimal amount;  // 결제 금액 (원)

    @Column(nullable = false)
    private BigDecimal tokenAmount;  // 토큰 수량

    private String transactionHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    private LocalDateTime completedAt;

    @Column(length = 1000)
    private String failureReason;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
