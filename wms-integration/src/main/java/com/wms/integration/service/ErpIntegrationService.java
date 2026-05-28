package com.wms.integration.service;

import com.wms.model.vo.SyncResultVO;

/**
 * ERP集成服务接口
 */
public interface ErpIntegrationService {

    /**
     * 同步商品数据到ERP
     * 
     * @return 同步结果
     */
    SyncResultVO syncProducts();

    /**
     * 同步库存数据到ERP
     * 
     * @return 同步结果
     */
    SyncResultVO syncInventory();

    /**
     * 同步入库单到ERP
     * 
     * @return 同步结果
     */
    SyncResultVO syncInboundOrders();

    /**
     * 同步出库单到ERP
     * 
     * @return 同步结果
     */
    SyncResultVO syncOutboundOrders();

    /**
     * 从ERP拉取采购订单
     * 
     * @return 同步结果
     */
    SyncResultVO pullPurchaseOrders();

    /**
     * 测试ERP连接
     * 
     * @return 连接是否成功
     */
    boolean testConnection();
}
