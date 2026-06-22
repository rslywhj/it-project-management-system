package com.pm.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "更新用户请求")
public class UserUpdateRequest {

    @Size(max = 50, message = "真实姓名不能超过50个字符")
    @Schema(description = "真实姓名")
    private String realName;

    @Size(max = 100, message = "邮箱不能超过100个字符")
    @Schema(description = "邮箱")
    private String email;

    @Size(max = 20, message = "手机号不能超过20个字符")
    @Schema(description = "手机号")
    private String phone;

    @Size(max = 500, message = "头像URL不能超过500个字符")
    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "用户状态: active/disabled/locked")
    private String status;

    @Schema(description = "角色ID列表")
    private List<Long> roleIds;
}
