package com.wms.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 员工更新DTO
 * 
 * 用于修改员工信息（不含用户名和密码）
 */
@Data
@Schema(description = "员工更新请求")
public class EmployeeUpdateDTO {

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phone;

    @NotEmpty(message = "角色不能为空")
    @Schema(description = "角色ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> roleIds;

    @Schema(description = "状态（0：禁用，1：启用）")
    private Integer status;
}
