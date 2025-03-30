package com.bank.duty.common.constant;

/**
 * 机构层级常量
 */
public class OrgLevel {
    /** 省级分行 */
    public static final String PROVINCIAL = "2";

    /** 二级分行及省行本部 */
    public static final String SECONDARY = "3";

    /** 支行、省行部室、二级分行本部 */
    public static final String BRANCH = "4";

    /** 网点、分行部室、支行本部 */
    public static final String OUTLET = "5";

    /** 支行部室 */
    public static final String BRANCH_DEPT = "6";

    /** 分行团队 */
    public static final String TEAM = "7";

    // 私有构造函数，防止实例化
    private OrgLevel() {
        // 不允许创建实例
    }
}