package com.bank.duty.common.constant;

/**
 * 业务常量
 */
public class BusinessConstants {

    /**
     * 业务条线
     */
    public interface BusinessLine {
        /** 办公室条线 */
        String OFFICE = "办公室";
        /** 纪委条线 */
        String DISCIPLINE = "纪委";
        /** 党廉条线 */
        String PARTY = "党廉";
    }

    /**
     * 任务状态
     */
    public interface TaskStatus {
        /** 待办 */
        String PENDING = "1";
        /** 已提交 */
        String SUBMITTED = "2";
        /** 审核中 */
        String REVIEWING = "3";
        /** 已审核 */
        String REVIEWED = "4";
        /** 审批中 */
        String APPROVING = "5";
        /** 已完成 */
        String COMPLETED = "6";
        /** 已驳回 */
        String REJECTED = "7";
    }

    /**
     * 任务流程类型
     */
    public interface ProcessType {
        /** 提交 */
        String SUBMIT = "1";
        /** 审核 */
        String REVIEW = "2";
        /** 审批 */
        String APPROVE = "3";
        /** 驳回 */
        String REJECT = "4";
    }

    /**
     * 操作结果
     */
    public interface OperateResult {
        /** 不通过 */
        String FAIL = "0";
        /** 通过 */
        String SUCCESS = "1";
    }

    /**
     * 机构层级
     */
    public interface OrgLevel {
        /** 省级分行 */
        String PROVINCIAL = "2";
        /** 二级分行及省行本部 */
        String SECONDARY = "3";
        /** 支行、省行部室、二级分行本部 */
        String BRANCH = "4";
        /** 网点、分行部室、支行本部 */
        String OUTLET = "5";
        /** 支行部室 */
        String BRANCH_DEPT = "6";
        /** 分行团队 */
        String TEAM = "7";
    }

    /**
     * 角色类型
     */
    public interface RoleType {
        /** 超级管理员 */
        String SUPER_ADMIN = "1";
        /** 系统管理员 */
        String SYSTEM_ADMIN = "2";
        /** 操作员 */
        String OPERATOR = "3";
    }

    /**
     * 定期任务类型
     */
    public interface PeriodicType {
        /** 每天 */
        String DAILY = "1";
        /** 每周 */
        String WEEKLY = "2";
        /** 每月 */
        String MONTHLY = "3";
        /** 每季度 */
        String QUARTERLY = "4";
        /** 每年 */
        String YEARLY = "5";
    }
}