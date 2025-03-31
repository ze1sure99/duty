package com.bank.duty.controller.auth;

import com.bank.duty.common.utils.JwtUtils;
import com.bank.duty.entity.User;
import com.bank.duty.framework.web.domain.AjaxResult;
import com.bank.duty.service.UserService;
import com.bank.duty.service.impl.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 认证控制器
 */
@Api(tags = "认证管理", description = "用户登录、登出及获取信息接口")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenBlacklistService blacklistService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 登录
     */
    @ApiOperation(value = "用户登录", notes = "根据用户名进行登录认证，返回JWT令牌")
    @PostMapping("/login")
    public AjaxResult login(
            @ApiParam(value = "用户名", required = true) @RequestParam String username) {
        // 根据用户名查询用户
        User user = userService.selectUserByEoaName(username);

        if (user == null) {
            return AjaxResult.error("用户不存在");
        }

        // 直接生成JWT，跳过密码验证
        String token = jwtUtils.generateToken(username);

        // 返回登录成功信息
        return AjaxResult.success("登录成功").put("token", token).put("user", user);
    }

    /**
     * 登出
     */
    @ApiOperation(value = "用户登出", notes = "退出登录，清除用户会话")
    @RequiresAuthentication
    @PostMapping("/logout")
    public AjaxResult logout(HttpServletRequest request) {
        String token = jwtUtils.getTokenFromHeader(request.getHeader("Authorization"));
        if (token != null) {
            // 从令牌中获取过期时间
            Claims claims = jwtUtils.getClaimsFromToken(token);
            long expiration = claims.getExpiration().getTime();
            // 将令牌添加到黑名单
            blacklistService.addToBlacklist(token, expiration);
        }

        return AjaxResult.success("退出成功");
    }

    /**
     * 获取当前登录用户信息
     */
    @ApiOperation(value = "获取用户信息", notes = "获取当前登录用户的详细信息")
    @RequiresAuthentication
    @GetMapping("/info")
    public AjaxResult info() {
        // 从JWT中获取用户名
        String username = com.bank.duty.common.utils.SecurityUtils.getCurrentUsername();
        if (username == null) {
            return AjaxResult.error("获取用户信息失败");
        }

        // 根据用户名查询用户信息
        User user = userService.selectUserByEoaName(username);
        if (user == null) {
            return AjaxResult.error("用户不存在");
        }

        return AjaxResult.success(user);
    }
}