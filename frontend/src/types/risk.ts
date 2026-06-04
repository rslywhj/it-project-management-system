/** 风险状态 */
export type RiskStatus = 'identified' | 'analyzing' | 'mitigating' | 'closed' | 'occurred'

/** 风险等级 */
export type RiskLevel = 'critical' | 'high' | 'medium' | 'low'

/** 问题状态 */
export type IssueStatus = 'open' | 'in_progress' | 'resolved' | 'closed'

/** 问题优先级 */
export type IssuePriority = 'critical' | 'high' | 'medium' | 'low'

/** 风险 */
export interface Risk {
  id: number
  projectId: number
  title: string
  description?: string
  category?: string
  level: RiskLevel
  probability: number
  impact: number
  riskScore: number
  status: RiskStatus
  mitigationPlan?: string
  contingencyPlan?: string
  ownerId?: number
  ownerName?: string
  identifiedDate: string
  closedDate?: string
  createdAt: string
  updatedAt: string
}

/** 风险创建请求 */
export interface RiskCreateRequest {
  title: string
  description?: string
  category?: string
  level?: RiskLevel
  probability?: number
  impact?: number
  mitigationPlan?: string
  contingencyPlan?: string
  ownerId?: number
}

/** 问题 */
export interface Issue {
  id: number
  projectId: number
  title: string
  description?: string
  category?: string
  priority: IssuePriority
  status: IssueStatus
  reporterId?: number
  reporterName?: string
  assigneeId?: number
  assigneeName?: string
  resolution?: string
  dueDate?: string
  resolvedAt?: string
  createdAt: string
  updatedAt: string
}

/** 问题创建请求 */
export interface IssueCreateRequest {
  title: string
  description?: string
  category?: string
  priority?: IssuePriority
  assigneeId?: number
  dueDate?: string
}
