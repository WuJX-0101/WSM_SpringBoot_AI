package com.wms.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 入库单请求参数DTO
 */
@Data
@Schema(description = "入库单请求参数")
public class InboundOrderDTO {

    @Schema(description = "入库单号（自动生成）", example = "IN20240101001")
    private String orderNo;

    @Schema(description = "入库类型（1：采购入库，2：退货入库，3：调拨入库）", example = "1")
    private Integer orderType;

    @NotNull(message = "仓库ID不能为空")
    @Schema(description = "仓库ID", example = "1")
    private Long warehouseId;

    @Schema(description = "供应商ID（采购入库时使用）", example = "1")
    private Long supplierId;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "入库单明细列表")
    private List<InboundOrderItemDTO> items;

    /**
     * 入库单明细DTO
     */
    @Data
    @Schema(description = "入库单明细请求参数")
    public static class InboundOrderItemDTO {

        @NotNull(message = "商品ID不能为空")
        @Schema(description = "商品ID", example = "1")
        private Long productId;

        @Schema(description = "库位ID", example = "1")
        private Long locationId;

        @Schema(description = "批次号", example = "BATCH001")
        private String batchNo;

        @NotNull(message = "数量不能为空")
        @Schema(description = "数量", example = "100")
        private Integer quantity;

        @Schema(description = "单价", example = "50.00")
        private BigDecimal price;

        @Schema(description = "生产日期")
        private LocalDateTime productionDate;

        @Schema(description = "过期日期")
        private LocalDateTime expiryDate;
    }
}
