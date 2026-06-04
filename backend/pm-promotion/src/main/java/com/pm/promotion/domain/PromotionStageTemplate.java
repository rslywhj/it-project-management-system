package com.pm.promotion.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pm.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 推广阶段模板实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("promotion_stage_template")
@Schema(description = "推广阶段模板")
public class PromotionStageTemplate extends BaseEntity {

    @Schema(description = "所属项目ID（NULL=集团全局模板）")
    private Long projectId;

    @Schema(description = "阶段名称")
    private String name;

    @Schema(description = "阶段说明")
    private String description;

    @Schema(description = "排序序号")
    private Integer sortOrder;

    @Schema(description = "是否集团锁定（不可由下级跳过）")
    private Boolean isLocked;

    @Schema(description = "预计天数")
    private Integer estimatedDays;
}
