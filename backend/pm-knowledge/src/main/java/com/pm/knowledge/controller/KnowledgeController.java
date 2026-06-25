package com.pm.knowledge.controller;

import com.pm.common.result.PageResult;
import com.pm.common.result.Result;
import com.pm.knowledge.dto.*;
import com.pm.knowledge.service.KnowledgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "知识库", description = "文档管理、知识沉淀、模板库")
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    // ==================== 分类管理 ====================

    @PostMapping("/projects/{projectId}/knowledge-categories")
    @Operation(summary = "创建分类")
    public Result<CategoryVO> createCategory(@PathVariable Long projectId,
                                              @Valid @RequestBody CategoryRequest request) {
        return Result.ok(knowledgeService.createCategory(projectId, request));
    }

    @GetMapping("/projects/{projectId}/knowledge-categories")
    @Operation(summary = "分类树")
    public Result<List<CategoryVO>> getCategoryTree(@PathVariable Long projectId) {
        return Result.ok(knowledgeService.getCategoryTree(projectId));
    }

    @PutMapping("/knowledge-categories/{categoryId}")
    @Operation(summary = "更新分类")
    public Result<CategoryVO> updateCategory(@PathVariable Long categoryId,
                                              @Valid @RequestBody CategoryRequest request) {
        return Result.ok(knowledgeService.updateCategory(categoryId, request));
    }

    @DeleteMapping("/knowledge-categories/{categoryId}")
    @Operation(summary = "删除分类")
    public Result<Void> deleteCategory(@PathVariable Long categoryId) {
        knowledgeService.deleteCategory(categoryId);
        return Result.ok();
    }

    // ==================== 文章管理 ====================

    @PostMapping("/projects/{projectId}/articles")
    @Operation(summary = "创建文章")
    public Result<ArticleVO> createArticle(@PathVariable Long projectId,
                                           @Valid @RequestBody ArticleRequest request) {
        return Result.ok(knowledgeService.createArticle(projectId, request));
    }

    @GetMapping("/projects/{projectId}/articles")
    @Operation(summary = "文章列表（分页）")
    public Result<PageResult<ArticleVO>> listArticles(
            @PathVariable Long projectId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "关键词搜索") @RequestParam(required = false) String keyword,
            @Parameter(description = "类别筛选") @RequestParam(required = false) String category,
            @Parameter(description = "状态筛选") @RequestParam(required = false) String status) {
        return Result.ok(knowledgeService.listArticles(projectId, page, size, keyword, category, status));
    }

    @GetMapping("/articles/{articleId}")
    @Operation(summary = "文章详情")
    public Result<ArticleVO> getArticle(@PathVariable Long articleId) {
        return Result.ok(knowledgeService.getArticle(articleId));
    }

    @PutMapping("/articles/{articleId}")
    @Operation(summary = "更新文章")
    public Result<ArticleVO> updateArticle(@PathVariable Long articleId,
                                           @Valid @RequestBody ArticleRequest request) {
        return Result.ok(knowledgeService.updateArticle(articleId, request));
    }

    @PostMapping("/articles/{articleId}/publish")
    @Operation(summary = "发布文章")
    public Result<ArticleVO> publishArticle(@PathVariable Long articleId) {
        return Result.ok(knowledgeService.publishArticle(articleId));
    }

    @PostMapping("/articles/{articleId}/archive")
    @Operation(summary = "归档文章")
    public Result<ArticleVO> archiveArticle(@PathVariable Long articleId) {
        return Result.ok(knowledgeService.archiveArticle(articleId));
    }

    @DeleteMapping("/articles/{articleId}")
    @Operation(summary = "删除文章")
    public Result<Void> deleteArticle(@PathVariable Long articleId) {
        knowledgeService.deleteArticle(articleId);
        return Result.ok();
    }

    @GetMapping("/projects/{projectId}/articles/search")
    @Operation(summary = "搜索文章")
    public Result<List<ArticleVO>> searchArticles(
            @PathVariable Long projectId,
            @Parameter(description = "关键词") @RequestParam String keyword) {
        return Result.ok(knowledgeService.searchArticles(projectId, keyword));
    }

    // ==================== 模板管理 ====================

    @PostMapping("/templates")
    @Operation(summary = "创建模板")
    public Result<TemplateVO> createTemplate(@Valid @RequestBody TemplateRequest request) {
        return Result.ok(knowledgeService.createTemplate(request));
    }

    @GetMapping("/templates")
    @Operation(summary = "模板列表")
    public Result<PageResult<TemplateVO>> listTemplates(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "类型筛选") @RequestParam(required = false) String type) {
        return Result.ok(knowledgeService.listTemplates(page, size, type));
    }

    @GetMapping("/templates/{templateId}")
    @Operation(summary = "模板详情")
    public Result<TemplateVO> getTemplate(@PathVariable Long templateId) {
        return Result.ok(knowledgeService.getTemplate(templateId));
    }

    @PutMapping("/templates/{templateId}")
    @Operation(summary = "更新模板")
    public Result<TemplateVO> updateTemplate(@PathVariable Long templateId,
                                             @Valid @RequestBody TemplateRequest request) {
        return Result.ok(knowledgeService.updateTemplate(templateId, request));
    }

    @DeleteMapping("/templates/{templateId}")
    @Operation(summary = "删除模板")
    public Result<Void> deleteTemplate(@PathVariable Long templateId) {
        knowledgeService.deleteTemplate(templateId);
        return Result.ok();
    }
}
