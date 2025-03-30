package com.bank.duty.service.impl;

import com.bank.duty.entity.User;
import com.bank.duty.entity.UserRole;
import com.bank.duty.mapper.UserMapper;
import com.bank.duty.mapper.UserRoleMapper;
import com.bank.duty.service.UserService;
import com.bank.duty.common.utils.SecurityUtils;
import com.bank.duty.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public List<User> selectUserList(User user) {
        return userMapper.selectUserList(user);
    }

    @Override
    public User selectUserById(Long userId) {
        return userMapper.selectUserById(userId);
    }

    @Override
    public User selectUserByEoaName(String eoaName) {
        return userMapper.selectUserByEoaName(eoaName);
    }

    @Override
    @Transactional
    public int insertUser(User user) {
        // 设置创建人和修改人（这里假设已有当前登录用户信息的获取方法）
        Long currentUserId = SecurityUtils.getCurrentUserId();
        user.setCreator(currentUserId);
        user.setModifier(currentUserId);

        // 新增用户信息
        return userMapper.insertUser(user);
    }

    @Override
    @Transactional
    public int updateUser(User user) {
        // 设置修改人
        user.setModifier(SecurityUtils.getCurrentUserId());

        return userMapper.updateUser(user);
    }

    @Override
    @Transactional
    public int deleteUserById(Long userId) {
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);

        // 删除用户
        return userMapper.deleteUserById(userId);
    }

    @Override
    @Transactional
    public int deleteUserByIds(Long[] userIds) {
        for (Long userId : userIds) {
            // 删除用户与角色关联
            userRoleMapper.deleteUserRoleByUserId(userId);
        }

        // 删除用户
        return userMapper.deleteUserByIds(userIds);
    }

    @Override
    public List<User> selectUserByOrgId(String orgId) {
        return userMapper.selectUserByOrgId(orgId);
    }

    @Override
    public List<User> selectUserByLine(String line) {
        return userMapper.selectUserByLine(line);
    }

    @Override
    @Transactional
    public String importUser(List<User> userList, boolean updateSupport) {
        if (userList == null || userList.isEmpty()) {
            throw new BusinessException("导入用户数据不能为空！");
        }

        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();

        for (User user : userList) {
            try {
                // 验证是否存在这个用户
                User u = userMapper.selectUserByEoaName(user.getEoaName());

                if (u == null) {
                    // 新增
                    insertUser(user);
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、账号 ").append(user.getEoaName()).append(" 导入成功");
                } else if (updateSupport) {
                    // 更新
                    user.setId(u.getId());
                    updateUser(user);
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、账号 ").append(user.getEoaName()).append(" 更新成功");
                } else {
                    failureNum++;
                    failureMsg.append("<br/>").append(failureNum).append("、账号 ").append(user.getEoaName()).append(" 已存在");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、账号 " + user.getEoaName() + " 导入失败：";
                failureMsg.append(msg).append(e.getMessage());
            }
        }

        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new BusinessException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }

        return successMsg.toString();
    }
}