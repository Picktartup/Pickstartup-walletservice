package com.picktartup.wallet.service;

import com.picktartup.wallet.dto.request.PaymentCompletedEvent;
import com.picktartup.wallet.dto.response.TransactionResponse;
import com.picktartup.wallet.entity.TokenTransaction;
import com.picktartup.wallet.entity.TransactionStatus;
import com.picktartup.wallet.entity.TransactionType;
import com.picktartup.wallet.entity.Wallet;
import com.picktartup.wallet.exception.TokenBalanceException;
import com.picktartup.wallet.exception.TokenMintException;
import com.picktartup.wallet.exception.WalletNotFoundException;
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
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import com.picktartup.wallet.contracts.PickenToken;

/**
 * TokenTransferService - 토큰 전송과 관련된 서비스 ( 인증 및 자격 증명 로직을 포함하고 토큰 전송 내역도 관리 )
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final Web3j web3j;
    private final WalletRepository walletRepository;
    private final TokenTransactionRepository tokenTransactionRepository;
    private final String contractAddress;
    private final Credentials adminCredentials;
    private final ContractGasProvider gasProvider;
    private final String keystoreDirectory;


    // 1. PG 결제 후 토큰 발행 (관리자)
    @Transactional
    public TransactionResponse mintTokenFromPayment(PaymentCompletedEvent request) {
        try {
            log.info("토큰 발행 시작 - 주문번호: {}, 금액: {}", request.getOrderId(), request.getAmount());

            // 사용자 지갑 조회
            Wallet userWallet = walletRepository.findByUserId(request.getUserId())
                    .orElseThrow(() -> new WalletNotFoundException("지갑을 찾을 수 없습니다."));

            // 토큰 발행 금액 계산 (100원 = 1 PKN)
            BigDecimal tokenAmount = request.getAmount()
                    .divide(BigDecimal.valueOf(100), 8, RoundingMode.FLOOR);
            BigInteger tokenAmountWei = Convert.toWei(
                    tokenAmount.toString(),
                    Convert.Unit.ETHER
            ).toBigInteger();

            // 토큰 트랜잭션 엔티티 생성
            TokenTransaction transaction = TokenTransaction.builder()
                    .userId(request.getUserId())
                    .orderId(request.getOrderId())
                    .paymentId(request.getPaymentId())
                    .walletAddress(userWallet.getAddress())
                    .amount(request.getAmount())
                    .tokenAmount(tokenAmount)
                    .type(TransactionType.MINT)
                    .status(TransactionStatus.PENDING)
                    .build();

            // 트랜잭션 저장 (PENDING 상태로)
            TokenTransaction savedTransaction = tokenTransactionRepository.save(transaction);

            try {
                // 컨트랙트 호출
                PickenToken tokenContract = PickenToken.load(
                        contractAddress,
                        web3j,
                        adminCredentials,
                        new DefaultGasProvider()
                );

                // mintFromPayment 실행
                TransactionReceipt receipt = tokenContract.mintFromPayment(
                        userWallet.getAddress(),
                        tokenAmountWei,
                        request.getOrderId()
                ).send();

                String txHash = receipt.getTransactionHash();

                // 트랜잭션 상태 업데이트
                savedTransaction.setTransactionHash(txHash);
                savedTransaction.setStatus(TransactionStatus.COMPLETED);
                savedTransaction.setCompletedAt(LocalDateTime.now());
                tokenTransactionRepository.save(savedTransaction);

                log.info("토큰 발행 완료 - 트랜잭션 해시: {}, 발행량: {} PKN", txHash, tokenAmount);

                // TransactionResponse 생성 및 반환
                return TransactionResponse.builder()
                        .transactionHash(txHash)
                        .from("0x0000000000000000000000000000000000000000") // 민팅의 경우 0 주소
                        .to(userWallet.getAddress())
                        .amount(tokenAmount)
                        .status("SUCCESS")
                        .build();

            } catch (Exception e) {
                // 컨트랙트 호출 실패 시 트랜잭션 상태 업데이트
                savedTransaction.setStatus(TransactionStatus.FAILED);
                savedTransaction.setFailureReason(e.getMessage());
                tokenTransactionRepository.save(savedTransaction);

                // 실패한 경우의 응답
                return TransactionResponse.builder()
                        .transactionHash(null)
                        .from("0x0000000000000000000000000000000000000000")
                        .to(userWallet.getAddress())
                        .amount(tokenAmount)
                        .status("FAILED")
                        .build();
            }

        } catch (Exception e) {
            log.error("토큰 발행 실패 - 주문번호: {}", request.getOrderId(), e);
            throw new TokenMintException("토큰 발행 중 오류 발생: " + e.getMessage());
        }
    }

//    // 2. 일반 토큰 전송
//    public String transferToken(TokenTransferRequest request, String keystoreFilename, String password) {
//        if (request == null || keystoreFilename == null || password == null) {
//            throw new IllegalArgumentException("필수 파라미터가 누락되었습니다.");
//        }
//        try {
//            log.info("토큰 전송 시작 - From: {}, To: {}, 금액: {}",
//                    request.getFromAddress(), request.getToAddress(), request.getAmount());
//
//            // 키스토어에서 자격증명 로드
//            Credentials credentials = WalletUtils.loadCredentials(
//                    password,
//                    keystoreDirectory + File.separator + keystoreFilename
//            );
//
//            // 발신자 주소 검증
//            if (!credentials.getAddress().equalsIgnoreCase(request.getFromAddress())) {
//                throw new InvalidAddressException("발신자 주소가 일치하지 않습니다.");
//            }
//
//            // 잔액 검증
//            BigDecimal balance = getTokenBalance(request.getFromAddress());
//            if (balance.compareTo(request.getAmount()) < 0) {
//                throw new InsufficientBalanceException(
//                        String.format("잔액 부족 (보유: %s PKN, 필요: %s PKN)",
//                                balance, request.getAmount())
//                );
//            }
//
//            // 컨트랙트 로드 및 전송
//            PickenToken tokenContract = PickenToken.load(
//                    contractAddress,
//                    web3j,
//                    credentials,
//                    new DefaultGasProvider()
//            );
//
//            BigInteger amountWei = Convert.toWei(
//                    request.getAmount().toString(),
//                    Convert.Unit.ETHER
//            ).toBigInteger();
//
//            TransactionReceipt receipt = tokenContract.transfer(
//                    request.getToAddress(),
//                    amountWei
//            ).send();
//
//            String txHash = receipt.getTransactionHash();
//            log.info("토큰 전송 완료 - 트랜잭션 해시: {}", txHash);
//
//            return txHash;
//
//        } catch (Exception e) {
//            log.error("토큰 전송 실패", e);
//            throw new TokenTransferException("토큰 전송 중 오류 발생: " + e.getMessage());
//        }
//    }

    // 3. 토큰 잔액 조회
    public BigDecimal getTokenBalance(String address) {
        try {
            PickenToken tokenContract = PickenToken.load(
                    contractAddress,
                    web3j,
                    adminCredentials,    // 의존성 주입된 adminCredentials 사용
                    gasProvider         // 의존성 주입된 gasProvider 사용
            );

            BigInteger balance = tokenContract.balanceOf(address).send();
            return Convert.fromWei(new BigDecimal(balance), Convert.Unit.ETHER);

        } catch (Exception e) {
            log.error("잔액 조회 실패 - 주소: {}", address, e);
            throw new TokenBalanceException("잔액 조회 중 오류 발생: " + e.getMessage());
        }
    }

}