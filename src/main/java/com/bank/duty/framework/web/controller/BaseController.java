package com.bank.duty.framework.web.controller;

import com.bank.duty.common.exception.BusinessException;
import com.bank.duty.common.utils.DateUtils;
import com.bank.duty.common.utils.SecurityUtils;
import com.bank.duty.common.utils.StringUtil;
import com.bank.duty.framework.web.domain.AjaxResult;
import org.apache.shiro.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.util.Date;

/**
 * Web层通用数据处理
 */
public class BaseController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Date类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(DateUtils.parseDate(text));
            }
        });
    }

    /**
     * 响应返回结果
     *
     * @param rows 影响行数
     * @return 操作结果
     */
    protected AjaxResult toAjax(int rows) {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 响应返回结果
     *
     * @param result 结果
     * @return 操作结果
     */
    protected AjaxResult toAjax(boolean result) {
        return result ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 页面跳转
     */
    public String redirect(String url) {
        return StringUtil.format("redirect:{}", url);
    }

    /**
     * 获取当前登录用户名
     */
    protected String getUsername() {
        return SecurityUtils.getCurrentUsername();
    }

    /**
     * 获取当前登录用户ID
     */
    protected Long getUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    /**
     * 判断当前用户是否具有某种权限
     */
    protected boolean hasPermission(String permission) {
        return SecurityUtils.isPermitted(permission);
    }

    /**
     * 判断当前用户是否具有某种角色
     */
    protected boolean hasRole(String role) {
        return SecurityUtils.hasRole(role);
    }
}