package com.wms.web.controller;

import com.wms.common.core.R;
import com.wms.model.vo.DashboardVO;
import com.wms.service.DashboardService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页统计接口
 */
@Tag(name = "首页统计接口")
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 获取首页统计数据
     * 
     * 包含：
     * - 商品总数
     * - 仓库数量
     * - 今日入库/出库单数和数量
     * - 库存总量
     * - 最近入库/出库单
     */
    @Operation(summary = "获取首页统计数据")
    @GetMapping("/stats")
    @SaCheckPermission("report:order")
    public R<DashboardVO> getDashboardStats() {
        DashboardVO stats = dashboardService.getDashboardStats();
        return R.ok(stats);
    }
}
