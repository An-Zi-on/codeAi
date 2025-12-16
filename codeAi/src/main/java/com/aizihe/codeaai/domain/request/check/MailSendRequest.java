package com.aizihe.codeaai.domain.request.check;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailSendRequest {

    /**
     * 发送的邮箱号
     */
    @NotBlank(message = "请输入要发送的邮箱号码")
    private String to;

    /**
     * 主题
     */
    private String subject;

    /**
     * 内容
     */
    private String content;

}