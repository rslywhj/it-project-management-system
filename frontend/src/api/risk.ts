import service from './index'
import type { PageParams, PageResult } from '@/types/api'
import type {
  Risk, RiskCreateRequest, RiskStatusUpdateRequest,
  Issue, IssueCreateRequest, IssueStatusUpdateRequest,
} from '@/types/risk'

// ==================== 风险管理 ====================

export function createRisk(projectId: number, data: RiskCreateRequest) {
  return service.post<any, Risk>(`/projects/${projectId}/risks`, data)
}

export function getRiskList(projectId: number, params?: PageParams & { status?: string; riskLevel?: string; category?: string }) {
  return service.get<any, PageResult<Risk>>(`/projects/${projectId}/risks`, { params })
}

export function getRiskDetail(id: number) {
  return service.get<any, Risk>(`/risks/${id}`)
}

export function updateRisk(id: number, data: RiskCreateRequest) {
  return service.put<any, Risk>(`/risks/${id}`, data)
}

export function updateRiskStatus(id: number, data: RiskStatusUpdateRequest) {
  return service.put<any, Risk>(`/risks/${id}/status`, data)
}

export function deleteRisk(id: number) {
  return service.delete<any, void>(`/risks/${id}`)
}

export function getOverdueRisks(projectId: number) {
  return service.get<any, Risk[]>(`/projects/${projectId}/risks/overdue`)
}

// ==================== 问题管理 ====================

export function createIssue(projectId: number, data: IssueCreateRequest) {
  return service.post<any, Issue>(`/projects/${projectId}/issues`, data)
}

export function getIssueList(projectId: number, params?: PageParams & { status?: string; severity?: string; category?: string }) {
  return service.get<any, PageResult<Issue>>(`/projects/${projectId}/issues`, { params })
}

export function getIssueDetail(id: number) {
  return service.get<any, Issue>(`/issues/${id}`)
}

export function updateIssue(id: number, data: IssueCreateRequest) {
  return service.put<any, Issue>(`/issues/${id}`, data)
}

export function updateIssueStatus(id: number, data: IssueStatusUpdateRequest) {
  return service.put<any, Issue>(`/issues/${id}/status`, data)
}

export function deleteIssue(id: number) {
  return service.delete<any, void>(`/issues/${id}`)
}

export function getOverdueIssues(projectId: number) {
  return service.get<any, Issue[]>(`/projects/${projectId}/issues/overdue`)
}
