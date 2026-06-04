package com.pm.promotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "推广看板数据")
public class PromotionDashboardVO {

    @Schema(description = "项目ID")
    private Long projectId;

    @Schema(description = "推广单元总数")
    private Integer totalUnits;

    @Schema(description = "已完成单元数")
    private Integer completedUnits;

    @Schema(description = "进行中单元数")
    private Integer inProgressUnits;

    @Schema(description = "未开始单元数")
    private Integer pendingUnits;

    @Schema(description = "延期单元数")
    private Integer overdueUnits;

    @Schema(description = "整体完成率")
    private BigDecimal overallCompletionRate;

    @Schema(description = "各单位进度对比")
    private List<UnitComparisonVO> unitComparisons;

    @Schema(description = "延期预警列表")
    private List<OverdueAlertVO> overdueAlerts;
}
