package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.exception.BusinessException;
import com.wms.dao.mapper.*;
import com.wms.model.dto.InboundOrderDTO;
import com.wms.model.entity.*;
import com.wms.model.vo.InboundOrderVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * InboundOrderServiceImpl 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("入库单服务单元测试")
class InboundOrderServiceImplTest {

    @Mock
    private WmsInboundOrderMapper inboundOrderMapper;

    @Mock
    private WmsInboundOrderItemMapper inboundOrderItemMapper;

    @Mock
    private WmsInventoryMapper inventoryMapper;

    @Mock
    private WmsInventoryLogMapper inventoryLogMapper;

    @Mock
    private WmsSupplierMapper supplierMapper;

    @Mock
    private WmsWarehouseMapper warehouseMapper;

    @Mock
    private WmsProductMapper productMapper;

    @Mock
    private WmsLocationMapper locationMapper;

    @InjectMocks
    private InboundOrderServiceImpl inboundOrderService;

    private WmsInboundOrder testOrder;
    private InboundOrderDTO createDTO;

    @BeforeEach
    void setUp() {
        // 准备测试订单数据
        testOrder = new WmsInboundOrder();
        testOrder.setId(1L);
        testOrder.setOrderNo("IN20260529001");
        testOrder.setOrderType(1); // 采购入库
        testOrder.setWarehouseId(1L);
        testOrder.setSupplierId(1L);
        testOrder.setOrderStatus(0); // 待审核
        testOrder.setTotalQuantity(100);
        testOrder.setTotalAmount(new BigDecimal("10000"));

        // 准备创建DTO
        createDTO = new InboundOrderDTO();
        createDTO.setOrderType(1);
        createDTO.setWarehouseId(1L);
        createDTO.setSupplierId(1L);
        createDTO.setRemark("测试入库单");
    }

