package com.pm.project.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pm.auth.annotation.RequirePermission;
import com.pm.common.exception.BusinessException;
import com.pm.common.result.PageResult;
import com.pm.common.result.ResultCode;
import com.pm.common.util.UserContext;
import com.pm.project.domain.Project;
import com.pm.project.domain.ProjectMember;
import com.pm.project.dto.ProjectCreateRequest;
import com.pm.project.dto.ProjectUpdateRequest;
import com.pm.project.dto.ProjectVO;
import com.pm.project.mapper.ProjectMapper;
import com.pm.project.mapper.ProjectMemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectMapper projectMapper;
    private final ProjectMemberMapper projectMemberMapper;

    /**
     * 创建项目
     */
    @Transactional
    @RequirePermission("project:create")
    public ProjectVO createProject(ProjectCreateRequest request) {
        Project project = new Project();
        BeanUtils.copyProperties(request, project);
        project.setStatus("planning");

        // 自动生成项目编码
        if (!StringUtils.hasText(request.getProjectCode())) {
            project.setProjectCode(generateProjectCode());
        }

        project.setOwnerUserId(UserContext.getUserId());
        projectMapper.insert(project);

        // 将创建者设为项目经理
        ProjectMember member = new ProjectMember();
        member.setProjectId(project.getId());
        member.setUserId(UserContext.getUserId());
        member.setRole("PROJECT_MANAGER");
        member.setJoinedAt(LocalDateTime.now());
        projectMemberMapper.insert(member);

        return toVO(project, 1);
    }

    /**
     * 获取项目列表（分页）
     */
    public PageResult<ProjectVO> listProjects(int page, int size, String keyword, String status) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(keyword), Project::getName, keyword)
                .eq(StringUtils.hasText(status), Project::getStatus, status)
                .orderByDesc(Project::getCreatedAt);

        Page<Project> result = projectMapper.selectPage(new Page<>(page, size), wrapper);
        List<ProjectVO> voList = result.getRecords().stream()
                .map(p -> toVO(p, null))
                .collect(Collectors.toList());

        return PageResult.of(voList, result.getTotal(), page, size);
    }

    /**
     * 获取项目详情
     */
    public ProjectVO getProject(Long projectId) {
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException(ResultCode.PROJECT_NOT_FOUND);
        }
        Long memberCount = projectMemberMapper.selectCount(
                new LambdaQueryWrapper<ProjectMember>().eq(ProjectMember::getProjectId, projectId));
        return toVO(project, memberCount.intValue());
    }

    /**
     * 更新项目
     */
    @Transactional
    @RequirePermission("project:update")
    public ProjectVO updateProject(Long projectId, ProjectUpdateRequest request) {
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException(ResultCode.PROJECT_NOT_FOUND);
        }
        BeanUtils.copyProperties(request, project, "id", "projectCode", "ownerUserId", "createdAt", "createdBy");
        projectMapper.updateById(project);
        return getProject(projectId);
    }

    /**
     * 删除项目（软删除）
     */
    @Transactional
    @RequirePermission("project:delete")
    public void deleteProject(Long projectId) {
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException(ResultCode.PROJECT_NOT_FOUND);
        }
        projectMapper.deleteById(projectId);
    }

    /**
     * 添加项目成员
     */
    @Transactional
    @RequirePermission("project:member:add")
    public void addMember(Long projectId, Long userId, String role) {
        Long exists = projectMemberMapper.selectCount(
                new LambdaQueryWrapper<ProjectMember>()
                        .eq(ProjectMember::getProjectId, projectId)
                        .eq(ProjectMember::getUserId, userId));
        if (exists > 0) {
            throw new BusinessException(ResultCode.PROJECT_MEMBER_EXISTS);
        }
        ProjectMember member = new ProjectMember();
        member.setProjectId(projectId);
        member.setUserId(userId);
        member.setRole(role);
        member.setJoinedAt(LocalDateTime.now());
        projectMemberMapper.insert(member);
    }

    /**
     * 移除项目成员
     */
    @Transactional
    @RequirePermission("project:member:remove")
    public void removeMember(Long projectId, Long userId) {
        projectMemberMapper.delete(
                new LambdaQueryWrapper<ProjectMember>()
                        .eq(ProjectMember::getProjectId, projectId)
                        .eq(ProjectMember::getUserId, userId));
    }

    /**
     * 获取项目成员列表
     */
    public List<ProjectMember> listMembers(Long projectId) {
        return projectMemberMapper.selectList(
                new LambdaQueryWrapper<ProjectMember>()
                        .eq(ProjectMember::getProjectId, projectId));
    }

    private ProjectVO toVO(Project project, Integer memberCount) {
        ProjectVO vo = new ProjectVO();
        BeanUtils.copyProperties(project, vo);
        if (memberCount != null) {
            vo.setMemberCount(memberCount);
        }
        return vo;
    }

    private String generateProjectCode() {
        // 简单实现：PM + 时间戳后6位
        return "PM" + (System.currentTimeMillis() % 1000000);
    }
}
