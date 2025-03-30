package com.bank.duty.framework.web.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 错误响应对象
 */
public class ErrorResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 响应时间戳 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date timestamp;

    /** 响应状态码 */
    private int status;

    /** 错误消息 */
    private String message;

    /** 请求路径 */
    private String path;

    /**
     * 初始化一个新创建的 ErrorResponse 对象
     */
    public ErrorResponse() {
        this.timestamp = new Date();
    }

    /**
     * 初始化一个新创建的 ErrorResponse 对象
     *
     * @param status 响应状态码
     * @param message 错误消息
     * @param path 请求路径
     */
    public ErrorResponse(int status, String message, String path) {
        this.timestamp = new Date();
        this.status = status;
        this.message = message;
        this.path = path;
    }

    /**
     * 创建错误响应对象
     */
    public static ErrorResponse error(int status, String message, String path) {
        return new ErrorResponse(status, message, path);
    }

    /**
     * 创建错误响应对象 - 400错误
     */
    public static ErrorResponse badRequest(String message, String path) {
        return error(400, message, path);
    }

    /**
     * 创建错误响应对象 - 401错误
     */
    public static ErrorResponse unauthorized(String message, String path) {
        return error(401, message, path);
    }

    /**
     * 创建错误响应对象 - 403错误
     */
    public static ErrorResponse forbidden(String message, String path) {
        return error(403, message, path);
    }

    /**
     * 创建错误响应对象 - 404错误
     */
    public static ErrorResponse notFound(String message, String path) {
        return error(404, message, path);
    }

    /**
     * 创建错误响应对象 - 500错误
     */
    public static ErrorResponse serverError(String message, String path) {
        return error(500, message, path);
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}