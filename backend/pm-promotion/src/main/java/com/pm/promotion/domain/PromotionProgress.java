package com.pm.promotion.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pm.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 推广单元阶段进度实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("promotion_progress")
@Schema(description = "推广单元阶段进度")
public class PromotionProgress extends BaseEntity {

    @Schema(description = "推广单元ID")
    private Long promotionUnitId;

    @Schema(description = "阶段模板ID")
    private Long stageTemplateId;

    @Schema(description = "状态：pending/in_progress/completed/skipped")
    private String status;

    @Schema(description = "开始时间")
    private LocalDateTime startedAt;

    @Schema(description = "完成时间")
    private LocalDateTime completedAt;

    @Schema(description = "预计结束日期")
    private LocalDate expectedEndDate;

    @Schema(description = "该阶段完成百分比")
    private BigDecimal completionRate;

    @Schema(description = "备注")
    private String remark;
}
