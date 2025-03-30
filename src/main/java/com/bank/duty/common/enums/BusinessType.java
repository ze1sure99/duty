package com.bank.duty.common.enums;

/**
 * 业务操作类型
 */
public enum BusinessType {
    /**
     * 其它
     */
    OTHER,

    /**
     * 新增
     */
    INSERT,

    /**
     * 修改
     */
    UPDATE,

    /**
     * 删除
     */
    DELETE,

    /**
     * 授权
     */
    GRANT,

    /**
     * 导出
     */
    EXPORT,

    /**
     * 导入
     */
    IMPORT,

    /**
     * 强退
     */
    FORCE,

    /**
     * 生成代码
     */
    GENCODE,

    /**
     * 清空数据
     */
    CLEAN,

    /**
     * 提交
     */
    SUBMIT,

    /**
     * 审核
     */
    REVIEW,

    /**
     * 审批
     */
    APPROVE,

    /**
     * 驳回
     */
    REJECT,

    /**
     * 评分
     */
    SCORE
}