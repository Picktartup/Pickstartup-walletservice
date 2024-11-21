package com.picktartup.wallet.dto;

import com.picktartup.wallet.entity.WalletStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WalletDto {

    public static class Create {

        @Getter
        @Builder
        public static class Request {
            @NotNull(message = "사용자 ID는 필수입니다.")
            private Long userId;
        }

        @Getter
        @Builder
        public static class Response {
            private Long userId;
            private String address;
            private String keystoreFilename;
            private String temporaryPassword;
            private BigDecimal balance;
            private WalletStatus status;
            private LocalDateTime createdAt;
            private LocalDateTime updatedAt;
        }
    }

    public static class Balance {
        @Getter
        @Builder
        public static class Response {
            private String address;
            private BigDecimal balance;
            private LocalDateTime lastChecked;
        }
    }

    public static class UpdateStatus {  // 기존 UpdateWalletStatusRequest를 여기로 통합
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Request {
            @NotNull(message = "상태값은 필수입니다")
            private WalletStatus status;
        }

    }

    public static class Keystore {
        @Getter
        @Builder
        public static class Request {
            @NotBlank(message = "키스토어 파일명은 필수입니다")
            private String keystoreFileName;
            private String password;
        }

        @Getter
        @Builder
        public static class Response {
            private String address;
            private String privateKey;
        }
    }
}
