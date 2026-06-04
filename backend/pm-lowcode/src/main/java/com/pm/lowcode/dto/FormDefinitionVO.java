package com.pm.lowcode.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "表单定义详情")
public class FormDefinitionVO {

    @Schema(description = "表单定义ID")
    private Long id;

    @Schema(description = "表单标识")
    private String formKey;

    @Schema(description = "表单名称")
    private String name;

    @Schema(description = "JSON Schema")
    private String schemaJson;

    @Schema(description = "版本号")
    private Integer version;

    @Schema(description = "绑定的业务实体类型")
    private String bindableEntity;

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
