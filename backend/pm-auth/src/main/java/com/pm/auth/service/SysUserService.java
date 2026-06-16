package com.pm.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pm.auth.dto.UserCreateRequest;
import com.pm.auth.dto.UserUpdateRequest;
import com.pm.auth.dto.UserVO;
import com.pm.auth.dto.RoleVO;
import com.pm.common.entity.SysRole;
import com.pm.common.entity.SysUser;
import com.pm.common.entity.SysUserRole;
import com.pm.common.exception.BusinessException;
import com.pm.common.mapper.SysRoleMapper;
import com.pm.common.mapper.SysUserMapper;
import com.pm.common.mapper.SysUserRoleMapper;
import com.pm.common.result.PageResult;
import com.pm.common.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理服务
 */
@Service
@RequiredArgsConstructor
public class SysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 分页查询用户列表
     */
    public PageResult<UserVO> listUsers(int page, int size, String keyword, String status) {
        Page<SysUser> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getRealName, keyword)
                    .or().like(SysUser::getEmail, keyword)
            );
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(SysUser::getStatus, status);
        }
        wrapper.orderByDesc(SysUser::getCreatedAt);

        Page<SysUser> result = sysUserMapper.selectPage(pageParam, wrapper);
        List<UserVO> voList = result.getRecords().stream()
                .map(this::toUserVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, result.getTotal(), page, size);
    }

    /**
     * 获取用户详情
     */
    public UserVO getUser(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return toUserVO(user);
    }

    /**
     * 创建用户
     */
    @Transactional
    public UserVO createUser(UserCreateRequest request) {
        // 检查用户名唯一性
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, request.getUsername());
        if (sysUserMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setOrgId(request.getOrgId());
        user.setRemark(request.getRemark());
        user.setStatus("active");

        UserContext.UserInfo currentUser = UserContext.get();
        if (currentUser != null) {
            user.setCreatedBy(currentUser.getUserId());
        }
        sysUserMapper.insert(user);

        // 分配角色
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            assignRoles(user.getId(), request.getRoleIds());
        }

        return toUserVO(user);
    }

    /**
     * 更新用户
     */
    @Transactional
    public UserVO updateUser(Long userId, UserUpdateRequest request) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (StringUtils.hasText(request.getRealName())) {
            user.setRealName(request.getRealName());
        }
        if (StringUtils.hasText(request.getEmail())) {
            user.setEmail(request.getEmail());
        }
        if (StringUtils.hasText(request.getPhone())) {
            user.setPhone(request.getPhone());
        }
        if (StringUtils.hasText(request.getAvatar())) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getOrgId() != null) {
            user.setOrgId(request.getOrgId());
        }
        if (request.getRemark() != null) {
            user.setRemark(request.getRemark());
        }

        UserContext.UserInfo currentUser = UserContext.get();
        if (currentUser != null) {
            user.setUpdatedBy(currentUser.getUserId());
        }
        sysUserMapper.updateById(user);

        // 更新角色
        if (request.getRoleIds() != null) {
            // 删除旧角色关联
            LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUserRole::getUserId, userId);
            sysUserRoleMapper.delete(wrapper);
            // 分配新角色
            if (!request.getRoleIds().isEmpty()) {
                assignRoles(userId, request.getRoleIds());
            }
        }

        return toUserVO(user);
    }

    /**
     * 删除用户
     */
    @Transactional
    public void deleteUser(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 删除用户角色关联
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        sysUserRoleMapper.delete(wrapper);
        // 删除用户
        sysUserMapper.deleteById(userId);
    }

    /**
     * 更新用户状态
     */
    public void updateUserStatus(Long userId, String status) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus(status);
        sysUserMapper.updateById(user);
    }

    /**
     * 重置用户密码
     */
    public void resetPassword(Long userId, String newPassword) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        sysUserMapper.updateById(user);
    }

    /**
     * 分配用户角色
     */
    private void assignRoles(Long userId, List<Long> roleIds) {
        UserContext.UserInfo currentUser = UserContext.get();
        for (Long roleId : roleIds) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRole.setStatus("active");
            if (currentUser != null) {
                userRole.setCreatedBy(currentUser.getUserId());
            }
            sysUserRoleMapper.insert(userRole);
        }
    }

    /**
     * 转换为 UserVO
     */
    private UserVO toUserVO(SysUser user) {
        // 查询用户角色
        List<SysRole> roles = getUserRoles(user.getId());
        List<RoleVO> roleVOs = roles.stream()
                .map(role -> RoleVO.builder()
                        .id(role.getId())
                        .roleCode(role.getRoleCode())
                        .roleName(role.getRoleName())
                        .description(role.getDescription())
                        .dataScope(role.getDataScope())
                        .build())
                .collect(Collectors.toList());

        return UserVO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .orgId(user.getOrgId())
                .status(user.getStatus())
                .remark(user.getRemark())
                .roles(roleVOs)
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .build();
    }

    /**
     * 获取用户的角色列表
     */
    private List<SysRole> getUserRoles(Long userId) {
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId)
        );
        if (userRoles.isEmpty()) {
            return List.of();
        }
        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());
        return sysRoleMapper.selectBatchIds(roleIds);
    }
}
