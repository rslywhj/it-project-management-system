package com.pm.risk.controller;

import com.pm.common.result.PageResult;
import com.pm.common.result.Result;
import com.pm.risk.dto.*;
import com.pm.risk.service.IssueService;
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
@Tag(name = "问题管理", description = "问题提交、分配、解决、关闭管理")
public class IssueController {

    private final IssueService issueService;

    @PostMapping("/projects/{projectId}/issues")
    @Operation(summary = "创建问题")
    public Result<IssueVO> createIssue(@PathVariable Long projectId,
                                        @Valid @RequestBody IssueCreateRequest request) {
        return Result.ok(issueService.createIssue(projectId, request));
    }

    @GetMapping("/projects/{projectId}/issues")
    @Operation(summary = "问题列表（分页）")
    public Result<PageResult<IssueVO>> listIssues(
            @PathVariable Long projectId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "关键词搜索") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态筛选") @RequestParam(required = false) String status,
            @Parameter(description = "严重程度筛选") @RequestParam(required = false) String severity,
            @Parameter(description = "指派人筛选") @RequestParam(required = false) Long assignedTo) {
        return Result.ok(issueService.listIssues(projectId, page, size, keyword, status, severity, assignedTo));
    }

    @GetMapping("/issues/{issueId}")
    @Operation(summary = "问题详情")
    public Result<IssueVO> getIssue(@PathVariable Long issueId) {
        return Result.ok(issueService.getIssue(issueId));
    }

    @PutMapping("/issues/{issueId}")
    @Operation(summary = "更新问题")
    public Result<IssueVO> updateIssue(@PathVariable Long issueId,
                                        @Valid @RequestBody IssueUpdateRequest request) {
        return Result.ok(issueService.updateIssue(issueId, request));
    }

    @PutMapping("/issues/{issueId}/status")
    @Operation(summary = "更新问题状态")
    public Result<IssueVO> updateIssueStatus(@PathVariable Long issueId,
                                              @Valid @RequestBody IssueStatusUpdateRequest request) {
        return Result.ok(issueService.updateIssueStatus(issueId, request));
    }

    @PutMapping("/issues/{issueId}/assign")
    @Operation(summary = "分配问题")
    public Result<IssueVO> assignIssue(@PathVariable Long issueId,
                                        @Parameter(description = "指派人ID") @RequestParam Long assigneeId) {
        return Result.ok(issueService.assignIssue(issueId, assigneeId));
    }

    @DeleteMapping("/issues/{issueId}")
    @Operation(summary = "删除问题")
    public Result<Void> deleteIssue(@PathVariable Long issueId) {
        issueService.deleteIssue(issueId);
        return Result.ok();
    }

    @GetMapping("/projects/{projectId}/issues/expiring")
    @Operation(summary = "获取即将到期的问题")
    public Result<List<IssueVO>> getExpiringIssues(
            @PathVariable Long projectId,
            @Parameter(description = "天数阈值") @RequestParam(defaultValue = "7") int days) {
        return Result.ok(issueService.getExpiringIssues(projectId, days));
    }

    @GetMapping("/projects/{projectId}/issues/overdue")
    @Operation(summary = "获取已逾期的问题")
    public Result<List<IssueVO>> getOverdueIssues(@PathVariable Long projectId) {
        return Result.ok(issueService.getOverdueIssues(projectId));
    }
}
