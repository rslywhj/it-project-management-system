import service from './index'
import type { PageParams, PageResult } from '@/types/api'
import type { KnowledgeArticle, KnowledgeArticleCreateRequest, KnowledgeTemplate, KnowledgeTemplateCreateRequest } from '@/types/knowledge'

// ==================== 文章管理 ====================

export function createArticle(projectId: number, data: KnowledgeArticleCreateRequest) {
  return service.post<any, KnowledgeArticle>(`/projects/${projectId}/articles`, data)
}

export function getArticleList(projectId: number, params?: PageParams & { category?: string; status?: string; keyword?: string }) {
  return service.get<any, PageResult<KnowledgeArticle>>(`/projects/${projectId}/articles`, { params })
}

export function getArticleDetail(id: number) {
  return service.get<any, KnowledgeArticle>(`/articles/${id}`)
}

export function updateArticle(id: number, data: KnowledgeArticleCreateRequest) {
  return service.put<any, KnowledgeArticle>(`/articles/${id}`, data)
}

export function deleteArticle(id: number) {
  return service.delete<any, void>(`/articles/${id}`)
}

export function publishArticle(id: number) {
  return service.post<any, void>(`/articles/${id}/publish`)
}

export function archiveArticle(id: number) {
  return service.post<any, void>(`/articles/${id}/archive`)
}

export function searchArticles(projectId: number, keyword: string) {
  return service.get<any, KnowledgeArticle[]>(`/projects/${projectId}/articles/search`, { params: { keyword } })
}

// ==================== 模板管理 ====================

export function createTemplate(data: KnowledgeTemplateCreateRequest) {
  return service.post<any, KnowledgeTemplate>(`/templates`, data)
}

export function getTemplateList(params?: PageParams & { type?: string }) {
  return service.get<any, PageResult<KnowledgeTemplate>>(`/templates`, { params })
}

export function getTemplateDetail(id: number) {
  return service.get<any, KnowledgeTemplate>(`/templates/${id}`)
}

export function updateTemplate(id: number, data: KnowledgeTemplateCreateRequest) {
  return service.put<any, KnowledgeTemplate>(`/templates/${id}`, data)
}

export function deleteTemplate(id: number) {
  return service.delete<any, void>(`/templates/${id}`)
}
