package com.pm.promotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "推广阶段模板请求")
public class PromotionStageTemplateRequest {

    @NotBlank(message = "阶段名称不能为空")
    @Schema(description = "阶段名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "阶段说明")
    private String description;

    @Schema(description = "排序序号")
    private Integer sortOrder;

    @Schema(description = "是否集团锁定")
    private Integer isLocked;

    @Schema(description = "预计天数")
    private Integer estimatedDays;
}
