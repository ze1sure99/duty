package com.bank.duty.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.duty.common.annotation.DataScope;
import com.bank.duty.common.exception.BusinessException;
import com.bank.duty.common.utils.SecurityUtils;
import com.bank.duty.common.utils.StringUtil;
import com.bank.duty.entity.Organization;
import com.bank.duty.entity.User;
import com.bank.duty.framework.web.domain.TreeSelect;
import com.bank.duty.mapper.OrganizationMapper;
import com.bank.duty.service.OrganizationService;
import com.bank.duty.service.RoleService;
import com.bank.duty.service.UserService;

/**
 * 机构管理 服务实现
 */
@Service
public class OrganizationServiceImpl implements OrganizationService {

    @Autowired
    private OrganizationMapper organizationMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    /**
     * 查询机构管理数据
     *
     * @param organization 机构信息
     * @return 机构信息集合
     */
    @Override
    @DataScope(orgAlias = "o")
    public List<Organization> selectOrganizationList(Organization organization) {
        return organizationMapper.selectOrganizationList(organization);
    }

    /**
     * 查询机构信息
     *
     * @param id 机构ID
     * @return 机构信息
     */
    @Override
    public Organization selectOrganizationById(Long id) {
        return organizationMapper.selectOrganizationById(id);
    }

    /**
     * 根据机构编码查询机构
     *
     * @param orgId 机构编码
     * @return 机构信息
     */
    @Override
    public Organization selectOrganizationByOrgId(String orgId) {
        return organizationMapper.selectOrganizationByOrgId(orgId);
    }

    /**
     * 查询子机构列表
     *
     * @param pOrgId 父机构ID
     * @return 子机构集合
     */
    @Override
    public List<Organization> selectChildrenOrganizationByPOrgId(String pOrgId) {
        return organizationMapper.selectChildrenOrganizationByPOrgId(pOrgId);
    }

    /**
     * 构建前端所需要树结构
     *
     * @param orgs 机构列表
     * @return 树结构列表
     */
    @Override
    public List<Organization> buildOrgTree(List<Organization> orgs) {
        if (orgs == null || orgs.isEmpty()) {
            return new ArrayList<>();
        }

        // 构建orgId到Organization对象的映射
        Map<String, Organization> orgMap = new HashMap<>();
        for (Organization org : orgs) {
            // 创建新对象，避免引用相同对象
            Organization newOrg = new Organization();
            // 复制属性
            BeanUtils.copyProperties(org, newOrg);
            // 重置children，避免已有数据干扰
            newOrg.setChildren(new ArrayList<>());
            orgMap.put(newOrg.getOrgId(), newOrg);
        }

        // 构建返回的树形结构
        List<Organization> returnList = new ArrayList<>();

        // 构建父子关系
        for (Organization org : orgMap.values()) {
            String pOrgId = org.getPOrgId();
            // 如果是顶级节点或父节点不在当前数据集中
            if (pOrgId == null || pOrgId.isEmpty() || "0".equals(pOrgId) || !orgMap.containsKey(pOrgId)) {
                returnList.add(org);
            } else {
                // 添加到父节点的children列表
                Organization parentOrg = orgMap.get(pOrgId);
                parentOrg.getChildren().add(org);
            }
        }

        // 如果返回列表为空但有机构数据，返回所有机构(平铺结构)
        if (returnList.isEmpty() && !orgMap.isEmpty()) {
            List<Organization> flatList = new ArrayList<>(orgMap.values());
            // 对平铺结构排序
            sortOrganizationsByOrderNum(flatList);
            return flatList;
        }

        // 对顶级节点排序
        sortOrganizationsByOrderNum(returnList);

        // 对所有节点的子节点进行排序
        sortChildrenRecursively(returnList);

        return returnList;
    }

    /**
     * 按 orderNum 排序机构列表
     */
    private void sortOrganizationsByOrderNum(List<Organization> orgs) {
        if (orgs != null && orgs.size() > 1) {
            orgs.sort(Comparator.comparing(Organization::getOrderNum, Comparator.nullsLast(Comparator.naturalOrder())));
        }
    }

