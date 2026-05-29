package com.wms.dao.mapper;

import com.wms.model.vo.OrderStatsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单统计Mapper
 * 使用SQL聚合查询，避免全量加载到内存
 */
@Mapper
public interface OrderStatsMapper {

    /**
     * 获取订单统计（使用SQL聚合）
     */
    OrderStatsVO getOrderStats(@Param("startTime") LocalDateTime startTime,
                               @Param("endTime") LocalDateTime endTime);

    /**
     * 获取仪表盘统计（使用SQL聚合）
     */
    Map<String, Object> getDashboardStats();

    /**
     * 获取每日入库统计（使用SQL GROUP BY）
     */
    List<Map<String, Object>> getDailyInboundStats(@Param("startTime") LocalDateTime startTime,
                                                   @Param("endTime") LocalDateTime endTime);

    /**
     * 获取每日出库统计（使用SQL GROUP BY）
     */
    List<Map<String, Object>> getDailyOutboundStats(@Param("startTime") LocalDateTime startTime,
                                                    @Param("endTime") LocalDateTime endTime);

    /**
     * 获取商品入库排行（使用SQL JOIN + GROUP BY）
     */
    List<Map<String, Object>> getProductInboundRank(@Param("startTime") LocalDateTime startTime,
                                                    @Param("endTime") LocalDateTime endTime,
                                                    @Param("limit") int limit);

    /**
     * 获取商品出库排行（使用SQL JOIN + GROUP BY）
     */
    List<Map<String, Object>> getProductOutboundRank(@Param("startTime") LocalDateTime startTime,
                                                     @Param("endTime") LocalDateTime endTime,
                                                     @Param("limit") int limit);
}
