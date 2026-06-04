package com.pm.promotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "推广看板汇总数据")
public class PromotionDashboardVO {

    @Schema(description = "项目ID")
    private Long projectId;

    @Schema(description = "推广单元总数")
    private Integer totalUnits;

    @Schema(description = "进行中单元数")
    private Integer inProgressUnits;

    @Schema(description = "已完成单元数")
    private Integer completedUnits;

    @Schema(description = "挂起单元数")
    private Integer suspendedUnits;

    @Schema(description = "整体完成率")
    private BigDecimal overallCompletionRate;

    @Schema(description = "延期单元数")
    private Integer delayedUnits;

    @Schema(description = "各单元进度对比列表")
    private List<UnitProgressSummary> unitSummaries;

    @Data
    @Schema(description = "单元进度摘要")
    public static class UnitProgressSummary {

        @Schema(description = "推广单元ID")
        private Long unitId;

        @Schema(description = "成员单位名称")
        private String orgName;

        @Schema(description = "状态")
        private String status;

        @Schema(description = "当前阶段名称")
        private String currentStageName;

        @Schema(description = "完成百分比")
        private BigDecimal completionRate;

        @Schema(description = "计划结束日期")
        private String expectedEndDate;

        @Schema(description = "是否延期")
        private Boolean delayed;
    }
}
