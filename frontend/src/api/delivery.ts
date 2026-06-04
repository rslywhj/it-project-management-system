import service from './index'
import type { PageParams, PageResult } from '@/types/api'
import type { Delivery, DeliveryCreateRequest, DeliveryReviewRequest } from '@/types/delivery'

/** 创建交付物 */
export function createDelivery(projectId: number, data: DeliveryCreateRequest) {
  return service.post<any, Delivery>(`/projects/${projectId}/deliveries`, data)
}

/** 获取交付物列表 */
export function getDeliveryList(
  projectId: number,
  params: PageParams & { status?: string; type?: string; keyword?: string },
) {
  return service.get<any, PageResult<Delivery>>(`/projects/${projectId}/deliveries`, { params })
}

/** 获取交付物详情 */
export function getDeliveryDetail(id: number) {
  return service.get<any, Delivery>(`/deliveries/${id}`)
}

/** 更新交付物 */
export function updateDelivery(id: number, data: Partial<Delivery>) {
  return service.put<any, Delivery>(`/deliveries/${id}`, data)
}

/** 删除交付物 */
export function deleteDelivery(id: number) {
  return service.delete<any, void>(`/deliveries/${id}`)
}

/** 提交审核 */
export function submitDelivery(id: number) {
  return service.put<any, Delivery>(`/deliveries/${id}/submit`)
}

/** 审核交付物 */
export function reviewDelivery(id: number, data: DeliveryReviewRequest) {
  return service.put<any, Delivery>(`/deliveries/${id}/review`, data)
}

/** 创建新版本 */
export function createDeliveryVersion(id: number, data: DeliveryCreateRequest) {
  return service.post<any, Delivery>(`/deliveries/${id}/versions`, data)
}
