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
public class UserUpdatePwdRequest {

    /**
     * id
     */
    private Long id;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 确认密码
     */
    private String checkPassword;
}
