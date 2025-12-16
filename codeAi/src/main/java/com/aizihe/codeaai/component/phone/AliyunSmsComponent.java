package com.aizihe.codeaai.component.phone;

import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.cxssboot.common.enums.BizCodeEnum;
import com.cxssboot.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

/**
 * 阿里云短信发送组件
 * @author 93564
 */
//@Component(value = "aliyunSmsComponent")
@Slf4j
public class AliyunSmsComponent extends com.cxssboot.base.component.sms.BaseSmsComponent {

    /**
     * 短信签名
     */
    @Value("${sms.verify-code.sign-name}")
    private String signName;
    
    /**
     * 短信模板编号
     */
    @Value("${sms.verify-code.template-code}")
    private String templateCode;
    
    /**
     * 阿里云ak
     */
    @Value("${sms.send.ak}")
    private String ak;
    
    /**
     * 阿里云sk
     */
    @Value("${sms.send.sk}")
    private String sk;

    private Client createSmsClient() {
        Config config = new Config()
                // 您的AccessKey ID
                .setAccessKeyId(ak)
                // 您的AccessKey Secret
                .setAccessKeySecret(sk);
        // 访问的域名
        config.endpoint = "dysmsapi.aliyuncs.com";
        try {
            return new Client(config);
        } catch (Exception e) {
            throw new BizException(BizCodeEnum.SYSTEM_ERROR.getCode(), "初始化短信发送客户端异常");
        }
    }
    
    /**
     * 发送短信
     *
     * @param userPhone 用户手机号
     * @param code      短信验证码
     */
    @Override
    protected void doSendVerifyCode(String userPhone, String code) {
        SendSmsRequest sendSmsRequest = new SendSmsRequest().setPhoneNumbers(userPhone).setSignName(signName)
                .setTemplateCode(templateCode).setTemplateParam(this.generateSmsParam(code).toJSONString());
        try {
            log.info("发送短信验证码请求入参={}", JSON.toJSONString(sendSmsRequest));
            SendSmsResponse sendSmsResponse = createSmsClient().sendSms(sendSmsRequest);
            log.info("发送短信验证码结果={}", JSON.toJSONString(sendSmsResponse));
        } catch (Exception e) {
            log.warn("阿里云发送短信验证码异常", e);
        }
    }
    
    /**
     * 生成发送短信的参数
     *
     * @param param 参数可变长数组
     * @return JSON字符串
     */
    private JSONObject generateSmsParam(String... param) {
        JSONObject paramJson = new JSONObject();
        paramJson.put("code", param[0]);
        return paramJson;
    }
}
