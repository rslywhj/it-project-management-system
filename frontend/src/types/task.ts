/** 任务状态 */
export type TaskStatus = 'todo' | 'in_progress' | 'done'

/** 任务类型 */
export type TaskType = 'dev' | 'test' | 'design' | 'research' | 'deploy' | 'training' | 'review' | 'other'

/** 任务信息（对齐后端 TaskVO） */
export interface Task {
  id: number
  projectId: number
  parentTaskId?: number
  requirementId?: number
  promotionUnitId?: number
  title: string
  description?: string
  type: TaskType
  status: TaskStatus
  priority: string
  assignedTo?: number
  plannedStart?: string
  plannedEnd?: string
  actualStart?: string
  actualEnd?: string
  completionRate: number
  wbsCode?: string
  children?: Task[]
  dependsOnTaskIds?: number[]
  createdAt: string
}

/** 任务创建请求 */
export interface TaskCreateRequest {
  title: string
  description?: string
  type?: TaskType
  priority?: string
  assignedTo?: number
  requirementId?: number
  plannedStart?: string
  plannedEnd?: string
}

/** 进度更新请求（对齐后端：completionRate + remark） */
export interface ProgressUpdateRequest {
  completionRate: number
  remark?: string
}

/** 依赖请求 */
export interface DependencyRequest {
  dependsOnTaskId: number
  dependencyType?: 'FS' | 'FF' | 'SS' | 'SF'
}
