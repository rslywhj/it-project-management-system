package com.pm.auth.controller;

import com.pm.auth.dto.RoleRequest;
import com.pm.auth.dto.RoleVO;
import com.pm.auth.service.SysRoleService;
import com.pm.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/api/system/roles")
@RequiredArgsConstructor
@Tag(name = "角色管理", description = "系统角色的增删改查及权限分配")
public class RoleController {

    private final SysRoleService sysRoleService;

    @GetMapping
    @Operation(summary = "查询角色列表")
    public Result<List<RoleVO>> listRoles() {
        return Result.ok(sysRoleService.listRoles());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取角色详情")
    public Result<RoleVO> getRole(@PathVariable Long id) {
        return Result.ok(sysRoleService.getRole(id));
    }

    @PostMapping
    @Operation(summary = "创建角色")
    public Result<RoleVO> createRole(@Valid @RequestBody RoleRequest request) {
        return Result.ok(sysRoleService.createRole(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新角色")
    public Result<RoleVO> updateRole(@PathVariable Long id, @Valid @RequestBody RoleRequest request) {
        return Result.ok(sysRoleService.updateRole(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色")
    public Result<Void> deleteRole(@PathVariable Long id) {
        sysRoleService.deleteRole(id);
        return Result.ok();
    }
}
