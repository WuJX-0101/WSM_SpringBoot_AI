package com.wms.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.model.entity.WmsProduct;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WmsProductMapper extends BaseMapper<WmsProduct> {
}
