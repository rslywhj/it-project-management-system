package com.pm.promotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "批量创建任务请求")
public class BatchTaskCreateRequest {

    @NotEmpty(message = "推广单元ID列表不能为空")
    @Schema(description = "推广单元ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> promotionUnitIds;

    @NotNull(message = "阶段模板ID不能为空")
    @Schema(description = "阶段模板ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long stageTemplateId;

    @Schema(description = "任务标题")
    private String taskTitle;

    @Schema(description = "任务描述")
    private String taskDescription;

    @Schema(description = "任务类型：dev/test/deploy/training/design/review/other")
    private String taskType;

    @Schema(description = "负责人ID")
    private Long assignedTo;
}
