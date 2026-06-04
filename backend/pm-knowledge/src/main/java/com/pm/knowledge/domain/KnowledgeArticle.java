package com.pm.knowledge.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pm.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("knowledge_article")
@Schema(description = "知识库文章")
public class KnowledgeArticle extends BaseEntity {

    @Schema(description = "所属项目ID（NULL=全局）")
    private Long projectId;

    @Schema(description = "文章标题")
    private String title;

    @Schema(description = "文章内容")
    private String content;

    @Schema(description = "类别（experience/best_practice/lesson_learned/template/guide）")
    private String category;

    @Schema(description = "标签（逗号分隔）")
    private String tags;

    @Schema(description = "状态（draft/published/archived）")
    private String status;

    @Schema(description = "浏览次数")
    private Integer viewCount;

    @Schema(description = "作者ID")
    private Long authorId;
}
