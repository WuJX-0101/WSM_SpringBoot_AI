package com.wms.web.controller;

import com.wms.common.core.PageResult;
import com.wms.common.core.R;
import com.wms.model.dto.InboundOrderDTO;
import com.wms.model.entity.WmsInboundOrder;
import com.wms.model.vo.InboundOrderVO;
import com.wms.service.InboundOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 入库管理接口
 */
@Tag(name = "入库管理接口")
@RestController
@RequestMapping("/api/v1/inbound")
@RequiredArgsConstructor
public class InboundOrderController {

    private final InboundOrderService inboundOrderService;

    /**
     * 创建入库单
     */
    @Operation(summary = "创建入库单")
    @PostMapping
    public R<WmsInboundOrder> create(@Valid @RequestBody InboundOrderDTO dto) {
        WmsInboundOrder order = inboundOrderService.create(dto);
        return R.ok(order, "创建成功");
    }

    /**
     * 审核入库单
     */
    @Operation(summary = "审核入库单")
    @PutMapping("/{id}/audit")
    public R<Void> audit(@Parameter(description = "入库单ID") @PathVariable Long id) {
        inboundOrderService.audit(id);
        return R.ok(null, "审核成功");
    }

    /**
     * 执行入库
     */
    @Operation(summary = "执行入库")
    @PutMapping("/{id}/execute")
    public R<Void> execute(@Parameter(description = "入库单ID") @PathVariable Long id) {
        inboundOrderService.execute(id);
        return R.ok(null, "入库成功");
    }

    /**
     * 取消入库单
     */
    @Operation(summary = "取消入库单")
    @PutMapping("/{id}/cancel")
    public R<Void> cancel(@Parameter(description = "入库单ID") @PathVariable Long id) {
        inboundOrderService.cancel(id);
        return R.ok(null, "取消成功");
    }

    /**
     * 查询入库单详情（包含明细和商品信息）
     */
    @Operation(summary = "查询入库单详情")
    @GetMapping("/{id}")
    public R<InboundOrderVO> getById(@Parameter(description = "入库单ID") @PathVariable Long id) {
        InboundOrderVO order = inboundOrderService.getById(id);
        return R.ok(order);
    }

    /**
     * 分页查询入库单列表
     */
    @Operation(summary = "分页查询入库单列表")
    @GetMapping("/list")
    public R<PageResult<InboundOrderVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        PageResult<InboundOrderVO> result = inboundOrderService.list(page, size, keyword, status);
        return R.ok(result);
    }
}
