package com.wms.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_location")
public class WmsLocation extends BaseEntity {

    @TableField("warehouse_id")
    private Long warehouseId;

    @TableField("location_code")
    private String locationCode;

    @TableField("location_name")
    private String locationName;

    @TableField("location_type")
    private Integer locationType;

    @TableField("area")
    private String area;

    @TableField("shelf")
    private String shelf;

    @TableField("layer")
    private Integer layer;

    @TableField("position")
    private Integer position;

    @TableField("capacity")
    private BigDecimal capacity;

    @TableField("status")
    private Integer status;
}
