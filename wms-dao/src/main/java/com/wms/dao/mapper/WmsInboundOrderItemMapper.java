package com.wms.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.model.entity.WmsInboundOrderItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 入库单明细Mapper接口
 */
@Mapper
public interface WmsInboundOrderItemMapper extends BaseMapper<WmsInboundOrderItem> {
}
