package com.aizihe.codeaai.aop;

import cn.hutool.json.JSONUtil;
import com.aizihe.codeaai.ThrowUtils.RedisService;
import com.aizihe.codeaai.ThrowUtils.ThrowUtils;
import com.aizihe.codeaai.annotation.MustRole;
import com.aizihe.codeaai.domain.VO.UserVO;
import com.aizihe.codeaai.domain.common.Constants;
import com.aizihe.codeaai.exception.ErrorCode;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @param
 * @return
 */
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    RedisService redisService;

    @Before("@annotation(mustRole)")
    public void checkRole(JoinPoint joinPoint, MustRole mustRole) {
        String requiredRole = mustRole.needRole();
        if (requiredRole.isEmpty()) {
            return; // 无角色要求，放行
        }

        // 获取当前请求
        ServletRequestAttributes attributes = (ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new RuntimeException("Not in web request context");
        }
        HttpServletRequest request = attributes.getRequest();
        UserVO currentUser =  JSONUtil.toBean((String) request.getSession().getAttribute(Constants.USER_CACHE),UserVO.class);
        String currentUserRole = currentUser.getUserRole();
        ThrowUtils.throwIf(currentUserRole == null || !currentUserRole.equals(requiredRole), ErrorCode.NO_AUTH_ERROR);

    }

}
