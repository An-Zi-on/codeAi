package com.aizihe.codeaai.ai.service;

import com.tencentcloudapi.captcha.v20190722.CaptchaClient;
import com.tencentcloudapi.captcha.v20190722.models.DescribeCaptchaResultRequest;
import com.tencentcloudapi.captcha.v20190722.models.DescribeCaptchaResultResponse;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;

public class TestCaptcha {

    // ====== 请在这里填写你的实际参数 ======
    private static final String SECRET_ID = "";
    private static final String SECRET_KEY = "";
    private static final String CAPTCHA_APP_ID = "";

    // 从前端获取（测试时可手动填一个真实的 ticket/randstr）
    private static final String TICKET = ""; // 替换为真实 ticket
    private static final String RANDSTR = "";     // 替换为真实 randstr
    private static final String USER_IP = "";   // 测试可用 8.8.8.8，正式需用户真实 IP
    // ===================================

    public static void main(String[] args) {
        try {
            // 1. 初始化凭证
            Credential cred = new Credential(SECRET_ID, SECRET_KEY);

            // 2. 创建客户端（地域固定为 ap-guangzhou）
            CaptchaClient client = new CaptchaClient(cred, "ap-guangzhou");

            // 3. 构造请求
            DescribeCaptchaResultRequest req = new DescribeCaptchaResultRequest();
            req.setCaptchaType(9L); // 必须是 9（新版验证码）
            req.setTicket(TICKET);
            req.setRandstr(RANDSTR);
            req.setAppSecretKey("OivqDrzTRCLgH1nlKI7B3jJt9");
            req.setUserIp(USER_IP);
            req.setCaptchaAppId(Long.parseLong(CAPTCHA_APP_ID));

            // 4. 发起请求
            System.out.println("正在调用腾讯云验证码校验接口...");
            DescribeCaptchaResultResponse resp = client.DescribeCaptchaResult(req);

            // 5. 输出结果
            System.out.println("返回码 (captchaCode): " + resp.getCaptchaCode());
            System.out.println("描述: " + resp.getCaptchaMsg());

            if (resp.getCaptchaCode() == 1) {
                System.out.println("✅ 验证码校验成功！");
            } else {
                System.out.println("❌ 验证码校验失败，请检查参数或 ticket 是否过期。");
            }

        } catch (TencentCloudSDKException e) {
            System.err.println("SDK 调用异常:");
            System.err.println("错误码: " + e.getErrorCode());
            System.err.println("错误信息: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("其他异常:");
            e.printStackTrace();
        }
    }
}