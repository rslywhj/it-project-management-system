package com.pm.milestone.controller;

import com.pm.common.result.PageResult;
import com.pm.common.result.Result;
import com.pm.milestone.dto.MilestoneCreateRequest;
import com.pm.milestone.dto.MilestoneUpdateRequest;
import com.pm.milestone.dto.MilestoneVO;
import com.pm.milestone.service.MilestoneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "里程碑管理", description = "里程碑 CRUD")
public class MilestoneController {

    private final MilestoneService milestoneService;

    @PostMapping("/projects/{projectId}/milestones")
    @Operation(summary = "创建里程碑")
    public Result<MilestoneVO> createMilestone(@PathVariable Long projectId,
                                               @Valid @RequestBody MilestoneCreateRequest request) {
        return Result.ok(milestoneService.createMilestone(projectId, request));
    }

    @GetMapping("/projects/{projectId}/milestones")
    @Operation(summary = "里程碑列表（分页）")
    public Result<PageResult<MilestoneVO>> listMilestones(
            @PathVariable Long projectId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "状态筛选") @RequestParam(required = false) String status) {
        return Result.ok(milestoneService.listMilestones(projectId, page, size, status));
    }

    @GetMapping("/milestones/{milestoneId}")
    @Operation(summary = "里程碑详情")
    public Result<MilestoneVO> getMilestone(@PathVariable Long milestoneId) {
        return Result.ok(milestoneService.getMilestone(milestoneId));
    }

    @PutMapping("/milestones/{milestoneId}")
    @Operation(summary = "更新里程碑")
    public Result<MilestoneVO> updateMilestone(@PathVariable Long milestoneId,
                                               @Valid @RequestBody MilestoneUpdateRequest request) {
        return Result.ok(milestoneService.updateMilestone(milestoneId, request));
    }

    @DeleteMapping("/milestones/{milestoneId}")
    @Operation(summary = "删除里程碑")
    public Result<Void> deleteMilestone(@PathVariable Long milestoneId) {
        milestoneService.deleteMilestone(milestoneId);
        return Result.ok();
    }
}
