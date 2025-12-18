package com.aizihe.codeaai.component.phone;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 阿里云短信发送组件（简化版本，仅日志输出，可根据需要接入真实 SDK）
 * @author 93564
 */
@Component(value = "aliyunSmsComponent")
@Slf4j
public class AliyunSmsComponent extends BaseSmsComponent {

    /**
     * 短信签名
     */
    @Value("${sms.verify-code.sign-name:默认签名}")
    private String signName;

    /**
     * 短信模板编号
     */
    @Value("${sms.verify-code.-code:defautemplateltTemplate}")
    private String templateCode;

    /**
     * 发送短信
     *
     * @param userPhone 用户手机号
     * @param code      短信验证码
     */
    @Override
    protected void doSendVerifyCode(String userPhone, String code) {
        // 这里先不接入真实阿里云 SDK，仅做日志输出，避免引入额外依赖导致编译错误
        log.info("【模拟发送阿里云短信】phone={}, signName={}, templateCode={}, code={}",
                userPhone, signName, templateCode, code);
    }
}
