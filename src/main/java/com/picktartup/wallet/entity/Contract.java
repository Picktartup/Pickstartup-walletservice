package com.picktartup.wallet.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Setter
@Getter
@Table(name ="contract")
@Entity
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contract_seq_generator")
    @SequenceGenerator(name = "contract_seq_generator", sequenceName = "contract_seq", allocationSize = 1)
    @Column(name = "contract_id")
    private Long contractId;

    private LocalDateTime signedAt;

    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users users;

    @ManyToOne
    @JoinColumn(name = "startup_id", nullable = false)
    private Startup startup;

    @OneToOne(mappedBy = "contract", cascade = CascadeType.ALL)
    private ContractDetails contractDetails;

}
