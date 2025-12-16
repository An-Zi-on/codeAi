package com.aizihe.codeaai.component.mail;

import com.aizihe.codeaai.domain.request.check.MailSendRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 邮件服务
 *
 * @author melo
 * @date 2021/9/14 16:39
 */
@Component
@Slf4j
public class MailSendComponent {

    @Autowired
    private Map<String, MailComponent> mailSendComponentMap;

    /**
     * 发送邮件
     *
     * @param mailSendRequest 发送邮件入参
     */
    public void mailSend(MailSendRequest mailSendRequest) {
        MailComponent springBootMailComponent = mailSendComponentMap.get("springBootMailComponent");
        springBootMailComponent.sendMail(mailSendRequest);
    }
}
