package com.wms.web.controller;

import com.wms.ai.service.AiService;
import com.wms.common.core.R;
import com.wms.model.vo.AnomalyDetectionVO;
import com.wms.model.vo.DemandForecastVO;
import com.wms.model.vo.ReplenishmentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * AI智能分析接口
 */
@Tag(name = "AI智能分析接口")
@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    /**
     * 需求预测
     */
    @Operation(summary = "需求预测")
    @GetMapping("/forecast/{productId}")
    public R<DemandForecastVO> forecastDemand(
            @Parameter(description = "商品ID") @PathVariable Long productId,
            @Parameter(description = "预测天数") @RequestParam(defaultValue = "7") Integer forecastDays) {
        DemandForecastVO result = aiService.forecastDemand(productId, forecastDays);
        return R.ok(result);
    }

    /**
     * 智能补货建议
     */
    @Operation(summary = "智能补货建议")
    @GetMapping("/replenishment")
    public R<ReplenishmentVO> getReplenishmentSuggestion(
            @Parameter(description = "仓库ID") @RequestParam(required = false) Long warehouseId) {
        ReplenishmentVO result = aiService.getReplenishmentSuggestion(warehouseId);
        return R.ok(result);
    }

    /**
     * 异常检测
     */
    @Operation(summary = "异常检测")
    @GetMapping("/anomaly")
    public R<AnomalyDetectionVO> detectAnomaly(
            @Parameter(description = "仓库ID") @RequestParam(required = false) Long warehouseId) {
        AnomalyDetectionVO result = aiService.detectAnomaly(warehouseId);
        return R.ok(result);
    }

    /**
     * 自然语言查询
     */
    @Operation(summary = "自然语言查询")
    @PostMapping("/chat")
    public R<String> chat(@Parameter(description = "用户问题") @RequestBody String question) {
        String answer = aiService.chat(question);
        return R.ok(answer);
    }
}
