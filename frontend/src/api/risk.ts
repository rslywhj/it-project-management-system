import service from './index'
import type { PageParams, PageResult } from '@/types/api'
import type { Risk, RiskCreateRequest, Issue, IssueCreateRequest } from '@/types/risk'

// ==================== 风险管理 ====================

export function createRisk(projectId: number, data: RiskCreateRequest) {
  return service.post<any, Risk>(`/projects/${projectId}/risks`, data)
}

export function getRiskList(projectId: number, params: PageParams & { status?: string; level?: string; category?: string }) {
  return service.get<any, PageResult<Risk>>(`/projects/${projectId}/risks`, { params })
}

export function getRiskDetail(id: number) {
  return service.get<any, Risk>(`/risks/${id}`)
}

export function updateRisk(id: number, data: Partial<Risk>) {
  return service.put<any, Risk>(`/risks/${id}`, data)
}

export function deleteRisk(id: number) {
  return service.delete<any, void>(`/risks/${id}`)
}

export function closeRisk(id: number, reason?: string) {
  return service.put<any, Risk>(`/risks/${id}/close`, { reason })
}

// ==================== 问题管理 ====================

export function createIssue(projectId: number, data: IssueCreateRequest) {
  return service.post<any, Issue>(`/projects/${projectId}/issues`, data)
}

export function getIssueList(projectId: number, params: PageParams & { status?: string; priority?: string; category?: string }) {
  return service.get<any, PageResult<Issue>>(`/projects/${projectId}/issues`, { params })
}

export function getIssueDetail(id: number) {
  return service.get<any, Issue>(`/issues/${id}`)
}

export function updateIssue(id: number, data: Partial<Issue>) {
  return service.put<any, Issue>(`/issues/${id}`, data)
}

export function deleteIssue(id: number) {
  return service.delete<any, void>(`/issues/${id}`)
}

export function resolveIssue(id: number, resolution: string) {
  return service.put<any, Issue>(`/issues/${id}/resolve`, { resolution })
}

export function closeIssue(id: number) {
  return service.put<any, Issue>(`/issues/${id}/close`)
}
