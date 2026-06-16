package com.pm.common.base;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体
 */
@Data
public abstract class BaseEntity implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private Long id;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建人ID")
    private Long createdBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新人ID")
    private Long updatedBy;

    @TableLogic
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    @Schema(description = "是否删除（0=未删除，1=已删除）")
    private Integer deleted;
}
