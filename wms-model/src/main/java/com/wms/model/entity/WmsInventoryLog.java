package com.wms.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 库存变更日志实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("wms_inventory_log")
public class WmsInventoryLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 仓库ID */
    @TableField("warehouse_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long warehouseId;

    /** 商品ID */
    @TableField("product_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long productId;

    /** 批次号 */
    @TableField("batch_no")
    private String batchNo;

    /** 变更类型（1：入库，2：出库，3：盘点，4：调整） */
    @TableField("change_type")
    private Integer changeType;

    /** 变更数量 */
    @TableField("change_quantity")
    private Integer changeQuantity;

    /** 变更前数量 */
    @TableField("before_quantity")
    private Integer beforeQuantity;

    /** 变更后数量 */
    @TableField("after_quantity")
    private Integer afterQuantity;

    /** 关联单号 */
    @TableField("order_no")
    private String orderNo;

    /** 备注 */
    @TableField("remark")
    private String remark;

    /** 创建时间 */
    @TableField("gmt_create")
    private LocalDateTime gmtCreate;

    /** 创建人 */
    @TableField("create_by")
    private String createBy;
}
