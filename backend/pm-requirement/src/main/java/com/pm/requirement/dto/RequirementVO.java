package com.pm.requirement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "需求详情")
public class RequirementVO {

    @Schema(description = "需求ID")
    private Long id;

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "需求标题")
    private String title;

    @Schema(description = "需求描述")
    private String description;

    @Schema(description = "验收标准")
    private String acceptanceCriteria;

    @Schema(description = "优先级")
    private String priority;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "来源")
    private String source;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "负责人ID")
    private Long assignedTo;

    @Schema(description = "关联里程碑ID")
    private Long milestoneId;

    @Schema(description = "预估工时（小时）")
    private BigDecimal estimatedHours;

    @Schema(description = "实际工时（小时）")
    private BigDecimal actualHours;

    @Schema(description = "创建人ID")
    private Long createdBy;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