    /**
     * 递归排序所有子节点
     */
    private void sortChildrenRecursively(List<Organization> orgs) {
        if (orgs == null || orgs.isEmpty()) {
            return;
        }

        for (Organization org : orgs) {
            List<Organization> children = org.getChildren();
            if (children != null && !children.isEmpty()) {
                // 排序当前层级的子节点
                sortOrganizationsByOrderNum(children);
                // 递归排序下一层级
                sortChildrenRecursively(children);
            }
        }
    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param orgs 机构列表
     * @return 下拉树结构列表
     */
    @Override
    public List<TreeSelect> buildOrgTreeSelect(List<Organization> orgs) {
        List<Organization> orgTrees = buildOrgTree(orgs);
        return orgTrees.stream()
                .map(org -> new TreeSelect(org))  // 使用lambda表达式替代方法引用
                .collect(Collectors.toList());
    }

    /**
     * 根据角色ID查询机构树信息
     *
     * @param roleId 角色ID
     * @return 选中机构列表
     */
    @Override
    public List<String> selectOrgListByRoleId(Long roleId) {
        return organizationMapper.selectOrgListByRoleId(roleId);
    }

    /**
     * 根据用户ID查询机构树信息
     *
     * @param userId 用户ID
     * @return 机构树列表
     */
    @Override
    public List<Organization> selectOrgTreeByUserId(Long userId) {
        // 获取用户信息
        User user = userService.selectUserById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 获取用户角色值
        List<String> roleValues = roleService.selectRoleValuesByUserId(userId);

        // 判断是否为超级管理员
        boolean isSuperAdmin = roleValues.contains("1");

        List<Organization> orgs;

        if (isSuperAdmin) {
            // 超级管理员查看所有机构
            orgs = organizationMapper.selectOrganizationList(new Organization());
        } else {
            // 系统管理员和操作员查看自己机构及下级
            String orgId = user.getOrgId();

            // 先获取自己所属机构
            Organization selfOrg = organizationMapper.selectOrganizationByOrgId(orgId);

            // 获取所有机构列表，后面通过树形构建筛选
            List<Organization> allOrgs = new ArrayList<>();
            if (selfOrg != null) {
                allOrgs.add(selfOrg);
                // 递归获取所有层级的子机构
                getAllChildOrgsRecursively(orgId, allOrgs);
            }

            orgs = allOrgs;
        }

        // 构建树结构
        return buildOrgTree(orgs);
    }

    /**
     * 递归获取所有层级的子机构
     */
    private void getAllChildOrgsRecursively(String parentOrgId, List<Organization> orgList) {
        // 直接获取下一级子机构（已按orderNum排序）
        List<Organization> children = organizationMapper.selectChildrenOrganizationByPOrgId(parentOrgId);
        if (children != null && !children.isEmpty()) {
            orgList.addAll(children);
            // 递归获取每个子机构的下级机构
            for (Organization child : children) {
                getAllChildOrgsRecursively(child.getOrgId(), orgList);
            }
        }
    }

    /**
     * 根据用户ID查询机构下拉树结构
     *
     * @param userId 用户ID
     * @return 下拉树结构列表
     */
    @Override
    public List<TreeSelect> selectOrgTreeSelectByUserId(Long userId) {
        List<Organization> orgs = selectOrgTreeByUserId(userId);
        return orgs.stream()
                .map(org -> new TreeSelect(org))  // 使用lambda表达式替代方法引用
                .collect(Collectors.toList());
    }

    /**
     * 新增机构信息
     *
     * @param organization 机构信息
     * @return 结果
     */
    @Override
    @Transactional
    public int insertOrganization(Organization organization) {
        // 设置创建人和修改人
        organization.setCreator(SecurityUtils.getCurrentUserId());
        organization.setModifier(SecurityUtils.getCurrentUserId());

        return organizationMapper.insertOrganization(organization);
    }

    /**
     * 修改机构信息
     *
     * @param organization 机构信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateOrganization(Organization organization) {
        // 设置修改人
        organization.setModifier(SecurityUtils.getCurrentUserId());

        return organizationMapper.updateOrganization(organization);
    }

    /**
     * 删除机构管理信息
     *
     * @param id 机构ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteOrganizationById(Long id) {
        Organization org = organizationMapper.selectOrganizationById(id);
        if (org == null) {
            throw new BusinessException("机构不存在");
        }

        // 判断是否存在子机构
        List<Organization> children = organizationMapper.selectChildrenOrganizationByPOrgId(org.getOrgId());
        if (children != null && !children.isEmpty()) {
            throw new BusinessException(String.format("存在子机构,不允许删除"));
        }

        // 判断是否存在用户
        if (organizationMapper.checkOrgExistUser(org.getOrgId()) > 0) {
            throw new BusinessException(String.format("机构存在用户,不允许删除"));
        }

        return organizationMapper.deleteOrganizationById(id);
    }

    /**
     * 批量删除机构管理信息
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteOrganizationByIds(Long[] ids) {
        for (Long id : ids) {
            deleteOrganizationById(id);
        }
        return ids.length;
    }

    /**
     * 校验机构编码是否唯一
     *
     * @param orgId 机构编码
     * @return 结果
     */
    @Override
    public boolean checkOrgIdUnique(String orgId) {
        return organizationMapper.checkOrgIdUnique(orgId) == 0;
    }

    /**
     * 校验机构名称是否唯一
     *
     * @param orgName 机构名称
     * @return 结果
     */
    @Override
    public boolean checkOrgNameUnique(String orgName) {
        return organizationMapper.checkOrgNameUnique(orgName) == 0;
    }

    /**
     * 根据机构ID获取机构层级
     *
     * @param orgId 机构ID
     * @return 机构层级
     */
    @Override
    public String getOrgLevelByOrgId(String orgId) {
        return organizationMapper.getOrgLevelByOrgId(orgId);
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<Organization> list, Organization t) {
        // 得到子节点列表
        List<Organization> childList = getChildList(list, t);
        t.setChildren(childList);
        for (Organization tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<Organization> getChildList(List<Organization> list, Organization t) {
        List<Organization> tlist = new ArrayList<>();
        for (Organization n : list) {
            if (StringUtil.isNotEmpty(n.getPOrgId()) && n.getPOrgId().equals(t.getOrgId())) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<Organization> list, Organization t) {
        return getChildList(list, t).size() > 0;
    }
}
