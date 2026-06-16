/** 推广单元状态 */
export type PromotionUnitStatus = 'pending' | 'in_progress' | 'completed' | 'suspended'

/** 推广进度状态 */
export type PromotionProgressStatus = 'pending' | 'in_progress' | 'completed' | 'skipped'

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
  completionRate?: number
  responsibleUserId?: number
  responsibleUserName?: string
  expectedStartDate?: string
  expectedEndDate?: string
  actualStartDate?: string
  actualEndDate?: string
  remark?: string
  stageProgress?: PromotionProgress[]
  createdAt: string
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

/** 批量创建推广单元请求 */
export interface BatchCreateUnitsRequest {
  units: PromotionUnitCreateRequest[]
  initStageProgress?: boolean
}

/** 推广阶段模板 */
export interface PromotionStageTemplate {
  id: number
  projectId: number
  name: string
  description?: string
  sortOrder?: number
  isLocked: number
  estimatedDays?: number
  createdAt: string
}

/** 推广阶段模板创建请求 */
export interface PromotionStageTemplateRequest {
  name: string
  description?: string
  sortOrder?: number
  isLocked?: number
  estimatedDays?: number
}

/** 推广进度 */
export interface PromotionProgress {
  id: number
  promotionUnitId: number
  stageTemplateId: number
  stageName?: string
  status: PromotionProgressStatus
  startedAt?: string
  completedAt?: string
  expectedEndDate?: string
  completionRate?: number
  remark?: string
}

/** 推广进度更新请求 */
export interface PromotionProgressUpdateRequest {
  status: PromotionProgressStatus
  completionRate?: number
  remark?: string
}

/** 单位进度对比 */
export interface UnitComparison {
  unitId: number
  orgName: string
  currentStage: string
  completionRate: number
  status: string
  isOverdue: boolean
}

/** 延期预警 */
export interface OverdueAlert {
  unitId: number
  orgName: string
  stageName: string
  expectedEndDate: string
  overdueDays: number
  alertLevel: string
}

/** 推广看板 */
export interface PromotionDashboard {
  projectId: number
  totalUnits: number
  completedUnits: number
  inProgressUnits: number
  pendingUnits: number
  overdueUnits: number
  overallCompletionRate: number
  unitComparisons: UnitComparison[]
  overdueAlerts: OverdueAlert[]
}

/** 差异化需求 */
export interface UnitRequirement {
  id: number
  promotionUnitId: number
  requirementId?: number
  title: string
  description?: string
  type?: string
  priority?: string
  status?: string
  createdAt: string
}

/** 差异化需求创建请求 */
export interface UnitRequirementRequest {
  title: string
  description?: string
  requirementId?: number
  type?: string
  priority?: string
}

/** 配置基线 */
export interface ConfigBaseline {
  id: number
  projectId: number
  configKey: string
  configValue?: string
  description?: string
  isLocked: number
  createdAt: string
}

/** 配置基线创建请求 */
export interface ConfigBaselineRequest {
  configKey: string
  configValue?: string
  description?: string
  isLocked?: number
}

/** 配置差异 */
export interface UnitConfigDiff {
  id: number
  promotionUnitId: number
  configBaselineId: number
  configKey?: string
  standardValue?: string
  diffValue?: string
  diffReason?: string
  status: ConfigDiffStatus
  approvedBy?: number
  approvedAt?: string
  createdAt: string
}

/** 配置差异创建请求 */
export interface UnitConfigDiffRequest {
  configBaselineId: number
  diffValue?: string
  diffReason?: string
}
