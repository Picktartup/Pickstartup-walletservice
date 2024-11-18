package com.picktartup.wallet.exception;

import org.springframework.validation.ObjectError;
import com.picktartup.wallet.dto.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

// Exception Handler
@RestControllerAdvice
@Slf4j
public class WalletExceptionHandler {

    @ExceptionHandler(WalletException.class)
    public ResponseEntity<BaseResponse<Void>> handleWalletException(WalletException e) {
        log.error("Wallet Exception: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(e.getMessage(), e.getErrorCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Void>> handleValidationException(
            MethodArgumentNotValidException e
    ) {
        log.error("Validation Exception: {}", e.getMessage(), e);
        String errorMessage = e.getBindingResult()
                .getAllErrors()  // getFieldErrors() 대신 getAllErrors() 사용
                .stream()
                .map(ObjectError::getDefaultMessage)  // FieldError 대신 ObjectError 사용
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(errorMessage, "VALIDATION_ERROR"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleException(Exception e) {
        log.error("Unexpected Exception: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("서버 오류가 발생했습니다.", "INTERNAL_SERVER_ERROR"));
    }
}
