package com.bank.duty.mapper;

import java.util.List;
import com.bank.duty.entity.Organization;
import org.apache.ibatis.annotations.Mapper;

/**
 * 机构Mapper接口
 */
@Mapper
public interface OrganizationMapper {

    /**
     * 查询机构信息
     *
     * @param id 机构ID
     * @return 机构信息
     */
    Organization selectOrganizationById(Long id);

    /**
     * 查询机构列表
     *
     * @param organization 机构信息
     * @return 机构集合
     */
    List<Organization> selectOrganizationList(Organization organization);

    /**
     * 根据机构编码查询机构
     *
     * @param orgId 机构编码
     * @return 机构信息
     */
    Organization selectOrganizationByOrgId(String orgId);

    /**
     * 查询子机构列表
     *
     * @param pOrgId 父机构ID
     * @return 子机构集合
     */
    List<Organization> selectChildrenOrganizationByPOrgId(String pOrgId);

    /**
     * 新增机构
     *
     * @param organization 机构信息
     * @return 结果
     */
    int insertOrganization(Organization organization);

    /**
     * 修改机构
     *
     * @param organization 机构信息
     * @return 结果
     */
    int updateOrganization(Organization organization);

    /**
     * 删除机构
     *
     * @param id 机构ID
     * @return 结果
     */
    int deleteOrganizationById(Long id);

    /**
     * 批量删除机构
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteOrganizationByIds(Long[] ids);

    /**
     * 校验机构编码是否唯一
     *
     * @param orgId 机构编码
     * @return 结果
     */
    int checkOrgIdUnique(String orgId);

    /**
     * 校验机构名称是否唯一
     *
     * @param orgName 机构名称
     * @return 结果
     */
    int checkOrgNameUnique(String orgName);
}