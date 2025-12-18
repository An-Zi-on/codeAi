package com.aizihe.codeaai.domain.request.check;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class CaptchaVerifyRequest {
    @NotBlank(message = "ticket 不能为空")
    private String ticket;

    @NotBlank(message = "randstr 不能为空")
    private String randstr;
}