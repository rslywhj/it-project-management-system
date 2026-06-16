/** 需求统计 */
export interface RequirementStats {
  total: number
  draft: number
  reviewing: number
  approved: number
  scheduled: number
  done: number
  completionRate: number
}

/** 任务统计 */
export interface TaskStats {
  total: number
  todo: number
  inProgress: number
  done: number
  completionRate: number
  averageProgress: number
}

/** 缺陷统计 */
export interface BugStats {
  total: number
  open: number
  inProgress: number
  resolved: number
  closed: number
  criticalCount: number
  majorCount: number
}

/** 里程碑统计 */
export interface MilestoneStats {
  total: number
  pending: number
  completed: number
  delayed: number
  atRisk: number
}

/** 近期活动 */
export interface RecentActivity {
  type: string
  title: string
  operator: string
  time: string
}

/** 项目看板数据 */
export interface ProjectDashboard {
  projectId: number
  projectName: string
  projectStatus: string
  requirementStats: RequirementStats
  taskStats: TaskStats
  bugStats: BugStats
  milestoneStats: MilestoneStats
  healthScore: number
  recentActivities: RecentActivity[]
}

/** 甘特图数据 */
export interface GanttData {
  projectId: number
  tasks: GanttTask[]
  dependencies: GanttDependency[]
}

/** 甘特图任务 */
export interface GanttTask {
  id: number
  text: string
  start: string
  end: string
  progress: number
  parent?: number
  type?: string
  open?: boolean
  assignee?: string
}

/** 甘特图依赖 */
export interface GanttDependency {
  id: number
  source: number
  target: number
  type: string
}

/** 燃尽图数据 */
export interface BurndownData {
  projectId: number
  iteration?: string
  startDate?: string
  endDate?: string
  totalTasks: number
  idealLine: BurndownPoint[]
  actualLine: BurndownPoint[]
}

/** 燃尽点 */
export interface BurndownPoint {
  date: string
  remaining: number
}
