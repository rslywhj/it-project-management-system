package com.pm.project.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目成员实体
 */
@Data
@TableName("project_member")
@Schema(description = "项目成员")
public class ProjectMember {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "项目ID")
    private Long projectId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "角色：PROJECT_MANAGER/DEVELOPER/TESTER/PRODUCT_MANAGER/EXTERNAL/GUEST")
    private String role;

    @Schema(description = "关联推广单元ID（推广单元负责人时有值）")
    private Long promotionUnitId;

    @Schema(description = "加入时间")
    private LocalDateTime joinedAt;
}
