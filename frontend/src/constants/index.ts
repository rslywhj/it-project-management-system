/**
 * 集中状态映射 — 消除各视图中的重复定义
 *
 * 用法：
 *   import { TASK_STATUS_LABEL, TASK_STATUS_TYPE, tagType } from '@/constants'
 *   TASK_STATUS_LABEL[s]  // '待办'
 *   TASK_STATUS_TYPE[s]   // 'info'
 */

// ─── 通用类型 ─────────────────────────────────────────────────

export type TagType = 'primary' | 'success' | 'warning' | 'info' | 'danger'

// ─── 任务 ─────────────────────────────────────────────────────

export const TASK_TYPE_LABEL: Record<string, string> = {
  dev: '开发', test: '测试', deploy: '部署', training: '培训', design: '设计', review: '评审',
}

export const TASK_STATUS_LABEL: Record<string, string> = {
  todo: '待办', in_progress: '进行中', done: '已完成',
}

export const TASK_STATUS_TYPE: Record<string, TagType> = {
  todo: 'info', in_progress: 'warning', done: 'success',
}

// ─── 需求 ─────────────────────────────────────────────────────

export const REQUIREMENT_STATUS_LABEL: Record<string, string> = {
  draft: '草稿', reviewing: '评审中', approved: '已通过', rejected: '已拒绝', scheduled: '已排期', in_progress: '进行中', done: '已完成',
}

export const REQUIREMENT_STATUS_TYPE: Record<string, TagType> = {
  draft: 'info', reviewing: 'warning', approved: 'success', rejected: 'danger', scheduled: 'warning', in_progress: 'warning', done: 'success',
}

// ─── 交付物 ───────────────────────────────────────────────────

export const DELIVERY_TYPE_LABEL: Record<string, string> = {
  document: '文档', code: '代码', test_report: '测试报告', other: '其他',
}

export const DELIVERY_STATUS_LABEL: Record<string, string> = {
  draft: '草稿', submitted: '已提交', approved: '已通过', rejected: '已驳回',
}

export const DELIVERY_STATUS_TYPE: Record<string, TagType> = {
  draft: 'info', submitted: 'warning', approved: 'success', rejected: 'danger',
}

// ─── 优先级 ───────────────────────────────────────────────────

export const PRIORITY_TYPE: Record<string, TagType> = {
  critical: 'danger', high: 'warning', medium: 'info', low: 'info',
}

// ─── 推广 ─────────────────────────────────────────────────────

export const PROMOTION_STATUS_LABEL: Record<string, string> = {
  pending: '待启动', in_progress: '进行中', completed: '已完成', suspended: '已暂停',
}

export const PROMOTION_STATUS_TYPE: Record<string, TagType> = {
  pending: 'info', in_progress: 'warning', completed: 'success', suspended: 'danger',
}

// ─── 风险 ─────────────────────────────────────────────────────

export const RISK_STATUS_LABEL: Record<string, string> = {
  identified: '已识别', analyzing: '分析中', mitigating: '缓解中', occurred: '已发生', closed: '已关闭',
}

export const RISK_STATUS_TYPE: Record<string, TagType> = {
  identified: 'info', analyzing: 'warning', mitigating: 'warning', occurred: 'danger', closed: 'success',
}

// ─── 测试 ─────────────────────────────────────────────────────

export const TEST_PLAN_STATUS_LABEL: Record<string, string> = {
  draft: '草稿', active: '执行中', completed: '已完成', archived: '已归档',
}

export const TEST_PLAN_STATUS_TYPE: Record<string, TagType> = {
  draft: 'info', active: 'warning', completed: 'success', archived: 'info',
}

export const TEST_CASE_STATUS_LABEL: Record<string, string> = {
  draft: '草稿', ready: '就绪', blocked: '阻塞',
}

export const TEST_CASE_STATUS_TYPE: Record<string, TagType> = {
  draft: 'info', ready: 'success', blocked: 'danger',
}

// ─── 通知 ─────────────────────────────────────────────────────

export const NOTIFICATION_TYPE_LABEL: Record<string, string> = {
  task: '任务', requirement: '需求', project: '项目', system: '系统',
}

export const NOTIFICATION_TYPE_TAG: Record<string, TagType> = {
  task: 'primary', requirement: 'success', project: 'warning', system: 'info',
}

// ─── 通用工具函数 ─────────────────────────────────────────────

/** 从映射表获取标签，无匹配时返回原始值 */
export function labelFrom(map: Record<string, string>, key: string): string {
  return map[key] ?? key
}

/** 从映射表获取 tag 类型，无匹配时返回 'info' */
export function tagType(map: Record<string, TagType>, key: string): TagType {
  return map[key] ?? 'info'
}
