package com.pm.promotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "创建推广阶段模板请求")
public class PromotionStageTemplateCreateRequest {

    @NotBlank(message = "阶段名称不能为空")
    @Size(max = 100, message = "阶段名称不能超过100个字符")
    @Schema(description = "阶段名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "阶段说明")
    private String description;

    @Schema(description = "排序序号")
    private Integer sortOrder;

    @Schema(description = "是否集团锁定")
    private Boolean isLocked;

    @Schema(description = "预计天数")
    private Integer estimatedDays;
}
