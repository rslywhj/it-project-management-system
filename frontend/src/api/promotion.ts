import service from './index'
import type { PageParams, PageResult } from '@/types/api'
import type {
  PromotionUnit,
  PromotionUnitCreateRequest,
  PromotionStageTemplate,
  PromotionStageTemplateCreateRequest,
  PromotionProgress,
  PromotionDashboard,
  UnitRequirement,
  UnitRequirementCreateRequest,
  ConfigBaseline,
  ConfigBaselineCreateRequest,
  UnitConfigDiff,
  UnitConfigDiffCreateRequest,
} from '@/types/promotion'

// ==================== 推广单元 ====================

/** 创建推广单元 */
export function createPromotionUnit(projectId: number, data: PromotionUnitCreateRequest) {
  return service.post<any, PromotionUnit>(`/projects/${projectId}/promotion-units`, data)
}

/** 获取推广单元列表 */
export function getPromotionUnitList(
  projectId: number,
  params: PageParams & { status?: string; keyword?: string },
) {
  return service.get<any, PageResult<PromotionUnit>>(`/projects/${projectId}/promotion-units`, { params })
}

/** 获取推广单元详情 */
export function getPromotionUnitDetail(id: number) {
  return service.get<any, PromotionUnit>(`/promotion-units/${id}`)
}

/** 更新推广单元 */
export function updatePromotionUnit(id: number, data: Partial<PromotionUnit>) {
  return service.put<any, PromotionUnit>(`/promotion-units/${id}`, data)
}

/** 删除推广单元 */
export function deletePromotionUnit(id: number) {
  return service.delete<any, void>(`/promotion-units/${id}`)
}

/** 批量创建推广单元 */
export function batchCreatePromotionUnits(projectId: number, data: PromotionUnitCreateRequest[]) {
  return service.post<any, PromotionUnit[]>(`/projects/${projectId}/promotion-units/batch`, data)
}

// ==================== 推广阶段模板 ====================

/** 创建推广阶段模板 */
export function createStageTemplate(projectId: number, data: PromotionStageTemplateCreateRequest) {
  return service.post<any, PromotionStageTemplate>(`/projects/${projectId}/stage-templates`, data)
}

/** 获取推广阶段模板列表 */
export function getStageTemplateList(projectId: number) {
  return service.get<any, PromotionStageTemplate[]>(`/projects/${projectId}/stage-templates`)
}

/** 更新推广阶段模板 */
export function updateStageTemplate(id: number, data: Partial<PromotionStageTemplate>) {
  return service.put<any, PromotionStageTemplate>(`/stage-templates/${id}`, data)
}

/** 删除推广阶段模板 */
export function deleteStageTemplate(id: number) {
  return service.delete<any, void>(`/stage-templates/${id}`)
}

/** 初始化默认阶段模板 */
export function initDefaultStageTemplates(projectId: number) {
  return service.post<any, PromotionStageTemplate[]>(`/projects/${projectId}/stage-templates/init-default`)
}

// ==================== 推广进度 ====================

/** 获取推广单元的阶段进度列表 */
export function getPromotionProgress(unitId: number) {
  return service.get<any, PromotionProgress[]>(`/promotion-units/${unitId}/progress`)
}

/** 推进阶段（更新进度） */
export function updatePromotionProgress(unitId: number, stageId: number, data: { status: string; completionRate?: number; remark?: string }) {
  return service.put<any, PromotionProgress>(`/promotion-units/${unitId}/progress/${stageId}`, data)
}

/** 获取推广看板汇总数据 */
export function getPromotionDashboard(projectId: number) {
  return service.get<any, PromotionDashboard>(`/projects/${projectId}/promotion-dashboard`)
}

// ==================== 差异化需求 ====================

/** 创建差异化需求 */
export function createUnitRequirement(unitId: number, data: UnitRequirementCreateRequest) {
  return service.post<any, UnitRequirement>(`/promotion-units/${unitId}/requirements`, data)
}

/** 获取推广单元的差异化需求列表 */
export function getUnitRequirementList(unitId: number, params?: PageParams) {
  return service.get<any, PageResult<UnitRequirement>>(`/promotion-units/${unitId}/requirements`, { params })
}

/** 更新差异化需求 */
export function updateUnitRequirement(id: number, data: Partial<UnitRequirement>) {
  return service.put<any, UnitRequirement>(`/unit-requirements/${id}`, data)
}

/** 删除差异化需求 */
export function deleteUnitRequirement(id: number) {
  return service.delete<any, void>(`/unit-requirements/${id}`)
}

// ==================== 配置基线 ====================

/** 创建配置基线 */
export function createConfigBaseline(projectId: number, data: ConfigBaselineCreateRequest) {
  return service.post<any, ConfigBaseline>(`/projects/${projectId}/config-baselines`, data)
}

/** 获取配置基线列表 */
export function getConfigBaselineList(projectId: number, params?: PageParams) {
  return service.get<any, PageResult<ConfigBaseline>>(`/projects/${projectId}/config-baselines`, { params })
}

/** 更新配置基线 */
export function updateConfigBaseline(id: number, data: Partial<ConfigBaseline>) {
  return service.put<any, ConfigBaseline>(`/config-baselines/${id}`, data)
}

/** 删除配置基线 */
export function deleteConfigBaseline(id: number) {
  return service.delete<any, void>(`/config-baselines/${id}`)
}

// ==================== 配置差异 ====================

/** 创建配置差异 */
export function createUnitConfigDiff(unitId: number, data: UnitConfigDiffCreateRequest) {
  return service.post<any, UnitConfigDiff>(`/promotion-units/${unitId}/config-diffs`, data)
}

/** 获取推广单元的配置差异列表 */
export function getUnitConfigDiffList(unitId: number, params?: PageParams) {
  return service.get<any, PageResult<UnitConfigDiff>>(`/promotion-units/${unitId}/config-diffs`, { params })
}

/** 审批配置差异 */
export function approveConfigDiff(id: number, data: { status: 'approved' | 'rejected' }) {
  return service.put<any, UnitConfigDiff>(`/config-diffs/${id}/approve`, data)
}
