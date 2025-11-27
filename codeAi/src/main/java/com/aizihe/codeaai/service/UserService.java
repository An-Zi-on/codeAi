package com.aizihe.codeaai.service;

import com.aizihe.codeaai.domain.VO.UserVO;
import com.aizihe.codeaai.domain.entity.User;
import com.aizihe.codeaai.domain.request.user.UserLoginRequest;
import com.aizihe.codeaai.domain.request.user.UserRegisterRequest;
import com.aizihe.codeaai.domain.request.user.UserUpdatePwdRequest;
import com.aizihe.codeaai.domain.request.user.UserUpdateRequest;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户 服务层。
 *
 * @author zhuge
 * @since yyyy-MM-dd
 */
public interface UserService extends IService<User> {
    boolean  userRegister(UserRegisterRequest request);

    boolean userLogin(UserLoginRequest request , HttpServletRequest servletRequest);

    User checkUpdate(UserUpdateRequest request);

    User checkPssword(UserUpdatePwdRequest request);

    UserVO current(HttpServletRequest request);

    /***
     * 校验是否是管理员
     * @param request
     * @return 当前登入的用户
     */
    UserVO isAdmin(HttpServletRequest request);
}
