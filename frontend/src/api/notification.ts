import service from './index'
import type { PageParams, PageResult } from '@/types/api'

export interface Notification {
  id: number
  userId: number
  title: string
  content: string
  type: string
  isRead: number
  relatedType?: string
  relatedId?: number
  createdAt: string
}

/** 通知列表 */
export async function getNotificationList(params: PageParams & { isRead?: number }): Promise<PageResult<Notification>> {
  return service.get('/notifications', { params })
}

/** 未读数量 */
export async function getUnreadCount(): Promise<{ count: number }> {
  return service.get('/notifications/unread-count')
}

/** 标记已读 */
export async function markAsRead(id: number): Promise<void> {
  return service.put(`/notifications/${id}/read`)
}

/** 全部已读 */
export async function markAllAsRead(): Promise<void> {
  return service.put('/notifications/read-all')
}
