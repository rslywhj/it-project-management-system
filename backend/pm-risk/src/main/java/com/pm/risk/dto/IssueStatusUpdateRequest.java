package com.pm.risk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "问题状态更新请求")
public class IssueStatusUpdateRequest {

    @NotBlank(message = "目标状态不能为空")
    @Schema(description = "目标状态", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;

    @Schema(description = "解决方案（状态为resolved时建议填写）")
    private String resolution;
}
