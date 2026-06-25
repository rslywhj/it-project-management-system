package com.pm.resource.controller;

import com.pm.common.result.PageResult;
import com.pm.common.result.Result;
import com.pm.resource.dto.*;
import com.pm.resource.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "资源与工时管理", description = "资源池管理、工时填报、资源负载分析")
public class ResourceController {

    private final ResourceService resourceService;

    // ==================== 资源池管理 ====================

    @PostMapping("/projects/{projectId}/resources")
    @Operation(summary = "添加资源")
    public Result<ResourceVO> addResource(@PathVariable Long projectId,
                                          @Valid @RequestBody ResourceRequest request) {
        return Result.ok(resourceService.addResource(projectId, request));
    }

    @GetMapping("/projects/{projectId}/resources")
    @Operation(summary = "资源列表")
    public Result<PageResult<ResourceVO>> listResources(
            @PathVariable Long projectId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "可用状态筛选") @RequestParam(required = false) String availability) {
        return Result.ok(resourceService.listResources(projectId, page, size, availability));
    }

    @GetMapping("/resources/{resourceId}")
    @Operation(summary = "资源详情")
    public Result<ResourceVO> getResource(@PathVariable Long resourceId) {
        return Result.ok(resourceService.getResource(resourceId));
    }

    @PutMapping("/resources/{resourceId}")
    @Operation(summary = "更新资源")
    public Result<ResourceVO> updateResource(@PathVariable Long resourceId,
                                             @Valid @RequestBody ResourceRequest request) {
        return Result.ok(resourceService.updateResource(resourceId, request));
    }

    @DeleteMapping("/resources/{resourceId}")
    @Operation(summary = "移除资源")
    public Result<Void> removeResource(@PathVariable Long resourceId) {
        resourceService.removeResource(resourceId);
        return Result.ok();
    }

    // ==================== 工时填报 ====================

    @PostMapping("/projects/{projectId}/work-logs")
    @Operation(summary = "填报工时")
    public Result<WorkLogVO> logWork(@PathVariable Long projectId,
                                     @Valid @RequestBody WorkLogRequest request) {
        return Result.ok(resourceService.logWork(projectId, request));
    }

    @GetMapping("/projects/{projectId}/work-logs")
    @Operation(summary = "工时记录列表")
    public Result<PageResult<WorkLogVO>> listWorkLogs(
            @PathVariable Long projectId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "用户ID筛选") @RequestParam(required = false) Long userId,
            @Parameter(description = "任务ID筛选") @RequestParam(required = false) Long taskId,
            @Parameter(description = "开始日期") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) LocalDate endDate) {
        return Result.ok(resourceService.listWorkLogs(projectId, page, size, userId, taskId, startDate, endDate));
    }

    @PutMapping("/work-logs/{workLogId}")
    @Operation(summary = "更新工时记录")
    public Result<WorkLogVO> updateWorkLog(@PathVariable Long workLogId,
                                           @Valid @RequestBody WorkLogRequest request) {
        return Result.ok(resourceService.updateWorkLog(workLogId, request));
    }

    @DeleteMapping("/work-logs/{workLogId}")
    @Operation(summary = "删除工时记录")
    public Result<Void> deleteWorkLog(@PathVariable Long workLogId) {
        resourceService.deleteWorkLog(workLogId);
        return Result.ok();
    }

    // ==================== 资源分配 ====================

    @PostMapping("/projects/{projectId}/allocations")
    @Operation(summary = "创建资源分配")
    public Result<AllocationVO> createAllocation(@PathVariable Long projectId,
                                                  @Valid @RequestBody AllocationRequest request) {
        return Result.ok(resourceService.createAllocation(projectId, request));
    }

    @GetMapping("/projects/{projectId}/allocations")
    @Operation(summary = "资源分配列表")
    public Result<PageResult<AllocationVO>> listAllocations(
            @PathVariable Long projectId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {
        return Result.ok(resourceService.listAllocations(projectId, page, size, userId, status));
    }

    @PutMapping("/allocations/{allocationId}")
    @Operation(summary = "更新资源分配")
    public Result<AllocationVO> updateAllocation(@PathVariable Long allocationId,
                                                  @Valid @RequestBody AllocationRequest request) {
        return Result.ok(resourceService.updateAllocation(allocationId, request));
    }

    @DeleteMapping("/allocations/{allocationId}")
    @Operation(summary = "删除资源分配")
    public Result<Void> deleteAllocation(@PathVariable Long allocationId) {
        resourceService.deleteAllocation(allocationId);
        return Result.ok();
    }

    // ==================== 工时记录（Timesheet） ====================

    @PostMapping("/projects/{projectId}/timesheets")
    @Operation(summary = "创建工时记录")
    public Result<TimesheetVO> createTimesheet(@PathVariable Long projectId,
                                                @Valid @RequestBody TimesheetRequest request) {
        return Result.ok(resourceService.createTimesheet(projectId, request));
    }

    @GetMapping("/projects/{projectId}/timesheets")
    @Operation(summary = "工时记录列表")
    public Result<PageResult<TimesheetVO>> listTimesheets(
            @PathVariable Long projectId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "开始日期") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) LocalDate endDate,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {
        return Result.ok(resourceService.listTimesheets(projectId, page, size, userId, startDate, endDate, status));
    }

    @PutMapping("/timesheets/{timesheetId}")
    @Operation(summary = "更新工时记录")
    public Result<TimesheetVO> updateTimesheet(@PathVariable Long timesheetId,
                                                @Valid @RequestBody TimesheetRequest request) {
        return Result.ok(resourceService.updateTimesheet(timesheetId, request));
    }

    @DeleteMapping("/timesheets/{timesheetId}")
    @Operation(summary = "删除工时记录")
    public Result<Void> deleteTimesheet(@PathVariable Long timesheetId) {
        resourceService.deleteTimesheet(timesheetId);
        return Result.ok();
    }

    @PutMapping("/timesheets/{timesheetId}/submit")
    @Operation(summary = "提交工时记录")
    public Result<TimesheetVO> submitTimesheet(@PathVariable Long timesheetId) {
        return Result.ok(resourceService.submitTimesheet(timesheetId));
    }

    @PutMapping("/timesheets/{timesheetId}/approve")
    @Operation(summary = "审批工时记录")
    public Result<TimesheetVO> approveTimesheet(@PathVariable Long timesheetId) {
        return Result.ok(resourceService.approveTimesheet(timesheetId));
    }

    @PutMapping("/timesheets/{timesheetId}/reject")
    @Operation(summary = "驳回工时记录")
    public Result<TimesheetVO> rejectTimesheet(@PathVariable Long timesheetId,
                                                @RequestBody(required = false) java.util.Map<String, String> body) {
        String reason = body != null ? body.get("reason") : null;
        return Result.ok(resourceService.rejectTimesheet(timesheetId, reason));
    }

    // ==================== 资源利用率 ====================

    @GetMapping("/projects/{projectId}/resource-utilization")
    @Operation(summary = "资源利用率")
    public Result<List<ResourceUtilizationVO>> getResourceUtilization(@PathVariable Long projectId) {
        return Result.ok(resourceService.getResourceUtilization(projectId));
    }

    // ==================== 资源负载分析 ====================

    @GetMapping("/projects/{projectId}/resources/workload")
    @Operation(summary = "资源负载报告")
    public Result<WorkloadReportVO> getWorkloadReport(@PathVariable Long projectId) {
        return Result.ok(resourceService.getWorkloadReport(projectId));
    }
}
