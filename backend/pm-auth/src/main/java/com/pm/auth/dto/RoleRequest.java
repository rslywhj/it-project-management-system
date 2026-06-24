package com.pm.auth.dto;

import com.pm.common.validation.ValidationPatterns;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "角色请求")
public class RoleRequest {

    @NotBlank(message = "角色编码不能为空")
    @Size(max = 50, message = "角色编码不能超过50个字符")
    @Schema(description = "角色编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String roleCode;

    @NotBlank(message = "角色名称不能为空")
    @Size(max = 100, message = "角色名称不能超过100个字符")
    @Schema(description = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String roleName;

    @Size(max = 500, message = "描述不能超过500个字符")
    @Schema(description = "描述")
    private String description;

    @Pattern(regexp = "^(all|project|promotion_unit|self)$", message = "数据范围必须是: all/project/promotion_unit/self")
    @Schema(description = "数据范围: all/project/promotion_unit/self")
    private String dataScope;

    @Schema(description = "排序顺序")
    private Integer sortOrder;

    @Schema(description = "权限ID列表")
    private List<Long> permissionIds;
}
