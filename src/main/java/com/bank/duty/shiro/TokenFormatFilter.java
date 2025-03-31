package com.bank.duty.shiro;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Token格式过滤器 - 自动处理没有Bearer前缀的Token
 */
public class TokenFormatFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化方法
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        TokenFormatRequestWrapper requestWrapper = new TokenFormatRequestWrapper(httpRequest);
        chain.doFilter(requestWrapper, response);
    }

    @Override
    public void destroy() {
        // 销毁方法
    }

    private static class TokenFormatRequestWrapper extends HttpServletRequestWrapper {
        public TokenFormatRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getHeader(String name) {
            String header = super.getHeader(name);
            if ("Authorization".equalsIgnoreCase(name) && header != null && !header.isEmpty() && !header.startsWith("Bearer ")) {
                return "Bearer " + header;
            }
            return header;
        }
    }
}