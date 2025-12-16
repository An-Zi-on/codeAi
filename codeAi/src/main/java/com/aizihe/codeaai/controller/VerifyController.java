package com.aizihe.codeaai.controller;


import com.aizihe.codeaai.component.img.CaptchaService;
import com.aizihe.codeaai.component.mail.MailSendComponent;
import com.aizihe.codeaai.component.phone.SmsSendComponent;
import com.aizihe.codeaai.domain.request.check.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
/**
 * 验证控制器
 */
public class VerifyController {

    private final CaptchaService captchaService;
    private  final SmsSendComponent smsSendComponent;
    private  final MailSendComponent mailSendComponent;
    private  final BaseComponent baseComponent;
    @PostMapping("captcha/verify")
    public CommonResult<Integer> verifyCaptcha(
            @Valid
            @RequestBody CaptchaVerifyRequest request,

            HttpServletRequest servletRequest) {

        // 获取真实用户 IP（支持代理）
        String requestIp = CommonUtil.getIpAddr(servletRequest);
        // 防止本地回环地址绕过验证
        if ("127.0.0.1".equals(requestIp) || "0:0:0:0:0:0:0:1".equals(requestIp)) {
            requestIp = "127.0.0.1"; // 或直接拒绝？根据业务决定
        }

        return captchaService.verify(request, requestIp);
    }

    @PostMapping("captcha/sms-send")
    public CommonResult<String> sendSms(@Valid @RequestBody SendRequest sendRequest) {
        SmsSendRequest request =  new SmsSendRequest();
        request.setUserPhone(sendRequest.getContact());
        smsSendComponent.sendVerifyCode(request);
        return CommonResult.success("验证码发送成功");
    }

    @PostMapping("captcha/check")
    public CommonResult<String> sendSms(@Valid @RequestBody CheckRequest request) {
        return baseComponent.check(request);
    }
    @PostMapping("captcha/email-send")
    public CommonResult<String> sendEmail(@Valid @RequestBody SendRequest sendRequest) {
        MailSendRequest request = new MailSendRequest();
        request.setTo(sendRequest.getContact());
        mailSendComponent.mailSend(request);
        return CommonResult.success("验证码发送成功");
    }
}