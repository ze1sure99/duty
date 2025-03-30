package com.bank.duty.entity;

import java.util.Date;
import java.io.Serializable;

import lombok.Data;

/**
 * 用户实体类
 */
@Data
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 姓名 */
    private String name;

    /** eoa用户名 */
    private String eoaName;

    /** DHR人员编号 */
    private String dhr;

    /** 办公电话 */
    private String officeTel;

    /** 机构id */
    private String orgId;

    /** 邮箱 */
    private String email;

    /** 是否党员 */
    private Boolean isCpc;

    /** 条线：办公室、纪委、党廉 */
    private String line;

    /** 创建人主键id */
    private Long creator;

    /** 编辑人主键id */
    private Long modifier;

    /** 创建时间 */
    private Date createTime;

    /** 修改时间 */
    private Date updateTime;

    /** 备用字段1 */
    private String spareInfo1;

    /** 备用字段2 */
    private String spareInfo2;

    /** 备用字段3 */
    private String spareInfo3;

    /** 备用字段4 */
    private String spareInfo4;

    /** 备用字段5 */
    private String spareInfo5;

    /** 备用字段6 */
    private String spareInfo6;

    /** 备用字段7 */
    private Long spareInfo7;

    /** 备用字段8 */
    private Boolean spareInfo8;

    /** 备用字段9 */
    private Integer spareInfo9;

    /** 备用字段10 */
    private Integer spareInfo10;
}