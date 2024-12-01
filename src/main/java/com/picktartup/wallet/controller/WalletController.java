package com.picktartup.wallet.controller;

import com.picktartup.wallet.dto.PaymentDto;
import com.picktartup.wallet.dto.TransactionDto;
import com.picktartup.wallet.dto.WalletDto;
import com.picktartup.wallet.dto.response.BaseResponse;
import com.picktartup.wallet.exception.BusinessException;
import com.picktartup.wallet.service.ResponseService;
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

    private final ResponseService responseService;
    private final WalletService walletService;
    private final TokenService tokenService;

    @GetMapping("/health_check")
    public String healthCheck() {
        // 헬스 체크 정보를 문자열로 생성
        StringBuilder healthStatus = new StringBuilder();
        healthStatus.append("Status: UP\n");
        healthStatus.append("Message: Service is running smoothly\n");
        healthStatus.append("jenkins, argocd CI/CD Test Succeeded\n");

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
    @GetMapping("/user/{userId}")
    public BaseResponse<WalletDto.Create.Response> getWalletByUserId(@PathVariable Long userId) {
        try {
            log.info("Successfully returned wallet info for userId: {}", userId);
            WalletDto.Create.Response result = walletService.getWalletByUserId(userId);
            return BaseResponse.success(result);
        } catch (BusinessException e) {
            return BaseResponse.error(e.getMessage(), e.getErrorCode().toString());
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage(), "INTERNAL_SERVER_ERROR");
        }
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
    public ResponseEntity<BaseResponse<TransactionDto.Response>> handlePaymentCallback(
            @RequestBody PaymentDto.Callback.Request request) {
        log.info("결제 완료 웹훅 수신 - 주문번호: {}", request.getTransactionId());

        PaymentDto.CompletedEvent event = PaymentDto.CompletedEvent.from(request);

        TransactionDto.Response result = tokenService.mintTokenFromPayment(event);
        return ResponseEntity.ok(
                BaseResponse.success(result)
        );
    }

    // TODO: 네트워크 상에 반영
    // 관리자 지갑으로 토큰 전송
    @PostMapping("/transmission/admin")
    public ResponseEntity<BaseResponse<TransactionDto.Response>> transmit(
            @RequestBody @Valid TransactionDto.Request request
    ) {
        log.info("환급 요청 - userId: {}, amount: {}",
                    request.getUserId(), request.getAmount());

        return ResponseEntity.ok(
                BaseResponse.success(
                        tokenService.transferToAdmin(request)
                )
        );
    }
}

