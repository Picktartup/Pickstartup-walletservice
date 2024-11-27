package com.picktartup.wallet.webclient;

import com.picktartup.wallet.dto.UserDto;
import com.picktartup.wallet.dto.response.BaseResponse;
import com.picktartup.wallet.exception.BusinessException;
import com.picktartup.wallet.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserServiceClient {
    private final WebClient userServiceWebClient;

    public void validateUserExists(Long userId) {
        try {
            BaseResponse<UserDto.ValidationResponse> response = userServiceWebClient.get()
                    .uri("/api/v1/users/" + userId + "/validation")
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), clientResponse -> {
                        if (clientResponse.statusCode() == HttpStatus.NOT_FOUND) {
                            return Mono.error(new BusinessException(ErrorCode.USER_NOT_FOUND));
                        }
                        return Mono.error(new BusinessException(ErrorCode.USER_SERVICE_ERROR));
                    })
                    .onStatus(status -> status.is5xxServerError(), clientResponse ->
                            Mono.error(new BusinessException(ErrorCode.USER_SERVICE_ERROR)))
                    .bodyToMono(new ParameterizedTypeReference<BaseResponse<UserDto.ValidationResponse>>() {})
                    .block();

            if (response != null && response.getData() != null
                    && !"ACTIVE".equals(response.getData().getStatus())) {
                throw new BusinessException(ErrorCode.USER_NOT_ACTIVE);
            }
        } catch (WebClientResponseException e) {
            log.error("User service error: {}", e.getMessage());
            throw new BusinessException(ErrorCode.USER_SERVICE_ERROR);
        } catch (Exception e) {
            log.error("Unexpected error while validating user: {}", e.getMessage());
            throw new BusinessException(ErrorCode.USER_SERVICE_ERROR);
        }
    }

    public void validateAdminUser(Long userId) {
        try {
            BaseResponse<UserDto.ValidationAdminResponse> response = userServiceWebClient.get()
                    .uri("/api/v1/users/" + userId + "/validation")
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), clientResponse -> {
                        if (clientResponse.statusCode() == HttpStatus.NOT_FOUND) {
                            return Mono.error(new BusinessException(ErrorCode.USER_NOT_FOUND));
                        }
                        return Mono.error(new BusinessException(ErrorCode.USER_SERVICE_ERROR));
                    })
                    .onStatus(status -> status.is5xxServerError(), clientResponse ->
                            Mono.error(new BusinessException(ErrorCode.USER_SERVICE_ERROR)))
                    .bodyToMono(new ParameterizedTypeReference<BaseResponse<UserDto.ValidationAdminResponse>>() {})
                    .block();

            if (response == null || response.getData() == null) {
                throw new BusinessException(ErrorCode.USER_SERVICE_ERROR);
            }

            UserDto.ValidationAdminResponse userInfo = response.getData();

            // 관리자 권한 체크
            if (!"ADMIN".equals(userInfo.getRole())) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
            }
        } catch (WebClientResponseException e) {
            log.error("User service error: {}", e.getMessage());
            throw new BusinessException(ErrorCode.USER_SERVICE_ERROR);
        }
    }
}