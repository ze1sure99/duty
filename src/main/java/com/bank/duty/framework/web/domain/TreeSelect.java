package com.bank.duty.framework.web.domain;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.bank.duty.entity.Organization;

/**
 * 树形下拉选择
 */
public class TreeSelect implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 节点ID */
    private String id;

    /** 节点名称 */
    private String label;

    /** 子节点 */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TreeSelect> children;

    public TreeSelect() {
    }

    // 添加接受Organization参数的构造函数
    public TreeSelect(Organization org) {
        this.id = org.getOrgId();
        this.label = org.getOrgName();
        if (org.getChildren() != null) {
            this.children = org.getChildren().stream()
                    .map(child -> new TreeSelect(child))
                    .collect(Collectors.toList());
        }
    }

    // Getter和Setter方法
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<TreeSelect> getChildren() {
        return children;
    }

    public void setChildren(List<TreeSelect> children) {
        this.children = children;
    }
}
