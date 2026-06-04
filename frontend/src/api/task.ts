import service from './index'
import type { ApiResult, PageParams, PageResult } from '@/types/api'
import type { Task, TaskCreateRequest, ProgressUpdateRequest, DependencyRequest } from '@/types/task'

/** 创建任务 */
export function createTask(projectId: number, data: TaskCreateRequest) {
  return service.post<ApiResult<Task>>(`/projects/${projectId}/tasks`, data)
}

/** 获取任务列表 */
export function getTaskList(
  projectId: number,
  params: PageParams & { keyword?: string; status?: string; assignedTo?: number },
) {
  return service.get<ApiResult<PageResult<Task>>>(`/projects/${projectId}/tasks`, { params })
}

/** 获取任务详情 */
export function getTaskDetail(id: number) {
  return service.get<ApiResult<Task>>(`/tasks/${id}`)
}

/** 更新任务 */
export function updateTask(id: number, data: Partial<Task>) {
  return service.put<ApiResult<Task>>(`/tasks/${id}`, data)
}

/** 更新任务进度 */
export function updateTaskProgress(id: number, data: ProgressUpdateRequest) {
  return service.put<ApiResult<void>>(`/tasks/${id}/progress`, data)
}

/** 删除任务 */
export function deleteTask(id: number) {
  return service.delete<ApiResult<void>>(`/tasks/${id}`)
}

/** 创建子任务 */
export function createSubtask(parentTaskId: number, data: TaskCreateRequest) {
  return service.post<ApiResult<Task>>(`/tasks/${parentTaskId}/subtasks`, data)
}

/** 获取子任务列表 */
export function getSubtasks(parentTaskId: number) {
  return service.get<ApiResult<Task[]>>(`/tasks/${parentTaskId}/subtasks`)
}

/** 获取 WBS 完整树 */
export function getWbsTree(projectId: number) {
  return service.get<ApiResult<Task[]>>(`/projects/${projectId}/tasks/wbs`)
}

/** 添加任务依赖 */
export function addDependency(taskId: number, data: DependencyRequest) {
  return service.post<ApiResult<void>>(`/tasks/${taskId}/dependencies`, data)
}
