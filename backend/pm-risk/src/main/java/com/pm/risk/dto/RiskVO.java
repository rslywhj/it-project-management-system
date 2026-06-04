package com.pm.risk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "风险详情")
public class RiskVO {

    @Schema(description = "风险ID")
    private Long id;

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "风险标题")
    private String title;

    @Schema(description = "风险描述")
    private String description;

    @Schema(description = "风险类型")
    private String riskType;

    @Schema(description = "风险等级")
    private String level;

    @Schema(description = "发生概率")
    private String probability;

    @Schema(description = "影响程度")
    private String impact;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "风险负责人ID")
    private Long ownerId;

    @Schema(description = "识别日期")
    private LocalDate identifiedDate;

    @Schema(description = "计划解决日期")
    private LocalDate targetResolutionDate;

    @Schema(description = "实际解决日期")
    private LocalDate actualResolutionDate;

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

    @Schema(description = "附件路径")
    private String attachmentPath;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
