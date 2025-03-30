package com.bank.duty.service;

import com.bank.duty.entity.User;
import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {
    /**
     * 根据条件分页查询用户列表
     */
    List<User> selectUserList(User user);

    /**
     * 通过用户ID查询用户
     */
    User selectUserById(Long userId);

    /**
     * 通过用户名查询用户
     */
    User selectUserByEoaName(String eoaName);

    /**
     * 新增用户信息
     */
    int insertUser(User user);

    /**
     * 修改用户信息
     */
    int updateUser(User user);

    /**
     * 删除用户信息
     */
    int deleteUserById(Long userId);

    /**
     * 批量删除用户信息
     */
    int deleteUserByIds(Long[] userIds);

    /**
     * 根据机构ID查询用户列表
     */
    List<User> selectUserByOrgId(String orgId);

    /**
     * 根据条线查询用户列表
     */
    List<User> selectUserByLine(String line);

    /**
     * 导入用户数据
     */
    String importUser(List<User> userList, boolean updateSupport);
}