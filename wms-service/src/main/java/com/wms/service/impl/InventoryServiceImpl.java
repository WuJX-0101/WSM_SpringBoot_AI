package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.core.PageResult;
import com.wms.common.exception.BusinessException;
import com.wms.dao.mapper.WmsInventoryLogMapper;
import com.wms.dao.mapper.WmsInventoryMapper;
import com.wms.dao.mapper.WmsLocationMapper;
import com.wms.dao.mapper.WmsProductMapper;
import com.wms.dao.mapper.WmsWarehouseMapper;
import com.wms.model.entity.WmsInventory;
import com.wms.model.entity.WmsInventoryLog;
import com.wms.model.entity.WmsLocation;
import com.wms.model.entity.WmsProduct;
import com.wms.model.entity.WmsWarehouse;
import com.wms.model.vo.InventoryLogVO;
import com.wms.model.vo.InventoryVO;
import com.wms.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 库存服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final WmsInventoryMapper inventoryMapper;
    private final WmsInventoryLogMapper inventoryLogMapper;
    private final WmsProductMapper productMapper;
    private final WmsWarehouseMapper warehouseMapper;
    private final WmsLocationMapper locationMapper;

    /**
     * 根据ID查询库存
     */
    @Override
    public WmsInventory getById(Long id) {
        WmsInventory inventory = inventoryMapper.selectById(id);
        if (inventory == null) {
            throw new BusinessException("库存记录不存在");
        }
        return inventory;
    }

    /**
     * 分页查询库存列表
     * 
     * @param page        页码
     * @param size        每页数量
     * @param warehouseId 仓库ID（可选）
     * @param productId   商品ID（可选）
     * @param keyword     搜索关键词（匹配批次号）
     */
    @Override
    public PageResult<InventoryVO> list(int page, int size, Long warehouseId, Long productId, String keyword) {
        LambdaQueryWrapper<WmsInventory> wrapper = new LambdaQueryWrapper<>();

        // 按仓库筛选
        if (warehouseId != null) {
            wrapper.eq(WmsInventory::getWarehouseId, warehouseId);
        }

        // 按商品筛选
        if (productId != null) {
            wrapper.eq(WmsInventory::getProductId, productId);
        }

        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            wrapper.like(WmsInventory::getBatchNo, keyword);
        }

        wrapper.orderByDesc(WmsInventory::getGmtModified);

        Page<WmsInventory> result = inventoryMapper.selectPage(new Page<>(page, size), wrapper);
        
        // 批量查询关联数据
        List<WmsInventory> records = result.getRecords();
        List<InventoryVO> voList = convertToInventoryVO(records);
        
        return new PageResult<>(voList, result.getTotal(), result.getSize(), result.getCurrent());
    }

    /**
     * 将库存实体列表转换为库存VO列表
     * 
     * 关联查询商品、仓库、库位名称
     */
    private List<InventoryVO> convertToInventoryVO(List<WmsInventory> inventories) {
        if (inventories.isEmpty()) {
            return List.of();
        }

        // 批量查询商品
        List<Long> productIds = inventories.stream()
                .map(WmsInventory::getProductId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, WmsProduct> productMap = productMapper.selectBatchIds(productIds).stream()
                .collect(Collectors.toMap(WmsProduct::getId, p -> p));

        // 批量查询仓库
        List<Long> warehouseIds = inventories.stream()
                .map(WmsInventory::getWarehouseId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, WmsWarehouse> warehouseMap = warehouseMapper.selectBatchIds(warehouseIds).stream()
                .collect(Collectors.toMap(WmsWarehouse::getId, w -> w));

        // 批量查询库位
        List<Long> locationIds = inventories.stream()
                .map(WmsInventory::getLocationId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, WmsLocation> locationMap = locationIds.isEmpty() ? Map.of() :
                locationMapper.selectBatchIds(locationIds).stream()
                        .collect(Collectors.toMap(WmsLocation::getId, l -> l));

        // 转换为VO
        return inventories.stream().map(inventory -> {
            InventoryVO vo = new InventoryVO();
            // 复制基础属性
            vo.setId(inventory.getId());
            vo.setWarehouseId(inventory.getWarehouseId());
            vo.setProductId(inventory.getProductId());
            vo.setLocationId(inventory.getLocationId());
            vo.setBatchNo(inventory.getBatchNo());
            vo.setQuantity(inventory.getQuantity());
            vo.setLockedQuantity(inventory.getLockedQuantity());
            vo.setAvailableQuantity(inventory.getAvailableQuantity());
            vo.setCostPrice(inventory.getCostPrice());
            vo.setProductionDate(inventory.getProductionDate());
            vo.setExpiryDate(inventory.getExpiryDate());
            vo.setGmtCreate(inventory.getGmtCreate());
            vo.setGmtModified(inventory.getGmtModified());
            
            // 设置关联名称
            WmsProduct product = productMap.get(inventory.getProductId());
            if (product != null) {
                vo.setProductName(product.getProductName());
                vo.setProductCode(product.getProductCode());
            }
            
            WmsWarehouse warehouse = warehouseMap.get(inventory.getWarehouseId());
            if (warehouse != null) {
                vo.setWarehouseName(warehouse.getWarehouseName());
            }
            
            if (inventory.getLocationId() != null) {
                WmsLocation location = locationMap.get(inventory.getLocationId());
                if (location != null) {
                    vo.setLocationCode(location.getLocationCode());
                }
            }
            
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 查询仓库下的库存列表
     */
    @Override
    public List<WmsInventory> listByWarehouseId(Long warehouseId) {
        return inventoryMapper.selectList(
                new LambdaQueryWrapper<WmsInventory>()
                        .eq(WmsInventory::getWarehouseId, warehouseId)
                        .gt(WmsInventory::getQuantity, 0)
                        .orderByDesc(WmsInventory::getGmtModified)
        );
    }

    /**
     * 查询商品的库存列表
     */
    @Override
    public List<WmsInventory> listByProductId(Long productId) {
        return inventoryMapper.selectList(
                new LambdaQueryWrapper<WmsInventory>()
                        .eq(WmsInventory::getProductId, productId)
                        .gt(WmsInventory::getQuantity, 0)
                        .orderByDesc(WmsInventory::getGmtModified)
        );
    }

    /**
     * 查询商品在指定仓库的总库存
     */
    @Override
    public int getTotalQuantity(Long warehouseId, Long productId) {
        List<WmsInventory> inventories = inventoryMapper.selectList(
                new LambdaQueryWrapper<WmsInventory>()
                        .eq(WmsInventory::getWarehouseId, warehouseId)
                        .eq(WmsInventory::getProductId, productId)
                        .gt(WmsInventory::getQuantity, 0)
        );
        return inventories.stream().mapToInt(WmsInventory::getQuantity).sum();
    }

    /**
     * 库存调整（盘盈/盘亏）
     * 
     * 流程:
     * 1. 检查库存记录是否存在
     * 2. 检查调整后库存不能为负数
     * 3. 更新库存数量
     * 4. 记录库存变更日志
     * 
     * @param inventoryId    库存ID
     * @param adjustQuantity 调整数量（正数为盘盈，负数为盘亏）
     * @param remark         备注
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adjust(Long inventoryId, int adjustQuantity, String remark) {
        // 1. 检查库存记录是否存在
        WmsInventory inventory = inventoryMapper.selectById(inventoryId);
        if (inventory == null) {
            throw new BusinessException("库存记录不存在");
        }

        // 2. 检查调整后库存不能为负数
        int afterQuantity = inventory.getQuantity() + adjustQuantity;
        if (afterQuantity < 0) {
            throw new BusinessException("调整后库存不能为负数");
        }

        // 3. 更新库存数量
        int beforeQuantity = inventory.getQuantity();
        inventory.setQuantity(afterQuantity);
        inventory.setAvailableQuantity(afterQuantity - inventory.getLockedQuantity());
        inventoryMapper.updateById(inventory);

        // 4. 记录库存变更日志
        WmsInventoryLog inventoryLog = new WmsInventoryLog();
        inventoryLog.setWarehouseId(inventory.getWarehouseId());
        inventoryLog.setProductId(inventory.getProductId());
        inventoryLog.setBatchNo(inventory.getBatchNo());
        inventoryLog.setChangeType(4);  // 调整
        inventoryLog.setChangeQuantity(adjustQuantity);
        inventoryLog.setBeforeQuantity(beforeQuantity);
        inventoryLog.setAfterQuantity(afterQuantity);
        inventoryLog.setRemark(remark);
        inventoryLogMapper.insert(inventoryLog);

        log.info("库存调整成功: 库存ID={}, 调整数量={}, 备注={}", inventoryId, adjustQuantity, remark);
    }

    /**
     * 分页查询库存日志
     *
     * @param page 页码
     * @param size 每页数量
     * @param productId 商品ID（可选）
     * @param warehouseId 仓库ID（可选）
     */
    @Override
    public PageResult<InventoryLogVO> listLog(int page, int size, Long productId, Long warehouseId) {
        LambdaQueryWrapper<WmsInventoryLog> wrapper = new LambdaQueryWrapper<>();

        // 按商品筛选
        if (productId != null) {
            wrapper.eq(WmsInventoryLog::getProductId, productId);
        }

        // 按仓库筛选
        if (warehouseId != null) {
            wrapper.eq(WmsInventoryLog::getWarehouseId, warehouseId);
        }

        wrapper.orderByDesc(WmsInventoryLog::getGmtCreate);

        Page<WmsInventoryLog> result = inventoryLogMapper.selectPage(new Page<>(page, size), wrapper);

        // 转换为VO（关联商品和仓库信息）
        List<InventoryLogVO> voList = convertToInventoryLogVO(result.getRecords());

        return new PageResult<>(voList, result.getTotal(), result.getSize(), result.getCurrent());
    }

    /**
     * 将库存日志实体列表转换为VO列表
     */
    private List<InventoryLogVO> convertToInventoryLogVO(List<WmsInventoryLog> logs) {
        if (logs.isEmpty()) {
            return List.of();
        }

        // 批量查询商品
        List<Long> productIds = logs.stream()
                .map(WmsInventoryLog::getProductId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, WmsProduct> productMap = productMapper.selectBatchIds(productIds).stream()
                .collect(Collectors.toMap(WmsProduct::getId, p -> p));

        // 批量查询仓库
        List<Long> warehouseIds = logs.stream()
                .map(WmsInventoryLog::getWarehouseId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, WmsWarehouse> warehouseMap = warehouseMapper.selectBatchIds(warehouseIds).stream()
                .collect(Collectors.toMap(WmsWarehouse::getId, w -> w));

        // 转换为VO
        return logs.stream().map(log -> {
            InventoryLogVO vo = new InventoryLogVO();
            vo.setId(log.getId());
            vo.setWarehouseId(log.getWarehouseId());
            vo.setProductId(log.getProductId());
            vo.setBatchNo(log.getBatchNo());
            vo.setChangeType(log.getChangeType());
            vo.setChangeQuantity(log.getChangeQuantity());
            vo.setBeforeQuantity(log.getBeforeQuantity());
            vo.setAfterQuantity(log.getAfterQuantity());
            vo.setOrderNo(log.getOrderNo());
            vo.setRemark(log.getRemark());
            vo.setGmtCreate(log.getGmtCreate());
            vo.setCreateBy(log.getCreateBy());

            // 设置商品信息
            WmsProduct product = productMap.get(log.getProductId());
            if (product != null) {
                vo.setProductCode(product.getProductCode());
                vo.setProductName(product.getProductName());
            }

            // 设置仓库信息
            WmsWarehouse warehouse = warehouseMap.get(log.getWarehouseId());
            if (warehouse != null) {
                vo.setWarehouseName(warehouse.getWarehouseName());
            }

            return vo;
        }).collect(Collectors.toList());
    }
}
