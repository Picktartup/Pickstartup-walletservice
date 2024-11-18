package com.picktartup.wallet.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCompletedEvent {

    // 주문 번호
    private String orderId;

    // 사용자 ID
    private Long userId;

    // 결제 금액
    private BigDecimal amount;

    // PG사 결제 ID
    private String paymentId;

    // 결제 시간 (선택적으로 추가)
    @Builder.Default
    private LocalDateTime completedAt = LocalDateTime.now();

    // equals & hashCode (선택적)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentCompletedEvent that = (PaymentCompletedEvent) o;
        return Objects.equals(orderId, that.orderId) &&
                Objects.equals(paymentId, that.paymentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, paymentId);
    }

    // toString
    @Override
    public String toString() {
        return "PaymentCompletedEvent{" +
                "orderId='" + orderId + '\'' +
                ", userId=" + userId +
                ", amount=" + amount +
                ", paymentId='" + paymentId + '\'' +
                ", completedAt=" + completedAt +
                '}';
    }
}
