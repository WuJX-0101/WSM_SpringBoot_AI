package com.wms.service;

import com.wms.model.vo.OrderStatsVO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 订单统计服务接口
 */
public interface OrderStatsService {

    /**
     * 获取订单统计数据
     * 
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 统计结果
     */
    OrderStatsVO getStats(LocalDate startDate, LocalDate endDate);

    /**
     * 获取每日入库统计
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 每日统计数据列表 [{date, count}, ...]
     */
    List<Map<String, Object>> getDailyInboundStats(LocalDate startDate, LocalDate endDate);

    /**
     * 获取每日出库统计
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 每日统计数据列表 [{date, count}, ...]
     */
    List<Map<String, Object>> getDailyOutboundStats(LocalDate startDate, LocalDate endDate);

    /**
     * 获取商品入库排行
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param limit     返回数量
     * @return 商品入库排行 [{productName, quantity}, ...]
     */
    List<Map<String, Object>> getProductInboundRank(LocalDate startDate, LocalDate endDate, int limit);

    /**
     * 获取商品出库排行
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param limit     返回数量
     * @return 商品出库排行 [{productName, quantity}, ...]
     */
    List<Map<String, Object>> getProductOutboundRank(LocalDate startDate, LocalDate endDate, int limit);
}
