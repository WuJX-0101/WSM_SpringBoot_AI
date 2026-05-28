package com.wms.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实体基类
 * 
 * 所有数据库实体类都继承此类
 * 包含公共字段：id、创建时间、修改时间、逻辑删除标记、创建人、更新人
 * 
 * MyBatis-Plus注解说明:
 * - @TableId: 主键注解，type=ASSIGN_ID 表示雪花算法生成ID
 * - @TableField: 字段注解，fill 表示自动填充策略
 * - @TableLogic: 逻辑删除注解，删除时只更新isDeleted字段
 * 
 * 自动填充由 MybatisPlusMetaObjectHandler 处理
 */
@Data
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID（雪花算法生成，全局唯一） */
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)  // 解决前端JavaScript精度丢失问题
    private Long id;

    /** 创建时间（插入时自动填充） */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtCreate;

    /** 修改时间（插入和更新时自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtModified;

    /** 逻辑删除标记（0:未删除，1:已删除） */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer isDeleted;

    /** 创建人（插入时自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /** 更新人（插入和更新时自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;
}
