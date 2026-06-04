import service from './index'
import type { ApiResult, PageParams, PageResult } from '@/types/api'
import type { Project, ProjectMember } from '@/types/project'

/** 创建项目 */
export function createProject(data: Partial<Project>) {
  return service.post<ApiResult<Project>>('/projects', data)
}

/** 获取项目列表 */
export function getProjectList(params: PageParams & { status?: string; keyword?: string }) {
  return service.get<ApiResult<PageResult<Project>>>('/projects', { params })
}

/** 获取项目详情 */
export function getProjectDetail(id: string) {
  return service.get<ApiResult<Project>>(`/projects/${id}`)
}

/** 更新项目 */
export function updateProject(id: string, data: Partial<Project>) {
  return service.put<ApiResult<Project>>(`/projects/${id}`, data)
}

/** 删除项目 */
export function deleteProject(id: string) {
  return service.delete<ApiResult<void>>(`/projects/${id}`)
}

/** 获取项目成员列表 */
export function getProjectMembers(projectId: string) {
  return service.get<ApiResult<ProjectMember[]>>(`/projects/${projectId}/members`)
}

/** 添加项目成员 */
export function addProjectMember(projectId: string, data: { userId: string; role: string }) {
  return service.post<ApiResult<ProjectMember>>(`/projects/${projectId}/members`, data)
}

/** 更新成员角色 */
export function updateProjectMember(projectId: string, userId: string, role: string) {
  return service.put<ApiResult<void>>(`/projects/${projectId}/members/${userId}`, { role })
}

/** 移除项目成员 */
export function removeProjectMember(projectId: string, userId: string) {
  return service.delete<ApiResult<void>>(`/projects/${projectId}/members/${userId}`)
}
