package com.picktartup.wallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class PaymentDto {

    // 결제 콜백 요청/응답
    public static class Callback {
        @Getter
        @Builder
        public static class Request {
            @NotBlank(message = "주문번호는 필수입니다")
            private Long transactionId;

            @NotNull(message = "사용자 ID는 필수입니다")
            private Long userId;

            @NotNull(message = "결제 금액은 필수입니다")
            @Positive(message = "결제 금액은 0보다 커야 합니다")
            private BigDecimal amount;

            private String paymentMethod;
        }

        @Getter
        @Builder
        public static class Response {
            private String transactionHash;
            private String toAddress;
            private BigDecimal amount;
            private String status;
            private LocalDateTime processedAt;
        }
    }

    // 결제 완료 이벤트
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CompletedEvent {
        private Long transactionId;
        private Long userId;
        private BigDecimal amount;

        @Builder.Default
        private LocalDateTime completedAt = LocalDateTime.now();

        // Callback.Request로부터 이벤트 생성
        public static CompletedEvent from(Callback.Request request) {
            return CompletedEvent.builder()
                    .transactionId(request.getTransactionId())
                    .userId(request.getUserId())
                    .amount(request.getAmount())
                    .build();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CompletedEvent that = (CompletedEvent) o;
            return Objects.equals(transactionId, that.transactionId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(transactionId);
        }

        @Override
        public String toString() {
            return "CompletedEvent{" +
                    "transactionId='" + transactionId + '\'' +
                    ", userId=" + userId +
                    ", amount=" + amount +
                    ", completedAt=" + completedAt +
                    '}';
        }
    }
}