package com.picktartup.wallet.service;

import com.picktartup.wallet.dto.TokenDto;
import com.picktartup.wallet.dto.WalletDto;
import com.picktartup.wallet.entity.Wallet;
import com.picktartup.wallet.entity.WalletStatus;
import com.picktartup.wallet.exception.*;
import com.picktartup.wallet.repository.WalletRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * WalletService - 사용자 지갑 관리 서비스
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WalletService {

    private final Web3j web3j;
    private final WalletRepository walletRepository;

    @Value("${wallet.keystore.directory}")
    private String keystoreDirectory;

    @PostConstruct
    public void init() {
        File directory = new File(keystoreDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
            // 디렉토리 권한 설정 (Linux/Unix)
            directory.setReadable(true, true);    // 소유자만 읽기 가능
            directory.setWritable(true, true);    // 소유자만 쓰기 가능
            directory.setExecutable(true, true);  // 소유자만 실행 가능
        }
    }

    @Transactional
    public WalletDto.Create.Response createWallet(WalletDto.Create.Request request) {
        try {
            // 1. 동일한 userId로 이미 지갑이 존재하는지 확인
            Optional<Wallet> existingWallet = walletRepository.findByUserId(request.getUserId());
            if (existingWallet.isPresent()) {
                throw new BusinessException(ErrorCode.WALLET_ALREADY_EXISTS);
            }

            // 2. 랜덤 비밀번호 생성 (또는 사용자로부터 받기)
            String password = generateSecurePassword();

            // 3. 키스토어 파일 생성
            String walletFileName = WalletUtils.generateFullNewWalletFile(
                    password, // 이 비밀번호로 키스토어 파일 암호화
                    new File(keystoreDirectory)
            );

            // 4. 생성된 지갑 정보 로드
            Credentials credentials = WalletUtils.loadCredentials(
                    password,
                    keystoreDirectory + File.separator + walletFileName
            );

            // 5. DB에 지갑 정보 저장
            Wallet wallet = walletRepository.save(Wallet.builder()
                    .userId(request.getUserId())
                    .address(credentials.getAddress())
                    .keystoreFilename(walletFileName)  // 키스토어 파일명 저장
                    .balance(BigDecimal.ZERO)
                    .status(WalletStatus.ACTIVE)
                    .build());

            // 6. 응답 생성 (키스토어 파일명과 임시 비밀번호 반환)
            return WalletDto.Create.Response.builder()
                    .userId(wallet.getUserId())
                    .address(wallet.getAddress())
                    .keystoreFilename(wallet.getKeystoreFilename())
                    .temporaryPassword(password)
                    .balance(wallet.getBalance())
                    .status(wallet.getStatus())
                    .createdAt(wallet.getCreatedAt())
                    .updatedAt(wallet.getUpdatedAt())
                    .build();

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("지갑 생성 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.WALLET_CREATION_FAILED, e.getMessage());
        }
    }

    // 안전한 랜덤 비밀번호 생성
    private String generateSecurePassword() {
        return UUID.randomUUID().toString();
    }


    // 사용자 ID로 지갑 조회
    public WalletDto.Create.Response getWalletByUserId(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.WALLET_NOT_FOUND));

        return WalletDto.Create.Response.builder()
                .userId(wallet.getUserId())
                .address(wallet.getAddress())
                .balance(wallet.getBalance())
                .status(wallet.getStatus())
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getUpdatedAt())
                .build();
    }

    // DB에 저장된 지갑 잔고 조회
    public TokenDto.Balance.Response getBalanceFromDB(String address) {
        Wallet wallet = walletRepository.findByAddress(address)
                .orElseThrow(() -> new BusinessException(ErrorCode.WALLET_NOT_FOUND));
        return TokenDto.Balance.Response.builder()
                .address(address)
                .balance(wallet.getBalance())
                .build();
    }

    // 지갑 상태 업데이트 (DB에만 상태 변경)
    @Transactional
    public WalletDto.Create.Response updateWalletStatus(Long walletId, WalletDto.UpdateStatus.Request request) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new BusinessException(ErrorCode.WALLET_NOT_FOUND));

        wallet.setStatus(request.getStatus());
        walletRepository.save(wallet);

        return WalletDto.Create.Response.builder()
                .userId(wallet.getUserId())
                .address(wallet.getAddress())
                .balance(wallet.getBalance())
                .status(wallet.getStatus())
                .build();
    }

    // 잔고 업데이트 (네트워크에서 최신 잔고 조회 후 DB에 저장)
    @Transactional
    public void updateBalance(String address) {
        try {
            BigInteger balance = getTokenBalance(address);
            Wallet wallet = walletRepository.findByAddress(address)
                    .orElseThrow(() -> new BusinessException(ErrorCode.WALLET_NOT_FOUND));
            wallet.updateBalance(new BigDecimal(balance));

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("잔고 업데이트 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.BALANCE_UPDATE_FAILED, e.getMessage());
        }
    }

    // 블록체인 네트워크에서 토큰 잔고 조회
    public BigInteger getTokenBalance(String address) throws Exception {
        String contractAddress = "0xYourTokenContractAddress";

        Function function = new Function(
                "balanceOf",
                Arrays.asList(new Address(address)),
                Arrays.asList(new TypeReference<Uint256>() {})
        );

        String encodedFunction = FunctionEncoder.encode(function);

        EthCall response = web3j.ethCall(
                Transaction.createEthCallTransaction(
                        null,
                        contractAddress,
                        encodedFunction
                ),
                DefaultBlockParameterName.LATEST
        ).send();

        List<Type> decoded = FunctionReturnDecoder.decode(
                response.getValue(),
                function.getOutputParameters()
        );

        return decoded.isEmpty() ? BigInteger.ZERO : (BigInteger) decoded.get(0).getValue();
    }

    // 지갑 삭제
    @Transactional
    public boolean deleteWallet(String address) {
        Optional<Wallet> wallet = walletRepository.findByAddress(address);

        if (wallet.isPresent()) {
            walletRepository.delete(wallet.get());
            return true;
        } else {
            return false;
        }
    }
}
