package com.bank.duty.entity;

import java.util.Date;
import java.io.Serializable;

import lombok.Data;

/**
 * 任务分类实体类
 */
@Data
public class TaskCategory implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 分类名称 */
    private String categoryName;

    /** 业务条线（办公室、纪委、党廉） */
    private String line;

    /** 父分类id */
    private Long parentId;

    /** 显示顺序 */
    private Integer orderNum;

    /** 归属部门 */
    private String department;

    /** 状态（0-正常，1-禁用） */
    private String status;

    /** 创建人主键id */
    private Long creator;

    /** 编辑人主键id */
    private Long modifier;

    /** 创建时间 */
    private Date createTime;

    /** 修改时间 */
    private Date updateTime;
}