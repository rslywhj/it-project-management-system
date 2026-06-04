import service from './index'
import type { PageParams, PageResult } from '@/types/api'
import type { KnowledgeCategory, KnowledgeCategoryCreateRequest, KnowledgeArticle, KnowledgeArticleCreateRequest, ArticleSearchResult } from '@/types/knowledge'

// ==================== 分类管理 ====================

export function createCategory(projectId: number, data: KnowledgeCategoryCreateRequest) {
  return service.post<any, KnowledgeCategory>(`/projects/${projectId}/knowledge-categories`, data)
}

export function getCategoryTree(projectId: number) {
  return service.get<any, KnowledgeCategory[]>(`/projects/${projectId}/knowledge-categories`)
}

export function updateCategory(id: number, data: Partial<KnowledgeCategory>) {
  return service.put<any, KnowledgeCategory>(`/knowledge-categories/${id}`, data)
}

export function deleteCategory(id: number) {
  return service.delete<any, void>(`/knowledge-categories/${id}`)
}

// ==================== 文章管理 ====================

export function createArticle(projectId: number, data: KnowledgeArticleCreateRequest) {
  return service.post<any, KnowledgeArticle>(`/projects/${projectId}/articles`, data)
}

export function getArticleList(projectId: number, params?: PageParams & { categoryId?: number; status?: string; keyword?: string; tags?: string }) {
  return service.get<any, PageResult<KnowledgeArticle>>(`/projects/${projectId}/articles`, { params })
}

export function getArticleDetail(id: number) {
  return service.get<any, KnowledgeArticle>(`/articles/${id}`)
}

export function updateArticle(id: number, data: Partial<KnowledgeArticle>) {
  return service.put<any, KnowledgeArticle>(`/articles/${id}`, data)
}

export function deleteArticle(id: number) {
  return service.delete<any, void>(`/articles/${id}`)
}

export function publishArticle(id: number) {
  return service.put<any, KnowledgeArticle>(`/articles/${id}/publish`)
}

export function archiveArticle(id: number) {
  return service.put<any, KnowledgeArticle>(`/articles/${id}/archive`)
}

// ==================== 搜索 ====================

export function searchArticles(projectId: number, keyword: string) {
  return service.get<any, ArticleSearchResult[]>(`/projects/${projectId}/articles/search`, { params: { keyword } })
}
