package com.wms.model.vo;

import com.wms.model.entity.WmsInventory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 库存视图对象
 * 
 * 继承库存实体，包含关联名称字段
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "库存视图对象")
public class InventoryVO extends WmsInventory {

    @Schema(description = "商品名称")
    private String productName;

    @Schema(description = "商品编码")
    private String productCode;

    @Schema(description = "仓库名称")
    private String warehouseName;

    @Schema(description = "库位编码")
    private String locationCode;
}
