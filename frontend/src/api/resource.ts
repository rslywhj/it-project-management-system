import service from './index'
import type { PageParams, PageResult } from '@/types/api'
import type { ResourceAllocation, ResourceAllocationCreateRequest, Timesheet, TimesheetCreateRequest, ResourceUtilization } from '@/types/resource'

// ==================== 资源分配 ====================

export function createAllocation(projectId: number, data: ResourceAllocationCreateRequest) {
  return service.post<any, ResourceAllocation>(`/projects/${projectId}/allocations`, data)
}

export function getAllocationList(projectId: number, params?: PageParams & { status?: string; userId?: number }) {
  return service.get<any, PageResult<ResourceAllocation>>(`/projects/${projectId}/allocations`, { params })
}

export function updateAllocation(id: number, data: Partial<ResourceAllocation>) {
  return service.put<any, ResourceAllocation>(`/allocations/${id}`, data)
}

export function deleteAllocation(id: number) {
  return service.delete<any, void>(`/allocations/${id}`)
}

// ==================== 工时记录 ====================

export function createTimesheet(projectId: number, data: TimesheetCreateRequest) {
  return service.post<any, Timesheet>(`/projects/${projectId}/timesheets`, data)
}

export function getTimesheetList(projectId: number, params?: PageParams & { userId?: number; startDate?: string; endDate?: string; status?: string }) {
  return service.get<any, PageResult<Timesheet>>(`/projects/${projectId}/timesheets`, { params })
}

export function updateTimesheet(id: number, data: Partial<Timesheet>) {
  return service.put<any, Timesheet>(`/timesheets/${id}`, data)
}

export function deleteTimesheet(id: number) {
  return service.delete<any, void>(`/timesheets/${id}`)
}

export function submitTimesheet(id: number) {
  return service.put<any, Timesheet>(`/timesheets/${id}/submit`)
}

export function approveTimesheet(id: number) {
  return service.put<any, Timesheet>(`/timesheets/${id}/approve`)
}

export function rejectTimesheet(id: number, reason?: string) {
  return service.put<any, Timesheet>(`/timesheets/${id}/reject`, { reason })
}

// ==================== 资源利用率 ====================

export function getResourceUtilization(projectId: number) {
  return service.get<any, ResourceUtilization[]>(`/projects/${projectId}/resource-utilization`)
}
