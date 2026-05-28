package com.wms.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 供应商请求参数DTO
 */
@Data
@Schema(description = "供应商请求参数")
public class SupplierDTO {

    @NotBlank(message = "供应商编码不能为空")
    @Schema(description = "供应商编码", example = "SUP001")
    private String supplierCode;

    @NotBlank(message = "供应商名称不能为空")
    @Schema(description = "供应商名称", example = "苹果供应链公司")
    private String supplierName;

    @Schema(description = "联系人", example = "张三")
    private String contactPerson;

    @Schema(description = "联系电话", example = "13800138000")
    private String contactPhone;

    @Schema(description = "邮箱", example = "supplier@example.com")
    private String email;

    @Schema(description = "地址", example = "上海市浦东新区")
    private String address;

    @Schema(description = "开户行", example = "中国银行")
    private String bankName;

    @Schema(description = "银行账号", example = "6222021234567890123")
    private String bankAccount;

    @Schema(description = "税号", example = "91310000MA1FL5XQ3T")
    private String taxNumber;

    @Schema(description = "状态（1：启用，0：禁用）", example = "1")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
