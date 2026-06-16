/** 风险状态 */
export type RiskStatus = 'open' | 'monitoring' | 'mitigated' | 'closed' | 'realized'

/** 风险等级 */
export type RiskLevel = 'critical' | 'high' | 'medium' | 'low'

/** 风险类别 */
export type RiskCategory = 'technical' | 'resource' | 'schedule' | 'quality' | 'external'

/** 概率/影响级别 */
export type ProbabilityImpact = 'high' | 'medium' | 'low'

/** 问题状态 */
export type IssueStatus = 'open' | 'in_progress' | 'resolved' | 'closed' | 'reopen'

/** 问题严重程度 */
export type IssueSeverity = 'critical' | 'major' | 'minor' | 'trivial'

/** 问题类别 */
export type IssueCategory = 'technical' | 'process' | 'resource' | 'communication' | 'other'

/** 风险 */
export interface Risk {
  id: number
  projectId: number
  title: string
  description?: string
  category?: RiskCategory
  probability: ProbabilityImpact
  impact: ProbabilityImpact
  riskLevel?: RiskLevel
  status: RiskStatus
  ownerId?: number
  mitigationPlan?: string
  contingencyPlan?: string
  identifiedDate?: string
  reviewDate?: string
  closedDate?: string
  createdAt: string
}

/** 风险创建请求 */
export interface RiskCreateRequest {
  title: string
  description?: string
  category?: RiskCategory
  probability?: ProbabilityImpact
  impact?: ProbabilityImpact
  ownerId?: number
  mitigationPlan?: string
  contingencyPlan?: string
  identifiedDate?: string
  reviewDate?: string
}

/** 风险状态更新请求 */
export interface RiskStatusUpdateRequest {
  status: RiskStatus
}

/** 问题 */
export interface Issue {
  id: number
  projectId: number
  title: string
  description?: string
  category?: IssueCategory
  severity: IssueSeverity
  status: IssueStatus
  assignedTo?: number
  reportedBy?: number
  resolution?: string
  resolvedAt?: string
  closedAt?: string
  dueDate?: string
  createdAt: string
}

/** 问题创建请求 */
export interface IssueCreateRequest {
  title: string
  description?: string
  category?: IssueCategory
  severity?: IssueSeverity
  assignedTo?: number
  dueDate?: string
}

/** 问题状态更新请求 */
export interface IssueStatusUpdateRequest {
  status: IssueStatus
  resolution?: string
}
