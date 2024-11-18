//package com.picktartup.wallet.dto.request;
//
//import lombok.Builder;
//import lombok.Getter;
//import org.springframework.transaction.TransactionStatus;
//import org.web3j.crypto.transaction.type.TransactionType;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//@Getter
//@Builder
//public class TokenTransactionDTO {
//    private Long id;
//    private Long userId;
//    private String walletAddress;
//    private BigDecimal amount;
//    private String orderId;
//    private String transactionHash;
//    private TransactionStatus status;
//    private TransactionType type;
//    private LocalDateTime createdAt;
//
//    public static TokenTransactionDTO from(PaymentCompletedEvent entity) {
//        return TokenTransactionDTO.builder()
//                .id(entitㅛ)
//                .userId(entity.getUserId())
//                .walletAddress(entity.getWalletAddress())
//                .amount(entity.getAmount())
//                .orderId(entity.getOrderId())
//                .transactionHash(entity.getTransactionHash())
//                .status(entity.getStatus())
//                .type(entity.getType())
//                .createdAt(entity.getCreatedAt())
//                .build();
//    }
//}
