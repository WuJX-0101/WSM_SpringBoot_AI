package com.wms.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 订单统计VO
 */
@Data
@Schema(description = "订单统计信息")
public class OrderStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "今日入库单数")
    private Long todayInbound;

    @Schema(description = "今日出库单数")
    private Long todayOutbound;

    @Schema(description = "库存总量")
    private Integer totalInventory;

    @Schema(description = "入库单总数")
    private Long inboundCount;

    @Schema(description = "待审核入库单数")
    private Long inboundPendingCount;

    @Schema(description = "已入库单数")
    private Long inboundCompletedCount;

    @Schema(description = "入库总数量")
    private Integer inboundTotalQuantity;

    @Schema(description = "入库总金额")
    private Double inboundTotalAmount;

    @Schema(description = "出库单总数")
    private Long outboundCount;

    @Schema(description = "待审核出库单数")
    private Long outboundPendingCount;

    @Schema(description = "已出库单数")
    private Long outboundCompletedCount;

    @Schema(description = "出库总数量")
    private Integer outboundTotalQuantity;

    @Schema(description = "出库总金额")
    private Double outboundTotalAmount;
}
