/** 项目状态 */
export type ProjectStatus = 'planning' | 'active' | 'suspended' | 'completed' | 'cancelled'

/** 项目信息 */
export interface Project {
  id: string
  name: string
  code: string
  description?: string
  status: ProjectStatus
  projectManagerId: string
  projectManagerName: string
  startDate?: string
  endDate?: string
  createdAt: string
  updatedAt: string
}

/** 项目成员 */
export interface ProjectMember {
  id: string
  userId: string
  username: string
  realName: string
  role: string
  joinedAt: string
}
