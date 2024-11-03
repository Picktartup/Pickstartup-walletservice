package com.picktartup.wallet.controller;

import com.picktartup.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    /**
     * 새 지갑을 생성하고 주소를 반환하는 API
     * @param password 지갑 비밀번호
     * @param walletDirectory 사용자가 지정한 지갑 파일 저장 경로
     * @return 생성된 지갑 주소
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createWallet(
            @RequestParam String password,
            @RequestParam String walletDirectory) {
        try {
            String address = walletService.createWallet(password, walletDirectory);
            Map<String, String> response = new HashMap<>();
            response.put("address", address);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "지갑 생성 실패: " + e.getMessage()));
        }
    }

    /**
     * 지갑 주소의 BNB 잔액을 조회하는 API
     * @param address 지갑 주소
     * @return 잔액 (BNB)
     */
    @GetMapping("/balance")
    public ResponseEntity<Map<String, Object>> getWalletBalance(@RequestParam String address) {
        try {
            BigDecimal balance = walletService.getWalletBalance(address);
            Map<String, Object> response = new HashMap<>();
            response.put("address", address);
            response.put("balance", balance);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "잔액 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 지갑 주소를 기반으로 지갑을 삭제하는 API
     * @param address 삭제할 지갑의 주소
     * @return 삭제 결과
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, String>> deleteWallet(@RequestParam String address) {
        boolean isDeleted = walletService.deleteWallet(address);
        Map<String, String> response = new HashMap<>();

        if (isDeleted) {
            response.put("message", "지갑이 성공적으로 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "지갑이 존재하지 않습니다.");
            return ResponseEntity.status(404).body(response);
        }
    }
}

