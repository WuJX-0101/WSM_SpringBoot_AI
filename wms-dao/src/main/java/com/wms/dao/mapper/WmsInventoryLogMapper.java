package com.wms.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.model.entity.WmsInventoryLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存变更日志Mapper接口
 */
@Mapper
public interface WmsInventoryLogMapper extends BaseMapper<WmsInventoryLog> {
}
