package com.aizihe.codeaai.component.img;

import com.aizihe.codeaai.ThrowUtils.BaseResponse;
import com.aizihe.codeaai.ThrowUtils.ResultUtils;
import com.aizihe.codeaai.config.TencentCloudProperties;
import com.aizihe.codeaai.domain.request.check.CaptchaVerifyRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CaptchaService {

    private final TencentCloudProperties tencentCloudProperties;

    public CaptchaService(TencentCloudProperties tencentCloudProperties) {
        this.tencentCloudProperties = tencentCloudProperties;
    }

    /**
     * 图形验证码校验（当前为简化实现，未真实调用腾讯云 SDK）
     *
     * @param request 前端验证码请求
     * @param userIp  用户 IP
     * @return 1 表示通过
     */
    public BaseResponse<Integer> verify(CaptchaVerifyRequest request, String userIp) {
        // TODO 如需接入腾讯云验证码，可在此处补充 SDK 调用逻辑
        log.info("【模拟校验图形验证码】ticket={}, randstr={}, ip={}", request.getTicket(), request.getRandstr(), userIp);
        // 直接返回通过
        return ResultUtils.success(1);
    }
}