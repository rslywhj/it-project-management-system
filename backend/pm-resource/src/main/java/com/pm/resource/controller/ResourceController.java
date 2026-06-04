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

    // ==================== 资源负载分析 ====================

    @GetMapping("/projects/{projectId}/resources/workload")
    @Operation(summary = "资源负载报告")
    public Result<WorkloadReportVO> getWorkloadReport(@PathVariable Long projectId) {
        return Result.ok(resourceService.getWorkloadReport(projectId));
    }
}
