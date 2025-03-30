package com.bank.duty.entity;

import java.util.Date;
import java.io.Serializable;

import lombok.Data;

/**
 * 用户角色关联实体类
 */
@Data
public class UserRole implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 用户表主键id */
    private Long userId;

    /** 角色表主键id */
    private Long roleId;

    /** 创建人主键id */
    private Long creator;

    /** 编辑人主键id */
    private Long modifier;

    /** 创建时间 */
    private Date createTime;

    /** 修改时间 */
    private Date updateTime;
}