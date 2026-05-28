package com.wms.dao.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 自动填充处理器
 * 
 * 功能：在插入和更新记录时自动填充公共字段
 * - gmtCreate: 创建时间（仅插入时填充）
 * - gmtModified: 修改时间（插入和更新时填充）
 * - isDeleted: 逻辑删除标记（仅插入时填充，默认值0表示未删除）
 * - createBy: 创建人（仅插入时填充）
 * - updateBy: 更新人（插入和更新时填充）
 * 
 * 使用方式：在实体类字段上添加 @TableField(fill = FieldFill.INSERT) 注解
 */
@Slf4j
@Component
public class MybatisPlusMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时自动填充
     * strictInsertFill: 严格填充（只有字段值为null时才填充）
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "gmtCreate", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "gmtModified", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "isDeleted", Integer.class, 0);
        this.strictInsertFill(metaObject, "createBy", String.class, "system");
        this.strictInsertFill(metaObject, "updateBy", String.class, "system");
    }

    /**
     * 更新时自动填充
     * strictUpdateFill: 严格填充（只有字段值为null时才填充）
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "gmtModified", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "updateBy", String.class, "system");
    }
}
