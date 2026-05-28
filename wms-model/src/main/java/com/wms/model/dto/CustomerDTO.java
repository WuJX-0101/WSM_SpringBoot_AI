package com.wms.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 客户请求参数DTO
 */
@Data
@Schema(description = "客户请求参数")
public class CustomerDTO {

    @NotBlank(message = "客户编码不能为空")
    @Schema(description = "客户编码", example = "CUS001")
    private String customerCode;

    @NotBlank(message = "客户名称不能为空")
    @Schema(description = "客户名称", example = "京东商城")
    private String customerName;

    @Schema(description = "联系人", example = "李四")
    private String contactPerson;

    @Schema(description = "联系电话", example = "13900139000")
    private String contactPhone;

    @Schema(description = "邮箱", example = "customer@example.com")
    private String email;

    @Schema(description = "地址", example = "北京市朝阳区")
    private String address;

    @Schema(description = "客户类型（1：普通客户，2：VIP客户，3：企业客户）", example = "1")
    private Integer customerType;

    @Schema(description = "状态（1：启用，0：禁用）", example = "1")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
