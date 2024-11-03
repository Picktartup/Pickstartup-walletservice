package com.picktartup.wallet.entity;


import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Setter
@Getter
@Table(name = "wallet")
@Entity
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wallet_seq_generator")
    @SequenceGenerator(name = "wallet_seq_generator", sequenceName = "wallet_seq", allocationSize = 1)
    @Column(name = "wallet_id")
    private Long walletId;

    private String address;

    private Double balance;

}
