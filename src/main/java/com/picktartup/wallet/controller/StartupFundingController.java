package com.picktartup.wallet.controller;

import com.picktartup.wallet.dto.request.CreateCampaignRequest;
import com.picktartup.wallet.dto.request.EmergencyWithdrawRequest;
import com.picktartup.wallet.dto.request.InvestRequest;
import com.picktartup.wallet.dto.response.*;
import com.picktartup.wallet.dto.response.BaseResponse;
import com.picktartup.wallet.service.StartupFundingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/startup-funding")
@RequiredArgsConstructor
public class StartupFundingController {

    private final StartupFundingService startupFundingService;

    // 1. 캠페인 생성
    @PostMapping("/campaigns")
    public ResponseEntity<BaseResponse<CampaignResponse>> createCampaign(
            @RequestBody @Valid CreateCampaignRequest request) {
        log.info("캠페인 생성 요청 - name: {}, targetAmount: {}",
                request.getName(), request.getTargetAmount());
        return ResponseEntity.ok(
                BaseResponse.success(startupFundingService.createCampaign(request))
        );
    }

    // 2. 투자하기
    @PostMapping("/campaigns/{campaignId}/invest")
    public ResponseEntity<BaseResponse<InvestmentResponse>> invest(
            @PathVariable Long campaignId,
            @RequestBody @Valid InvestRequest request
    ) {
        log.info("투자 요청 - campaignId: {}, amount: {}, userId: {}",
                campaignId, request.getAmount(), request.getUserId());
        return ResponseEntity.ok(
                BaseResponse.success(
                        startupFundingService.invest(campaignId, request)
                )
        );
    }

    // 3. 캠페인 상세 조회 - STARTUP 별 목표 금액, 현재 모금액, 남은 금액 조회
    @GetMapping("/campaigns/{campaignId}")
    public ResponseEntity<BaseResponse<CampaignDetailResponse>> getCampaignDetails(
            @PathVariable Long campaignId
    ) {
        log.info("캠페인 상세 조회 - campaignId: {}", campaignId);
        return ResponseEntity.ok(
                BaseResponse.success(
                        startupFundingService.getCampaignDetails(campaignId)
                )
        );
    }

    // 4. 투자자 상태 조회 (투자 금액, 스타트업 총 모금액, 지분율)
    @GetMapping("/campaigns/{campaignId}/investors/{userId}")
    public ResponseEntity<BaseResponse<InvestorStatusResponse>> getInvestorStatus(
            @PathVariable Long campaignId,
            @PathVariable Long userId
    ) {
        log.info("투자자 상태 조회 - campaignId: {}, userId: {}", campaignId, userId);
        return ResponseEntity.ok(
                BaseResponse.success(
                        startupFundingService.getInvestorStatus(campaignId, userId)
                )
        );
    }

    // 5. 특정 투자자의 투자 금액 조회
    @GetMapping("/campaigns/{campaignId}/investments/{address}")
    public ResponseEntity<BaseResponse<InvestmentAmountResponse>> getInvestmentAmount(
            @PathVariable Long campaignId,
            @PathVariable String address
    ) {
        log.info("투자 금액 조회 - campaignId: {}, address: {}", campaignId, address);
        return ResponseEntity.ok(
                BaseResponse.success(
                        startupFundingService.getInvestmentAmount(campaignId, address)
                )
        );
    }

    // 6. 컨트랙트의 총 토큰 보유량 확인
    @GetMapping("/total-held-tokens")
    public ResponseEntity<BaseResponse<TokenBalanceResponse>> getTotalHeldTokens() {
        log.info("컨트랙트 총 토큰 보유량 조회");
        return ResponseEntity.ok(
                BaseResponse.success(startupFundingService.getTotalHeldTokens())
        );
    }

    // 7. 긴급 출금 (관리자 전용)
    @PostMapping("/campaigns/{campaignId}/emergency-withdraw")
    public ResponseEntity<BaseResponse<EmergencyWithdrawResponse>> emergencyWithdraw(
            @PathVariable Long campaignId,
            @RequestBody @Valid EmergencyWithdrawRequest request
    ) {
        log.info("긴급 출금 요청 - campaignId: {}, toAddress: {}",
                campaignId, request.getToAddress());
        return ResponseEntity.ok(
                BaseResponse.success(
                        startupFundingService.emergencyWithdraw(campaignId, request)
                )
        );
    }
}

