package com.wms.integration.service;

import com.wms.model.vo.SyncResultVO;

/**
 * 电商平台集成服务接口
 */
public interface EcommerceIntegrationService {

    /**
     * 同步商品到电商平台
     * 
     * @return 同步结果
     */
    SyncResultVO syncProducts();

    /**
     * 同步库存到电商平台
     * 
     * @return 同步结果
     */
    SyncResultVO syncInventory();

    /**
     * 从电商平台拉取订单
     * 
     * @return 同步结果
     */
    SyncResultVO pullOrders();

    /**
     * 推送发货信息到电商平台
     * 
     * @return 同步结果
     */
    SyncResultVO pushShippingInfo();

    /**
     * 测试电商平台连接
     * 
     * @return 连接是否成功
     */
    boolean testConnection();
}
