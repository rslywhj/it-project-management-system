package com.pm.risk.controller;

import com.pm.common.result.PageResult;
import com.pm.common.result.Result;
import com.pm.risk.dto.*;
import com.pm.risk.service.IssueService;
import com.pm.risk.service.RiskService;
import com.pm.risk.service.WarningService;
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
@Tag(name = "风险管理", description = "风险登记、评估、应对措施管理")
public class RiskController {

    private final RiskService riskService;
    private final WarningService warningService;

    // ==================== 风险管理 ====================

    @PostMapping("/projects/{projectId}/risks")
    @Operation(summary = "创建风险")
    public Result<RiskVO> createRisk(@PathVariable Long projectId,
                                      @Valid @RequestBody RiskCreateRequest request) {
        return Result.ok(riskService.createRisk(projectId, request));
    }

    @GetMapping("/projects/{projectId}/risks")
    @Operation(summary = "风险列表（分页）")
    public Result<PageResult<RiskVO>> listRisks(
            @PathVariable Long projectId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "关键词搜索") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态筛选") @RequestParam(required = false) String status,
            @Parameter(description = "等级筛选") @RequestParam(required = false) String level,
            @Parameter(description = "负责人筛选") @RequestParam(required = false) Long ownerId) {
        return Result.ok(riskService.listRisks(projectId, page, size, keyword, status, level, ownerId));
    }

    @GetMapping("/risks/{riskId}")
    @Operation(summary = "风险详情")
    public Result<RiskVO> getRisk(@PathVariable Long riskId) {
        return Result.ok(riskService.getRisk(riskId));
    }

    @PutMapping("/risks/{riskId}")
    @Operation(summary = "更新风险")
    public Result<RiskVO> updateRisk(@PathVariable Long riskId,
                                      @Valid @RequestBody RiskUpdateRequest request) {
        return Result.ok(riskService.updateRisk(riskId, request));
    }

    @PutMapping("/risks/{riskId}/status")
    @Operation(summary = "更新风险状态")
    public Result<RiskVO> updateRiskStatus(@PathVariable Long riskId,
                                            @Valid @RequestBody RiskStatusUpdateRequest request) {
        return Result.ok(riskService.updateRiskStatus(riskId, request));
    }

    @DeleteMapping("/risks/{riskId}")
    @Operation(summary = "删除风险")
    public Result<Void> deleteRisk(@PathVariable Long riskId) {
        riskService.deleteRisk(riskId);
        return Result.ok();
    }

    @GetMapping("/projects/{projectId}/risks/expiring")
    @Operation(summary = "获取即将到期的风险")
    public Result<List<RiskVO>> getExpiringRisks(
            @PathVariable Long projectId,
            @Parameter(description = "天数阈值") @RequestParam(defaultValue = "7") int days) {
        return Result.ok(riskService.getExpiringRisks(projectId, days));
    }

    @GetMapping("/projects/{projectId}/risks/overdue")
    @Operation(summary = "获取已逾期的风险")
    public Result<List<RiskVO>> getOverdueRisks(@PathVariable Long projectId) {
        return Result.ok(riskService.getOverdueRisks(projectId));
    }

    // ==================== 预警管理 ====================

    @PostMapping("/projects/{projectId}/warnings/check")
    @Operation(summary = "检查并生成预警")
    public Result<List<WarningVO>> checkWarnings(
            @PathVariable Long projectId,
            @Parameter(description = "天数阈值") @RequestParam(defaultValue = "7") int days) {
        return Result.ok(warningService.checkAndGenerateWarnings(projectId, days));
    }

    @GetMapping("/projects/{projectId}/warnings")
    @Operation(summary = "预警列表（分页）")
    public Result<PageResult<WarningVO>> listWarnings(
            @PathVariable Long projectId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "预警类型") @RequestParam(required = false) String warningType,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {
        return Result.ok(warningService.listWarnings(projectId, page, size, warningType, status));
    }

    @GetMapping("/projects/{projectId}/warnings/pending")
    @Operation(summary = "获取待处理预警")
    public Result<List<WarningVO>> getPendingWarnings(@PathVariable Long projectId) {
        return Result.ok(warningService.getPendingWarnings(projectId));
    }

    @PutMapping("/warnings/{warningId}/acknowledge")
    @Operation(summary = "确认预警")
    public Result<WarningVO> acknowledgeWarning(@PathVariable Long warningId) {
        return Result.ok(warningService.acknowledgeWarning(warningId));
    }

    @PutMapping("/warnings/{warningId}/dismiss")
    @Operation(summary = "忽略预警")
    public Result<WarningVO> dismissWarning(@PathVariable Long warningId) {
        return Result.ok(warningService.dismissWarning(warningId));
    }
}
