package com.pm.lowcode.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("process_instance_ref")
@Schema(description = "流程实例与业务实体关联")
public class ProcessInstanceRef {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "Flowable流程实例ID")
    private String processInstanceId;

    @Schema(description = "业务实体ID")
    private Long entityId;

    @Schema(description = "业务实体类型")
    private String entityType;

    @Schema(description = "状态（running/completed/cancelled）")
    private String status;

    @Schema(description = "发起人ID")
    private Long startedBy;

    @Schema(description = "发起时间")
    private LocalDateTime startedAt;

    @Schema(description = "完成时间")
    private LocalDateTime completedAt;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
