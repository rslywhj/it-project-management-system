package com.pm.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "模板请求")
public class TemplateRequest {

    @NotBlank(message = "模板名称不能为空")
    @Schema(description = "模板名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "模板说明")
    private String description;

    @Schema(description = "类型（project/document/task/requirement）")
    private String type;

    @Schema(description = "模板内容（JSON格式）")
    private String content;
}
