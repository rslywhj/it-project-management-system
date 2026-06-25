package com.pm.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "知识库分类响应")
public class CategoryVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "项目ID")
    private Long projectId;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "父分类ID")
    private Long parentId;

    @Schema(description = "排序序号")
    private Integer sortOrder;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "文章数量")
    private Integer articleCount;

    @Schema(description = "子分类")
    private List<CategoryVO> children;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
