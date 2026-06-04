package com.pm.promotion.controller;

import com.pm.common.result.Result;
import com.pm.promotion.dto.PromotionProgressVO;
import com.pm.promotion.dto.ProgressAdvanceRequest;
import com.pm.promotion.dto.ProgressUpdateRequest;
import com.pm.promotion.service.PromotionProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "推广进度管理", description = "推广阶段进度推进、完成率计算")
public class PromotionProgressController {

    private final PromotionProgressService progressService;

    @GetMapping("/promotion-units/{unitId}/progress")
    @Operation(summary = "获取推广单元阶段进度列表")
    public Result<List<PromotionProgressVO>> listProgress(@PathVariable Long unitId) {
        return Result.ok(progressService.listProgress(unitId));
    }

    @PutMapping("/progress/{progressId}/advance")
    @Operation(summary = "推进阶段进度")
    public Result<PromotionProgressVO> advanceStage(@PathVariable Long progressId,
                                                     @Valid @RequestBody ProgressAdvanceRequest request) {
        return Result.ok(progressService.advanceStage(progressId, request));
    }

    @PutMapping("/progress/{progressId}")
    @Operation(summary = "更新阶段进度")
    public Result<PromotionProgressVO> updateProgress(@PathVariable Long progressId,
                                                       @Valid @RequestBody ProgressUpdateRequest request) {
        return Result.ok(progressService.updateProgress(progressId, request));
    }
}
