package com.pm.task.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pm.auth.annotation.RequirePermission;
import com.pm.common.exception.BusinessException;
import com.pm.common.result.PageResult;
import com.pm.common.result.ResultCode;
import com.pm.task.domain.Task;
import com.pm.task.domain.TaskDependency;
import com.pm.task.dto.*;
import com.pm.task.enums.TaskStatus;
import com.pm.task.mapper.TaskDependencyMapper;
import com.pm.task.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskMapper taskMapper;
    private final TaskDependencyMapper dependencyMapper;

    @Transactional
    @RequirePermission("task:create")
    public TaskVO createTask(Long projectId, TaskCreateRequest request) {
        Task task = new Task();
        BeanUtils.copyProperties(request, task);
        task.setProjectId(projectId);
        task.setStatus(TaskStatus.TODO.getCode());
        task.setCompletionRate(BigDecimal.ZERO);

        // 生成 WBS 编码
        task.setWbsCode(generateWbsCode(projectId, request.getParentTaskId()));

        taskMapper.insert(task);
        return toVO(task);
    }

    public PageResult<TaskVO> listTasks(Long projectId, int page, int size,
                                         String keyword, String status, Long assignedTo) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getProjectId, projectId)
                .like(StringUtils.hasText(keyword), Task::getTitle, keyword)
                .eq(StringUtils.hasText(status), Task::getStatus, status)
                .eq(assignedTo != null, Task::getAssignedTo, assignedTo)
                .orderByAsc(Task::getWbsCode);

        Page<Task> result = taskMapper.selectPage(new Page<>(page, size), wrapper);
        List<TaskVO> voList = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        return PageResult.of(voList, result.getTotal(), page, size);
    }

    public TaskVO getTask(Long taskId) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }
        return toVO(task);
    }

    @Transactional
    @RequirePermission("task:update")
    public TaskVO updateTask(Long taskId, TaskCreateRequest request) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }
        BeanUtils.copyProperties(request, task, "id", "projectId", "status", "completionRate",
                "wbsCode", "createdAt", "createdBy");
        taskMapper.updateById(task);
        return toVO(task);
    }

    @Transactional
    @RequirePermission("task:update")
    public TaskVO updateProgress(Long taskId, ProgressUpdateRequest request) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }

        task.setCompletionRate(request.getCompletionRate());

        // 自动更新状态
        if (request.getCompletionRate().compareTo(BigDecimal.valueOf(100)) >= 0) {
            task.setStatus(TaskStatus.DONE.getCode());
            task.setActualEnd(LocalDate.now());
        } else if (request.getCompletionRate().compareTo(BigDecimal.ZERO) > 0) {
            task.setStatus(TaskStatus.IN_PROGRESS.getCode());
            if (task.getActualStart() == null) {
                task.setActualStart(LocalDate.now());
            }
        }

        taskMapper.updateById(task);

        // 如果有父任务，更新父任务进度
        if (task.getParentTaskId() != null) {
            updateParentTaskProgress(task.getParentTaskId());
        }

        return toVO(task);
    }

    @Transactional
    @RequirePermission("task:delete")
    public void deleteTask(Long taskId) {
        // 删除相关依赖
        dependencyMapper.delete(
                new LambdaQueryWrapper<TaskDependency>()
                        .eq(TaskDependency::getTaskId, taskId)
                        .or()
                        .eq(TaskDependency::getDependsOnTaskId, taskId));
        taskMapper.deleteById(taskId);
    }

    /**
     * 创建子任务（WBS 分解）
     */
    @Transactional
    @RequirePermission("task:create")
    public TaskVO createSubtask(Long parentTaskId, TaskCreateRequest request) {
        Task parentTask = taskMapper.selectById(parentTaskId);
        if (parentTask == null) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }
        request.setParentTaskId(parentTaskId);
        return createTask(parentTask.getProjectId(), request);
    }

    /**
     * 获取子任务树
     */
    public List<TaskVO> getSubtasks(Long parentTaskId) {
        List<Task> tasks = taskMapper.selectList(
                new LambdaQueryWrapper<Task>()
                        .eq(Task::getParentTaskId, parentTaskId)
                        .orderByAsc(Task::getWbsCode));
        return tasks.stream().map(this::toVO).collect(Collectors.toList());
    }

    /**
     * 获取 WBS 完整树结构
     */
    public List<TaskVO> getWbsTree(Long projectId) {
        List<Task> allTasks = taskMapper.selectList(
                new LambdaQueryWrapper<Task>()
                        .eq(Task::getProjectId, projectId)
                        .orderByAsc(Task::getWbsCode));

        // 获取所有依赖关系（使用已查询的任务ID列表，避免 SQL 拼接）
        List<Long> taskIds = allTasks.stream().map(Task::getId).collect(Collectors.toList());
        List<TaskDependency> allDeps = taskIds.isEmpty()
                ? Collections.emptyList()
                : dependencyMapper.selectList(
                        new LambdaQueryWrapper<TaskDependency>()
                                .in(TaskDependency::getTaskId, taskIds));
        Map<Long, List<Long>> depMap = allDeps.stream()
                .collect(Collectors.groupingBy(TaskDependency::getTaskId,
                        Collectors.mapping(TaskDependency::getDependsOnTaskId, Collectors.toList())));

        // 构建树
        Map<Long, List<Task>> parentMap = allTasks.stream()
                .filter(t -> t.getParentTaskId() != null)
                .collect(Collectors.groupingBy(Task::getParentTaskId));

        return allTasks.stream()
                .filter(t -> t.getParentTaskId() == null) // 顶级任务
                .map(t -> buildTree(t, parentMap, depMap))
                .collect(Collectors.toList());
    }

    /**
     * 添加任务依赖
     */
    @Transactional
    @RequirePermission("task:update")
    public void addDependency(Long taskId, DependencyRequest request) {
        // 检查循环依赖
        if (wouldCreateCycle(taskId, request.getDependsOnTaskId())) {
            throw new BusinessException(ResultCode.TASK_DEPENDENCY_CYCLE);
        }

        TaskDependency dep = new TaskDependency();
        dep.setTaskId(taskId);
        dep.setDependsOnTaskId(request.getDependsOnTaskId());
        dep.setDependencyType(request.getDependencyType() != null ? request.getDependencyType() : "FS");
        dependencyMapper.insert(dep);
    }

    private TaskVO buildTree(Task task, Map<Long, List<Task>> parentMap,
                              Map<Long, List<Long>> depMap) {
        TaskVO vo = toVO(task);
        vo.setDependsOnTaskIds(depMap.getOrDefault(task.getId(), List.of()));

        List<Task> children = parentMap.getOrDefault(task.getId(), List.of());
        if (!children.isEmpty()) {
            vo.setChildren(children.stream()
                    .map(c -> buildTree(c, parentMap, depMap))
                    .collect(Collectors.toList()));
        }
        return vo;
    }

    private void updateParentTaskProgress(Long parentTaskId) {
        List<Task> children = taskMapper.selectList(
                new LambdaQueryWrapper<Task>().eq(Task::getParentTaskId, parentTaskId));
        if (children.isEmpty()) return;

        BigDecimal totalRate = children.stream()
                .map(Task::getCompletionRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avgRate = totalRate.divide(BigDecimal.valueOf(children.size()), 2, BigDecimal.ROUND_HALF_UP);

        Task parent = taskMapper.selectById(parentTaskId);
        if (parent != null) {
            parent.setCompletionRate(avgRate);
            if (avgRate.compareTo(BigDecimal.valueOf(100)) >= 0) {
                parent.setStatus(TaskStatus.DONE.getCode());
            } else if (avgRate.compareTo(BigDecimal.ZERO) > 0) {
                parent.setStatus(TaskStatus.IN_PROGRESS.getCode());
            }
            taskMapper.updateById(parent);

            // 递归更新上级任务
            if (parent.getParentTaskId() != null) {
                updateParentTaskProgress(parent.getParentTaskId());
            }
        }
    }

    private boolean wouldCreateCycle(Long taskId, Long dependsOnTaskId) {
        // 简单实现：检查 dependsOnTaskId 是否直接或间接依赖 taskId
        if (taskId.equals(dependsOnTaskId)) return true;

        List<Long> visited = new ArrayList<>();
        return checkCycle(dependsOnTaskId, taskId, visited);
    }

    private boolean checkCycle(Long current, Long target, List<Long> visited) {
        if (current.equals(target)) return true;
        if (visited.contains(current)) return false;
        visited.add(current);

        List<TaskDependency> deps = dependencyMapper.selectList(
                new LambdaQueryWrapper<TaskDependency>().eq(TaskDependency::getTaskId, current));
        for (TaskDependency dep : deps) {
            if (checkCycle(dep.getDependsOnTaskId(), target, visited)) {
                return true;
            }
        }
        return false;
    }

    private String generateWbsCode(Long projectId, Long parentTaskId) {
        if (parentTaskId == null) {
            // 顶级任务
            Long count = taskMapper.selectCount(
                    new LambdaQueryWrapper<Task>()
                            .eq(Task::getProjectId, projectId)
                            .isNull(Task::getParentTaskId));
            return String.valueOf(count + 1);
        } else {
            Task parent = taskMapper.selectById(parentTaskId);
            Long siblingCount = taskMapper.selectCount(
                    new LambdaQueryWrapper<Task>().eq(Task::getParentTaskId, parentTaskId));
            return parent.getWbsCode() + "." + (siblingCount + 1);
        }
    }

    private TaskVO toVO(Task task) {
        TaskVO vo = new TaskVO();
        BeanUtils.copyProperties(task, vo);
        // 获取依赖
        List<TaskDependency> deps = dependencyMapper.selectList(
                new LambdaQueryWrapper<TaskDependency>().eq(TaskDependency::getTaskId, task.getId()));
        vo.setDependsOnTaskIds(deps.stream()
                .map(TaskDependency::getDependsOnTaskId)
                .collect(Collectors.toList()));
        return vo;
    }
}
