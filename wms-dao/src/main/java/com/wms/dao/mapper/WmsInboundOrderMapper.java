package com.wms.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.model.entity.WmsInboundOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 入库单Mapper接口
 */
@Mapper
public interface WmsInboundOrderMapper extends BaseMapper<WmsInboundOrder> {
}
