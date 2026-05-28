package com.wms.web.controller;

import com.wms.common.core.R;
import com.wms.model.vo.OrderStatsVO;
import com.wms.service.OrderStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 订单统计接口
 */
@Tag(name = "订单统计接口")
@RestController
@RequestMapping("/api/v1/order-stats")
@RequiredArgsConstructor
public class OrderStatsController {

    private final OrderStatsService orderStatsService;

    /**
     * 获取订单统计数据
     */
    @Operation(summary = "获取订单统计数据")
    @GetMapping
    public R<OrderStatsVO> getStats(
            @Parameter(description = "开始日期") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        OrderStatsVO stats = orderStatsService.getStats(startDate, endDate);
        return R.ok(stats);
    }

    /**
     * 获取每日入库统计
     */
    @Operation(summary = "获取每日入库统计")
    @GetMapping("/daily-inbound")
    public R<List<Map<String, Object>>> getDailyInboundStats(
            @Parameter(description = "开始日期") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        List<Map<String, Object>> stats = orderStatsService.getDailyInboundStats(startDate, endDate);
        return R.ok(stats);
    }

    /**
     * 获取每日出库统计
     */
    @Operation(summary = "获取每日出库统计")
    @GetMapping("/daily-outbound")
    public R<List<Map<String, Object>>> getDailyOutboundStats(
            @Parameter(description = "开始日期") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        List<Map<String, Object>> stats = orderStatsService.getDailyOutboundStats(startDate, endDate);
        return R.ok(stats);
    }

    /**
     * 获取商品入库排行
     */
    @Operation(summary = "获取商品入库排行")
    @GetMapping("/inbound-rank")
    public R<List<Map<String, Object>>> getProductInboundRank(
            @Parameter(description = "开始日期") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) LocalDate endDate,
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "10") int limit) {
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        List<Map<String, Object>> stats = orderStatsService.getProductInboundRank(startDate, endDate, limit);
        return R.ok(stats);
    }

    /**
     * 获取商品出库排行
     */
    @Operation(summary = "获取商品出库排行")
    @GetMapping("/outbound-rank")
    public R<List<Map<String, Object>>> getProductOutboundRank(
            @Parameter(description = "开始日期") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) LocalDate endDate,
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "10") int limit) {
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        List<Map<String, Object>> stats = orderStatsService.getProductOutboundRank(startDate, endDate, limit);
        return R.ok(stats);
    }
}
