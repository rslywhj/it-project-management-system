package com.pm.lowcode.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "表单实例请求")
public class FormInstanceRequest {

    @NotNull(message = "表单定义ID不能为空")
    @Schema(description = "表单定义ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long formDefinitionId;

    @Schema(description = "关联业务实体ID")
    private Long entityId;

    @Schema(description = "业务实体类型")
    private String entityType;

    @Schema(description = "表单数据（JSON格式）")
    private String dataJson;
}
