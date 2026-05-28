package com.wms.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 智能补货建议VO
 */
@Data
@Schema(description = "智能补货建议")
public class ReplenishmentVO {

    @Schema(description = "补货建议列表")
    private List<ReplenishmentItem> items;

    @Schema(description = "总补货数量")
    private Integer totalQuantity;

    @Schema(description = "总预计金额")
    private Double totalAmount;

    @Schema(description = "整体建议")
    private String overallSuggestion;

    @Data
    @Schema(description = "补货项")
    public static class ReplenishmentItem {

        @Schema(description = "商品ID")
        private Long productId;

        @Schema(description = "商品名称")
        private String productName;

        @Schema(description = "商品编码")
        private String productCode;

        @Schema(description = "当前库存")
        private Integer currentStock;

        @Schema(description = "安全库存")
        private Integer safetyStock;

        @Schema(description = "建议补货数量")
        private Integer replenishQuantity;

        @Schema(description = "建议供应商")
        private String suggestedSupplier;

        @Schema(description = "预计单价")
        private Double estimatedPrice;

        @Schema(description = "预计金额")
        private Double estimatedAmount;

        @Schema(description = "紧急程度（高/中/低）")
        private String urgency;

        @Schema(description = "补货原因")
        private String reason;
    }
}
