package com.bank.duty.mapper;

import com.bank.duty.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 用户角色关联数据层
 */
@Mapper
public interface UserRoleMapper {
    /**
     * 通过用户ID删除用户和角色关联
     */
    int deleteUserRoleByUserId(Long userId);

    /**
     * 批量删除用户和角色关联
     */
    int deleteUserRole(Long[] ids);

    /**
     * 通过角色ID查询角色使用数量
     */
    int countUserRoleByRoleId(Long roleId);

    /**
     * 批量新增用户角色信息
     */
    int batchUserRole(List<UserRole> userRoleList);

    /**
     * 删除用户和角色关联信息
     */
    int deleteUserRoleInfo(UserRole userRole);

    /**
     * 批量取消授权用户角色
     */
    int deleteUserRoleInfos(@Param("roleId") Long roleId, @Param("userIds") Long[] userIds);
}