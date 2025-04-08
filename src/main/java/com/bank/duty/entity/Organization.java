package com.bank.duty.entity;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 机构实体类
 */
@Data
public class Organization {
    /** 主键id */
    private Long id;

    /** 机构id */
    private String orgId;

    /** 父机构id */
    private String pOrgId;

    /** 机构简称 */
    private String orgShort;

    /** 机构名称 */
    private String orgName;

    /** 机构类别 */
    private String orgType;

    /** 机构所在层级 */
    private String orgLevel;

    /** 排序号 */
    private Integer orderNum;

    /** 创建人主键id */
    private Long creator;

    /** 编辑人主键id */
    private Long modifier;

    /** 创建时间 */
    private String createTime;

    /** 修改时间 */
    private String updateTime;

    /** 子部门 */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Organization> children = new ArrayList<>();

    /** 数据权限 */
    private String dataScope;
}
