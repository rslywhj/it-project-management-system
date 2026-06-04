package com.pm.lowcode.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("form_instance")
@Schema(description = "表单实例")
public class FormInstance {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "关联表单定义ID")
    private Long formDefinitionId;

    @Schema(description = "关联业务实体ID")
    private Long entityId;

    @Schema(description = "业务实体类型")
    private String entityType;

    @Schema(description = "用户填写的表单数据（JSON格式）")
    private String dataJson;

    @Schema(description = "关联Flowable流程实例ID")
    private String processInstanceId;

    @Schema(description = "关联Flowable任务ID")
    private String taskId;

    @Schema(description = "提交人ID")
    private Long submittedBy;

    @Schema(description = "提交时间")
    private LocalDateTime submittedAt;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
