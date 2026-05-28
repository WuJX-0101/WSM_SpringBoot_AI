package com.wms.service;

import com.wms.common.core.PageResult;
import com.wms.model.dto.LocationDTO;
import com.wms.model.entity.WmsLocation;
import com.wms.model.vo.LocationVO;

import java.util.List;

/**
 * 库位服务接口
 */
public interface LocationService {

    /**
     * 创建库位
     */
    WmsLocation create(LocationDTO dto);

    /**
     * 更新库位
     */
    WmsLocation update(Long id, LocationDTO dto);

    /**
     * 删除库位
     */
    void delete(Long id);

    /**
     * 根据ID查询库位
     */
    WmsLocation getById(Long id);

    /**
     * 分页查询库位列表
     */
    PageResult<LocationVO> list(int page, int size, String keyword, Long warehouseId);

    /**
     * 查询仓库下的库位列表
     */
    List<WmsLocation> listByWarehouseId(Long warehouseId);
}
