/** 里程碑状态 */
export type MilestoneStatus = 'pending' | 'in_progress' | 'completed' | 'delayed'

/** 里程碑信息（对齐后端 MilestoneVO） */
export interface Milestone {
  id: number
  projectId: number
  name: string
  description?: string
  plannedDate: string
  actualDate?: string
  status: MilestoneStatus
  sortOrder: number
  createdAt: string
}

/** 里程碑创建请求 */
export interface MilestoneCreateRequest {
  name: string
  description?: string
  plannedDate: string
  sortOrder?: number
}

/** 里程碑更新请求 */
export interface MilestoneUpdateRequest {
  name?: string
  description?: string
  plannedDate?: string
  actualDate?: string
  status?: MilestoneStatus
  sortOrder?: number
}
