package com.wms.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.model.entity.WmsOutboundOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 出库单Mapper接口
 */
@Mapper
public interface WmsOutboundOrderMapper extends BaseMapper<WmsOutboundOrder> {
}
