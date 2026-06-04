package com.pm.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "项目看板数据")
public class ProjectDashboardVO {

    @Schema(description = "项目ID")
    private Long projectId;

    @Schema(description = "项目名称")
    private String projectName;

    @Schema(description = "项目状态")
    private String projectStatus;

    @Schema(description = "需求统计")
    private RequirementStats requirementStats;

    @Schema(description = "任务统计")
    private TaskStats taskStats;

    @Schema(description = "缺陷统计")
    private BugStats bugStats;

    @Schema(description = "里程碑统计")
    private MilestoneStats milestoneStats;

    @Schema(description = "项目健康度（0-100）")
    private Integer healthScore;

    @Schema(description = "近期活动")
    private List<RecentActivity> recentActivities;

    @Data
    @Schema(description = "需求统计")
    public static class RequirementStats {
        private Integer total;
        private Integer draft;
        private Integer reviewing;
        private Integer approved;
        private Integer scheduled;
        private Integer done;
        private BigDecimal completionRate;
    }

    @Data
    @Schema(description = "任务统计")
    public static class TaskStats {
        private Integer total;
        private Integer todo;
        private Integer inProgress;
        private Integer done;
        private BigDecimal completionRate;
        private BigDecimal averageProgress;
    }

    @Data
    @Schema(description = "缺陷统计")
    public static class BugStats {
        private Integer total;
        private Integer open;
        private Integer inProgress;
        private Integer resolved;
        private Integer closed;
        private Integer criticalCount;
        private Integer majorCount;
    }

    @Data
    @Schema(description = "里程碑统计")
    public static class MilestoneStats {
        private Integer total;
        private Integer pending;
        private Integer completed;
        private Integer delayed;
        private Integer atRisk;
    }

    @Data
    @Schema(description = "近期活动")
    public static class RecentActivity {
        private String type;
        private String title;
        private String operator;
        private String time;
    }
}
