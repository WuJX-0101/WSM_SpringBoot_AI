package com.wms.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 异常检测VO
 */
@Data
@Schema(description = "异常检测结果")
public class AnomalyDetectionVO {

    @Schema(description = "是否存在异常")
    private Boolean hasAnomaly;

    @Schema(description = "异常数量")
    private Integer anomalyCount;

    @Schema(description = "异常列表")
    private List<AnomalyItem> anomalies;

    @Schema(description = "整体建议")
    private String suggestion;

    @Data
    @Schema(description = "异常项")
    public static class AnomalyItem {

        @Schema(description = "异常类型（库存异常/订单异常/价格异常）")
        private String type;

        @Schema(description = "异常级别（高/中/低）")
        private String level;

        @Schema(description = "异常描述")
        private String description;

        @Schema(description = "涉及商品/订单")
        private String target;

        @Schema(description = "建议处理方式")
        private String suggestion;
    }
}
