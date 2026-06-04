package com.pm.knowledge.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pm.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("template")
@Schema(description = "模板")
public class Template extends BaseEntity {

    @Schema(description = "模板名称")
    private String name;

    @Schema(description = "模板说明")
    private String description;

    @Schema(description = "类型（project/document/task/requirement）")
    private String type;

    @Schema(description = "模板内容（JSON格式）")
    private String content;

    @Schema(description = "状态（active/deprecated）")
    private String status;

    @Schema(description = "是否系统内置")
    private Integer isSystem;
}
