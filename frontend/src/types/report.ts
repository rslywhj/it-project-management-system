/** 项目看板数据 */
export interface ProjectDashboard {
  projectId: number
  projectName: string
  stats: ProjectStats
  requirementStats: ModuleStats
  taskStats: ModuleStats
  milestoneStats: MilestoneStats
  recentActivities: Activity[]
}

/** 项目统计 */
export interface ProjectStats {
  totalRequirements: number
  totalTasks: number
  totalMilestones: number
  totalMembers: number
  overallProgress: number
}

/** 模块统计 */
export interface ModuleStats {
  total: number
  byStatus: Record<string, number>
  completionRate: number
}

/** 里程碑统计 */
export interface MilestoneStats {
  total: number
  completed: number
  upcoming: MilestoneItem[]
  overdue: MilestoneItem[]
}

/** 里程碑简项 */
export interface MilestoneItem {
  id: number
  name: string
  plannedDate: string
  status: string
}

/** 最近活动 */
export interface Activity {
  id: number
  type: string
  title: string
  userName: string
  createdAt: string
}

/** 甘特图数据 */
export interface GanttData {
  tasks: GanttTask[]
}

/** 甘特图任务 */
export interface GanttTask {
  id: number
  text: string
  start_date: string
  end_date: string
  progress: number
  parent: number
  type?: string
}

/** 燃尽图数据 */
export interface BurndownData {
  dates: string[]
  ideal: number[]
  actual: number[]
}

/** 工时统计 */
export interface WorkloadData {
  userId: number
  userName: string
  totalTasks: number
  completedTasks: number
  inProgressTasks: number
  estimatedHours: number
  actualHours: number
}
