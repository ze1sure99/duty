package com.bank.duty.controller.system;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.duty.common.annotation.Log;
import com.bank.duty.common.enums.BusinessType;
import com.bank.duty.common.utils.SecurityUtils;
import com.bank.duty.entity.Organization;
import com.bank.duty.framework.web.domain.AjaxResult;
import com.bank.duty.framework.web.domain.TreeSelect;
import com.bank.duty.service.OrganizationService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 机构信息
 */
@Api(tags = "机构管理接口")
@RestController
@RequestMapping("/system/org")
public class OrgController {
    @Autowired
    private OrganizationService organizationService;

    /**
     * 获取机构列表
     */
    @ApiOperation("获取机构列表")
    @RequiresPermissions("system:org:list")
    @GetMapping("/list")
    public AjaxResult list(Organization organization) {
        List<Organization> orgs = organizationService.selectOrganizationList(organization);
        return AjaxResult.success(orgs);
    }

    /**
     * 获取当前用户的机构树
     */
    @ApiOperation("获取当前用户的机构树")
    @GetMapping("/userOrgTree")
    public AjaxResult userOrgTree() {
        Long userId = SecurityUtils.getCurrentUserId();
        return AjaxResult.success(organizationService.selectOrgTreeSelectByUserId(userId));
    }

    /**
     * 根据用户ID获取机构树
     */
    @ApiOperation("根据用户ID获取机构树")
    @RequiresPermissions("system:org:list")
    @GetMapping("/userOrgTree/{userId}")
    public AjaxResult userOrgTreeById(@PathVariable("userId") Long userId) {
        return AjaxResult.success(organizationService.selectOrgTreeSelectByUserId(userId));
    }

    /**
     * 获取机构下拉树列表
     */
    @ApiOperation("获取机构下拉树列表")
    @GetMapping("/treeselect")
    public AjaxResult treeselect(Organization organization) {
        List<Organization> orgs = organizationService.selectOrganizationList(organization);
        return AjaxResult.success(organizationService.buildOrgTreeSelect(orgs));
    }

    /**
     * 加载对应角色机构列表树
     */
    @ApiOperation("加载对应角色机构列表树")
    @GetMapping(value = "/roleDeptTreeselect/{roleId}")
    public AjaxResult roleDeptTreeselect(@PathVariable("roleId") Long roleId) {
        List<Organization> orgs = organizationService.selectOrganizationList(new Organization());
        AjaxResult ajax = AjaxResult.success();
        ajax.put("checkedKeys", organizationService.selectOrgListByRoleId(roleId));
        ajax.put("orgs", organizationService.buildOrgTreeSelect(orgs));
        return ajax;
    }

    /**
     * 根据机构id获取详细信息
     */
    @ApiOperation("根据机构id获取详细信息")
    @RequiresPermissions("system:org:query")
    @GetMapping(value = "/{orgId}")
    public AjaxResult getInfo(@PathVariable String orgId) {
        return AjaxResult.success(organizationService.selectOrganizationByOrgId(orgId));
    }

    /**
     * 新增机构
     */
    @ApiOperation("新增机构")
    @RequiresPermissions("system:org:add")
    @Log(title = "机构管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Organization organization) {
        return AjaxResult.success(organizationService.insertOrganization(organization));
    }

    /**
     * 修改机构
     */
    @ApiOperation("修改机构")
    @RequiresPermissions("system:org:edit")
    @Log(title = "机构管理", businessType = BusinessType.UPDATE)
    @PostMapping("/update")
    public AjaxResult edit(@RequestBody Organization organization) {
        return AjaxResult.success(organizationService.updateOrganization(organization));
    }

    /**
     * 删除机构
     */
    @ApiOperation("删除机构")
    @RequiresPermissions("system:org:remove")
    @Log(title = "机构管理", businessType = BusinessType.DELETE)
    @PostMapping("/delete/{orgId}")
    public AjaxResult remove(@PathVariable String orgId) {
        Organization org = organizationService.selectOrganizationByOrgId(orgId);
        if (org == null) {
            return AjaxResult.error("机构不存在");
        }
        return AjaxResult.success(organizationService.deleteOrganizationById(org.getId()));
    }
}
