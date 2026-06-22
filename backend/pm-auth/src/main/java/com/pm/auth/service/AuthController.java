package com.pm.auth.service;

import com.pm.auth.config.JwtTokenProvider;
import com.pm.auth.dto.LoginRequest;
import com.pm.auth.dto.TokenResponse;
import com.pm.common.mapper.SysPermissionMapper;
import com.pm.common.result.Result;
import com.pm.common.util.UserContext;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "登录、登出、Token刷新")
public class AuthController {

    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";

    private final AuthService authService;
    private final SysPermissionMapper sysPermissionMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate stringRedisTemplate;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户名密码登录，返回 JWT Token")
    public Result<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新Token", description = "使用 Refresh Token 获取新的 Access Token")
    public Result<TokenResponse> refreshToken(@RequestParam String refreshToken) {
        return Result.ok(authService.refreshToken(refreshToken));
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "登出并将Token加入黑名单")
    public Result<Void> logout(HttpServletRequest request) {
        String token = extractToken(request);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 将token加入Redis黑名单
            Claims claims = jwtTokenProvider.parseToken(token);
            long expiration = claims.getExpiration().getTime() - System.currentTimeMillis();
            if (expiration > 0) {
                stringRedisTemplate.opsForValue().set(
                        TOKEN_BLACKLIST_PREFIX + token,
                        "1",
                        expiration,
                        TimeUnit.MILLISECONDS);
                log.info("Token已加入黑名单，用户: {}", claims.getSubject());
            }
        }
        return Result.ok();
    }

    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public Result<Object> getCurrentUser() {
        UserContext.UserInfo user = UserContext.get();
        if (user == null) {
            return Result.fail(401, "未登录");
        }

        // 查询用户的权限码列表
        List<String> permissions = sysPermissionMapper.selectPermissionCodesByUserId(user.getUserId());
        List<String> roles = sysPermissionMapper.selectRoleCodesByUserId(user.getUserId());

        // 构建返回对象，兼容前端 UserInfo 接口
        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getUserId());
        result.put("username", user.getUsername());
        result.put("realName", user.getRealName() != null ? user.getRealName() : user.getUsername());
        result.put("role", user.getRole());
        result.put("roles", roles.isEmpty() ? List.of(user.getRole()) : roles);
        result.put("permissions", permissions);
        result.put("orgId", user.getOrgId());

        return Result.ok(result);
    }

    /**
     * 从请求头中提取Token
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
