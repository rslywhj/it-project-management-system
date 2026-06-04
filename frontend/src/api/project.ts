import service from './index'
import type { PageParams, PageResult } from '@/types/api'
import type { Project, ProjectMember } from '@/types/project'

/** 创建项目 */
export async function createProject(data: Partial<Project>): Promise<Project> {
  return service.post('/projects', data)
}

/** 获取项目列表 */
export async function getProjectList(params: PageParams & { status?: string; keyword?: string }): Promise<PageResult<Project>> {
  return service.get('/projects', { params })
}

/** 获取项目详情 */
export async function getProjectDetail(id: number): Promise<Project> {
  return service.get(`/projects/${id}`)
}

/** 更新项目 */
export async function updateProject(id: number, data: Partial<Project>): Promise<Project> {
  return service.put(`/projects/${id}`, data)
}

/** 删除项目 */
export async function deleteProject(id: number): Promise<void> {
  return service.delete(`/projects/${id}`)
}

/** 获取项目成员列表 */
export async function getProjectMembers(projectId: number): Promise<ProjectMember[]> {
  return service.get(`/projects/${projectId}/members`)
}

/** 添加项目成员（query 参数） */
export async function addProjectMember(projectId: number, data: { userId: number; role: string }): Promise<ProjectMember> {
  return service.post(`/projects/${projectId}/members`, null, { params: data })
}

/** 移除项目成员 */
export async function removeProjectMember(projectId: number, userId: number): Promise<void> {
  return service.delete(`/projects/${projectId}/members/${userId}`)
}
