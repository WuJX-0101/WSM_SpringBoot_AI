package com.wms.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 库位请求参数DTO
 */
@Data
@Schema(description = "库位请求参数")
public class LocationDTO {

    @NotNull(message = "仓库ID不能为空")
    @Schema(description = "仓库ID", example = "1")
    private Long warehouseId;

    @NotBlank(message = "库位编码不能为空")
    @Schema(description = "库位编码", example = "A-01-01")
    private String locationCode;

    @Schema(description = "库位名称", example = "A区1排1层")
    private String locationName;

    @Schema(description = "库位类型（1：存储位，2：拣货位，3：暂存位）", example = "1")
    private Integer locationType;

    @Schema(description = "区域", example = "A区")
    private String area;

    @Schema(description = "货架", example = "A01")
    private String shelf;

    @Schema(description = "层", example = "1")
    private Integer layer;

    @Schema(description = "位置", example = "1")
    private Integer position;

    @Schema(description = "容量", example = "100")
    private BigDecimal capacity;
}
