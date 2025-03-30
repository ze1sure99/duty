package com.bank.duty.framework.aspectj;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bank.duty.common.annotation.DataScope;
import com.bank.duty.common.constant.OrgLevel;
import com.bank.duty.common.utils.SecurityUtils;
import com.bank.duty.common.utils.StringUtil;
import com.bank.duty.entity.User;
import com.bank.duty.service.UserService;
import com.bank.duty.service.OrganizationService;

/**
 * 数据过滤处理
 */
@Aspect
@Component
public class DataScopeAspect {

    /**
     * 全部数据权限
     */
    public static final String DATA_SCOPE_ALL = "1";

    /**
     * 自定数据权限
     */
    public static final String DATA_SCOPE_CUSTOM = "2";

    /**
     * 部门数据权限
     */
    public static final String DATA_SCOPE_DEPT = "3";

    /**
     * 部门及以下数据权限
     */
    public static final String DATA_SCOPE_DEPT_AND_CHILD = "4";

    /**
     * 仅本人数据权限
     */
    public static final String DATA_SCOPE_SELF = "5";

    /**
     * 数据权限过滤关键字
     */
    public static final String DATA_SCOPE = "dataScope";

    @Autowired
    private UserService userService;

    @Autowired
    private OrganizationService organizationService;

    // 配置织入点
    @Pointcut("@annotation(com.bank.duty.common.annotation.DataScope)")
    public void dataScopePointCut() {
    }

    @Before("dataScopePointCut()")
    public void doBefore(JoinPoint point) throws Throwable {
        handleDataScope(point);
    }

    protected void handleDataScope(final JoinPoint joinPoint) {
        // 获得注解
        DataScope controllerDataScope = getAnnotationLog(joinPoint);
        if (controllerDataScope == null) {
            return;
        }

        // 获取当前的用户
        String username = SecurityUtils.getCurrentUsername();
        User currentUser = userService.selectUserByEoaName(username);
        if (currentUser != null) {
            // 数据权限过滤
            dataScopeFilter(joinPoint, currentUser, controllerDataScope.orgAlias(),
                    controllerDataScope.userAlias(), controllerDataScope.lineParam(), organizationService);
        }
    }

    /**
     * 数据范围过滤
     *
     * @param joinPoint 切点
     * @param user 用户
     * @param orgAlias 机构别名
     * @param userAlias 用户别名
     * @param lineParam 业务条线参数
     * @param orgService 组织服务
     */
    public static void dataScopeFilter(JoinPoint joinPoint, User user, String orgAlias, String userAlias,
                                       String lineParam, OrganizationService orgService) {
        StringBuilder sqlString = new StringBuilder();

        // 判断用户是否为超级管理员，超级管理员拥有所有权限
        if (user.getId() != 1L) {
            // 非超级管理员，进行数据范围过滤
            // 针对不同的用户设置不同的数据权限
            String orgId = user.getOrgId();
            String line = user.getLine();

            // 通过orgId查询组织级别
            String orgLevel = orgService.getOrgLevelByOrgId(orgId);

            if (StringUtil.isNotEmpty(orgAlias)) {
                // 根据机构层级设置不同的数据权限

                // 1. 省级分行用户 - 可以查看所有下级机构数据
                if (OrgLevel.PROVINCIAL.equals(orgLevel)) {
                    sqlString.append(String.format(
                            " OR %s.org_id IN (SELECT org_id FROM hnzrlz_org WHERE p_org_id = '%s')",
                            orgAlias, orgId));
                }

                // 2. 二级分行用户 - 可以查看所属二级分行和下级机构数据
                else if (OrgLevel.SECONDARY.equals(orgLevel)) {
                    sqlString.append(String.format(
                            " OR %s.org_id = '%s' OR %s.org_id IN (SELECT org_id FROM hnzrlz_org WHERE p_org_id = '%s')",
                            orgAlias, orgId, orgAlias, orgId));
                }

                // 3. 支行用户 - 可以查看所属支行和下级网点、部室数据
                else if (OrgLevel.BRANCH.equals(orgLevel)) {
                    sqlString.append(String.format(
                            " OR %s.org_id = '%s' OR %s.org_id IN (SELECT org_id FROM hnzrlz_org WHERE p_org_id = '%s')",
                            orgAlias, orgId, orgAlias, orgId));
                }

                // 4. 网点用户 - 可以查看本网点数据
                else if (OrgLevel.OUTLET.equals(orgLevel)) {
                    sqlString.append(String.format(" OR %s.org_id = '%s'", orgAlias, orgId));
                }

                // 5. 支行部室用户 - 只能查看本部室数据
                else if (OrgLevel.BRANCH_DEPT.equals(orgLevel)) {
                    sqlString.append(String.format(" OR %s.org_id = '%s'", orgAlias, orgId));
                }

                // 6. 分行团队用户 - 只能查看本团队数据
                else if (OrgLevel.TEAM.equals(orgLevel)) {
                    sqlString.append(String.format(" OR %s.org_id = '%s'", orgAlias, orgId));
                }
            }

            // 如果有业务条线参数，添加条线过滤
            if (StringUtil.isNotEmpty(lineParam) && StringUtil.isNotEmpty(line)) {
                sqlString.append(String.format(" AND %s = '%s'", lineParam, line));
            }

            if (StringUtil.isNotEmpty(userAlias)) {
                // 拼接创建人过滤条件
                sqlString.append(String.format(" OR %s.creator = %d", userAlias, user.getId()));
            }
        }

        if (StringUtil.isNotBlank(sqlString.toString())) {
            // 组装查询条件
            Object params = joinPoint.getArgs()[0];
            if (StringUtil.isNotNull(params)) {
                // 反射获取对象的datascope属性并设置值
                setDataScope(params, sqlString.toString());
            }
        }
    }

    /**
     * 获取数据权限注解
     */
    private DataScope getAnnotationLog(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();

        if (method != null) {
            return method.getAnnotation(DataScope.class);
        }
        return null;
    }

    /**
     * 通过反射设置对象的dataScope属性值
     */
    private static void setDataScope(Object params, String dataScope) {
        try {
            Method setDataScopeMethod = params.getClass().getDeclaredMethod("setDataScope", String.class);
            setDataScopeMethod.invoke(params, dataScope);
        } catch (Exception e) {
            // 设置数据权限失败，可能是对象没有dataScope属性
        }
    }
}