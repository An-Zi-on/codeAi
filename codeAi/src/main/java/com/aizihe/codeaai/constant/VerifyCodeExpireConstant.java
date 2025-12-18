package com.aizihe.codeaai.constant;

/**
 * 验证码过期时间常量（单位：秒）
 *
 * @author 93564 / gavyn
 */
public final class VerifyCodeExpireConstant {

    /**
     * 短信验证码过期时间：5 分钟 = 300 秒
     */
    public static final long SMS_VERIFY_CODE_EXPIRE_SECONDS = 60L;

    /**
     * 邮箱验证码过期时间：10 分钟 = 600 秒
     */
    public static final long EMAIL_VERIFY_CODE_EXPIRE_SECONDS = 60L;

    /**
     * 图形验证码过期时间：2 分钟 = 120 秒
     */
    public static final long CAPTCHA_VERIFY_CODE_EXPIRE_SECONDS = 60L;

    // 可根据业务扩展其他类型验证码过期时间
}