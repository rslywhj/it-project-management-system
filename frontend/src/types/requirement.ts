/** 需求状态 */
export type RequirementStatus =
  | 'draft'
  | 'reviewing'
  | 'approved'
  | 'rejected'
  | 'scheduled'
  | 'done'

/** 需求优先级 */
export type Priority = 'critical' | 'high' | 'medium' | 'low'

/** 需求信息（对齐后端 RequirementVO） */
export interface Requirement {
  id: number
  projectId: number
  title: string
  description?: string
  acceptanceCriteria?: string
  priority: Priority
  status: RequirementStatus
  source?: string
  category?: string
  assignedTo?: number
  estimatedHours?: number
  milestoneId?: number
  createdBy: number
  createdAt: string
  updatedAt: string
}

/** 需求创建请求 */
export interface RequirementCreateRequest {
  title: string
  description?: string
  acceptanceCriteria?: string
  priority?: Priority
  source?: string
  category?: string
  assignedTo?: number
  estimatedHours?: number
}

/** 状态流转请求（对齐后端：targetStatus + remark） */
export interface StatusUpdateRequest {
  targetStatus: RequirementStatus
  remark?: string
}
