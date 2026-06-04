package com.pm.task.controller;

import com.pm.common.result.PageResult;
import com.pm.common.result.Result;
import com.pm.task.dto.*;
import com.pm.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "任务管理", description = "任务 CRUD、WBS 分解、依赖管理、进度跟踪")
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/projects/{projectId}/tasks")
    @Operation(summary = "创建任务")
    public Result<TaskVO> createTask(@PathVariable Long projectId,
                                     @Valid @RequestBody TaskCreateRequest request) {
        return Result.ok(taskService.createTask(projectId, request));
    }

    @GetMapping("/projects/{projectId}/tasks")
    @Operation(summary = "任务列表（分页）")
    public Result<PageResult<TaskVO>> listTasks(
            @PathVariable Long projectId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "关键词搜索") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态筛选") @RequestParam(required = false) String status,
            @Parameter(description = "负责人筛选") @RequestParam(required = false) Long assignedTo) {
        return Result.ok(taskService.listTasks(projectId, page, size, keyword, status, assignedTo));
    }

    @GetMapping("/tasks/{taskId}")
    @Operation(summary = "任务详情")
    public Result<TaskVO> getTask(@PathVariable Long taskId) {
        return Result.ok(taskService.getTask(taskId));
    }

    @PutMapping("/tasks/{taskId}")
    @Operation(summary = "更新任务")
    public Result<TaskVO> updateTask(@PathVariable Long taskId,
                                     @Valid @RequestBody TaskCreateRequest request) {
        return Result.ok(taskService.updateTask(taskId, request));
    }

    @PutMapping("/tasks/{taskId}/progress")
    @Operation(summary = "更新任务进度")
    public Result<TaskVO> updateProgress(@PathVariable Long taskId,
                                         @Valid @RequestBody ProgressUpdateRequest request) {
        return Result.ok(taskService.updateProgress(taskId, request));
    }

    @DeleteMapping("/tasks/{taskId}")
    @Operation(summary = "删除任务")
    public Result<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return Result.ok();
    }

    @PostMapping("/tasks/{parentTaskId}/subtasks")
    @Operation(summary = "创建子任务（WBS 分解）")
    public Result<TaskVO> createSubtask(@PathVariable Long parentTaskId,
                                        @Valid @RequestBody TaskCreateRequest request) {
        return Result.ok(taskService.createSubtask(parentTaskId, request));
    }

    @GetMapping("/tasks/{parentTaskId}/subtasks")
    @Operation(summary = "获取子任务列表")
    public Result<List<TaskVO>> getSubtasks(@PathVariable Long parentTaskId) {
        return Result.ok(taskService.getSubtasks(parentTaskId));
    }

    @GetMapping("/projects/{projectId}/tasks/wbs")
    @Operation(summary = "获取 WBS 完整树结构")
    public Result<List<TaskVO>> getWbsTree(@PathVariable Long projectId) {
        return Result.ok(taskService.getWbsTree(projectId));
    }

    @PostMapping("/tasks/{taskId}/dependencies")
    @Operation(summary = "添加任务依赖")
    public Result<Void> addDependency(@PathVariable Long taskId,
                                      @Valid @RequestBody DependencyRequest request) {
        taskService.addDependency(taskId, request);
        return Result.ok();
    }
}
