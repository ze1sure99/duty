package com.bank.duty.controller.system;

import java.util.List;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.bank.duty.entity.Organization;
import com.bank.duty.service.OrganizationService;
import com.bank.duty.framework.web.controller.BaseController;
import com.bank.duty.framework.web.domain.AjaxResult;
import com.bank.duty.common.annotation.Log;
import com.bank.duty.common.enums.BusinessType;
import com.bank.duty.common.annotation.DataScope;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import static com.bank.duty.framework.web.page.PageUtils.startPage;

/**
 * 机构管理 控制器
 */
@Api(tags = "机构管理接口")
@RestController
@RequestMapping("/system/org")
public class OrgController extends BaseController {

    @Autowired
    private OrganizationService organizationService;

    /**
     * 获取机构列表
     */
    @ApiOperation(value = "获取机构列表", notes = "获取所有机构信息列表")
    @RequiresPermissions("system:org:list")
    @GetMapping("/list")
    @DataScope(orgAlias = "o")
    public AjaxResult list(Organization organization) {
        startPage();
        List<Organization> list = organizationService.selectOrganizationList(organization);
        return AjaxResult.success(list);
    }

    /**
     * 获取机构详细信息
     */
    @ApiOperation(value = "获取机构详细信息", notes = "根据机构ID获取机构详细信息")
    @RequiresPermissions("system:org:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@ApiParam(value = "机构ID", required = true, example = "1") @PathVariable("id") Long id) {
        return AjaxResult.success(organizationService.selectOrganizationById(id));
    }

    /**
     * 获取子机构列表
     */
    @ApiOperation(value = "获取子机构列表", notes = "根据父机构ID获取子机构列表")
    @RequiresPermissions("system:org:list")
    @GetMapping(value = "/children/{pOrgId}")
    @DataScope(orgAlias = "o")
    public AjaxResult getChildren(@ApiParam(value = "父机构ID", required = true, example = "1001") @PathVariable("pOrgId") String pOrgId) {
        List<Organization> list = organizationService.selectChildrenOrganizationByPOrgId(pOrgId);
        return AjaxResult.success(list);
    }

    /**
     * 新增机构
     */
    @ApiOperation(value = "新增机构", notes = "新增机构信息")
    @RequiresPermissions("system:org:add")
    @Log(title = "机构管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Organization organization) {
        if (!organizationService.checkOrgIdUnique(organization.getOrgId())) {
            return AjaxResult.error("新增机构'" + organization.getOrgName() + "'失败，机构编码已存在");
        }
        if (!organizationService.checkOrgNameUnique(organization.getOrgName())) {
            return AjaxResult.error("新增机构'" + organization.getOrgName() + "'失败，机构名称已存在");
        }
        return toAjax(organizationService.insertOrganization(organization));
    }

    /**
     * 修改机构
     */
    @ApiOperation(value = "修改机构", notes = "修改机构信息")
    @RequiresPermissions("system:org:edit")
    @Log(title = "机构管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Organization organization) {
        Organization existingOrg = organizationService.selectOrganizationById(organization.getId());
        if (existingOrg == null) {
            return AjaxResult.error("修改机构'" + organization.getOrgName() + "'失败，机构不存在");
        }

        // 如果更改了机构编码，需要校验唯一性
        if (!existingOrg.getOrgId().equals(organization.getOrgId()) &&
                !organizationService.checkOrgIdUnique(organization.getOrgId())) {
            return AjaxResult.error("修改机构'" + organization.getOrgName() + "'失败，机构编码已存在");
        }

        // 如果更改了机构名称，需要校验唯一性
        if (!existingOrg.getOrgName().equals(organization.getOrgName()) &&
                !organizationService.checkOrgNameUnique(organization.getOrgName())) {
            return AjaxResult.error("修改机构'" + organization.getOrgName() + "'失败，机构名称已存在");
        }

        return toAjax(organizationService.updateOrganization(organization));
    }

    /**
     * 删除机构
     */
    @ApiOperation(value = "删除机构", notes = "根据机构ID删除机构信息")
    @RequiresPermissions("system:org:remove")
    @Log(title = "机构管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@ApiParam(value = "机构ID集合", required = true, example = "1,2,3") @PathVariable Long[] ids) {
        return toAjax(organizationService.deleteOrganizationByIds(ids));
    }

    /**
     * 校验机构编码
     */
    @ApiOperation(value = "校验机构编码", notes = "校验机构编码是否唯一")
    @GetMapping("/checkOrgIdUnique/{orgId}")
    public AjaxResult checkOrgIdUnique(@ApiParam(value = "机构编码", required = true, example = "1001") @PathVariable("orgId") String orgId) {
        return AjaxResult.success(organizationService.checkOrgIdUnique(orgId));
    }

    /**
     * 校验机构名称
     */
    @ApiOperation(value = "校验机构名称", notes = "校验机构名称是否唯一")
    @GetMapping("/checkOrgNameUnique/{orgName}")
    public AjaxResult checkOrgNameUnique(@ApiParam(value = "机构名称", required = true, example = "总行") @PathVariable("orgName") String orgName) {
        return AjaxResult.success(organizationService.checkOrgNameUnique(orgName));
    }

    /**
     * 获取机构级别
     */
    @ApiOperation(value = "获取机构级别", notes = "根据机构编码获取机构级别")
    @GetMapping("/level/{orgId}")
    public AjaxResult getOrgLevel(@ApiParam(value = "机构编码", required = true, example = "1001") @PathVariable("orgId") String orgId) {
        return AjaxResult.success(organizationService.getOrgLevelByOrgId(orgId));
    }
}