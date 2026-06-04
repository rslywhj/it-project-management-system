package com.pm.requirement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "创建需求请求")
public class RequirementCreateRequest {

    @NotBlank(message = "需求标题不能为空")
    @Schema(description = "需求标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "需求描述")
    private String description;

    @Schema(description = "优先级：critical/high/medium/low", defaultValue = "medium")
    private String priority;

    @Schema(description = "来源：business/tech/regulatory")
    private String source;

    @Schema(description = "分类：general/differential")
    private String category;

    @Schema(description = "负责人ID")
    private Long assignedTo;

    @Schema(description = "关联里程碑ID")
    private Long milestoneId;
}
