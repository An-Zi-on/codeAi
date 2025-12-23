package com.aizihe.codeaai.component.phone;


import com.aizihe.codeaai.constant.CacheKey;
import com.aizihe.codeaai.constant.VerifyCodeExpireConstant;
import com.aizihe.codeaai.domain.request.check.SmsCheckRequest;
import com.aizihe.codeaai.domain.request.check.SmsSendRequest;
import com.aizihe.codeaai.exception.BusinessException;
import com.aizihe.codeaai.exception.ErrorCode;
import com.aizihe.codeaai.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

/**
 * 短信发送基类
 * @author 93564
 */
@Slf4j
public abstract class BaseSmsComponent implements SmsComponent {

    /**
     * redis操作类
     */
    @Autowired
    private RedisService redisService;

    @Override
    public void sendVerifyCode(SmsSendRequest smsSendRequest) {
        // 因为是非登录态下发送短信验证码，方式被刷，要做限制
        // 校验图形验证码
//        captchaComponent.validateCaptcha(smsSendRequest.getKey(), smsSendRequest.getCode());
        // 在redis中获取验证码
        String cacheKey = String.format(CacheKey.SMS_VERIFY_CODE, smsSendRequest.getUserPhone());
        // 缓存 验证码_时间戳 000000_xxxxxxxxxxxxx
        String cacheCode = redisService.getCacheObject(cacheKey);
        if (StringUtils.isNotEmpty(cacheCode)) {
            //todo 时间计算
           throw  new BusinessException(ErrorCode.PARAMS_ERROR,"请勿重复申请");
        }
//        String code = CommonUtil.getRandomCode(6);
        String code = "999999";
        String value = code + "_" + System.currentTimeMillis();
        // 将验证码保存到redis中
        redisService.setCacheObject(cacheKey, value, VerifyCodeExpireConstant.CAPTCHA_VERIFY_CODE_EXPIRE_SECONDS, TimeUnit.MILLISECONDS);
//        doSendVerifyCode(smsSendRequest.getUserPhone(),code);
    }

    /**
     * 发送验证码方法  具体实现类实现自己的发送短信方法
     *
     * @param userPhone 用户手机号
     * @param code 短信验证码
     */
    protected abstract void doSendVerifyCode(String userPhone,String code);

    /**
     * 校验短信验证码
     *
     * @param smsCheckRequest  用户手机号
     */
    @Override
    public void checkSmsVerifyCode(SmsCheckRequest smsCheckRequest) {
        // 在redis中获取验证码
        String cacheKey = String.format(CacheKey.SMS_VERIFY_CODE, smsCheckRequest.getUserPhone());
        // 获取验证码缓存信息 验证码_时间戳
        String cacheCode = redisService.getCacheObject(cacheKey);
        // 校验手机验证码
        if (null == cacheCode || cacheCode.split("_").length != 2 || !cacheCode.split("_")[0].equals(smsCheckRequest.getCode())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 删除redis验证码
        redisService.deleteObject(cacheKey);
    }
}