package com.bank.duty.config;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.WebRequest;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 异常处理配置
 */
@Configuration
public class ExceptionConfig {

    /**
     * 自定义错误属性
     */
    @Bean
    public ErrorAttributes errorAttributes() {
        return new DefaultErrorAttributes() {
            @Override
            public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
                Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);

                // 格式化错误响应
                Map<String, Object> resultMap = new LinkedHashMap<>();
                resultMap.put("timestamp", errorAttributes.get("timestamp"));
                resultMap.put("status", errorAttributes.get("status"));
                resultMap.put("message", errorAttributes.get("message"));
                resultMap.put("path", errorAttributes.get("path"));

                return resultMap;
            }
        };
    }
}