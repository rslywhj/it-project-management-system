package com.pm.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pm.auth.config.JwtTokenProvider;
import com.pm.auth.dto.LoginRequest;
import com.pm.auth.dto.TokenResponse;
import com.pm.auth.dto.UserInfoVO;
import com.pm.common.entity.SysUser;
import com.pm.common.entity.SysUserRole;
import com.pm.common.exception.BusinessException;
import com.pm.common.mapper.SysPermissionMapper;
import com.pm.common.mapper.SysUserMapper;
import com.pm.common.mapper.SysUserRoleMapper;
import com.pm.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务 — 对接数据库用户表，实现真正的 RBAC 登录
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysPermissionMapper sysPermissionMapper;
    private final StringRedisTemplate stringRedisTemplate;

    /** Redis key 前缀：token 黑名单 */
    private static final String TOKEN_BLACKLIST_PREFIX = "auth:token:blacklist:";

    /**
     * 用户登录
     */
    public TokenResponse login(LoginRequest request) {
        // 1. 从数据库查询用户
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, request.getUsername()));

        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 2. 检查用户状态
        if (!"active".equals(user.getStatus())) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 3. 校验密码（仅 BCrypt，无明文后门）
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 4. 查询用户角色（取第一个角色用于 JWT claim）
        List<String> roleCodes = sysPermissionMapper.selectRoleCodesByUserId(user.getId());
        String primaryRole = roleCodes.isEmpty() ? "guest" : roleCodes.get(0);

        // 5. 生成 Token
        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(), user.getUsername(), primaryRole);
        String refreshToken = jwtTokenProvider.generateRefreshToken(
                user.getId(), user.getUsername());

        // 6. 更新最后登录时间
        user.setLastLoginAt(LocalDateTime.now());
        sysUserMapper.updateById(user);

        // 7. 构建返回
        UserInfoVO userInfo = UserInfoVO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName() != null ? user.getRealName() : user.getUsername())
                .role(primaryRole)
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

        // 检查 refresh token 是否在黑名单中
        if (isTokenBlacklisted(refreshToken)) {
            throw new BusinessException(ResultCode.TOKEN_EXPIRED);
        }

        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        String username = jwtTokenProvider.parseToken(refreshToken).getSubject();

        // 查询用户是否存在且状态正常
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null || !"active".equals(user.getStatus())) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 查询用户角色
        List<String> roleCodes = sysPermissionMapper.selectRoleCodesByUserId(user.getId());
        String primaryRole = roleCodes.isEmpty() ? "guest" : roleCodes.get(0);

        String newAccessToken = jwtTokenProvider.generateAccessToken(userId, username, primaryRole);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId, username);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration() / 1000)
                .build();
    }

    /**
     * 登出：将当前 token 加入 Redis 黑名单
     */
    public void logout(String token) {
        if (token == null || token.isEmpty()) {
            return;
        }
        try {
            // 计算 token 剩余有效期，作为黑名单 key 的 TTL
            long remainingMs = jwtTokenProvider.getTokenRemainingMs(token);
            if (remainingMs > 0) {
                String key = TOKEN_BLACKLIST_PREFIX + token;
                stringRedisTemplate.opsForValue().set(key, "1", remainingMs, TimeUnit.MILLISECONDS);
                log.info("Token added to blacklist, expires in {}ms", remainingMs);
            }
        } catch (Exception e) {
            log.warn("Failed to blacklist token: {}", e.getMessage());
        }
    }

    /**
     * 检查 token 是否在黑名单中
     */
    public boolean isTokenBlacklisted(String token) {
        String key = TOKEN_BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }

    /**
     * 修改密码
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 验证原密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        sysUserMapper.updateById(user);
        log.info("User {} changed password", userId);
    }

    /**
     * 更新个人信息
     */
    public SysUser updateProfile(Long userId, String realName, String email, String phone, String avatar) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (realName != null) {
            user.setRealName(realName);
        }
        if (email != null) {
            user.setEmail(email);
        }
        if (phone != null) {
            user.setPhone(phone);
        }
        if (avatar != null) {
            user.setAvatar(avatar);
        }

        sysUserMapper.updateById(user);
        return user;
    }
}
