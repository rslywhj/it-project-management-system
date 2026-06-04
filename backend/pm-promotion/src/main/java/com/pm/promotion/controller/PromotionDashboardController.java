package com.pm.promotion.controller;

import com.pm.common.result.Result;
import com.pm.promotion.dto.BatchTaskCreateRequest;
import com.pm.promotion.dto.PromotionDashboardVO;
import com.pm.promotion.service.PromotionDashboardService;
import com.pm.task.domain.Task;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "推广看板与批量操作", description = "推广看板汇总数据、基于模板批量创建任务")
public class PromotionDashboardController {

    private final PromotionDashboardService dashboardService;

    @GetMapping("/projects/{projectId}/promotion/dashboard")
    @Operation(summary = "获取推广看板汇总数据")
    public Result<PromotionDashboardVO> getDashboard(@PathVariable Long projectId) {
        return Result.ok(dashboardService.getDashboard(projectId));
    }

    @PostMapping("/projects/{projectId}/promotion-units/batch-tasks")
    @Operation(summary = "基于模板为多个推广单元批量创建任务")
    public Result<List<Task>> batchCreateTasks(@PathVariable Long projectId,
                                                @Valid @RequestBody BatchTaskCreateRequest request) {
        return Result.ok(dashboardService.batchCreateTasks(projectId, request));
    }
}
