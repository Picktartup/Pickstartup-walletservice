package com.picktartup.wallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

public class CampaignDto {

    public static class Create {
        @Getter
        @Builder
        public static class Request {
            @NotNull
            private String name;
            @NotNull
            private String description;
            @NotNull
            private String startupWallet;
            @Positive
            private Long targetAmount;
            @NotNull
            private Long adminUserId;
        }

        @Getter
        @Builder
        public static class Response {
            private Long campaignId;
            private String name;
            private String description;
            private Long targetAmount;
            private String transactionHash;
        }
    }

    public static class Investment {
        @Getter
        @Builder
        public static class Request {
            @NotNull(message = "사용자 ID는 필수입니다")
            private Long userId;

            @NotBlank(message = "지갑 비밀번호는 필수입니다")
            private String walletPassword;

            @Positive(message = "투자 금액은 0보다 커야 합니다")
            private Long amount;
        }

        @Getter
        @Builder
        public static class Response {
            private Long campaignId;
            private String investorAddress;
            private Long amount;
            private Long totalRaised;
            private String transactionHash;
        }

        @Getter
        @Builder
        public static class AmountResponse {
            private Long campaignId;
            private String investorAddress;
            private Long amount;
        }
    }

    public static class Investor {
        @Getter
        @Builder
        public static class StatusResponse {  // 기존 InvestorStatusResponse를 대체
            private Long campaignId;
            private String investorAddress;
            private Long investedAmount;
            private Long campaignTotal;
            private Long sharePercentage;
        }
    }

    public static class Detail {
        @Getter
        @Builder
        public static class Response {
            private Long campaignId;
            private Long targetAmount;
            private Long currentBalance;
            private Long remainingAmount;
        }
    }

    // Emergency Withdraw
    public static class Emergency {

        @Getter
        @Builder
        public static class Request {
            @NotNull(message = "관리자 ID는 필수입니다")
            private Long adminUserId;

            @NotNull(message = "출금 주소는 필수입니다")
            private String toAddress;
        }

        @Getter
        @Builder
        public static class Response {
            private Long campaignId;
            private String toAddress;
            private String transactionHash;
        }
    }
}
