package com.wms.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 商品分类请求参数DTO
 */
@Data
@Schema(description = "商品分类请求参数")
public class CategoryDTO {

    @Schema(description = "父分类ID（0表示顶级分类）", example = "0")
    private Long parentId;

    @NotBlank(message = "分类名称不能为空")
    @Schema(description = "分类名称", example = "电子产品")
    private String categoryName;

    @NotBlank(message = "分类编码不能为空")
    @Schema(description = "分类编码", example = "ELEC")
    private String categoryCode;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

    @Schema(description = "状态（1：启用，0：禁用）", example = "1")
    private Integer status;
}
