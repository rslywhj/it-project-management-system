package com.pm.auth.service;

import com.pm.auth.dto.RoleRequest;
import com.pm.auth.dto.UserCreateRequest;
import com.pm.auth.dto.UserUpdateRequest;
import com.pm.common.result.PageResult;
import com.pm.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 系统管理控制器 — 用户、角色、权限管理
 */
@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
@Tag(name = "系统管理", description = "用户管理、角色管理、权限管理")
public class SystemController {

    private final SystemService systemService;

    // ==================== 用户管理 ====================

    @GetMapping("/users")
    @Operation(summary = "用户列表", description = "分页查询用户列表")
    public Result<PageResult<Map<String, Object>>> listUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        return Result.ok(systemService.listUsers(page, size, keyword, status));
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "用户详情", description = "获取用户详细信息")
    public Result<Map<String, Object>> getUser(@PathVariable Long userId) {
        return Result.ok(systemService.getUser(userId));
    }

    @PostMapping("/users")
    @Operation(summary = "创建用户", description = "创建新用户")
    public Result<Map<String, Object>> createUser(@Valid @RequestBody UserCreateRequest request) {
        return Result.ok(systemService.createUser(request));
    }

    @PutMapping("/users/{userId}")
    @Operation(summary = "更新用户", description = "更新用户信息")
    public Result<Map<String, Object>> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserUpdateRequest request) {
        return Result.ok(systemService.updateUser(userId, request));
    }

    @DeleteMapping("/users/{userId}")
    @Operation(summary = "删除用户", description = "删除用户")
    public Result<Void> deleteUser(@PathVariable Long userId) {
        systemService.deleteUser(userId);
        return Result.ok();
    }

    @PostMapping("/users/{userId}/reset-password")
    @Operation(summary = "重置密码", description = "管理员重置用户密码")
    public Result<Void> resetPassword(
            @PathVariable Long userId,
            @RequestParam String newPassword) {
        systemService.resetPassword(userId, newPassword);
        return Result.ok();
    }

    // ==================== 角色管理 ====================

    @GetMapping("/roles")
    @Operation(summary = "角色列表", description = "获取所有角色")
    public Result<List<Map<String, Object>>> listRoles() {
        return Result.ok(systemService.listRoles());
    }

    @GetMapping("/roles/{roleId}")
    @Operation(summary = "角色详情", description = "获取角色详细信息")
    public Result<Map<String, Object>> getRole(@PathVariable Long roleId) {
        return Result.ok(systemService.getRole(roleId));
    }

    @PostMapping("/roles")
    @Operation(summary = "创建角色", description = "创建新角色")
    public Result<Map<String, Object>> createRole(@Valid @RequestBody RoleRequest request) {
        return Result.ok(systemService.createRole(request));
    }

    @PutMapping("/roles/{roleId}")
    @Operation(summary = "更新角色", description = "更新角色信息")
    public Result<Map<String, Object>> updateRole(
            @PathVariable Long roleId,
            @Valid @RequestBody RoleRequest request) {
        return Result.ok(systemService.updateRole(roleId, request));
    }

    @DeleteMapping("/roles/{roleId}")
    @Operation(summary = "删除角色", description = "删除角色")
    public Result<Void> deleteRole(@PathVariable Long roleId) {
        systemService.deleteRole(roleId);
        return Result.ok();
    }

    // ==================== 权限管理 ====================

    @GetMapping("/permissions")
    @Operation(summary = "权限列表", description = "获取所有权限")
    public Result<List<Map<String, Object>>> listPermissions() {
        return Result.ok(systemService.listPermissions());
    }
}
