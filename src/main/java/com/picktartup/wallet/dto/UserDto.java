package com.picktartup.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationResponse {
        private Long id;
        private String username;
        private String status;  // "ACTIVE" or "INACTIVE"
    }
}
