package com.pm.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Schema(description = "自定义报表数据")
public class CustomReportVO {

    @Schema(description = "报表名称")
    private String reportName;

    @Schema(description = "维度")
    private String dimension;

    @Schema(description = "指标")
    private String metric;

    @Schema(description = "数据列表")
    private List<Map<String, Object>> data;

    @Schema(description = "汇总")
    private Map<String, Object> summary;
}
