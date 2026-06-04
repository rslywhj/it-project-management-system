package com.pm.promotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "单位进度对比")
public class UnitComparisonVO {

    @Schema(description = "推广单元ID")
    private Long unitId;

    @Schema(description = "成员单位名称")
    private String orgName;

    @Schema(description = "当前阶段")
    private String currentStage;

    @Schema(description = "完成率")
    private BigDecimal completionRate;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "是否延期")
    private Boolean isOverdue;
}
