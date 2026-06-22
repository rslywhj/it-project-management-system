package com.pm.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pm.auth.config.JwtTokenProvider;
import com.pm.auth.dto.LoginRequest;
import com.pm.auth.dto.TokenResponse;
import com.pm.auth.dto.UserInfoVO;
import com.pm.common.entity.SysUser;
import com.pm.common.exception.BusinessException;
import com.pm.common.mapper.SysPermissionMapper;
import com.pm.common.mapper.SysUserMapper;
import com.pm.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 认证服务
 * 从数据库查询用户，支持多用户登录
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final SysUserMapper sysUserMapper;
    private final SysPermissionMapper sysPermissionMapper;

    /**
     * 用户登录
     */
    public TokenResponse login(LoginRequest request) {
        // 从数据库查询用户
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, request.getUsername())
                        .eq(SysUser::getDeleted, 0));

        if (user == null) {
            log.warn("用户不存在: {}", request.getUsername());
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 检查用户状态
        if (!"active".equals(user.getStatus())) {
            log.warn("用户状态异常: {}, status: {}", request.getUsername(), user.getStatus());
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 校验密码（仅BCrypt加密，无明文后门）
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("密码错误: {}", request.getUsername());
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 查询用户角色
        String role = getUserRole(user.getId());

        // 更新最后登录时间
        user.setLastLoginAt(LocalDateTime.now());
        sysUserMapper.updateById(user);

        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(), user.getUsername(), role);
        String refreshToken = jwtTokenProvider.generateRefreshToken(
                user.getId(), user.getUsername());

        UserInfoVO userInfo = UserInfoVO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .role(role)
                .build();

        log.info("用户登录成功: {}", request.getUsername());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration() / 1000)
                .userInfo(userInfo)
                .build();
    }

    /**
     * 刷新 Token
     */
    public TokenResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(ResultCode.TOKEN_EXPIRED);
        }

        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        String username = jwtTokenProvider.parseToken(refreshToken).getSubject();

        // 从数据库查询用户角色
        String role = getUserRole(userId);

        String newAccessToken = jwtTokenProvider.generateAccessToken(userId, username, role);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId, username);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration() / 1000)
                .build();
    }

    /**
     * 获取用户角色
     */
    private String getUserRole(Long userId) {
        List<String> roles = sysPermissionMapper.selectRoleCodesByUserId(userId);
        if (roles != null && !roles.isEmpty()) {
            return roles.get(0);  // 返回第一个角色
        }
        return "user";  // 默认角色
    }
}
