package com.aizihe.codeaai.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.server.HttpServerRequest;
import com.aizihe.codeaai.ThrowUtils.CryptoUtils;
import com.aizihe.codeaai.ThrowUtils.ThrowUtils;
import com.aizihe.codeaai.domain.VO.UserVO;
import com.aizihe.codeaai.domain.entity.User;
import com.aizihe.codeaai.domain.request.user.UserLoginRequest;
import com.aizihe.codeaai.domain.request.user.UserRegisterRequest;
import com.aizihe.codeaai.domain.request.user.UserUpdatePwdRequest;
import com.aizihe.codeaai.domain.request.user.UserUpdateRequest;
import com.aizihe.codeaai.enums.UserRole;
import com.aizihe.codeaai.exception.ErrorCode;
import com.aizihe.codeaai.mapper.UserMapper;
import com.aizihe.codeaai.service.UserService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.aizihe.codeaai.domain.common.Constants.USER_CACHE;
import static com.aizihe.codeaai.domain.common.Constants.USER_SALT;

/**
 * 用户 服务层实现。
 *
 * @author zhuge
 * @since yyyy-MM-dd
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService{
    @Resource
    UserMapper userMapper ;

    @Override
    public boolean userRegister(UserRegisterRequest request) {
        String userAccount = request.getUserAccount();
        //校验账号
        validateField(userAccount,"账号",8,10);

        //校验密码
        String userPassword = request.getUserPassword();
        validateField(userPassword,"密码",8,10);

        //校验确认密码
        String checkPassword = request.getCheckPassword();
        validateField(checkPassword,"确认密码",8,10);

        //密码加密
        String ciphertextP = CryptoUtils.hashPassword(userPassword, USER_SALT);
        String ciphertextC = CryptoUtils.hashPassword(checkPassword, USER_SALT);
        ThrowUtils.throwIf(!ciphertextC.equals(ciphertextP),ErrorCode.PARAMS_ERROR,"两次密码输入不一致");
        User user = BeanUtil.copyProperties(request, User.class);
        user.setUserName("无名");
        user.setUserRole(UserRole.USER.getValue());
        user.setEditTime(LocalDateTime.now());
        int insert = userMapper.insert(user);
        ThrowUtils.throwIf(insert != 1,ErrorCode.SYSTEM_ERROR);
        return true;
    }

    @Override
    public boolean userLogin(UserLoginRequest request, HttpServletRequest servletRequest) {
        String userAccount = request.getUserAccount();
        validateField(userAccount,"账号",8,10);
        String userPassword = request.getUserPassword();
        validateField(userPassword,"密码",8,10);
        User user = userMapper.selectOneByQuery(new QueryWrapper().eq(User::getUserAccount, userAccount));
        ThrowUtils.throwIf(user== null,ErrorCode.NOT_FOUND_ERROR,"账号不存在");
        String userPasswordD = user.getUserPassword();
        String ciphertextU = CryptoUtils.hashPassword(userPassword, USER_SALT);
        ThrowUtils.throwIf(!ciphertextU.equals(userPasswordD),ErrorCode.PARAMS_ERROR,"密码错误");
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        HttpSession session = servletRequest.getSession();
        session.setAttribute(USER_CACHE,userVO);
        return true;
    }

    @Override
    public User checkUpdate(UserUpdateRequest request) {
        Long id = request.getId();
        User userD = userMapper.selectOneById(id);
        ThrowUtils.throwIf(userD == null,ErrorCode.NOT_FOUND_ERROR);
        String userName = request.getUserName();
        validateField(userName,"用户名",2,8);
        String userProfile = request.getUserProfile();
        validateField(userProfile,"个人简介",0,30);
        User user = BeanUtil.copyProperties(request, User.class);
        user.setEditTime(LocalDateTime.now());
        return user;
    }

    @Override
    public User checkPssword(UserUpdatePwdRequest request) {
        String userPassword = request.getUserPassword();
        String checkPassword = request.getCheckPassword();
        ThrowUtils.throwIf(!userPassword.equals(checkPassword),ErrorCode.PARAMS_ERROR,"两次密码不一致");
        Long id = request.getId();
        User userD = userMapper.selectOneById(id);
        ThrowUtils.throwIf(userD == null ,ErrorCode.NOT_FOUND_ERROR);
        String hashPassword = CryptoUtils.hashPassword(userPassword, USER_SALT);
        ThrowUtils.throwIf(!userD.getUserPassword().equals(hashPassword),ErrorCode.PARAMS_ERROR,"密码重复");
        User user = BeanUtil.copyProperties(request, User.class);
        user.setEditTime(LocalDateTime.now());
        return user;
    }

    /**
     * 校验字段：非空、非空白、长度 8~10
     */
    private void validateField(String value, String fieldName,int minLength,int maxLength) {
        boolean invalid = StrUtil.isBlank(value) || value.length() < minLength || value.length() > maxLength;
        ThrowUtils.throwIf(invalid, ErrorCode.PARAMS_ERROR, fieldName + "不能为空且长度需为"+minLength+"-"+maxLength+"位");
    }
}
