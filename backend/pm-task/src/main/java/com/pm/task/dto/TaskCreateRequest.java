package com.pm.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "创建任务请求")
public class TaskCreateRequest {

    @NotBlank(message = "任务标题不能为空")
    @Schema(description = "任务标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "任务描述")
    private String description;

    @Schema(description = "父任务ID（WBS分解）")
    private Long parentTaskId;

    @Schema(description = "关联需求ID")
    private Long requirementId;

    @Schema(description = "任务类型：dev/test/deploy/training/design/review")
    private String type;

    @Schema(description = "优先级：critical/high/medium/low", defaultValue = "medium")
    private String priority;

    @Schema(description = "负责人ID")
    private Long assignedTo;

    @Schema(description = "计划开始日期")
    private LocalDate plannedStart;

    @Schema(description = "计划结束日期")
    private LocalDate plannedEnd;
}
