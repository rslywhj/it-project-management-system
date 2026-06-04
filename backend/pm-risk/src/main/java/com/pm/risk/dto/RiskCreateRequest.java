package com.pm.risk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "创建风险请求")
public class RiskCreateRequest {

    @NotBlank(message = "风险标题不能为空")
    @Schema(description = "风险标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "风险描述")
    private String description;

    @Schema(description = "风险类型：technical/schedule/resource/requirement/external", defaultValue = "technical")
    private String riskType;

    @Schema(description = "风险等级：critical/high/medium/low", defaultValue = "medium")
    private String level;

    @Schema(description = "发生概率：high/medium/low", defaultValue = "medium")
    private String probability;

    @Schema(description = "影响程度：high/medium/low", defaultValue = "medium")
    private String impact;

    @Schema(description = "风险负责人ID")
    private Long ownerId;

    @Schema(description = "识别日期")
    private LocalDate identifiedDate;

    @Schema(description = "计划解决日期")
    private LocalDate targetResolutionDate;

    @Schema(description = "应对措施")
    private String mitigationPlan;

    @Schema(description = "应急预案")
    private String contingencyPlan;

    @Schema(description = "触发条件")
    private String triggerConditions;

    @Schema(description = "关联需求ID")
    private Long relatedRequirementId;

    @Schema(description = "关联任务ID")
    private Long relatedTaskId;
}
