package com.picktartup.wallet.aop;

import com.picktartup.wallet.dto.PaymentDto;
import com.picktartup.wallet.dto.TransactionDto;
import com.picktartup.wallet.dto.WalletDto;
import com.picktartup.wallet.entity.TokenTransaction;
import com.picktartup.wallet.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Aspect
@Component
public class WalletLoggingAspect {

    @Value("${app.service-name:wallet-service}")
    private String serviceName;

    /**
     * 공통 로그 기록 메서드
     */
    private void logApiCall(Map<String, Object> logData, boolean success, int httpStatus, long responseTime, String errorMessage) {
        logData.put("status", success ? "success" : "failed");
        logData.put("http_status", httpStatus);
        logData.put("response_time_ms", responseTime);
        if (errorMessage != null) {
            logData.put("error_message", errorMessage);
        }
        log.info("{}", logData);
    }

    /**
     * 공통 API 호출 로그
     */
    @Around("execution(* com.picktartup.wallet.controller.*.*(..))")
    public Object logAllApiCalls(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        Map<String, Object> logData = new HashMap<>();
        logData.put("http_method", request.getMethod());
        logData.put("uri", request.getRequestURI());
        logData.put("api_path", request.getRequestURI().replaceAll("/\\d+", "/{id}"));
        logData.put("api_name", ((MethodSignature) joinPoint.getSignature()).getMethod().getName());
        logData.put("client_ip", request.getRemoteAddr());
        logData.put("request_id", UUID.randomUUID().toString());
        logData.put("timestamp", LocalDateTime.now().toString());
        logData.put("service_name", serviceName);

        try {
            Object result = joinPoint.proceed();
            long responseTime = System.currentTimeMillis() - startTime;

            // 성공 로그
            int httpStatus = (result instanceof ResponseEntity) ?
                    ((ResponseEntity<?>) result).getStatusCodeValue() : 200;
            logApiCall(logData, true, httpStatus, responseTime, null);

            return result;
        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;

            // 실패 로그
            logApiCall(logData, false, 500, responseTime, e.getMessage());
            throw e;
        }
    }

    /**
     * 결제 웹훅 로직 (토큰화 금액 포함)
     */
    @Around("execution(* com.picktartup.wallet.service.TokenService.mintTokenFromPayment(..))")
    public Object monitorPayment(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        PaymentDto.CompletedEvent payment = (PaymentDto.CompletedEvent) args[0];

        Map<String, Object> logData = new HashMap<>();
        logData.put("log_type", "business");
        logData.put("action", "payment_completed");
        logData.put("amount", payment.getAmount()); // 결제 금액
        logData.put("transaction_id", payment.getTransactionId()); // 주문번호
        logData.put("service_name", serviceName);
        logData.put("timestamp", LocalDateTime.now().toString());

        // 서비스에서 전달된 토큰화 금액 사용
        try {
            Object result = joinPoint.proceed();

            // 성공 로그
            if (result instanceof TokenTransaction) {
                TokenTransaction transaction = (TokenTransaction) result;
                logData.put("tokenized_amount", transaction.getTokenAmount()); // 토큰화 금액
                logData.put("tokenized_amount_wei", TokenUtils.toWei(transaction.getTokenAmount().doubleValue())); // Wei 변환
            }
            logApiCall(logData, true, 200, 0, null); // 성공 시 HTTP 200
            return result;
        } catch (Exception e) {
            logApiCall(logData, false, 500, 0, e.getMessage()); // 실패 시 HTTP 500
            throw e;
        }
    }


    /**
     * 잔고 조회 로직
     */
    @Around("execution(* com.picktartup.wallet.service.WalletService.getWalletBalanceByUserId(..))")
    public Object monitorBalanceCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Long userId = (Long) args[0];
        long startTime = System.currentTimeMillis();

        Map<String, Object> logData = new HashMap<>();
        logData.put("log_type", "business");
        logData.put("action", "check_wallet_balance");
        logData.put("user_id", userId);
        logData.put("timestamp", LocalDateTime.now().toString());
        logData.put("service_name", serviceName);

        try {
            Object result = joinPoint.proceed();
            long responseTime = System.currentTimeMillis() - startTime;

            // 성공 로그
            logApiCall(logData, true, 200, responseTime, null);
            return result;
        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;

            // 실패 로그
            logApiCall(logData, false, 500, responseTime, e.getMessage());
            throw e;
        }
    }

    /**
     * 잔고 업데이트 로직
     */
    @Around("execution(* com.picktartup.wallet.service.WalletService.updateBalance(..))")
    public Object monitorBalanceUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Long userId = (Long) args[0];
        long startTime = System.currentTimeMillis();

        Map<String, Object> logData = new HashMap<>();
        logData.put("log_type", "business");
        logData.put("action", "update_wallet_balance");
        logData.put("user_id", userId);
        logData.put("timestamp", LocalDateTime.now().toString());
        logData.put("service_name", serviceName);

        try {
            Object result = joinPoint.proceed();
            long responseTime = System.currentTimeMillis() - startTime;

            // 성공 로그
            logApiCall(logData, true, 200, responseTime, null);
            return result;
        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;

            // 실패 로그
            logApiCall(logData, false, 500, responseTime, e.getMessage());
            throw e;
        }
    }
    @Around("execution(* com.picktartup.wallet.service.TokenService.transferToAdmin(..))")
    public Object monitorTokenRefund(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        TransactionDto.Request request = (TransactionDto.Request) args[0];

        Map<String, Object> logData = new HashMap<>();
        logData.put("log_type", "business");
        logData.put("action", "token_refund");
        logData.put("user_id", request.getUserId());
        logData.put("transaction_id", request.getTransactionId());
        logData.put("requested_token_amount", request.getAmount());
        logData.put("timestamp", LocalDateTime.now().toString());
        logData.put("service_name", serviceName);

        try {
            Object result = joinPoint.proceed();

            if (result instanceof TransactionDto.Response) {
                TransactionDto.Response response = (TransactionDto.Response) result;
                logData.put("status", "success");
                logData.put("refunded_cash_amount", response.getAmount());
                logData.put("transaction_hash", response.getTransactionHash());
            }

            logApiCall(logData, true, 200, 0, null);
            return result;
        } catch (Exception e) {
            logApiCall(logData, false, 500, 0, e.getMessage());
            throw e;
        }
    }

}
