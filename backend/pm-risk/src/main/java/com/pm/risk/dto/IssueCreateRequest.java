package com.pm.risk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "创建问题请求")
public class IssueCreateRequest {

    @NotBlank(message = "问题标题不能为空")
    @Schema(description = "问题标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "问题描述")
    private String description;

    @Schema(description = "问题类型：technical/process/resource/external/other", defaultValue = "technical")
    private String issueType;

    @Schema(description = "严重程度：critical/major/minor/trivial", defaultValue = "medium")
    private String severity;

    @Schema(description = "优先级：critical/high/medium/low", defaultValue = "medium")
    private String priority;

    @Schema(description = "指派给")
    private Long assignedTo;

    @Schema(description = "发现日期")
    private LocalDate identifiedDate;

    @Schema(description = "计划解决日期")
    private LocalDate targetResolutionDate;

    @Schema(description = "根本原因")
    private String rootCause;

    @Schema(description = "影响分析")
    private String impactAnalysis;

    @Schema(description = "关联风险ID")
    private Long relatedRiskId;

    @Schema(description = "关联需求ID")
    private Long relatedRequirementId;

    @Schema(description = "关联任务ID")
    private Long relatedTaskId;
}
