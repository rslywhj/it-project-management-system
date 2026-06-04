package com.pm.requirement.controller;

import com.pm.common.result.PageResult;
import com.pm.common.result.Result;
import com.pm.requirement.dto.RequirementCreateRequest;
import com.pm.requirement.dto.RequirementVO;
import com.pm.requirement.dto.StatusUpdateRequest;
import com.pm.requirement.service.RequirementService;
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
@Tag(name = "需求管理", description = "需求 CRUD、状态流转、需求池")
public class RequirementController {

    private final RequirementService requirementService;

    @PostMapping("/projects/{projectId}/requirements")
    @Operation(summary = "创建需求")
    public Result<RequirementVO> createRequirement(@PathVariable Long projectId,
                                                   @Valid @RequestBody RequirementCreateRequest request) {
        return Result.ok(requirementService.createRequirement(projectId, request));
    }

    @GetMapping("/projects/{projectId}/requirements")
    @Operation(summary = "需求列表（分页）")
    public Result<PageResult<RequirementVO>> listRequirements(
            @PathVariable Long projectId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "关键词搜索") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态筛选") @RequestParam(required = false) String status,
            @Parameter(description = "优先级筛选") @RequestParam(required = false) String priority) {
        return Result.ok(requirementService.listRequirements(projectId, page, size, keyword, status, priority));
    }

    @GetMapping("/requirements/{requirementId}")
    @Operation(summary = "需求详情")
    public Result<RequirementVO> getRequirement(@PathVariable Long requirementId) {
        return Result.ok(requirementService.getRequirement(requirementId));
    }

    @PutMapping("/requirements/{requirementId}")
    @Operation(summary = "更新需求")
    public Result<RequirementVO> updateRequirement(@PathVariable Long requirementId,
                                                   @Valid @RequestBody RequirementCreateRequest request) {
        return Result.ok(requirementService.updateRequirement(requirementId, request));
    }

    @PutMapping("/requirements/{requirementId}/status")
    @Operation(summary = "需求状态流转",
            description = "状态流转路径：draft→reviewing→approved/rejected→draft, approved→scheduled→done")
    public Result<RequirementVO> updateStatus(@PathVariable Long requirementId,
                                              @Valid @RequestBody StatusUpdateRequest request) {
        return Result.ok(requirementService.updateStatus(requirementId, request));
    }

    @DeleteMapping("/requirements/{requirementId}")
    @Operation(summary = "删除需求")
    public Result<Void> deleteRequirement(@PathVariable Long requirementId) {
        requirementService.deleteRequirement(requirementId);
        return Result.ok();
    }

    @GetMapping("/projects/{projectId}/requirements/pool")
    @Operation(summary = "需求池", description = "获取未排期的需求列表")
    public Result<List<RequirementVO>> getRequirementPool(@PathVariable Long projectId) {
        return Result.ok(requirementService.getRequirementPool(projectId));
    }

    @PostMapping("/requirements/{requirementId}/assign")
    @Operation(summary = "指派需求负责人")
    public Result<Void> assignRequirement(@PathVariable Long requirementId,
                                          @RequestParam Long userId) {
        requirementService.assignRequirement(requirementId, userId);
        return Result.ok();
    }
}
