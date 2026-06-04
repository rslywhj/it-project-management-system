package com.pm.promotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "更新推广阶段模板请求")
public class PromotionStageTemplateUpdateRequest {

    @Size(max = 100, message = "阶段名称不能超过100个字符")
    @Schema(description = "阶段名称")
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
