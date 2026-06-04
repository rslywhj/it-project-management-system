package com.pm.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "自定义报表请求")
public class CustomReportRequest {

    @NotBlank(message = "报表名称不能为空")
    @Schema(description = "报表名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String reportName;

    @Schema(description = "维度（org/project/team/member）")
    private String dimension;

    @Schema(description = "指标（requirement/task/bug/progress）")
    private String metric;

    @Schema(description = "筛选条件")
    private List<FilterItem> filters;

    @Schema(description = "时间范围-开始")
    private LocalDate startDate;

    @Schema(description = "时间范围-结束")
    private LocalDate endDate;

    @Data
    @Schema(description = "筛选项")
    public static class FilterItem {
        private String field;
        private String operator;
        private String value;
    }
}
