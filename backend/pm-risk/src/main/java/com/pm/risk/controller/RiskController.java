package com.pm.risk.controller;

import com.pm.common.result.PageResult;
import com.pm.common.result.Result;
import com.pm.risk.dto.*;
import com.pm.risk.service.RiskService;
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
@Tag(name = "风险与问题管理", description = "风险登记、问题跟踪、预警机制")
public class RiskController {

    private final RiskService riskService;

    // ==================== 风险管理 ====================

    @PostMapping("/projects/{projectId}/risks")
    @Operation(summary = "登记风险")
    public Result<RiskVO> createRisk(@PathVariable Long projectId,
                                     @Valid @RequestBody RiskRequest request) {
        return Result.ok(riskService.createRisk(projectId, request));
    }

    @GetMapping("/projects/{projectId}/risks")
    @Operation(summary = "风险列表（分页）")
    public Result<PageResult<RiskVO>> listRisks(
            @PathVariable Long projectId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "状态筛选") @RequestParam(required = false) String status,
            @Parameter(description = "风险等级筛选") @RequestParam(required = false) String riskLevel) {
        return Result.ok(riskService.listRisks(projectId, page, size, status, riskLevel));
    }

    @GetMapping("/risks/{riskId}")
    @Operation(summary = "风险详情")
    public Result<RiskVO> getRisk(@PathVariable Long riskId) {
        return Result.ok(riskService.getRisk(riskId));
    }

    @PutMapping("/risks/{riskId}")
    @Operation(summary = "更新风险")
    public Result<RiskVO> updateRisk(@PathVariable Long riskId,
                                     @Valid @RequestBody RiskRequest request) {
        return Result.ok(riskService.updateRisk(riskId, request));
    }

    @PutMapping("/risks/{riskId}/status")
    @Operation(summary = "更新风险状态")
    public Result<RiskVO> updateRiskStatus(@PathVariable Long riskId,
                                           @RequestParam String status) {
        return Result.ok(riskService.updateRiskStatus(riskId, status));
    }

    @DeleteMapping("/risks/{riskId}")
    @Operation(summary = "删除风险")
    public Result<Void> deleteRisk(@PathVariable Long riskId) {
        riskService.deleteRisk(riskId);
        return Result.ok();
    }

    // ==================== 问题管理 ====================

    @PostMapping("/projects/{projectId}/issues")
    @Operation(summary = "提交问题")
    public Result<IssueVO> createIssue(@PathVariable Long projectId,
                                       @Valid @RequestBody IssueRequest request) {
        return Result.ok(riskService.createIssue(projectId, request));
    }

    @GetMapping("/projects/{projectId}/issues")
    @Operation(summary = "问题列表（分页）")
    public Result<PageResult<IssueVO>> listIssues(
            @PathVariable Long projectId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "状态筛选") @RequestParam(required = false) String status,
            @Parameter(description = "严重程度筛选") @RequestParam(required = false) String severity) {
        return Result.ok(riskService.listIssues(projectId, page, size, status, severity));
    }

    @GetMapping("/issues/{issueId}")
    @Operation(summary = "问题详情")
    public Result<IssueVO> getIssue(@PathVariable Long issueId) {
        return Result.ok(riskService.getIssue(issueId));
    }

    @PutMapping("/issues/{issueId}")
    @Operation(summary = "更新问题")
    public Result<IssueVO> updateIssue(@PathVariable Long issueId,
                                       @Valid @RequestBody IssueRequest request) {
        return Result.ok(riskService.updateIssue(issueId, request));
    }

    @PutMapping("/issues/{issueId}/status")
    @Operation(summary = "更新问题状态")
    public Result<IssueVO> updateIssueStatus(@PathVariable Long issueId,
                                             @Valid @RequestBody IssueStatusUpdateRequest request) {
        return Result.ok(riskService.updateIssueStatus(issueId, request));
    }

    @DeleteMapping("/issues/{issueId}")
    @Operation(summary = "删除问题")
    public Result<Void> deleteIssue(@PathVariable Long issueId) {
        riskService.deleteIssue(issueId);
        return Result.ok();
    }

    // ==================== 预警 ====================

    @GetMapping("/projects/{projectId}/risks/overdue")
    @Operation(summary = "到期风险预警")
    public Result<List<RiskVO>> getOverdueRisks(@PathVariable Long projectId) {
        return Result.ok(riskService.getOverdueRisks(projectId));
    }

    @GetMapping("/projects/{projectId}/issues/overdue")
    @Operation(summary = "到期问题预警")
    public Result<List<IssueVO>> getOverdueIssues(@PathVariable Long projectId) {
        return Result.ok(riskService.getOverdueIssues(projectId));
    }
}
