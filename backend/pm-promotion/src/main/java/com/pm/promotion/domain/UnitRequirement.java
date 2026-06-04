package com.pm.promotion.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pm.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("unit_requirement")
@Schema(description = "推广单元差异化需求")
public class UnitRequirement extends BaseEntity {

    @Schema(description = "推广单元ID")
    private Long promotionUnitId;

    @Schema(description = "关联需求ID（NULL表示单位独立需求）")
    private Long requirementId;

    @Schema(description = "需求标题")
    private String title;

    @Schema(description = "需求描述")
    private String description;

    @Schema(description = "类型（general通用/differential差异化）")
    private String type;

    @Schema(description = "优先级（critical/high/medium/low）")
    private String priority;

    @Schema(description = "状态（draft/reviewing/approved/scheduled/done）")
    private String status;
}
