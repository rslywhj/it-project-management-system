package com.pm.lowcode.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "表单实例详情")
public class FormInstanceVO {

    @Schema(description = "实例ID")
    private Long id;

    @Schema(description = "表单定义ID")
    private Long formDefinitionId;

    @Schema(description = "表单名称")
    private String formName;

    @Schema(description = "JSON Schema")
    private String schemaJson;

    @Schema(description = "关联业务实体ID")
    private Long entityId;

    @Schema(description = "业务实体类型")
    private String entityType;

    @Schema(description = "表单数据")
    private String dataJson;

    @Schema(description = "流程实例ID")
    private String processInstanceId;

    @Schema(description = "任务ID")
    private String taskId;

    @Schema(description = "提交人ID")
    private Long submittedBy;

    @Schema(description = "提交时间")
    private LocalDateTime submittedAt;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
