package com.bank.duty.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.io.Serializable;

import lombok.Data;

/**
 * 任务实体类
 */
@Data
public class Task implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 任务标题 */
    private String taskTitle;

    /** 任务内容/工作要求 */
    private String taskContent;

    /** 任务分类，多个用逗号分隔 */
    private String taskCategories;

    /** 业务条线（办公室、纪委、党廉） */
    private String line;

    /** 创建机构id */
    private String createOrgId;

    /** 目标机构，多个用逗号分隔 */
    private String targetOrgs;

    /** 协办人IDs，多个用逗号分隔 */
    private String coordinatorIds;

    /** 是否需要审核（0-否，1-是） */
    private Boolean needReview;

    /** 审核人IDs，多个用逗号分隔 */
    private String reviewerIds;

    /** 是否需要审批（0-否，1-是） */
    private Boolean needApproval;

    /** 审批人IDs，多个用逗号分隔 */
    private String approverIds;

    /** 任务得分上限 */
    private BigDecimal maxScore;

    /** 系统得分 */
    private BigDecimal systemScore;

    /** 材料得分 */
    private BigDecimal materialScore;

    /** 扣分 */
    private BigDecimal deductionScore;

    /** 实际得分 */
    private BigDecimal actualScore;

    /** 开始时间 */
    private Date startTime;

    /** 结束时间 */
    private Date endTime;

    /** 是否定期任务（0-否，1-是） */
    private Boolean isPeriodic;

    /** 定期类型（1-每天，2-每周，3-每月，4-每季度，5-每年） */
    private String periodicType;

    /** 状态（1-待办，2-已提交，3-审核中，4-已审核，5-审批中，6-已完成，7-已驳回） */
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