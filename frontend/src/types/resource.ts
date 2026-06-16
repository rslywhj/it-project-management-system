/** 资源可用状态 */
export type Availability = 'available' | 'busy' | 'unavailable' | 'on_leave'

/** 工作类型 */
export type WorkType = 'development' | 'testing' | 'meeting' | 'design' | 'review' | 'other'

/** 资源 */
export interface Resource {
  id: number
  userId: number
  username?: string
  realName?: string
  projectId: number
  skillTags?: string
  availability: Availability
  workloadPercent?: number
  capacityHoursPerWeek?: number
  remark?: string
  createdAt: string
}

/** 资源创建/更新请求 */
export interface ResourceRequest {
  userId: number
  skillTags?: string
  availability?: Availability
  capacityHoursPerWeek?: number
  remark?: string
}

/** 工时记录 */
export interface WorkLog {
  id: number
  projectId: number
  userId: number
  taskId?: number
  taskTitle?: string
  workDate: string
  hours: number
  workType?: WorkType
  description?: string
  createdAt: string
}

/** 工时记录创建/更新请求 */
export interface WorkLogRequest {
  workDate: string
  hours: number
  taskId?: number
  workType?: WorkType
  description?: string
}

/** 资源负载详情 */
export interface ResourceWorkload {
  userId: number
  username: string
  realName: string
  workloadPercent: number
  availability: string
  totalHoursThisWeek: number
  totalHoursThisMonth: number
}

/** 工时统计 */
export interface WorkHoursSummary {
  totalHoursThisWeek: number
  totalHoursThisMonth: number
  averageHoursPerPerson: number
}

/** 资源负载报告 */
export interface WorkloadReport {
  projectId: number
  totalResources: number
  availableResources: number
  overloadedResources: number
  resourceWorkloads: ResourceWorkload[]
  workHoursSummary: WorkHoursSummary
}
