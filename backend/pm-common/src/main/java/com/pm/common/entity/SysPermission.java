package com.pm.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_permission")
@Schema(description = "系统权限")
public class SysPermission {

    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "权限码")
    private String permissionCode;

    @Schema(description = "权限名称")
    private String permissionName;

    @Schema(description = "所属模块")
    private String module;

    @Schema(description = "操作类型")
    private String operation;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "排序序号")
    private Integer sortOrder;

    private LocalDateTime createdAt;
    private Integer isDeleted;
}
