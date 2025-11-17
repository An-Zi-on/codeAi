package com.aizihe.codeaai.ThrowUtils;

import cn.hutool.crypto.SecureUtil;

/**
 * 纯 Hutool 实现的加盐 MD5 工具类（兼容 5.8.38）
 * 注意：MD5 不安全，仅用于演示或非密码场景！
 */
public final class CryptoUtils {


    /**
     * 加盐 MD5 哈希（password + salt）
     */
    public static String hashPassword(String password, String salt) {
        if (password == null || salt == null) {
            throw new IllegalArgumentException("密码或盐不能为空");
        }
        return SecureUtil.md5(password + salt);
    }


}