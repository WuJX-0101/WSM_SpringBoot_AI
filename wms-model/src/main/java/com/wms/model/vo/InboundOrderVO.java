package com.wms.model.vo;

import com.wms.model.entity.WmsInboundOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 入库单视图对象
 *
 * 继承入库单实体，包含关联名称字段和明细列表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "入库单视图对象")
public class InboundOrderVO extends WmsInboundOrder {

    @Schema(description = "供应商名称")
    private String supplierName;

    @Schema(description = "仓库名称")
    private String warehouseName;

    @Schema(description = "入库单明细列表")
    private List<InboundOrderItemVO> items;
}
