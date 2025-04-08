package com.bank.duty.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

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
        List<Organization> returnList = new ArrayList<>();
        List<String> tempList = orgs.stream().map(Organization::getOrgId).collect(Collectors.toList());

        for (Organization org : orgs) {
            // 如果是顶级节点，遍历该父节点的所有子节点
            if (!tempList.contains(org.getPOrgId()) || "0".equals(org.getPOrgId()) || StringUtil.isEmpty(org.getPOrgId())) {
                recursionFn(orgs, org);
                returnList.add(org);
            }
        }

        if (returnList.isEmpty()) {
            returnList = orgs;
        }
        return returnList;
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

        // 初始化查询参数
        Organization orgParam = new Organization();

        // 判断是否为超级管理员
        boolean isSuperAdmin = roleValues.contains("1");

        if (!isSuperAdmin) {
            // 非超级管理员，设置数据权限
            StringBuilder dataScope = new StringBuilder();

            // 机构权限过滤：只能看到自己所属机构和下级机构
            String orgId = user.getOrgId();
            dataScope.append(String.format(
                    "(org_id = '%s' OR org_id IN (SELECT org_id FROM hnzrlz_org WHERE p_org_id = '%s'))",
                    orgId, orgId));

            // 条线权限过滤：只能看到本条线数据
            String line = user.getLine();
            if (StringUtil.isNotEmpty(line)) {
                dataScope.append(String.format(" AND line = '%s'", line));
            }

            orgParam.setDataScope(dataScope.toString());
        }

        // 获取机构列表
        List<Organization> orgs = organizationMapper.selectOrganizationList(orgParam);

        // 构建树结构
        return buildOrgTree(orgs);
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
