package com.pm.auth.filter;

import com.pm.auth.config.JwtTokenProvider;
import com.pm.auth.service.AuthService;
import com.pm.common.entity.SysUser;
import com.pm.common.mapper.SysUserMapper;
import com.pm.common.util.UserContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT 认证过滤器
 * 1. 校验 token 有效性
 * 2. 检查 token 是否在 Redis 黑名单中（已登出）
 * 3. 设置 UserContext 和 Spring Security 上下文
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final SysUserMapper sysUserMapper;
    private final AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractToken(request);
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                // 检查 token 是否已被登出（黑名单）
                if (authService.isTokenBlacklisted(token)) {
                    log.debug("Token is blacklisted (logged out), skipping authentication");
                    filterChain.doFilter(request, response);
                    return;
                }

                Long userId = jwtTokenProvider.getUserIdFromToken(token);
                String role = jwtTokenProvider.getRoleFromToken(token);
                String username = jwtTokenProvider.parseToken(token).getSubject();

                // 从数据库查询用户真实姓名
                String realName = username;
                try {
                    SysUser sysUser = sysUserMapper.selectById(userId);
                    if (sysUser != null && sysUser.getRealName() != null) {
                        realName = sysUser.getRealName();
                    }
                } catch (Exception e) {
                    log.warn("Failed to query user realName from database", e);
                }

                // 设置 UserContext
                UserContext.UserInfo userInfo = new UserContext.UserInfo();
                userInfo.setUserId(userId);
                userInfo.setUsername(username);
                userInfo.setRealName(realName);
                userInfo.setRole(role);
                UserContext.set(userInfo);

                // 设置 Spring Security 上下文
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userId, null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.error("JWT authentication failed", e);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            UserContext.clear();
        }
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
