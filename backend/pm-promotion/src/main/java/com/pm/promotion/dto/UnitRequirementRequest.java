package com.pm.promotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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

    @Schema(description = "类型（general/differential）")
    private String type;

    @Schema(description = "优先级")
    private String priority;
}
