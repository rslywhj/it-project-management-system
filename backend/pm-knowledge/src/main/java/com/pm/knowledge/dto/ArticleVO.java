package com.pm.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "文章详情")
public class ArticleVO {

    @Schema(description = "文章ID")
    private Long id;

    @Schema(description = "项目ID")
    private Long projectId;

    @Schema(description = "文章标题")
    private String title;

    @Schema(description = "文章内容")
    private String content;

    @Schema(description = "类别")
    private String category;

    @Schema(description = "标签")
    private String tags;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "浏览次数")
    private Integer viewCount;

    @Schema(description = "作者ID")
    private Long authorId;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
