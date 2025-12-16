package com.aizihe.codeaai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "sms.tencent")
public class TencentSmsProperties {
    private String secretId;
    private String secretKey;
    private String sdkAppId;
    private String signName;
    private String templateId;
    private String region = "ap-guangzhou";
}