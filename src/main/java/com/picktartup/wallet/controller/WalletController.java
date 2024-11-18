package com.picktartup.wallet.controller;

import com.picktartup.wallet.dto.request.*;
import com.picktartup.wallet.dto.response.BaseResponse;
import com.picktartup.wallet.dto.response.TransactionResponse;
import com.picktartup.wallet.dto.response.WalletResponse;
import com.picktartup.wallet.service.TokenService;
import com.picktartup.wallet.service.WalletService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final TokenService tokenService;

    // 지갑 생성 
    @PostMapping
    public ResponseEntity<BaseResponse<WalletResponse>> createWallet(
            @RequestBody @Valid CreateWalletRequest request
    ) {
        log.info("지갑 생성 요청 - userId: {}", request.getUserId());
        return ResponseEntity.ok(
                BaseResponse.success(walletService.createWallet(request))
        );
    }

    // 사용자의 지갑 정보 조회
    @GetMapping("/users/{userId}")
    public ResponseEntity<BaseResponse<WalletResponse>> getWalletByUserId(
            @PathVariable Long userId
    ) {
        log.info("지갑 정보 조회 - userId: {}", userId);
        return ResponseEntity.ok(
                BaseResponse.success(walletService.getWalletByUserId(userId))
        );
    }

    // 특정 지갑의 상태를 변경
    @PatchMapping("/{walletId}/status")
    public ResponseEntity<BaseResponse<WalletResponse>> updateWalletStatus(
            @PathVariable Long walletId,
            @RequestBody @Valid UpdateWalletStatusRequest request) {
        log.info("지갑 상태 변경 요청 - walletId: {}, status: {}", walletId, request.getStatus());
        WalletResponse response = walletService.updateWalletStatus(walletId, request);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    // 특정 지갑의 잔고를 네트워크에서 조회하여 DB에 업데이트
    @PostMapping("/{address}/update-balance")
    public ResponseEntity<BaseResponse<String>> updateBalance(@PathVariable String address) {
        log.info("잔고 업데이트 요청 - address: {}", address);
        walletService.updateBalance(address);
        return ResponseEntity.ok(BaseResponse.success("잔고가 성공적으로 업데이트되었습니다."));
    }

    // 특정 지갑의 잔고 조회(DB 조회 O, 실제 네트워크 X )


    // PG사 결제 완료 웹훅
    @PostMapping("/payment/callback")
    public ResponseEntity<TransactionResponse> handlePaymentCallback(
            @RequestBody PaymentCallbackRequest request) {
        log.info("결제 완료 웹훅 수신 - 주문번호: {}", request.getOrderId());

        PaymentCompletedEvent event = PaymentCompletedEvent.builder()
                .orderId(request.getOrderId())
                .userId(request .getUserId())
                .amount(request.getAmount())
                .paymentId(request.getPaymentId())
                .build();

        TransactionResponse result = tokenService.mintTokenFromPayment(event);
        return ResponseEntity.ok(result);
    }

}

