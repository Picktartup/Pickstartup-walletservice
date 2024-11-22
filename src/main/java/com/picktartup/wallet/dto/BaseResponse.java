package com.picktartup.wallet.dto;

import lombok.Builder;
import lombok.Getter;

// 공통 응답 형식
@Getter
@Builder
public class BaseResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private String errorCode;

    public static <T> BaseResponse<T> success(T data) {
        return BaseResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static <T> BaseResponse<T> error(String message, String errorCode) {
        return BaseResponse.<T>builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .build();
    }
}
