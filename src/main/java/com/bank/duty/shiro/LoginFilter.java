package com.bank.duty.shiro;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.filter.authc.UserFilter;
import org.springframework.http.HttpStatus;

import com.alibaba.fastjson.JSON;
import com.bank.duty.framework.web.domain.AjaxResult;

/**
 * 登录过滤器
 */
public class LoginFilter extends UserFilter {

    /**
     * 当访问拒绝时执行
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        // 设置响应状态码为401
        httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        // 设置响应内容类型
        httpResponse.setContentType("application/json;charset=UTF-8");

        // 返回未授权错误信息
        AjaxResult result = AjaxResult.error("未登录，请先登录");

        httpResponse.getWriter().write(JSON.toJSONString(result));
        return false;
    }
}