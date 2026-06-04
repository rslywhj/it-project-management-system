package com.pm.promotion.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pm.auth.annotation.RequirePermission;
import com.pm.promotion.domain.PromotionProgress;
import com.pm.promotion.domain.PromotionStageTemplate;
import com.pm.promotion.domain.PromotionUnit;
import com.pm.promotion.dto.PromotionDashboardVO;
import com.pm.promotion.mapper.PromotionProgressMapper;
import com.pm.promotion.mapper.PromotionStageTemplateMapper;
import com.pm.promotion.mapper.PromotionUnitMapper;
import com.pm.task.domain.Task;
import com.pm.task.mapper.TaskMapper;
import com.pm.promotion.dto.BatchTaskCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionDashboardService {

    private final PromotionUnitMapper unitMapper;
    private final PromotionStageTemplateMapper stageTemplateMapper;
    private final PromotionProgressMapper progressMapper;
    private final TaskMapper taskMapper;

    @RequirePermission("promotion:view")
    public PromotionDashboardVO getDashboard(Long projectId) {
        PromotionDashboardVO dashboard = new PromotionDashboardVO();
        dashboard.setProjectId(projectId);

        List<PromotionUnit> units = unitMapper.selectList(
                new LambdaQueryWrapper<PromotionUnit>()
                        .eq(PromotionUnit::getProjectId, projectId));

        dashboard.setTotalUnits(units.size());
        dashboard.setInProgressUnits((int) units.stream().filter(u -> "in_progress".equals(u.getStatus())).count());
        dashboard.setCompletedUnits((int) units.stream().filter(u -> "completed".equals(u.getStatus())).count());
        dashboard.setSuspendedUnits((int) units.stream().filter(u -> "suspended".equals(u.getStatus())).count());

        // 计算整体完成率
        if (!units.isEmpty()) {
            BigDecimal totalRate = units.stream()
                    .map(PromotionUnit::getCompletionRate)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            dashboard.setOverallCompletionRate(
                    totalRate.divide(BigDecimal.valueOf(units.size()), 2, RoundingMode.HALF_UP));
        } else {
            dashboard.setOverallCompletionRate(BigDecimal.ZERO);
        }

        // 构建各单元进度摘要
        List<Long> allStageIds = units.stream()
                .filter(u -> u.getCurrentStageId() != null)
                .map(PromotionUnit::getCurrentStageId)
                .collect(Collectors.toList());
        Map<Long, String> stageNameMap = allStageIds.isEmpty() ? Map.of() :
                stageTemplateMapper.selectBatchIds(allStageIds).stream()
                        .collect(Collectors.toMap(PromotionStageTemplate::getId, PromotionStageTemplate::getName));

        LocalDate today = LocalDate.now();
        int delayedCount = 0;
        List<PromotionDashboardVO.UnitProgressSummary> summaries = new ArrayList<>();

        for (PromotionUnit unit : units) {
            PromotionDashboardVO.UnitProgressSummary summary = new PromotionDashboardVO.UnitProgressSummary();
            summary.setUnitId(unit.getId());
            summary.setOrgName(unit.getOrgName());
            summary.setStatus(unit.getStatus());
            summary.setCompletionRate(unit.getCompletionRate());

            if (unit.getCurrentStageId() != null) {
                summary.setCurrentStageName(stageNameMap.get(unit.getCurrentStageId()));
            }

            if (unit.getExpectedEndDate() != null) {
                summary.setExpectedEndDate(unit.getExpectedEndDate().toString());
                boolean delayed = !"completed".equals(unit.getStatus()) &&
                        unit.getExpectedEndDate().isBefore(today);
                summary.setDelayed(delayed);
                if (delayed) delayedCount++;
            } else {
                summary.setDelayed(false);
            }

            summaries.add(summary);
        }

        dashboard.setDelayedUnits(delayedCount);
        dashboard.setUnitSummaries(summaries);

        return dashboard;
    }

    /**
     * 基于模板批量创建任务
     */
    @Transactional
    @RequirePermission("promotion:manage")
    public List<Task> batchCreateTasks(Long projectId, BatchTaskCreateRequest request) {
        List<Task> createdTasks = new ArrayList<>();

        for (Long unitId : request.getPromotionUnitIds()) {
            Task task = new Task();
            task.setProjectId(projectId);
            task.setPromotionUnitId(unitId);
            task.setTitle(request.getTaskTitle());
            task.setDescription(request.getTaskDescription());
            task.setType(request.getTaskType() != null ? request.getTaskType() : "dev");
            task.setStatus("todo");
            task.setCompletionRate(BigDecimal.ZERO);
            task.setAssignedTo(request.getAssignedTo());
            taskMapper.insert(task);
            createdTasks.add(task);
        }

        return createdTasks;
    }
}
