package com.aizihe.codeaai.component.phone;


import com.cxssboot.common.constant.CacheKey;
import com.cxssboot.common.enums.BizCodeEnum;
import com.cxssboot.common.exception.BizException;
import com.cxssboot.component.CaptchaComponent;
import com.cxssboot.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

/**
 * 短信发送基类
 * @author 93564
 */
@Slf4j
public abstract class BaseSmsComponent implements com.cxssboot.base.component.sms.SmsComponent {

    /**
     * redis操作类
     */
    @Autowired
    private RedisService redisService;


    @Autowired
    private CaptchaComponent captchaComponent;
    
    @Override
    public void sendVerifyCode(SmsSendRequest smsSendRequest) {
        // 因为是非登录态下发送短信验证码，方式被刷，要做限制
        // 校验图形验证码
//        captchaComponent.validateCaptcha(smsSendRequest.getKey(), smsSendRequest.getCode());
        // 在redis中获取验证码
        String cacheKey = String.format(CacheKey.SMS_VERIFY_CODE, smsSendRequest.getSource(), smsSendRequest.getUserPhone());
        // 缓存 验证码_时间戳 000000_xxxxxxxxxxxxx
        String cacheCode = redisService.getCacheObject(cacheKey);
        if (StringUtils.isNotEmpty(cacheCode)) {
            log.info("获取之前发送的验证码={}", cacheCode);
            // 获取验证码生成的时间戳
            long ttl = Long.parseLong(cacheCode.split("_")[1]);
            // 剩余的时间
            long remainingTime = CommonUtil.getCurrentTimestamp() - ttl;
            if (remainingTime < (VerifyCodeExpireConstant.CODE_SEND_LIMIT)) {
                log.info("验证码发送过于频繁，60s之内只能发送一次，距下一次发送还有{}ms", remainingTime);
                throw new BizException(BizCodeEnum.SMS_CODE_LIMITED);
            }
        }
//        String code = CommonUtil.getRandomCode(6);
        String code = "999999";
        String value = code + "_" + CommonUtil.getCurrentTimestamp();
        // 将验证码保存到redis中
        redisService.setCacheObject(cacheKey, value, VerifyCodeExpireConstant.CODE_EXPIRE, TimeUnit.MILLISECONDS);
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
        String cacheKey = String.format(CacheKey.SMS_VERIFY_CODE, smsCheckRequest.getSource(), smsCheckRequest.getUserPhone());
        // 获取验证码缓存信息 验证码_时间戳
        String cacheCode = redisService.getCacheObject(cacheKey);
        // 校验手机验证码
        if (null == cacheCode || cacheCode.split("_").length != 2 || !cacheCode.split("_")[0].equals(smsCheckRequest.getVerifyCode())) {
            throw new BizException(BizCodeEnum.VERIFY_CODE_ERROR);
        }
        // 删除redis验证码
        redisService.deleteObject(cacheKey);
    }
}