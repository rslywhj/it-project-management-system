import service from './index'
import type { PageParams, PageResult } from '@/types/api'
import type { Requirement, RequirementCreateRequest, StatusUpdateRequest } from '@/types/requirement'

/** 创建需求 */
export async function createRequirement(projectId: number, data: RequirementCreateRequest): Promise<Requirement> {
  return service.post(`/projects/${projectId}/requirements`, data)
}

/** 获取需求列表 */
export async function getRequirementList(
  projectId: number,
  params: PageParams & { status?: string; priority?: string; keyword?: string },
): Promise<PageResult<Requirement>> {
  return service.get(`/projects/${projectId}/requirements`, { params })
}

/** 获取需求详情 */
export async function getRequirementDetail(id: number): Promise<Requirement> {
  return service.get(`/requirements/${id}`)
}

/** 更新需求 */
export async function updateRequirement(id: number, data: Partial<Requirement>): Promise<Requirement> {
  return service.put(`/requirements/${id}`, data)
}

/** 需求状态流转 */
export async function updateRequirementStatus(id: number, data: StatusUpdateRequest): Promise<void> {
  return service.put(`/requirements/${id}/status`, data)
}

/** 删除需求 */
export async function deleteRequirement(id: number): Promise<void> {
  return service.delete(`/requirements/${id}`)
}

/** 获取需求池（未排期需求） */
export async function getRequirementPool(projectId: number): Promise<Requirement[]> {
  return service.get(`/projects/${projectId}/requirements/pool`)
}

/** 指派需求负责人（query 参数） */
export async function assignRequirement(id: number, userId: number): Promise<void> {
  return service.post(`/requirements/${id}/assign`, null, { params: { userId } })
}
