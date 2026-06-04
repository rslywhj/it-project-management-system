package com.pm.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "甘特图数据")
public class GanttChartDataVO {

    @Schema(description = "项目ID")
    private Long projectId;

    @Schema(description = "任务列表")
    private List<GanttTask> tasks;

    @Schema(description = "依赖关系")
    private List<GanttDependency> dependencies;

    @Data
    @Schema(description = "甘特图任务")
    public static class GanttTask {
        private Long id;
        private String text;
        private LocalDate start;
        private LocalDate end;
        private Long parent;
        private Double progress;
        private String type;
        private Boolean open;
        private String assignee;
    }

    @Data
    @Schema(description = "甘特图依赖")
    public static class GanttDependency {
        private Long id;
        private Long source;
        private Long target;
        private String type;
    }
}
