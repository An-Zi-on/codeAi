package com.aizihe.codeaai.component.mail;

import com.aizihe.codeaai.domain.request.check.MailSendRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 邮件发送组件（简化版本，仅日志输出）
 */
@Component(value = "springBootMailComponent")
@Slf4j
public class SpringBootMailComponent implements MailComponent {

    /**
     * 发件人
     */
    @Value("${spring.mail.from:no-reply@example.com}")
    private String from;

    /**
     * 发送邮件
     *
     * @param mailSendRequest 发送邮件入参
     */
    @Override
    public void sendMail(MailSendRequest mailSendRequest) {
        // 这里先不接入真实邮件服务，避免引入额外依赖导致编译错误
        log.info("【模拟发送邮件】from={}, to={}, subject={}, contentLength={}",
                from,
                mailSendRequest.getTo(),
                mailSendRequest.getSubject(),
                mailSendRequest.getContent() == null ? 0 : mailSendRequest.getContent().length());
    }
}
