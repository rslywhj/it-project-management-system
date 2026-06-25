package com.pm.knowledge.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pm.auth.annotation.RequirePermission;
import com.pm.common.exception.BusinessException;
import com.pm.common.result.PageResult;
import com.pm.common.util.UserContext;
import com.pm.knowledge.domain.KnowledgeArticle;
import com.pm.knowledge.domain.KnowledgeCategory;
import com.pm.knowledge.domain.Template;
import com.pm.knowledge.dto.*;
import com.pm.knowledge.mapper.KnowledgeArticleMapper;
import com.pm.knowledge.mapper.KnowledgeCategoryMapper;
import com.pm.knowledge.mapper.TemplateMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeService {

    private final KnowledgeArticleMapper articleMapper;
    private final KnowledgeCategoryMapper categoryMapper;
    private final TemplateMapper templateMapper;

    // ==================== 分类管理 ====================

    @Transactional
    @RequirePermission("knowledge:create")
    public CategoryVO createCategory(Long projectId, CategoryRequest request) {
        KnowledgeCategory category = new KnowledgeCategory();
        BeanUtils.copyProperties(request, category);
        category.setProjectId(projectId);
        categoryMapper.insert(category);
        return toCategoryVO(category, 0);
    }

    @RequirePermission("knowledge:view")
    public List<CategoryVO> getCategoryTree(Long projectId) {
        // 查询所有分类
        List<KnowledgeCategory> allCategories = categoryMapper.selectList(
                new LambdaQueryWrapper<KnowledgeCategory>()
                        .eq(KnowledgeCategory::getProjectId, projectId)
                        .orderByAsc(KnowledgeCategory::getSortOrder));

        // 查询每个分类的文章数量
        Map<Long, Long> articleCountMap = allCategories.stream()
                .collect(Collectors.toMap(
                        KnowledgeCategory::getId,
                        cat -> articleMapper.selectCount(
                                new LambdaQueryWrapper<KnowledgeArticle>()
                                        .eq(KnowledgeArticle::getProjectId, projectId)
                                        .eq(KnowledgeArticle::getCategory, cat.getName()))));

        // 构建树结构
        List<CategoryVO> allVOs = allCategories.stream()
                .map(cat -> {
                    CategoryVO vo = toCategoryVO(cat, articleCountMap.getOrDefault(cat.getId(), 0L).intValue());
                    return vo;
                })
                .collect(Collectors.toList());

        return buildCategoryTree(allVOs, null);
    }

    @Transactional
    @RequirePermission("knowledge:edit")
    public CategoryVO updateCategory(Long categoryId, CategoryRequest request) {
        KnowledgeCategory category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException(1113, "分类不存在");
        }
        BeanUtils.copyProperties(request, category, "id", "projectId", "createdAt", "createdBy", "isDeleted");
        categoryMapper.updateById(category);
        return toCategoryVO(category, 0);
    }

    @Transactional
    @RequirePermission("knowledge:delete")
    public void deleteCategory(Long categoryId) {
        KnowledgeCategory category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException(1113, "分类不存在");
        }
        // 检查是否有子分类
        Long childCount = categoryMapper.selectCount(
                new LambdaQueryWrapper<KnowledgeCategory>()
                        .eq(KnowledgeCategory::getParentId, categoryId));
        if (childCount > 0) {
            throw new BusinessException(1114, "请先删除子分类");
        }
        categoryMapper.deleteById(categoryId);
    }

    // ==================== 文章管理 ====================

    @Transactional
    @RequirePermission("knowledge:create")
    public ArticleVO createArticle(Long projectId, ArticleRequest request) {
        KnowledgeArticle article = new KnowledgeArticle();
        BeanUtils.copyProperties(request, article);
        article.setProjectId(projectId);
        article.setStatus("draft");
        article.setViewCount(0);
        article.setAuthorId(UserContext.getUserId());
        articleMapper.insert(article);
        return toArticleVO(article);
    }

    @RequirePermission("knowledge:view")
    public PageResult<ArticleVO> listArticles(Long projectId, int page, int size,
                                               String keyword, String category, String status) {
        LambdaQueryWrapper<KnowledgeArticle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(projectId != null, KnowledgeArticle::getProjectId, projectId)
                .like(StringUtils.hasText(keyword), KnowledgeArticle::getTitle, keyword)
                .eq(StringUtils.hasText(category), KnowledgeArticle::getCategory, category)
                .eq(StringUtils.hasText(status), KnowledgeArticle::getStatus, status)
                .orderByDesc(KnowledgeArticle::getCreatedAt);

        Page<KnowledgeArticle> result = articleMapper.selectPage(new Page<>(page, size), wrapper);
        List<ArticleVO> voList = result.getRecords().stream()
                .map(this::toArticleVO)
                .collect(Collectors.toList());
        return PageResult.of(voList, result.getTotal(), page, size);
    }

    @RequirePermission("knowledge:view")
    public ArticleVO getArticle(Long articleId) {
        KnowledgeArticle article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BusinessException(1110, "文章不存在");
        }
        // 增加浏览次数
        article.setViewCount(article.getViewCount() + 1);
        articleMapper.updateById(article);
        return toArticleVO(article);
    }

    @Transactional
    @RequirePermission("knowledge:edit")
    public ArticleVO updateArticle(Long articleId, ArticleRequest request) {
        KnowledgeArticle article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BusinessException(1110, "文章不存在");
        }
        BeanUtils.copyProperties(request, article, "id", "projectId", "status", "viewCount",
                "authorId", "createdAt", "createdBy", "isDeleted");
        articleMapper.updateById(article);
        return toArticleVO(article);
    }

    @Transactional
    @RequirePermission("knowledge:edit")
    public ArticleVO publishArticle(Long articleId) {
        KnowledgeArticle article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BusinessException(1110, "文章不存在");
        }
        article.setStatus("published");
        articleMapper.updateById(article);
        return toArticleVO(article);
    }

    @Transactional
    @RequirePermission("knowledge:edit")
    public ArticleVO archiveArticle(Long articleId) {
        KnowledgeArticle article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BusinessException(1110, "文章不存在");
        }
        article.setStatus("archived");
        articleMapper.updateById(article);
        return toArticleVO(article);
    }

    @Transactional
    @RequirePermission("knowledge:delete")
    public void deleteArticle(Long articleId) {
        articleMapper.deleteById(articleId);
    }

    @RequirePermission("knowledge:view")
    public List<ArticleVO> searchArticles(Long projectId, String keyword) {
        LambdaQueryWrapper<KnowledgeArticle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(projectId != null, KnowledgeArticle::getProjectId, projectId)
                .eq(KnowledgeArticle::getStatus, "published")
                .and(w -> w.like(KnowledgeArticle::getTitle, keyword)
                        .or().like(KnowledgeArticle::getTags, keyword))
                .orderByDesc(KnowledgeArticle::getViewCount);
        return articleMapper.selectList(wrapper).stream()
                .map(this::toArticleVO)
                .collect(Collectors.toList());
    }

    // ==================== 模板管理 ====================

    @Transactional
    @RequirePermission("knowledge:create")
    public TemplateVO createTemplate(TemplateRequest request) {
        Template template = new Template();
        BeanUtils.copyProperties(request, template);
        template.setStatus("active");
        template.setIsSystem(0);
        templateMapper.insert(template);
        return toTemplateVO(template);
    }

    @RequirePermission("knowledge:view")
    public PageResult<TemplateVO> listTemplates(int page, int size, String type) {
        LambdaQueryWrapper<Template> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(type), Template::getType, type)
                .eq(Template::getStatus, "active")
                .orderByDesc(Template::getCreatedAt);

        Page<Template> result = templateMapper.selectPage(new Page<>(page, size), wrapper);
        List<TemplateVO> voList = result.getRecords().stream()
                .map(this::toTemplateVO)
                .collect(Collectors.toList());
        return PageResult.of(voList, result.getTotal(), page, size);
    }

    @RequirePermission("knowledge:view")
    public TemplateVO getTemplate(Long templateId) {
        Template template = templateMapper.selectById(templateId);
        if (template == null) {
            throw new BusinessException(1111, "模板不存在");
        }
        return toTemplateVO(template);
    }

    @Transactional
    @RequirePermission("knowledge:edit")
    public TemplateVO updateTemplate(Long templateId, TemplateRequest request) {
        Template template = templateMapper.selectById(templateId);
        if (template == null) {
            throw new BusinessException(1111, "模板不存在");
        }
        if (template.getIsSystem() == 1) {
            throw new BusinessException(1112, "系统内置模板不可修改");
        }
        BeanUtils.copyProperties(request, template, "id", "status", "isSystem",
                "createdAt", "createdBy", "isDeleted");
        templateMapper.updateById(template);
        return toTemplateVO(template);
    }

    @Transactional
    @RequirePermission("knowledge:delete")
    public void deleteTemplate(Long templateId) {
        Template template = templateMapper.selectById(templateId);
        if (template == null) {
            throw new BusinessException(1111, "模板不存在");
        }
        if (template.getIsSystem() == 1) {
            throw new BusinessException(1112, "系统内置模板不可删除");
        }
        templateMapper.deleteById(templateId);
    }

    // ==================== VO 转换 ====================

    private ArticleVO toArticleVO(KnowledgeArticle article) {
        ArticleVO vo = new ArticleVO();
        BeanUtils.copyProperties(article, vo);
        return vo;
    }

    private TemplateVO toTemplateVO(Template template) {
        TemplateVO vo = new TemplateVO();
        BeanUtils.copyProperties(template, vo);
        return vo;
    }

    private CategoryVO toCategoryVO(KnowledgeCategory category, int articleCount) {
        CategoryVO vo = new CategoryVO();
        BeanUtils.copyProperties(category, vo);
        vo.setArticleCount(articleCount);
        return vo;
    }

    private List<CategoryVO> buildCategoryTree(List<CategoryVO> allCategories, Long parentId) {
        return allCategories.stream()
                .filter(cat -> (parentId == null && cat.getParentId() == null)
                        || (parentId != null && parentId.equals(cat.getParentId())))
                .map(cat -> {
                    cat.setChildren(buildCategoryTree(allCategories, cat.getId()));
                    return cat;
                })
                .collect(Collectors.toList());
    }
}
