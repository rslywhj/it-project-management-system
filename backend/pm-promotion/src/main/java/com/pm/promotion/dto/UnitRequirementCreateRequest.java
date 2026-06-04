package com.pm.promotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "创建推广单元需求请求")
public class UnitRequirementCreateRequest {

    @NotBlank(message = "需求标题不能为空")
    @Size(max = 500, message = "需求标题不能超过500个字符")
    @Schema(description = "需求标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "需求描述")
    private String description;

    @Schema(description = "关联需求ID")
    private Long requirementId;

    @Schema(description = "类型：general通用/differential差异化")
    private String type;

    @Schema(description = "优先级：critical/high/medium/low")
    private String priority;
}
