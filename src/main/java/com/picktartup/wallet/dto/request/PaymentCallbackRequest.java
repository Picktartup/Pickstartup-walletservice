package com.picktartup.wallet.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class PaymentCallbackRequest {
    private String orderId;
    private Long userId;
    private BigDecimal amount;
    private String paymentId;
    private String paymentMethod;
}
