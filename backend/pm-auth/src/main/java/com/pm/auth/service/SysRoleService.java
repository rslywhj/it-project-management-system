package com.pm.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pm.auth.dto.RoleRequest;
import com.pm.auth.dto.RoleVO;
import com.pm.common.entity.SysRole;
import com.pm.common.entity.SysRolePermission;
import com.pm.common.exception.BusinessException;
import com.pm.common.mapper.SysRoleMapper;
import com.pm.common.mapper.SysRolePermissionMapper;
import com.pm.common.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色管理服务
 */
@Service
@RequiredArgsConstructor
public class SysRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;

    /**
     * 查询角色列表
     */
    public List<RoleVO> listRoles() {
        List<SysRole> roles = sysRoleMapper.selectList(
                new LambdaQueryWrapper<SysRole>()
                        .orderByAsc(SysRole::getSortOrder)
                        .orderByDesc(SysRole::getCreatedAt)
        );
        return roles.stream().map(this::toRoleVO).collect(Collectors.toList());
    }

    /**
     * 获取角色详情
     */
    public RoleVO getRole(Long roleId) {
        SysRole role = sysRoleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        return toRoleVO(role);
    }

    /**
     * 创建角色
     */
    @Transactional
    public RoleVO createRole(RoleRequest request) {
        // 检查角色编码唯一性
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleCode, request.getRoleCode());
        if (sysRoleMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("角色编码已存在");
        }

        SysRole role = new SysRole();
        role.setRoleCode(request.getRoleCode());
        role.setRoleName(request.getRoleName());
        role.setDescription(request.getDescription());
        role.setDataScope(StringUtils.hasText(request.getDataScope()) ? request.getDataScope() : "self");
        role.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        role.setStatus("active");
        role.setIsSystem(0);

        UserContext.UserInfo currentUser = UserContext.get();
        if (currentUser != null) {
            role.setCreatedBy(currentUser.getUserId());
        }
        sysRoleMapper.insert(role);

        // 分配权限
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            assignPermissions(role.getId(), request.getPermissionIds());
        }

        return toRoleVO(role);
    }

    /**
     * 更新角色
     */
    @Transactional
    public RoleVO updateRole(Long roleId, RoleRequest request) {
        SysRole role = sysRoleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        if (role.getIsSystem() != null && role.getIsSystem() == 1) {
            throw new BusinessException("系统内置角色不可修改");
        }

        if (StringUtils.hasText(request.getRoleName())) {
            role.setRoleName(request.getRoleName());
        }
        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }
        if (StringUtils.hasText(request.getDataScope())) {
            role.setDataScope(request.getDataScope());
        }
        if (request.getSortOrder() != null) {
            role.setSortOrder(request.getSortOrder());
        }

        UserContext.UserInfo currentUser = UserContext.get();
        if (currentUser != null) {
            role.setUpdatedBy(currentUser.getUserId());
        }
        sysRoleMapper.updateById(role);

        // 更新权限
        if (request.getPermissionIds() != null) {
            // 删除旧权限关联
            LambdaQueryWrapper<SysRolePermission> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysRolePermission::getRoleId, roleId);
            sysRolePermissionMapper.delete(wrapper);
            // 分配新权限
            if (!request.getPermissionIds().isEmpty()) {
                assignPermissions(roleId, request.getPermissionIds());
            }
        }

        return toRoleVO(role);
    }

    /**
     * 删除角色
     */
    @Transactional
    public void deleteRole(Long roleId) {
        SysRole role = sysRoleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        if (role.getIsSystem() != null && role.getIsSystem() == 1) {
            throw new BusinessException("系统内置角色不可删除");
        }
        // 删除角色权限关联
        LambdaQueryWrapper<SysRolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRolePermission::getRoleId, roleId);
        sysRolePermissionMapper.delete(wrapper);
        // 删除角色
        sysRoleMapper.deleteById(roleId);
    }

    /**
     * 分配角色权限
     */
    private void assignPermissions(Long roleId, List<Long> permissionIds) {
        UserContext.UserInfo currentUser = UserContext.get();
        for (Long permissionId : permissionIds) {
            SysRolePermission rp = new SysRolePermission();
            rp.setRoleId(roleId);
            rp.setPermissionId(permissionId);
            if (currentUser != null) {
                rp.setCreatedBy(currentUser.getUserId());
            }
            sysRolePermissionMapper.insert(rp);
        }
    }

    /**
     * 转换为 RoleVO
     */
    private RoleVO toRoleVO(SysRole role) {
        // 查询角色权限
        List<SysRolePermission> rps = sysRolePermissionMapper.selectList(
                new LambdaQueryWrapper<SysRolePermission>()
                        .eq(SysRolePermission::getRoleId, role.getId())
        );
        List<Long> permissionIds = rps.stream()
                .map(SysRolePermission::getPermissionId)
                .collect(Collectors.toList());

        return RoleVO.builder()
                .id(role.getId())
                .roleCode(role.getRoleCode())
                .roleName(role.getRoleName())
                .description(role.getDescription())
                .dataScope(role.getDataScope())
                .sortOrder(role.getSortOrder())
                .status(role.getStatus())
                .isSystem(role.getIsSystem())
                .permissionIds(permissionIds)
                .createdAt(role.getCreatedAt())
                .build();
    }
}
