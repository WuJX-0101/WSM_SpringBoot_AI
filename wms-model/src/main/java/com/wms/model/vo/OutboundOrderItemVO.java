package com.wms.model.vo;

import com.wms.model.entity.WmsOutboundOrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 出库单明细视图对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "出库单明细视图对象")
public class OutboundOrderItemVO extends WmsOutboundOrderItem {

    @Schema(description = "商品编码")
    private String productCode;

    @Schema(description = "商品名称")
    private String productName;

    @Schema(description = "库位编码")
    private String locationCode;

    @Schema(description = "库位名称")
    private String locationName;
}
