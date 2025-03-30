package com.bank.duty.framework.web.page;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.bank.duty.framework.web.domain.BaseQuery;

import java.util.List;

/**
 * 分页查询辅助类
 */
public class PageSupport {

    /**
     * 开始分页
     *
     * @param query 查询参数
     */
    public static void startPage(BaseQuery query) {
        if (query != null) {
            Integer pageNum = query.getPageNum();
            Integer pageSize = query.getPageSize();
            String orderByColumn = query.getOrderByColumn();
            String isAsc = query.getIsAsc();

            // 分页参数不为空，则进行分页
            if (pageNum != null && pageSize != null) {
                String orderBy = "";
                if (orderByColumn != null && !orderByColumn.isEmpty()) {
                    orderBy = orderByColumn + " " + (isAsc != null ? isAsc : "asc");
                }
                PageHelper.startPage(pageNum, pageSize, orderBy);
            }
        }
    }

    /**
     * 获取分页信息
     *
     * @param list 列表数据
     * @return 分页结果
     */
    public static <T> PageInfo<T> getPageInfo(List<T> list) {
        return new PageInfo<T>(list);
    }

    /**
     * 获取分页数据
     *
     * @param list 列表数据
     * @return 分页结果
     */
    public static <T> TableDataInfo getPageTable(List<T> list) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode("200");
        rspData.setRows(list);
        rspData.setMsg("查询成功");
        rspData.setTotal(new PageInfo(list).getTotal());
        return rspData;
    }
}