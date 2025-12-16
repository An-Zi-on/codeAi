package com.aizihe.codeaai.component.phone;


/**
 * @author 93564
 */
public interface SmsComponent {
    
    /**
     * 发送短信验
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
