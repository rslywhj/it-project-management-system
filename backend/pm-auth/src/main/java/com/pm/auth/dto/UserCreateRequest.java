package com.pm.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "创建用户请求")
public class UserCreateRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(max = 50, message = "用户名不能超过50个字符")
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Size(max = 50, message = "真实姓名不能超过50个字符")
    @Schema(description = "真实姓名")
    private String realName;

    @Size(max = 100, message = "邮箱不能超过100个字符")
    @Schema(description = "邮箱")
    private String email;

    @Size(max = 20, message = "手机号不能超过20个字符")
    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "角色ID列表")
    private List<Long> roleIds;
}
