package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.core.PageResult;
import com.wms.common.exception.BusinessException;
import com.wms.dao.mapper.*;
import com.wms.model.dto.InboundOrderDTO;
import com.wms.model.entity.*;
import com.wms.model.vo.InboundOrderItemVO;
import com.wms.model.vo.InboundOrderVO;
import com.wms.service.InboundOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 入库单服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InboundOrderServiceImpl implements InboundOrderService {

    private final WmsInboundOrderMapper inboundOrderMapper;
    private final WmsInboundOrderItemMapper inboundOrderItemMapper;
    private final WmsInventoryMapper inventoryMapper;
    private final WmsInventoryLogMapper inventoryLogMapper;
    private final WmsSupplierMapper supplierMapper;
    private final WmsWarehouseMapper warehouseMapper;
    private final WmsProductMapper productMapper;
    private final WmsLocationMapper locationMapper;

    /**
     * 创建入库单
     * 
     * 流程:
     * 1. 自动生成入库单号（如果未提供）
     * 2. 创建入库单主表
     * 3. 创建入库单明细
     * 4. 计算总数量和总金额
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public WmsInboundOrder create(InboundOrderDTO dto) {
        // 1. 自动生成入库单号（格式：IN + 年月日 + 4位序号）
        String orderNo = dto.getOrderNo();
        if (!StringUtils.hasText(orderNo)) {
            String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String prefix = "IN" + dateStr;
            // 查询当天最大单号
            WmsInboundOrder lastOrder = inboundOrderMapper.selectOne(
                    new LambdaQueryWrapper<WmsInboundOrder>()
                            .likeRight(WmsInboundOrder::getOrderNo, prefix)
                            .orderByDesc(WmsInboundOrder::getOrderNo)
                            .last("LIMIT 1")
            );
            int seq = 1;
            if (lastOrder != null) {
                String lastNo = lastOrder.getOrderNo();
                seq = Integer.parseInt(lastNo.substring(lastNo.length() - 4)) + 1;
            }
            orderNo = prefix + String.format("%04d", seq);
        }

        // 2. 设置默认入库类型（采购入库）
        if (dto.getOrderType() == null) {
            dto.setOrderType(1);
        }

        // 3. 创建入库单主表
        WmsInboundOrder order = new WmsInboundOrder();
        BeanUtils.copyProperties(dto, order);
        order.setOrderNo(orderNo);
        order.setOrderStatus(0);  // 待审核状态
        order.setTotalQuantity(0);
        order.setTotalAmount(BigDecimal.ZERO);
        inboundOrderMapper.insert(order);

        // 3. 创建入库单明细
        int totalQuantity = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;

        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            for (InboundOrderDTO.InboundOrderItemDTO itemDTO : dto.getItems()) {
                WmsInboundOrderItem item = new WmsInboundOrderItem();
                BeanUtils.copyProperties(itemDTO, item);
                item.setOrderId(order.getId());

                // 计算金额
                if (item.getPrice() != null && item.getQuantity() != null) {
                    item.setAmount(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                    totalAmount = totalAmount.add(item.getAmount());
                }

                totalQuantity += item.getQuantity();
                inboundOrderItemMapper.insert(item);
            }
        }

        // 4. 更新总数量和总金额
        order.setTotalQuantity(totalQuantity);
        order.setTotalAmount(totalAmount);
        inboundOrderMapper.updateById(order);

        log.info("创建入库单成功: {}", order.getOrderNo());
        return order;
    }

    /**
     * 审核入库单
     * 
     * 流程:
     * 1. 检查入库单是否存在
     * 2. 检查入库单状态是否为待审核
     * 3. 更新状态为已审核
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void audit(Long id) {
        WmsInboundOrder order = inboundOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("入库单不存在");
        }

        if (order.getOrderStatus() != 0) {
            throw new BusinessException("入库单状态不是待审核");
        }

        order.setOrderStatus(1);  // 已审核
        inboundOrderMapper.updateById(order);
        log.info("审核入库单成功: {}", order.getOrderNo());
    }

    /**
     * 执行入库（更新库存）
     * 
     * 流程:
     * 1. 检查入库单是否存在
     * 2. 检查入库单状态是否为已审核
     * 3. 遍历入库单明细，更新库存
     * 4. 记录库存变更日志
     * 5. 更新入库单状态为已入库
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(Long id) {
        WmsInboundOrder order = inboundOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("入库单不存在");
        }

        if (order.getOrderStatus() != 1) {
            throw new BusinessException("入库单状态不是已审核");
        }

        // 获取入库单明细
        List<WmsInboundOrderItem> items = inboundOrderItemMapper.selectList(
                new LambdaQueryWrapper<WmsInboundOrderItem>()
                        .eq(WmsInboundOrderItem::getOrderId, id)
        );

        // 遍历明细，更新库存
        for (WmsInboundOrderItem item : items) {
            // 查询是否已有该商品的库存
            WmsInventory inventory = inventoryMapper.selectOne(
                    new LambdaQueryWrapper<WmsInventory>()
                            .eq(WmsInventory::getWarehouseId, order.getWarehouseId())
                            .eq(WmsInventory::getProductId, item.getProductId())
                            .eq(WmsInventory::getLocationId, item.getLocationId())
                            .eq(WmsInventory::getBatchNo, item.getBatchNo())
            );

            int beforeQuantity = 0;
            int afterQuantity = item.getQuantity();

            if (inventory != null) {
                // 已有库存，累加数量
                beforeQuantity = inventory.getQuantity();
                inventory.setQuantity(inventory.getQuantity() + item.getQuantity());
                inventory.setAvailableQuantity(inventory.getQuantity() - inventory.getLockedQuantity());
                inventoryMapper.updateById(inventory);
                afterQuantity = inventory.getQuantity();
            } else {
                // 没有库存，创建新记录
                inventory = new WmsInventory();
                inventory.setWarehouseId(order.getWarehouseId());
                inventory.setProductId(item.getProductId());
                inventory.setLocationId(item.getLocationId());
                inventory.setBatchNo(item.getBatchNo());
                inventory.setQuantity(item.getQuantity());
                inventory.setLockedQuantity(0);
                inventory.setAvailableQuantity(item.getQuantity());
                inventory.setCostPrice(item.getPrice());
                inventoryMapper.insert(inventory);
            }

            // 记录库存变更日志
            WmsInventoryLog inventoryLog = new WmsInventoryLog();
            inventoryLog.setWarehouseId(order.getWarehouseId());
            inventoryLog.setProductId(item.getProductId());
            inventoryLog.setBatchNo(item.getBatchNo());
            inventoryLog.setChangeType(1);  // 入库
            inventoryLog.setChangeQuantity(item.getQuantity());
            inventoryLog.setBeforeQuantity(beforeQuantity);
            inventoryLog.setAfterQuantity(afterQuantity);
            inventoryLog.setOrderNo(order.getOrderNo());
            inventoryLog.setRemark("采购入库");
            inventoryLogMapper.insert(inventoryLog);
        }

        // 更新入库单状态
        order.setOrderStatus(2);  // 已入库
        order.setInboundTime(LocalDateTime.now());
        inboundOrderMapper.updateById(order);

        log.info("执行入库成功: {}", order.getOrderNo());
    }

    /**
     * 取消入库单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long id) {
        WmsInboundOrder order = inboundOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("入库单不存在");
        }

        if (order.getOrderStatus() == 2) {
            throw new BusinessException("已入库的订单不能取消");
        }

        order.setOrderStatus(3);  // 已取消
        inboundOrderMapper.updateById(order);
        log.info("取消入库单成功: {}", order.getOrderNo());
    }

    /**
     * 根据ID查询入库单（包含明细和商品信息）
     */
    @Override
    public InboundOrderVO getById(Long id) {
        WmsInboundOrder order = inboundOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("入库单不存在");
        }

        // 转换为主VO
        InboundOrderVO vo = new InboundOrderVO();
        BeanUtils.copyProperties(order, vo);

        // 查询供应商名称
        if (order.getSupplierId() != null) {
            WmsSupplier supplier = supplierMapper.selectById(order.getSupplierId());
            if (supplier != null) {
                vo.setSupplierName(supplier.getSupplierName());
            }
        }

        // 查询仓库名称
        WmsWarehouse warehouse = warehouseMapper.selectById(order.getWarehouseId());
        if (warehouse != null) {
            vo.setWarehouseName(warehouse.getWarehouseName());
        }

        // 查询明细列表
        List<WmsInboundOrderItem> items = inboundOrderItemMapper.selectList(
                new LambdaQueryWrapper<WmsInboundOrderItem>()
                        .eq(WmsInboundOrderItem::getOrderId, id)
        );

        if (!items.isEmpty()) {
            // 批量查询商品
            List<Long> productIds = items.stream()
                    .map(WmsInboundOrderItem::getProductId)
                    .distinct()
                    .collect(Collectors.toList());
            Map<Long, WmsProduct> productMap = productMapper.selectBatchIds(productIds).stream()
                    .collect(Collectors.toMap(WmsProduct::getId, p -> p));

            // 批量查询库位
            List<Long> locationIds = items.stream()
                    .map(WmsInboundOrderItem::getLocationId)
                    .filter(lid -> lid != null)
                    .distinct()
                    .collect(Collectors.toList());
            Map<Long, WmsLocation> locationMap = locationIds.isEmpty() ? Map.of() :
                    locationMapper.selectBatchIds(locationIds).stream()
                            .collect(Collectors.toMap(WmsLocation::getId, l -> l));

            // 转换明细VO
            List<InboundOrderItemVO> itemVOs = items.stream().map(item -> {
                InboundOrderItemVO itemVO = new InboundOrderItemVO();
                BeanUtils.copyProperties(item, itemVO);

                // 设置商品信息
                WmsProduct product = productMap.get(item.getProductId());
                if (product != null) {
                    itemVO.setProductCode(product.getProductCode());
                    itemVO.setProductName(product.getProductName());
                }

                // 设置库位信息
                if (item.getLocationId() != null) {
                    WmsLocation location = locationMap.get(item.getLocationId());
                    if (location != null) {
                        itemVO.setLocationCode(location.getLocationCode());
                        itemVO.setLocationName(location.getLocationName());
                    }
                }

                return itemVO;
            }).collect(Collectors.toList());

            vo.setItems(itemVOs);
        }

        return vo;
    }

    /**
     * 分页查询入库单列表
     */
    @Override
    public PageResult<InboundOrderVO> list(int page, int size, String keyword, Integer status) {
        LambdaQueryWrapper<WmsInboundOrder> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.like(WmsInboundOrder::getOrderNo, keyword);
        }

        if (status != null) {
            wrapper.eq(WmsInboundOrder::getOrderStatus, status);
        }

        wrapper.orderByDesc(WmsInboundOrder::getGmtCreate);

        Page<WmsInboundOrder> result = inboundOrderMapper.selectPage(new Page<>(page, size), wrapper);
        
        // 批量查询关联数据
        List<WmsInboundOrder> records = result.getRecords();
        List<InboundOrderVO> voList = convertToInboundOrderVO(records);
        
        return new PageResult<>(voList, result.getTotal(), result.getSize(), result.getCurrent());
    }

    /**
     * 将入库单实体列表转换为入库单VO列表
     * 
     * 关联查询供应商、仓库名称
     */
    private List<InboundOrderVO> convertToInboundOrderVO(List<WmsInboundOrder> orders) {
        if (orders.isEmpty()) {
            return List.of();
        }

        // 批量查询供应商
        List<Long> supplierIds = orders.stream()
                .map(WmsInboundOrder::getSupplierId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, WmsSupplier> supplierMap = supplierIds.isEmpty() ? Map.of() :
                supplierMapper.selectBatchIds(supplierIds).stream()
                        .collect(Collectors.toMap(WmsSupplier::getId, s -> s));

        // 批量查询仓库
        List<Long> warehouseIds = orders.stream()
                .map(WmsInboundOrder::getWarehouseId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, WmsWarehouse> warehouseMap = warehouseMapper.selectBatchIds(warehouseIds).stream()
                .collect(Collectors.toMap(WmsWarehouse::getId, w -> w));

        // 转换为VO
        return orders.stream().map(order -> {
            InboundOrderVO vo = new InboundOrderVO();
            // 复制基础属性
            vo.setId(order.getId());
            vo.setOrderNo(order.getOrderNo());
            vo.setOrderType(order.getOrderType());
            vo.setWarehouseId(order.getWarehouseId());
            vo.setSupplierId(order.getSupplierId());
            vo.setOrderStatus(order.getOrderStatus());
            vo.setTotalQuantity(order.getTotalQuantity());
            vo.setTotalAmount(order.getTotalAmount());
            vo.setInboundTime(order.getInboundTime());
            vo.setRemark(order.getRemark());
            vo.setGmtCreate(order.getGmtCreate());
            vo.setGmtModified(order.getGmtModified());
            
            // 设置关联名称
            if (order.getSupplierId() != null) {
                WmsSupplier supplier = supplierMap.get(order.getSupplierId());
                if (supplier != null) {
                    vo.setSupplierName(supplier.getSupplierName());
                }
            }
            
            WmsWarehouse warehouse = warehouseMap.get(order.getWarehouseId());
            if (warehouse != null) {
                vo.setWarehouseName(warehouse.getWarehouseName());
            }
            
            return vo;
        }).collect(Collectors.toList());
    }
}
