package com.bank.duty.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bank.duty.entity.Organization;
import com.bank.duty.mapper.OrganizationMapper;
import com.bank.duty.service.OrganizationService;
import com.bank.duty.common.utils.SecurityUtils;

/**
 * 机构管理 服务实现
 */
@Service
public class OrganizationServiceImpl implements OrganizationService {

    @Autowired
    private OrganizationMapper organizationMapper;

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
     * 查询机构列表
     *
     * @param organization 机构信息
     * @return 机构集合
     */
    @Override
    public List<Organization> selectOrganizationList(Organization organization) {
        return organizationMapper.selectOrganizationList(organization);
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
     * 新增机构
     *
     * @param organization 机构信息
     * @return 结果
     */
    @Override
    @Transactional
    public int insertOrganization(Organization organization) {
        // 设置创建者和修改者
        organization.setCreator(SecurityUtils.getCurrentUserId());
        organization.setModifier(SecurityUtils.getCurrentUserId());
        return organizationMapper.insertOrganization(organization);
    }

    /**
     * 修改机构
     *
     * @param organization 机构信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateOrganization(Organization organization) {
        // 设置修改者
        organization.setModifier(SecurityUtils.getCurrentUserId());
        return organizationMapper.updateOrganization(organization);
    }

    /**
     * 删除机构信息
     *
     * @param id 机构ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteOrganizationById(Long id) {
        return organizationMapper.deleteOrganizationById(id);
    }

    /**
     * 批量删除机构信息
     *
     * @param ids 需要删除的机构ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteOrganizationByIds(Long[] ids) {
        return organizationMapper.deleteOrganizationByIds(ids);
    }

    /**
     * 校验机构编码是否唯一
     *
     * @param orgId 机构编码
     * @return 结果
     */
    @Override
    public boolean checkOrgIdUnique(String orgId) {
        return organizationMapper.checkOrgIdUnique(orgId) <= 0;
    }

    /**
     * 校验机构名称是否唯一
     *
     * @param orgName 机构名称
     * @return 结果
     */
    @Override
    public boolean checkOrgNameUnique(String orgName) {
        return organizationMapper.checkOrgNameUnique(orgName) <= 0;
    }

    /**
     * 根据机构编码获取机构级别
     *
     * @param orgId 机构编码
     * @return 机构级别
     */
    @Override
    public String getOrgLevelByOrgId(String orgId) {
        Organization org = organizationMapper.selectOrganizationByOrgId(orgId);
        if (org != null) {
            return org.getOrgLevel();
        }
        return null;
    }
}