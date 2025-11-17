package com.aizihe.codeaai.service;

import cn.hutool.http.server.HttpServerRequest;
import com.aizihe.codeaai.domain.VO.UserVO;
import com.aizihe.codeaai.domain.request.user.UserLoginRequest;
import com.aizihe.codeaai.domain.request.user.UserRegisterRequest;
import com.aizihe.codeaai.domain.request.user.UserUpdatePwdRequest;
import com.aizihe.codeaai.domain.request.user.UserUpdateRequest;
import com.mybatisflex.core.service.IService;
import com.aizihe.codeaai.domain.entity.User;
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
}
