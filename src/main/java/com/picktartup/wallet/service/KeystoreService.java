package com.picktartup.wallet.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.picktartup.wallet.exception.KeystoreException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.exception.CipherException;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeystoreService {


    public WalletFile getWalletFile(String keyStoreFileName) throws IOException, CipherException {
        // 홈 디렉토리 기준 절대 경로 생성
        String homeDir = System.getProperty("user.home");
        File keystoreDir = new File(homeDir, "keystore");
        File keystoreFile = new File(keystoreDir, keyStoreFileName);

        log.info("찾고있는 Keystore 파일 경로: {}", keystoreFile.getAbsolutePath());


        if (!keystoreFile.exists()) {
            throw new KeystoreException("Keystore 파일을 찾을 수 없습니다: " + keyStoreFileName);
        }

        // WalletUtils를 사용하여 WalletFile 객체 생성
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(keystoreFile, WalletFile.class);
        } catch (Exception e) {
            log.error("Keystore 파일 읽기 실패: {}", keyStoreFileName, e);
            throw new KeystoreException("Keystore 파일을 읽을 수 없습니다: " + keyStoreFileName);
        }
    }

    public String decryptPrivateKey(WalletFile walletFile, String password) throws CipherException {
        try {
            // WalletUtils를 사용하여 Credentials 생성
            Credentials credentials = Credentials.create(Wallet.decrypt(password, walletFile));
            return credentials.getEcKeyPair().getPrivateKey().toString(16);
        } catch (CipherException e) {
            log.error("Private key 복호화 실패", e);
            throw new KeystoreException("Private key 복호화에 실패했습니다.");
        }
    }
}

