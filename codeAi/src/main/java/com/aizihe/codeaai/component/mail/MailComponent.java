package com.aizihe.codeaai.component.mail;

import com.cxssboot.domain.request.MailSendRequest;

/**
 * 邮箱发送组件
 */
public interface MailComponent {

    /**
     * 发送邮件
     *
     * @param mailSendRequest 发送邮件入参
     */
    void sendMail(MailSendRequest mailSendRequest);

}
