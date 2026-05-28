package com.wms.model.vo;

import com.wms.model.entity.WmsOutboundOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 出库单视图对象
 *
 * 继承出库单实体，包含关联名称字段和明细列表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "出库单视图对象")
public class OutboundOrderVO extends WmsOutboundOrder {

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "仓库名称")
    private String warehouseName;

    @Schema(description = "出库单明细列表")
    private List<OutboundOrderItemVO> items;
}
