/** 测试计划状态 */
export type TestPlanStatus = 'draft' | 'active' | 'completed' | 'archived'

/** 测试用例状态 */
export type TestCaseStatus = 'draft' | 'ready' | 'blocked'

/** 测试执行结果 */
export type ExecutionResult = 'pass' | 'fail' | 'blocked' | 'skip'

/** 缺陷状态 */
export type DefectStatus = 'open' | 'in_progress' | 'resolved' | 'closed' | 'reopened'

/** 缺陷优先级 */
export type DefectPriority = 'critical' | 'high' | 'medium' | 'low'

/** 缺陷严重程度 */
export type DefectSeverity = 'blocker' | 'critical' | 'major' | 'minor' | 'trivial'

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
  createdAt: string
  updatedAt: string
  caseCount?: number
  passRate?: number
}

/** 测试计划创建请求 */
export interface TestPlanCreateRequest {
  name: string
  description?: string
  startDate?: string
  endDate?: string
}

/** 测试用例 */
export interface TestCase {
  id: number
  projectId: number
  moduleId?: string
  title: string
  precondition?: string
  steps?: string
  expectedResult?: string
  priority: DefectPriority
  status: TestCaseStatus
  assignedTo?: number
  assignedToName?: string
  requirementId?: number
  createdBy: number
  createdByName?: string
  createdAt: string
  updatedAt: string
}

/** 测试用例创建请求 */
export interface TestCaseCreateRequest {
  title: string
  moduleId?: string
  precondition?: string
  steps?: string
  expectedResult?: string
  priority?: DefectPriority
  assignedTo?: number
  requirementId?: number
}

/** 测试执行 */
export interface TestExecution {
  id: number
  testPlanId: number
  testCaseId: number
  testCaseTitle?: string
  result: ExecutionResult
  executedBy: number
  executedByName?: string
  executedAt: string
  remark?: string
  defectId?: number
}

/** 测试执行请求 */
export interface TestExecutionRequest {
  result: ExecutionResult
  remark?: string
  createDefect?: boolean
  defectSummary?: string
}

/** 缺陷 */
export interface Defect {
  id: number
  projectId: number
  testPlanId?: number
  testCaseId?: number
  title: string
  description?: string
  steps?: string
  priority: DefectPriority
  severity: DefectSeverity
  status: DefectStatus
  assignedTo?: number
  assignedToName?: string
  reportedBy: number
  reportedByName?: string
  resolvedAt?: string
  resolvedBy?: number
  resolution?: string
  createdAt: string
  updatedAt: string
}

/** 缺陷创建请求 */
export interface DefectCreateRequest {
  title: string
  description?: string
  steps?: string
  priority?: DefectPriority
  severity?: DefectSeverity
  assignedTo?: number
  testPlanId?: number
  testCaseId?: number
}

/** 测试报告摘要 */
export interface TestReport {
  testPlanId: number
  testPlanName: string
  totalCases: number
  executedCases: number
  passCases: number
  failCases: number
  blockedCases: number
  passRate: number
  defectStats: {
    total: number
    open: number
    resolved: number
    closed: number
  }
}
