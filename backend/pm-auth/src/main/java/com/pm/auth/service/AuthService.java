package com.pm.auth.service;

import com.pm.auth.config.JwtTokenProvider;
import com.pm.auth.dto.LoginRequest;
import com.pm.auth.dto.TokenResponse;
import com.pm.auth.dto.UserInfoVO;
import com.pm.common.exception.BusinessException;
import com.pm.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务
 * 注意：初期使用内存用户，后续接入数据库用户表
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    // TODO: 替换为数据库查询
    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi"; // Admin@123456
    private static final Long DEFAULT_USER_ID = 1L;
    private static final String DEFAULT_ROLE = "super_admin";  // 与数据库一致
    private static final String DEFAULT_REAL_NAME = "系统管理员";

    /**
     * 用户登录
     */
    public TokenResponse login(LoginRequest request) {
        // TODO: 从数据库查询用户
        if (!DEFAULT_USERNAME.equals(request.getUsername())) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 校验密码（BCrypt加密）
        // 开发环境临时逻辑：允许 admin/Admin@123456 登录
        if (!passwordEncoder.matches(request.getPassword(), DEFAULT_PASSWORD)
                && !"Admin@123456".equals(request.getPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(
                DEFAULT_USER_ID, DEFAULT_USERNAME, DEFAULT_ROLE);
        String refreshToken = jwtTokenProvider.generateRefreshToken(
                DEFAULT_USER_ID, DEFAULT_USERNAME);

        UserInfoVO userInfo = UserInfoVO.builder()
                .userId(DEFAULT_USER_ID)
                .username(DEFAULT_USERNAME)
                .realName(DEFAULT_REAL_NAME)
                .role(DEFAULT_ROLE)
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

        // TODO: 从数据库查询用户角色
        String role = DEFAULT_ROLE;

        String newAccessToken = jwtTokenProvider.generateAccessToken(userId, username, role);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId, username);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration() / 1000)
                .build();
    }
}
