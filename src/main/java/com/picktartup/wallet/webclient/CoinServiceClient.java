package com.picktartup.wallet.webclient;

import com.picktartup.wallet.dto.TokenDto;
import com.picktartup.wallet.dto.response.BaseResponse;
import com.picktartup.wallet.exception.BusinessException;
import com.picktartup.wallet.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Component
@Slf4j
@RequiredArgsConstructor
public class CoinServiceClient {
    private final WebClient coinServiceWebClient;

    public void validatePayment(Long transactionId, Long userId, BigDecimal amount) {
        try {
            BaseResponse<TokenDto.PaymentValidationResponse> response = coinServiceWebClient.get()
                    .uri("/api/v1/coins/" + transactionId + "/validation")
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), clientResponse -> {
                        if (clientResponse.statusCode() == HttpStatus.NOT_FOUND) {
                            return Mono.error(new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
                        }
                        return Mono.error(new BusinessException(ErrorCode.PAYMENT_SERVICE_ERROR));
                    })
                    .onStatus(status -> status.is5xxServerError(), clientResponse ->
                            Mono.error(new BusinessException(ErrorCode.PAYMENT_SERVICE_ERROR)))
                    .bodyToMono(new ParameterizedTypeReference<BaseResponse<TokenDto.PaymentValidationResponse>>() {})
                    .block();

            if (response == null || response.getData() == null) {
                throw new BusinessException(ErrorCode.PAYMENT_SERVICE_ERROR);
            }

            TokenDto.PaymentValidationResponse paymentInfo = response.getData();

            // 결제 금액 검증
            if (!paymentInfo.getAmount().equals(amount)) {
                throw new BusinessException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
            }

            // 결제 사용자 검증
            if (!paymentInfo.getUserId().equals(userId)) {
                throw new BusinessException(ErrorCode.PAYMENT_USER_MISMATCH);
            }

        } catch (WebClientResponseException e) {
            log.error("Coin service error: {}", e.getMessage());
            throw new BusinessException(ErrorCode.PAYMENT_SERVICE_ERROR);
        }
    }
}