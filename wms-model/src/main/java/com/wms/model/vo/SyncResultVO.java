package com.wms.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 同步结果VO
 */
@Data
@Schema(description = "同步结果")
public class SyncResultVO {

    @Schema(description = "是否成功")
    private Boolean success;

    @Schema(description = "同步数量")
    private Integer syncCount;

    @Schema(description = "成功数量")
    private Integer successCount;

    @Schema(description = "失败数量")
    private Integer failCount;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "同步详情")
    private String detail;
}
