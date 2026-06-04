package com.pm.promotion.controller;

import com.pm.common.result.PageResult;
import com.pm.common.result.Result;
import com.pm.promotion.dto.*;
import com.pm.promotion.service.PromotionService;
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
@Tag(name = "推广管理", description = "集团推广单元、阶段模板、进度、差异化需求、配置基线、看板")
public class PromotionController {

    private final PromotionService promotionService;

    // ==================== 推广单元管理 ====================

    @PostMapping("/projects/{projectId}/promotion-units")
    @Operation(summary = "创建推广单元")
    public Result<PromotionUnitVO> createUnit(@PathVariable Long projectId,
                                              @Valid @RequestBody PromotionUnitCreateRequest request) {
        return Result.ok(promotionService.createUnit(projectId, request));
    }

    @PostMapping("/projects/{projectId}/promotion-units/batch")
    @Operation(summary = "批量创建推广单元")
    public Result<List<PromotionUnitVO>> batchCreateUnits(@PathVariable Long projectId,
                                                          @Valid @RequestBody BatchCreateUnitsRequest request) {
        return Result.ok(promotionService.batchCreateUnits(projectId, request));
    }

    @GetMapping("/projects/{projectId}/promotion-units")
    @Operation(summary = "推广单元列表（分页）")
    public Result<PageResult<PromotionUnitVO>> listUnits(
            @PathVariable Long projectId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "状态筛选") @RequestParam(required = false) String status) {
        return Result.ok(promotionService.listUnits(projectId, page, size, status));
    }

    @GetMapping("/promotion-units/{unitId}")
    @Operation(summary = "推广单元详情")
    public Result<PromotionUnitVO> getUnit(@PathVariable Long unitId) {
        return Result.ok(promotionService.getUnit(unitId));
    }

    @PutMapping("/promotion-units/{unitId}")
    @Operation(summary = "更新推广单元")
    public Result<PromotionUnitVO> updateUnit(@PathVariable Long unitId,
                                              @Valid @RequestBody PromotionUnitCreateRequest request) {
        return Result.ok(promotionService.updateUnit(unitId, request));
    }

    @DeleteMapping("/promotion-units/{unitId}")
    @Operation(summary = "删除推广单元")
    public Result<Void> deleteUnit(@PathVariable Long unitId) {
        promotionService.deleteUnit(unitId);
        return Result.ok();
    }

    // ==================== 推广阶段模板管理 ====================

    @PostMapping("/projects/{projectId}/promotion-stage-templates")
    @Operation(summary = "创建推广阶段模板")
    public Result<PromotionStageTemplateVO> createStageTemplate(
            @PathVariable Long projectId,
            @Valid @RequestBody PromotionStageTemplateRequest request) {
        return Result.ok(promotionService.createStageTemplate(projectId, request));
    }

    @GetMapping("/projects/{projectId}/promotion-stage-templates")
    @Operation(summary = "推广阶段模板列表")
    public Result<List<PromotionStageTemplateVO>> listStageTemplates(@PathVariable Long projectId) {
        return Result.ok(promotionService.listStageTemplates(projectId));
    }

    @PutMapping("/promotion-stage-templates/{templateId}")
    @Operation(summary = "更新推广阶段模板")
    public Result<PromotionStageTemplateVO> updateStageTemplate(
            @PathVariable Long templateId,
            @Valid @RequestBody PromotionStageTemplateRequest request) {
        return Result.ok(promotionService.updateStageTemplate(templateId, request));
    }

    @DeleteMapping("/promotion-stage-templates/{templateId}")
    @Operation(summary = "删除推广阶段模板")
    public Result<Void> deleteStageTemplate(@PathVariable Long templateId) {
        promotionService.deleteStageTemplate(templateId);
        return Result.ok();
    }

    @PostMapping("/projects/{projectId}/promotion-stage-templates/init-default")
    @Operation(summary = "初始化默认推广阶段模板")
    public Result<Void> initDefaultStageTemplates(@PathVariable Long projectId) {
        promotionService.initDefaultStageTemplates(projectId);
        return Result.ok();
    }

    // ==================== 推广进度管理 ====================

    @GetMapping("/promotion-units/{unitId}/progress")
    @Operation(summary = "获取推广单元阶段进度")
    public Result<List<PromotionProgressVO>> getUnitProgress(@PathVariable Long unitId) {
        return Result.ok(promotionService.getUnitProgress(unitId));
    }

    @PutMapping("/promotion-units/{unitId}/progress/{stageId}")
    @Operation(summary = "更新推广阶段进度")
    public Result<PromotionProgressVO> updateProgress(
            @PathVariable Long unitId,
            @PathVariable Long stageId,
            @Valid @RequestBody ProgressUpdateRequest request) {
        return Result.ok(promotionService.updateProgress(unitId, stageId, request));
    }

    // ==================== 差异化需求管理 ====================

    @PostMapping("/promotion-units/{unitId}/requirements")
    @Operation(summary = "创建差异化需求")
    public Result<UnitRequirementVO> createUnitRequirement(
            @PathVariable Long unitId,
            @Valid @RequestBody UnitRequirementRequest request) {
        return Result.ok(promotionService.createUnitRequirement(unitId, request));
    }

    @GetMapping("/promotion-units/{unitId}/requirements")
    @Operation(summary = "差异化需求列表")
    public Result<PageResult<UnitRequirementVO>> listUnitRequirements(
            @PathVariable Long unitId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "类型筛选") @RequestParam(required = false) String type) {
        return Result.ok(promotionService.listUnitRequirements(unitId, page, size, type));
    }

    @PutMapping("/promotion-requirements/{requirementId}")
    @Operation(summary = "更新差异化需求")
    public Result<UnitRequirementVO> updateUnitRequirement(
            @PathVariable Long requirementId,
            @Valid @RequestBody UnitRequirementRequest request) {
        return Result.ok(promotionService.updateUnitRequirement(requirementId, request));
    }

    @DeleteMapping("/promotion-requirements/{requirementId}")
    @Operation(summary = "删除差异化需求")
    public Result<Void> deleteUnitRequirement(@PathVariable Long requirementId) {
        promotionService.deleteUnitRequirement(requirementId);
        return Result.ok();
    }

    // ==================== 配置基线管理 ====================

    @PostMapping("/projects/{projectId}/config-baselines")
    @Operation(summary = "创建配置基线")
    public Result<ConfigBaselineVO> createConfigBaseline(
            @PathVariable Long projectId,
            @Valid @RequestBody ConfigBaselineRequest request) {
        return Result.ok(promotionService.createConfigBaseline(projectId, request));
    }

    @GetMapping("/projects/{projectId}/config-baselines")
    @Operation(summary = "配置基线列表")
    public Result<List<ConfigBaselineVO>> listConfigBaselines(@PathVariable Long projectId) {
        return Result.ok(promotionService.listConfigBaselines(projectId));
    }

    @PutMapping("/config-baselines/{baselineId}")
    @Operation(summary = "更新配置基线")
    public Result<ConfigBaselineVO> updateConfigBaseline(
            @PathVariable Long baselineId,
            @Valid @RequestBody ConfigBaselineRequest request) {
        return Result.ok(promotionService.updateConfigBaseline(baselineId, request));
    }

    @DeleteMapping("/config-baselines/{baselineId}")
    @Operation(summary = "删除配置基线")
    public Result<Void> deleteConfigBaseline(@PathVariable Long baselineId) {
        promotionService.deleteConfigBaseline(baselineId);
        return Result.ok();
    }

    // ==================== 配置差异管理 ====================

    @PostMapping("/promotion-units/{unitId}/config-diffs")
    @Operation(summary = "创建配置差异")
    public Result<UnitConfigDiffVO> createConfigDiff(
            @PathVariable Long unitId,
            @Valid @RequestBody UnitConfigDiffRequest request) {
        return Result.ok(promotionService.createConfigDiff(unitId, request));
    }

    @GetMapping("/promotion-units/{unitId}/config-diffs")
    @Operation(summary = "配置差异列表")
    public Result<List<UnitConfigDiffVO>> listConfigDiffs(@PathVariable Long unitId) {
        return Result.ok(promotionService.listConfigDiffs(unitId));
    }

    @PutMapping("/config-diffs/{diffId}/approve")
    @Operation(summary = "审批配置差异")
    public Result<UnitConfigDiffVO> approveConfigDiff(
            @PathVariable Long diffId,
            @Parameter(description = "审批动作（approve/reject）") @RequestParam String action) {
        return Result.ok(promotionService.approveConfigDiff(diffId, action));
    }

    // ==================== 推广看板 ====================

    @GetMapping("/projects/{projectId}/promotion/dashboard")
    @Operation(summary = "推广看板数据")
    public Result<PromotionDashboardVO> getDashboard(@PathVariable Long projectId) {
        return Result.ok(promotionService.getDashboard(projectId));
    }

    // ==================== 批量操作 ====================

    @PostMapping("/projects/{projectId}/promotion-units/batch-create-tasks")
    @Operation(summary = "批量创建推广任务")
    public Result<Void> batchCreateTasks(@PathVariable Long projectId,
                                         @Valid @RequestBody BatchCreateTasksRequest request) {
        promotionService.batchCreateTasks(projectId, request);
        return Result.ok();
    }
}
