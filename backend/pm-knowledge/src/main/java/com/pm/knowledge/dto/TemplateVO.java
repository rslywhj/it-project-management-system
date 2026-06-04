package com.pm.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "模板详情")
public class TemplateVO {

    @Schema(description = "模板ID")
    private Long id;

    @Schema(description = "模板名称")
    private String name;

    @Schema(description = "模板说明")
    private String description;

    @Schema(description = "类型")
    private String type;

    @Schema(description = "模板内容")
    private String content;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "是否系统内置")
    private Integer isSystem;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
