package com.wms.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 首页统计数据视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "首页统计数据")
public class DashboardVO {

    @Schema(description = "商品总数")
    private Long productCount;

    @Schema(description = "仓库数量")
    private Long warehouseCount;

    @Schema(description = "今日入库单数")
    private Long todayInboundCount;

    @Schema(description = "今日出库单数")
    private Long todayOutboundCount;

    @Schema(description = "今日入库数量")
    private Integer todayInboundQuantity;

    @Schema(description = "今日出库数量")
    private Integer todayOutboundQuantity;

    @Schema(description = "库存总量")
    private Long totalInventoryQuantity;

    @Schema(description = "最近入库单列表")
    private List<RecentOrderVO> recentInboundOrders;

    @Schema(description = "最近出库单列表")
    private List<RecentOrderVO> recentOutboundOrders;

    /**
     * 最近订单视图对象
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "最近订单信息")
    public static class RecentOrderVO {

        @Schema(description = "订单ID")
        private Long id;

        @Schema(description = "订单号")
        private String orderNo;

        @Schema(description = "关联方名称（供应商/客户）")
        private String relatedName;

        @Schema(description = "仓库名称")
        private String warehouseName;

        @Schema(description = "总数量")
        private Integer totalQuantity;

        @Schema(description = "总金额")
        private java.math.BigDecimal totalAmount;

        @Schema(description = "订单状态（0：待审核，1：已审核，2：已完成，3：已取消）")
        private Integer orderStatus;

        @Schema(description = "创建时间")
        private String gmtCreate;
    }
}
