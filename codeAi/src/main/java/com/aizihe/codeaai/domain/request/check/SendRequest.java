package com.aizihe.codeaai.domain.request.check;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @param
 * @return
 */ @Data
public class SendRequest {
     @NotBlank(message = "消息不允许为空")
    private  String contact;
}
