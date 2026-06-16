package com.pm.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 角色创建/更新请求
 */
@Data
@Schema(description = "角色请求")
public class RoleRequest {

    @NotBlank(message = "角色编码不能为空")
    @Schema(description = "角色编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String roleCode;

    @NotBlank(message = "角色名称不能为空")
    @Schema(description = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String roleName;

    @Schema(description = "角色说明")
    private String description;

    @Schema(description = "数据范围（all/project/promotion_unit/self）")
    private String dataScope;

    @Schema(description = "排序序号")
    private Integer sortOrder;

    @Schema(description = "权限ID列表")
    private List<Long> permissionIds;
}
