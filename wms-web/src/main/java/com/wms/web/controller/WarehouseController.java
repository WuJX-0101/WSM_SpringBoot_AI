package com.wms.web.controller;

import com.wms.common.core.PageResult;
import com.wms.common.core.R;
import com.wms.model.dto.WarehouseDTO;
import com.wms.model.entity.WmsWarehouse;
import com.wms.service.WarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "仓库管理接口")
@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @Operation(summary = "创建仓库")
    @PostMapping
    public R<WmsWarehouse> create(@Valid @RequestBody WarehouseDTO dto) {
        WmsWarehouse warehouse = warehouseService.create(dto);
        return R.ok(warehouse, "创建成功");
    }

    @Operation(summary = "更新仓库")
    @PutMapping("/{id}")
    public R<WmsWarehouse> update(
            @Parameter(description = "仓库ID") @PathVariable Long id,
            @Valid @RequestBody WarehouseDTO dto) {
        WmsWarehouse warehouse = warehouseService.update(id, dto);
        return R.ok(warehouse, "更新成功");
    }

    @Operation(summary = "删除仓库")
    @DeleteMapping("/{id}")
    public R<Void> delete(@Parameter(description = "仓库ID") @PathVariable Long id) {
        warehouseService.delete(id);
        return R.ok(null, "删除成功");
    }

    @Operation(summary = "查询仓库详情")
    @GetMapping("/{id}")
    public R<WmsWarehouse> getById(@Parameter(description = "仓库ID") @PathVariable Long id) {
        WmsWarehouse warehouse = warehouseService.getById(id);
        return R.ok(warehouse);
    }

    @Operation(summary = "分页查询仓库列表")
    @GetMapping("/list")
    public R<PageResult<WmsWarehouse>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword) {
        PageResult<WmsWarehouse> result = warehouseService.list(page, size, keyword);
        return R.ok(result);
    }
}
