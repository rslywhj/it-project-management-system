import service from './index'
import type { ProjectDashboard, GanttData, BurndownData, WorkloadData } from '@/types/report'

/** 获取项目看板数据 */
export function getProjectDashboard(projectId: number) {
  return service.get<any, ProjectDashboard>(`/projects/${projectId}/dashboard`)
}

/** 获取甘特图数据 */
export function getGanttData(projectId: number) {
  return service.get<any, GanttData>(`/projects/${projectId}/gantt`)
}

/** 获取燃尽图数据 */
export function getBurndownData(projectId: number) {
  return service.get<any, BurndownData>(`/projects/${projectId}/burndown`)
}

/** 获取团队工时统计 */
export function getWorkloadData(projectId: number) {
  return service.get<any, WorkloadData[]>(`/projects/${projectId}/resources/workload`)
}
