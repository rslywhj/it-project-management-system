package com.pm.risk.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pm.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 问题实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("issue")
@Schema(description = "问题")
public class Issue extends BaseEntity {

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "问题标题")
    private String title;

    @Schema(description = "问题描述")
    private String description;

    @Schema(description = "问题类型：technical/process/resource/external/other")
    private String issueType;

    @Schema(description = "严重程度：critical/major/minor/trivial")
    private String severity;

    @Schema(description = "优先级：critical/high/medium/low")
    private String priority;

    @Schema(description = "状态：open/in_progress/resolved/closed/reopen")
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
}
