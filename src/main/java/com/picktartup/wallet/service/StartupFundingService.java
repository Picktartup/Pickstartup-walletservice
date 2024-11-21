package com.picktartup.wallet.service;

import com.picktartup.wallet.contracts.PickenToken;
import com.picktartup.wallet.contracts.StartupFunding;
import com.picktartup.wallet.dto.CampaignDto;
import com.picktartup.wallet.dto.TokenDto;
import com.picktartup.wallet.entity.Wallet;
import com.picktartup.wallet.exception.BusinessException;
import com.picktartup.wallet.exception.ErrorCode;
import com.picktartup.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletFile;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
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

    @Transactional
    public CampaignDto.Create.Response createCampaign(CampaignDto.Create.Request request) {
        log.info("캠페인 생성 시작 - adminUserId: {}, name: {}", request.getAdminUserId(), request.getName());

        validateAdminAccess(request.getAdminUserId());

        try {
            StartupFunding contract = loadFundingContract(adminCredentials);

            TransactionReceipt receipt = contract.createCampaign(
                    request.getName(),
                    request.getDescription(),
                    request.getStartupWallet(),
                    BigInteger.valueOf(request.getTargetAmount())
            ).send();

            BigInteger campaignId = extractCampaignId(contract, receipt);

            log.info("캠페인 생성 완료 - campaignId: {}, txHash: {}",
                    campaignId, receipt.getTransactionHash());

            return CampaignDto.Create.Response.builder()
                    .campaignId(campaignId.longValue())
                    .name(request.getName())
                    .description(request.getDescription())
                    .targetAmount(request.getTargetAmount())
                    .transactionHash(receipt.getTransactionHash())
                    .build();

        } catch (Exception e) {
            log.error("캠페인 생성 실패 - adminUserId: {}", request.getAdminUserId(), e);
            throw new BusinessException(
                    ErrorCode.CAMPAIGN_CREATION_FAILED,
                    "캠페인 생성 중 오류가 발생했습니다: " + e.getMessage(),
                    e
            );
        }
    }

    @Transactional
    public CampaignDto.Investment.Response invest(Long campaignId, CampaignDto.Investment.Request request) {
        log.info("투자 시작 - userId: {}, campaignId: {}, amount: {}",
                request.getUserId(), campaignId, request.getAmount());

        Wallet investorWallet = findAndValidateInvestorWallet(request.getUserId());
        Credentials investorCredentials = loadInvestorCredentials(
                investorWallet,
                request.getWalletPassword()
        );

        try {
            validateAndApproveTokens(investorCredentials, request.getAmount());

            StartupFunding contract = loadFundingContract(investorCredentials);
            TransactionReceipt receipt = executeInvestment(
                    contract,
                    campaignId,
                    request.getAmount()
            );

            StartupFunding.InvestmentMadeEventResponse event =
                    contract.getInvestmentMadeEvents(receipt).get(0);

            log.info("투자 완료 - userId: {}, campaignId: {}, txHash: {}",
                    request.getUserId(), campaignId, receipt.getTransactionHash());

            return CampaignDto.Investment.Response.builder()
                    .campaignId(campaignId)
                    .investorAddress(investorWallet.getAddress())
                    .amount(request.getAmount())
                    .totalRaised(event.totalRaised.longValue())
                    .transactionHash(receipt.getTransactionHash())
                    .build();

        } catch (Exception e) {
            log.error("투자 실패 - userId: {}, campaignId: {}",
                    request.getUserId(), campaignId, e);
            throw new BusinessException(
                    ErrorCode.INVESTMENT_FAILED,
                    "투자 처리 중 오류가 발생했습니다: " + e.getMessage(),
                    e
            );
        }
    }

    public CampaignDto.Detail.Response getCampaignDetails(Long campaignId) {
        log.info("캠페인 상세 조회 시작 - campaignId: {}", campaignId);

        try {
            StartupFunding contract = loadFundingContract(createReadOnlyCredentials());

            Tuple3<BigInteger, BigInteger, BigInteger> balance =
                    contract.getCampaignBalance(BigInteger.valueOf(campaignId)).send();

            log.info("캠페인 상세 조회 완료 - campaignId: {}, currentBalance: {}",
                    campaignId, balance.component2());

            return CampaignDto.Detail.Response.builder()
                    .campaignId(campaignId)
                    .targetAmount(balance.component1().longValue())
                    .currentBalance(balance.component2().longValue())
                    .remainingAmount(balance.component3().longValue())
                    .build();

        } catch (Exception e) {
            log.error("캠페인 상세 조회 실패 - campaignId: {}", campaignId, e);
            throw new BusinessException(
                    ErrorCode.CAMPAIGN_DETAIL_FETCH_FAILED,
                    String.format("캠페인 상세 조회 중 오류 발생 - campaignId: %d", campaignId),
                    e
            );
        }
    }

    public CampaignDto.Investor.StatusResponse getInvestorStatus(Long campaignId, Long userId) {
        log.info("투자자 상태 조회 시작 - campaignId: {}, userId: {}", campaignId, userId);

        Wallet investorWallet = findAndValidateInvestorWallet(userId);

        try {
            StartupFunding contract = loadFundingContract(createReadOnlyCredentials());

            Tuple3<BigInteger, BigInteger, BigInteger> status =
                    contract.getInvestorStatus(
                            BigInteger.valueOf(campaignId),
                            investorWallet.getAddress()
                    ).send();

            log.info("투자자 상태 조회 완료 - campaignId: {}, userId: {}, investedAmount: {}",
                    campaignId, userId, status.component1());

            return CampaignDto.Investor.StatusResponse.builder()
                    .campaignId(campaignId)
                    .investorAddress(investorWallet.getAddress())
                    .investedAmount(status.component1().longValue())
                    .campaignTotal(status.component2().longValue())
                    .sharePercentage(status.component3().longValue())
                    .build();

        } catch (Exception e) {
            log.error("투자자 상태 조회 실패 - campaignId: {}, userId: {}", campaignId, userId, e);
            throw new BusinessException(
                    ErrorCode.INVESTOR_STATUS_FETCH_FAILED,
                    String.format("투자자 상태 조회 중 오류 발생 - campaignId: %d, userId: %d",
                            campaignId, userId),
                    e
            );
        }
    }

    public CampaignDto.Investment.AmountResponse getInvestmentAmount(Long campaignId, String address) {
        log.info("투자 금액 조회 시작 - campaignId: {}, address: {}", campaignId, address);

        try {
            StartupFunding contract = loadFundingContract(createReadOnlyCredentials());

            BigInteger amount = contract.getInvestmentAmount(
                    BigInteger.valueOf(campaignId),
                    address
            ).send();

            log.info("투자 금액 조회 완료 - campaignId: {}, address: {}, amount: {}",
                    campaignId, address, amount);

            return CampaignDto.Investment.AmountResponse.builder()
                    .campaignId(campaignId)
                    .investorAddress(address)
                    .amount(amount.longValue())
                    .build();

        } catch (Exception e) {
            log.error("투자 금액 조회 실패 - campaignId: {}, address: {}", campaignId, address, e);
            throw new BusinessException(
                    ErrorCode.BALANCE_CHECK_FAILED,
                    String.format("투자 금액 조회 중 오류 발생 - campaignId: %d, address: %s",
                            campaignId, address),
                    e
            );
        }
    }

    public TokenDto.Balance.Response getTotalHeldTokens() {
        log.info("총 보유 토큰 조회 시작");

        try {
            StartupFunding contract = loadFundingContract(createReadOnlyCredentials());
            BigInteger balance = contract.getTotalHeldTokens().send();

            // BigInteger를 BigDecimal로 변환 (Wei to Ether)
            BigDecimal balanceInEther = Convert.fromWei(new BigDecimal(balance), Convert.Unit.ETHER);

            log.info("총 보유 토큰 조회 완료 - balance: {}", balance);

            return TokenDto.Balance.Response.builder()
                    .address(fundingContractAddress)
                    .balance(balanceInEther)
                    .build();

        } catch (Exception e) {
            log.error("총 보유 토큰 조회 실패", e);
            throw new BusinessException(
                    ErrorCode.BALANCE_CHECK_FAILED,
                    "총 보유 토큰 조회 중 오류 발생",
                    e
            );
        }
    }

    @Transactional
    public CampaignDto.Emergency.Response emergencyWithdraw(
            Long campaignId,
            CampaignDto.Emergency.Request request
    ) {
        log.info("긴급 출금 시작 - campaignId: {}, adminUserId: {}",
                campaignId, request.getAdminUserId());

        validateAdminAccess(request.getAdminUserId());

        try {
            StartupFunding contract = loadFundingContract(adminCredentials);

            TransactionReceipt receipt = contract.emergencyWithdraw(
                    BigInteger.valueOf(campaignId),
                    request.getToAddress()
            ).send();

            log.info("긴급 출금 완료 - campaignId: {}, toAddress: {}, txHash: {}",
                    campaignId, request.getToAddress(), receipt.getTransactionHash());

            return CampaignDto.Emergency.Response.builder()
                    .campaignId(campaignId)
                    .toAddress(request.getToAddress())
                    .transactionHash(receipt.getTransactionHash())
                    .build();

        } catch (Exception e) {
            log.error("긴급 출금 실패 - campaignId: {}", campaignId, e);
            throw new BusinessException(
                    ErrorCode.EMERGENCY_WITHDRAW_FAILED,
                    String.format("긴급 출금 중 오류 발생 - campaignId: %d", campaignId),
                    e
            );
        }
    }

    // Private helper methods
    private StartupFunding loadFundingContract(Credentials credentials) {
        return StartupFunding.load(
                fundingContractAddress,
                web3j,
                credentials,
                gasProvider
        );
    }

    private Wallet findAndValidateInvestorWallet(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.WALLET_NOT_FOUND));
    }

    private Credentials loadInvestorCredentials(Wallet wallet, String password) {
        try {
            WalletFile walletFile = keystoreService.getWalletFile(wallet.getKeystoreFilename());
            String privateKey = keystoreService.decryptPrivateKey(walletFile, password);
            return Credentials.create(privateKey);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("자격 증명 로드 실패 - walletAddress: {}", wallet.getAddress(), e);
            throw new BusinessException(
                    ErrorCode.CONTRACT_INTERACTION_FAILED,
                    "자격 증명 로드 중 오류가 발생했습니다: " + e.getMessage(),
                    e
            );
        }
    }

    private void validateAndApproveTokens(Credentials credentials, Long amount) {
        if (!checkAndApproveTokens(credentials, amount)) {
            throw new BusinessException(ErrorCode.TOKEN_APPROVAL_FAILED);
        }
    }

    private TransactionReceipt executeInvestment(
            StartupFunding contract,
            Long campaignId,
            Long amount) throws Exception {
        return contract.invest(
                BigInteger.valueOf(campaignId),
                BigInteger.valueOf(amount)
        ).send();
    }

    private BigInteger extractCampaignId(StartupFunding contract, TransactionReceipt receipt) {
        return contract.getCampaignCreatedEvents(receipt)
                .stream()
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.CAMPAIGN_CREATION_FAILED))
                .campaignId;
    }

    private Credentials createReadOnlyCredentials() {
        return Credentials.create("0x0");
    }

    private void validateAdminAccess(Long adminUserId) {
        Wallet adminWallet = walletRepository.findByUserId(adminUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));

        if (!isAdmin(adminUserId)) {
            throw new BusinessException(
                    ErrorCode.UNAUTHORIZED_ACCESS,
                    "관리자 권한이 없습니다."
            );
        }
    }

    private boolean isAdmin(Long userId) {
        // TODO: 실제 관리자 권한 확인 로직 구현 필요
        return true;
    }

    private boolean checkAndApproveTokens(Credentials credentials, Long amount) {
        try {
            PickenToken tokenContract = loadTokenContract(credentials);

            // 1. 토큰 잔액 검증
            BigInteger balance = tokenContract.balanceOf(credentials.getAddress()).send();
            if (balance.compareTo(BigInteger.valueOf(amount)) < 0) {
                throw new BusinessException(ErrorCode.INSUFFICIENT_TOKEN_BALANCE);
            }

            // 2. 현재 승인액 확인
            BigInteger allowance = tokenContract.allowance(
                    credentials.getAddress(),
                    fundingContractAddress
            ).send();

            // 3. 필요한 경우에만 승인 진행
            if (allowance.compareTo(BigInteger.valueOf(amount)) < 0) {
                return handleTokenApproval(tokenContract, allowance, amount, credentials);
            }

            return true;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("토큰 승인 처리 중 오류 발생", e);
            throw new BusinessException(
                    ErrorCode.TOKEN_APPROVAL_FAILED,
                    "토큰 승인 처리 중 오류가 발생했습니다.",
                    e
            );
        }
    }

    private boolean handleTokenApproval(
            PickenToken tokenContract,
            BigInteger currentAllowance,
            Long requiredAmount,
            Credentials credentials
    ) throws Exception {
        // 기존 승인액 초기화
        if (currentAllowance.compareTo(BigInteger.ZERO) > 0) {
            TransactionReceipt resetReceipt = tokenContract.approve(
                    fundingContractAddress,
                    BigInteger.ZERO
            ).send();

            if (!resetReceipt.isStatusOK()) {
                throw new BusinessException(
                        ErrorCode.TOKEN_APPROVAL_FAILED,
                        "토큰 승인 초기화 실패"
                );
            }
        }

        // 새로운 승인
        TransactionReceipt approvalReceipt = tokenContract.approve(
                fundingContractAddress,
                BigInteger.valueOf(requiredAmount)
        ).send();

        if (!approvalReceipt.isStatusOK()) {
            throw new BusinessException(
                    ErrorCode.TOKEN_APPROVAL_FAILED,
                    "토큰 승인 실패"
            );
        }

        // 승인 결과 확인
        BigInteger newAllowance = tokenContract.allowance(
                credentials.getAddress(),
                fundingContractAddress
        ).send();

        if (newAllowance.compareTo(BigInteger.valueOf(requiredAmount)) < 0) {
            throw new BusinessException(
                    ErrorCode.TOKEN_APPROVAL_FAILED,
                    "토큰 승인 확인 실패"
            );
        }

        return true;
    }

    private PickenToken loadTokenContract(Credentials credentials) {
        return PickenToken.load(
                tokenContractAddress,
                web3j,
                credentials,
                gasProvider
        );
    }
}