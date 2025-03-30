package com.bank.duty.controller.system;

import com.bank.duty.entity.User;
import com.bank.duty.service.UserService;
import com.bank.duty.service.RoleService;
import com.bank.duty.framework.web.domain.AjaxResult;
import com.bank.duty.common.annotation.DataScope;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户管理控制器
 */
@Api(tags = "用户管理", description = "用户管理接口")
@RestController
@RequestMapping("/system/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    /**
     * 获取用户列表
     */
    @ApiOperation(value = "获取用户列表", notes = "获取所有用户信息")
    @RequiresPermissions("system:user:list")
    @GetMapping("/list")
    @DataScope(orgAlias = "u", userAlias = "u")
    public AjaxResult list(User user) {
        List<User> list = userService.selectUserList(user);
        return AjaxResult.success(list);
    }

    /**
     * 根据用户ID获取详细信息
     */
    @ApiOperation(value = "获取用户详情", notes = "根据用户ID查询用户详细信息")
    @RequiresPermissions("system:user:query")
    @GetMapping(value = "/{userId}")
    public AjaxResult getInfo(@ApiParam(value = "用户ID", required = true) @PathVariable Long userId) {
        return AjaxResult.success(userService.selectUserById(userId));
    }

    /**
     * 新增用户
     */
    @ApiOperation(value = "新增用户", notes = "新增用户信息")
    @RequiresPermissions("system:user:add")
    @PostMapping
    public AjaxResult add(@ApiParam(value = "用户信息", required = true) @RequestBody User user) {
        return toAjax(userService.insertUser(user));
    }

    /**
     * 修改用户
     */
    @ApiOperation(value = "修改用户", notes = "修改用户信息")
    @RequiresPermissions("system:user:edit")
    @PutMapping
    public AjaxResult edit(@ApiParam(value = "用户信息", required = true) @RequestBody User user) {
        return toAjax(userService.updateUser(user));
    }

    /**
     * 删除用户
     */
    @ApiOperation(value = "删除用户", notes = "根据用户ID数组批量删除用户")
    @RequiresPermissions("system:user:remove")
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@ApiParam(value = "用户ID数组", required = true) @PathVariable Long[] userIds) {
        return toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 根据用户ID查询用户所属角色
     */
    @ApiOperation(value = "获取用户角色", notes = "根据用户ID查询用户所属角色")
    @RequiresPermissions("system:user:query")
    @GetMapping("/roles/{userId}")
    public AjaxResult getRolesByUserId(@ApiParam(value = "用户ID", required = true) @PathVariable Long userId) {
        return AjaxResult.success(roleService.selectRolesByUserId(userId));
    }

    /**
     * 用户授权角色
     */
    @ApiOperation(value = "用户授权角色", notes = "为用户分配角色")
    @RequiresPermissions("system:user:edit")
    @PutMapping("/authRole")
    public AjaxResult insertAuthRole(
            @ApiParam(value = "用户ID", required = true) Long userId,
            @ApiParam(value = "角色ID数组", required = true) Long[] roleIds) {
        return toAjax(roleService.insertUserRole(userId, roleIds));
    }

    /**
     * 根据条线查询用户列表
     */
    @ApiOperation(value = "按条线获取用户", notes = "根据条线查询用户列表")
    @RequiresPermissions("system:user:list")
    @GetMapping("/line/{line}")
    @DataScope(orgAlias = "u", userAlias = "u", lineParam = "u.line")
    public AjaxResult getUsersByLine(@ApiParam(value = "条线名称", required = true) @PathVariable String line) {
        return AjaxResult.success(userService.selectUserByLine(line));
    }

    /**
     * 根据机构ID查询用户列表
     */
    @ApiOperation(value = "按机构获取用户", notes = "根据机构ID查询用户列表")
    @RequiresPermissions("system:user:list")
    @GetMapping("/org/{orgId}")
    @DataScope(orgAlias = "u", userAlias = "u")
    public AjaxResult getUsersByOrgId(@ApiParam(value = "机构ID", required = true) @PathVariable String orgId) {
        return AjaxResult.success(userService.selectUserByOrgId(orgId));
    }

    /**
     * 导入用户数据
     */
    @ApiOperation(value = "导入用户数据", notes = "从Excel文件导入用户数据")
    @RequiresPermissions("system:user:import")
    @PostMapping("/importData")
    public AjaxResult importData(
            @ApiParam(value = "Excel文件", required = true) MultipartFile file,
            @ApiParam(value = "是否更新已存在的用户数据", required = true) boolean updateSupport) throws Exception {
        // 实际实现时需要从文件中解析出用户数据
        List<User> userList = null;
        String message = userService.importUser(userList, updateSupport);
        return AjaxResult.success(message);
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