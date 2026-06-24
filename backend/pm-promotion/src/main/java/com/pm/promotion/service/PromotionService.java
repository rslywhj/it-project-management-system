package com.pm.promotion.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pm.auth.annotation.RequirePermission;
import com.pm.common.exception.BusinessException;
import com.pm.common.result.PageResult;
import com.pm.common.result.ResultCode;
import com.pm.common.util.UserContext;
import com.pm.promotion.domain.*;
import com.pm.promotion.dto.*;
import com.pm.promotion.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionUnitMapper unitMapper;
    private final PromotionStageTemplateMapper stageTemplateMapper;
    private final PromotionProgressMapper progressMapper;
    private final UnitRequirementMapper unitRequirementMapper;
    private final ConfigBaselineMapper configBaselineMapper;
    private final UnitConfigDiffMapper unitConfigDiffMapper;

    // ==================== 推广单元管理 ====================

    @Transactional
    @RequirePermission("promotion:create")
    public PromotionUnitVO createUnit(Long projectId, PromotionUnitCreateRequest request) {
        // 检查编码唯一性
        Long exists = unitMapper.selectCount(
                new LambdaQueryWrapper<PromotionUnit>()
                        .eq(PromotionUnit::getProjectId, projectId)
                        .eq(PromotionUnit::getOrgCode, request.getOrgCode()));
        if (exists > 0) {
            throw new BusinessException(1050, "成员单位编码已存在");
        }

        PromotionUnit unit = new PromotionUnit();
        BeanUtils.copyProperties(request, unit);
        unit.setProjectId(projectId);
        unit.setStatus("pending");
        unit.setCompletionRate(BigDecimal.ZERO);
        unitMapper.insert(unit);

        // 如果有阶段模板，自动初始化进度
        initStageProgress(unit.getId(), projectId);

        return toUnitVO(unit, Collections.emptyMap(), Collections.emptyMap());
    }

    @Transactional
    @RequirePermission("promotion:create")
    public List<PromotionUnitVO> batchCreateUnits(Long projectId, BatchCreateUnitsRequest request) {
        List<PromotionUnitVO> result = new ArrayList<>();
        for (PromotionUnitCreateRequest unitReq : request.getUnits()) {
            PromotionUnitVO vo = createUnit(projectId, unitReq);
            result.add(vo);
        }
        return result;
    }

    public PageResult<PromotionUnitVO> listUnits(Long projectId, int page, int size, String status) {
        LambdaQueryWrapper<PromotionUnit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PromotionUnit::getProjectId, projectId)
                .eq(StringUtils.hasText(status), PromotionUnit::getStatus, status)
                .orderByAsc(PromotionUnit::getOrgCode);

        Page<PromotionUnit> result = unitMapper.selectPage(new Page<>(page, size), wrapper);
        List<PromotionUnit> units = result.getRecords();

        // 批量查询阶段模板和进度数据
        Map<Long, String> stageNameMap = batchGetStageNames(units);
        Map<Long, List<PromotionProgress>> progressMap = batchGetProgress(units);

        List<PromotionUnitVO> voList = units.stream()
                .map(u -> toUnitVO(u, stageNameMap, progressMap))
                .collect(Collectors.toList());
        return PageResult.of(voList, result.getTotal(), page, size);
    }

    public PromotionUnitVO getUnit(Long unitId) {
        PromotionUnit unit = unitMapper.selectById(unitId);
        if (unit == null) {
            throw new BusinessException(1051, "推广单元不存在");
        }
        // 单个单元，直接查询
        Map<Long, String> stageNameMap = Collections.emptyMap();
        if (unit.getCurrentStageId() != null) {
            PromotionStageTemplate stage = stageTemplateMapper.selectById(unit.getCurrentStageId());
            if (stage != null) {
                stageNameMap = Map.of(unit.getCurrentStageId(), stage.getName());
            }
        }
        List<PromotionProgress> progressList = progressMapper.selectList(
                new LambdaQueryWrapper<PromotionProgress>()
                        .eq(PromotionProgress::getPromotionUnitId, unitId));
        Map<Long, List<PromotionProgress>> progressMap = Map.of(unitId, progressList);
        return toUnitVO(unit, stageNameMap, progressMap);
    }

    @Transactional
    @RequirePermission("promotion:edit")
    public PromotionUnitVO updateUnit(Long unitId, PromotionUnitCreateRequest request) {
        PromotionUnit unit = unitMapper.selectById(unitId);
        if (unit == null) {
            throw new BusinessException(1051, "推广单元不存在");
        }
        BeanUtils.copyProperties(request, unit, "id", "projectId", "status", "completionRate",
                "currentStageId", "createdAt", "createdBy", "isDeleted");
        unitMapper.updateById(unit);
        return getUnit(unitId);
    }

    @Transactional
    @RequirePermission("promotion:delete")
    public void deleteUnit(Long unitId) {
        unitMapper.deleteById(unitId);
    }

    // ==================== 推广阶段模板管理 ====================

    @Transactional
    @RequirePermission("promotion:manage")
    public PromotionStageTemplateVO createStageTemplate(Long projectId, PromotionStageTemplateRequest request) {
        PromotionStageTemplate template = new PromotionStageTemplate();
        BeanUtils.copyProperties(request, template);
        template.setProjectId(projectId);
        stageTemplateMapper.insert(template);
        return toStageTemplateVO(template);
    }

    public List<PromotionStageTemplateVO> listStageTemplates(Long projectId) {
        // 返回项目专属模板 + 全局模板
        List<PromotionStageTemplate> templates = stageTemplateMapper.selectList(
                new LambdaQueryWrapper<PromotionStageTemplate>()
                        .and(w -> w.eq(PromotionStageTemplate::getProjectId, projectId)
                                .or().isNull(PromotionStageTemplate::getProjectId))
                        .orderByAsc(PromotionStageTemplate::getSortOrder));
        return templates.stream().map(this::toStageTemplateVO).collect(Collectors.toList());
    }

    @Transactional
    @RequirePermission("promotion:manage")
    public PromotionStageTemplateVO updateStageTemplate(Long templateId, PromotionStageTemplateRequest request) {
        PromotionStageTemplate template = stageTemplateMapper.selectById(templateId);
        if (template == null) {
            throw new BusinessException(1052, "阶段模板不存在");
        }
        BeanUtils.copyProperties(request, template, "id", "projectId", "createdAt", "createdBy", "isDeleted");
        stageTemplateMapper.updateById(template);
        return toStageTemplateVO(template);
    }

    @Transactional
    @RequirePermission("promotion:manage")
    public void deleteStageTemplate(Long templateId) {
        stageTemplateMapper.deleteById(templateId);
    }

    @Transactional
    @RequirePermission("promotion:manage")
    public void initDefaultStageTemplates(Long projectId) {
        // 初始化默认推广阶段模板
        String[][] defaults = {
                {"准备", "推广准备阶段，包括环境部署、数据准备等", "1", "15"},
                {"实施", "系统实施阶段，包括配置、定制、培训等", "2", "30"},
                {"试运行", "试运行阶段，验证系统功能和性能", "3", "14"},
                {"正式上线", "正式上线运行", "4", "7"},
                {"验收", "项目验收阶段", "5", "10"}
        };

        for (String[] d : defaults) {
            PromotionStageTemplate template = new PromotionStageTemplate();
            template.setProjectId(projectId);
            template.setName(d[0]);
            template.setDescription(d[1]);
            template.setSortOrder(Integer.parseInt(d[2]));
            template.setEstimatedDays(Integer.parseInt(d[3]));
            template.setIsLocked(0);
            stageTemplateMapper.insert(template);
        }
    }

    // ==================== 推广进度管理 ====================

    @Transactional
    @RequirePermission("promotion:edit")
    public PromotionProgressVO updateProgress(Long unitId, Long stageId, ProgressUpdateRequest request) {
        PromotionProgress progress = progressMapper.selectOne(
                new LambdaQueryWrapper<PromotionProgress>()
                        .eq(PromotionProgress::getPromotionUnitId, unitId)
                        .eq(PromotionProgress::getStageTemplateId, stageId));
        if (progress == null) {
            throw new BusinessException(1053, "推广进度记录不存在");
        }

        progress.setStatus(request.getStatus());
        if (request.getCompletionRate() != null) {
            progress.setCompletionRate(request.getCompletionRate());
        }
        if (request.getRemark() != null) {
            progress.setRemark(request.getRemark());
        }

        // 根据状态更新时间
        if ("in_progress".equals(request.getStatus()) && progress.getStartedAt() == null) {
            progress.setStartedAt(LocalDateTime.now());
        } else if ("completed".equals(request.getStatus())) {
            progress.setCompletedAt(LocalDateTime.now());
            progress.setCompletionRate(BigDecimal.valueOf(100));
        }

        progressMapper.updateById(progress);

        // 更新推广单元整体进度
        updateUnitCompletionRate(unitId);

        // 查询阶段名称并返回VO
        Map<Long, String> stageNameMap = batchGetStageNameMap(List.of(progress.getStageTemplateId()));
        return toProgressVO(progress, stageNameMap);
    }

    public List<PromotionProgressVO> getUnitProgress(Long unitId) {
        List<PromotionProgress> progressList = progressMapper.selectList(
                new LambdaQueryWrapper<PromotionProgress>()
                        .eq(PromotionProgress::getPromotionUnitId, unitId)
                        .orderByAsc(PromotionProgress::getCreatedAt));

        // 批量查询阶段名称
        List<Long> stageIds = progressList.stream()
                .map(PromotionProgress::getStageTemplateId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, String> stageNameMap = batchGetStageNameMap(stageIds);

        return progressList.stream()
                .map(p -> toProgressVO(p, stageNameMap))
                .collect(Collectors.toList());
    }

    // ==================== 差异化需求管理 ====================

    @Transactional
    @RequirePermission("promotion:create")
    public UnitRequirementVO createUnitRequirement(Long unitId, UnitRequirementRequest request) {
        UnitRequirement requirement = new UnitRequirement();
        BeanUtils.copyProperties(request, requirement);
        requirement.setPromotionUnitId(unitId);
        requirement.setStatus("draft");
        unitRequirementMapper.insert(requirement);
        return toUnitRequirementVO(requirement);
    }

    public PageResult<UnitRequirementVO> listUnitRequirements(Long unitId, int page, int size, String type) {
        LambdaQueryWrapper<UnitRequirement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UnitRequirement::getPromotionUnitId, unitId)
                .eq(StringUtils.hasText(type), UnitRequirement::getType, type)
                .orderByDesc(UnitRequirement::getCreatedAt);

        Page<UnitRequirement> result = unitRequirementMapper.selectPage(new Page<>(page, size), wrapper);
        List<UnitRequirementVO> voList = result.getRecords().stream()
                .map(this::toUnitRequirementVO)
                .collect(Collectors.toList());
        return PageResult.of(voList, result.getTotal(), page, size);
    }

    @Transactional
    @RequirePermission("promotion:edit")
    public UnitRequirementVO updateUnitRequirement(Long requirementId, UnitRequirementRequest request) {
        UnitRequirement requirement = unitRequirementMapper.selectById(requirementId);
        if (requirement == null) {
            throw new BusinessException(1054, "差异化需求不存在");
        }
        BeanUtils.copyProperties(request, requirement, "id", "promotionUnitId", "status",
                "createdAt", "createdBy", "isDeleted");
        unitRequirementMapper.updateById(requirement);
        return toUnitRequirementVO(requirement);
    }

    @Transactional
    @RequirePermission("promotion:delete")
    public void deleteUnitRequirement(Long requirementId) {
        unitRequirementMapper.deleteById(requirementId);
    }

    // ==================== 配置基线管理 ====================

    @Transactional
    @RequirePermission("promotion:manage")
    public ConfigBaselineVO createConfigBaseline(Long projectId, ConfigBaselineRequest request) {
        ConfigBaseline baseline = new ConfigBaseline();
        BeanUtils.copyProperties(request, baseline);
        baseline.setProjectId(projectId);
        configBaselineMapper.insert(baseline);
        return toConfigBaselineVO(baseline);
    }

    public List<ConfigBaselineVO> listConfigBaselines(Long projectId) {
        List<ConfigBaseline> baselines = configBaselineMapper.selectList(
                new LambdaQueryWrapper<ConfigBaseline>()
                        .eq(ConfigBaseline::getProjectId, projectId)
                        .orderByAsc(ConfigBaseline::getConfigKey));
        return baselines.stream().map(this::toConfigBaselineVO).collect(Collectors.toList());
    }

    @Transactional
    @RequirePermission("promotion:manage")
    public ConfigBaselineVO updateConfigBaseline(Long baselineId, ConfigBaselineRequest request) {
        ConfigBaseline baseline = configBaselineMapper.selectById(baselineId);
        if (baseline == null) {
            throw new BusinessException(1055, "配置基线不存在");
        }
        BeanUtils.copyProperties(request, baseline, "id", "projectId", "createdAt", "createdBy", "isDeleted");
        configBaselineMapper.updateById(baseline);
        return toConfigBaselineVO(baseline);
    }

    @Transactional
    @RequirePermission("promotion:manage")
    public void deleteConfigBaseline(Long baselineId) {
        configBaselineMapper.deleteById(baselineId);
    }

    // ==================== 配置差异管理 ====================

    @Transactional
    @RequirePermission("promotion:create")
    public UnitConfigDiffVO createConfigDiff(Long unitId, UnitConfigDiffRequest request) {
        UnitConfigDiff diff = new UnitConfigDiff();
        BeanUtils.copyProperties(request, diff);
        diff.setPromotionUnitId(unitId);
        diff.setStatus("pending");
        unitConfigDiffMapper.insert(diff);
        return toConfigDiffVO(diff);
    }

    public List<UnitConfigDiffVO> listConfigDiffs(Long unitId) {
        List<UnitConfigDiff> diffs = unitConfigDiffMapper.selectList(
                new LambdaQueryWrapper<UnitConfigDiff>()
                        .eq(UnitConfigDiff::getPromotionUnitId, unitId)
                        .orderByDesc(UnitConfigDiff::getCreatedAt));
        return diffs.stream().map(this::toConfigDiffVO).collect(Collectors.toList());
    }

    @Transactional
    @RequirePermission("promotion:manage")
    public UnitConfigDiffVO approveConfigDiff(Long diffId, String action) {
        UnitConfigDiff diff = unitConfigDiffMapper.selectById(diffId);
        if (diff == null) {
            throw new BusinessException(1056, "配置差异不存在");
        }
        diff.setStatus("approve".equals(action) ? "approved" : "rejected");
        diff.setApprovedBy(UserContext.getUserId());
        diff.setApprovedAt(LocalDateTime.now());
        unitConfigDiffMapper.updateById(diff);
        return toConfigDiffVO(diff);
    }

    // ==================== 推广看板 ====================

    public PromotionDashboardVO getDashboard(Long projectId) {
        PromotionDashboardVO dashboard = new PromotionDashboardVO();
        dashboard.setProjectId(projectId);

        List<PromotionUnit> units = unitMapper.selectList(
                new LambdaQueryWrapper<PromotionUnit>()
                        .eq(PromotionUnit::getProjectId, projectId));

        dashboard.setTotalUnits(units.size());
        dashboard.setCompletedUnits((int) units.stream().filter(u -> "completed".equals(u.getStatus())).count());
        dashboard.setInProgressUnits((int) units.stream().filter(u -> "in_progress".equals(u.getStatus())).count());
        dashboard.setPendingUnits((int) units.stream().filter(u -> "pending".equals(u.getStatus())).count());

        // 计算整体完成率
        BigDecimal overallRate = units.stream()
                .map(PromotionUnit::getCompletionRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (!units.isEmpty()) {
            dashboard.setOverallCompletionRate(
                    overallRate.divide(BigDecimal.valueOf(units.size()), 2, java.math.RoundingMode.HALF_UP));
        } else {
            dashboard.setOverallCompletionRate(BigDecimal.ZERO);
        }

        // 批量查询阶段名称（避免N+1）
        Map<Long, String> stageNameMap = batchGetStageNames(units);

        // 各单位进度对比
        List<UnitComparisonVO> comparisons = units.stream().map(unit -> {
            UnitComparisonVO comp = new UnitComparisonVO();
            comp.setUnitId(unit.getId());
            comp.setOrgName(unit.getOrgName());
            comp.setCompletionRate(unit.getCompletionRate());
            comp.setStatus(unit.getStatus());
            // 使用预查询的阶段名称
            if (unit.getCurrentStageId() != null) {
                comp.setCurrentStage(stageNameMap.get(unit.getCurrentStageId()));
            }
            // 判断是否延期
            comp.setIsOverdue(unit.getExpectedEndDate() != null
                    && LocalDate.now().isAfter(unit.getExpectedEndDate())
                    && !"completed".equals(unit.getStatus()));
            return comp;
        }).collect(Collectors.toList());
        dashboard.setUnitComparisons(comparisons);

        // 延期预警
        int overdueCount = 0;
        List<OverdueAlertVO> alerts = new ArrayList<>();
        for (UnitComparisonVO comp : comparisons) {
            if (Boolean.TRUE.equals(comp.getIsOverdue())) {
                overdueCount++;
                PromotionUnit unit = units.stream()
                        .filter(u -> u.getId().equals(comp.getUnitId()))
                        .findFirst().orElse(null);
                if (unit != null && unit.getExpectedEndDate() != null) {
                    OverdueAlertVO alert = new OverdueAlertVO();
                    alert.setUnitId(unit.getId());
                    alert.setOrgName(unit.getOrgName());
                    alert.setStageName(comp.getCurrentStage());
                    alert.setExpectedEndDate(unit.getExpectedEndDate());
                    long overdueDays = ChronoUnit.DAYS.between(unit.getExpectedEndDate(), LocalDate.now());
                    alert.setOverdueDays((int) overdueDays);
                    alert.setAlertLevel(overdueDays > 30 ? "critical" : "warning");
                    alerts.add(alert);
                }
            }
        }
        dashboard.setOverdueUnits(overdueCount);
        dashboard.setOverdueAlerts(alerts);

        return dashboard;
    }

    // ==================== 批量操作 ====================

    @Transactional
    @RequirePermission("promotion:manage")
    public void batchCreateTasks(Long projectId, BatchCreateTasksRequest request) {
        List<PromotionUnit> units = unitMapper.selectList(
                new LambdaQueryWrapper<PromotionUnit>()
                        .in(PromotionUnit::getId, request.getUnitIds()));

        for (PromotionUnit unit : units) {
            for (BatchCreateTasksRequest.TaskTemplateItem template : request.getTaskTemplates()) {
                // 创建任务的逻辑（需要调用 task 模块）
                // 这里记录日志，实际实现需要注入 TaskService
                log.info("为推广单元 {} 创建任务: {}", unit.getOrgName(), template.getTitle());
            }
        }
    }

    // ==================== 内部方法 ====================

    /**
     * 批量获取阶段名称映射（避免N+1查询）
     * @return stageId -> stageName 的映射
     */
    private Map<Long, String> batchGetStageNames(List<PromotionUnit> units) {
        List<Long> stageIds = units.stream()
                .map(PromotionUnit::getCurrentStageId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        return batchGetStageNameMap(stageIds);
    }

    /**
     * 批量获取阶段名称映射
     * @param stageIds 阶段模板ID列表
     * @return stageId -> stageName 的映射
     */
    private Map<Long, String> batchGetStageNameMap(List<Long> stageIds) {
        if (stageIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<PromotionStageTemplate> templates = stageTemplateMapper.selectList(
                new LambdaQueryWrapper<PromotionStageTemplate>()
                        .in(PromotionStageTemplate::getId, stageIds));
        return templates.stream()
                .collect(Collectors.toMap(
                        PromotionStageTemplate::getId,
                        PromotionStageTemplate::getName,
                        (v1, v2) -> v1));
    }

    /**
     * 批量获取推广单元的进度数据（避免N+1查询）
     * @return unitId -> progressList 的映射
     */
    private Map<Long, List<PromotionProgress>> batchGetProgress(List<PromotionUnit> units) {
        List<Long> unitIds = units.stream()
                .map(PromotionUnit::getId)
                .collect(Collectors.toList());
        if (unitIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<PromotionProgress> allProgress = progressMapper.selectList(
                new LambdaQueryWrapper<PromotionProgress>()
                        .in(PromotionProgress::getPromotionUnitId, unitIds));
        return allProgress.stream()
                .collect(Collectors.groupingBy(PromotionProgress::getPromotionUnitId));
    }

    private void initStageProgress(Long unitId, Long projectId) {
        List<PromotionStageTemplate> templates = stageTemplateMapper.selectList(
                new LambdaQueryWrapper<PromotionStageTemplate>()
                        .and(w -> w.eq(PromotionStageTemplate::getProjectId, projectId)
                                .or().isNull(PromotionStageTemplate::getProjectId))
                        .orderByAsc(PromotionStageTemplate::getSortOrder));

        for (PromotionStageTemplate template : templates) {
            PromotionProgress progress = new PromotionProgress();
            progress.setPromotionUnitId(unitId);
            progress.setStageTemplateId(template.getId());
            progress.setStatus("pending");
            progress.setCompletionRate(BigDecimal.ZERO);
            progressMapper.insert(progress);
        }
    }

    private void updateUnitCompletionRate(Long unitId) {
        List<PromotionProgress> progressList = progressMapper.selectList(
                new LambdaQueryWrapper<PromotionProgress>()
                        .eq(PromotionProgress::getPromotionUnitId, unitId));

        if (progressList.isEmpty()) return;

        BigDecimal totalRate = progressList.stream()
                .map(PromotionProgress::getCompletionRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avgRate = totalRate.divide(BigDecimal.valueOf(progressList.size()), 2, java.math.RoundingMode.HALF_UP);

        PromotionUnit unit = unitMapper.selectById(unitId);
        if (unit != null) {
            unit.setCompletionRate(avgRate);

            // 更新当前阶段
            progressList.stream()
                    .filter(p -> "in_progress".equals(p.getStatus()))
                    .findFirst()
                    .ifPresent(p -> unit.setCurrentStageId(p.getStageTemplateId()));

            // 检查是否全部完成
            boolean allCompleted = progressList.stream()
                    .allMatch(p -> "completed".equals(p.getStatus()) || "skipped".equals(p.getStatus()));
            if (allCompleted) {
                unit.setStatus("completed");
                unit.setActualEndDate(LocalDate.now());
            } else if (avgRate.compareTo(BigDecimal.ZERO) > 0) {
                unit.setStatus("in_progress");
                if (unit.getActualStartDate() == null) {
                    unit.setActualStartDate(LocalDate.now());
                }
            }

            unitMapper.updateById(unit);
        }
    }

    // ==================== VO 转换 ====================

    /**
     * 转换为VO（使用预查询的数据，避免N+1）
     * @param stageNameMap 阶段ID->名称映射
     * @param progressMap 单元ID->进度列表映射
     */
    private PromotionUnitVO toUnitVO(PromotionUnit unit,
                                      Map<Long, String> stageNameMap,
                                      Map<Long, List<PromotionProgress>> progressMap) {
        PromotionUnitVO vo = new PromotionUnitVO();
        BeanUtils.copyProperties(unit, vo);

        // 使用预查询的阶段名称
        if (unit.getCurrentStageId() != null) {
            vo.setCurrentStageName(stageNameMap.get(unit.getCurrentStageId()));
        }

        // 使用预查询的进度数据
        List<PromotionProgress> progressList = progressMap.getOrDefault(unit.getId(), Collections.emptyList());

        // 批量查询进度对应的阶段名称
        List<Long> stageIds = progressList.stream()
                .map(PromotionProgress::getStageTemplateId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, String> progressStageNameMap = batchGetStageNameMap(stageIds);

        vo.setStageProgress(progressList.stream()
                .map(p -> toProgressVO(p, progressStageNameMap))
                .collect(Collectors.toList()));

        return vo;
    }

    private PromotionStageTemplateVO toStageTemplateVO(PromotionStageTemplate template) {
        PromotionStageTemplateVO vo = new PromotionStageTemplateVO();
        BeanUtils.copyProperties(template, vo);
        return vo;
    }

    /**
     * 转换进度VO（使用预查询的阶段名称）
     */
    private PromotionProgressVO toProgressVO(PromotionProgress progress, Map<Long, String> stageNameMap) {
        PromotionProgressVO vo = new PromotionProgressVO();
        BeanUtils.copyProperties(progress, vo);
        vo.setStageName(stageNameMap.get(progress.getStageTemplateId()));
        return vo;
    }

    private UnitRequirementVO toUnitRequirementVO(UnitRequirement requirement) {
        UnitRequirementVO vo = new UnitRequirementVO();
        BeanUtils.copyProperties(requirement, vo);
        return vo;
    }

    private ConfigBaselineVO toConfigBaselineVO(ConfigBaseline baseline) {
        ConfigBaselineVO vo = new ConfigBaselineVO();
        BeanUtils.copyProperties(baseline, vo);
        return vo;
    }

    private UnitConfigDiffVO toConfigDiffVO(UnitConfigDiff diff) {
        UnitConfigDiffVO vo = new UnitConfigDiffVO();
        BeanUtils.copyProperties(diff, vo);

        // 获取配置基线信息
        ConfigBaseline baseline = configBaselineMapper.selectById(diff.getConfigBaselineId());
        if (baseline != null) {
            vo.setConfigKey(baseline.getConfigKey());
            vo.setStandardValue(baseline.getConfigValue());
        }

        return vo;
    }
}
