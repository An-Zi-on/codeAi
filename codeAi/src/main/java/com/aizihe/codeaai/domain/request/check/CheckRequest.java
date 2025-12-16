package com.aizihe.codeaai.domain.request.check;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @param
 * @return
 */
@Data
public class CheckRequest {
    @NotBlank(message = "联系方式")
    private String contact;

    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String code;

}
