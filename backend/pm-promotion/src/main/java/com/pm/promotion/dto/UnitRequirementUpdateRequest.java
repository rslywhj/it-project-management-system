package com.pm.promotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "更新推广单元需求请求")
public class UnitRequirementUpdateRequest {

    @Size(max = 500, message = "需求标题不能超过500个字符")
    @Schema(description = "需求标题")
    private String title;

    @Schema(description = "需求描述")
    private String description;

    @Schema(description = "类型：general通用/differential差异化")
    private String type;

    @Schema(description = "优先级：critical/high/medium/low")
    private String priority;

    @Schema(description = "状态：draft/reviewing/approved/scheduled/done")
    private String status;
}
