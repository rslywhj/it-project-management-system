import service from './index'
import type { ApiResult, PageParams, PageResult } from '@/types/api'
import type { Requirement, RequirementCreateRequest, StatusUpdateRequest } from '@/types/requirement'

/** 创建需求 */
export function createRequirement(projectId: number, data: RequirementCreateRequest) {
  return service.post<ApiResult<Requirement>>(`/projects/${projectId}/requirements`, data)
}

/** 获取需求列表 */
export function getRequirementList(
  projectId: number,
  params: PageParams & { status?: string; priority?: string; keyword?: string },
) {
  return service.get<ApiResult<PageResult<Requirement>>>(`/projects/${projectId}/requirements`, { params })
}

/** 获取需求详情 */
export function getRequirementDetail(id: number) {
  return service.get<ApiResult<Requirement>>(`/requirements/${id}`)
}

/** 更新需求 */
export function updateRequirement(id: number, data: Partial<Requirement>) {
  return service.put<ApiResult<Requirement>>(`/requirements/${id}`, data)
}

/** 需求状态流转 */
export function updateRequirementStatus(id: number, data: StatusUpdateRequest) {
  return service.put<ApiResult<void>>(`/requirements/${id}/status`, data)
}

/** 删除需求 */
export function deleteRequirement(id: number) {
  return service.delete<ApiResult<void>>(`/requirements/${id}`)
}

/** 获取需求池（未排期需求） */
export function getRequirementPool(projectId: number) {
  return service.get<ApiResult<Requirement[]>>(`/projects/${projectId}/requirements/pool`)
}

/** 指派需求负责人 */
export function assignRequirement(id: number, userId: number) {
  return service.post<ApiResult<void>>(`/requirements/${id}/assign`, { userId })
}
