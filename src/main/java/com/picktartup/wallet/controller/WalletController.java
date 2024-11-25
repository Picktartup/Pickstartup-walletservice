package com.picktartup.wallet.controller;

import com.picktartup.wallet.dto.PaymentDto;
import com.picktartup.wallet.dto.TransactionDto;
import com.picktartup.wallet.dto.WalletDto;
import com.picktartup.wallet.dto.BaseResponse;
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

    @GetMapping("/health_check")
    public String healthCheck() {
        // 헬스 체크 정보를 문자열로 생성
        StringBuilder healthStatus = new StringBuilder();
        healthStatus.append("Status: UP\n");
        healthStatus.append("Message: Service is running smoothly");

        return healthStatus.toString(); // 텍스트 형식으로 반환
    }

    // 지갑 생성
    @PostMapping
    public ResponseEntity<BaseResponse<WalletDto.Create.Response>> createWallet(
            @RequestBody @Valid WalletDto.Create.Request request) {
        log.info("지갑 생성 요청 - userId: {}", request.getUserId());
        return ResponseEntity.ok(
                BaseResponse.success(walletService.createWallet(request))
        );
    }

    // 사용자의 지갑 정보 조회
    @GetMapping("/users/{userId}")
    public ResponseEntity<BaseResponse<WalletDto.Create.Response>> getWalletByUserId(
            @PathVariable Long userId) {
        log.info("지갑 정보 조회 - userId: {}", userId);
        return ResponseEntity.ok(
                BaseResponse.success(walletService.getWalletByUserId(userId))
        );
    }

    // 특정 지갑의 상태를 변경
    @PatchMapping("/{walletId}/status")
    public ResponseEntity<BaseResponse<WalletDto.Create.Response>> updateWalletStatus(
            @PathVariable Long walletId,
            @RequestBody @Valid WalletDto.UpdateStatus.Request request) {
        log.info("지갑 상태 변경 요청 - walletId: {}, status: {}", walletId, request.getStatus());
        WalletDto.Create.Response response = walletService.updateWalletStatus(walletId, request);
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
    @GetMapping("/users/{userId}/balance")
    public ResponseEntity<BaseResponse<WalletDto.Balance.Response>> getWalletBalanceByUserId(
            @PathVariable Long userId) {
        log.info("지갑 정보 조회 - userId: {}", userId);
        return ResponseEntity.ok(
                BaseResponse.success(walletService.getWalletBalanceByUserId(userId))
        );
    }

    // PG사 결제 완료 웹훅
    @PostMapping("/payment/callback")
    public ResponseEntity<TransactionDto.Response> handlePaymentCallback(
            @RequestBody PaymentDto.Callback.Request request) {
        log.info("결제 완료 웹훅 수신 - 주문번호: {}", request.getOrderId());

        PaymentDto.CompletedEvent event = PaymentDto.CompletedEvent.builder()
                .orderId(request.getOrderId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .build();

        TransactionDto.Response result = tokenService.mintTokenFromPayment(event);
        return ResponseEntity.ok(result);
    }
}

