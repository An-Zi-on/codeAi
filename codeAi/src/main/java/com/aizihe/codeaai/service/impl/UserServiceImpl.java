package com.aizihe.codeaai.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.aizihe.codeaai.ThrowUtils.CryptoUtils;
import com.aizihe.codeaai.ThrowUtils.RedisService;
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
    RedisService redisService;
    @Resource
    UserMapper userMapper ;

    @Override
    public boolean userRegister(UserRegisterRequest request) {
        String userAccount = request.getUserAccount();
        //校验账号
        validateField(userAccount,"账号",8,10,false);
        long count = userMapper.selectCountByQuery(new QueryWrapper().eq(User::getUserAccount, userAccount));
        ThrowUtils.throwIf(count >0,ErrorCode.PARAMS_ERROR,"账号已存在");
        //校验密码
        String userPassword = request.getUserPassword();
        validateField(userPassword,"密码",8,10,false);

        //校验确认密码
        String checkPassword = request.getCheckPassword();
        validateField(checkPassword,"确认密码",8,10,false);

        //密码加密
        String ciphertextP = CryptoUtils.hashPassword(userPassword, USER_SALT);
        String ciphertextC = CryptoUtils.hashPassword(checkPassword, USER_SALT);
        ThrowUtils.throwIf(!ciphertextC.equals(ciphertextP),ErrorCode.PARAMS_ERROR,"两次密码输入不一致");
        User user = BeanUtil.copyProperties(request, User.class);
        user.setUserPassword(ciphertextP);
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
        validateField(userAccount,"账号",8,10,false);
        String userPassword = request.getUserPassword();
        validateField(userPassword,"密码",8,10,false);
        User user = userMapper.selectOneByQuery(new QueryWrapper().eq(User::getUserAccount, userAccount));
        ThrowUtils.throwIf(user== null,ErrorCode.NOT_FOUND_ERROR,"账号不存在");
        String userPasswordD = user.getUserPassword();
        String ciphertextU = CryptoUtils.hashPassword(userPassword, USER_SALT);
        ThrowUtils.throwIf(!ciphertextU.equals(userPasswordD),ErrorCode.PARAMS_ERROR,"密码错误");
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        servletRequest.getSession().setAttribute(USER_CACHE,JSONUtil.toJsonStr(userVO));
        return true;
    }

    @Override
    public User checkUpdate(UserUpdateRequest request) {
        Long id = request.getId();
        User userD = userMapper.selectOneById(id);
        ThrowUtils.throwIf(userD == null,ErrorCode.NOT_FOUND_ERROR);
        String userName = request.getUserName();
        validateField(userName,"用户名",2,8,false);
        String userProfile = request.getUserProfile();
        validateField(userProfile,"个人简介",0,30,true);
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
        ThrowUtils.throwIf(userD.getUserPassword().equals(hashPassword),ErrorCode.PARAMS_ERROR,"密码重复");
        User user = BeanUtil.copyProperties(request, User.class);
        user.setUserPassword(CryptoUtils.hashPassword(userPassword,USER_SALT));
        user.setEditTime(LocalDateTime.now());
        return user;
    }

    @Override
    public UserVO current(HttpServletRequest request) {
        UserVO currentUser = JSONUtil.toBean((String) request.getSession().getAttribute(USER_CACHE),UserVO.class) ;
        ThrowUtils.throwIf(currentUser == null,ErrorCode.NO_AUTH_ERROR,"当前用户未登入");
        Long id = currentUser.getId();
        User user = this.getById(id);
        ThrowUtils.throwIf(user == null,ErrorCode.NOT_FOUND_ERROR);
        UserVO userVO = UserVO.fromEntity(user);
        request.setAttribute(USER_CACHE,JSONUtil.toJsonStr(userVO));
        return userVO;
    }

    @Override
    public UserVO isAdmin(HttpServletRequest request) {
        UserVO current = current(request);
        ThrowUtils.throwIf(UserRole.ADMIN.getValue().equals(current.getUserRole()),ErrorCode.NO_AUTH_ERROR);
        return current;

    }

    /**
     * 校验字符串字段
     *
     * @param value        字段值
     * @param fieldName    字段名（用于错误提示）
     * @param minLength    最小长度（包含）
     * @param maxLength    最大长度（包含）
     * @param allowNull    是否允许为 null 或空白（true=允许，false=不允许）
     */
    private void validateField(String value, String fieldName, int minLength, int maxLength, boolean allowNull) {
        // 如果不允许为空，先校验非空非空白
        if (!allowNull) {
            if (StrUtil.isBlank(value)) {
                ThrowUtils.throwIf(true, ErrorCode.PARAMS_ERROR, fieldName + "不能为空");
            }
        }

        // 如果允许为空，且当前值为空，则跳过长度校验
        if (allowNull && StrUtil.isBlank(value)) {
            return;
        }

        // 执行长度校验（此时 value 一定非空）
        if (value.length() < minLength || value.length() > maxLength) {
            ThrowUtils.throwIf(true, ErrorCode.PARAMS_ERROR,
                    String.format("%s长度需为%d-%d位", fieldName, minLength, maxLength));
        }
    }
}
