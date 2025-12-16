package com.aizihe.codeaai.component.phone;


import com.cxssboot.common.enums.BizCodeEnum;
import com.cxssboot.common.exception.BizException;

import java.util.Map;

/**
 * 发送短信验service组件
 *
 * @author gavyn
 */
//@Component
public class SmsSendComponent {


    /**
     * 发送短信验证码组件
     */
    //@Autowired
    private Map<String, com.cxssboot.component.phone.SmsComponent> smsComponentMap;


    public void sendVerifyCode(SmsSendRequest smsSendRequest) {
        //获取对应的发送短信组件
        // TODO
        //获取对应的注册组件
        SmsComponent smsComponent = smsComponentMap.get("aliyunSmsComponent");
        if (smsComponent == null) {
            throw new BizException(BizCodeEnum.SMS_COMPONENT_NOT_EXIST);
        }
        smsComponent.sendVerifyCode(smsSendRequest);
    }

    public void checkSmsVerifyCode(SmsCheckRequest smsCheckRequest) {
        //获取对应的发送短信组件
        // TODO
        //获取对应的注册组件
        SmsComponent smsComponent = smsComponentMap.get("aliyunSmsComponent");
        if (smsComponent == null) {
            throw new BizException(BizCodeEnum.SMS_COMPONENT_NOT_EXIST);
        }
        smsComponent.checkSmsVerifyCode(smsCheckRequest);
    }
}
