package com.pm.auth.service;

import com.pm.auth.config.JwtTokenProvider;
import com.pm.auth.dto.LoginRequest;
import com.pm.auth.dto.TokenResponse;
import com.pm.common.entity.SysUser;
import com.pm.common.exception.BusinessException;
import com.pm.common.mapper.SysPermissionMapper;
import com.pm.common.mapper.SysUserMapper;
import com.pm.common.mapper.SysUserRoleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AuthService 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 单元测试")
class AuthServiceTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private SysUserRoleMapper sysUserRoleMapper;

    @Mock
    private SysPermissionMapper sysPermissionMapper;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private AuthService authService;

    private SysUser testUser;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new SysUser();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("$2a$10$encodedPassword");
        testUser.setRealName("测试用户");
        testUser.setStatus("active");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");
    }

    @Test
    @DisplayName("登录成功 - 正常场景")
    void login_Success() {
        // Given
        when(sysUserMapper.selectOne(any())).thenReturn(testUser);
        when(passwordEncoder.matches("password123", "$2a$10$encodedPassword")).thenReturn(true);
        when(sysPermissionMapper.selectRoleCodesByUserId(1L)).thenReturn(List.of("developer"));
        when(jwtTokenProvider.generateAccessToken(1L, "testuser", "developer")).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(1L, "testuser")).thenReturn("refresh-token");
        when(jwtTokenProvider.getAccessTokenExpiration()).thenReturn(7200000L);

        // When
        TokenResponse response = authService.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertNotNull(response.getUserInfo());
        assertEquals(1L, response.getUserInfo().getUserId());
        assertEquals("testuser", response.getUserInfo().getUsername());

        verify(sysUserMapper).updateById(testUser);
        assertNotNull(testUser.getLastLoginAt());
    }

    @Test
    @DisplayName("登录失败 - 用户不存在")
    void login_UserNotFound() {
        // Given
        when(sysUserMapper.selectOne(any())).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> authService.login(loginRequest));
    }

    @Test
    @DisplayName("登录失败 - 用户已禁用")
    void login_UserDisabled() {
        // Given
        testUser.setStatus("disabled");
        when(sysUserMapper.selectOne(any())).thenReturn(testUser);

        // When & Then
        assertThrows(BusinessException.class, () -> authService.login(loginRequest));
    }

    @Test
    @DisplayName("登录失败 - 密码错误")
    void login_WrongPassword() {
        // Given
        when(sysUserMapper.selectOne(any())).thenReturn(testUser);
        when(passwordEncoder.matches("password123", "$2a$10$encodedPassword")).thenReturn(false);

        // When & Then
        assertThrows(BusinessException.class, () -> authService.login(loginRequest));
    }

    @Test
    @DisplayName("登出成功 - Token加入黑名单")
    void logout_Success() {
        // Given
        String token = "test-token";
        when(jwtTokenProvider.getTokenRemainingMs(token)).thenReturn(3600000L);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);

        // When
        authService.logout(token);

        // Then
        verify(valueOperations).set(eq("auth:token:blacklist:" + token), eq("1"), eq(3600000L), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    @DisplayName("检查Token黑名单 - 在黑名单中")
    void isTokenBlacklisted_True() {
        // Given
        String token = "test-token";
        when(stringRedisTemplate.hasKey("auth:token:blacklist:" + token)).thenReturn(true);

        // When
        boolean result = authService.isTokenBlacklisted(token);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("修改密码成功")
    void changePassword_Success() {
        // Given
        when(sysUserMapper.selectById(1L)).thenReturn(testUser);
        when(passwordEncoder.matches("oldPassword", "$2a$10$encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("$2a$10$newEncodedPassword");

        // When
        authService.changePassword(1L, "oldPassword", "newPassword");

        // Then
        verify(sysUserMapper).updateById(testUser);
        assertEquals("$2a$10$newEncodedPassword", testUser.getPassword());
    }

    @Test
    @DisplayName("修改密码失败 - 原密码错误")
    void changePassword_WrongOldPassword() {
        // Given
        when(sysUserMapper.selectById(1L)).thenReturn(testUser);
        when(passwordEncoder.matches("wrongPassword", "$2a$10$encodedPassword")).thenReturn(false);

        // When & Then
        assertThrows(BusinessException.class,
                () -> authService.changePassword(1L, "wrongPassword", "newPassword"));
    }
}
