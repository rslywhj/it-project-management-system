/** 测试计划状态 */
export type TestPlanStatus = 'draft' | 'in_progress' | 'completed' | 'archived'

/** 用例优先级 */
export type CasePriority = 'critical' | 'high' | 'medium' | 'low'

/** 用例执行结果 */
export type CaseResult = 'pass' | 'fail' | 'block' | 'skip' | 'pending'

/** 缺陷严重程度 */
export type DefectSeverity = 'critical' | 'major' | 'minor' | 'trivial'

/** 缺陷状态 */
export type DefectStatus = 'open' | 'in_progress' | 'resolved' | 'closed' | 'reopened'

/** 缺陷优先级 */
export type DefectPriority = 'critical' | 'high' | 'medium' | 'low'

/** 测试计划 */
export interface TestPlan {
  id: number
  projectId: number
  name: string
  description?: string
  status: TestPlanStatus
  startDate?: string
  endDate?: string
  createdBy: number
  createdByName?: string
  caseCount: number
  passRate: number
  createdAt: string
  updatedAt: string
}

/** 测试用例 */
export interface TestCase {
  id: number
  projectId: number
  planId?: number
  moduleId?: number
  moduleName?: string
  title: string
  precondition?: string
  steps?: string
  expectedResult?: string
  priority: CasePriority
  result: CaseResult
  executedBy?: number
  executedByName?: string
  executedAt?: string
  createdBy: number
  createdByName?: string
  createdAt: string
  updatedAt: string
  children?: TestCase[]
}

/** 缺陷 */
export interface Defect {
  id: number
  projectId: number
  planId?: number
  caseId?: number
  title: string
  description?: string
  severity: DefectSeverity
  priority: DefectPriority
  status: DefectStatus
  assignee?: number
  assigneeName?: string
  reporter: number
  reporterName?: string
  resolvedAt?: string
  closedAt?: string
  createdAt: string
  updatedAt: string
}

/** 测试报告汇总 */
export interface TestReportSummary {
  totalCases: number
  passCases: number
  failCases: number
  blockCases: number
  skipCases: number
  pendingCases: number
  passRate: number
  totalDefects: number
  openDefects: number
  resolvedDefects: number
  closedDefects: number
}

/** 缺陷分布 */
export interface DefectDistribution {
  name: string
  value: number
}

/** 缺陷趋势 */
export interface DefectTrend {
  date: string
  created: number
  resolved: number
}

/** 测试计划创建请求 */
export interface TestPlanCreateRequest {
  name: string
  description?: string
  startDate?: string
  endDate?: string
}

/** 测试用例创建请求 */
export interface TestCaseCreateRequest {
  planId?: number
  moduleId?: number
  title: string
  precondition?: string
  steps?: string
  expectedResult?: string
  priority?: CasePriority
}

/** 缺陷创建请求 */
export interface DefectCreateRequest {
  planId?: number
  caseId?: number
  title: string
  description?: string
  severity?: DefectSeverity
  priority?: DefectPriority
  assignee?: number
}

/** 缺陷状态流转请求 */
export interface DefectStatusUpdateRequest {
  targetStatus: DefectStatus
  remark?: string
}
