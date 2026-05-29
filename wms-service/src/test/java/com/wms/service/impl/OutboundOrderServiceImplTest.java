package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.exception.BusinessException;
import com.wms.dao.mapper.*;
import com.wms.model.dto.OutboundOrderDTO;
import com.wms.model.entity.*;
import com.wms.model.vo.OutboundOrderVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * OutboundOrderServiceImpl 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("出库单服务单元测试")
class OutboundOrderServiceImplTest {

    @Mock
    private WmsOutboundOrderMapper outboundOrderMapper;

    @Mock
    private WmsOutboundOrderItemMapper outboundOrderItemMapper;

    @Mock
    private WmsInventoryMapper inventoryMapper;

    @Mock
    private WmsInventoryLogMapper inventoryLogMapper;

    @Mock
    private WmsCustomerMapper customerMapper;

    @Mock
    private WmsWarehouseMapper warehouseMapper;

    @Mock
    private WmsProductMapper productMapper;

    @Mock
    private WmsLocationMapper locationMapper;

    @InjectMocks
    private OutboundOrderServiceImpl outboundOrderService;

    private WmsOutboundOrder testOrder;
    private OutboundOrderDTO createDTO;

    @BeforeEach
    void setUp() {
        // 准备测试订单数据
        testOrder = new WmsOutboundOrder();
        testOrder.setId(1L);
        testOrder.setOrderNo("OUT20260529001");
        testOrder.setOrderType(1); // 销售出库
        testOrder.setWarehouseId(1L);
        testOrder.setCustomerId(1L);
        testOrder.setOrderStatus(0); // 待审核
        testOrder.setTotalQuantity(100);
        testOrder.setTotalAmount(new BigDecimal("10000"));

        // 准备创建DTO
        createDTO = new OutboundOrderDTO();
        createDTO.setOrderType(1);
        createDTO.setWarehouseId(1L);
        createDTO.setCustomerId(1L);
        createDTO.setRemark("测试出库单");
    }