    @Test
    @DisplayName("创建入库单 - 自动生成单号")
    void create_AutoGenerateOrderNo() {
        // Given
        when(inboundOrderMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(inboundOrderMapper.insert(any(WmsInboundOrder.class))).thenReturn(1);

        // When
        WmsInboundOrder result = inboundOrderService.create(createDTO);

        // Then
        assertNotNull(result);
        verify(inboundOrderMapper).insert(any(WmsInboundOrder.class));
    }

    @Test
    @DisplayName("创建入库单 - 手动指定单号")
    void create_ManualOrderNo() {
        // Given
        createDTO.setOrderNo("IN20260529001");
        when(inboundOrderMapper.insert(any(WmsInboundOrder.class))).thenReturn(1);

        // When
        WmsInboundOrder result = inboundOrderService.create(createDTO);

        // Then
        assertNotNull(result);
        assertEquals("IN20260529001", result.getOrderNo());
    }

    @Test
    @DisplayName("审核入库单 - 成功")
    void audit_Success() {
        // Given
        when(inboundOrderMapper.selectById(1L)).thenReturn(testOrder);
        when(inboundOrderMapper.updateById(any(WmsInboundOrder.class))).thenReturn(1);

        // When
        assertDoesNotThrow(() -> inboundOrderService.audit(1L));

        // Then
        verify(inboundOrderMapper).updateById(argThat(order -> order.getOrderStatus() == 1));
    }

    @Test
    @DisplayName("审核入库单 - 入库单不存在")
    void audit_OrderNotFound() {
        // Given
        when(inboundOrderMapper.selectById(999L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            inboundOrderService.audit(999L);
        });
        assertEquals("入库单不存在", exception.getMessage());
    }

    @Test
    @DisplayName("审核入库单 - 状态不是待审核")
    void audit_InvalidStatus() {
        // Given
        testOrder.setOrderStatus(1); // 已审核
        when(inboundOrderMapper.selectById(1L)).thenReturn(testOrder);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            inboundOrderService.audit(1L);
        });
        assertEquals("入库单状态不是待审核", exception.getMessage());
    }

    @Test
    @DisplayName("执行入库 - 成功")
    void execute_Success() {
        // Given
        testOrder.setOrderStatus(1); // 已审核
        when(inboundOrderMapper.selectById(1L)).thenReturn(testOrder);

        WmsInboundOrderItem item = new WmsInboundOrderItem();
        item.setId(1L);
        item.setOrderId(1L);
        item.setProductId(1L);
        item.setLocationId(1L);
        item.setBatchNo("BATCH001");
        item.setQuantity(10);
        item.setPrice(new BigDecimal("100"));
        when(inboundOrderItemMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.singletonList(item));

        when(inventoryMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(inventoryMapper.insert(any(WmsInventory.class))).thenReturn(1);
        when(inventoryLogMapper.insert(any(WmsInventoryLog.class))).thenReturn(1);
        when(inboundOrderMapper.updateById(any(WmsInboundOrder.class))).thenReturn(1);

        // When
        assertDoesNotThrow(() -> inboundOrderService.execute(1L));

        // Then
        verify(inventoryMapper).insert(any(WmsInventory.class));
        verify(inventoryLogMapper).insert(any(WmsInventoryLog.class));
        verify(inboundOrderMapper).updateById(argThat(order -> order.getOrderStatus() == 2));
    }

    @Test
    @DisplayName("执行入库 - 入库单不存在")
    void execute_OrderNotFound() {
        // Given
        when(inboundOrderMapper.selectById(999L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            inboundOrderService.execute(999L);
        });
        assertEquals("入库单不存在", exception.getMessage());
    }

    @Test
    @DisplayName("执行入库 - 状态不是已审核")
    void execute_InvalidStatus() {
        // Given
        testOrder.setOrderStatus(0); // 待审核
        when(inboundOrderMapper.selectById(1L)).thenReturn(testOrder);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            inboundOrderService.execute(1L);
        });
        assertEquals("入库单状态不是已审核", exception.getMessage());
    }

    @Test
    @DisplayName("取消入库单 - 成功")
    void cancel_Success() {
        // Given
        when(inboundOrderMapper.selectById(1L)).thenReturn(testOrder);
        when(inboundOrderMapper.updateById(any(WmsInboundOrder.class))).thenReturn(1);

        // When
        assertDoesNotThrow(() -> inboundOrderService.cancel(1L));

        // Then
        verify(inboundOrderMapper).updateById(argThat(order -> order.getOrderStatus() == 3));
    }

    @Test
    @DisplayName("取消入库单 - 已入库不能取消")
    void cancel_CannotCancelCompleted() {
        // Given
        testOrder.setOrderStatus(2); // 已入库
        when(inboundOrderMapper.selectById(1L)).thenReturn(testOrder);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            inboundOrderService.cancel(1L);
        });
        assertEquals("已入库的订单不能取消", exception.getMessage());
    }

    @Test
    @DisplayName("查询入库单详情 - 成功")
    void getById_Success() {
        // Given
        when(inboundOrderMapper.selectById(1L)).thenReturn(testOrder);

        WmsSupplier supplier = new WmsSupplier();
        supplier.setId(1L);
        supplier.setSupplierName("测试供应商");
        when(supplierMapper.selectById(1L)).thenReturn(supplier);

        WmsWarehouse warehouse = new WmsWarehouse();
        warehouse.setId(1L);
        warehouse.setWarehouseName("测试仓库");
        when(warehouseMapper.selectById(1L)).thenReturn(warehouse);

        when(inboundOrderItemMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        // When
        InboundOrderVO result = inboundOrderService.getById(1L);

        // Then
        assertNotNull(result);
        assertEquals("IN20260529001", result.getOrderNo());
        assertEquals("测试供应商", result.getSupplierName());
        assertEquals("测试仓库", result.getWarehouseName());
    }

    @Test
    @DisplayName("查询入库单详情 - 入库单不存在")
    void getById_OrderNotFound() {
        // Given
        when(inboundOrderMapper.selectById(999L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            inboundOrderService.getById(999L);
        });
        assertEquals("入库单不存在", exception.getMessage());
    }

    @Test
    @DisplayName("分页查询入库单列表 - 成功")
    void list_Success() {
        // Given
        Page<WmsInboundOrder> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(testOrder));
        page.setTotal(1);
        when(inboundOrderMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(page);

        when(supplierMapper.selectBatchIds(anyList()))
                .thenReturn(Collections.singletonList(new WmsSupplier()));
        when(warehouseMapper.selectBatchIds(anyList()))
                .thenReturn(Collections.singletonList(new WmsWarehouse()));

        // When
        var result = inboundOrderService.list(1, 10, null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotal());
    }
}
