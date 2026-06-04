package com.pm.promotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "差异化需求详情")
public class UnitRequirementVO {

    @Schema(description = "需求ID")
    private Long id;

    @Schema(description = "推广单元ID")
    private Long promotionUnitId;

    @Schema(description = "关联需求ID")
    private Long requirementId;

    @Schema(description = "需求标题")
    private String title;

    @Schema(description = "需求描述")
    private String description;

    @Schema(description = "类型")
    private String type;

    @Schema(description = "优先级")
    private String priority;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
