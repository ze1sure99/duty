package com.bank.duty.service;

import com.bank.duty.entity.Role;
import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService {
    /**
     * 查询角色列表
     */
    List<Role> selectRoleList(Role role);

    /**
     * 根据用户ID查询角色
     */
    List<Role> selectRolesByUserId(Long userId);

    /**
     * 根据用户ID查询角色值列表
     * 这个方法返回的是角色的role字段值列表，如["1", "2"]
     */
    List<String> selectRoleValuesByUserId(Long userId);

    /**
     * 查询所有角色
     */
    List<Role> selectRoleAll();

    /**
     * 通过角色ID查询角色
     */
    Role selectRoleById(Long roleId);


    /**
     * 校验角色名称是否唯一
     */
    boolean checkRoleNameUnique(Role role);

    /**
     * 新增保存角色信息
     */
    int insertRole(Role role);

    /**
     * 修改保存角色信息
     */
    int updateRole(Role role);

    /**
     * 批量删除角色信息
     */
    int deleteRoleByIds(Long[] roleIds);

    /**
     * 授权用户角色
     */
    int insertUserRole(Long userId, Long[] roleIds);

    /**
     * 取消授权用户角色
     */
    int deleteUserRoleInfo(Long userId, Long roleId);

    /**
     * 批量取消授权用户角色
     */
    int deleteUserRoleInfos(Long roleId, Long[] userIds);
}
