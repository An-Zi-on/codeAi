package com.aizihe.codeaai.domain.request.check;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 校验短信验证码请求参数
 *
 * @author 93564 / gavyn
 */
@Data
public class SmsCheckRequest {

    /**
     * 手机号（纯数字，如：13812345678）
     */
    @NotBlank(message = "手机号不能为空")
    private String userPhone;
    /**
     * 用户输入的短信验证码（通常为 4~6 位数字）
     */
    @NotBlank(message = "验证码不能为空")
    private String code;
}


