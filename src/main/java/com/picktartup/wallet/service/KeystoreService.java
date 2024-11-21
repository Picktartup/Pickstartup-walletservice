package com.picktartup.wallet.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.picktartup.wallet.exception.BusinessException;
import com.picktartup.wallet.exception.ErrorCode;
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

    private final ObjectMapper objectMapper;

    public WalletFile getWalletFile(String keystoreFileName) {
        File keystoreFile = resolveKeystoreFile(keystoreFileName);
        validateKeystoreFileExists(keystoreFile, keystoreFileName);
        return readWalletFile(keystoreFile, keystoreFileName);
    }

    public String decryptPrivateKey(WalletFile walletFile, String password) {
        try {
            Credentials credentials = Credentials.create(Wallet.decrypt(password, walletFile));
            return credentials.getEcKeyPair().getPrivateKey().toString(16);
        } catch (CipherException e) {
            log.error("Private key 복호화 실패 - 원인: {}", e.getMessage());

            // 비밀번호 오류와 기타 오류 구분
            if (e.getMessage().contains("Invalid password")) {
                throw new BusinessException(
                        ErrorCode.INVALID_PASSWORD,
                        "입력한 비밀번호가 올바르지 않습니다."
                );
            }

            throw new BusinessException(
                    ErrorCode.PRIVATE_KEY_DECRYPT_FAILED,
                    "Private key 복호화 중 오류가 발생했습니다.",
                    e
            );
        }
    }

    // Private helper methods
    private File resolveKeystoreFile(String keystoreFileName) {
        String homeDir = System.getProperty("user.home");
        File keystoreDir = new File(homeDir, "keystore");
        File keystoreFile = new File(keystoreDir, keystoreFileName);
        log.debug("Keystore 파일 경로: {}", keystoreFile.getAbsolutePath());
        return keystoreFile;
    }

    private void validateKeystoreFileExists(File keystoreFile, String fileName) {
        if (!keystoreFile.exists()) {
            log.error("Keystore 파일 없음: {}", fileName);
            throw new BusinessException(
                    ErrorCode.KEYSTORE_FILE_NOT_FOUND,
                    String.format("Keystore 파일을 찾을 수 없습니다: %s", fileName)
            );
        }
    }

    private WalletFile readWalletFile(File keystoreFile, String fileName) {
        try {
            return objectMapper.readValue(keystoreFile, WalletFile.class);
        } catch (IOException e) {
            log.error("Keystore 파일 읽기 실패: {}", fileName, e);
            throw new BusinessException(
                    ErrorCode.KEYSTORE_READ_FAILED,
                    String.format("Keystore 파일 읽기에 실패했습니다: %s", fileName),
                    e
            );
        } catch (Exception e) {
            log.error("잘못된 Keystore 파일 형식: {}", fileName, e);
            throw new BusinessException(
                    ErrorCode.INVALID_KEYSTORE_FORMAT,
                    String.format("잘못된 Keystore 파일 형식입니다: %s", fileName),
                    e
            );
        }
    }

}

