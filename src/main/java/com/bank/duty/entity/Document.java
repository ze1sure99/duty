package com.bank.duty.entity;

import java.util.Date;
import java.io.Serializable;

import lombok.Data;

/**
 * 制度文件实体类
 */
@Data
public class Document implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 文档标题 */
    private String docTitle;

    /** 文档描述 */
    private String docDesc;

    /** 业务条线（办公室、纪委、党廉） */
    private String line;

    /** 文件名 */
    private String fileName;

    /** 文件路径 */
    private String filePath;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 文件类型 */
    private String fileType;

    /** 状态（0-正常，1-禁用） */
    private String status;

    /** 创建人主键id */
    private Long creator;

    /** 编辑人主键id */
    private Long modifier;

    /** 创建时间 */
    private Date createTime;

    /** 修改时间 */
    private Date updateTime;
}