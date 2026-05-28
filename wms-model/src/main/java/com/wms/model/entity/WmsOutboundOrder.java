package com.wms.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 出库单实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_outbound_order")
public class WmsOutboundOrder extends BaseEntity {

    /** 出库单号 */
    @TableField("order_no")
    private String orderNo;

    /** 出库类型（1：销售出库，2：退货出库，3：调拨出库） */
    @TableField("order_type")
    private Integer orderType;

    /** 仓库ID */
    @TableField("warehouse_id")
    private Long warehouseId;

    /** 客户ID（销售出库时使用） */
    @TableField("customer_id")
    private Long customerId;

    /** 订单状态（0：待审核，1：已审核，2：已出库，3：已取消） */
    @TableField("order_status")
    private Integer orderStatus;

    /** 总数量 */
    @TableField("total_quantity")
    private Integer totalQuantity;

    /** 总金额 */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /** 出库时间 */
    @TableField("outbound_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime outboundTime;

    /** 备注 */
    @TableField("remark")
    private String remark;
}
