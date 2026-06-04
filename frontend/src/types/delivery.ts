/** 交付物状态 */
export type DeliveryStatus = 'draft' | 'submitted' | 'approved' | 'rejected'

/** 交付物类型 */
export type DeliveryType = 'document' | 'code' | 'test_report' | 'other'

/** 交付物 */
export interface Delivery {
  id: number
  projectId: number
  milestoneId?: number
  milestoneName?: string
  promotionUnitId?: number
  name: string
  type: DeliveryType
  description?: string
  status: DeliveryStatus
  filePath?: string
  fileName?: string
  version: number
  submittedBy?: number
  submittedByName?: string
  reviewedBy?: number
  reviewedByName?: string
  reviewedAt?: string
  reviewComment?: string
  createdAt: string
  updatedAt: string
}

/** 交付物创建请求 */
export interface DeliveryCreateRequest {
  name: string
  type?: DeliveryType
  description?: string
  milestoneId?: number
  promotionUnitId?: number
}

/** 交付物审核请求 */
export interface DeliveryReviewRequest {
  status: 'approved' | 'rejected'
  reviewComment?: string
}
