package com.picktartup.wallet.service;

import com.picktartup.wallet.dto.PaymentDto;
import com.picktartup.wallet.dto.TransactionDto;
import com.picktartup.wallet.entity.TokenTransaction;
import com.picktartup.wallet.entity.TransactionStatus;
import com.picktartup.wallet.entity.TransactionType;
import com.picktartup.wallet.entity.Wallet;
import com.picktartup.wallet.exception.*;
import com.picktartup.wallet.repository.TokenTransactionRepository;
import com.picktartup.wallet.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import com.picktartup.wallet.contracts.PickenToken;

/**
 * TokenService - 토큰 전송과 관련된 서비스 ( 인증 및 자격 증명 로직을 포함하고 토큰 전송 내역도 관리 )
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final Web3j web3j;
    private final WalletRepository walletRepository;
    private final TokenTransactionRepository tokenTransactionRepository;
    private final String tokenContractAddress;
    private final Credentials adminCredentials;
    private final ContractGasProvider gasProvider;

    @Transactional
    public TransactionDto.Response mintTokenFromPayment(PaymentDto.CompletedEvent request) {
        log.info("토큰 발행 시작 - 주문번호: {}, 금액: {}", request.getOrderId(), request.getAmount());

        // 1. 사용자 지갑 조회
        Wallet userWallet = walletRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.WALLET_NOT_FOUND));

        // 2. 토큰 발행 금액 계산 및 검증
        BigDecimal tokenAmount = calculateTokenAmount(request.getAmount());
        validateTokenAmount(tokenAmount);

        // 3. 트랜잭션 생성 및 저장
        TokenTransaction transaction = createTokenTransaction(request, userWallet, tokenAmount);
        TokenTransaction savedTransaction = tokenTransactionRepository.save(transaction);

        try {
            // 4. 컨트랙트 실행
            TransactionReceipt receipt = executeTokenMint(userWallet.getAddress(), tokenAmount, request.getOrderId());

            // 5. 트랜잭션 완료 처리
            updateTransactionSuccess(savedTransaction, receipt.getTransactionHash());

            log.info("토큰 발행 완료 - 트랜잭션 해시: {}, 발행량: {} PKN",
                    receipt.getTransactionHash(), tokenAmount);

            return createSuccessResponse(receipt.getTransactionHash(), userWallet.getAddress(), tokenAmount);

        } catch (Exception e) {
            // 6. 실패 처리
            log.error("토큰 발행 실패 - 주문번호: {}", request.getOrderId(), e);
            updateTransactionFailure(savedTransaction, e.getMessage());
            throw new BusinessException(ErrorCode.TOKEN_MINT_FAILED,
                    "토큰 발행 중 오류 발생: " + e.getMessage(), e);
        }
    }

    public BigDecimal getTokenBalance(String address) {
        try {
            PickenToken tokenContract = loadTokenContract();
            BigInteger balance = tokenContract.balanceOf(address).send();
            return Convert.fromWei(new BigDecimal(balance), Convert.Unit.ETHER);
        } catch (Exception e) {
            log.error("잔액 조회 실패 - 주소: {}", address, e);
            throw new BusinessException(ErrorCode.TOKEN_BALANCE_CHECK_FAILED,
                    "잔액 조회 중 오류 발생: " + e.getMessage(), e);
        }
    }

    // Private helper methods
    private BigDecimal calculateTokenAmount(BigDecimal paymentAmount) {
        return paymentAmount.divide(BigDecimal.valueOf(100), 8, RoundingMode.FLOOR);
    }

    private void validateTokenAmount(BigDecimal tokenAmount) {
        if (tokenAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN_AMOUNT);
        }
    }

    private TokenTransaction createTokenTransaction(
            PaymentDto.CompletedEvent request,
            Wallet userWallet,
            BigDecimal tokenAmount) {
        return TokenTransaction.builder()
                .userId(request.getUserId())
                .orderId(request.getOrderId())
                .paymentId(request.getPaymentId())
                .walletAddress(userWallet.getAddress())
                .amount(request.getAmount())
                .tokenAmount(tokenAmount)
                .type(TransactionType.MINT)
                .status(TransactionStatus.PENDING)
                .build();
    }

    private TransactionReceipt executeTokenMint(
            String address,
            BigDecimal tokenAmount,
            String orderId) throws Exception {
        PickenToken tokenContract = loadTokenContract();
        BigInteger tokenAmountWei = Convert.toWei(
                tokenAmount.toString(),
                Convert.Unit.ETHER
        ).toBigInteger();

        return tokenContract.mintFromPayment(address, tokenAmountWei, orderId).send();
    }

    private PickenToken loadTokenContract() {
        return PickenToken.load(
                tokenContractAddress,
                web3j,
                adminCredentials,
                gasProvider
        );
    }

    private void updateTransactionSuccess(TokenTransaction transaction, String txHash) {
        transaction.setTransactionHash(txHash);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setCompletedAt(LocalDateTime.now());
        tokenTransactionRepository.save(transaction);
    }

    private void updateTransactionFailure(TokenTransaction transaction, String errorMessage) {
        transaction.setStatus(TransactionStatus.FAILED);
        transaction.setFailureReason(errorMessage);
        tokenTransactionRepository.save(transaction);
    }

    private TransactionDto.Response createSuccessResponse(
            String txHash,
            String toAddress,
            BigDecimal amount) {
        return TransactionDto.Response.builder()
                .transactionHash(txHash)
                .from("0x0000000000000000000000000000000000000000")
                .to(toAddress)
                .amount(amount)
                .status("SUCCESS")
                .build();
    }
}