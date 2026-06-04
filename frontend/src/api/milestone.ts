import service from './index'
import type { PageParams, PageResult } from '@/types/api'
import type { Milestone, MilestoneCreateRequest, MilestoneUpdateRequest } from '@/types/milestone'

/** 创建里程碑 */
export async function createMilestone(projectId: number, data: MilestoneCreateRequest): Promise<Milestone> {
  return service.post(`/projects/${projectId}/milestones`, data)
}

/** 获取里程碑列表 */
export async function getMilestoneList(
  projectId: number,
  params: PageParams & { status?: string },
): Promise<PageResult<Milestone>> {
  return service.get(`/projects/${projectId}/milestones`, { params })
}

/** 获取里程碑详情 */
export async function getMilestoneDetail(id: number): Promise<Milestone> {
  return service.get(`/milestones/${id}`)
}

/** 更新里程碑 */
export async function updateMilestone(id: number, data: MilestoneUpdateRequest): Promise<Milestone> {
  return service.put(`/milestones/${id}`, data)
}

/** 删除里程碑 */
export async function deleteMilestone(id: number): Promise<void> {
  return service.delete(`/milestones/${id}`)
}
