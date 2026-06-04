package com.pm.lowcode.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "表单定义请求")
public class FormDefinitionRequest {

    @NotBlank(message = "表单标识不能为空")
    @Schema(description = "表单标识", requiredMode = Schema.RequiredMode.REQUIRED)
    private String formKey;

    @NotBlank(message = "表单名称不能为空")
    @Schema(description = "表单名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "JSON Schema不能为空")
    @Schema(description = "VForm3生成的JSON Schema", requiredMode = Schema.RequiredMode.REQUIRED)
    private String schemaJson;

    @Schema(description = "绑定的业务实体类型")
    private String bindableEntity;

    @Schema(description = "所属项目ID（NULL=全局模板）")
    private Long projectId;
}
