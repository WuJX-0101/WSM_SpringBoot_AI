package com.wms.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 库存变更日志视图对象
 */
@Data
@Schema(description = "库存变更日志视图对象")
public class InventoryLogVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "仓库ID")
    private Long warehouseId;

    @Schema(description = "仓库名称")
    private String warehouseName;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "商品编码")
    private String productCode;

    @Schema(description = "商品名称")
    private String productName;

    @Schema(description = "批次号")
    private String batchNo;

    @Schema(description = "变更类型（1：入库，2：出库，3：盘点，4：调整）")
    private Integer changeType;

    @Schema(description = "变更数量")
    private Integer changeQuantity;

    @Schema(description = "变更前数量")
    private Integer beforeQuantity;

    @Schema(description = "变更后数量")
    private Integer afterQuantity;

    @Schema(description = "关联单号")
    private String orderNo;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtCreate;

    @Schema(description = "创建人")
    private String createBy;
}
