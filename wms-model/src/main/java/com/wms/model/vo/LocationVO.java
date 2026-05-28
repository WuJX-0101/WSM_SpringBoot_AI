package com.wms.model.vo;

import com.wms.model.entity.WmsLocation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 库位视图对象
 * 
 * 继承库位实体，包含关联名称字段
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "库位视图对象")
public class LocationVO extends WmsLocation {

    @Schema(description = "仓库名称")
    private String warehouseName;
}
