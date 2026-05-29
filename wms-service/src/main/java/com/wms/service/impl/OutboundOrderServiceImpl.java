package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.core.PageResult;
import com.wms.common.exception.BusinessException;
import com.wms.dao.mapper.*;
import com.wms.model.dto.OutboundOrderDTO;
import com.wms.model.entity.*;
import com.wms.model.vo.OutboundOrderItemVO;
import com.wms.model.vo.OutboundOrderVO;
import com.wms.service.DashboardService;
import com.wms.service.OutboundOrderService;
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
 * 出库单服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OutboundOrderServiceImpl implements OutboundOrderService {

    private final WmsOutboundOrderMapper outboundOrderMapper;
    private final WmsOutboundOrderItemMapper outboundOrderItemMapper;
    private final WmsInventoryMapper inventoryMapper;
    private final WmsInventoryLogMapper inventoryLogMapper;
    private final WmsCustomerMapper customerMapper;
    private final WmsWarehouseMapper warehouseMapper;
    private final WmsProductMapper productMapper;
    private final WmsLocationMapper locationMapper;
    private final DashboardService dashboardService;

    /**
     * 创建出库单
     * 
     * 流程:
     * 1. 自动生成出库单号（如果未提供）
     * 2. 创建出库单主表
     * 3. 创建出库单明细
     * 4. 计算总数量和总金额
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public WmsOutboundOrder create(OutboundOrderDTO dto) {
        // 1. 自动生成出库单号（格式：OUT + 年月日 + 4位序号）
        String orderNo = dto.getOrderNo();
        if (!StringUtils.hasText(orderNo)) {
            String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String prefix = "OUT" + dateStr;
            // 查询当天最大单号
            WmsOutboundOrder lastOrder = outboundOrderMapper.selectOne(
                    new LambdaQueryWrapper<WmsOutboundOrder>()
                            .likeRight(WmsOutboundOrder::getOrderNo, prefix)
                            .orderByDesc(WmsOutboundOrder::getOrderNo)
                            .last("LIMIT 1")
            );
            int seq = 1;
            if (lastOrder != null) {
                String lastNo = lastOrder.getOrderNo();
                seq = Integer.parseInt(lastNo.substring(lastNo.length() - 4)) + 1;
            }
            orderNo = prefix + String.format("%04d", seq);
        }

        // 2. 设置默认出库类型（销售出库）
        if (dto.getOrderType() == null) {
            dto.setOrderType(1);
        }

        // 3. 创建出库单主表
        WmsOutboundOrder order = new WmsOutboundOrder();
        BeanUtils.copyProperties(dto, order);
        order.setOrderNo(orderNo);
        order.setOrderStatus(0);  // 待审核状态
        order.setTotalQuantity(0);
        order.setTotalAmount(BigDecimal.ZERO);
        outboundOrderMapper.insert(order);

        // 3. 创建出库单明细
        int totalQuantity = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;

        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            for (OutboundOrderDTO.OutboundOrderItemDTO itemDTO : dto.getItems()) {
                WmsOutboundOrderItem item = new WmsOutboundOrderItem();
                BeanUtils.copyProperties(itemDTO, item);
                item.setOrderId(order.getId());

                // 计算金额
                if (item.getPrice() != null && item.getQuantity() != null) {
                    item.setAmount(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                    totalAmount = totalAmount.add(item.getAmount());
                }

                totalQuantity += item.getQuantity();
                outboundOrderItemMapper.insert(item);
            }
        }

        // 4. 更新总数量和总金额
        order.setTotalQuantity(totalQuantity);
        order.setTotalAmount(totalAmount);
        outboundOrderMapper.updateById(order);

        log.info("创建出库单成功: {}", order.getOrderNo());
        return order;
    }

    /**
     * 审核出库单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void audit(Long id) {
        WmsOutboundOrder order = outboundOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("出库单不存在");
        }

        if (order.getOrderStatus() != 0) {
            throw new BusinessException("出库单状态不是待审核");
        }

        order.setOrderStatus(1);  // 已审核
        outboundOrderMapper.updateById(order);
        log.info("审核出库单成功: {}", order.getOrderNo());
    }

    /**
     * 执行出库（更新库存）
     * 
     * 流程:
     * 1. 检查出库单是否存在
     * 2. 检查出库单状态是否为已审核
     * 3. 遍历出库单明细，检查库存是否充足
     * 4. 更新库存
     * 5. 记录库存变更日志
     * 6. 更新出库单状态为已出库
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(Long id) {
        WmsOutboundOrder order = outboundOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("出库单不存在");
        }

        if (order.getOrderStatus() != 1) {
            throw new BusinessException("出库单状态不是已审核");
        }

        // 获取出库单明细
        List<WmsOutboundOrderItem> items = outboundOrderItemMapper.selectList(
                new LambdaQueryWrapper<WmsOutboundOrderItem>()
                        .eq(WmsOutboundOrderItem::getOrderId, id)
        );

        // 遍历明细，检查库存并更新
        for (WmsOutboundOrderItem item : items) {
            // 查询库存
            WmsInventory inventory = inventoryMapper.selectOne(
                    new LambdaQueryWrapper<WmsInventory>()
                            .eq(WmsInventory::getWarehouseId, order.getWarehouseId())
                            .eq(WmsInventory::getProductId, item.getProductId())
                            .eq(WmsInventory::getLocationId, item.getLocationId())
                            .eq(WmsInventory::getBatchNo, item.getBatchNo())
            );

            if (inventory == null) {
                throw new BusinessException("商品库存不存在");
            }

            // 检查可用库存是否充足
            if (inventory.getAvailableQuantity() < item.getQuantity()) {
                throw new BusinessException("商品可用库存不足");
            }

            // 更新库存
            int beforeQuantity = inventory.getQuantity();
            inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
            inventory.setAvailableQuantity(inventory.getQuantity() - inventory.getLockedQuantity());
            inventoryMapper.updateById(inventory);

            // 记录库存变更日志
            WmsInventoryLog inventoryLog = new WmsInventoryLog();
            inventoryLog.setWarehouseId(order.getWarehouseId());
            inventoryLog.setProductId(item.getProductId());
            inventoryLog.setBatchNo(item.getBatchNo());
            inventoryLog.setChangeType(2);  // 出库
            inventoryLog.setChangeQuantity(-item.getQuantity());
            inventoryLog.setBeforeQuantity(beforeQuantity);
            inventoryLog.setAfterQuantity(inventory.getQuantity());
            inventoryLog.setOrderNo(order.getOrderNo());
            inventoryLog.setRemark("销售出库");
            inventoryLogMapper.insert(inventoryLog);
        }

        // 更新出库单状态
        order.setOrderStatus(2);  // 已出库
        order.setOutboundTime(LocalDateTime.now());
        outboundOrderMapper.updateById(order);

        // 清除仪表盘缓存（Cache Aside模式：写入后删除缓存）
        dashboardService.clearDashboardCache();

        log.info("执行出库成功: {}", order.getOrderNo());
    }

    /**
     * 取消出库单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long id) {
        WmsOutboundOrder order = outboundOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("出库单不存在");
        }

        if (order.getOrderStatus() == 2) {
            throw new BusinessException("已出库的订单不能取消");
        }

        order.setOrderStatus(3);  // 已取消
        outboundOrderMapper.updateById(order);
        log.info("取消出库单成功: {}", order.getOrderNo());
    }

    /**
     * 根据ID查询出库单（包含明细和商品信息）
     */
    @Override
    public OutboundOrderVO getById(Long id) {
        WmsOutboundOrder order = outboundOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("出库单不存在");
        }

        // 转换为主VO
        OutboundOrderVO vo = new OutboundOrderVO();
        BeanUtils.copyProperties(order, vo);

        // 查询客户名称
        if (order.getCustomerId() != null) {
            WmsCustomer customer = customerMapper.selectById(order.getCustomerId());
            if (customer != null) {
                vo.setCustomerName(customer.getCustomerName());
            }
        }

        // 查询仓库名称
        WmsWarehouse warehouse = warehouseMapper.selectById(order.getWarehouseId());
        if (warehouse != null) {
            vo.setWarehouseName(warehouse.getWarehouseName());
        }

        // 查询明细列表
        List<WmsOutboundOrderItem> items = outboundOrderItemMapper.selectList(
                new LambdaQueryWrapper<WmsOutboundOrderItem>()
                        .eq(WmsOutboundOrderItem::getOrderId, id)
        );

        if (!items.isEmpty()) {
            // 批量查询商品
            List<Long> productIds = items.stream()
                    .map(WmsOutboundOrderItem::getProductId)
                    .distinct()
                    .collect(Collectors.toList());
            Map<Long, WmsProduct> productMap = productMapper.selectBatchIds(productIds).stream()
                    .collect(Collectors.toMap(WmsProduct::getId, p -> p));

            // 批量查询库位
            List<Long> locationIds = items.stream()
                    .map(WmsOutboundOrderItem::getLocationId)
                    .filter(lid -> lid != null)
                    .distinct()
                    .collect(Collectors.toList());
            Map<Long, WmsLocation> locationMap = locationIds.isEmpty() ? Map.of() :
                    locationMapper.selectBatchIds(locationIds).stream()
                            .collect(Collectors.toMap(WmsLocation::getId, l -> l));

            // 转换明细VO
            List<OutboundOrderItemVO> itemVOs = items.stream().map(item -> {
                OutboundOrderItemVO itemVO = new OutboundOrderItemVO();
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
     * 分页查询出库单列表
     */
    @Override
    public PageResult<OutboundOrderVO> list(int page, int size, String keyword, Integer status) {
        LambdaQueryWrapper<WmsOutboundOrder> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.like(WmsOutboundOrder::getOrderNo, keyword);
        }

        if (status != null) {
            wrapper.eq(WmsOutboundOrder::getOrderStatus, status);
        }

        wrapper.orderByDesc(WmsOutboundOrder::getGmtCreate);

        Page<WmsOutboundOrder> result = outboundOrderMapper.selectPage(new Page<>(page, size), wrapper);
        
        // 批量查询关联数据
        List<WmsOutboundOrder> records = result.getRecords();
        List<OutboundOrderVO> voList = convertToOutboundOrderVO(records);
        
        return new PageResult<>(voList, result.getTotal(), result.getSize(), result.getCurrent());
    }

    /**
     * 将出库单实体列表转换为出库单VO列表
     * 
     * 关联查询客户、仓库名称
     */
    private List<OutboundOrderVO> convertToOutboundOrderVO(List<WmsOutboundOrder> orders) {
        if (orders.isEmpty()) {
            return List.of();
        }

        // 批量查询客户
        List<Long> customerIds = orders.stream()
                .map(WmsOutboundOrder::getCustomerId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, WmsCustomer> customerMap = customerIds.isEmpty() ? Map.of() :
                customerMapper.selectBatchIds(customerIds).stream()
                        .collect(Collectors.toMap(WmsCustomer::getId, c -> c));

        // 批量查询仓库
        List<Long> warehouseIds = orders.stream()
                .map(WmsOutboundOrder::getWarehouseId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, WmsWarehouse> warehouseMap = warehouseMapper.selectBatchIds(warehouseIds).stream()
                .collect(Collectors.toMap(WmsWarehouse::getId, w -> w));

        // 转换为VO
        return orders.stream().map(order -> {
            OutboundOrderVO vo = new OutboundOrderVO();
            // 复制基础属性
            vo.setId(order.getId());
            vo.setOrderNo(order.getOrderNo());
            vo.setOrderType(order.getOrderType());
            vo.setWarehouseId(order.getWarehouseId());
            vo.setCustomerId(order.getCustomerId());
            vo.setOrderStatus(order.getOrderStatus());
            vo.setTotalQuantity(order.getTotalQuantity());
            vo.setTotalAmount(order.getTotalAmount());
            vo.setOutboundTime(order.getOutboundTime());
            vo.setRemark(order.getRemark());
            vo.setGmtCreate(order.getGmtCreate());
            vo.setGmtModified(order.getGmtModified());
            
            // 设置关联名称
            if (order.getCustomerId() != null) {
                WmsCustomer customer = customerMap.get(order.getCustomerId());
                if (customer != null) {
                    vo.setCustomerName(customer.getCustomerName());
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
