package com.aizihe.codeaai.controller;

import com.aizihe.codeaai.ThrowUtils.BaseResponse;
import com.aizihe.codeaai.ThrowUtils.ResultUtils;
import com.aizihe.codeaai.component.BaseComponent;
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
    private final SmsSendComponent smsSendComponent;
    private final MailSendComponent mailSendComponent;
    private final BaseComponent baseComponent;

    @PostMapping("captcha/verify")
    public BaseResponse<Integer> verifyCaptcha(
            @Valid
            @RequestBody CaptchaVerifyRequest request,
            HttpServletRequest servletRequest) {

        // 简化获取用户 IP（不依赖外部 CommonUtil）
        String requestIp = servletRequest.getRemoteAddr();
        return captchaService.verify(request, requestIp);
    }

    @PostMapping("captcha/sms-send")
    public BaseResponse<String> sendSms(@Valid @RequestBody SendRequest sendRequest) {
        SmsSendRequest request = new SmsSendRequest();
        request.setUserPhone(sendRequest.getContact());
        smsSendComponent.sendVerifyCode(request);
        return ResultUtils.success("验证码发送成功");
    }

    @PostMapping("captcha/check")
    public BaseResponse<String> sendSms(@Valid @RequestBody CheckRequest request) {
        return baseComponent.check(request);
    }

    @PostMapping("captcha/email-send")
    public BaseResponse<String> sendEmail(@Valid @RequestBody SendRequest sendRequest) {
        MailSendRequest request = new MailSendRequest();
        request.setTo(sendRequest.getContact());
        mailSendComponent.mailSend(request);
        return ResultUtils.success("验证码发送成功");
    }
}