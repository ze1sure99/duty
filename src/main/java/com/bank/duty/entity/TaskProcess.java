package com.bank.duty.entity;

import java.util.Date;
import java.io.Serializable;

import lombok.Data;

/**
 * 任务流程实体类
 */
@Data
public class TaskProcess implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 任务id */
    private Long taskId;

    /** 流程类型（1-提交，2-审核，3-审批，4-驳回） */
    private String processType;

    /** 操作人id */
    private Long operatorId;

    /** 操作人姓名 */
    private String operatorName;

    /** 操作人机构id */
    private String operatorOrgId;

    /** 操作时间 */
    private Date operateTime;

    /** 操作结果（0-不通过，1-通过） */
    private String operateResult;

    /** 操作意见 */
    private String operateOpinion;

    /** 创建人主键id */
    private Long creator;

    /** 编辑人主键id */
    private Long modifier;

    /** 创建时间 */
    private Date createTime;

    /** 修改时间 */
    private Date updateTime;
}