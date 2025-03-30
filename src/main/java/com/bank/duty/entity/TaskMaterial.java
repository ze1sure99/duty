package com.bank.duty.entity;

import java.util.Date;
import java.io.Serializable;

import lombok.Data;

/**
 * 任务材料实体类
 */
@Data
public class TaskMaterial implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 任务id */
    private Long taskId;

    /** 文件名 */
    private String fileName;

    /** 文件路径 */
    private String filePath;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 文件类型 */
    private String fileType;

    /** 上传用户id */
    private Long uploadUserId;

    /** 上传用户姓名 */
    private String uploadUserName;

    /** 上传机构id */
    private String uploadOrgId;

    /** 上传时间 */
    private Date uploadTime;

    /** 是否确认（0-否，1-是） */
    private Boolean isConfirmed;

    /** 确认时间 */
    private Date confirmTime;

    /** 创建人主键id */
    private Long creator;

    /** 编辑人主键id */
    private Long modifier;

    /** 创建时间 */
    private Date createTime;

    /** 修改时间 */
    private Date updateTime;
}