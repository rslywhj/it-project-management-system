package com.pm.risk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "问题详情")
public class IssueVO {

    @Schema(description = "问题ID")
    private Long id;

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "问题标题")
    private String title;

    @Schema(description = "问题描述")
    private String description;

    @Schema(description = "问题类型")
    private String issueType;

    @Schema(description = "严重程度")
    private String severity;

    @Schema(description = "优先级")
    private String priority;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "指派给")
    private Long assignedTo;

    @Schema(description = "报告人ID")
    private Long reportedBy;

    @Schema(description = "发现日期")
    private LocalDate identifiedDate;

    @Schema(description = "计划解决日期")
    private LocalDate targetResolutionDate;

    @Schema(description = "实际解决日期")
    private LocalDate actualResolutionDate;

    @Schema(description = "解决方案")
    private String resolution;

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

    @Schema(description = "附件路径")
    private String attachmentPath;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
