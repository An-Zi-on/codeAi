package com.aizihe.codeaai.component.mail;


import com.aizihe.codeaai.domain.request.check.MailSendRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;




@Component(value = "springBootMailComponent")
@Slf4j
public class SpringBootMailComponent implements MailComponent {

    /**
     * 邮件发送器
     */
    @Autowired
    private JavaMailSender javaMailSender;

    /**
     * 发件人
     */
    @Value("${spring.mail.from}")
    private String from;

    /**
     * 发送邮件
     *
     * @param mailSendRequest 发送邮件入参
     */
    @Override
    public void sendMail(MailSendRequest mailSendRequest) {

        try {
            MimeMessage message = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(mailSendRequest.getTo());
            helper.setFrom(from);
            helper.setSubject(mailSendRequest.getSubject());
            helper.setText(mailSendRequest.getContent(), true);
            javaMailSender.send(message);
            log.info("Email sent successfully");
        } catch (MessagingException e) {
            log.warn("Email sending failed：", e);
            throw new BizException("Email sending failed");
        }
    }
}
