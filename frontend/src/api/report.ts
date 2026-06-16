import service from './index'
import type { ProjectDashboard, GanttData, BurndownData } from '@/types/report'

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
