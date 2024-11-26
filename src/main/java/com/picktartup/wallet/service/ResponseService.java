package com.picktartup.wallet.service;

import com.picktartup.wallet.dto.response.SingleResponse;
import com.picktartup.wallet.exception.ErrorCode;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class ResponseService {

   // 단일건 결과 처리 메서드 - 비동기
    public <T> Mono<SingleResponse<T>> getMonoSingleResult(Mono<T> data) {
        return data.map(result -> {
            SingleResponse<T> response = new SingleResponse<>();
            response.setSuccess(true);
            response.setCode(0);
            response.setMessage("성공하였습니다.");
            response.setData(result);
            return response;
        });
    }

    // 단일건 결과 처리 메서드 - 동기
    public <T> SingleResponse<T> getSingleResult(T data) {
        SingleResponse<T> response = new SingleResponse<>();
        response.setSuccess(true);
        response.setCode(0);
        response.setMessage("성공하였습니다.");
        response.setData(data);
        return response;
    }

    // 실패 결과 처리
    public <T> SingleResponse<T> getErrorResult(String code, String message) {
        SingleResponse<T> response = new SingleResponse<>();
        response.setSuccess(false);
        response.setCode(Integer.parseInt(code));
        response.setMessage(message);
        return response;
    }

    // BusinessException 처리
    public <T> SingleResponse<T> getErrorResult(ErrorCode errorCode) {
        return getErrorResult(errorCode.getCode(), errorCode.getMessage());
    }

}
