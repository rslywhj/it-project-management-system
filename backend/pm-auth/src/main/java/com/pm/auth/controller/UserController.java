package com.pm.auth.controller;

import com.pm.auth.dto.UserCreateRequest;
import com.pm.auth.dto.UserUpdateRequest;
import com.pm.auth.dto.UserVO;
import com.pm.auth.service.SysUserService;
import com.pm.common.result.PageResult;
import com.pm.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/system/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "系统用户的增删改查")
public class UserController {

    private final SysUserService sysUserService;

    @GetMapping
    @Operation(summary = "分页查询用户列表")
    public Result<PageResult<UserVO>> listUsers(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "关键词（用户名/姓名/邮箱）") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态筛选") @RequestParam(required = false) String status) {
        return Result.ok(sysUserService.listUsers(page, size, keyword, status));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情")
    public Result<UserVO> getUser(@PathVariable Long id) {
        return Result.ok(sysUserService.getUser(id));
    }

    @PostMapping
    @Operation(summary = "创建用户")
    public Result<UserVO> createUser(@Valid @RequestBody UserCreateRequest request) {
        return Result.ok(sysUserService.createUser(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户")
    public Result<UserVO> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        return Result.ok(sysUserService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public Result<Void> deleteUser(@PathVariable Long id) {
        sysUserService.deleteUser(id);
        return Result.ok();
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "启用/禁用用户")
    public Result<Void> updateUserStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        sysUserService.updateUserStatus(id, status);
        return Result.ok();
    }

    @PutMapping("/{id}/password/reset")
    @Operation(summary = "重置用户密码")
    public Result<Void> resetPassword(
            @PathVariable Long id,
            @RequestParam String newPassword) {
        sysUserService.resetPassword(id, newPassword);
        return Result.ok();
    }
}
