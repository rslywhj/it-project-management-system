package com.pm.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色视图对象
 */
@Data
@Builder
@Schema(description = "角色详情")
public class RoleVO {

    @Schema(description = "角色ID")
    private Long id;

    @Schema(description = "角色编码")
    private String roleCode;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色说明")
    private String description;

    @Schema(description = "数据范围")
    private String dataScope;

    @Schema(description = "排序序号")
    private Integer sortOrder;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "是否系统内置")
    private Integer isSystem;

    @Schema(description = "权限ID列表")
    private List<Long> permissionIds;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
