package com.pm.resource.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "资源请求")
public class ResourceRequest {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Schema(description = "技能标签（逗号分隔）")
    private String skillTags;

    @Schema(description = "可用状态（available/busy/unavailable/on_leave）")
    private String availability;

    @Schema(description = "每周可用工时")
    private BigDecimal capacityHoursPerWeek;

    @Schema(description = "备注")
    private String remark;
}
