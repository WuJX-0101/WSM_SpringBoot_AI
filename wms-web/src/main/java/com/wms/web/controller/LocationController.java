package com.wms.web.controller;

import com.wms.common.core.PageResult;
import com.wms.common.core.R;
import com.wms.model.dto.LocationDTO;
import com.wms.model.entity.WmsLocation;
import com.wms.model.vo.LocationVO;
import com.wms.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 库位管理接口
 */
@Tag(name = "库位管理接口")
@RestController
@RequestMapping("/api/v1/location")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    /**
     * 创建库位
     */
    @Operation(summary = "创建库位")
    @PostMapping
    public R<WmsLocation> create(@Valid @RequestBody LocationDTO dto) {
        WmsLocation location = locationService.create(dto);
        return R.ok(location, "创建成功");
    }

    /**
     * 更新库位
     */
    @Operation(summary = "更新库位")
    @PutMapping("/{id}")
    public R<WmsLocation> update(
            @Parameter(description = "库位ID") @PathVariable Long id,
            @Valid @RequestBody LocationDTO dto) {
        WmsLocation location = locationService.update(id, dto);
        return R.ok(location, "更新成功");
    }

    /**
     * 删除库位
     */
    @Operation(summary = "删除库位")
    @DeleteMapping("/{id}")
    public R<Void> delete(@Parameter(description = "库位ID") @PathVariable Long id) {
        locationService.delete(id);
        return R.ok(null, "删除成功");
    }

    /**
     * 查询库位详情
     */
    @Operation(summary = "查询库位详情")
    @GetMapping("/{id}")
    public R<WmsLocation> getById(@Parameter(description = "库位ID") @PathVariable Long id) {
        WmsLocation location = locationService.getById(id);
        return R.ok(location);
    }

    /**
     * 分页查询库位列表
     */
    @Operation(summary = "分页查询库位列表")
    @GetMapping("/list")
    public R<PageResult<LocationVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "仓库ID") @RequestParam(required = false) Long warehouseId) {
        PageResult<LocationVO> result = locationService.list(page, size, keyword, warehouseId);
        return R.ok(result);
    }

    /**
     * 查询仓库下的库位列表
     */
    @Operation(summary = "查询仓库下的库位列表")
    @GetMapping("/warehouse/{warehouseId}")
    public R<List<WmsLocation>> listByWarehouseId(
            @Parameter(description = "仓库ID") @PathVariable Long warehouseId) {
        List<WmsLocation> result = locationService.listByWarehouseId(warehouseId);
        return R.ok(result);
    }
}
