package com.aizihe.codeaai.component.phone;

import com.aizihe.codeaai.domain.request.check.SmsCheckRequest;
import com.aizihe.codeaai.domain.request.check.SmsSendRequest;

/**
 * 短信发送与校验组件接口
 * @author 93564
 */
public interface SmsComponent {

    /**
     * 发送短信验证码
     *
     * @param smsSendRequest 发送短信验证码请求参数
     */
    void sendVerifyCode(SmsSendRequest smsSendRequest);

    /**
     * 校验短信验证码
     *
     * @param smsCheckRequest 校验短信验证码请求参数
     */
    void checkSmsVerifyCode(SmsCheckRequest smsCheckRequest);
}
