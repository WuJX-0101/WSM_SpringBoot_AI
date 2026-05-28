package com.wms.integration.service;

import com.wms.model.vo.SyncResultVO;

/**
 * 财务系统集成服务接口
 */
public interface FinanceIntegrationService {

    /**
     * 同步采购入库单到财务系统
     * 
     * @return 同步结果
     */
    SyncResultVO syncInboundOrders();

    /**
     * 同步销售出库单到财务系统
     * 
     * @return 同步结果
     */
    SyncResultVO syncOutboundOrders();

    /**
     * 同步库存成本到财务系统
     * 
     * @return 同步结果
     */
    SyncResultVO syncInventoryCost();

    /**
     * 推送应收应付数据
     * 
     * @return 同步结果
     */
    SyncResultVO pushReceivablePayable();

    /**
     * 测试财务系统连接
     * 
     * @return 连接是否成功
     */
    boolean testConnection();
}
