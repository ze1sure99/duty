package com.bank.duty.entity;

import java.util.Date;
import java.io.Serializable;

import lombok.Data;

/**
 * 系统日志实体类
 */
@Data
public class Log implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 用户id */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 机构id */
    private String orgId;

    /** 操作内容 */
    private String operation;

    /** 请求方法 */
    private String method;

    /** 请求参数 */
    private String params;

    /** 执行时长(毫秒) */
    private Long time;

    /** IP地址 */
    private String ip;

    /** 创建时间 */
    private Date createTime;
}