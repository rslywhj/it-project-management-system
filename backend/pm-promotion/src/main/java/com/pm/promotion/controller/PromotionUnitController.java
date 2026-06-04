package com.pm.promotion.controller;

import com.pm.common.result.PageResult;
import com.pm.common.result.Result;
import com.pm.promotion.dto.PromotionUnitCreateRequest;
import com.pm.promotion.dto.PromotionUnitUpdateRequest;
import com.pm.promotion.dto.PromotionUnitVO;
import com.pm.promotion.service.PromotionUnitService;
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
@Tag(name = "推广单元管理", description = "推广单元 CRUD、批量创建")
public class PromotionUnitController {

    private final PromotionUnitService unitService;

    @PostMapping("/projects/{projectId}/promotion-units")
    @Operation(summary = "创建推广单元")
    public Result<PromotionUnitVO> createUnit(@PathVariable Long projectId,
                                               @Valid @RequestBody PromotionUnitCreateRequest request) {
        return Result.ok(unitService.createUnit(projectId, request));
    }

    @PostMapping("/projects/{projectId}/promotion-units/batch")
    @Operation(summary = "批量创建推广单元")
    public Result<List<PromotionUnitVO>> batchCreateUnits(@PathVariable Long projectId,
                                                           @Valid @RequestBody List<PromotionUnitCreateRequest> requests) {
        return Result.ok(unitService.batchCreateUnits(projectId, requests));
    }

    @GetMapping("/projects/{projectId}/promotion-units")
    @Operation(summary = "推广单元列表（分页）")
    public Result<PageResult<PromotionUnitVO>> listUnits(
            @PathVariable Long projectId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "关键词搜索") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态筛选") @RequestParam(required = false) String status) {
        return Result.ok(unitService.listUnits(projectId, page, size, keyword, status));
    }

    @GetMapping("/promotion-units/{unitId}")
    @Operation(summary = "推广单元详情")
    public Result<PromotionUnitVO> getUnit(@PathVariable Long unitId) {
        return Result.ok(unitService.getUnit(unitId));
    }

    @PutMapping("/promotion-units/{unitId}")
    @Operation(summary = "更新推广单元")
    public Result<PromotionUnitVO> updateUnit(@PathVariable Long unitId,
                                               @Valid @RequestBody PromotionUnitUpdateRequest request) {
        return Result.ok(unitService.updateUnit(unitId, request));
    }

    @DeleteMapping("/promotion-units/{unitId}")
    @Operation(summary = "删除推广单元")
    public Result<Void> deleteUnit(@PathVariable Long unitId) {
        unitService.deleteUnit(unitId);
        return Result.ok();
    }
}
