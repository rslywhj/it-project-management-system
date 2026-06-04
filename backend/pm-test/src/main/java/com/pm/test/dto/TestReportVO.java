package com.pm.test.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "测试报告")
public class TestReportVO {

    @Schema(description = "项目ID")
    private Long projectId;

    @Schema(description = "测试计划ID")
    private Long testPlanId;

    @Schema(description = "计划名称")
    private String planName;

    @Schema(description = "用例总数")
    private Integer totalCases;

    @Schema(description = "已执行数")
    private Integer executedCases;

    @Schema(description = "通过数")
    private Integer passedCases;

    @Schema(description = "失败数")
    private Integer failedCases;

    @Schema(description = "阻塞数")
    private Integer blockedCases;

    @Schema(description = "跳过数")
    private Integer skippedCases;

    @Schema(description = "通过率")
    private BigDecimal passRate;

    @Schema(description = "缺陷总数")
    private Integer totalBugs;

    @Schema(description = "未解决缺陷数")
    private Integer openBugs;

    @Schema(description = "已解决缺陷数")
    private Integer resolvedBugs;

    @Schema(description = "缺陷按严重程度分布")
    private List<BugSeverityStat> severityDistribution;

    @Schema(description = "缺陷按模块分布")
    private List<BugModuleStat> moduleDistribution;

    @Data
    @Schema(description = "缺陷严重程度统计")
    public static class BugSeverityStat {
        private String severity;
        private Integer count;
    }

    @Data
    @Schema(description = "缺陷模块统计")
    public static class BugModuleStat {
        private String module;
        private Integer count;
    }
}
