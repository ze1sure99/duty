package com.bank.duty.config;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.bank.duty.common.exception.BaseException;
import com.bank.duty.common.exception.BusinessException;
import com.bank.duty.framework.web.domain.ErrorResponse;

/**
 * 自定义异常解析器
 */
public class CustomHandlerExceptionResolver implements HandlerExceptionResolver {

    private static final Logger logger = LoggerFactory.getLogger(CustomHandlerExceptionResolver.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
                                         Object handler, Exception ex) {
        logger.error("CustomHandlerExceptionResolver处理异常: ", ex);

        try {
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

            // 设置状态码和错误消息
            if (ex instanceof BaseException) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            } else if (ex instanceof BusinessException) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
            } else {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }

            // 构建错误响应
            ErrorResponse errorResponse = new ErrorResponse(
                    response.getStatus(),
                    ex.getMessage(),
                    request.getRequestURI()
            );

            // 序列化为JSON并写入响应
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));

        } catch (IOException e) {
            logger.error("处理异常时发生错误: ", e);
        }

        return new ModelAndView();
    }
}