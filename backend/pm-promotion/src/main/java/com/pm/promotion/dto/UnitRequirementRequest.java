package com.pm.promotion.dto;

import com.pm.common.validation.ValidationPatterns;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "差异化需求请求")
public class UnitRequirementRequest {

    @NotBlank(message = "需求标题不能为空")
    @Schema(description = "需求标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "需求描述")
    private String description;

    @Schema(description = "关联需求ID")
    private Long requirementId;

    @Pattern(regexp = ValidationPatterns.REQUIREMENT_CATEGORY, message = "类型必须为 general/differential")
    @Schema(description = "类型（general/differential）")
    private String type;

    @Pattern(regexp = ValidationPatterns.PRIORITY, message = "优先级必须为 critical/high/medium/low")
    @Schema(description = "优先级")
    private String priority;
}
