package com.bank.duty.entity;

import java.util.Date;
import java.io.Serializable;

import lombok.Data;

/**
 * 菜单实体类
 */
@Data
public class Menu implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 菜单名称 */
    private String menuName;

    /** 父菜单id */
    private Long parentId;

    /** 显示顺序 */
    private Integer orderNum;

    /** 路由地址 */
    private String path;

    /** 组件路径 */
    private String component;

    /** 菜单类型（M-目录，C-菜单，F-按钮） */
    private String menuType;

    /** 菜单状态（0-显示，1-隐藏） */
    private String visible;

    /** 菜单状态（0-正常，1-禁用） */
    private String status;

    /** 权限标识 */
    private String perms;

    /** 菜单图标 */
    private String icon;

    /** 业务条线（办公室、纪委、党廉） */
    private String line;

    /** 创建人主键id */
    private Long creator;

    /** 编辑人主键id */
    private Long modifier;

    /** 创建时间 */
    private Date createTime;

    /** 修改时间 */
    private Date updateTime;

    /** 备注 */
    private String remark;
}