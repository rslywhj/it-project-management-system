package com.pm.requirement.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pm.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 需求实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("requirement")
@Schema(description = "需求")
public class Requirement extends BaseEntity {

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "需求标题")
    private String title;

    @Schema(description = "需求描述")
    private String description;

    @Schema(description = "验收标准")
    private String acceptanceCriteria;

    @Schema(description = "优先级：critical/high/medium/low")
    private String priority;

    @Schema(description = "状态：draft/reviewing/approved/scheduled/done/rejected")
    private String status;

    @Schema(description = "来源：business/tech/regulatory")
    private String source;

    @Schema(description = "分类：general/differential（通用/差异化）")
    private String category;

    @Schema(description = "负责人ID")
    private Long assignedTo;

    @Schema(description = "关联里程碑ID")
    private Long milestoneId;

    @Schema(description = "预估工时（小时）")
    private BigDecimal estimatedHours;

    @Schema(description = "实际工时（小时）")
    private BigDecimal actualHours;

    @Schema(description = "备注")
    private String remark;
}
