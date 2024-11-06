package com.picktartup.wallet.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Setter
@Getter
@Table(name = "contractdetails")
@Entity
public class ContractDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "details_seq_generator")
    @SequenceGenerator(name = "details_seq_generator", sequenceName = "details_seq", allocationSize = 1)
    @Column(name = "details_id")
    private Long detailsId;

    @OneToOne
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    // tokenAmount 이 자주 변경될 것이 예상되어 ContractDetails를 연관관계의 주인으로
    private Double tokenAmount;
    private String imgUrl;
    private String investorSignature;
    private String startupSignature;

}
