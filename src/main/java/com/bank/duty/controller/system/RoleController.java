package com.bank.duty.controller.system;

import com.bank.duty.entity.Role;
import com.bank.duty.service.RoleService;
import com.bank.duty.framework.web.domain.AjaxResult;
import com.bank.duty.common.annotation.DataScope;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 */
@Api(tags = "角色管理", description = "角色管理接口")
@RestController
@RequestMapping("/system/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 获取角色列表
     */
    @ApiOperation(value = "获取角色列表", notes = "获取所有角色信息")
    @RequiresPermissions("system:role:list")
    @GetMapping("/list")
    @DataScope(orgAlias = "r")
    public AjaxResult list(Role role) {
        List<Role> list = roleService.selectRoleList(role);
        return AjaxResult.success(list);
    }

    /**
     * 根据角色ID获取详细信息
     */
    @ApiOperation(value = "获取角色详情", notes = "根据角色ID查询角色详细信息")
    @RequiresPermissions("system:role:query")
    @GetMapping(value = "/{roleId}")
    public AjaxResult getInfo(@ApiParam(value = "角色ID", required = true, example = "1") @PathVariable Long roleId) {
        return AjaxResult.success(roleService.selectRoleById(roleId));
    }

    /**
     * 新增角色
     */
    @ApiOperation(value = "新增角色", notes = "新增角色信息")
    @RequiresPermissions("system:role:add")
    @PostMapping
    public AjaxResult add(@ApiParam(value = "角色信息", required = true) @RequestBody Role role) {
        if (!roleService.checkRoleNameUnique(role)) {
            return AjaxResult.error("新增角色'" + role.getEoaName() + "'失败，角色名称已存在");
        }
        return toAjax(roleService.insertRole(role));
    }

    /**
     * 修改角色
     */
    @ApiOperation(value = "修改角色", notes = "修改角色信息")
    @RequiresPermissions("system:role:edit")
    @PutMapping
    public AjaxResult edit(@ApiParam(value = "角色信息", required = true) @RequestBody Role role) {
        if (!roleService.checkRoleNameUnique(role)) {
            return AjaxResult.error("修改角色'" + role.getEoaName() + "'失败，角色名称已存在");
        }
        return toAjax(roleService.updateRole(role));
    }

    /**
     * 删除角色
     */
    @ApiOperation(value = "删除角色", notes = "根据角色ID数组批量删除角色")
    @RequiresPermissions("system:role:remove")
    @DeleteMapping("/{roleIds}")
    public AjaxResult remove(@ApiParam(value = "角色ID数组", required = true, example = "1,2,3") @PathVariable Long[] roleIds) {
        return toAjax(roleService.deleteRoleByIds(roleIds));
    }

    /**
     * 获取角色选择框列表
     */
    @ApiOperation(value = "获取角色选择列表", notes = "获取所有角色，用于下拉选择框")
    @GetMapping("/optionselect")
    @DataScope(orgAlias = "r")
    public AjaxResult optionselect() {
        return AjaxResult.success(roleService.selectRoleAll());
    }

    /**
     * 取消授权用户角色
     */
    @ApiOperation(value = "取消用户角色授权", notes = "取消指定用户的指定角色授权")
    @RequiresPermissions("system:role:edit")
    @PutMapping("/authUser/cancel")
    public AjaxResult cancelAuthUser(
            @ApiParam(value = "用户ID", required = true, example = "1") Long userId,
            @ApiParam(value = "角色ID", required = true, example = "1") Long roleId) {
        return toAjax(roleService.deleteUserRoleInfo(userId, roleId));
    }

    /**
     * 批量取消授权用户角色
     */
    @ApiOperation(value = "批量取消用户角色授权", notes = "批量取消多个用户的指定角色授权")
    @RequiresPermissions("system:role:edit")
    @PutMapping("/authUser/cancelAll")
    public AjaxResult cancelAuthUserAll(
            @ApiParam(value = "角色ID", required = true, example = "1") Long roleId,
            @ApiParam(value = "用户ID数组", required = true, example = "1,2,3") Long[] userIds) {
        return toAjax(roleService.deleteUserRoleInfos(roleId, userIds));
    }

    /**
     * 响应返回结果
     *
     * @param rows 影响行数
     * @return 操作结果
     */
    private AjaxResult toAjax(int rows) {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }
}