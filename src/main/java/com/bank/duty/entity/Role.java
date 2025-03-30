package com.bank.duty.entity;

import java.util.Date;
import java.io.Serializable;

import lombok.Data;

/**
 * 角色实体类
 */
@Data
public class Role implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** eoa名称 */
    private String eoaName;

    /** 角色 1超级管理员 2系统管理员 3操作员 */
    private String role;

    /** 创建人主键id */
    private Long creator;

    /** 编辑人主键id */
    private Long modifier;

    /** 创建时间 */
    private Date createTime;

    /** 修改时间 */
    private Date updateTime;
}