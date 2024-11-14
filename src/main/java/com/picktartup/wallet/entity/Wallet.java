package com.picktartup.wallet.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "wallet")
public class Wallet extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wallet_seq_generator")
    @SequenceGenerator(name = "wallet_seq_generator", sequenceName = "wallet_seq", allocationSize = 1)
    @Column(name = "wallet_id")
    private Long walletId;

    @Column(nullable = false)
    private Long userId;  // User 서비스의 User id를 참조값으로 저장

    @Column(nullable = false, unique = true)
    private String address;//지갑 주소

    @Column(name = "keystore_filename")
    private String keystoreFilename;  // 키스토어 파일명

    @Column(nullable = false)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletStatus status;

    @Builder
    public Wallet(Long userId, String address, String keystoreFilename, BigDecimal balance, WalletStatus status) {
        this.userId = userId;
        this.keystoreFilename = keystoreFilename;
        this.address = address;
        this.balance=balance;
        this.status=status;
    }

    // 잔고 업데이트
    public void updateBalance(BigDecimal newBalance) {
        this.balance = newBalance;
    }

}
