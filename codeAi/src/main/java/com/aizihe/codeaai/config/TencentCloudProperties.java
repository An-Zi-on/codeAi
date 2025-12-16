package com.aizihe.codeaai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 腾讯云图形验证码配置读取类
 */
@Data
@Component
@ConfigurationProperties(prefix = "tencent.cloud")
public class TencentCloudProperties {
    private String secretId;
    private String secretKey;
    private Captcha captcha = new Captcha();
    private  Email email = new Email();
    @Data
    public static class Captcha {
        private Long appId;
        private String region = "ap-guangzhou";
    }
    @Data
    public static  class Email{
        private String fromEmail;
        private String region = "ap-guangzhou";
        private String templateId;
    }
}