package com.picktartup.wallet.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class CampaignDto {

    public static class Create {
        @Getter
        @Builder
        public static class Request {
            @NotNull(message = "캠페인 이름은 필수입니다")
            private String name;

            @NotNull(message = "캠페인 설명은 필수입니다")
            private String description;

            @NotNull(message = "스타트업 지갑 주소는 필수입니다")
            @Pattern(regexp = "^0x[a-fA-F0-9]{40}$", message = "올바른 이더리움 주소 형식이 아닙니다")
            private String startupWallet;

            @Positive(message = "목표 금액은 0보다 커야 합니다")
            private Long targetAmount;

            @NotNull(message = "관리자 ID는 필수입니다")
            private Long adminUserId;

            @Positive(message = "캠페인 기간은 0보다 커야 합니다")
            @Max(value = 365, message = "캠페인 기간은 최대 365일입니다")
            private Integer durationInDays;
        }

        @Getter
        @Builder
        public static class Response {
            private Long campaignId;
            private String name;
            private String description;
            private String startupWallet;
            private Long targetAmount;
            private LocalDateTime startTime;
            private LocalDateTime endTime;
            private String transactionHash;
        }
    }

    public static class Status {
        @Getter
        @Builder
        public static class Response {
            private Long campaignId;
            private CampaignStatus status;
            private LocalDateTime startTime;
            private LocalDateTime endTime;
            private String timeRemaining;
        }
    }

    public enum CampaignStatus {
        ACTIVE("진행중"),
        SUCCESSFUL("성공"),
        FAILED("실패"),
        CANCELLED("취소됨");

        private final String description;

        CampaignStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
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
            private LocalDateTime investedAt;  // 추가
        }
    }

    public static class Investor {
        @Getter
        @Builder
        public static class StatusResponse {
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

    public static class Refund {  // 추가: 환불 관련 DTO
        @Getter
        @Builder
        public static class Request {
            @NotNull(message = "사용자 ID는 필수입니다")
            private Long userId;
        }

        @Getter
        @Builder
        public static class Response {
            private Long campaignId;
            private String investorAddress;
            private Long amount;
            private String transactionHash;
            private LocalDateTime refundedAt;
        }
    }

    public static class Emergency {
        @Getter
        @Builder
        public static class Request {
            @NotNull(message = "관리자 ID는 필수입니다")
            private Long adminUserId;

            @NotNull(message = "출금 주소는 필수입니다")
            @Pattern(regexp = "^0x[a-fA-F0-9]{40}$", message = "올바른 이더리움 주소 형식이 아닙니다")
            private String toAddress;
        }

        @Getter
        @Builder
        public static class Response {
            private Long campaignId;
            private String toAddress;
            private Long amount;  // 추가: 출금 금액
            private String transactionHash;
            private LocalDateTime withdrawnAt;  // 추가: 출금 시간
        }
    }
}