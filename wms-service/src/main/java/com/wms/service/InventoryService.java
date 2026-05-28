package com.wms.service;

import com.wms.common.core.PageResult;
import com.wms.model.entity.WmsInventory;
import com.wms.model.entity.WmsInventoryLog;
import com.wms.model.vo.InventoryLogVO;
import com.wms.model.vo.InventoryVO;

import java.util.List;

/**
 * 库存服务接口
 */
public interface InventoryService {

    /**
     * 根据ID查询库存
     */
    WmsInventory getById(Long id);

    /**
     * 分页查询库存列表
     */
    PageResult<InventoryVO> list(int page, int size, Long warehouseId, Long productId, String keyword);

    /**
     * 查询仓库下的库存列表
     */
    List<WmsInventory> listByWarehouseId(Long warehouseId);

    /**
     * 查询商品的库存列表
     */
    List<WmsInventory> listByProductId(Long productId);

    /**
     * 查询商品在指定仓库的总库存
     */
    int getTotalQuantity(Long warehouseId, Long productId);

    /**
     * 库存调整（盘盈/盘亏）
     * 
     * @param inventoryId 库存ID
     * @param adjustQuantity 调整数量（正数为盘盈，负数为盘亏）
     * @param remark 备注
     */
    void adjust(Long inventoryId, int adjustQuantity, String remark);

    /**
     * 分页查询库存日志
     *
     * @param page 页码
     * @param size 每页数量
     * @param productId 商品ID（可选）
     * @param warehouseId 仓库ID（可选）
     */
    PageResult<InventoryLogVO> listLog(int page, int size, Long productId, Long warehouseId);
}
