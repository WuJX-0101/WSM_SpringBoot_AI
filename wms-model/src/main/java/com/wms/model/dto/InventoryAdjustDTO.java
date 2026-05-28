package com.wms.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 库存调整请求参数DTO
 */
@Data
@Schema(description = "库存调整请求参数")
public class InventoryAdjustDTO {

    @NotNull(message = "调整数量不能为空")
    @Schema(description = "调整数量（正数为盘盈，负数为盘亏）", example = "10")
    private Integer quantity;

    @Schema(description = "备注", example = "盘点调整")
    private String reason;
}
