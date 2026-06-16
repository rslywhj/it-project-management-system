package com.pm.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户更新请求
 */
@Data
@Schema(description = "用户更新请求")
public class UserUpdateRequest {

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

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "角色ID列表")
    private List<Long> roleIds;
}
