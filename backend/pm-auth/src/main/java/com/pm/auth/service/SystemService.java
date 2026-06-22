package com.pm.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pm.auth.dto.RoleRequest;
import com.pm.auth.dto.UserCreateRequest;
import com.pm.auth.dto.UserUpdateRequest;
import com.pm.auth.annotation.RequirePermission;
import com.pm.common.entity.*;
import com.pm.common.exception.BusinessException;
import com.pm.common.result.PageResult;
import com.pm.common.result.ResultCode;
import com.pm.common.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统管理服务 — 用户、角色、权限管理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysPermissionMapper sysPermissionMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;
    private final PasswordEncoder passwordEncoder;

    // ==================== 用户管理 ====================

    @RequirePermission("system:user")
    public PageResult<Map<String, Object>> listUsers(int page, int size, String keyword, String status) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getRealName, keyword)
                    .or().like(SysUser::getEmail, keyword));
        }
        wrapper.eq(StringUtils.hasText(status), SysUser::getStatus, status);
        wrapper.orderByDesc(SysUser::getCreatedAt);

        Page<SysUser> result = sysUserMapper.selectPage(new Page<>(page, size), wrapper);
        List<Map<String, Object>> voList = result.getRecords().stream()
                .map(this::toUserVO)
                .collect(Collectors.toList());
        return PageResult.of(voList, result.getTotal(), page, size);
    }

    @RequirePermission("system:user")
    public Map<String, Object> getUser(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return toUserVO(user);
    }

    @Transactional
    @RequirePermission("system:user")
    public Map<String, Object> createUser(UserCreateRequest request) {
        // 检查用户名唯一性
        Long exists = sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, request.getUsername()));
        if (exists > 0) {
            throw new BusinessException(1001, "用户名已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setStatus("active");
        sysUserMapper.insert(user);

        // 分配角色
        if (!CollectionUtils.isEmpty(request.getRoleIds())) {
            assignRoles(user.getId(), request.getRoleIds());
        }

        return toUserVO(user);
    }

    @Transactional
    @RequirePermission("system:user")
    public Map<String, Object> updateUser(Long userId, UserUpdateRequest request) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (request.getRealName() != null) {
            user.setRealName(request.getRealName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        sysUserMapper.updateById(user);

        // 更新角色
        if (request.getRoleIds() != null) {
            // 删除旧角色
            sysUserRoleMapper.delete(
                    new LambdaQueryWrapper<SysUserRole>()
                            .eq(SysUserRole::getUserId, userId));
            // 分配新角色
            if (!request.getRoleIds().isEmpty()) {
                assignRoles(userId, request.getRoleIds());
            }
        }

        return toUserVO(user);
    }

    @Transactional
    @RequirePermission("system:user")
    public void deleteUser(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        // 不允许删除超级管理员
        if ("admin".equals(user.getUsername())) {
            throw new BusinessException(1002, "不能删除超级管理员");
        }
        sysUserMapper.deleteById(userId);
        // 删除用户角色关联
        sysUserRoleMapper.delete(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, userId));
    }

    @RequirePermission("system:user")
    public void resetPassword(Long userId, String newPassword) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        sysUserMapper.updateById(user);
        log.info("Admin reset password for user {}", userId);
    }

    // ==================== 角色管理 ====================

    @RequirePermission("system:role")
    public List<Map<String, Object>> listRoles() {
        List<SysRole> roles = sysRoleMapper.selectList(
                new LambdaQueryWrapper<SysRole>()
                        .orderByAsc(SysRole::getSortOrder));
        return roles.stream().map(this::toRoleVO).collect(Collectors.toList());
    }

    @RequirePermission("system:role")
    public Map<String, Object> getRole(Long roleId) {
        SysRole role = sysRoleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException(1003, "角色不存在");
        }
        return toRoleVO(role);
    }

    @Transactional
    @RequirePermission("system:role")
    public Map<String, Object> createRole(RoleRequest request) {
        // 检查角色编码唯一性
        Long exists = sysRoleMapper.selectCount(
                new LambdaQueryWrapper<SysRole>()
                        .eq(SysRole::getRoleCode, request.getRoleCode()));
        if (exists > 0) {
            throw new BusinessException(1004, "角色编码已存在");
        }

        SysRole role = new SysRole();
        role.setRoleCode(request.getRoleCode());
        role.setRoleName(request.getRoleName());
        role.setDescription(request.getDescription());
        role.setDataScope(request.getDataScope() != null ? request.getDataScope() : "self");
        role.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        role.setStatus("active");
        role.setIsSystem(0);
        sysRoleMapper.insert(role);

        // 分配权限
        if (!CollectionUtils.isEmpty(request.getPermissionIds())) {
            assignPermissions(role.getId(), request.getPermissionIds());
        }

        return toRoleVO(role);
    }

    @Transactional
    @RequirePermission("system:role")
    public Map<String, Object> updateRole(Long roleId, RoleRequest request) {
        SysRole role = sysRoleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException(1003, "角色不存在");
        }
        // 不允许修改系统内置角色的编码
        if (role.getIsSystem() == 1 && !role.getRoleCode().equals(request.getRoleCode())) {
            throw new BusinessException(1005, "系统内置角色编码不可修改");
        }

        if (request.getRoleCode() != null) {
            role.setRoleCode(request.getRoleCode());
        }
        if (request.getRoleName() != null) {
            role.setRoleName(request.getRoleName());
        }
        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }
        if (request.getDataScope() != null) {
            role.setDataScope(request.getDataScope());
        }
        if (request.getSortOrder() != null) {
            role.setSortOrder(request.getSortOrder());
        }
        sysRoleMapper.updateById(role);

        // 更新权限
        if (request.getPermissionIds() != null) {
            // 删除旧权限
            sysRolePermissionMapper.delete(
                    new LambdaQueryWrapper<SysRolePermission>()
                            .eq(SysRolePermission::getRoleId, roleId));
            // 分配新权限
            if (!request.getPermissionIds().isEmpty()) {
                assignPermissions(roleId, request.getPermissionIds());
            }
        }

        return toRoleVO(role);
    }

    @Transactional
    @RequirePermission("system:role")
    public void deleteRole(Long roleId) {
        SysRole role = sysRoleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException(1003, "角色不存在");
        }
        if (role.getIsSystem() == 1) {
            throw new BusinessException(1006, "系统内置角色不可删除");
        }
        sysRoleMapper.deleteById(roleId);
        // 删除角色权限关联
        sysRolePermissionMapper.delete(
                new LambdaQueryWrapper<SysRolePermission>()
                        .eq(SysRolePermission::getRoleId, roleId));
        // 删除用户角色关联
        sysUserRoleMapper.delete(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getRoleId, roleId));
    }

    // ==================== 权限管理 ====================

    @RequirePermission("system:permission")
    public List<Map<String, Object>> listPermissions() {
        List<SysPermission> permissions = sysPermissionMapper.selectList(
                new LambdaQueryWrapper<SysPermission>()
                        .orderByAsc(SysPermission::getModule)
                        .orderByAsc(SysPermission::getSortOrder));
        return permissions.stream().map(this::toPermissionVO).collect(Collectors.toList());
    }

    // ==================== 内部方法 ====================

    private void assignRoles(Long userId, List<Long> roleIds) {
        for (Long roleId : roleIds) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRole.setStatus("active");
            sysUserRoleMapper.insert(userRole);
        }
    }

    private void assignPermissions(Long roleId, List<Long> permissionIds) {
        for (Long permissionId : permissionIds) {
            SysRolePermission rolePermission = new SysRolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permissionId);
            sysRolePermissionMapper.insert(rolePermission);
        }
    }

    private Map<String, Object> toUserVO(SysUser user) {
        Map<String, Object> vo = new LinkedHashMap<>();
        vo.put("id", user.getId());
        vo.put("username", user.getUsername());
        vo.put("realName", user.getRealName());
        vo.put("email", user.getEmail());
        vo.put("phone", user.getPhone());
        vo.put("avatar", user.getAvatar());
        vo.put("orgId", user.getOrgId());
        vo.put("status", user.getStatus());
        vo.put("lastLoginAt", user.getLastLoginAt());
        vo.put("createdAt", user.getCreatedAt());

        // 查询用户角色
        List<String> roleCodes = sysPermissionMapper.selectRoleCodesByUserId(user.getId());
        vo.put("roles", roleCodes);

        return vo;
    }

    private Map<String, Object> toRoleVO(SysRole role) {
        Map<String, Object> vo = new LinkedHashMap<>();
        vo.put("id", role.getId());
        vo.put("roleCode", role.getRoleCode());
        vo.put("roleName", role.getRoleName());
        vo.put("description", role.getDescription());
        vo.put("dataScope", role.getDataScope());
        vo.put("sortOrder", role.getSortOrder());
        vo.put("status", role.getStatus());
        vo.put("isSystem", role.getIsSystem());

        // 查询角色权限
        List<SysRolePermission> rolePermissions = sysRolePermissionMapper.selectList(
                new LambdaQueryWrapper<SysRolePermission>()
                        .eq(SysRolePermission::getRoleId, role.getId()));
        List<Long> permissionIds = rolePermissions.stream()
                .map(SysRolePermission::getPermissionId)
                .collect(Collectors.toList());
        vo.put("permissionIds", permissionIds);

        return vo;
    }

    private Map<String, Object> toPermissionVO(SysPermission permission) {
        Map<String, Object> vo = new LinkedHashMap<>();
        vo.put("id", permission.getId());
        vo.put("permissionCode", permission.getPermissionCode());
        vo.put("permissionName", permission.getPermissionName());
        vo.put("module", permission.getModule());
        vo.put("operation", permission.getOperation());
        vo.put("description", permission.getDescription());
        return vo;
    }
}
