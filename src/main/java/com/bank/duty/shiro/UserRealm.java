package com.bank.duty.shiro;

import com.bank.duty.common.utils.JwtUtils;
import com.bank.duty.entity.User;
import com.bank.duty.entity.Role;
import com.bank.duty.service.UserService;
import com.bank.duty.service.RoleService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 用户Realm
 */
public class UserRealm extends AuthorizingRealm {
    private static final Logger logger = LoggerFactory.getLogger(UserRealm.class);

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 支持JwtToken
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = jwtUtils.getUsernameFromToken(principals.toString());
        User user = userService.selectUserByEoaName(username);
        if (user == null) {
            logger.info("用户: {} 不存在.", username);
            return null;
        }

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        // 设置用户角色
        List<Role> roles = roleService.selectRolesByUserId(user.getId());
        Set<String> roleSet = new HashSet<>();

        for (Role role : roles) {
            // 角色值：1超级管理员2系统管理员3操作员
            if (role.getRole() != null) {
                String roleType = role.getRole();
                if ("1".equals(roleType)) {
                    roleSet.add("admin");
                } else if ("2".equals(roleType)) {
                    roleSet.add("system");
                } else if ("3".equals(roleType)) {
                    roleSet.add("operator");
                }
            }
        }

        info.setRoles(roleSet);

        // 这里可以根据角色设置对应的权限
        Set<String> permissionSet = new HashSet<>();
        if (roleSet.contains("admin")) {
            // 超级管理员拥有所有权限
            permissionSet.add("*:*:*");
        } else if (roleSet.contains("system")) {
            // 系统管理员拥有用户管理权限
            permissionSet.add("system:user:*");
            permissionSet.add("system:role:*");
            permissionSet.add("system:menu:*");
            permissionSet.add("system:org:*");
        } else if (roleSet.contains("operator")) {
            // 操作员拥有查询权限
            permissionSet.add("system:user:list");
            permissionSet.add("system:role:list");
        }

        info.setStringPermissions(permissionSet);
        return info;
    }

    /**
     * 认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String token = (String) authenticationToken.getCredentials();

        // 验证token是否有效
        if (!jwtUtils.validateToken(token)) {
            throw new ExpiredCredentialsException("令牌已过期");
        }

        // 从token中获取用户名
        String username = jwtUtils.getUsernameFromToken(token);
        if (username == null) {
            throw new UnknownAccountException("令牌验证失败");
        }

        // 查询用户是否存在
        User user = userService.selectUserByEoaName(username);
        if (user == null) {
            throw new UnknownAccountException("用户不存在");
        }

        return new SimpleAuthenticationInfo(token, token, getName());
    }
}