package com.pm.resource.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "资源负载报告")
public class WorkloadReportVO {

    @Schema(description = "项目ID")
    private Long projectId;

    @Schema(description = "资源总数")
    private Integer totalResources;

    @Schema(description = "可用资源数")
    private Integer availableResources;

    @Schema(description = "过载资源数（负载>80%）")
    private Integer overloadedResources;

    @Schema(description = "资源负载详情")
    private List<ResourceWorkload> resourceWorkloads;

    @Schema(description = "工时统计")
    private WorkHoursSummary workHoursSummary;

    @Data
    @Schema(description = "资源负载详情")
    public static class ResourceWorkload {
        private Long userId;
        private String username;
        private String realName;
        private Integer workloadPercent;
        private String availability;
        private BigDecimal totalHoursThisWeek;
        private BigDecimal totalHoursThisMonth;
    }

    @Data
    @Schema(description = "工时统计")
    public static class WorkHoursSummary {
        private BigDecimal totalHoursThisWeek;
        private BigDecimal totalHoursThisMonth;
        private BigDecimal averageHoursPerPerson;
    }
}
