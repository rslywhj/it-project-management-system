package com.pm.risk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "风险统计信息")
public class RiskStatisticsVO {

    @Schema(description = "风险总数")
    private long totalRisks;

    @Schema(description = "按状态统计")
    private Map<String, Long> byStatus;

    @Schema(description = "按等级统计")
    private Map<String, Long> byLevel;

    @Schema(description = "问题总数")
    private long totalIssues;

    @Schema(description = "问题按状态统计")
    private Map<String, Long> issuesByStatus;

    @Schema(description = "问题按严重程度统计")
    private Map<String, Long> issuesBySeverity;

    @Schema(description = "待处理预警数")
    private long pendingWarnings;
}
