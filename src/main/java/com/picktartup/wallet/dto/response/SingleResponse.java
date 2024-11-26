package com.picktartup.wallet.dto.response;

import com.picktartup.wallet.dto.response.CommonResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SingleResponse<T> extends CommonResponse {
    private T data;

}
