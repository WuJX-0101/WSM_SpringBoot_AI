package com.wms.service.impl;

import com.wms.dao.mapper.OrderStatsMapper;
import com.wms.model.vo.OrderStatsVO;
import com.wms.service.OrderStatsService;
import com.wms.service.config.CacheConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * 订单统计服务实现类
 * 使用SQL聚合查询，避免全量加载到内存
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderStatsServiceImpl implements OrderStatsService {

    private final OrderStatsMapper orderStatsMapper;

    /**
     * 获取订单统计数据
     * 优化：使用SQL聚合查询替代全量加载
     * 缓存：结果缓存5分钟
     */
    @Override
    @Cacheable(value = CacheConfig.CACHE_ORDER_STATS, key = "#startDate + ':' + #endDate")
    public OrderStatsVO getStats(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        return orderStatsMapper.getOrderStats(start, end);
    }

    /**
     * 获取每日入库统计
     * 优化：使用SQL GROUP BY替代Java Stream分组
     */
    @Override
    public List<Map<String, Object>> getDailyInboundStats(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        return orderStatsMapper.getDailyInboundStats(start, end);
    }

    /**
     * 获取每日出库统计
     * 优化：使用SQL GROUP BY替代Java Stream分组
     */
    @Override
    public List<Map<String, Object>> getDailyOutboundStats(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        return orderStatsMapper.getDailyOutboundStats(start, end);
    }

    /**
     * 获取商品入库排行
     * 优化：使用SQL JOIN + GROUP BY替代多次查询和Java分组
     */
    @Override
    public List<Map<String, Object>> getProductInboundRank(LocalDate startDate, LocalDate endDate, int limit) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        return orderStatsMapper.getProductInboundRank(start, end, limit);
    }

    /**
     * 获取商品出库排行
     * 优化：使用SQL JOIN + GROUP BY替代多次查询和Java分组
     */
    @Override
    public List<Map<String, Object>> getProductOutboundRank(LocalDate startDate, LocalDate endDate, int limit) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        return orderStatsMapper.getProductOutboundRank(start, end, limit);
    }
}
