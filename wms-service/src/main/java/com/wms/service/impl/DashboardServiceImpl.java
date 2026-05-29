package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.dao.mapper.OrderStatsMapper;
import com.wms.dao.mapper.*;
import com.wms.model.entity.*;
import com.wms.model.vo.DashboardVO;
import com.wms.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 首页服务实现类
 * 使用SQL聚合查询，避免全量加载到内存
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final OrderStatsMapper orderStatsMapper;
    private final WmsInboundOrderMapper inboundOrderMapper;
    private final WmsOutboundOrderMapper outboundOrderMapper;
    private final WmsSupplierMapper supplierMapper;
    private final WmsCustomerMapper customerMapper;
    private final WmsWarehouseMapper warehouseMapper;

    /**
     * 获取首页统计数据
     * 优化：使用SQL聚合查询替代全量加载
     */
    @Override
    public DashboardVO getDashboardStats() {
        // 1. 使用SQL聚合查询获取统计数据
        Map<String, Object> stats = orderStatsMapper.getDashboardStats();

        long productCount = ((Number) stats.get("productCount")).longValue();
        long warehouseCount = ((Number) stats.get("warehouseCount")).longValue();
        long todayInboundCount = ((Number) stats.get("todayInboundCount")).longValue();
        int todayInboundQuantity = ((Number) stats.get("todayInboundQuantity")).intValue();
        long todayOutboundCount = ((Number) stats.get("todayOutboundCount")).longValue();
        int todayOutboundQuantity = ((Number) stats.get("todayOutboundQuantity")).intValue();
        long totalInventoryQuantity = ((Number) stats.get("totalInventoryQuantity")).longValue();

        // 2. 最近入库单（最新5条）
        List<WmsInboundOrder> recentInbound = inboundOrderMapper.selectList(
                new LambdaQueryWrapper<WmsInboundOrder>()
                        .eq(WmsInboundOrder::getIsDeleted, 0)
                        .orderByDesc(WmsInboundOrder::getGmtCreate)
                        .last("LIMIT 5")
        );
        List<DashboardVO.RecentOrderVO> recentInboundVOs = convertToRecentInboundVO(recentInbound);

        // 3. 最近出库单（最新5条）
        List<WmsOutboundOrder> recentOutbound = outboundOrderMapper.selectList(
                new LambdaQueryWrapper<WmsOutboundOrder>()
                        .eq(WmsOutboundOrder::getIsDeleted, 0)
                        .orderByDesc(WmsOutboundOrder::getGmtCreate)
                        .last("LIMIT 5")
        );
        List<DashboardVO.RecentOrderVO> recentOutboundVOs = convertToRecentOutboundVO(recentOutbound);

        // 4. 构建返回对象
        return DashboardVO.builder()
                .productCount(productCount)
                .warehouseCount(warehouseCount)
                .todayInboundCount(todayInboundCount)
                .todayOutboundCount(todayOutboundCount)
                .todayInboundQuantity(todayInboundQuantity)
                .todayOutboundQuantity(todayOutboundQuantity)
                .totalInventoryQuantity(totalInventoryQuantity)
                .recentInboundOrders(recentInboundVOs)
                .recentOutboundOrders(recentOutboundVOs)
                .build();
    }

    /**
     * 将入库单列表转换为最近订单VO列表
     */
    private List<DashboardVO.RecentOrderVO> convertToRecentInboundVO(List<WmsInboundOrder> orders) {
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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return orders.stream().map(order -> {
            String supplierName = "";
            if (order.getSupplierId() != null) {
                WmsSupplier supplier = supplierMap.get(order.getSupplierId());
                if (supplier != null) {
                    supplierName = supplier.getSupplierName();
                }
            }

            String warehouseName = "";
            WmsWarehouse warehouse = warehouseMap.get(order.getWarehouseId());
            if (warehouse != null) {
                warehouseName = warehouse.getWarehouseName();
            }

            return DashboardVO.RecentOrderVO.builder()
                    .id(order.getId())
                    .orderNo(order.getOrderNo())
                    .relatedName(supplierName)
                    .warehouseName(warehouseName)
                    .totalQuantity(order.getTotalQuantity())
                    .totalAmount(order.getTotalAmount())
                    .orderStatus(order.getOrderStatus())
                    .gmtCreate(order.getGmtCreate() != null ? order.getGmtCreate().format(formatter) : "")
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * 将出库单列表转换为最近订单VO列表
     */
    private List<DashboardVO.RecentOrderVO> convertToRecentOutboundVO(List<WmsOutboundOrder> orders) {
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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return orders.stream().map(order -> {
            String customerName = "";
            if (order.getCustomerId() != null) {
                WmsCustomer customer = customerMap.get(order.getCustomerId());
                if (customer != null) {
                    customerName = customer.getCustomerName();
                }
            }

            String warehouseName = "";
            WmsWarehouse warehouse = warehouseMap.get(order.getWarehouseId());
            if (warehouse != null) {
                warehouseName = warehouse.getWarehouseName();
            }

            return DashboardVO.RecentOrderVO.builder()
                    .id(order.getId())
                    .orderNo(order.getOrderNo())
                    .relatedName(customerName)
                    .warehouseName(warehouseName)
                    .totalQuantity(order.getTotalQuantity())
                    .totalAmount(order.getTotalAmount())
                    .orderStatus(order.getOrderStatus())
                    .gmtCreate(order.getGmtCreate() != null ? order.getGmtCreate().format(formatter) : "")
                    .build();
        }).collect(Collectors.toList());
    }
}
