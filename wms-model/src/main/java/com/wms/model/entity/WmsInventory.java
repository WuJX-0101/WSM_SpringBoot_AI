package com.wms.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 库存实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_inventory")
public class WmsInventory extends BaseEntity {

    /** 仓库ID */
    @TableField("warehouse_id")
    private Long warehouseId;

    /** 库位ID */
    @TableField("location_id")
    private Long locationId;

    /** 商品ID */
    @TableField("product_id")
    private Long productId;

    /** 批次号 */
    @TableField("batch_no")
    private String batchNo;

    /** 库存数量 */
    @TableField("quantity")
    private Integer quantity;

    /** 锁定数量（已分配但未出库） */
    @TableField("locked_quantity")
    private Integer lockedQuantity;

    /** 可用数量（库存数量 - 锁定数量） */
    @TableField("available_quantity")
    private Integer availableQuantity;

    /** 成本价 */
    @TableField("cost_price")
    private BigDecimal costPrice;

    /** 生产日期 */
    @TableField("production_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate productionDate;

    /** 过期日期 */
    @TableField("expiry_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;
}
