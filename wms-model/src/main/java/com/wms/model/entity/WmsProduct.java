package com.wms.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_product")
public class WmsProduct extends BaseEntity {

    @TableField("product_code")
    private String productCode;

    @TableField("product_name")
    private String productName;

    @TableField("category_id")
    private Long categoryId;

    @TableField("brand")
    private String brand;

    @TableField("specification")
    private String specification;

    @TableField("unit")
    private String unit;

    @TableField("barcode")
    private String barcode;

    @TableField("weight")
    private BigDecimal weight;

    @TableField("volume")
    private BigDecimal volume;

    @TableField("purchase_price")
    private BigDecimal purchasePrice;

    @TableField("sale_price")
    private BigDecimal salePrice;

    @TableField("safety_stock")
    private Integer safetyStock;

    @TableField("min_stock")
    private Integer minStock;

    @TableField("max_stock")
    private Integer maxStock;

    @TableField("image")
    private String image;

    @TableField("status")
    private Integer status;

    @TableField("remark")
    private String remark;
}
