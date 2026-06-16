/** 项目状态 */
export type ProjectStatus = 'planning' | 'active' | 'suspended' | 'completed' | 'cancelled'

/** 项目信息（对齐后端 ProjectVO） */
export interface Project {
  id: number
  projectCode: string
  name: string
  description?: string
  type: string
  status: ProjectStatus
  promotionEnabled: boolean
  startDate?: string
  endDate?: string
  orgId?: number
  projectManagerId: number
  createdAt: string
  memberCount?: number
}

/** 项目创建请求 */
export interface ProjectCreateRequest {
  projectCode: string
  name: string
  description?: string
  type: string
  promotionEnabled?: boolean
  startDate?: string
  endDate?: string
  orgId?: number
}

/** 项目更新请求 */
export interface ProjectUpdateRequest {
  name?: string
  description?: string
  status?: ProjectStatus
  startDate?: string
  endDate?: string
}

/** 项目成员 */
export interface ProjectMember {
  id: number
  projectId: number
  userId: number
  username: string
  realName: string
  role: string
  joinedAt: string
}
