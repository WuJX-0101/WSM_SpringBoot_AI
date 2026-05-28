package com.wms.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.model.entity.WmsOutboundOrderItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 出库单明细Mapper接口
 */
@Mapper
public interface WmsOutboundOrderItemMapper extends BaseMapper<WmsOutboundOrderItem> {
}
