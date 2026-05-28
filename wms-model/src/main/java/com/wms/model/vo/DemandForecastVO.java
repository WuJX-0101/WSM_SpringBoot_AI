package com.wms.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 需求预测VO
 */
@Data
@Schema(description = "需求预测结果")
public class DemandForecastVO {

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "商品名称")
    private String productName;

    @Schema(description = "预测周期（天）")
    private Integer forecastDays;

    @Schema(description = "预测需求量")
    private Integer forecastQuantity;

    @Schema(description = "置信度（0-100）")
    private Integer confidence;

    @Schema(description = "预测依据")
    private String basis;

    @Schema(description = "预测说明")
    private String explanation;

    @Schema(description = "建议补货时间")
    private String replenishDate;

    @Schema(description = "历史销售数据")
    private List<HistoryData> historyData;

    @Schema(description = "预测趋势数据")
    private List<ForecastData> forecastData;

    @Schema(description = "预测建议")
    private String suggestion;

    @Data
    @Schema(description = "历史数据")
    public static class HistoryData {

        @Schema(description = "日期")
        private String date;

        @Schema(description = "数量")
        private Integer quantity;
    }

    @Data
    @Schema(description = "预测趋势数据")
    public static class ForecastData {

        @Schema(description = "日期")
        private String date;

        @Schema(description = "预测值")
        private Integer value;
    }
}
