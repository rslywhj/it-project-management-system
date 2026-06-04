package com.pm.lowcode.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "启动流程请求")
public class ProcessStartRequest {

    @NotBlank(message = "流程定义Key不能为空")
    @Schema(description = "流程定义Key", requiredMode = Schema.RequiredMode.REQUIRED)
    private String processDefinitionKey;

    @NotNull(message = "业务实体ID不能为空")
    @Schema(description = "业务实体ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long entityId;

    @NotBlank(message = "业务实体类型不能为空")
    @Schema(description = "业务实体类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private String entityType;

    @Schema(description = "表单数据（JSON格式）")
    private String formDataJson;

    @Schema(description = "表单定义ID")
    private Long formDefinitionId;
}
