/** 资源分配状态 */
export type AllocationStatus = 'planned' | 'active' | 'completed' | 'released'

/** 工时记录状态 */
export type TimesheetStatus = 'draft' | 'submitted' | 'approved' | 'rejected'

/** 资源分配 */
export interface ResourceAllocation {
  id: number
  projectId: number
  projectName?: string
  userId: number
  userName?: string
  role: string
  allocationPercent: number
  startDate: string
  endDate: string
  status: AllocationStatus
  createdAt: string
}

/** 资源分配创建请求 */
export interface ResourceAllocationCreateRequest {
  userId: number
  role: string
  allocationPercent?: number
  startDate: string
  endDate: string
}

/** 工时记录 */
export interface Timesheet {
  id: number
  projectId: number
  userId: number
  userName?: string
  taskId?: number
  taskTitle?: string
  workDate: string
  hours: number
  description?: string
  status: TimesheetStatus
  approvedBy?: number
  approvedAt?: string
  createdAt: string
}

/** 工时记录创建请求 */
export interface TimesheetCreateRequest {
  taskId?: number
  workDate: string
  hours: number
  description?: string
}

/** 资源利用率 */
export interface ResourceUtilization {
  userId: number
  userName: string
  totalAllocation: number
  activeProjects: number
  totalHoursThisMonth: number
  utilizationRate: number
}
