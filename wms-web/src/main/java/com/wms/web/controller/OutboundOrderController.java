package com.wms.web.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.wms.common.core.PageResult;
import com.wms.common.core.R;
import com.wms.model.dto.OutboundOrderDTO;
import com.wms.model.entity.WmsOutboundOrder;
import com.wms.model.vo.OutboundOrderVO;
import com.wms.service.OutboundOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 出库管理接口
 */
@Tag(name = "出库管理接口")
@RestController
@RequestMapping("/api/v1/outbound")
@RequiredArgsConstructor
public class OutboundOrderController {

    private final OutboundOrderService outboundOrderService;

    /**
     * 创建出库单
     */
    @Operation(summary = "创建出库单")
    @PostMapping
    @SaCheckPermission("outbound:create")
    public R<WmsOutboundOrder> create(@Valid @RequestBody OutboundOrderDTO dto) {
        WmsOutboundOrder order = outboundOrderService.create(dto);
        return R.ok(order, "创建成功");
    }

    /**
     * 审核出库单
     */
    @Operation(summary = "审核出库单")
    @PutMapping("/{id}/audit")
    @SaCheckPermission("outbound:audit")
    public R<Void> audit(@Parameter(description = "出库单ID") @PathVariable Long id) {
        outboundOrderService.audit(id);
        return R.ok(null, "审核成功");
    }

    /**
     * 执行出库
     */
    @Operation(summary = "执行出库")
    @PutMapping("/{id}/execute")
    @SaCheckPermission("outbound:execute")
    public R<Void> execute(@Parameter(description = "出库单ID") @PathVariable Long id) {
        outboundOrderService.execute(id);
        return R.ok(null, "出库成功");
    }

    /**
     * 取消出库单
     */
    @Operation(summary = "取消出库单")
    @PutMapping("/{id}/cancel")
    @SaCheckPermission("outbound:cancel")
    public R<Void> cancel(@Parameter(description = "出库单ID") @PathVariable Long id) {
        outboundOrderService.cancel(id);
        return R.ok(null, "取消成功");
    }

    /**
     * 查询出库单详情（包含明细和商品信息）
     */
    @Operation(summary = "查询出库单详情")
    @GetMapping("/{id}")
    @SaCheckPermission("outbound:view")
    public R<OutboundOrderVO> getById(@Parameter(description = "出库单ID") @PathVariable Long id) {
        OutboundOrderVO order = outboundOrderService.getById(id);
        return R.ok(order);
    }

    /**
     * 分页查询出库单列表
     */
    @Operation(summary = "分页查询出库单列表")
    @GetMapping("/list")
    @SaCheckPermission("outbound:list")
    public R<PageResult<OutboundOrderVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        PageResult<OutboundOrderVO> result = outboundOrderService.list(page, size, keyword, status);
        return R.ok(result);
    }
}
