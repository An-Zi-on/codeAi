// CaptchaService.java
package com.aizihe.codeaai.component.img;

import com.cxssboot.common.domain.CommonResult;
import com.cxssboot.config.TencentCloudProperties;
import com.cxssboot.domain.request.CaptchaVerifyRequest;
import com.tencentcloudapi.captcha.v20190722.CaptchaClient;
import com.tencentcloudapi.captcha.v20190722.models.DescribeCaptchaResultRequest;
import com.tencentcloudapi.captcha.v20190722.models.DescribeCaptchaResultResponse;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CaptchaService {

    private final TencentCloudProperties tencentCloudProperties;

    public CaptchaService(TencentCloudProperties tencentCloudProperties) {
        this.tencentCloudProperties = tencentCloudProperties;
    }

    public CommonResult<Integer> verify(CaptchaVerifyRequest request, String userIp) {


        try {
            // 1. 构建凭证
            Credential cred = new Credential(
                tencentCloudProperties.getSecretId(),
                tencentCloudProperties.getSecretKey()
            );

            // 2. 创建 Client（使用配置的地域）
            String region = tencentCloudProperties.getCaptcha().getRegion();
            CaptchaClient client = new CaptchaClient(cred, region);

            // （可选）开启调试日志
            // ClientProfile profile = new ClientProfile();
            // profile.setDebug(true);
            // client = new CaptchaClient(cred, region, profile);

            // 3. 构建请求
            DescribeCaptchaResultRequest req = new DescribeCaptchaResultRequest();
            req.setCaptchaType(9L); // 固定值，参考腾讯云文档
            req.setTicket(request.getTicket());
            req.setAppSecretKey("6fGbOIWqiwrFDzHMDGIyaNagT");
            req.setRandstr(request.getRandstr());
            req.setUserIp(userIp);
            req.setCaptchaAppId(tencentCloudProperties.getCaptcha().getAppId());

            // 4. 发起请求
            DescribeCaptchaResultResponse resp = client.DescribeCaptchaResult(req);

            // 5. 解析结果
            Integer captchaCode = Math.toIntExact(resp.getCaptchaCode());
            if (captchaCode == 1) {
                return CommonResult.success(captchaCode);
            } else {
                CommonResult.error("验证码验证失败，code: " + captchaCode);
                log.warn("CAPTCHA 验证失败 | ticket={}, ip={}, code={}",
                         request.getTicket(), userIp, captchaCode);
            }

        } catch (TencentCloudSDKException e) {
            log.error("调用腾讯云 CAPTCHA SDK 失败", e);
            CommonResult.error("验证服务异常: " + e.getMessage());
        } catch (Exception e) {

           CommonResult.error(e.getMessage());
        }
    return CommonResult.success();
    }
}