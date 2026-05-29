package com.wms.service;

import com.wms.model.vo.DashboardVO;

/**
 * 首页服务接口
 */
public interface DashboardService {

    /**
     * 获取首页统计数据
     *
     * 包含：
     * - 商品总数
     * - 仓库数量
     * - 今日入库/出库单数和数量
     * - 库存总量
     * - 最近入库/出库单
     *
     * @return DashboardVO 统计数据
     */
    DashboardVO getDashboardStats();

    /**
     * 清除仪表盘缓存
     * 在入库、出库等操作后调用
     */
    void clearDashboardCache();
}
