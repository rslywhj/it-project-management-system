package com.pm.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "燃尽图数据")
public class BurndownChartVO {

    @Schema(description = "项目ID")
    private Long projectId;

    @Schema(description = "迭代/版本")
    private String iteration;

    @Schema(description = "计划开始日期")
    private LocalDate startDate;

    @Schema(description = "计划结束日期")
    private LocalDate endDate;

    @Schema(description = "总任务数")
    private Integer totalTasks;

    @Schema(description = "理想燃尽线")
    private List<BurndownPoint> idealLine;

    @Schema(description = "实际燃尽线")
    private List<BurndownPoint> actualLine;

    @Data
    @Schema(description = "燃尽点")
    public static class BurndownPoint {
        private LocalDate date;
        private BigDecimal remaining;
    }
}
