package com.aizihe.codeaai.domain.request.check;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


/**
 * 发送短信验证码请求参数
 *
 * @author 93564 / gavyn
 */
@Data
public class SmsSendRequest {

    /**
     * 手机号（纯数字，如：13812345678）
     */
    @NotBlank(message = "手机号不能为空")
    private String userPhone;
}