package com.pm.project.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pm.common.exception.BusinessException;
import com.pm.project.domain.Project;
import com.pm.project.dto.ProjectCreateRequest;
import com.pm.project.mapper.ProjectMapper;
import com.pm.project.mapper.ProjectMemberMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ProjectService 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectService 单元测试")
class ProjectServiceTest {

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private ProjectMemberMapper projectMemberMapper;

    @InjectMocks
    private ProjectService projectService;

    private ProjectCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        createRequest = new ProjectCreateRequest();
        createRequest.setName("测试项目");
        createRequest.setDescription("测试项目描述");
        createRequest.setType("software_dev");
    }

    @Test
    @DisplayName("创建项目成功 - 自动生成项目编码")
    void createProject_Success_AutoGenerateCode() {
        // Given
        when(projectMapper.selectCount(any())).thenReturn(0L);
        when(projectMapper.insert(any(Project.class))).thenReturn(1);

        // When
        var result = projectService.createProject(createRequest);

        // Then
        assertNotNull(result);
        assertNotNull(result.getProjectCode());
        assertTrue(result.getProjectCode().startsWith("PM"));
        assertEquals("测试项目", result.getName());

        verify(projectMapper).insert(any(Project.class));
    }

    @Test
    @DisplayName("创建项目成功 - 使用指定编码")
    void createProject_Success_WithSpecifiedCode() {
        // Given
        createRequest.setProjectCode("CUSTOM-001");
        when(projectMapper.insert(any(Project.class))).thenReturn(1);

        // When
        var result = projectService.createProject(createRequest);

        // Then
        assertNotNull(result);
        assertEquals("CUSTOM-001", result.getProjectCode());
    }

    @Test
    @DisplayName("项目编码生成不重复")
    void generateProjectCode_NoCollision() {
        // Given
        when(projectMapper.insert(any(Project.class))).thenReturn(1);

        // When - 创建多个项目
        var result1 = projectService.createProject(createRequest);
        var result2 = projectService.createProject(createRequest);

        // Then - 编码应该不同
        assertNotEquals(result1.getProjectCode(), result2.getProjectCode());
    }

    @Test
    @DisplayName("删除项目成功 - 软删除")
    void deleteProject_Success() {
        // Given
        Project project = new Project();
        project.setId(1L);
        project.setName("测试项目");
        when(projectMapper.selectById(1L)).thenReturn(project);
        when(projectMapper.updateById(any(Project.class))).thenReturn(1);

        // When
        assertDoesNotThrow(() -> projectService.deleteProject(1L));

        // Then
        verify(projectMapper).updateById(any(Project.class));
    }

    @Test
    @DisplayName("删除项目失败 - 项目不存在")
    void deleteProject_NotFound() {
        // Given
        when(projectMapper.selectById(999L)).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> projectService.deleteProject(999L));
    }
}
