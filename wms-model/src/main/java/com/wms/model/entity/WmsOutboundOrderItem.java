package com.wms.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 出库单明细实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_outbound_order_item")
public class WmsOutboundOrderItem extends BaseEntity {

    /** 出库单ID */
    @TableField("order_id")
    private Long orderId;

    /** 商品ID */
    @TableField("product_id")
    private Long productId;

    /** 库位ID */
    @TableField("location_id")
    private Long locationId;

    /** 批次号 */
    @TableField("batch_no")
    private String batchNo;

    /** 数量 */
    @TableField("quantity")
    private Integer quantity;

    /** 单价 */
    @TableField("price")
    private BigDecimal price;

    /** 金额 */
    @TableField("amount")
    private BigDecimal amount;
}
