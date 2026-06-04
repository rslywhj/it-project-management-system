package com.pm.lowcode.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "完成任务请求")
public class TaskCompleteRequest {

    @NotBlank(message = "审批动作不能为空")
    @Schema(description = "审批动作（approve/reject）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String action;

    @Schema(description = "表单数据（JSON格式）")
    private String formDataJson;

    @Schema(description = "审批意见")
    private String comment;
}
