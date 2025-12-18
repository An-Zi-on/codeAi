package com.aizihe.codeaai.domain.request.check;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


/**
 * @param
 * @return
 */ @Data
public class SendRequest {
     @NotBlank(message = "消息不允许为空")
    private  String contact;
}
