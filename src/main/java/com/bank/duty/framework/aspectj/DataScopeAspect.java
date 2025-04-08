package com.bank.duty.framework.aspectj;

import java.lang.reflect.Method;
import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bank.duty.common.annotation.DataScope;
import com.bank.duty.common.utils.SecurityUtils;
import com.bank.duty.common.utils.StringUtil;
import com.bank.duty.entity.User;
import com.bank.duty.service.UserService;
import com.bank.duty.service.RoleService;

/**
 * 数据过滤处理
 */
@Aspect
@Component
public class DataScopeAspect {

    /**
     * 数据权限过滤关键字
     */
    public static final String DATA_SCOPE = "dataScope";

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

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
                    controllerDataScope.userAlias(), controllerDataScope.lineParam());
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
     */
    public void dataScopeFilter(JoinPoint joinPoint, User user, String orgAlias, String userAlias,
                                String lineParam) {
        StringBuilder sqlString = new StringBuilder();

        // 判断用户是否为超级管理员
        boolean isSuperAdmin = isSuperAdmin(user.getId());

        if (!isSuperAdmin) {
            // 非超级管理员，进行数据范围过滤
            String orgId = user.getOrgId();
            String line = user.getLine();

            if (StringUtil.isNotEmpty(orgAlias)) {
                // 机构权限过滤：所有非超级管理员用户(系统管理员和普通操作员)只能看到自己所属机构和下级机构
                sqlString.append(String.format(
                        " OR %s.org_id = '%s' OR %s.org_id IN (SELECT org_id FROM hnzrlz_org WHERE p_org_id = '%s')",
                        orgAlias, orgId, orgAlias, orgId));
            }

            // 条线权限过滤：非超级管理员只能看到本条线数据
            if (StringUtil.isNotEmpty(line)) {
                // 如果指定了条线参数名，使用该参数
                if (StringUtil.isNotEmpty(lineParam)) {
                    sqlString.append(String.format(" AND %s = '%s'", lineParam, line));
                } else {
                    // 如果没有指定条线参数名，但有line字段，默认使用line字段
                    sqlString.append(String.format(" AND line = '%s'", line));
                }
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
                // 反射获取对象的dataScope属性并设置值
                setDataScope(params, sqlString.toString());
            }
        }
    }

    /**
     * 判断用户是否为超级管理员
     * @param userId 用户ID
     * @return 是否超级管理员
     */
    private boolean isSuperAdmin(Long userId) {
        // 使用已有的方法获取用户角色值列表
        List<String> roleValues = roleService.selectRoleValuesByUserId(userId);

        // 检查是否包含超级管理员角色值 "1"
        return roleValues.contains("1");
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
            if (dataScope.startsWith(" OR ")) {
                dataScope = "(" + dataScope.substring(4) + ")";
            }
            setDataScopeMethod.invoke(params, dataScope);
        } catch (Exception e) {
            // 设置数据权限失败，可能是对象没有dataScope属性
        }
    }
}



