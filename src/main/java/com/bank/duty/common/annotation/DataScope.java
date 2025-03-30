package com.bank.duty.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据权限过滤注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {
    /**
     * 机构表的别名
     */
    String orgAlias() default "";

    /**
     * 用户表的别名
     */
    String userAlias() default "";

    /**
     * 业务条线参数
     */
    String lineParam() default "";
}