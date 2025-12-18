package com.aizihe.codeaai.component;

import cn.hutool.core.util.StrUtil;
import com.aizihe.codeaai.ThrowUtils.BaseResponse;
import com.aizihe.codeaai.ThrowUtils.RedisService;
import com.aizihe.codeaai.ThrowUtils.ResultUtils;
import com.aizihe.codeaai.constant.CacheKey;
import com.aizihe.codeaai.domain.request.check.CheckRequest;
import com.aizihe.codeaai.exception.BusinessException;
import com.aizihe.codeaai.exception.ErrorCode;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @param
 * @return
 */
@Component
public class BaseComponent {
    @Resource
    private RedisService redisService;

    public BaseResponse<String> check(CheckRequest request) {
        String contact = request.getContact();
        String code = request.getCode();

        // 1. 参数校验
        if (StrUtil.isBlank(contact)) {
           throw  new BusinessException(ErrorCode.PARAMS_ERROR,"联系方式不能为空");
        }
        if (StrUtil.isBlank(code)) {
            throw  new BusinessException(ErrorCode.PARAMS_ERROR,"联系方式不能为空");
        }

        // 2. 判断类型并获取 Redis 中的验证码
        String cacheKey;
        if (contact.contains("@")) {
            // 邮箱
            cacheKey = String.format(CacheKey.SMS_EMAIL_CODE, contact);
        } else if (contact.matches("^1[3-9]\\d{9}$")) {
            // 中国大陆手机号
            cacheKey = String.format(CacheKey.SMS_VERIFY_CODE, contact);
        } else {
            throw  new BusinessException(ErrorCode.PARAMS_ERROR,"格式无效");
        }

        // 3. 验证码比对
        String storedCode = redisService.getCacheObject(cacheKey);
        if (StrUtil.isNotBlank(storedCode) && storedCode.equals(code)) {
            redisService.deleteObject(cacheKey);
            return ResultUtils.success("0"); // 成功

        } else {
            return ResultUtils.success("1"); // 验证码错误
        }
    }
}
