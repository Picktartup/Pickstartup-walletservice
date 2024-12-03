package com.picktartup.wallet.aop;

import com.picktartup.wallet.dto.PaymentDto;
import com.picktartup.wallet.dto.TransactionDto;
import com.picktartup.wallet.dto.WalletDto;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
public class WalletLoggingAspect {

    @Value("${app.service-name:wallet-service}")
    private String serviceName;

    /**
     * 공통 로그 기록 메서드
     */
    private void logBusinessAction(String action, Map<String, Object> additionalFields) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("log_type", "business");
        logData.put("action", action);
        logData.put("timestamp", LocalDateTime.now().toString());
        logData.put("service_name", serviceName); // 서비스 이름 추가
        logData.putAll(additionalFields);

        log.info("{}", logData); // JSON 형태로 기록
    }

    /**
     * 결제 웹훅 모니터링
     */
    @Around("execution(* com.picktartup.wallet.service.TokenService.mintTokenFromPayment(..))")
    public Object monitorPayment(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        PaymentDto.CompletedEvent payment = (PaymentDto.CompletedEvent) args[0];

        try {
            Object result = joinPoint.proceed();

            logBusinessAction("payment_completed", Map.of(
                    "amount", payment.getAmount(),
                    "transaction_id", payment.getTransactionId(),
                    "status", "success"
            ));

            return result;
        } catch (Exception e) {
            logBusinessAction("payment_failed", Map.of(
                    "amount", payment.getAmount(),
                    "transaction_id", payment.getTransactionId(),
                    "status", "failed",
                    "error_message", e.getMessage()
            ));
            throw e;
        }
    }

    /**
     * 지갑 잔고 조회 모니터링
     */
    @Around("execution(* com.picktartup.wallet.service.WalletService.getWalletBalanceByUserId(..))")
    public Object monitorBalanceCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Long userId = (Long) args[0];

        try {
            Object result = joinPoint.proceed();

            logBusinessAction("check_wallet_balance", Map.of(
                    "user_id", userId,
                    "status", "success"
            ));

            return result;
        } catch (Exception e) {
            logBusinessAction("check_wallet_balance_failed", Map.of(
                    "user_id", userId,
                    "status", "failed",
                    "error_message", e.getMessage()
            ));
            throw e;
        }

    }


    /**
     * 송금 패턴 모니터링
     */
    @Around("execution(* com.picktartup.wallet.service.TokenService.transferToAdmin(..))")
    public Object monitorTransfer(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        TransactionDto.Request request = (TransactionDto.Request) args[0];

        try {
            Object result = joinPoint.proceed();

            logBusinessAction("transfer_to_admin", Map.of(
                    "user_id", request.getUserId(),
                    "amount", request.getAmount(),
                    "status", "success"
            ));

            return result;
        } catch (Exception e) {
            logBusinessAction("transfer_to_admin_failed", Map.of(
                    "user_id", request.getUserId(),
                    "amount", request.getAmount(),
                    "status", "failed",
                    "error_message", e.getMessage()
            ));
            throw e;
        }
    }

    /**
     * 지갑 상태 변경 모니터링
     */
    @Around("execution(* com.picktartup.wallet.service.WalletService.updateWalletStatus(..))")
    public Object monitorWalletStatus(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Long walletId = (Long) args[0];
        WalletDto.UpdateStatus.Request request = (WalletDto.UpdateStatus.Request) args[1];

        try {
            Object result = joinPoint.proceed();

            logBusinessAction("update_wallet_status", Map.of(
                    "wallet_id", walletId,
                    "new_status", request.getStatus(),
                    "status", "success"
            ));

            return result;
        } catch (Exception e) {
            logBusinessAction("update_wallet_status_failed", Map.of(
                    "wallet_id", walletId,
                    "new_status", request.getStatus(),
                    "status", "failed",
                    "error_message", e.getMessage()
            ));
            throw e;
        }
    }

    /**
     * 잔고 업데이트 모니터링
     */
    @Around("execution(* com.picktartup.wallet.service.WalletService.updateBalance(..))")
    public Object monitorBalanceUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Long userId = (Long) args[0];

        try {
            Object result = joinPoint.proceed();

            logBusinessAction("update_wallet_balance", Map.of(
                    "user_id", userId,
                    "status", "success"
            ));

            return result;
        } catch (Exception e) {
            logBusinessAction("update_wallet_balance_failed", Map.of(
                    "user_id", userId,
                    "status", "failed",
                    "error_message", e.getMessage()
            ));
            throw e;
        }
    }
}
