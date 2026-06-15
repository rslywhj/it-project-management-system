package com.pm.test.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pm.auth.annotation.RequirePermission;
import com.pm.common.exception.BusinessException;
import com.pm.common.result.PageResult;
import com.pm.common.result.ResultCode;
import com.pm.common.util.UserContext;
import com.pm.test.domain.Bug;
import com.pm.test.domain.TestCase;
import com.pm.test.domain.TestExecution;
import com.pm.test.domain.TestPlan;
import com.pm.test.dto.*;
import com.pm.test.mapper.BugMapper;
import com.pm.test.mapper.TestCaseMapper;
import com.pm.test.mapper.TestExecutionMapper;
import com.pm.test.mapper.TestPlanMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestService {

    private final TestPlanMapper testPlanMapper;
    private final TestCaseMapper testCaseMapper;
    private final TestExecutionMapper testExecutionMapper;
    private final BugMapper bugMapper;

    // ==================== 测试计划管理 ====================

    @Transactional
    @RequirePermission("test:create")
    public TestPlanVO createTestPlan(Long projectId, TestPlanRequest request) {
        TestPlan plan = new TestPlan();
        BeanUtils.copyProperties(request, plan);
        plan.setProjectId(projectId);
        plan.setStatus("draft");
        plan.setTotalCases(0);
        plan.setExecutedCases(0);
        plan.setPassedCases(0);
        plan.setFailedCases(0);
        plan.setBlockedCases(0);
        plan.setPassRate(BigDecimal.ZERO);
        testPlanMapper.insert(plan);
        return toTestPlanVO(plan);
    }

    public PageResult<TestPlanVO> listTestPlans(Long projectId, int page, int size, String status) {
        LambdaQueryWrapper<TestPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TestPlan::getProjectId, projectId)
                .eq(StringUtils.hasText(status), TestPlan::getStatus, status)
                .orderByDesc(TestPlan::getCreatedAt);

        Page<TestPlan> result = testPlanMapper.selectPage(new Page<>(page, size), wrapper);
        List<TestPlanVO> voList = result.getRecords().stream()
                .map(this::toTestPlanVO)
                .collect(Collectors.toList());
        return PageResult.of(voList, result.getTotal(), page, size);
    }

    public TestPlanVO getTestPlan(Long planId) {
        TestPlan plan = testPlanMapper.selectById(planId);
        if (plan == null) {
            throw new BusinessException(1060, "测试计划不存在");
        }
        return toTestPlanVO(plan);
    }

    @Transactional
    @RequirePermission("test:edit")
    public TestPlanVO updateTestPlan(Long planId, TestPlanRequest request) {
        TestPlan plan = testPlanMapper.selectById(planId);
        if (plan == null) {
            throw new BusinessException(1060, "测试计划不存在");
        }
        BeanUtils.copyProperties(request, plan, "id", "projectId", "status", "totalCases",
                "executedCases", "passedCases", "failedCases", "blockedCases", "passRate",
                "createdAt", "createdBy", "isDeleted");
        testPlanMapper.updateById(plan);
        return toTestPlanVO(plan);
    }

    @Transactional
    @RequirePermission("test:edit")
    public TestPlanVO updateTestPlanStatus(Long planId, String status) {
        TestPlan plan = testPlanMapper.selectById(planId);
        if (plan == null) {
            throw new BusinessException(1060, "测试计划不存在");
        }
        plan.setStatus(status);
        testPlanMapper.updateById(plan);
        return toTestPlanVO(plan);
    }

    @Transactional
    @RequirePermission("test:delete")
    public void deleteTestPlan(Long planId) {
        testPlanMapper.deleteById(planId);
    }

    // ==================== 测试用例管理 ====================

    @Transactional
    @RequirePermission("test:create")
    public TestCaseVO createTestCase(Long projectId, TestCaseRequest request) {
        TestCase testCase = new TestCase();
        BeanUtils.copyProperties(request, testCase);
        testCase.setProjectId(projectId);
        testCase.setStatus("active");
        testCaseMapper.insert(testCase);
        return toTestCaseVO(testCase);
    }

    public PageResult<TestCaseVO> listTestCases(Long projectId, int page, int size,
                                                 String keyword, String module, String priority) {
        LambdaQueryWrapper<TestCase> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TestCase::getProjectId, projectId)
                .like(StringUtils.hasText(keyword), TestCase::getTitle, keyword)
                .eq(StringUtils.hasText(module), TestCase::getModule, module)
                .eq(StringUtils.hasText(priority), TestCase::getPriority, priority)
                .orderByDesc(TestCase::getCreatedAt);

        Page<TestCase> result = testCaseMapper.selectPage(new Page<>(page, size), wrapper);
        List<TestCaseVO> voList = result.getRecords().stream()
                .map(this::toTestCaseVO)
                .collect(Collectors.toList());
        return PageResult.of(voList, result.getTotal(), page, size);
    }

    public TestCaseVO getTestCase(Long caseId) {
        TestCase testCase = testCaseMapper.selectById(caseId);
        if (testCase == null) {
            throw new BusinessException(1061, "测试用例不存在");
        }
        return toTestCaseVO(testCase);
    }

    @Transactional
    @RequirePermission("test:edit")
    public TestCaseVO updateTestCase(Long caseId, TestCaseRequest request) {
        TestCase testCase = testCaseMapper.selectById(caseId);
        if (testCase == null) {
            throw new BusinessException(1061, "测试用例不存在");
        }
        BeanUtils.copyProperties(request, testCase, "id", "projectId", "status",
                "createdAt", "createdBy", "isDeleted");
        testCaseMapper.updateById(testCase);
        return toTestCaseVO(testCase);
    }

    @Transactional
    @RequirePermission("test:delete")
    public void deleteTestCase(Long caseId) {
        testCaseMapper.deleteById(caseId);
    }

    // ==================== 测试执行管理 ====================

    @Transactional
    @RequirePermission("test:create")
    public TestExecutionVO executeTest(Long planId, TestExecutionRequest request) {
        // 检查是否已存在执行记录
        TestExecution existing = testExecutionMapper.selectOne(
                new LambdaQueryWrapper<TestExecution>()
                        .eq(TestExecution::getTestPlanId, planId)
                        .eq(TestExecution::getTestCaseId, request.getTestCaseId()));

        if (existing != null) {
            // 更新已有记录
            existing.setStatus(request.getStatus());
            existing.setActualResult(request.getActualResult());
            existing.setRemark(request.getRemark());
            existing.setBugId(request.getBugId());
            existing.setExecutedBy(UserContext.getUserId());
            existing.setExecutedAt(LocalDateTime.now());
            testExecutionMapper.updateById(existing);
            updateTestPlanStats(planId);
            return toTestExecutionVO(existing);
        }

        // 创建新记录
        TestExecution execution = new TestExecution();
        execution.setTestPlanId(planId);
        execution.setTestCaseId(request.getTestCaseId());
        execution.setStatus(request.getStatus());
        execution.setActualResult(request.getActualResult());
        execution.setRemark(request.getRemark());
        execution.setBugId(request.getBugId());
        execution.setExecutedBy(UserContext.getUserId());
        execution.setExecutedAt(LocalDateTime.now());
        testExecutionMapper.insert(execution);

        // 更新测试计划统计
        updateTestPlanStats(planId);

        return toTestExecutionVO(execution);
    }

    public List<TestExecutionVO> listTestExecutions(Long planId) {
        List<TestExecution> executions = testExecutionMapper.selectList(
                new LambdaQueryWrapper<TestExecution>()
                        .eq(TestExecution::getTestPlanId, planId)
                        .orderByDesc(TestExecution::getExecutedAt));
        return executions.stream().map(this::toTestExecutionVO).collect(Collectors.toList());
    }

    // ==================== 缺陷管理 ====================

    @Transactional
    @RequirePermission("test:create")
    public BugVO createBug(Long projectId, BugRequest request) {
        Bug bug = new Bug();
        BeanUtils.copyProperties(request, bug);
        bug.setProjectId(projectId);
        bug.setStatus("open");
        bug.setReportedBy(UserContext.getUserId());
        bugMapper.insert(bug);
        return toBugVO(bug);
    }

    public PageResult<BugVO> listBugs(Long projectId, int page, int size,
                                       String keyword, String status, String severity) {
        LambdaQueryWrapper<Bug> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bug::getProjectId, projectId)
                .like(StringUtils.hasText(keyword), Bug::getTitle, keyword)
                .eq(StringUtils.hasText(status), Bug::getStatus, status)
                .eq(StringUtils.hasText(severity), Bug::getSeverity, severity)
                .orderByDesc(Bug::getCreatedAt);

        Page<Bug> result = bugMapper.selectPage(new Page<>(page, size), wrapper);
        List<BugVO> voList = result.getRecords().stream()
                .map(this::toBugVO)
                .collect(Collectors.toList());
        return PageResult.of(voList, result.getTotal(), page, size);
    }

    public BugVO getBug(Long bugId) {
        Bug bug = bugMapper.selectById(bugId);
        if (bug == null) {
            throw new BusinessException(1062, "缺陷不存在");
        }
        return toBugVO(bug);
    }

    @Transactional
    @RequirePermission("test:edit")
    public BugVO updateBug(Long bugId, BugRequest request) {
        Bug bug = bugMapper.selectById(bugId);
        if (bug == null) {
            throw new BusinessException(1062, "缺陷不存在");
        }
        BeanUtils.copyProperties(request, bug, "id", "projectId", "status", "reportedBy",
                "resolvedAt", "resolvedBy", "resolution", "closedAt",
                "createdAt", "createdBy", "isDeleted");
        bugMapper.updateById(bug);
        return toBugVO(bug);
    }

    @Transactional
    @RequirePermission("test:edit")
    public BugVO updateBugStatus(Long bugId, BugStatusUpdateRequest request) {
        Bug bug = bugMapper.selectById(bugId);
        if (bug == null) {
            throw new BusinessException(1062, "缺陷不存在");
        }

        bug.setStatus(request.getStatus());

        if ("resolved".equals(request.getStatus())) {
            bug.setResolvedAt(LocalDateTime.now());
            bug.setResolvedBy(UserContext.getUserId());
            bug.setResolution(request.getResolution());
        } else if ("closed".equals(request.getStatus())) {
            bug.setClosedAt(LocalDateTime.now());
        }

        bugMapper.updateById(bug);
        return toBugVO(bug);
    }

    @Transactional
    @RequirePermission("test:delete")
    public void deleteBug(Long bugId) {
        bugMapper.deleteById(bugId);
    }

    // ==================== 测试报告 ====================

    public TestReportVO getTestReport(Long projectId, Long planId) {
        TestPlan plan = testPlanMapper.selectById(planId);
        if (plan == null) {
            throw new BusinessException(1060, "测试计划不存在");
        }

        TestReportVO report = new TestReportVO();
        report.setProjectId(projectId);
        report.setTestPlanId(planId);
        report.setPlanName(plan.getName());
        report.setTotalCases(plan.getTotalCases());
        report.setExecutedCases(plan.getExecutedCases());
        report.setPassedCases(plan.getPassedCases());
        report.setFailedCases(plan.getFailedCases());
        report.setBlockedCases(plan.getBlockedCases());
        report.setPassRate(plan.getPassRate());

        // 统计跳过数
        int skipped = plan.getTotalCases() - plan.getExecutedCases();
        report.setSkippedCases(Math.max(0, skipped));

        // 统计缺陷
        List<Bug> bugs = bugMapper.selectList(
                new LambdaQueryWrapper<Bug>()
                        .eq(Bug::getProjectId, projectId)
                        .eq(Bug::getTestPlanId, planId));

        report.setTotalBugs(bugs.size());
        report.setOpenBugs((int) bugs.stream()
                .filter(b -> "open".equals(b.getStatus()) || "in_progress".equals(b.getStatus())).count());
        report.setResolvedBugs((int) bugs.stream()
                .filter(b -> "resolved".equals(b.getStatus()) || "closed".equals(b.getStatus())).count());

        // 缺陷按严重程度分布
        List<TestReportVO.BugSeverityStat> severityStats = new ArrayList<>();
        for (String severity : new String[]{"critical", "major", "minor", "trivial"}) {
            long count = bugs.stream().filter(b -> severity.equals(b.getSeverity())).count();
            if (count > 0) {
                TestReportVO.BugSeverityStat stat = new TestReportVO.BugSeverityStat();
                stat.setSeverity(severity);
                stat.setCount((int) count);
                severityStats.add(stat);
            }
        }
        report.setSeverityDistribution(severityStats);

        return report;
    }

    // ==================== 内部方法 ====================

    private void updateTestPlanStats(Long planId) {
        TestPlan plan = testPlanMapper.selectById(planId);
        if (plan == null) return;

        List<TestExecution> executions = testExecutionMapper.selectList(
                new LambdaQueryWrapper<TestExecution>()
                        .eq(TestExecution::getTestPlanId, planId));

        int totalCases = testCaseMapper.selectCount(
                new LambdaQueryWrapper<TestCase>()
                        .eq(TestCase::getProjectId, plan.getProjectId())
                        .eq(TestCase::getDeleted, 0)).intValue();

        plan.setTotalCases(totalCases);
        plan.setExecutedCases(executions.size());
        plan.setPassedCases((int) executions.stream().filter(e -> "passed".equals(e.getStatus())).count());
        plan.setFailedCases((int) executions.stream().filter(e -> "failed".equals(e.getStatus())).count());
        plan.setBlockedCases((int) executions.stream().filter(e -> "blocked".equals(e.getStatus())).count());

        if (executions.isEmpty()) {
            plan.setPassRate(BigDecimal.ZERO);
        } else {
            BigDecimal passRate = BigDecimal.valueOf(plan.getPassedCases())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(executions.size()), 2, RoundingMode.HALF_UP);
            plan.setPassRate(passRate);
        }

        testPlanMapper.updateById(plan);
    }

    // ==================== VO 转换 ====================

    private TestPlanVO toTestPlanVO(TestPlan plan) {
        TestPlanVO vo = new TestPlanVO();
        BeanUtils.copyProperties(plan, vo);
        return vo;
    }

    private TestCaseVO toTestCaseVO(TestCase testCase) {
        TestCaseVO vo = new TestCaseVO();
        BeanUtils.copyProperties(testCase, vo);
        return vo;
    }

    private TestExecutionVO toTestExecutionVO(TestExecution execution) {
        TestExecutionVO vo = new TestExecutionVO();
        BeanUtils.copyProperties(execution, vo);

        // 获取用例标题
        TestCase testCase = testCaseMapper.selectById(execution.getTestCaseId());
        if (testCase != null) {
            vo.setCaseTitle(testCase.getTitle());
        }

        return vo;
    }

    private BugVO toBugVO(Bug bug) {
        BugVO vo = new BugVO();
        BeanUtils.copyProperties(bug, vo);
        return vo;
    }
}
