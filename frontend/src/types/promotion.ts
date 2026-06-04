/** 推广单元状态 */
export type PromotionUnitStatus = 'pending' | 'in_progress' | 'completed' | 'suspended'

/** 推广阶段状态 */
export type PromotionStageStatus = 'pending' | 'in_progress' | 'completed' | 'skipped'

/** 差异化需求类型 */
export type UnitRequirementType = 'general' | 'differential'

/** 配置差异状态 */
export type ConfigDiffStatus = 'pending' | 'approved' | 'rejected'

/** 推广单元 */
export interface PromotionUnit {
  id: number
  projectId: number
  orgName: string
  orgCode: string
  status: PromotionUnitStatus
  currentStageId?: number
  currentStageName?: string
  completionRate: number
  responsibleUserId?: number
  responsibleUserName?: string
  expectedStartDate?: string
  expectedEndDate?: string
  actualStartDate?: string
  actualEndDate?: string
  remark?: string
  createdAt: string
  updatedAt: string
}

/** 推广单元创建请求 */
export interface PromotionUnitCreateRequest {
  orgName: string
  orgCode: string
  responsibleUserId?: number
  expectedStartDate?: string
  expectedEndDate?: string
  remark?: string
}

/** 推广阶段模板 */
export interface PromotionStageTemplate {
  id: number
  projectId?: number
  name: string
  description?: string
  sortOrder: number
  isLocked: boolean
  estimatedDays?: number
  createdAt: string
}

/** 推广阶段模板创建请求 */
export interface PromotionStageTemplateCreateRequest {
  name: string
  description?: string
  sortOrder?: number
  isLocked?: boolean
  estimatedDays?: number
}

/** 推广单元阶段进度 */
export interface PromotionProgress {
  id: number
  promotionUnitId: number
  stageTemplateId: number
  stageTemplateName?: string
  status: PromotionStageStatus
  startedAt?: string
  completedAt?: string
  expectedEndDate?: string
  completionRate: number
  remark?: string
  createdAt: string
}

/** 推广看板汇总 */
export interface PromotionDashboard {
  totalUnits: number
  completedUnits: number
  inProgressUnits: number
  pendingUnits: number
  suspendedUnits: number
  delayedUnits: number
  averageCompletionRate: number
  unitProgressList: PromotionUnitProgressSummary[]
}

/** 推广单元进度摘要 */
export interface PromotionUnitProgressSummary {
  unitId: number
  orgName: string
  status: PromotionUnitStatus
  completionRate: number
  currentStageName?: string
  expectedEndDate?: string
  isDelayed: boolean
}

/** 差异化需求 */
export interface UnitRequirement {
  id: number
  promotionUnitId: number
  requirementId?: number
  title: string
  description?: string
  type: UnitRequirementType
  priority: string
  status: string
  createdAt: string
}

/** 差异化需求创建请求 */
export interface UnitRequirementCreateRequest {
  title: string
  description?: string
  type?: UnitRequirementType
  priority?: string
  requirementId?: number
}

/** 配置基线 */
export interface ConfigBaseline {
  id: number
  projectId: number
  configKey: string
  configValue?: string
  description?: string
  isLocked: boolean
  createdAt: string
}

/** 配置基线创建请求 */
export interface ConfigBaselineCreateRequest {
  configKey: string
  configValue?: string
  description?: string
  isLocked?: boolean
}

/** 配置差异 */
export interface UnitConfigDiff {
  id: number
  promotionUnitId: number
  configBaselineId: number
  configKey?: string
  diffValue?: string
  diffReason?: string
  status: ConfigDiffStatus
  approvedBy?: number
  approvedAt?: string
  createdAt: string
}

/** 配置差异创建请求 */
export interface UnitConfigDiffCreateRequest {
  configBaselineId: number
  diffValue?: string
  diffReason?: string
}
