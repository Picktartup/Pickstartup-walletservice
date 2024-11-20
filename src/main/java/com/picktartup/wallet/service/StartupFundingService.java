package com.picktartup.wallet.service;

import com.picktartup.wallet.contracts.PickenToken;
import com.picktartup.wallet.contracts.StartupFunding;
import com.picktartup.wallet.dto.request.CreateCampaignRequest;
import com.picktartup.wallet.dto.request.EmergencyWithdrawRequest;
import com.picktartup.wallet.dto.request.InvestRequest;
import com.picktartup.wallet.dto.response.*;
import com.picktartup.wallet.entity.Wallet;
import com.picktartup.wallet.exception.BlockchainException;
import com.picktartup.wallet.exception.InsufficientTokenException;
import com.picktartup.wallet.exception.InvalidWalletPasswordException;
import com.picktartup.wallet.exception.WalletNotFoundException;
import com.picktartup.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.exception.CipherException;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class StartupFundingService {

    private final Web3j web3j;
    private final WalletRepository walletRepository;
    private final Credentials adminCredentials;
    private final String fundingContractAddress;
    private final String tokenContractAddress;
    private final KeystoreService keystoreService;
    private final ContractGasProvider gasProvider;


    // 스타트업 생성(관리자만 가능)
    @Transactional
    public CampaignResponse createCampaign(CreateCampaignRequest request) {
        // 관리자 지갑 조회
        Wallet adminWallet = walletRepository.findByUserId(request.getAdminUserId())
                .orElseThrow(() -> new RuntimeException("관리자 지갑을 찾을 수 없습니다."));

        //todo : user 조회해서 관리자 권한 확인 로직 추가

        try {
            // 컨트랙트 인스턴스 생성
            StartupFunding contract = StartupFunding.load(
                    fundingContractAddress,
                    web3j,
                    adminCredentials,
                    gasProvider
            );

            // 캠페인 생성 트랜잭션 실행
            TransactionReceipt receipt = contract.createCampaign(
                    request.getName(),
                    request.getDescription(),
                    request.getStartupWallet(),
                    BigInteger.valueOf(request.getTargetAmount())
            ).send();

            // 이벤트에서 캠페인 ID 추출
            BigInteger campaignId = contract.getCampaignCreatedEvents(receipt)
                    .get(0)
                    .campaignId;

            return CampaignResponse.builder()
                    .campaignId(campaignId.longValue())
                    .name(request.getName())
                    .description(request.getDescription())
                    .targetAmount(request.getTargetAmount())
                    .transactionHash(receipt.getTransactionHash())
                    .build();

        } catch (Exception e) {
            log.error("캠페인 생성 실패", e);
            throw new RuntimeException("캠페인 생성에 실패했습니다.", e);
        }
    }

    @Transactional
    public InvestmentResponse invest(Long campaignId, InvestRequest request) {
        // 투자자 지갑 조회
        Wallet investorWallet = walletRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new WalletNotFoundException("투자자 지갑을 찾을 수 없습니다."));

        try {
            // 투자자의 Keystore에서 자격증명 로드
            WalletFile walletFile = keystoreService.getWalletFile(investorWallet.getKeystoreFilename());

            // 사용자가 입력한 비밀번호로 private key 복호화
            Credentials investorCredentials;
            try {
                investorCredentials = Credentials.create(
                        keystoreService.decryptPrivateKey(walletFile, request.getWalletPassword())
                );
            } catch (CipherException e) {
                log.error("지갑 비밀번호가 올바르지 않습니다. userId: {}", request.getUserId());
                throw new InvalidWalletPasswordException("지갑 비밀번호가 올바르지 않습니다.");
            }

            StartupFunding contract = StartupFunding.load(
                    fundingContractAddress,
                    web3j,
                    investorCredentials,
                    gasProvider
            );

            // 투자 전 토큰 승인 여부 확인 및 처리
            if (!checkAndApproveTokens(investorCredentials, request.getAmount())) {
                throw new InsufficientTokenException("토큰 승인에 실패했습니다.");
            }

            TransactionReceipt receipt = contract.invest(
                    BigInteger.valueOf(campaignId),
                    BigInteger.valueOf(request.getAmount())
            ).send();

            StartupFunding.InvestmentMadeEventResponse investmentEvent =
                    contract.getInvestmentMadeEvents(receipt).get(0);

            return InvestmentResponse.builder()
                    .campaignId(campaignId)
                    .investorAddress(investorWallet.getAddress())
                    .amount(request.getAmount())
                    .totalRaised(investmentEvent.totalRaised.longValue())
                    .transactionHash(receipt.getTransactionHash())
                    .build();

        } catch (Exception e) {
            log.error("투자 실패 - userId: {}, campaignId: {}", request.getUserId(), campaignId, e);
            throw new BlockchainException("투자에 실패했습니다.", e);
        }
    }

    // StartupFundingService 내부 메소드
    //토큰 잔액 확인 → 현재 승인액 확인 -> (필요시) 승인 초기화 → 새 금액 승인 → 승인 결과 확인
    private boolean checkAndApproveTokens(Credentials credentials, Long amount) {
        try {
            // 토큰 컨트랙트 인스턴스 생성
            PickenToken tokenContract = PickenToken.load(
                    tokenContractAddress,
                    web3j,
                    credentials,
                    gasProvider
            );

            // 1. 토큰 잔액 확인
            BigInteger balance = tokenContract.balanceOf(credentials.getAddress()).send();
            if (balance.compareTo(BigInteger.valueOf(amount)) < 0) {
                throw new InsufficientTokenException("토큰 잔액이 부족합니다.");
            }

            // 2. 현재 승인된 금액 확인
            BigInteger allowance = tokenContract.allowance(
                    credentials.getAddress(),
                    fundingContractAddress
            ).send();

            // 3. 승인이 필요한 경우에만 승인 진행
            if (allowance.compareTo(BigInteger.valueOf(amount)) < 0) {
                // 이전 승인 금액이 있다면 0으로 초기화 (일부 토큰의 보안 요구사항)
                if (allowance.compareTo(BigInteger.ZERO) > 0) {
                    TransactionReceipt resetReceipt = tokenContract.approve(
                            fundingContractAddress,
                            BigInteger.ZERO
                    ).send();

                    if (!resetReceipt.isStatusOK()) {
                        log.error("토큰 승인 초기화 실패 - address: {}", credentials.getAddress());
                        return false;
                    }
                }

                // 새로운 금액으로 승인
                TransactionReceipt approvalReceipt = tokenContract.approve(
                        fundingContractAddress,
                        BigInteger.valueOf(amount)
                ).send();

                if (!approvalReceipt.isStatusOK()) {
                    log.error("토큰 승인 실패 - address: {}, amount: {}",
                            credentials.getAddress(), amount);
                    return false;
                }

                // 승인 후 allowance 다시 확인
                BigInteger newAllowance = tokenContract.allowance(
                        credentials.getAddress(),
                        fundingContractAddress
                ).send();

                if (newAllowance.compareTo(BigInteger.valueOf(amount)) < 0) {
                    log.error("토큰 승인 확인 실패 - address: {}, amount: {}, allowance: {}",
                            credentials.getAddress(), amount, newAllowance);
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            if (e instanceof InsufficientTokenException) {
                throw (InsufficientTokenException) e;
            }
            log.error("토큰 승인 처리 중 오류 발생", e);
            return false;
        }
    }

    public CampaignDetailResponse getCampaignDetails(Long campaignId) {
        try {
            // 읽기 전용 컨트랙트 인스턴스 생성
            StartupFunding contract = StartupFunding.load(
                    fundingContractAddress,
                    web3j,
                    Credentials.create("0x0"), // 읽기 전용이므로 더미 자격증명 사용
                    gasProvider
            );

            // 캠페인 상태 조회
            // Tuple3는 Web3j가 생성한 타입으로, 스마트 컨트랙트의 반환값을 담는 클래스
            Tuple3<BigInteger, BigInteger, BigInteger> balance = contract.getCampaignBalance(
                    BigInteger.valueOf(campaignId)
            ).send();


            return CampaignDetailResponse.builder()
                    .campaignId(campaignId)
                    .targetAmount(balance.component1().longValue())  // targetAmount
                    .currentBalance(balance.component2().longValue()) // currentBalance
                    .remainingAmount(balance.component3().longValue()) // remainingAmount
                    .build();

        } catch (Exception e) {
            log.error("캠페인 상세 조회 실패", e);
            throw new RuntimeException("캠페인 상세 조회에 실패했습니다.", e);
        }
    }

    public InvestorStatusResponse getInvestorStatus(Long campaignId, Long userId) {
        // 투자자 지갑 조회
        Wallet investorWallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("투자자 지갑을 찾을 수 없습니다."));

        try {
            // 읽기 전용 컨트랙트 인스턴스 생성
            StartupFunding contract = StartupFunding.load(
                    fundingContractAddress,
                    web3j,
                    Credentials.create("0x0"), // 읽기 전용이므로 더미 자격증명 사용
                    gasProvider
            );

            // 투자자 상태 조회
            Tuple3<BigInteger, BigInteger, BigInteger> status = contract.getInvestorStatus(
                    BigInteger.valueOf(campaignId),
                    investorWallet.getAddress()
            ).send();


            return InvestorStatusResponse.builder()
                    .campaignId(campaignId)
                    .investorAddress(investorWallet.getAddress())
                    .investedAmount(status.component1().longValue())
                    .campaignTotal(status.component2().longValue())
                    .sharePercentage(status.component3().longValue())
                    .build();

        } catch (Exception e) {
            log.error("투자자 상태 조회 실패", e);
            throw new RuntimeException("투자자 상태 조회에 실패했습니다.", e);
        }
    }

    public InvestmentAmountResponse getInvestmentAmount(Long campaignId, String address) {
        try {
            StartupFunding contract = StartupFunding.load(
                    fundingContractAddress,
                    web3j,
                    Credentials.create("0x0"),
                    gasProvider
            );

            BigInteger amount = contract.getInvestmentAmount(
                    BigInteger.valueOf(campaignId),
                    address
            ).send();

            return InvestmentAmountResponse.builder()
                    .campaignId(campaignId)
                    .investorAddress(address)
                    .amount(amount.longValue())
                    .build();

        } catch (Exception e) {
            log.error("투자 금액 조회 실패", e);
            throw new RuntimeException("투자 금액 조회에 실패했습니다.", e);
        }
    }

    public TokenBalanceResponse getTotalHeldTokens() {
        try {
            StartupFunding contract = StartupFunding.load(
                    fundingContractAddress,
                    web3j,
                    Credentials.create("0x0"),
                    gasProvider
            );

            BigInteger balance = contract.getTotalHeldTokens().send();

            return TokenBalanceResponse.builder()
                    .balance(balance.longValue())
                    .build();

        } catch (Exception e) {
            log.error("총 토큰 보유량 조회 실패", e);
            throw new RuntimeException("총 토큰 보유량 조회에 실패했습니다.", e);
        }
    }

    @Transactional
    public EmergencyWithdrawResponse emergencyWithdraw(
            Long campaignId,
            EmergencyWithdrawRequest request
    ) {
        // 관리자 지갑 조회
        Wallet adminWallet = walletRepository.findByUserId(request.getAdminUserId())
                .orElseThrow(() -> new RuntimeException("관리자 지갑을 찾을 수 없습니다."));

        try {
            StartupFunding contract = StartupFunding.load(
                    fundingContractAddress,
                    web3j,
                    adminCredentials,
                    gasProvider
            );

            TransactionReceipt receipt = contract.emergencyWithdraw(
                    BigInteger.valueOf(campaignId),
                    request.getToAddress()
            ).send();

            return EmergencyWithdrawResponse.builder()
                    .campaignId(campaignId)
                    .toAddress(request.getToAddress())
                    .transactionHash(receipt.getTransactionHash())
                    .build();

        } catch (Exception e) {
            log.error("긴급 출금 실패", e);
            throw new RuntimeException("긴급 출금에 실패했습니다.", e);
        }
    }
}

