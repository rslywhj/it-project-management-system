package com.pm.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户视图对象
 */
@Data
@Builder
@Schema(description = "用户详情")
public class UserVO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "所属组织ID")
    private Long orgId;

    @Schema(description = "组织名称")
    private String orgName;

    @Schema(description = "状态（active/disabled/locked）")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "角色列表")
    private List<RoleVO> roles;

    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginAt;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
