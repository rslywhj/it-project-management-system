import service from './index'
import type { ApiResult, PageParams, PageResult } from '@/types/api'
import type { Milestone, MilestoneCreateRequest, MilestoneUpdateRequest } from '@/types/milestone'

/** 创建里程碑 */
export function createMilestone(projectId: number, data: MilestoneCreateRequest) {
  return service.post<ApiResult<Milestone>>(`/projects/${projectId}/milestones`, data)
}

/** 获取里程碑列表 */
export function getMilestoneList(
  projectId: number,
  params: PageParams & { status?: string },
) {
  return service.get<ApiResult<PageResult<Milestone>>>(`/projects/${projectId}/milestones`, { params })
}

/** 获取里程碑详情 */
export function getMilestoneDetail(id: number) {
  return service.get<ApiResult<Milestone>>(`/milestones/${id}`)
}

/** 更新里程碑 */
export function updateMilestone(id: number, data: MilestoneUpdateRequest) {
  return service.put<ApiResult<Milestone>>(`/milestones/${id}`, data)
}

/** 删除里程碑 */
export function deleteMilestone(id: number) {
  return service.delete<ApiResult<void>>(`/milestones/${id}`)
}
