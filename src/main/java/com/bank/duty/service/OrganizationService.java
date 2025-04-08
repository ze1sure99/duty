package com.bank.duty.service;

import java.util.List;
import com.bank.duty.entity.Organization;
import com.bank.duty.framework.web.domain.TreeSelect;

/**
 * 机构管理 服务接口
 */
public interface OrganizationService {
    /**
     * 查询机构管理数据
     *
     * @param organization 机构信息
     * @return 机构信息集合
     */
    public List<Organization> selectOrganizationList(Organization organization);

    /**
     * 查询机构信息
     *
     * @param id 机构ID
     * @return 机构信息
     */
    public Organization selectOrganizationById(Long id);

    /**
     * 根据机构编码查询机构
     *
     * @param orgId 机构编码
     * @return 机构信息
     */
    public Organization selectOrganizationByOrgId(String orgId);

    /**
     * 查询子机构列表
     *
     * @param pOrgId 父机构ID
     * @return 子机构集合
     */
    public List<Organization> selectChildrenOrganizationByPOrgId(String pOrgId);

    /**
     * 构建前端所需要机构树结构
     *
     * @param orgs 机构列表
     * @return 树结构列表
     */
    public List<Organization> buildOrgTree(List<Organization> orgs);

    /**
     * 构建前端所需要下拉树结构
     *
     * @param orgs 机构列表
     * @return 下拉树结构列表
     */
    public List<TreeSelect> buildOrgTreeSelect(List<Organization> orgs);

    /**
     * 根据角色ID查询机构树信息
     *
     * @param roleId 角色ID
     * @return 选中机构列表
     */
    public List<String> selectOrgListByRoleId(Long roleId);

    /**
     * 根据用户ID查询机构树信息
     *
     * @param userId 用户ID
     * @return 机构树列表
     */
    public List<Organization> selectOrgTreeByUserId(Long userId);

    /**
     * 根据用户ID查询机构下拉树结构
     *
     * @param userId 用户ID
     * @return 下拉树结构列表
     */
    public List<TreeSelect> selectOrgTreeSelectByUserId(Long userId);

    /**
     * 新增机构信息
     *
     * @param organization 机构信息
     * @return 结果
     */
    public int insertOrganization(Organization organization);

    /**
     * 修改机构信息
     *
     * @param organization 机构信息
     * @return 结果
     */
    public int updateOrganization(Organization organization);

    /**
     * 删除机构管理信息
     *
     * @param id 机构ID
     * @return 结果
     */
    public int deleteOrganizationById(Long id);

    /**
     * 批量删除机构管理信息
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteOrganizationByIds(Long[] ids);

    /**
     * 校验机构编码是否唯一
     *
     * @param orgId 机构编码
     * @return 结果
     */
    public boolean checkOrgIdUnique(String orgId);

    /**
     * 校验机构名称是否唯一
     *
     * @param orgName 机构名称
     * @return 结果
     */
    public boolean checkOrgNameUnique(String orgName);

    /**
     * 根据机构ID获取机构层级
     *
     * @param orgId 机构ID
     * @return 机构层级
     */
    public String getOrgLevelByOrgId(String orgId);
}
