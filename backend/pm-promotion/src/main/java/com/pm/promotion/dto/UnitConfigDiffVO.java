package com.pm.promotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "配置差异详情")
public class UnitConfigDiffVO {

    @Schema(description = "差异ID")
    private Long id;

    @Schema(description = "推广单元ID")
    private Long promotionUnitId;

    @Schema(description = "关联配置基线ID")
    private Long configBaselineId;

    @Schema(description = "配置项键")
    private String configKey;

    @Schema(description = "标准配置值")
    private String baselineValue;

    @Schema(description = "差异值")
    private String diffValue;

    @Schema(description = "差异原因")
    private String diffReason;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "审批人ID")
    private Long approvedBy;

    @Schema(description = "审批时间")
    private LocalDateTime approvedAt;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
