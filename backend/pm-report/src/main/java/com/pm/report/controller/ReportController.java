package com.pm.report.controller;

import com.pm.common.result.Result;
import com.pm.report.dto.*;
import com.pm.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "看板与报表", description = "项目看板、甘特图、燃尽图、自定义报表、推广汇总")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/projects/{projectId}/dashboard")
    @Operation(summary = "项目看板数据")
    public Result<ProjectDashboardVO> getProjectDashboard(@PathVariable Long projectId) {
        return Result.ok(reportService.getProjectDashboard(projectId));
    }

    @GetMapping("/projects/{projectId}/gantt")
    @Operation(summary = "甘特图数据")
    public Result<GanttChartDataVO> getGanttChartData(@PathVariable Long projectId) {
        return Result.ok(reportService.getGanttChartData(projectId));
    }

    @GetMapping("/projects/{projectId}/burndown")
    @Operation(summary = "燃尽图数据")
    public Result<BurndownChartVO> getBurndownChart(
            @PathVariable Long projectId,
            @Parameter(description = "迭代/版本") @RequestParam(required = false) String iteration) {
        return Result.ok(reportService.getBurndownChart(projectId, iteration));
    }

    @PostMapping("/projects/{projectId}/reports/custom")
    @Operation(summary = "生成自定义报表")
    public Result<CustomReportVO> generateCustomReport(
            @PathVariable Long projectId,
            @Valid @RequestBody CustomReportRequest request) {
        return Result.ok(reportService.generateCustomReport(projectId, request));
    }

    @GetMapping("/projects/{projectId}/promotion/summary")
    @Operation(summary = "推广汇总报表")
    public Result<PromotionSummaryVO> getPromotionSummary(@PathVariable Long projectId) {
        return Result.ok(reportService.getPromotionSummary(projectId));
    }
}
