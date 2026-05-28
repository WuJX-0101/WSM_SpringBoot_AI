package com.wms.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品请求参数DTO
 */
@Data
@Schema(description = "商品请求参数")
public class ProductDTO {

    @NotBlank(message = "商品编码不能为空")
    @Schema(description = "商品编码", example = "SP001")
    private String productCode;

    @NotBlank(message = "商品名称不能为空")
    @Schema(description = "商品名称", example = "iPhone 15")
    private String productName;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "品牌", example = "Apple")
    private String brand;

    @Schema(description = "规格", example = "256GB 黑色")
    private String specification;

    @Schema(description = "单位", example = "台")
    private String unit;

    @Schema(description = "条码", example = "6901234567890")
    private String barcode;

    @Schema(description = "重量（kg）", example = "0.5")
    private BigDecimal weight;

    @Schema(description = "体积（立方米）", example = "0.001")
    private BigDecimal volume;

    @Schema(description = "采购价", example = "5000")
    private BigDecimal purchasePrice;

    @Schema(description = "销售价", example = "6999")
    private BigDecimal salePrice;

    @Schema(description = "安全库存", example = "10")
    private Integer safetyStock;

    @Schema(description = "最小库存", example = "5")
    private Integer minStock;

    @Schema(description = "最大库存", example = "100")
    private Integer maxStock;

    @Schema(description = "商品图片")
    private String image;

    @Schema(description = "状态（1：启用，0：禁用）", example = "1")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
