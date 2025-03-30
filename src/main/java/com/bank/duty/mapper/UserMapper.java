package com.bank.duty.mapper;

import com.bank.duty.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 用户数据层
 */
@Mapper
public interface UserMapper {
    /**
     * 通过ID查询单个用户
     */
    User selectUserById(Long id);

    /**
     * 通过EOA用户名查询用户
     */
    User selectUserByEoaName(String eoaName);

    /**
     * 查询用户列表
     */
    List<User> selectUserList(User user);

    /**
     * 新增用户
     */
    int insertUser(User user);

    /**
     * 修改用户
     */
    int updateUser(User user);

    /**
     * 删除用户
     */
    int deleteUserById(Long id);

    /**
     * 批量删除用户
     */
    int deleteUserByIds(Long[] ids);

    /**
     * 根据机构ID查询用户列表
     */
    List<User> selectUserByOrgId(String orgId);

    /**
     * 根据条线查询用户列表
     */
    List<User> selectUserByLine(String line);
}