package com.pm.test.controller;

import com.pm.common.result.PageResult;
import com.pm.common.result.Result;
import com.pm.test.dto.*;
import com.pm.test.service.TestService;
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
@Tag(name = "测试管理", description = "测试计划、用例管理、缺陷跟踪、测试报告")
public class TestController {

    private final TestService testService;

    // ==================== 测试计划 ====================

    @PostMapping("/projects/{projectId}/test-plans")
    @Operation(summary = "创建测试计划")
    public Result<TestPlanVO> createTestPlan(@PathVariable Long projectId,
                                             @Valid @RequestBody TestPlanRequest request) {
        return Result.ok(testService.createTestPlan(projectId, request));
    }

    @GetMapping("/projects/{projectId}/test-plans")
    @Operation(summary = "测试计划列表（分页）")
    public Result<PageResult<TestPlanVO>> listTestPlans(
            @PathVariable Long projectId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "状态筛选") @RequestParam(required = false) String status) {
        return Result.ok(testService.listTestPlans(projectId, page, size, status));
    }

    @GetMapping("/test-plans/{planId}")
    @Operation(summary = "测试计划详情")
    public Result<TestPlanVO> getTestPlan(@PathVariable Long planId) {
        return Result.ok(testService.getTestPlan(planId));
    }

    @PutMapping("/test-plans/{planId}")
    @Operation(summary = "更新测试计划")
    public Result<TestPlanVO> updateTestPlan(@PathVariable Long planId,
                                             @Valid @RequestBody TestPlanRequest request) {
        return Result.ok(testService.updateTestPlan(planId, request));
    }

    @PutMapping("/test-plans/{planId}/status")
    @Operation(summary = "更新测试计划状态")
    public Result<TestPlanVO> updateTestPlanStatus(@PathVariable Long planId,
                                                   @RequestParam String status) {
        return Result.ok(testService.updateTestPlanStatus(planId, status));
    }

    @DeleteMapping("/test-plans/{planId}")
    @Operation(summary = "删除测试计划")
    public Result<Void> deleteTestPlan(@PathVariable Long planId) {
        testService.deleteTestPlan(planId);
        return Result.ok();
    }

    // ==================== 测试用例 ====================

    @PostMapping("/projects/{projectId}/test-cases")
    @Operation(summary = "创建测试用例")
    public Result<TestCaseVO> createTestCase(@PathVariable Long projectId,
                                             @Valid @RequestBody TestCaseRequest request) {
        return Result.ok(testService.createTestCase(projectId, request));
    }

    @GetMapping("/projects/{projectId}/test-cases")
    @Operation(summary = "测试用例列表（分页）")
    public Result<PageResult<TestCaseVO>> listTestCases(
            @PathVariable Long projectId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "关键词搜索") @RequestParam(required = false) String keyword,
            @Parameter(description = "模块筛选") @RequestParam(required = false) String module,
            @Parameter(description = "优先级筛选") @RequestParam(required = false) String priority) {
        return Result.ok(testService.listTestCases(projectId, page, size, keyword, module, priority));
    }

    @GetMapping("/test-cases/{caseId}")
    @Operation(summary = "测试用例详情")
    public Result<TestCaseVO> getTestCase(@PathVariable Long caseId) {
        return Result.ok(testService.getTestCase(caseId));
    }

    @PutMapping("/test-cases/{caseId}")
    @Operation(summary = "更新测试用例")
    public Result<TestCaseVO> updateTestCase(@PathVariable Long caseId,
                                             @Valid @RequestBody TestCaseRequest request) {
        return Result.ok(testService.updateTestCase(caseId, request));
    }

    @DeleteMapping("/test-cases/{caseId}")
    @Operation(summary = "删除测试用例")
    public Result<Void> deleteTestCase(@PathVariable Long caseId) {
        testService.deleteTestCase(caseId);
        return Result.ok();
    }

    // ==================== 测试执行 ====================

    @PostMapping("/test-plans/{planId}/executions")
    @Operation(summary = "执行测试用例")
    public Result<TestExecutionVO> executeTest(@PathVariable Long planId,
                                               @Valid @RequestBody TestExecutionRequest request) {
        return Result.ok(testService.executeTest(planId, request));
    }

    @GetMapping("/test-plans/{planId}/executions")
    @Operation(summary = "测试执行记录列表")
    public Result<List<TestExecutionVO>> listTestExecutions(@PathVariable Long planId) {
        return Result.ok(testService.listTestExecutions(planId));
    }

    // ==================== 缺陷管理 ====================

    @PostMapping("/projects/{projectId}/bugs")
    @Operation(summary = "提交缺陷")
    public Result<BugVO> createBug(@PathVariable Long projectId,
                                   @Valid @RequestBody BugRequest request) {
        return Result.ok(testService.createBug(projectId, request));
    }

    @GetMapping("/projects/{projectId}/bugs")
    @Operation(summary = "缺陷列表（分页）")
    public Result<PageResult<BugVO>> listBugs(
            @PathVariable Long projectId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "关键词搜索") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态筛选") @RequestParam(required = false) String status,
            @Parameter(description = "严重程度筛选") @RequestParam(required = false) String severity) {
        return Result.ok(testService.listBugs(projectId, page, size, keyword, status, severity));
    }

    @GetMapping("/bugs/{bugId}")
    @Operation(summary = "缺陷详情")
    public Result<BugVO> getBug(@PathVariable Long bugId) {
        return Result.ok(testService.getBug(bugId));
    }

    @PutMapping("/bugs/{bugId}")
    @Operation(summary = "更新缺陷")
    public Result<BugVO> updateBug(@PathVariable Long bugId,
                                   @Valid @RequestBody BugRequest request) {
        return Result.ok(testService.updateBug(bugId, request));
    }

    @PutMapping("/bugs/{bugId}/status")
    @Operation(summary = "更新缺陷状态")
    public Result<BugVO> updateBugStatus(@PathVariable Long bugId,
                                         @Valid @RequestBody BugStatusUpdateRequest request) {
        return Result.ok(testService.updateBugStatus(bugId, request));
    }

    @DeleteMapping("/bugs/{bugId}")
    @Operation(summary = "删除缺陷")
    public Result<Void> deleteBug(@PathVariable Long bugId) {
        testService.deleteBug(bugId);
        return Result.ok();
    }

    // ==================== 测试报告 ====================

    @GetMapping("/projects/{projectId}/test-plans/{planId}/report")
    @Operation(summary = "获取测试报告")
    public Result<TestReportVO> getTestReport(@PathVariable Long projectId,
                                              @PathVariable Long planId) {
        return Result.ok(testService.getTestReport(projectId, planId));
    }
}
