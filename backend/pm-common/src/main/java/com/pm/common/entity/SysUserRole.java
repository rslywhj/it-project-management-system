package com.pm.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user_role")
@Schema(description = "用户角色关联")
public class SysUserRole {

    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "项目ID（项目级角色时有值）")
    private Long projectId;

    @Schema(description = "状态")
    private String status;

    private LocalDateTime createdAt;
    private Long createdBy;
}
