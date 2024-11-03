package com.picktartup.wallet.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Setter
@Getter
@Table(name ="cointransaction")
@Entity
public class CoinTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_seq_generator")
    @SequenceGenerator(name = "transaction_seq_generator", sequenceName = "transaction_seq", allocationSize = 1)
    @Column(name = "transaction_id")
    private Long transactionId;


    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private Long coinAmount;
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users users;
}
