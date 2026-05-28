package com.wms.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.model.entity.WmsInventory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存Mapper接口
 */
@Mapper
public interface WmsInventoryMapper extends BaseMapper<WmsInventory> {
}
