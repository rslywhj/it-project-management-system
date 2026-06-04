package com.pm.requirement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "需求状态流转请求")
public class StatusUpdateRequest {

    @NotBlank(message = "目标状态不能为空")
    @Schema(description = "目标状态", requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = {"reviewing", "approved", "scheduled", "done", "rejected", "draft"})
    private String targetStatus;

    @Schema(description = "流转备注")
    private String remark;
}
