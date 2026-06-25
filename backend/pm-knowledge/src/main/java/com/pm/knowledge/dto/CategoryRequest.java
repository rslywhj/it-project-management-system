package com.pm.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "知识库分类请求")
public class CategoryRequest {

    @NotBlank(message = "分类名称不能为空")
    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "父分类ID")
    private Long parentId;

    @Schema(description = "排序序号")
    private Integer sortOrder = 0;

    @Schema(description = "图标")
    private String icon;
}
