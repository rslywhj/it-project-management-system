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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 认证服务 - 已接入数据库
 */
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
        );

        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 检查用户状态
        if ("disabled".equals(user.getStatus())) {
            throw new BusinessException("用户已被禁用");
        }
        if ("locked".equals(user.getStatus())) {
            throw new BusinessException("用户已被锁定");
        }

        // 校验密码（BCrypt加密）
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 获取用户角色
        List<String> roles = sysPermissionMapper.selectRoleCodesByUserId(user.getId());
        String primaryRole = roles.isEmpty() ? "guest" : roles.get(0);

        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(), user.getUsername(), primaryRole);
        String refreshToken = jwtTokenProvider.generateRefreshToken(
                user.getId(), user.getUsername());

        // 更新最后登录时间
        user.setLastLoginAt(LocalDateTime.now());
        sysUserMapper.updateById(user);

        UserInfoVO userInfo = UserInfoVO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .role(primaryRole)
                .orgId(user.getOrgId())
                .build();

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
        List<String> roles = sysPermissionMapper.selectRoleCodesByUserId(userId);
        String primaryRole = roles.isEmpty() ? "guest" : roles.get(0);

        String newAccessToken = jwtTokenProvider.generateAccessToken(userId, username, primaryRole);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId, username);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration() / 1000)
                .build();
    }
}
