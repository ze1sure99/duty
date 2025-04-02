package com.bank.duty.mapper;

import com.bank.duty.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 角色数据层
 */
@Mapper
public interface RoleMapper {
    /**
     * 通过ID查询单个角色
     */
    Role selectRoleById(Long id);

    /**
     * 查询角色列表
     */
    List<Role> selectRoleList(Role role);

    /**
     * 新增角色
     */
    int insertRole(Role role);

    /**
     * 修改角色
     */
    int updateRole(Role role);

    /**
     * 删除角色
     */
    int deleteRoleById(Long id);

    /**
     * 批量删除角色
     */
    int deleteRoleByIds(Long[] ids);

    /**
     * 查询用户拥有的角色
     */
    List<Role> selectRolesByUserId(Long userId);

    /**
     * 根据用户ID查询角色值列表
     * 这个方法返回的是角色的role字段值列表，如["1", "2"]
     */
    List<String> selectRoleValuesByUserId(Long userId);
}