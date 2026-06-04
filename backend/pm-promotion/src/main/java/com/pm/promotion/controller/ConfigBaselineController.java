package com.pm.promotion.controller;

import com.pm.common.result.PageResult;
import com.pm.common.result.Result;
import com.pm.promotion.dto.*;
import com.pm.promotion.service.ConfigBaselineService;
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
@Tag(name = "配置基线与差异管理", description = "配置基线 CRUD、成员单位配置差异管理")
public class ConfigBaselineController {

    private final ConfigBaselineService baselineService;

    @PostMapping("/projects/{projectId}/config-baselines")
    @Operation(summary = "创建配置基线")
    public Result<ConfigBaselineVO> createBaseline(@PathVariable Long projectId,
                                                     @Valid @RequestBody ConfigBaselineCreateRequest request) {
        return Result.ok(baselineService.createBaseline(projectId, request));
    }

    @GetMapping("/projects/{projectId}/config-baselines")
    @Operation(summary = "配置基线列表（分页）")
    public Result<PageResult<ConfigBaselineVO>> listBaselines(
            @PathVariable Long projectId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "关键词搜索") @RequestParam(required = false) String keyword) {
        return Result.ok(baselineService.listBaselines(projectId, page, size, keyword));
    }

    @GetMapping("/config-baselines/{baselineId}")
    @Operation(summary = "配置基线详情")
    public Result<ConfigBaselineVO> getBaseline(@PathVariable Long baselineId) {
        return Result.ok(baselineService.getBaseline(baselineId));
    }

    @PutMapping("/config-baselines/{baselineId}")
    @Operation(summary = "更新配置基线")
    public Result<ConfigBaselineVO> updateBaseline(@PathVariable Long baselineId,
                                                     @Valid @RequestBody ConfigBaselineUpdateRequest request) {
        return Result.ok(baselineService.updateBaseline(baselineId, request));
    }

    @DeleteMapping("/config-baselines/{baselineId}")
    @Operation(summary = "删除配置基线")
    public Result<Void> deleteBaseline(@PathVariable Long baselineId) {
        baselineService.deleteBaseline(baselineId);
        return Result.ok();
    }

    // ===== 配置差异 =====

    @PostMapping("/promotion-units/{unitId}/config-diffs")
    @Operation(summary = "创建配置差异")
    public Result<UnitConfigDiffVO> createDiff(@PathVariable Long unitId,
                                                @Valid @RequestBody UnitConfigDiffCreateRequest request) {
        return Result.ok(baselineService.createDiff(unitId, request));
    }

    @GetMapping("/promotion-units/{unitId}/config-diffs")
    @Operation(summary = "配置差异列表")
    public Result<List<UnitConfigDiffVO>> listDiffs(@PathVariable Long unitId) {
        return Result.ok(baselineService.listDiffs(unitId));
    }

    @PutMapping("/config-diffs/{diffId}/approve")
    @Operation(summary = "审批配置差异")
    public Result<UnitConfigDiffVO> approveDiff(@PathVariable Long diffId,
                                                 @Valid @RequestBody UnitConfigDiffApproveRequest request) {
        return Result.ok(baselineService.approveDiff(diffId, request));
    }
}
