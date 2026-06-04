package com.pm.promotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "推广阶段模板详情")
public class PromotionStageTemplateVO {

    @Schema(description = "模板ID")
    private Long id;

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "阶段名称")
    private String name;

    @Schema(description = "阶段说明")
    private String description;

    @Schema(description = "排序序号")
    private Integer sortOrder;

    @Schema(description = "是否集团锁定")
    private Integer isLocked;

    @Schema(description = "预计天数")
    private Integer estimatedDays;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
