package com.pm.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "推广汇总报表")
public class PromotionSummaryVO {

    @Schema(description = "项目ID")
    private Long projectId;

    @Schema(description = "推广单元总数")
    private Integer totalUnits;

    @Schema(description = "整体完成率")
    private BigDecimal overallCompletionRate;

    @Schema(description = "各单位进度")
    private List<UnitProgress> unitProgressList;

    @Schema(description = "各阶段完成情况")
    private List<StageCompletion> stageCompletions;

    @Data
    @Schema(description = "单位进度")
    public static class UnitProgress {
        private Long unitId;
        private String orgName;
        private String currentStage;
        private BigDecimal completionRate;
        private String status;
        private Integer overdueDays;
    }

    @Data
    @Schema(description = "阶段完成情况")
    public static class StageCompletion {
        private String stageName;
        private Integer totalUnits;
        private Integer completedUnits;
        private Integer inProgressUnits;
        private Integer pendingUnits;
    }
}
