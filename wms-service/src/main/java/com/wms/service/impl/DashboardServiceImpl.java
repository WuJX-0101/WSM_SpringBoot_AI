package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.dao.mapper.*;
import com.wms.model.entity.*;
import com.wms.model.vo.DashboardVO;
import com.wms.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 首页服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final WmsProductMapper productMapper;
    private final WmsWarehouseMapper warehouseMapper;
    private final WmsInboundOrderMapper inboundOrderMapper;
    private final WmsOutboundOrderMapper outboundOrderMapper;
    private final WmsInventoryMapper inventoryMapper;
    private final WmsSupplierMapper supplierMapper;
    private final WmsCustomerMapper customerMapper;

    /**
     * 获取首页统计数据
     */
    @Override
    public DashboardVO getDashboardStats() {
        // 1. 商品总数
        long productCount = productMapper.selectCount(
                new LambdaQueryWrapper<WmsProduct>().eq(WmsProduct::getIsDeleted, 0)
        );

        // 2. 仓库数量
        long warehouseCount = warehouseMapper.selectCount(
                new LambdaQueryWrapper<WmsWarehouse>().eq(WmsWarehouse::getIsDeleted, 0)
        );

        // 3. 今日时间范围
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);

        // 4. 今日入库统计
        List<WmsInboundOrder> todayInboundOrders = inboundOrderMapper.selectList(
                new LambdaQueryWrapper<WmsInboundOrder>()
                        .ge(WmsInboundOrder::getGmtCreate, todayStart)
                        .le(WmsInboundOrder::getGmtCreate, todayEnd)
                        .eq(WmsInboundOrder::getIsDeleted, 0)
                        .eq(WmsInboundOrder::getOrderStatus, 2)  // 已完成
        );
        long todayInboundCount = todayInboundOrders.size();
        int todayInboundQuantity = todayInboundOrders.stream()
                .mapToInt(o -> o.getTotalQuantity() != null ? o.getTotalQuantity() : 0)
                .sum();

        // 5. 今日出库统计
        List<WmsOutboundOrder> todayOutboundOrders = outboundOrderMapper.selectList(
                new LambdaQueryWrapper<WmsOutboundOrder>()
                        .ge(WmsOutboundOrder::getGmtCreate, todayStart)
                        .le(WmsOutboundOrder::getGmtCreate, todayEnd)
                        .eq(WmsOutboundOrder::getIsDeleted, 0)
                        .eq(WmsOutboundOrder::getOrderStatus, 2)  // 已完成
        );
        long todayOutboundCount = todayOutboundOrders.size();
        int todayOutboundQuantity = todayOutboundOrders.stream()
                .mapToInt(o -> o.getTotalQuantity() != null ? o.getTotalQuantity() : 0)
                .sum();

        // 6. 库存总量
        List<WmsInventory> inventories = inventoryMapper.selectList(
                new LambdaQueryWrapper<WmsInventory>().eq(WmsInventory::getIsDeleted, 0)
        );
        long totalInventoryQuantity = inventories.stream()
                .mapToLong(i -> i.getQuantity() != null ? i.getQuantity() : 0)
                .sum();

        // 7. 最近入库单（最新5条）
        List<WmsInboundOrder> recentInbound = inboundOrderMapper.selectList(
                new LambdaQueryWrapper<WmsInboundOrder>()
                        .eq(WmsInboundOrder::getIsDeleted, 0)
                        .orderByDesc(WmsInboundOrder::getGmtCreate)
                        .last("LIMIT 5")
        );
        List<DashboardVO.RecentOrderVO> recentInboundVOs = convertToRecentInboundVO(recentInbound);

        // 8. 最近出库单（最新5条）
        List<WmsOutboundOrder> recentOutbound = outboundOrderMapper.selectList(
                new LambdaQueryWrapper<WmsOutboundOrder>()
                        .eq(WmsOutboundOrder::getIsDeleted, 0)
                        .orderByDesc(WmsOutboundOrder::getGmtCreate)
                        .last("LIMIT 5")
        );
        List<DashboardVO.RecentOrderVO> recentOutboundVOs = convertToRecentOutboundVO(recentOutbound);

        // 9. 构建返回对象
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
        java.util.Map<Long, WmsSupplier> supplierMap = supplierIds.isEmpty() ? java.util.Map.of() :
                supplierMapper.selectBatchIds(supplierIds).stream()
                        .collect(Collectors.toMap(WmsSupplier::getId, s -> s));

        // 批量查询仓库
        List<Long> warehouseIds = orders.stream()
                .map(WmsInboundOrder::getWarehouseId)
                .distinct()
                .collect(Collectors.toList());
        java.util.Map<Long, WmsWarehouse> warehouseMap = warehouseMapper.selectBatchIds(warehouseIds).stream()
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
        java.util.Map<Long, WmsCustomer> customerMap = customerIds.isEmpty() ? java.util.Map.of() :
                customerMapper.selectBatchIds(customerIds).stream()
                        .collect(Collectors.toMap(WmsCustomer::getId, c -> c));

        // 批量查询仓库
        List<Long> warehouseIds = orders.stream()
                .map(WmsOutboundOrder::getWarehouseId)
                .distinct()
                .collect(Collectors.toList());
        java.util.Map<Long, WmsWarehouse> warehouseMap = warehouseMapper.selectBatchIds(warehouseIds).stream()
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
