package com.pm.promotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "推广进度详情")
public class PromotionProgressVO {

    @Schema(description = "进度ID")
    private Long id;

    @Schema(description = "推广单元ID")
    private Long promotionUnitId;

    @Schema(description = "阶段模板ID")
    private Long stageTemplateId;

    @Schema(description = "阶段名称")
    private String stageName;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "开始时间")
    private LocalDateTime startedAt;

    @Schema(description = "完成时间")
    private LocalDateTime completedAt;

    @Schema(description = "预计结束日期")
    private LocalDate expectedEndDate;

    @Schema(description = "完成百分比")
    private BigDecimal completionRate;

    @Schema(description = "备注")
    private String remark;
}
