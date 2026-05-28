package com.wms.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 出库单请求参数DTO
 */
@Data
@Schema(description = "出库单请求参数")
public class OutboundOrderDTO {

    @Schema(description = "出库单号（自动生成）", example = "OUT20240101001")
    private String orderNo;

    @Schema(description = "出库类型（1：销售出库，2：退货出库，3：调拨出库）", example = "1")
    private Integer orderType;

    @NotNull(message = "仓库ID不能为空")
    @Schema(description = "仓库ID", example = "1")
    private Long warehouseId;

    @Schema(description = "客户ID（销售出库时使用）", example = "1")
    private Long customerId;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "出库单明细列表")
    private List<OutboundOrderItemDTO> items;

    /**
     * 出库单明细DTO
     */
    @Data
    @Schema(description = "出库单明细请求参数")
    public static class OutboundOrderItemDTO {

        @NotNull(message = "商品ID不能为空")
        @Schema(description = "商品ID", example = "1")
        private Long productId;

        @Schema(description = "库位ID", example = "1")
        private Long locationId;

        @Schema(description = "批次号", example = "BATCH001")
        private String batchNo;

        @NotNull(message = "数量不能为空")
        @Schema(description = "数量", example = "50")
        private Integer quantity;

        @Schema(description = "单价", example = "80.00")
        private BigDecimal price;
    }
}
