package com.bank.duty.shiro;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bank.duty.service.impl.TokenBlacklistService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.bank.duty.common.utils.JwtUtils;
import com.bank.duty.common.utils.SpringUtils;
import com.bank.duty.framework.web.domain.AjaxResult;

/**
 * JWT过滤器
 */
public class JwtFilter extends BasicHttpAuthenticationFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    /**
     * 请求头Token键名
     */
    private static final String TOKEN_HEADER = "Authorization";

    private TokenBlacklistService blacklistService;

    /**
     * 在过滤器配置设置后初始化
     */
    @Override
    protected void onFilterConfigSet() throws Exception {
        super.onFilterConfigSet();
        try {
            // 使用SpringUtils从Spring容器获取TokenBlacklistService实例
            this.blacklistService = SpringUtils.getBean(TokenBlacklistService.class);
            logger.debug("成功初始化TokenBlacklistService");
        } catch (Exception e) {
            logger.error("初始化TokenBlacklistService失败", e);
        }
    }

    /**
     * 判断是否允许访问
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        // 先判断请求是否是OPTIONS请求（跨域预检请求）
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        if (httpRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            return true;
        }

        // 尝试登录
        if (isLoginAttempt(request, response)) {
            try {
                // 获取JWT令牌
                String token = getAuthzHeader(request);
                if (token != null) {
                    // 移除Bearer前缀
                    if (token.startsWith("Bearer ")) {
                        token = token.substring(7);
                    }

                    // 检查令牌是否在黑名单中（确保service已初始化）
                    if (blacklistService != null && blacklistService.isBlacklisted(token)) {
                        logger.debug("Token已被加入黑名单，拒绝访问");
                        return false;
                    }
                }

                // 继续正常的登录验证流程
                return executeLogin(request, response);
            } catch (Exception e) {
                // 认证失败，不做处理，将进入onAccessDenied处理
                logger.debug("Token认证失败", e);
            }
        }

        return false;
    }

    // 其余方法保持不变...
    /**
     * 判断是否是登录请求
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        String token = req.getHeader(TOKEN_HEADER);
        return token != null;
    }

    /**
     * 执行登录
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader(TOKEN_HEADER);

        // 获取JwtUtils实例
        JwtUtils jwtUtils = SpringUtils.getBean(JwtUtils.class);

        // 从请求头获取JWT
        token = jwtUtils.getTokenFromHeader(token);

        // 创建JwtToken
        JwtToken jwtToken = new JwtToken(token);

        // 提交给Realm进行登录，如果错误会抛出异常并被捕获
        getSubject(request, response).login(jwtToken);

        return true;
    }

    /**
     * 当访问拒绝时执行
     */
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 设置响应状态码为401
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        // 设置响应内容类型
        response.setContentType("application/json;charset=UTF-8");

        // 返回未授权错误信息
        AjaxResult result = AjaxResult.error("未授权，请重新登录");

        try {
            response.getWriter().write(JSON.toJSONString(result));
        } catch (IOException e) {
            logger.error("输出响应数据失败", e);
        }

        return false;
    }

    /**
     * 处理跨域问题
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        httpResponse.setHeader("Access-Control-Allow-Origin", httpRequest.getHeader("Origin"));
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpResponse.setHeader("Access-Control-Allow-Headers", httpRequest.getHeader("Access-Control-Request-Headers"));
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");

        // 对于OPTIONS请求直接返回成功
        if (httpRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpResponse.setStatus(HttpStatus.OK.value());
            return false;
        }

        return super.preHandle(request, response);
    }
}