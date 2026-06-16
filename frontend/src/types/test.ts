/** 测试计划状态 */
export type TestPlanStatus = 'draft' | 'in_progress' | 'completed' | 'cancelled'

/** 测试用例状态 */
export type TestCaseStatus = 'active' | 'deprecated' | 'draft'

/** 测试执行结果 */
export type ExecutionStatus = 'pending' | 'passed' | 'failed' | 'blocked' | 'skipped'

/** 缺陷状态 */
export type BugStatus = 'open' | 'in_progress' | 'resolved' | 'closed' | 'reopen'

/** 缺陷优先级 */
export type BugPriority = 'critical' | 'high' | 'medium' | 'low'

/** 缺陷严重程度 */
export type BugSeverity = 'critical' | 'major' | 'minor' | 'trivial'

/** 缺陷类型 */
export type BugType = 'functional' | 'ui' | 'performance' | 'security' | 'other'

/** 测试计划类型 */
export type TestPlanType = 'functional' | 'performance' | 'security' | 'regression'

/** 测试用例类型 */
export type TestCaseType = 'functional' | 'api' | 'ui' | 'performance'

/** 测试计划 */
export interface TestPlan {
  id: number
  projectId: number
  name: string
  description?: string
  type?: TestPlanType
  status: TestPlanStatus
  iteration?: string
  startDate?: string
  endDate?: string
  totalCases?: number
  executedCases?: number
  passedCases?: number
  failedCases?: number
  blockedCases?: number
  passRate?: number
  createdAt: string
}

/** 测试计划创建请求 */
export interface TestPlanCreateRequest {
  name: string
  description?: string
  type?: TestPlanType
  iteration?: string
  startDate?: string
  endDate?: string
}

/** 测试用例 */
export interface TestCase {
  id: number
  projectId: number
  module?: string
  title: string
  description?: string
  precondition?: string
  steps?: string
  expectedResult?: string
  priority: BugPriority
  type?: TestCaseType
  status: TestCaseStatus
  requirementId?: number
  createdAt: string
}

/** 测试用例创建请求 */
export interface TestCaseCreateRequest {
  title: string
  module?: string
  description?: string
  precondition?: string
  steps?: string
  expectedResult?: string
  priority?: BugPriority
  type?: TestCaseType
  requirementId?: number
}

/** 测试执行 */
export interface TestExecution {
  id: number
  testPlanId: number
  testCaseId: number
  caseTitle?: string
  status: ExecutionStatus
  executedBy?: number
  executedAt?: string
  actualResult?: string
  remark?: string
  bugId?: number
  createdAt?: string
}

/** 测试执行请求 */
export interface TestExecutionRequest {
  testCaseId: number
  status: ExecutionStatus
  actualResult?: string
  remark?: string
  bugId?: number
}

/** 缺陷 */
export interface Bug {
  id: number
  projectId: number
  testPlanId?: number
  testCaseId?: number
  title: string
  description?: string
  stepsToReproduce?: string
  expectedResult?: string
  actualResult?: string
  severity: BugSeverity
  priority?: BugPriority
  status: BugStatus
  type?: BugType
  assignedTo?: number
  reportedBy?: number
  requirementId?: number
  resolvedAt?: string
  resolvedBy?: number
  resolution?: string
  closedAt?: string
  environment?: string
  attachmentPath?: string
  createdAt: string
}

/** 缺陷创建请求 */
export interface BugCreateRequest {
  title: string
  description?: string
  stepsToReproduce?: string
  expectedResult?: string
  actualResult?: string
  severity?: BugSeverity
  priority?: BugPriority
  type?: BugType
  assignedTo?: number
  testPlanId?: number
  testCaseId?: number
  requirementId?: number
  environment?: string
}

/** 缺陷状态更新请求 */
export interface BugStatusUpdateRequest {
  status: BugStatus
  resolution?: string
}

/** 缺陷严重程度统计 */
export interface BugSeverityStat {
  severity: string
  count: number
}

/** 缺陷模块统计 */
export interface BugModuleStat {
  module: string
  count: number
}

/** 测试报告 */
export interface TestReport {
  projectId: number
  testPlanId: number
  planName: string
  totalCases: number
  executedCases: number
  passedCases: number
  failedCases: number
  blockedCases: number
  skippedCases?: number
  passRate: number
  totalBugs: number
  openBugs: number
  resolvedBugs: number
  severityDistribution?: BugSeverityStat[]
  moduleDistribution?: BugModuleStat[]
}
