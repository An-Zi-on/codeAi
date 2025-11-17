package com.aizihe.codeaai.domain.request.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @param
 * @return
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequest {
    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

}
