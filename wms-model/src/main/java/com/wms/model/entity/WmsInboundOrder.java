package com.wms.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 入库单实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_inbound_order")
public class WmsInboundOrder extends BaseEntity {

    /** 入库单号 */
    @TableField("order_no")
    private String orderNo;

    /** 入库类型（1：采购入库，2：退货入库，3：调拨入库） */
    @TableField("order_type")
    private Integer orderType;

    /** 仓库ID */
    @TableField("warehouse_id")
    private Long warehouseId;

    /** 供应商ID（采购入库时使用） */
    @TableField("supplier_id")
    private Long supplierId;

    /** 订单状态（0：待审核，1：已审核，2：已入库，3：已取消） */
    @TableField("order_status")
    private Integer orderStatus;

    /** 总数量 */
    @TableField("total_quantity")
    private Integer totalQuantity;

    /** 总金额 */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /** 入库时间 */
    @TableField("inbound_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime inboundTime;

    /** 备注 */
    @TableField("remark")
    private String remark;
}
