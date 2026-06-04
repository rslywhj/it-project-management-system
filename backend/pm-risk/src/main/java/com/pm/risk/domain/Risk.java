package com.pm.risk.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pm.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 风险实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("risk")
@Schema(description = "风险")
public class Risk extends BaseEntity {

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "风险标题")
    private String title;

    @Schema(description = "风险描述")
    private String description;

    @Schema(description = "风险类型：technical/schedule/resource/requirement/external")
    private String riskType;

    @Schema(description = "风险等级：critical/high/medium/low")
    private String level;

    @Schema(description = "发生概率：high/medium/low")
    private String probability;

    @Schema(description = "影响程度：high/medium/low")
    private String impact;

    @Schema(description = "状态：identified/analyzing/mitigating/accepted/closed")
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
}
