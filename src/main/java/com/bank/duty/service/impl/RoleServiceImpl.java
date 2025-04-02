package com.bank.duty.service.impl;

import com.bank.duty.entity.Role;
import com.bank.duty.entity.UserRole;
import com.bank.duty.mapper.RoleMapper;
import com.bank.duty.mapper.UserRoleMapper;
import com.bank.duty.service.RoleService;
import com.bank.duty.common.utils.SecurityUtils;
import com.bank.duty.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * 角色服务实现类
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public List<Role> selectRoleList(Role role) {
        return roleMapper.selectRoleList(role);
    }

    @Override
    public List<Role> selectRolesByUserId(Long userId) {
        return roleMapper.selectRolesByUserId(userId);
    }

    @Override
    public List<String> selectRoleValuesByUserId(Long userId) {
        return roleMapper.selectRoleValuesByUserId(userId);
    }

    @Override
    public List<Role> selectRoleAll() {
        return roleMapper.selectRoleList(new Role());
    }

    @Override
    public Role selectRoleById(Long roleId) {
        return roleMapper.selectRoleById(roleId);
    }

    @Override
    public boolean checkRoleNameUnique(Role role) {
        Long roleId = role.getId() == null ? -1L : role.getId();
        Role info = roleMapper.selectRoleById(role.getId());
        if (info != null && info.getId().longValue() != roleId.longValue() &&
                info.getEoaName().equals(role.getEoaName())) {
            return false;
        }
        return true;
    }

    @Override
    @Transactional
    public int insertRole(Role role) {
        // 设置创建人和修改人
        Long currentUserId = SecurityUtils.getCurrentUserId();
        role.setCreator(currentUserId);
        role.setModifier(currentUserId);

        return roleMapper.insertRole(role);
    }

    @Override
    @Transactional
    public int updateRole(Role role) {
        // 设置修改人
        role.setModifier(SecurityUtils.getCurrentUserId());

        return roleMapper.updateRole(role);
    }

    @Override
    @Transactional
    public int deleteRoleByIds(Long[] roleIds) {
        for (Long roleId : roleIds) {
            // 检查角色是否已分配
            if (userRoleMapper.countUserRoleByRoleId(roleId) > 0) {
                throw new BusinessException(String.format("角色ID为%d的角色已分配，不能删除", roleId));
            }
        }

        return roleMapper.deleteRoleByIds(roleIds);
    }

    @Override
    @Transactional
    public int insertUserRole(Long userId, Long[] roleIds) {
        if (roleIds == null || roleIds.length == 0) {
            return 0;
        }

        // 先删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);

        // 新增用户与角色关联
        List<UserRole> list = new ArrayList<>();
        for (Long roleId : roleIds) {
            UserRole ur = new UserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            ur.setCreator(SecurityUtils.getCurrentUserId());
            ur.setModifier(SecurityUtils.getCurrentUserId());
            list.add(ur);
        }

        return userRoleMapper.batchUserRole(list);
    }

    @Override
    public int deleteUserRoleInfo(Long userId, Long roleId) {
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        return userRoleMapper.deleteUserRoleInfo(userRole);
    }

    @Override
    public int deleteUserRoleInfos(Long roleId, Long[] userIds) {
        return userRoleMapper.deleteUserRoleInfos(roleId, userIds);
    }
}