package com.wms.web.controller;

import com.wms.common.core.PageResult;
import com.wms.common.core.R;
import com.wms.model.dto.InventoryAdjustDTO;
import com.wms.model.entity.WmsInventory;
import com.wms.model.entity.WmsInventoryLog;
import com.wms.model.vo.InventoryLogVO;
import com.wms.model.vo.InventoryVO;
import com.wms.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 库存管理接口
 */
@Tag(name = "库存管理接口")
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * 查询库存日志
     */
    @Operation(summary = "查询库存日志")
    @GetMapping("/log")
    public R<PageResult<InventoryLogVO>> listLog(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "商品ID") @RequestParam(required = false) Long productId,
            @Parameter(description = "仓库ID") @RequestParam(required = false) Long warehouseId) {
        PageResult<InventoryLogVO> result = inventoryService.listLog(page, size, productId, warehouseId);
        return R.ok(result);
    }

    /**
     * 查询库存详情
     */
    @Operation(summary = "查询库存详情")
    @GetMapping("/{id}")
    public R<WmsInventory> getById(@Parameter(description = "库存ID") @PathVariable Long id) {
        WmsInventory inventory = inventoryService.getById(id);
        return R.ok(inventory);
    }

    /**
     * 分页查询库存列表
     */
    @Operation(summary = "分页查询库存列表")
    @GetMapping("/list")
    public R<PageResult<InventoryVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "仓库ID") @RequestParam(required = false) Long warehouseId,
            @Parameter(description = "商品ID") @RequestParam(required = false) Long productId,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword) {
        PageResult<InventoryVO> result = inventoryService.list(page, size, warehouseId, productId, keyword);
        return R.ok(result);
    }

    /**
     * 查询仓库下的库存列表
     */
    @Operation(summary = "查询仓库下的库存列表")
    @GetMapping("/warehouse/{warehouseId}")
    public R<List<WmsInventory>> listByWarehouseId(
            @Parameter(description = "仓库ID") @PathVariable Long warehouseId) {
        List<WmsInventory> result = inventoryService.listByWarehouseId(warehouseId);
        return R.ok(result);
    }

    /**
     * 查询商品的库存列表
     */
    @Operation(summary = "查询商品的库存列表")
    @GetMapping("/product/{productId}")
    public R<List<WmsInventory>> listByProductId(
            @Parameter(description = "商品ID") @PathVariable Long productId) {
        List<WmsInventory> result = inventoryService.listByProductId(productId);
        return R.ok(result);
    }

    /**
     * 查询商品在指定仓库的总库存
     */
    @Operation(summary = "查询商品在指定仓库的总库存")
    @GetMapping("/total")
    public R<Integer> getTotalQuantity(
            @Parameter(description = "仓库ID") @RequestParam Long warehouseId,
            @Parameter(description = "商品ID") @RequestParam Long productId) {
        int total = inventoryService.getTotalQuantity(warehouseId, productId);
        return R.ok(total);
    }

    /**
     * 库存调整
     */
    @Operation(summary = "库存调整")
    @PutMapping("/{id}/adjust")
    public R<Void> adjust(
            @Parameter(description = "库存ID") @PathVariable Long id,
            @RequestBody InventoryAdjustDTO dto) {
        inventoryService.adjust(id, dto.getQuantity(), dto.getReason());
        return R.ok(null, "调整成功");
    }
}
