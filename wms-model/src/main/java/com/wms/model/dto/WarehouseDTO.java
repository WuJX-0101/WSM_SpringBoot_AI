package com.wms.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "仓库请求参数")
public class WarehouseDTO {

    @NotBlank(message = "仓库编码不能为空")
    @Schema(description = "仓库编码", example = "WH001")
    private String warehouseCode;

    @NotBlank(message = "仓库名称不能为空")
    @Schema(description = "仓库名称", example = "主仓库")
    private String warehouseName;

    @Schema(description = "仓库类型（1：普通仓库，2：保税仓库，3：冷链仓库）", example = "1")
    private Integer warehouseType;

    @Schema(description = "仓库地址")
    private String address;

    @Schema(description = "联系人")
    private String contactPerson;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "仓库面积（平方米）")
    private BigDecimal area;

    @Schema(description = "状态（1：启用，0：禁用）", example = "1")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
