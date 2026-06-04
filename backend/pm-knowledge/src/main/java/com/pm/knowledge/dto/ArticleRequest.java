package com.pm.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "文章请求")
public class ArticleRequest {

    @NotBlank(message = "文章标题不能为空")
    @Schema(description = "文章标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "文章内容")
    private String content;

    @Schema(description = "类别（experience/best_practice/lesson_learned/template/guide）")
    private String category;

    @Schema(description = "标签（逗号分隔）")
    private String tags;
}
