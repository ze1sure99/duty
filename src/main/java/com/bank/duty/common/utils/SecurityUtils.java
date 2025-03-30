package com.bank.duty.common.utils;

import com.bank.duty.common.exception.BusinessException;
import com.bank.duty.entity.User;
import com.bank.duty.service.UserService;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 安全工具类
 */
@Component
public class SecurityUtils {
    private static final Logger logger = LoggerFactory.getLogger(SecurityUtils.class);

    private static JwtUtils jwtUtils;
    private static UserService userService;

    @Autowired
    public void setJwtUtils(JwtUtils jwtUtils) {
        SecurityUtils.jwtUtils = jwtUtils;
    }

    @Autowired
    public void setUserService(UserService userService) {
        SecurityUtils.userService = userService;
    }

    /**
     * 获取当前用户名
     */
    public static String getCurrentUsername() {
        String token = getToken();
        return jwtUtils.getUsernameFromToken(token);
    }

    /**
     * 获取当前用户ID
     * 从数据库中查询当前登录用户的ID
     */
    public static Long getCurrentUserId() {
        try {
            String username = getCurrentUsername();
            if (StringUtil.isEmpty(username)) {
                logger.warn("获取当前用户名为空");
                throw new BusinessException("获取用户信息异常");
            }

            // 根据EOA用户名查询用户信息
            User user = userService.selectUserByEoaName(username);
            if (user == null) {
                logger.warn("根据用户名[{}]查询不到用户信息", username);
                throw new BusinessException("获取用户信息异常");
            }

            return user.getId();
        } catch (Exception e) {
            logger.error("获取当前用户ID异常", e);
            throw new BusinessException("获取用户信息异常");
        }
    }

    /**
     * 获取当前Token
     */
    public static String getToken() {
        try {
            Subject subject = org.apache.shiro.SecurityUtils.getSubject();
            if (subject == null || subject.getPrincipal() == null) {
                logger.warn("获取Subject或Principal为空");
                throw new BusinessException("获取用户信息异常");
            }
            return subject.getPrincipal().toString();
        } catch (Exception e) {
            logger.error("获取当前Token异常", e);
            throw new BusinessException("获取用户信息异常");
        }
    }

    /**
     * 判断是否有权限
     */
    public static boolean isPermitted(String permission) {
        return org.apache.shiro.SecurityUtils.getSubject().isPermitted(permission);
    }

    /**
     * 判断是否有角色
     */
    public static boolean hasRole(String role) {
        return org.apache.shiro.SecurityUtils.getSubject().hasRole(role);
    }
}