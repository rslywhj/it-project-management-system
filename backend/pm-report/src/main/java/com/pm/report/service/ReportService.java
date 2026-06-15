package com.pm.report.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pm.auth.annotation.RequirePermission;
import com.pm.common.exception.BusinessException;
import com.pm.milestone.domain.Milestone;
import com.pm.milestone.mapper.MilestoneMapper;
import com.pm.project.domain.Project;
import com.pm.project.mapper.ProjectMapper;
import com.pm.promotion.domain.PromotionProgress;
import com.pm.promotion.domain.PromotionStageTemplate;
import com.pm.promotion.domain.PromotionUnit;
import com.pm.promotion.mapper.PromotionProgressMapper;
import com.pm.promotion.mapper.PromotionStageTemplateMapper;
import com.pm.promotion.mapper.PromotionUnitMapper;
import com.pm.report.dto.*;
import com.pm.requirement.domain.Requirement;
import com.pm.requirement.mapper.RequirementMapper;
import com.pm.task.domain.Task;
import com.pm.task.mapper.TaskMapper;
import com.pm.test.domain.Bug;
import com.pm.test.mapper.BugMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ProjectMapper projectMapper;
    private final RequirementMapper requirementMapper;
    private final TaskMapper taskMapper;
    private final BugMapper bugMapper;
    private final MilestoneMapper milestoneMapper;
    private final PromotionUnitMapper promotionUnitMapper;
    private final PromotionStageTemplateMapper stageTemplateMapper;
    private final PromotionProgressMapper progressMapper;

    // ==================== 项目看板 ====================

    @RequirePermission("project:view")
    public ProjectDashboardVO getProjectDashboard(Long projectId) {
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException(1010, "项目不存在");
        }

        ProjectDashboardVO dashboard = new ProjectDashboardVO();
        dashboard.setProjectId(projectId);
        dashboard.setProjectName(project.getName());
        dashboard.setProjectStatus(project.getStatus());

        // 需求统计
        dashboard.setRequirementStats(getRequirementStats(projectId));

        // 任务统计
        dashboard.setTaskStats(getTaskStats(projectId));

        // 缺陷统计
        dashboard.setBugStats(getBugStats(projectId));

        // 里程碑统计
        dashboard.setMilestoneStats(getMilestoneStats(projectId));

        // 健康度计算
        dashboard.setHealthScore(calculateHealthScore(projectId));

        return dashboard;
    }

    // ==================== 甘特图数据 ====================

    @RequirePermission("task:view")
    public GanttChartDataVO getGanttChartData(Long projectId) {
        GanttChartDataVO data = new GanttChartDataVO();
        data.setProjectId(projectId);

        List<Task> tasks = taskMapper.selectList(
                new LambdaQueryWrapper<Task>()
                        .eq(Task::getProjectId, projectId)
                        .orderByAsc(Task::getWbsCode));

        List<GanttChartDataVO.GanttTask> ganttTasks = tasks.stream().map(task -> {
            GanttChartDataVO.GanttTask gt = new GanttChartDataVO.GanttTask();
            gt.setId(task.getId());
            gt.setText(task.getTitle());
            gt.setStart(task.getPlannedStart());
            gt.setEnd(task.getPlannedEnd());
            gt.setParent(task.getParentTaskId());
            gt.setProgress(task.getCompletionRate() != null ?
                    task.getCompletionRate().doubleValue() / 100.0 : 0.0);
            gt.setType(task.getParentTaskId() == null ? "project" : "task");
            gt.setOpen(true);
            return gt;
        }).collect(Collectors.toList());
        data.setTasks(ganttTasks);

        // 依赖关系
        List<GanttChartDataVO.GanttDependency> dependencies = new ArrayList<>();
        // TODO: 从 task_dependency 表查询
        data.setDependencies(dependencies);

        return data;
    }

    // ==================== 燃尽图数据 ====================

    @RequirePermission("task:view")
    public BurndownChartVO getBurndownChart(Long projectId, String iteration) {
        BurndownChartVO chart = new BurndownChartVO();
        chart.setProjectId(projectId);
        chart.setIteration(iteration);

        // 获取项目信息
        Project project = projectMapper.selectById(projectId);
        if (project != null) {
            chart.setStartDate(project.getStartDate());
            chart.setEndDate(project.getEndDate());
        }

        // 获取任务总数
        List<Task> tasks = taskMapper.selectList(
                new LambdaQueryWrapper<Task>()
                        .eq(Task::getProjectId, projectId));
        chart.setTotalTasks(tasks.size());

        // 计算理想燃尽线
        if (project != null && project.getStartDate() != null && project.getEndDate() != null) {
            long totalDays = ChronoUnit.DAYS.between(project.getStartDate(), project.getEndDate());
            List<BurndownChartVO.BurndownPoint> idealLine = new ArrayList<>();
            for (int i = 0; i <= totalDays; i++) {
                BurndownChartVO.BurndownPoint point = new BurndownChartVO.BurndownPoint();
                point.setDate(project.getStartDate().plusDays(i));
                point.setRemaining(BigDecimal.valueOf(tasks.size() * (totalDays - i) / totalDays));
                idealLine.add(point);
            }
            chart.setIdealLine(idealLine);
        }

        // 计算实际燃尽线（按天统计未完成任务数）
        // TODO: 需要任务历史记录表来精确计算
        List<BurndownChartVO.BurndownPoint> actualLine = new ArrayList<>();
        long doneTasks = tasks.stream()
                .filter(t -> "done".equals(t.getStatus())).count();
        BurndownChartVO.BurndownPoint currentPoint = new BurndownChartVO.BurndownPoint();
        currentPoint.setDate(LocalDate.now());
        currentPoint.setRemaining(BigDecimal.valueOf(tasks.size() - doneTasks));
        actualLine.add(currentPoint);
        chart.setActualLine(actualLine);

        return chart;
    }

    // ==================== 自定义报表 ====================

    @RequirePermission("report:view")
    public CustomReportVO generateCustomReport(Long projectId, CustomReportRequest request) {
        CustomReportVO report = new CustomReportVO();
        report.setReportName(request.getReportName());
        report.setDimension(request.getDimension());
        report.setMetric(request.getMetric());

        List<Map<String, Object>> data = new ArrayList<>();

        switch (request.getMetric()) {
            case "requirement":
                data = getRequirementReportData(projectId, request);
                break;
            case "task":
                data = getTaskReportData(projectId, request);
                break;
            case "bug":
                data = getBugReportData(projectId, request);
                break;
            default:
                data = getTaskReportData(projectId, request);
        }

        report.setData(data);

        // 汇总
        Map<String, Object> summary = new HashMap<>();
        summary.put("total", data.size());
        report.setSummary(summary);

        return report;
    }

    // ==================== 推广汇总报表 ====================

    @RequirePermission("promotion:view")
    public PromotionSummaryVO getPromotionSummary(Long projectId) {
        PromotionSummaryVO summary = new PromotionSummaryVO();
        summary.setProjectId(projectId);

        List<PromotionUnit> units = promotionUnitMapper.selectList(
                new LambdaQueryWrapper<PromotionUnit>()
                        .eq(PromotionUnit::getProjectId, projectId));

        summary.setTotalUnits(units.size());

        // 整体完成率
        BigDecimal overallRate = units.stream()
                .map(PromotionUnit::getCompletionRate)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (!units.isEmpty()) {
            summary.setOverallCompletionRate(
                    overallRate.divide(BigDecimal.valueOf(units.size()), 2, RoundingMode.HALF_UP));
        } else {
            summary.setOverallCompletionRate(BigDecimal.ZERO);
        }

        // 各单位进度
        List<PromotionSummaryVO.UnitProgress> unitProgressList = units.stream().map(unit -> {
            PromotionSummaryVO.UnitProgress progress = new PromotionSummaryVO.UnitProgress();
            progress.setUnitId(unit.getId());
            progress.setOrgName(unit.getOrgName());
            progress.setCompletionRate(unit.getCompletionRate());
            progress.setStatus(unit.getStatus());

            // 获取当前阶段名称
            if (unit.getCurrentStageId() != null) {
                PromotionStageTemplate stage = stageTemplateMapper.selectById(unit.getCurrentStageId());
                if (stage != null) {
                    progress.setCurrentStage(stage.getName());
                }
            }

            // 计算延期天数
            if (unit.getExpectedEndDate() != null && !"completed".equals(unit.getStatus())) {
                long overdueDays = ChronoUnit.DAYS.between(unit.getExpectedEndDate(), LocalDate.now());
                progress.setOverdueDays(overdueDays > 0 ? (int) overdueDays : 0);
            } else {
                progress.setOverdueDays(0);
            }

            return progress;
        }).collect(Collectors.toList());
        summary.setUnitProgressList(unitProgressList);

        // 各阶段完成情况
        List<PromotionStageTemplate> stages = stageTemplateMapper.selectList(
                new LambdaQueryWrapper<PromotionStageTemplate>()
                        .and(w -> w.eq(PromotionStageTemplate::getProjectId, projectId)
                                .or().isNull(PromotionStageTemplate::getProjectId))
                        .orderByAsc(PromotionStageTemplate::getSortOrder));

        List<PromotionSummaryVO.StageCompletion> stageCompletions = stages.stream().map(stage -> {
            PromotionSummaryVO.StageCompletion completion = new PromotionSummaryVO.StageCompletion();
            completion.setStageName(stage.getName());

            List<PromotionProgress> progressList = progressMapper.selectList(
                    new LambdaQueryWrapper<PromotionProgress>()
                            .eq(PromotionProgress::getStageTemplateId, stage.getId()));

            completion.setTotalUnits(progressList.size());
            completion.setCompletedUnits((int) progressList.stream()
                    .filter(p -> "completed".equals(p.getStatus())).count());
            completion.setInProgressUnits((int) progressList.stream()
                    .filter(p -> "in_progress".equals(p.getStatus())).count());
            completion.setPendingUnits((int) progressList.stream()
                    .filter(p -> "pending".equals(p.getStatus())).count());

            return completion;
        }).collect(Collectors.toList());
        summary.setStageCompletions(stageCompletions);

        return summary;
    }

    // ==================== 内部方法 ====================

    private ProjectDashboardVO.RequirementStats getRequirementStats(Long projectId) {
        ProjectDashboardVO.RequirementStats stats = new ProjectDashboardVO.RequirementStats();
        List<Requirement> requirements = requirementMapper.selectList(
                new LambdaQueryWrapper<Requirement>()
                        .eq(Requirement::getProjectId, projectId)
                        .eq(Requirement::getDeleted, 0));

        stats.setTotal(requirements.size());
        stats.setDraft((int) requirements.stream().filter(r -> "draft".equals(r.getStatus())).count());
        stats.setReviewing((int) requirements.stream().filter(r -> "reviewing".equals(r.getStatus())).count());
        stats.setApproved((int) requirements.stream().filter(r -> "approved".equals(r.getStatus())).count());
        stats.setScheduled((int) requirements.stream().filter(r -> "scheduled".equals(r.getStatus())).count());
        stats.setDone((int) requirements.stream().filter(r -> "done".equals(r.getStatus())).count());

        if (requirements.isEmpty()) {
            stats.setCompletionRate(BigDecimal.ZERO);
        } else {
            stats.setCompletionRate(BigDecimal.valueOf(stats.getDone())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(requirements.size()), 2, RoundingMode.HALF_UP));
        }

        return stats;
    }

    private ProjectDashboardVO.TaskStats getTaskStats(Long projectId) {
        ProjectDashboardVO.TaskStats stats = new ProjectDashboardVO.TaskStats();
        List<Task> tasks = taskMapper.selectList(
                new LambdaQueryWrapper<Task>()
                        .eq(Task::getProjectId, projectId)
                        .eq(Task::getDeleted, 0));

        stats.setTotal(tasks.size());
        stats.setTodo((int) tasks.stream().filter(t -> "todo".equals(t.getStatus())).count());
        stats.setInProgress((int) tasks.stream().filter(t -> "in_progress".equals(t.getStatus())).count());
        stats.setDone((int) tasks.stream().filter(t -> "done".equals(t.getStatus())).count());

        if (tasks.isEmpty()) {
            stats.setCompletionRate(BigDecimal.ZERO);
            stats.setAverageProgress(BigDecimal.ZERO);
        } else {
            stats.setCompletionRate(BigDecimal.valueOf(stats.getDone())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(tasks.size()), 2, RoundingMode.HALF_UP));

            BigDecimal totalProgress = tasks.stream()
                    .map(t -> t.getCompletionRate() != null ? t.getCompletionRate() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            stats.setAverageProgress(totalProgress.divide(BigDecimal.valueOf(tasks.size()), 2, RoundingMode.HALF_UP));
        }

        return stats;
    }

    private ProjectDashboardVO.BugStats getBugStats(Long projectId) {
        ProjectDashboardVO.BugStats stats = new ProjectDashboardVO.BugStats();
        List<Bug> bugs = bugMapper.selectList(
                new LambdaQueryWrapper<Bug>()
                        .eq(Bug::getProjectId, projectId)
                        .eq(Bug::getDeleted, 0));

        stats.setTotal(bugs.size());
        stats.setOpen((int) bugs.stream().filter(b -> "open".equals(b.getStatus())).count());
        stats.setInProgress((int) bugs.stream().filter(b -> "in_progress".equals(b.getStatus())).count());
        stats.setResolved((int) bugs.stream().filter(b -> "resolved".equals(b.getStatus())).count());
        stats.setClosed((int) bugs.stream().filter(b -> "closed".equals(b.getStatus())).count());
        stats.setCriticalCount((int) bugs.stream().filter(b -> "critical".equals(b.getSeverity())).count());
        stats.setMajorCount((int) bugs.stream().filter(b -> "major".equals(b.getSeverity())).count());

        return stats;
    }

    private ProjectDashboardVO.MilestoneStats getMilestoneStats(Long projectId) {
        ProjectDashboardVO.MilestoneStats stats = new ProjectDashboardVO.MilestoneStats();
        List<Milestone> milestones = milestoneMapper.selectList(
                new LambdaQueryWrapper<Milestone>()
                        .eq(Milestone::getProjectId, projectId)
                        .eq(Milestone::getDeleted, 0));

        stats.setTotal(milestones.size());
        stats.setPending((int) milestones.stream().filter(m -> "pending".equals(m.getStatus())).count());
        stats.setCompleted((int) milestones.stream().filter(m -> "completed".equals(m.getStatus())).count());
        stats.setDelayed((int) milestones.stream().filter(m -> "delayed".equals(m.getStatus())).count());
        stats.setAtRisk((int) milestones.stream().filter(m -> "at_risk".equals(m.getStatus())).count());

        return stats;
    }

    private Integer calculateHealthScore(Long projectId) {
        int score = 100;

        // 任务完成率影响
        ProjectDashboardVO.TaskStats taskStats = getTaskStats(projectId);
        if (taskStats.getTotal() > 0) {
            int taskCompletion = taskStats.getCompletionRate().intValue();
            if (taskCompletion < 50) score -= 20;
            else if (taskCompletion < 80) score -= 10;
        }

        // 缺陷影响
        ProjectDashboardVO.BugStats bugStats = getBugStats(projectId);
        score -= bugStats.getCriticalCount() * 5;
        score -= bugStats.getMajorCount() * 2;

        // 里程碑延期影响
        ProjectDashboardVO.MilestoneStats milestoneStats = getMilestoneStats(projectId);
        score -= milestoneStats.getDelayed() * 10;
        score -= milestoneStats.getAtRisk() * 5;

        return Math.max(0, Math.min(100, score));
    }

    private List<Map<String, Object>> getRequirementReportData(Long projectId, CustomReportRequest request) {
        List<Requirement> requirements = requirementMapper.selectList(
                new LambdaQueryWrapper<Requirement>()
                        .eq(Requirement::getProjectId, projectId)
                        .eq(Requirement::getDeleted, 0));

        Map<String, Long> grouped = requirements.stream()
                .collect(Collectors.groupingBy(Requirement::getStatus, Collectors.counting()));

        return grouped.entrySet().stream().map(entry -> {
            Map<String, Object> row = new HashMap<>();
            row.put("category", entry.getKey());
            row.put("count", entry.getValue());
            return row;
        }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> getTaskReportData(Long projectId, CustomReportRequest request) {
        List<Task> tasks = taskMapper.selectList(
                new LambdaQueryWrapper<Task>()
                        .eq(Task::getProjectId, projectId)
                        .eq(Task::getDeleted, 0));

        Map<String, Long> grouped = tasks.stream()
                .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()));

        return grouped.entrySet().stream().map(entry -> {
            Map<String, Object> row = new HashMap<>();
            row.put("category", entry.getKey());
            row.put("count", entry.getValue());
            return row;
        }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> getBugReportData(Long projectId, CustomReportRequest request) {
        List<Bug> bugs = bugMapper.selectList(
                new LambdaQueryWrapper<Bug>()
                        .eq(Bug::getProjectId, projectId)
                        .eq(Bug::getDeleted, 0));

        Map<String, Long> grouped = bugs.stream()
                .collect(Collectors.groupingBy(Bug::getSeverity, Collectors.counting()));

        return grouped.entrySet().stream().map(entry -> {
            Map<String, Object> row = new HashMap<>();
            row.put("category", entry.getKey());
            row.put("count", entry.getValue());
            return row;
        }).collect(Collectors.toList());
    }
}
