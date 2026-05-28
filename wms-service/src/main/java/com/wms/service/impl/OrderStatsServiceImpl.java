package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.dao.mapper.WmsInboundOrderItemMapper;
import com.wms.dao.mapper.WmsInboundOrderMapper;
import com.wms.dao.mapper.WmsInventoryMapper;
import com.wms.dao.mapper.WmsOutboundOrderItemMapper;
import com.wms.dao.mapper.WmsOutboundOrderMapper;
import com.wms.dao.mapper.WmsProductMapper;
import com.wms.model.entity.WmsInboundOrder;
import com.wms.model.entity.WmsInboundOrderItem;
import com.wms.model.entity.WmsInventory;
import com.wms.model.entity.WmsOutboundOrder;
import com.wms.model.entity.WmsOutboundOrderItem;
import com.wms.model.entity.WmsProduct;
import com.wms.model.vo.OrderStatsVO;
import com.wms.service.OrderStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单统计服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderStatsServiceImpl implements OrderStatsService {

    private final WmsInboundOrderMapper inboundOrderMapper;
    private final WmsOutboundOrderMapper outboundOrderMapper;
    private final WmsInboundOrderItemMapper inboundOrderItemMapper;
    private final WmsOutboundOrderItemMapper outboundOrderItemMapper;
    private final WmsProductMapper productMapper;
    private final WmsInventoryMapper inventoryMapper;

    /**
     * 获取订单统计数据
     */
    @Override
    public OrderStatsVO getStats(LocalDate startDate, LocalDate endDate) {
        OrderStatsVO stats = new OrderStatsVO();

        // 构建时间范围
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        // 今日时间范围
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);

        // 入库单统计
        List<WmsInboundOrder> inboundOrders = inboundOrderMapper.selectList(
                new LambdaQueryWrapper<WmsInboundOrder>()
                        .ge(WmsInboundOrder::getGmtCreate, start)
                        .le(WmsInboundOrder::getGmtCreate, end)
                        .eq(WmsInboundOrder::getIsDeleted, 0)
        );

        stats.setInboundCount((long) inboundOrders.size());
        stats.setInboundPendingCount(inboundOrders.stream()
                .filter(o -> o.getOrderStatus() == 0).count());
        stats.setInboundCompletedCount(inboundOrders.stream()
                .filter(o -> o.getOrderStatus() == 2).count());
        stats.setInboundTotalQuantity(inboundOrders.stream()
                .mapToInt(o -> o.getTotalQuantity() != null ? o.getTotalQuantity() : 0).sum());
        stats.setInboundTotalAmount(inboundOrders.stream()
                .mapToDouble(o -> o.getTotalAmount() != null ? o.getTotalAmount().doubleValue() : 0).sum());

        // 出库单统计
        List<WmsOutboundOrder> outboundOrders = outboundOrderMapper.selectList(
                new LambdaQueryWrapper<WmsOutboundOrder>()
                        .ge(WmsOutboundOrder::getGmtCreate, start)
                        .le(WmsOutboundOrder::getGmtCreate, end)
                        .eq(WmsOutboundOrder::getIsDeleted, 0)
        );

        stats.setOutboundCount((long) outboundOrders.size());
        stats.setOutboundPendingCount(outboundOrders.stream()
                .filter(o -> o.getOrderStatus() == 0).count());
        stats.setOutboundCompletedCount(outboundOrders.stream()
                .filter(o -> o.getOrderStatus() == 2).count());
        stats.setOutboundTotalQuantity(outboundOrders.stream()
                .mapToInt(o -> o.getTotalQuantity() != null ? o.getTotalQuantity() : 0).sum());
        stats.setOutboundTotalAmount(outboundOrders.stream()
                .mapToDouble(o -> o.getTotalAmount() != null ? o.getTotalAmount().doubleValue() : 0).sum());

        // 今日入库单数
        stats.setTodayInbound(inboundOrders.stream()
                .filter(o -> o.getGmtCreate().isAfter(todayStart) && o.getGmtCreate().isBefore(todayEnd))
                .count());

        // 今日出库单数
        stats.setTodayOutbound(outboundOrders.stream()
                .filter(o -> o.getGmtCreate().isAfter(todayStart) && o.getGmtCreate().isBefore(todayEnd))
                .count());

        // 库存总量
        List<WmsInventory> inventories = inventoryMapper.selectList(
                new LambdaQueryWrapper<WmsInventory>()
                        .eq(WmsInventory::getIsDeleted, 0)
        );
        stats.setTotalInventory(inventories.stream()
                .mapToInt(WmsInventory::getQuantity)
                .sum());

        return stats;
    }

    /**
     * 获取每日入库统计
     */
    @Override
    public List<Map<String, Object>> getDailyInboundStats(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        List<WmsInboundOrder> orders = inboundOrderMapper.selectList(
                new LambdaQueryWrapper<WmsInboundOrder>()
                        .ge(WmsInboundOrder::getGmtCreate, start)
                        .le(WmsInboundOrder::getGmtCreate, end)
                        .eq(WmsInboundOrder::getIsDeleted, 0)
                        .eq(WmsInboundOrder::getOrderStatus, 2)
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Map<String, Integer> grouped = orders.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getGmtCreate().format(formatter),
                        LinkedHashMap::new,
                        Collectors.summingInt(o -> o.getTotalQuantity() != null ? o.getTotalQuantity() : 0)
                ));

        return grouped.entrySet().stream()
                .map(e -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("date", e.getKey());
                    item.put("count", e.getValue());
                    return item;
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取每日出库统计
     */
    @Override
    public List<Map<String, Object>> getDailyOutboundStats(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        List<WmsOutboundOrder> orders = outboundOrderMapper.selectList(
                new LambdaQueryWrapper<WmsOutboundOrder>()
                        .ge(WmsOutboundOrder::getGmtCreate, start)
                        .le(WmsOutboundOrder::getGmtCreate, end)
                        .eq(WmsOutboundOrder::getIsDeleted, 0)
                        .eq(WmsOutboundOrder::getOrderStatus, 2)
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Map<String, Integer> grouped = orders.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getGmtCreate().format(formatter),
                        LinkedHashMap::new,
                        Collectors.summingInt(o -> o.getTotalQuantity() != null ? o.getTotalQuantity() : 0)
                ));

        return grouped.entrySet().stream()
                .map(e -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("date", e.getKey());
                    item.put("count", e.getValue());
                    return item;
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取商品入库排行
     */
    @Override
    public List<Map<String, Object>> getProductInboundRank(LocalDate startDate, LocalDate endDate, int limit) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        List<WmsInboundOrder> orders = inboundOrderMapper.selectList(
                new LambdaQueryWrapper<WmsInboundOrder>()
                        .ge(WmsInboundOrder::getGmtCreate, start)
                        .le(WmsInboundOrder::getGmtCreate, end)
                        .eq(WmsInboundOrder::getIsDeleted, 0)
                        .eq(WmsInboundOrder::getOrderStatus, 2)
                        .select(WmsInboundOrder::getId)
        );

        if (orders.isEmpty()) {
            return List.of();
        }

        List<Long> orderIds = orders.stream().map(WmsInboundOrder::getId).collect(Collectors.toList());
        List<WmsInboundOrderItem> items = inboundOrderItemMapper.selectList(
                new LambdaQueryWrapper<WmsInboundOrderItem>()
                        .in(WmsInboundOrderItem::getOrderId, orderIds)
                        .eq(WmsInboundOrderItem::getIsDeleted, 0)
        );

        // 按商品分组统计数量
        Map<Long, Integer> productQuantityMap = items.stream()
                .collect(Collectors.groupingBy(
                        WmsInboundOrderItem::getProductId,
                        Collectors.summingInt(WmsInboundOrderItem::getQuantity)
                ));

        // 批量查询商品名称
        List<Long> productIds = productQuantityMap.keySet().stream().collect(Collectors.toList());
        Map<Long, WmsProduct> productMap = productMapper.selectBatchIds(productIds).stream()
                .collect(Collectors.toMap(WmsProduct::getId, p -> p));

        // 排序并返回
        return productQuantityMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(e -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    WmsProduct product = productMap.get(e.getKey());
                    item.put("productName", product != null ? product.getProductName() : "未知商品");
                    item.put("quantity", e.getValue());
                    return item;
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取商品出库排行
     */
    @Override
    public List<Map<String, Object>> getProductOutboundRank(LocalDate startDate, LocalDate endDate, int limit) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        List<WmsOutboundOrder> orders = outboundOrderMapper.selectList(
                new LambdaQueryWrapper<WmsOutboundOrder>()
                        .ge(WmsOutboundOrder::getGmtCreate, start)
                        .le(WmsOutboundOrder::getGmtCreate, end)
                        .eq(WmsOutboundOrder::getIsDeleted, 0)
                        .eq(WmsOutboundOrder::getOrderStatus, 2)
                        .select(WmsOutboundOrder::getId)
        );

        if (orders.isEmpty()) {
            return List.of();
        }

        List<Long> orderIds = orders.stream().map(WmsOutboundOrder::getId).collect(Collectors.toList());
        List<WmsOutboundOrderItem> items = outboundOrderItemMapper.selectList(
                new LambdaQueryWrapper<WmsOutboundOrderItem>()
                        .in(WmsOutboundOrderItem::getOrderId, orderIds)
                        .eq(WmsOutboundOrderItem::getIsDeleted, 0)
        );

        // 按商品分组统计数量
        Map<Long, Integer> productQuantityMap = items.stream()
                .collect(Collectors.groupingBy(
                        WmsOutboundOrderItem::getProductId,
                        Collectors.summingInt(WmsOutboundOrderItem::getQuantity)
                ));

        // 批量查询商品名称
        List<Long> productIds = productQuantityMap.keySet().stream().collect(Collectors.toList());
        Map<Long, WmsProduct> productMap = productMapper.selectBatchIds(productIds).stream()
                .collect(Collectors.toMap(WmsProduct::getId, p -> p));

        // 排序并返回
        return productQuantityMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(e -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    WmsProduct product = productMap.get(e.getKey());
                    item.put("productName", product != null ? product.getProductName() : "未知商品");
                    item.put("quantity", e.getValue());
                    return item;
                })
                .collect(Collectors.toList());
    }
}
