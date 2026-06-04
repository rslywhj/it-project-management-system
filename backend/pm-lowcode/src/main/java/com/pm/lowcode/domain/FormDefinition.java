package com.pm.lowcode.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pm.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("form_definition")
@Schema(description = "表单定义")
public class FormDefinition extends BaseEntity {

    @Schema(description = "表单标识")
    private String formKey;

    @Schema(description = "表单名称")
    private String name;

    @Schema(description = "VForm3生成的JSON Schema")
    private String schemaJson;

    @Schema(description = "版本号")
    private Integer version;

    @Schema(description = "绑定的业务实体类型")
    private String bindableEntity;

    @Schema(description = "所属项目ID（NULL=全局模板）")
    private Long projectId;

    @Schema(description = "状态（draft/active/deprecated）")
    private String status;
}
