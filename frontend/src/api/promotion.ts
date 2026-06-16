import service from './index'
import type { PageParams, PageResult } from '@/types/api'
import type {
  PromotionUnit, PromotionUnitCreateRequest, BatchCreateUnitsRequest,
  PromotionStageTemplate, PromotionStageTemplateRequest,
  PromotionProgress, PromotionProgressUpdateRequest,
  PromotionDashboard,
  UnitRequirement, UnitRequirementRequest,
  ConfigBaseline, ConfigBaselineRequest,
  UnitConfigDiff, UnitConfigDiffRequest,
} from '@/types/promotion'

// ==================== 推广单元管理 ====================

export function createPromotionUnit(projectId: number, data: PromotionUnitCreateRequest) {
  return service.post<any, PromotionUnit>(`/projects/${projectId}/promotion-units`, data)
}

export function batchCreatePromotionUnits(projectId: number, data: BatchCreateUnitsRequest) {
  return service.post<any, PromotionUnit[]>(`/projects/${projectId}/promotion-units/batch`, data)
}

export function getPromotionUnitList(projectId: number, params?: PageParams & { status?: string }) {
  return service.get<any, PageResult<PromotionUnit>>(`/projects/${projectId}/promotion-units`, { params })
}

export function getPromotionUnitDetail(id: number) {
  return service.get<any, PromotionUnit>(`/promotion-units/${id}`)
}

export function updatePromotionUnit(id: number, data: PromotionUnitCreateRequest) {
  return service.put<any, PromotionUnit>(`/promotion-units/${id}`, data)
}

export function deletePromotionUnit(id: number) {
  return service.delete<any, void>(`/promotion-units/${id}`)
}

// ==================== 推广阶段模板 ====================

export function createStageTemplate(projectId: number, data: PromotionStageTemplateRequest) {
  return service.post<any, PromotionStageTemplate>(`/projects/${projectId}/promotion-stage-templates`, data)
}

export function getStageTemplateList(projectId: number) {
  return service.get<any, PromotionStageTemplate[]>(`/projects/${projectId}/promotion-stage-templates`)
}

export function updateStageTemplate(id: number, data: PromotionStageTemplateRequest) {
  return service.put<any, PromotionStageTemplate>(`/promotion-stage-templates/${id}`, data)
}

export function deleteStageTemplate(id: number) {
  return service.delete<any, void>(`/promotion-stage-templates/${id}`)
}

export function initDefaultStageTemplates(projectId: number) {
  return service.post<any, void>(`/projects/${projectId}/promotion-stage-templates/init-default`)
}

// ==================== 推广进度 ====================

export function getUnitProgress(unitId: number) {
  return service.get<any, PromotionProgress[]>(`/promotion-units/${unitId}/progress`)
}

export function updateUnitProgress(unitId: number, stageId: number, data: PromotionProgressUpdateRequest) {
  return service.put<any, PromotionProgress>(`/promotion-units/${unitId}/progress/${stageId}`, data)
}

// ==================== 差异化需求 ====================

export function createUnitRequirement(unitId: number, data: UnitRequirementRequest) {
  return service.post<any, UnitRequirement>(`/promotion-units/${unitId}/requirements`, data)
}

export function getUnitRequirementList(unitId: number, params?: PageParams & { type?: string }) {
  return service.get<any, PageResult<UnitRequirement>>(`/promotion-units/${unitId}/requirements`, { params })
}

export function updateUnitRequirement(id: number, data: UnitRequirementRequest) {
  return service.put<any, UnitRequirement>(`/promotion-requirements/${id}`, data)
}

export function deleteUnitRequirement(id: number) {
  return service.delete<any, void>(`/promotion-requirements/${id}`)
}

// ==================== 配置基线 ====================

export function createConfigBaseline(projectId: number, data: ConfigBaselineRequest) {
  return service.post<any, ConfigBaseline>(`/projects/${projectId}/config-baselines`, data)
}

export function getConfigBaselineList(projectId: number) {
  return service.get<any, ConfigBaseline[]>(`/projects/${projectId}/config-baselines`)
}

export function updateConfigBaseline(id: number, data: ConfigBaselineRequest) {
  return service.put<any, ConfigBaseline>(`/config-baselines/${id}`, data)
}

export function deleteConfigBaseline(id: number) {
  return service.delete<any, void>(`/config-baselines/${id}`)
}

// ==================== 配置差异 ====================

export function createConfigDiff(unitId: number, data: UnitConfigDiffRequest) {
  return service.post<any, UnitConfigDiff>(`/promotion-units/${unitId}/config-diffs`, data)
}

export function getConfigDiffList(unitId: number) {
  return service.get<any, UnitConfigDiff[]>(`/promotion-units/${unitId}/config-diffs`)
}

export function approveConfigDiff(id: number, action: 'approve' | 'reject') {
  return service.put<any, UnitConfigDiff>(`/config-diffs/${id}/approve`, null, { params: { action } })
}

// ==================== 推广看板 ====================

export function getPromotionDashboard(projectId: number) {
  return service.get<any, PromotionDashboard>(`/projects/${projectId}/promotion/dashboard`)
}
