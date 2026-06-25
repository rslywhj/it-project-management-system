package com.pm.requirement.dto;

import com.pm.common.validation.ValidationPatterns;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "创建需求请求")
public class RequirementCreateRequest {

    @NotBlank(message = "需求标题不能为空")
    @Size(max = 200, message = "需求标题不能超过200个字符")
    @Schema(description = "需求标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "需求描述")
    private String description;

    @Schema(description = "验收标准")
    private String acceptanceCriteria;

    @Pattern(regexp = ValidationPatterns.PRIORITY, message = "优先级必须为 critical/high/medium/low")
    @Schema(description = "优先级：critical/high/medium/low", defaultValue = "medium")
    private String priority;

    @Pattern(regexp = ValidationPatterns.REQUIREMENT_SOURCE, message = "来源必须为 business/tech/regulatory")
    @Schema(description = "来源：business/tech/regulatory")
    private String source;

    @Pattern(regexp = ValidationPatterns.REQUIREMENT_CATEGORY, message = "分类必须为 general/differential")
    @Schema(description = "分类：general/differential")
    private String category;

    @Schema(description = "负责人ID")
    private Long assignedTo;

    @Schema(description = "关联里程碑ID")
    private Long milestoneId;

    @Schema(description = "预估工时（小时）")
    private BigDecimal estimatedHours;
}
