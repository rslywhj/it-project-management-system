import service from './index'
import type { PageParams, PageResult } from '@/types/api'
import type {
  TestPlan, TestPlanCreateRequest,
  TestCase, TestCaseCreateRequest,
  TestExecution, TestExecutionRequest,
  Defect, DefectCreateRequest,
  TestReport,
} from '@/types/test'

// ==================== 测试计划 ====================

/** 创建测试计划 */
export function createTestPlan(projectId: number, data: TestPlanCreateRequest) {
  return service.post<any, TestPlan>(`/projects/${projectId}/test-plans`, data)
}

/** 获取测试计划列表 */
export function getTestPlanList(projectId: number, params: PageParams & { status?: string }) {
  return service.get<any, PageResult<TestPlan>>(`/projects/${projectId}/test-plans`, { params })
}

/** 获取测试计划详情 */
export function getTestPlanDetail(id: number) {
  return service.get<any, TestPlan>(`/test-plans/${id}`)
}

/** 更新测试计划 */
export function updateTestPlan(id: number, data: Partial<TestPlan>) {
  return service.put<any, TestPlan>(`/test-plans/${id}`, data)
}

/** 删除测试计划 */
export function deleteTestPlan(id: number) {
  return service.delete<any, void>(`/test-plans/${id}`)
}

/** 获取测试报告 */
export function getTestReport(planId: number) {
  return service.get<any, TestReport>(`/test-plans/${planId}/report`)
}

// ==================== 测试用例 ====================

/** 创建测试用例 */
export function createTestCase(projectId: number, data: TestCaseCreateRequest) {
  return service.post<any, TestCase>(`/projects/${projectId}/test-cases`, data)
}

/** 获取测试用例列表 */
export function getTestCaseList(projectId: number, params: PageParams & { status?: string; priority?: string; moduleId?: string }) {
  return service.get<any, PageResult<TestCase>>(`/projects/${projectId}/test-cases`, { params })
}

/** 获取测试用例详情 */
export function getTestCaseDetail(id: number) {
  return service.get<any, TestCase>(`/test-cases/${id}`)
}

/** 更新测试用例 */
export function updateTestCase(id: number, data: Partial<TestCase>) {
  return service.put<any, TestCase>(`/test-cases/${id}`, data)
}

/** 删除测试用例 */
export function deleteTestCase(id: number) {
  return service.delete<any, void>(`/test-cases/${id}`)
}

// ==================== 测试执行 ====================

/** 执行测试用例 */
export function executeTestCase(planId: number, caseId: number, data: TestExecutionRequest) {
  return service.post<any, TestExecution>(`/test-plans/${planId}/cases/${caseId}/execute`, data)
}

/** 获取测试计划的执行记录 */
export function getExecutionList(planId: number, params?: PageParams) {
  return service.get<any, PageResult<TestExecution>>(`/test-plans/${planId}/executions`, { params })
}

// ==================== 缺陷管理 ====================

/** 创建缺陷 */
export function createDefect(projectId: number, data: DefectCreateRequest) {
  return service.post<any, Defect>(`/projects/${projectId}/bugs`, data)
}

/** 获取缺陷列表 */
export function getDefectList(projectId: number, params: PageParams & { status?: string; priority?: string; severity?: string }) {
  return service.get<any, PageResult<Defect>>(`/projects/${projectId}/bugs`, { params })
}

/** 获取缺陷详情 */
export function getDefectDetail(id: number) {
  return service.get<any, Defect>(`/bugs/${id}`)
}

/** 更新缺陷 */
export function updateDefect(id: number, data: Partial<Defect>) {
  return service.put<any, Defect>(`/bugs/${id}`, data)
}

/** 关闭缺陷 */
export function closeDefect(id: number, resolution?: string) {
  return service.put<any, Defect>(`/bugs/${id}/status`, { resolution })
}

/** 删除缺陷 */
export function deleteDefect(id: number) {
  return service.delete<any, void>(`/bugs/${id}`)
}
