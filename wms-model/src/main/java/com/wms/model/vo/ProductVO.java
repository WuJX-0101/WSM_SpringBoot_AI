package com.wms.model.vo;

import com.wms.model.entity.WmsProduct;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品视图对象
 * 
 * 继承商品实体，包含关联名称字段
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品视图对象")
public class ProductVO extends WmsProduct {

    @Schema(description = "分类名称")
    private String categoryName;
}
