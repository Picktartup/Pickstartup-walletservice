package com.picktartup.wallet.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Setter
@Getter
@Table(name ="users")
@Entity
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq_generator")
    @SequenceGenerator(name = "users_seq_generator", sequenceName = "users_seq", allocationSize = 1)
    @Column(name = "users_id")
    private Long userId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    private String username;
    private String email;
    private String encryptedPwd;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Boolean isActivated;
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private Set<Contract> contracts;

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private Set<CoinTransaction> transactions;

}
