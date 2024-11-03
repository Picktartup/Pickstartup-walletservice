package com.picktartup.wallet.service;

import com.picktartup.wallet.entity.Wallet;
import com.picktartup.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

@Service
public class WalletService {

    private final Web3j web3j;
    private final WalletRepository walletRepository;

    @Value("${bsc.network.url}")
    private String bscNetworkUrl;

    @Autowired
    public WalletService(WalletRepository walletRepository, @Value("${bsc.network.url}") String bscNetworkUrl) {
        this.walletRepository = walletRepository;
        this.web3j = Web3j.build(new HttpService(bscNetworkUrl));
    }

    /**
     * 새 지갑을 생성하고 지정된 경로에 저장한 후, 지갑 주소를 DB에 저장하고 반환합니다.
     * @param password 지갑 비밀번호
     * @param walletDirectory 지갑 파일을 저장할 사용자 지정 디렉토리
     * @return 지갑 주소
     * @throws Exception 지갑 생성 오류
     */
    public String createWallet(String password, String walletDirectory) throws Exception {
        // 지갑 파일 생성 및 주소 얻기
        String walletFileName = WalletUtils.generateFullNewWalletFile(password, new File(walletDirectory));
        Credentials credentials = WalletUtils.loadCredentials(password, walletDirectory + "/" + walletFileName);
        String walletAddress = credentials.getAddress();

        // 지갑 주소를 DB에 저장
        Wallet wallet = Wallet.builder()
                .address(walletAddress)
                .balance(0.0)
                .build();

        walletRepository.save(wallet);

        return walletAddress;
    }

    /**
     * 지갑 주소의 BNB 잔액을 조회합니다.
     * @param address 지갑 주소
     * @return 잔액 (BNB)
     * @throws Exception 잔액 조회 오류
     */
    public BigDecimal getWalletBalance(String address) throws Exception {
        EthGetBalance ethGetBalance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
        BigInteger weiBalance = ethGetBalance.getBalance();
        return Convert.fromWei(weiBalance.toString(), Convert.Unit.ETHER);
    }

    /**
     * 지갑 주소를 기반으로 지갑을 삭제합니다.
     * @param address 삭제할 지갑의 주소
     * @return 삭제 여부 (true: 삭제 성공, false: 지갑이 존재하지 않음)
     */
    public boolean deleteWallet(String address) {
        Optional<Wallet> wallet = walletRepository.findByAddress(address);

        if (wallet.isPresent()) {
            walletRepository.deleteByAddress(address);
            return true; // 삭제 성공
        } else {
            return false; // 지갑이 존재하지 않음
        }
    }
}
