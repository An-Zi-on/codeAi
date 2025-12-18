package com.aizihe.codeaai.component.phone;

import com.aizihe.codeaai.domain.request.check.SmsCheckRequest;
import com.aizihe.codeaai.domain.request.check.SmsSendRequest;
import com.aizihe.codeaai.exception.BusinessException;
import com.aizihe.codeaai.exception.ErrorCode;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 发送短信验证码 service 组件
 *
 * @author gavyn
 */
@Component
public class SmsSendComponent {


    /**
     * 短信发送组件集合（按 beanName 区分实现）
     */
    @Resource
    private Map<String, SmsComponent> smsComponentMap;


    public void sendVerifyCode(SmsSendRequest smsSendRequest) {
        // 默认使用阿里云短信组件
        SmsComponent smsComponent = smsComponentMap.getOrDefault("aliyunSmsComponent", null);
        if (smsComponent == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "短信组件不存在");
        }
        smsComponent.sendVerifyCode(smsSendRequest);
    }

    public void checkSmsVerifyCode(SmsCheckRequest smsCheckRequest) {
        // 默认使用阿里云短信组件
        SmsComponent smsComponent = smsComponentMap.getOrDefault("aliyunSmsComponent", null);
        if (smsComponent == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "短信组件不存在");
        }
        smsComponent.checkSmsVerifyCode(smsCheckRequest);
    }
}
