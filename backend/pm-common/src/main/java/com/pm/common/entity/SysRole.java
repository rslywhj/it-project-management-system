package com.pm.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pm.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
@Schema(description = "系统角色")
public class SysRole extends BaseEntity {

    @Schema(description = "角色编码")
    private String roleCode;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色说明")
    private String description;

    @Schema(description = "数据范围（all/project/promotion_unit/self）")
    private String dataScope;

    @Schema(description = "排序序号")
    private Integer sortOrder;

    @Schema(description = "状态（active/disabled）")
    private String status;

    @Schema(description = "是否系统内置角色")
    private Integer isSystem;
}
