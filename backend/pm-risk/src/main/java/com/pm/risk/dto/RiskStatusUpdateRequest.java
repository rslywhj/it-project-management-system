package com.pm.risk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "风险状态更新请求")
public class RiskStatusUpdateRequest {

    @NotBlank(message = "目标状态不能为空")
    @Schema(description = "目标状态", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;
}
