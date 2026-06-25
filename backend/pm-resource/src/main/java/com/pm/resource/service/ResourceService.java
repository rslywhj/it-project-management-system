package com.pm.resource.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pm.auth.annotation.RequirePermission;
import com.pm.common.exception.BusinessException;
import com.pm.common.result.PageResult;
import com.pm.common.util.UserContext;
import com.pm.common.entity.SysUser;
import com.pm.common.mapper.SysUserMapper;
import com.pm.resource.domain.Resource;
import com.pm.resource.domain.ResourceAllocation;
import com.pm.resource.domain.Timesheet;
import com.pm.resource.domain.WorkLog;
import com.pm.resource.dto.*;
import com.pm.resource.mapper.ResourceAllocationMapper;
import com.pm.resource.mapper.ResourceMapper;
import com.pm.resource.mapper.TimesheetMapper;
import com.pm.resource.mapper.WorkLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceMapper resourceMapper;
    private final WorkLogMapper workLogMapper;
    private final ResourceAllocationMapper allocationMapper;
    private final TimesheetMapper timesheetMapper;
    private final SysUserMapper sysUserMapper;

    // ==================== 资源池管理 ====================

    @Transactional
    @RequirePermission("resource:create")
    public ResourceVO addResource(Long projectId, ResourceRequest request) {
        // 检查是否已存在
        Long exists = resourceMapper.selectCount(
                new LambdaQueryWrapper<Resource>()
                        .eq(Resource::getUserId, request.getUserId())
                        .eq(Resource::getProjectId, projectId));
        if (exists > 0) {
            throw new BusinessException(1100, "该用户已在资源池中");
        }

        Resource resource = new Resource();
        BeanUtils.copyProperties(request, resource);
        resource.setProjectId(projectId);
        resource.setWorkloadPercent(0);
        if (resource.getAvailability() == null) {
            resource.setAvailability("available");
        }
        if (resource.getCapacityHoursPerWeek() == null) {
            resource.setCapacityHoursPerWeek(BigDecimal.valueOf(40));
        }
        resourceMapper.insert(resource);
        return toResourceVO(resource);
    }

    @RequirePermission("resource:view")
    public PageResult<ResourceVO> listResources(Long projectId, int page, int size, String availability) {
        LambdaQueryWrapper<Resource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Resource::getProjectId, projectId)
                .eq(StringUtils.hasText(availability), Resource::getAvailability, availability)
                .orderByDesc(Resource::getCreatedAt);

        Page<Resource> result = resourceMapper.selectPage(new Page<>(page, size), wrapper);
        List<ResourceVO> voList = result.getRecords().stream()
                .map(this::toResourceVO)
                .collect(Collectors.toList());
        return PageResult.of(voList, result.getTotal(), page, size);
    }

    @RequirePermission("resource:view")
    public ResourceVO getResource(Long resourceId) {
        Resource resource = resourceMapper.selectById(resourceId);
        if (resource == null) {
            throw new BusinessException(1101, "资源不存在");
        }
        return toResourceVO(resource);
    }

    @Transactional
    @RequirePermission("resource:edit")
    public ResourceVO updateResource(Long resourceId, ResourceRequest request) {
        Resource resource = resourceMapper.selectById(resourceId);
        if (resource == null) {
            throw new BusinessException(1101, "资源不存在");
        }
        BeanUtils.copyProperties(request, resource, "id", "projectId", "userId", "workloadPercent",
                "createdAt", "createdBy", "isDeleted");
        resourceMapper.updateById(resource);
        return toResourceVO(resource);
    }

    @Transactional
    @RequirePermission("resource:delete")
    public void removeResource(Long resourceId) {
        resourceMapper.deleteById(resourceId);
    }

    // ==================== 工时填报 ====================

    @Transactional
    @RequirePermission("resource:create")
    public WorkLogVO logWork(Long projectId, WorkLogRequest request) {
        WorkLog workLog = new WorkLog();
        BeanUtils.copyProperties(request, workLog);
        workLog.setProjectId(projectId);
        workLog.setUserId(UserContext.getUserId());
        workLogMapper.insert(workLog);

        // 更新资源负载
        updateResourceWorkload(projectId, UserContext.getUserId());

        return toWorkLogVO(workLog);
    }

    @RequirePermission("resource:view")
    public PageResult<WorkLogVO> listWorkLogs(Long projectId, int page, int size,
                                               Long userId, Long taskId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<WorkLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkLog::getProjectId, projectId)
                .eq(userId != null, WorkLog::getUserId, userId)
                .eq(taskId != null, WorkLog::getTaskId, taskId)
                .ge(startDate != null, WorkLog::getWorkDate, startDate)
                .le(endDate != null, WorkLog::getWorkDate, endDate)
                .orderByDesc(WorkLog::getWorkDate);

        Page<WorkLog> result = workLogMapper.selectPage(new Page<>(page, size), wrapper);
        List<WorkLogVO> voList = result.getRecords().stream()
                .map(this::toWorkLogVO)
                .collect(Collectors.toList());
        return PageResult.of(voList, result.getTotal(), page, size);
    }

    @Transactional
    @RequirePermission("resource:edit")
    public WorkLogVO updateWorkLog(Long workLogId, WorkLogRequest request) {
        WorkLog workLog = workLogMapper.selectById(workLogId);
        if (workLog == null) {
            throw new BusinessException(1102, "工时记录不存在");
        }
        BeanUtils.copyProperties(request, workLog, "id", "projectId", "userId",
                "createdAt", "createdBy", "isDeleted");
        workLogMapper.updateById(workLog);
        return toWorkLogVO(workLog);
    }

    @Transactional
    @RequirePermission("resource:delete")
    public void deleteWorkLog(Long workLogId) {
        workLogMapper.deleteById(workLogId);
    }

    // ==================== 资源分配管理 ====================

    @Transactional
    @RequirePermission("resource:create")
    public AllocationVO createAllocation(Long projectId, AllocationRequest request) {
        ResourceAllocation allocation = new ResourceAllocation();
        BeanUtils.copyProperties(request, allocation);
        allocation.setProjectId(projectId);
        allocation.setStatus("planned");
        allocationMapper.insert(allocation);
        return toAllocationVO(allocation);
    }

    @RequirePermission("resource:view")
    public PageResult<AllocationVO> listAllocations(Long projectId, int page, int size,
                                                     Long userId, String status) {
        LambdaQueryWrapper<ResourceAllocation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ResourceAllocation::getProjectId, projectId)
                .eq(userId != null, ResourceAllocation::getUserId, userId)
                .eq(StringUtils.hasText(status), ResourceAllocation::getStatus, status)
                .orderByDesc(ResourceAllocation::getCreatedAt);

        Page<ResourceAllocation> result = allocationMapper.selectPage(new Page<>(page, size), wrapper);
        List<AllocationVO> voList = result.getRecords().stream()
                .map(this::toAllocationVO)
                .collect(Collectors.toList());
        return PageResult.of(voList, result.getTotal(), page, size);
    }

    @Transactional
    @RequirePermission("resource:edit")
    public AllocationVO updateAllocation(Long allocationId, AllocationRequest request) {
        ResourceAllocation allocation = allocationMapper.selectById(allocationId);
        if (allocation == null) {
            throw new BusinessException(1103, "资源分配不存在");
        }
        BeanUtils.copyProperties(request, allocation, "id", "projectId", "status",
                "createdAt", "createdBy", "isDeleted");
        allocationMapper.updateById(allocation);
        return toAllocationVO(allocation);
    }

    @Transactional
    @RequirePermission("resource:delete")
    public void deleteAllocation(Long allocationId) {
        allocationMapper.deleteById(allocationId);
    }

    // ==================== 工时记录（Timesheet） ====================

    @Transactional
    @RequirePermission("resource:create")
    public TimesheetVO createTimesheet(Long projectId, TimesheetRequest request) {
        Timesheet timesheet = new Timesheet();
        BeanUtils.copyProperties(request, timesheet);
        timesheet.setProjectId(projectId);
        timesheet.setUserId(UserContext.getUserId());
        timesheet.setStatus("draft");
        timesheetMapper.insert(timesheet);
        return toTimesheetVO(timesheet);
    }

    @RequirePermission("resource:view")
    public PageResult<TimesheetVO> listTimesheets(Long projectId, int page, int size,
                                                   Long userId, LocalDate startDate, LocalDate endDate, String status) {
        LambdaQueryWrapper<Timesheet> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Timesheet::getProjectId, projectId)
                .eq(userId != null, Timesheet::getUserId, userId)
                .ge(startDate != null, Timesheet::getWorkDate, startDate)
                .le(endDate != null, Timesheet::getWorkDate, endDate)
                .eq(StringUtils.hasText(status), Timesheet::getStatus, status)
                .orderByDesc(Timesheet::getWorkDate);

        Page<Timesheet> result = timesheetMapper.selectPage(new Page<>(page, size), wrapper);
        List<TimesheetVO> voList = result.getRecords().stream()
                .map(this::toTimesheetVO)
                .collect(Collectors.toList());
        return PageResult.of(voList, result.getTotal(), page, size);
    }

    @Transactional
    @RequirePermission("resource:edit")
    public TimesheetVO updateTimesheet(Long timesheetId, TimesheetRequest request) {
        Timesheet timesheet = timesheetMapper.selectById(timesheetId);
        if (timesheet == null) {
            throw new BusinessException(1104, "工时记录不存在");
        }
        if (!"draft".equals(timesheet.getStatus()) && !"rejected".equals(timesheet.getStatus())) {
            throw new BusinessException(1105, "只有草稿或被驳回的记录可以编辑");
        }
        BeanUtils.copyProperties(request, timesheet, "id", "projectId", "userId", "status",
                "approvedBy", "approvedAt", "rejectReason", "createdAt", "createdBy", "isDeleted");
        timesheetMapper.updateById(timesheet);
        return toTimesheetVO(timesheet);
    }

    @Transactional
    @RequirePermission("resource:delete")
    public void deleteTimesheet(Long timesheetId) {
        Timesheet timesheet = timesheetMapper.selectById(timesheetId);
        if (timesheet == null) {
            throw new BusinessException(1104, "工时记录不存在");
        }
        if (!"draft".equals(timesheet.getStatus()) && !"rejected".equals(timesheet.getStatus())) {
            throw new BusinessException(1105, "只有草稿或被驳回的记录可以删除");
        }
        timesheetMapper.deleteById(timesheetId);
    }

    @Transactional
    @RequirePermission("resource:edit")
    public TimesheetVO submitTimesheet(Long timesheetId) {
        Timesheet timesheet = timesheetMapper.selectById(timesheetId);
        if (timesheet == null) {
            throw new BusinessException(1104, "工时记录不存在");
        }
        if (!"draft".equals(timesheet.getStatus()) && !"rejected".equals(timesheet.getStatus())) {
            throw new BusinessException(1105, "只有草稿或被驳回的记录可以提交");
        }
        timesheet.setStatus("submitted");
        timesheetMapper.updateById(timesheet);
        return toTimesheetVO(timesheet);
    }

    @Transactional
    @RequirePermission("resource:manage")
    public TimesheetVO approveTimesheet(Long timesheetId) {
        Timesheet timesheet = timesheetMapper.selectById(timesheetId);
        if (timesheet == null) {
            throw new BusinessException(1104, "工时记录不存在");
        }
        if (!"submitted".equals(timesheet.getStatus())) {
            throw new BusinessException(1106, "只有已提交的记录可以审批");
        }
        timesheet.setStatus("approved");
        timesheet.setApprovedBy(UserContext.getUserId());
        timesheet.setApprovedAt(LocalDateTime.now());
        timesheetMapper.updateById(timesheet);
        return toTimesheetVO(timesheet);
    }

    @Transactional
    @RequirePermission("resource:manage")
    public TimesheetVO rejectTimesheet(Long timesheetId, String reason) {
        Timesheet timesheet = timesheetMapper.selectById(timesheetId);
        if (timesheet == null) {
            throw new BusinessException(1104, "工时记录不存在");
        }
        if (!"submitted".equals(timesheet.getStatus())) {
            throw new BusinessException(1106, "只有已提交的记录可以驳回");
        }
        timesheet.setStatus("rejected");
        timesheet.setApprovedBy(UserContext.getUserId());
        timesheet.setApprovedAt(LocalDateTime.now());
        timesheet.setRejectReason(reason);
        timesheetMapper.updateById(timesheet);
        return toTimesheetVO(timesheet);
    }

    // ==================== 资源利用率 ====================

    @RequirePermission("resource:view")
    public List<ResourceUtilizationVO> getResourceUtilization(Long projectId) {
        // 获取项目所有资源
        List<Resource> resources = resourceMapper.selectList(
                new LambdaQueryWrapper<Resource>()
                        .eq(Resource::getProjectId, projectId));

        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);

        return resources.stream().map(resource -> {
            ResourceUtilizationVO vo = new ResourceUtilizationVO();
            vo.setUserId(resource.getUserId());
            vo.setUserName(getUserName(resource.getUserId()));
            vo.setTotalAllocation(resource.getWorkloadPercent());

            // 活跃项目数
            Long activeProjects = allocationMapper.selectCount(
                    new LambdaQueryWrapper<ResourceAllocation>()
                            .eq(ResourceAllocation::getUserId, resource.getUserId())
                            .eq(ResourceAllocation::getStatus, "active"));
            vo.setActiveProjects(activeProjects.intValue());

            // 本月工时
            BigDecimal monthHours = workLogMapper.selectList(
                    new LambdaQueryWrapper<WorkLog>()
                            .eq(WorkLog::getProjectId, projectId)
                            .eq(WorkLog::getUserId, resource.getUserId())
                            .ge(WorkLog::getWorkDate, monthStart)
                            .le(WorkLog::getWorkDate, LocalDate.now())).stream()
                    .map(WorkLog::getHours)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            vo.setTotalHoursThisMonth(monthHours);

            // 利用率 = 本月工时 / (每周可用工时 * 4周) * 100
            if (resource.getCapacityHoursPerWeek() != null && resource.getCapacityHoursPerWeek().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal monthlyCapacity = resource.getCapacityHoursPerWeek().multiply(BigDecimal.valueOf(4));
                BigDecimal utilization = monthHours.multiply(BigDecimal.valueOf(100))
                        .divide(monthlyCapacity, 2, RoundingMode.HALF_UP);
                vo.setUtilizationRate(utilization);
            } else {
                vo.setUtilizationRate(BigDecimal.ZERO);
            }

            return vo;
        }).collect(Collectors.toList());
    }

    // ==================== 资源负载分析 ====================

    @RequirePermission("resource:view")
    public WorkloadReportVO getWorkloadReport(Long projectId) {
        WorkloadReportVO report = new WorkloadReportVO();
        report.setProjectId(projectId);

        List<Resource> resources = resourceMapper.selectList(
                new LambdaQueryWrapper<Resource>()
                        .eq(Resource::getProjectId, projectId));

        report.setTotalResources(resources.size());
        report.setAvailableResources((int) resources.stream()
                .filter(r -> "available".equals(r.getAvailability())).count());
        report.setOverloadedResources((int) resources.stream()
                .filter(r -> r.getWorkloadPercent() != null && r.getWorkloadPercent() > 80).count());

        // 资源负载详情
        LocalDate weekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);

        List<WorkloadReportVO.ResourceWorkload> workloads = resources.stream().map(resource -> {
            WorkloadReportVO.ResourceWorkload workload = new WorkloadReportVO.ResourceWorkload();
            workload.setUserId(resource.getUserId());
            workload.setWorkloadPercent(resource.getWorkloadPercent());
            workload.setAvailability(resource.getAvailability());

            // 本周工时
            BigDecimal weekHours = workLogMapper.selectList(
                    new LambdaQueryWrapper<WorkLog>()
                            .eq(WorkLog::getProjectId, projectId)
                            .eq(WorkLog::getUserId, resource.getUserId())
                            .ge(WorkLog::getWorkDate, weekStart)
                            .le(WorkLog::getWorkDate, LocalDate.now())).stream()
                    .map(WorkLog::getHours)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            workload.setTotalHoursThisWeek(weekHours);

            // 本月工时
            BigDecimal monthHours = workLogMapper.selectList(
                    new LambdaQueryWrapper<WorkLog>()
                            .eq(WorkLog::getProjectId, projectId)
                            .eq(WorkLog::getUserId, resource.getUserId())
                            .ge(WorkLog::getWorkDate, monthStart)
                            .le(WorkLog::getWorkDate, LocalDate.now())).stream()
                    .map(WorkLog::getHours)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            workload.setTotalHoursThisMonth(monthHours);

            return workload;
        }).collect(Collectors.toList());
        report.setResourceWorkloads(workloads);

        // 工时汇总
        WorkloadReportVO.WorkHoursSummary summary = new WorkloadReportVO.WorkHoursSummary();
        summary.setTotalHoursThisWeek(workloads.stream()
                .map(WorkloadReportVO.ResourceWorkload::getTotalHoursThisWeek)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        summary.setTotalHoursThisMonth(workloads.stream()
                .map(WorkloadReportVO.ResourceWorkload::getTotalHoursThisMonth)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        if (!resources.isEmpty()) {
            summary.setAverageHoursPerPerson(summary.getTotalHoursThisMonth()
                    .divide(BigDecimal.valueOf(resources.size()), 2, RoundingMode.HALF_UP));
        }
        report.setWorkHoursSummary(summary);

        return report;
    }

    // ==================== 内部方法 ====================

    private void updateResourceWorkload(Long projectId, Long userId) {
        Resource resource = resourceMapper.selectOne(
                new LambdaQueryWrapper<Resource>()
                        .eq(Resource::getProjectId, projectId)
                        .eq(Resource::getUserId, userId));
        if (resource == null) return;

        // 计算本周工时占比
        LocalDate weekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        BigDecimal weekHours = workLogMapper.selectList(
                new LambdaQueryWrapper<WorkLog>()
                        .eq(WorkLog::getProjectId, projectId)
                        .eq(WorkLog::getUserId, userId)
                        .ge(WorkLog::getWorkDate, weekStart)
                        .le(WorkLog::getWorkDate, LocalDate.now())).stream()
                .map(WorkLog::getHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int workloadPercent = weekHours.multiply(BigDecimal.valueOf(100))
                .divide(resource.getCapacityHoursPerWeek(), 0, RoundingMode.HALF_UP)
                .intValue();

        resource.setWorkloadPercent(Math.min(100, workloadPercent));
        resourceMapper.updateById(resource);
    }

    private ResourceVO toResourceVO(Resource resource) {
        ResourceVO vo = new ResourceVO();
        BeanUtils.copyProperties(resource, vo);
        return vo;
    }

    private WorkLogVO toWorkLogVO(WorkLog workLog) {
        WorkLogVO vo = new WorkLogVO();
        BeanUtils.copyProperties(workLog, vo);
        return vo;
    }

    private AllocationVO toAllocationVO(ResourceAllocation allocation) {
        AllocationVO vo = new AllocationVO();
        BeanUtils.copyProperties(allocation, vo);
        vo.setUserName(getUserName(allocation.getUserId()));
        return vo;
    }

    private TimesheetVO toTimesheetVO(Timesheet timesheet) {
        TimesheetVO vo = new TimesheetVO();
        BeanUtils.copyProperties(timesheet, vo);
        vo.setUserName(getUserName(timesheet.getUserId()));
        return vo;
    }

    private String getUserName(Long userId) {
        if (userId == null) return null;
        SysUser user = sysUserMapper.selectById(userId);
        return user != null ? user.getRealName() : null;
    }
}