    @Test
    @DisplayName("创建出库单 - 自动生成单号")
    void create_AutoGenerateOrderNo() {
        // Given
        when(outboundOrderMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(outboundOrderMapper.insert(any(WmsOutboundOrder.class))).thenReturn(1);

        // When
        WmsOutboundOrder result = outboundOrderService.create(createDTO);

        // Then
        assertNotNull(result);
        verify(outboundOrderMapper).insert(any(WmsOutboundOrder.class));
    }

    @Test
    @DisplayName("审核出库单 - 成功")
    void audit_Success() {
        // Given
        when(outboundOrderMapper.selectById(1L)).thenReturn(testOrder);
        when(outboundOrderMapper.updateById(any(WmsOutboundOrder.class))).thenReturn(1);

        // When
        assertDoesNotThrow(() -> outboundOrderService.audit(1L));

        // Then
        verify(outboundOrderMapper).updateById(argThat(order -> order.getOrderStatus() == 1));
    }

    @Test
    @DisplayName("审核出库单 - 出库单不存在")
    void audit_OrderNotFound() {
        // Given
        when(outboundOrderMapper.selectById(999L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            outboundOrderService.audit(999L);
        });
        assertEquals("出库单不存在", exception.getMessage());
    }

    @Test
    @DisplayName("审核出库单 - 状态不是待审核")
    void audit_InvalidStatus() {
        // Given
        testOrder.setOrderStatus(1); // 已审核
        when(outboundOrderMapper.selectById(1L)).thenReturn(testOrder);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            outboundOrderService.audit(1L);
        });
        assertEquals("出库单状态不是待审核", exception.getMessage());
    }

    @Test
    @DisplayName("执行出库 - 成功")
    void execute_Success() {
        // Given
        testOrder.setOrderStatus(1); // 已审核
        when(outboundOrderMapper.selectById(1L)).thenReturn(testOrder);

        WmsOutboundOrderItem item = new WmsOutboundOrderItem();
        item.setId(1L);
        item.setOrderId(1L);
        item.setProductId(1L);
        item.setLocationId(1L);
        item.setBatchNo("BATCH001");
        item.setQuantity(10);
        item.setPrice(new BigDecimal("100"));
        when(outboundOrderItemMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.singletonList(item));

        WmsInventory inventory = new WmsInventory();
        inventory.setId(1L);
        inventory.setQuantity(100);
        inventory.setLockedQuantity(0);
        inventory.setAvailableQuantity(100);
        when(inventoryMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(inventory);
        when(inventoryMapper.updateById(any(WmsInventory.class))).thenReturn(1);
        when(inventoryLogMapper.insert(any(WmsInventoryLog.class))).thenReturn(1);
        when(outboundOrderMapper.updateById(any(WmsOutboundOrder.class))).thenReturn(1);

        // When
        assertDoesNotThrow(() -> outboundOrderService.execute(1L));

        // Then
        verify(inventoryMapper).updateById(any(WmsInventory.class));
        verify(inventoryLogMapper).insert(any(WmsInventoryLog.class));
        verify(outboundOrderMapper).updateById(argThat(order -> order.getOrderStatus() == 2));
    }

    @Test
    @DisplayName("执行出库 - 库存不存在")
    void execute_InventoryNotFound() {
        // Given
        testOrder.setOrderStatus(1);
        when(outboundOrderMapper.selectById(1L)).thenReturn(testOrder);

        WmsOutboundOrderItem item = new WmsOutboundOrderItem();
        item.setProductId(1L);
        item.setQuantity(10);
        when(outboundOrderItemMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.singletonList(item));

        when(inventoryMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            outboundOrderService.execute(1L);
        });
        assertEquals("商品库存不存在", exception.getMessage());
    }

    @Test
    @DisplayName("执行出库 - 可用库存不足")
    void execute_InsufficientStock() {
        // Given
        testOrder.setOrderStatus(1);
        when(outboundOrderMapper.selectById(1L)).thenReturn(testOrder);

        WmsOutboundOrderItem item = new WmsOutboundOrderItem();
        item.setProductId(1L);
        item.setQuantity(100);
        when(outboundOrderItemMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.singletonList(item));

        WmsInventory inventory = new WmsInventory();
        inventory.setQuantity(50);
        inventory.setLockedQuantity(0);
        inventory.setAvailableQuantity(50);
        when(inventoryMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(inventory);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            outboundOrderService.execute(1L);
        });
        assertEquals("商品可用库存不足", exception.getMessage());
    }

    @Test
    @DisplayName("取消出库单 - 成功")
    void cancel_Success() {
        // Given
        when(outboundOrderMapper.selectById(1L)).thenReturn(testOrder);
        when(outboundOrderMapper.updateById(any(WmsOutboundOrder.class))).thenReturn(1);

        // When
        assertDoesNotThrow(() -> outboundOrderService.cancel(1L));

        // Then
        verify(outboundOrderMapper).updateById(argThat(order -> order.getOrderStatus() == 3));
    }

    @Test
    @DisplayName("取消出库单 - 已出库不能取消")
    void cancel_CannotCancelCompleted() {
        // Given
        testOrder.setOrderStatus(2); // 已出库
        when(outboundOrderMapper.selectById(1L)).thenReturn(testOrder);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            outboundOrderService.cancel(1L);
        });
        assertEquals("已出库的订单不能取消", exception.getMessage());
    }

    @Test
    @DisplayName("查询出库单详情 - 成功")
    void getById_Success() {
        // Given
        when(outboundOrderMapper.selectById(1L)).thenReturn(testOrder);

        WmsCustomer customer = new WmsCustomer();
        customer.setId(1L);
        customer.setCustomerName("测试客户");
        when(customerMapper.selectById(1L)).thenReturn(customer);

        WmsWarehouse warehouse = new WmsWarehouse();
        warehouse.setId(1L);
        warehouse.setWarehouseName("测试仓库");
        when(warehouseMapper.selectById(1L)).thenReturn(warehouse);

        when(outboundOrderItemMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        // When
        OutboundOrderVO result = outboundOrderService.getById(1L);

        // Then
        assertNotNull(result);
        assertEquals("OUT20260529001", result.getOrderNo());
        assertEquals("测试客户", result.getCustomerName());
        assertEquals("测试仓库", result.getWarehouseName());
    }

    @Test
    @DisplayName("查询出库单详情 - 出库单不存在")
    void getById_OrderNotFound() {
        // Given
        when(outboundOrderMapper.selectById(999L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            outboundOrderService.getById(999L);
        });
        assertEquals("出库单不存在", exception.getMessage());
    }
}
