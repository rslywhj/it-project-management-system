import service from './index'
import type { PageParams, PageResult } from '@/types/api'
import type {
  TestPlan,
  TestPlanCreateRequest,
  TestCase,
  TestCaseCreateRequest,
  Defect,
  DefectCreateRequest,
  DefectStatusUpdateRequest,
  TestReportSummary,
  DefectDistribution,
  DefectTrend,
} from '@/types/test'

// ==================== 测试计划 ====================

/** 创建测试计划 */
export async function createTestPlan(projectId: number, data: TestPlanCreateRequest): Promise<TestPlan> {
  return service.post(`/projects/${projectId}/test-plans`, data)
}

/** 获取测试计划列表 */
export async function getTestPlanList(
  projectId: number,
  params: PageParams & { status?: string; keyword?: string },
): Promise<PageResult<TestPlan>> {
  return service.get(`/projects/${projectId}/test-plans`, { params })
}

/** 获取测试计划详情 */
export async function getTestPlanDetail(id: number): Promise<TestPlan> {
  return service.get(`/test-plans/${id}`)
}

/** 更新测试计划 */
export async function updateTestPlan(id: number, data: Partial<TestPlanCreateRequest>): Promise<TestPlan> {
  return service.put(`/test-plans/${id}`, data)
}

/** 删除测试计划 */
export async function deleteTestPlan(id: number): Promise<void> {
  return service.delete(`/test-plans/${id}`)
}

// ==================== 测试用例 ====================

/** 创建测试用例 */
export async function createTestCase(projectId: number, data: TestCaseCreateRequest): Promise<TestCase> {
  return service.post(`/projects/${projectId}/test-cases`, data)
}

/** 获取测试用例列表（树形） */
export async function getTestCaseList(
  projectId: number,
  params: { planId?: number; keyword?: string; priority?: string; result?: string },
): Promise<TestCase[]> {
  return service.get(`/projects/${projectId}/test-cases`, { params })
}

/** 更新测试用例 */
export async function updateTestCase(id: number, data: Partial<TestCaseCreateRequest>): Promise<TestCase> {
  return service.put(`/test-cases/${id}`, data)
}

/** 删除测试用例 */
export async function deleteTestCase(id: number): Promise<void> {
  return service.delete(`/test-cases/${id}`)
}

/** 执行测试用例 */
export async function executeTestCase(id: number, result: string): Promise<void> {
  return service.put(`/test-cases/${id}/execute`, { result })
}

// ==================== 缺陷 ====================

/** 创建缺陷 */
export async function createDefect(projectId: number, data: DefectCreateRequest): Promise<Defect> {
  return service.post(`/projects/${projectId}/defects`, data)
}

/** 获取缺陷列表 */
export async function getDefectList(
  projectId: number,
  params: PageParams & { status?: string; severity?: string; keyword?: string },
): Promise<PageResult<Defect>> {
  return service.get(`/projects/${projectId}/defects`, { params })
}

/** 更新缺陷 */
export async function updateDefect(id: number, data: Partial<DefectCreateRequest>): Promise<Defect> {
  return service.put(`/defects/${id}`, data)
}

/** 缺陷状态流转 */
export async function updateDefectStatus(id: number, data: DefectStatusUpdateRequest): Promise<void> {
  return service.put(`/defects/${id}/status`, data)
}

/** 删除缺陷 */
export async function deleteDefect(id: number): Promise<void> {
  return service.delete(`/defects/${id}`)
}

// ==================== 测试报告 ====================

/** 获取测试报告汇总 */
export async function getTestReportSummary(projectId: number, planId?: number): Promise<TestReportSummary> {
  return service.get(`/projects/${projectId}/test-reports/summary`, { params: { planId } })
}

/** 获取缺陷分布 */
export async function getDefectDistribution(projectId: number, planId?: number): Promise<DefectDistribution[]> {
  return service.get(`/projects/${projectId}/test-reports/defect-distribution`, { params: { planId } })
}

/** 获取缺陷趋势 */
export async function getDefectTrend(projectId: number, planId?: number): Promise<DefectTrend[]> {
  return service.get(`/projects/${projectId}/test-reports/defect-trend`, { params: { planId } })
}
