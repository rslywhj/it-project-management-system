package com.pm.project.controller;

import com.pm.project.domain.ProjectMember;
import com.pm.project.dto.ProjectCreateRequest;
import com.pm.project.dto.ProjectUpdateRequest;
import com.pm.project.dto.ProjectVO;
import com.pm.project.service.ProjectService;
import com.pm.common.result.PageResult;
import com.pm.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "项目管理", description = "项目 CRUD、成员管理")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @Operation(summary = "创建项目")
    public Result<ProjectVO> createProject(@Valid @RequestBody ProjectCreateRequest request) {
        return Result.ok(projectService.createProject(request));
    }

    @GetMapping
    @Operation(summary = "项目列表（分页）")
    public Result<PageResult<ProjectVO>> listProjects(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "关键词搜索") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态筛选") @RequestParam(required = false) String status) {
        return Result.ok(projectService.listProjects(page, size, keyword, status));
    }

    @GetMapping("/{projectId}")
    @Operation(summary = "项目详情")
    public Result<ProjectVO> getProject(@PathVariable Long projectId) {
        return Result.ok(projectService.getProject(projectId));
    }

    @PutMapping("/{projectId}")
    @Operation(summary = "更新项目")
    public Result<ProjectVO> updateProject(@PathVariable Long projectId,
                                           @Valid @RequestBody ProjectUpdateRequest request) {
        return Result.ok(projectService.updateProject(projectId, request));
    }

    @DeleteMapping("/{projectId}")
    @Operation(summary = "删除项目")
    public Result<Void> deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
        return Result.ok();
    }

    @PostMapping("/{projectId}/members")
    @Operation(summary = "添加项目成员")
    public Result<Void> addMember(@PathVariable Long projectId,
                                  @RequestParam Long userId,
                                  @RequestParam String role) {
        projectService.addMember(projectId, userId, role);
        return Result.ok();
    }

    @GetMapping("/{projectId}/members")
    @Operation(summary = "项目成员列表")
    public Result<List<ProjectMember>> listMembers(@PathVariable Long projectId) {
        return Result.ok(projectService.listMembers(projectId));
    }

    @DeleteMapping("/{projectId}/members/{userId}")
    @Operation(summary = "移除项目成员")
    public Result<Void> removeMember(@PathVariable Long projectId, @PathVariable Long userId) {
        projectService.removeMember(projectId, userId);
        return Result.ok();
    }
}
