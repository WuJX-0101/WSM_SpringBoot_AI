package com.wms.web.controller;

import com.wms.common.core.R;
import com.wms.integration.service.EcommerceIntegrationService;
import com.wms.integration.service.ErpIntegrationService;
import com.wms.integration.service.FinanceIntegrationService;
import com.wms.model.vo.SyncResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统集成接口
 */
@Tag(name = "系统集成接口")
@RestController
@RequestMapping("/api/v1/integration")
@RequiredArgsConstructor
public class IntegrationController {

    private final ErpIntegrationService erpIntegrationService;
    private final EcommerceIntegrationService ecommerceIntegrationService;
    private final FinanceIntegrationService financeIntegrationService;

    /**
     * 测试所有集成连接
     */
    @Operation(summary = "测试所有集成连接")
    @GetMapping("/test-connections")
    public R<Map<String, Boolean>> testConnections() {
        Map<String, Boolean> results = new HashMap<>();
        results.put("erp", erpIntegrationService.testConnection());
        results.put("ecommerce", ecommerceIntegrationService.testConnection());
        results.put("finance", financeIntegrationService.testConnection());
        return R.ok(results);
    }

    // ==================== ERP集成 ====================

    /**
     * 同步商品到ERP
     */
    @Operation(summary = "同步商品到ERP")
    @PostMapping("/erp/sync-products")
    public R<SyncResultVO> syncProductsToErp() {
        SyncResultVO result = erpIntegrationService.syncProducts();
        return R.ok(result);
    }

    /**
     * 同步库存到ERP
     */
    @Operation(summary = "同步库存到ERP")
    @PostMapping("/erp/sync-inventory")
    public R<SyncResultVO> syncInventoryToErp() {
        SyncResultVO result = erpIntegrationService.syncInventory();
        return R.ok(result);
    }

    /**
     * 同步入库单到ERP
     */
    @Operation(summary = "同步入库单到ERP")
    @PostMapping("/erp/sync-inbound")
    public R<SyncResultVO> syncInboundToErp() {
        SyncResultVO result = erpIntegrationService.syncInboundOrders();
        return R.ok(result);
    }

    /**
     * 同步出库单到ERP
     */
    @Operation(summary = "同步出库单到ERP")
    @PostMapping("/erp/sync-outbound")
    public R<SyncResultVO> syncOutboundToErp() {
        SyncResultVO result = erpIntegrationService.syncOutboundOrders();
        return R.ok(result);
    }

    /**
     * 从ERP拉取采购订单
     */
    @Operation(summary = "从ERP拉取采购订单")
    @PostMapping("/erp/pull-purchase-orders")
    public R<SyncResultVO> pullPurchaseOrdersFromErp() {
        SyncResultVO result = erpIntegrationService.pullPurchaseOrders();
        return R.ok(result);
    }

    // ==================== 电商平台集成 ====================

    /**
     * 同步商品到电商平台
     */
    @Operation(summary = "同步商品到电商平台")
    @PostMapping("/ecommerce/sync-products")
    public R<SyncResultVO> syncProductsToEcommerce() {
        SyncResultVO result = ecommerceIntegrationService.syncProducts();
        return R.ok(result);
    }

    /**
     * 同步库存到电商平台
     */
    @Operation(summary = "同步库存到电商平台")
    @PostMapping("/ecommerce/sync-inventory")
    public R<SyncResultVO> syncInventoryToEcommerce() {
        SyncResultVO result = ecommerceIntegrationService.syncInventory();
        return R.ok(result);
    }

    /**
     * 从电商平台拉取订单
     */
    @Operation(summary = "从电商平台拉取订单")
    @PostMapping("/ecommerce/pull-orders")
    public R<SyncResultVO> pullOrdersFromEcommerce() {
        SyncResultVO result = ecommerceIntegrationService.pullOrders();
        return R.ok(result);
    }

    /**
     * 推送发货信息到电商平台
     */
    @Operation(summary = "推送发货信息到电商平台")
    @PostMapping("/ecommerce/push-shipping")
    public R<SyncResultVO> pushShippingToEcommerce() {
        SyncResultVO result = ecommerceIntegrationService.pushShippingInfo();
        return R.ok(result);
    }

    // ==================== 财务系统集成 ====================

    /**
     * 同步入库单到财务系统
     */
    @Operation(summary = "同步入库单到财务系统")
    @PostMapping("/finance/sync-inbound")
    public R<SyncResultVO> syncInboundToFinance() {
        SyncResultVO result = financeIntegrationService.syncInboundOrders();
        return R.ok(result);
    }

    /**
     * 同步出库单到财务系统
     */
    @Operation(summary = "同步出库单到财务系统")
    @PostMapping("/finance/sync-outbound")
    public R<SyncResultVO> syncOutboundToFinance() {
        SyncResultVO result = financeIntegrationService.syncOutboundOrders();
        return R.ok(result);
    }

    /**
     * 同步库存成本到财务系统
     */
    @Operation(summary = "同步库存成本到财务系统")
    @PostMapping("/finance/sync-cost")
    public R<SyncResultVO> syncCostToFinance() {
        SyncResultVO result = financeIntegrationService.syncInventoryCost();
        return R.ok(result);
    }

    /**
     * 推送应收应付数据到财务系统
     */
    @Operation(summary = "推送应收应付数据到财务系统")
    @PostMapping("/finance/push-receivable-payable")
    public R<SyncResultVO> pushReceivablePayableToFinance() {
        SyncResultVO result = financeIntegrationService.pushReceivablePayable();
        return R.ok(result);
    }
}
