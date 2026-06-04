package com.pm.promotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "审批配置差异请求")
public class UnitConfigDiffApproveRequest {

    @NotNull(message = "审批结果不能为空")
    @Schema(description = "审批结果：approved/rejected", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;

    @Schema(description = "审批意见")
    private String remark;
}
