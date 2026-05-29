package com.wms.service;

import com.wms.common.core.PageResult;
import com.wms.model.dto.WarehouseDTO;
import com.wms.model.entity.WmsWarehouse;

import java.util.List;

public interface WarehouseService {

    /**
     * 创建仓库
     */
    WmsWarehouse create(WarehouseDTO dto);

    /**
     * 更新仓库
     */
    WmsWarehouse update(Long id, WarehouseDTO dto);

    /**
     * 删除仓库
     */
    void delete(Long id);

    /**
     * 根据ID查询仓库
     */
    WmsWarehouse getById(Long id);

    /**
     * 分页查询仓库列表
     */
    PageResult<WmsWarehouse> list(int page, int size, String keyword);

    /**
     * 获取所有启用的仓库（用于下拉选择）
     */
    List<WmsWarehouse> listAll();
}
