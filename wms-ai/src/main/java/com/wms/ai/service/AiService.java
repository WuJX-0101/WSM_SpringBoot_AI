package com.wms.ai.service;

import com.wms.model.vo.AnomalyDetectionVO;
import com.wms.model.vo.DemandForecastVO;
import com.wms.model.vo.ReplenishmentVO;

/**
 * AI服务接口
 */
public interface AiService {

    /**
     * 需求预测
     * 
     * @param productId   商品ID
     * @param forecastDays 预测天数
     * @return 预测结果
     */
    DemandForecastVO forecastDemand(Long productId, Integer forecastDays);

    /**
     * 智能补货建议
     * 
     * @param warehouseId 仓库ID（可选，null表示所有仓库）
     * @return 补货建议
     */
    ReplenishmentVO getReplenishmentSuggestion(Long warehouseId);

    /**
     * 异常检测
     * 
     * @param warehouseId 仓库ID（可选，null表示所有仓库）
     * @return 异常检测结果
     */
    AnomalyDetectionVO detectAnomaly(Long warehouseId);

    /**
     * 自然语言查询
     * 
     * @param question 用户问题
     * @return AI回答
     */
    String chat(String question);
}
