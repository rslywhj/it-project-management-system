import service from './index'
import type { PageParams, PageResult } from '@/types/api'
import type { Resource, ResourceRequest, WorkLog, WorkLogRequest, WorkloadReport } from '@/types/resource'

// ==================== 资源池管理 ====================

export function createResource(projectId: number, data: ResourceRequest) {
  return service.post<any, Resource>(`/projects/${projectId}/resources`, data)
}

export function getResourceList(projectId: number, params?: PageParams & { availability?: string }) {
  return service.get<any, PageResult<Resource>>(`/projects/${projectId}/resources`, { params })
}

export function getResource(id: number) {
  return service.get<any, Resource>(`/resources/${id}`)
}

export function updateResource(id: number, data: ResourceRequest) {
  return service.put<any, Resource>(`/resources/${id}`, data)
}

export function deleteResource(id: number) {
  return service.delete<any, void>(`/resources/${id}`)
}

// ==================== 工时填报 ====================

export function createWorkLog(projectId: number, data: WorkLogRequest) {
  return service.post<any, WorkLog>(`/projects/${projectId}/work-logs`, data)
}

export function getWorkLogList(projectId: number, params?: PageParams & { userId?: number; taskId?: number; startDate?: string; endDate?: string }) {
  return service.get<any, PageResult<WorkLog>>(`/projects/${projectId}/work-logs`, { params })
}

export function updateWorkLog(id: number, data: WorkLogRequest) {
  return service.put<any, WorkLog>(`/work-logs/${id}`, data)
}

export function deleteWorkLog(id: number) {
  return service.delete<any, void>(`/work-logs/${id}`)
}

// ==================== 资源负载 ====================

export function getWorkloadReport(projectId: number) {
  return service.get<any, WorkloadReport>(`/projects/${projectId}/resources/workload`)
}
