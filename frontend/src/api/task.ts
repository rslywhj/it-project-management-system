import service from './index'
import type { PageParams, PageResult } from '@/types/api'
import type { Task, TaskCreateRequest, ProgressUpdateRequest, DependencyRequest } from '@/types/task'

/** 创建任务 */
export async function createTask(projectId: number, data: TaskCreateRequest): Promise<Task> {
  return service.post(`/projects/${projectId}/tasks`, data)
}

/** 获取任务列表 */
export async function getTaskList(
  projectId: number,
  params: PageParams & { keyword?: string; status?: string; assignedTo?: number },
): Promise<PageResult<Task>> {
  return service.get(`/projects/${projectId}/tasks`, { params })
}

/** 获取任务详情 */
export async function getTaskDetail(id: number): Promise<Task> {
  return service.get(`/tasks/${id}`)
}

/** 更新任务 */
export async function updateTask(id: number, data: Partial<Task>): Promise<Task> {
  return service.put(`/tasks/${id}`, data)
}

/** 更新任务进度 */
export async function updateTaskProgress(id: number, data: ProgressUpdateRequest): Promise<void> {
  return service.put(`/tasks/${id}/progress`, data)
}

/** 删除任务 */
export async function deleteTask(id: number): Promise<void> {
  return service.delete(`/tasks/${id}`)
}

/** 创建子任务 */
export async function createSubtask(parentTaskId: number, data: TaskCreateRequest): Promise<Task> {
  return service.post(`/tasks/${parentTaskId}/subtasks`, data)
}

/** 获取子任务列表 */
export async function getSubtasks(parentTaskId: number): Promise<Task[]> {
  return service.get(`/tasks/${parentTaskId}/subtasks`)
}

/** 获取 WBS 完整树 */
export async function getWbsTree(projectId: number): Promise<Task[]> {
  return service.get(`/projects/${projectId}/tasks/wbs`)
}

/** 添加任务依赖 */
export async function addDependency(taskId: number, data: DependencyRequest): Promise<void> {
  return service.post(`/tasks/${taskId}/dependencies`, data)
}
