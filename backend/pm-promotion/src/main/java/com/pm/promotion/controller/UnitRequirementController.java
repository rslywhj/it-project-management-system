package com.pm.promotion.controller;

import com.pm.common.result.PageResult;
import com.pm.common.result.Result;
import com.pm.promotion.dto.UnitRequirementCreateRequest;
import com.pm.promotion.dto.UnitRequirementUpdateRequest;
import com.pm.promotion.dto.UnitRequirementVO;
import com.pm.promotion.service.UnitRequirementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "推广单元需求管理", description = "推广单元差异化需求 CRUD")
public class UnitRequirementController {

    private final UnitRequirementService requirementService;

    @PostMapping("/promotion-units/{unitId}/requirements")
    @Operation(summary = "创建推广单元需求")
    public Result<UnitRequirementVO> createRequirement(@PathVariable Long unitId,
                                                        @Valid @RequestBody UnitRequirementCreateRequest request) {
        return Result.ok(requirementService.createRequirement(unitId, request));
    }

    @GetMapping("/promotion-units/{unitId}/requirements")
    @Operation(summary = "推广单元需求列表（分页）")
    public Result<PageResult<UnitRequirementVO>> listRequirements(
            @PathVariable Long unitId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "关键词搜索") @RequestParam(required = false) String keyword,
            @Parameter(description = "类型筛选：general/differential") @RequestParam(required = false) String type) {
        return Result.ok(requirementService.listRequirements(unitId, page, size, keyword, type));
    }

    @GetMapping("/unit-requirements/{requirementId}")
    @Operation(summary = "获取需求详情")
    public Result<UnitRequirementVO> getRequirement(@PathVariable Long requirementId) {
        return Result.ok(requirementService.getRequirement(requirementId));
    }

    @PutMapping("/unit-requirements/{requirementId}")
    @Operation(summary = "更新需求")
    public Result<UnitRequirementVO> updateRequirement(@PathVariable Long requirementId,
                                                        @Valid @RequestBody UnitRequirementUpdateRequest request) {
        return Result.ok(requirementService.updateRequirement(requirementId, request));
    }

    @DeleteMapping("/unit-requirements/{requirementId}")
    @Operation(summary = "删除需求")
    public Result<Void> deleteRequirement(@PathVariable Long requirementId) {
        requirementService.deleteRequirement(requirementId);
        return Result.ok();
    }
}
