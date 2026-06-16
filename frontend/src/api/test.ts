import service from './index'
import type { PageParams, PageResult } from '@/types/api'
import type {
  TestPlan, TestPlanCreateRequest,
  TestCase, TestCaseCreateRequest,
  TestExecution, TestExecutionRequest,
  Bug, BugCreateRequest, BugStatusUpdateRequest,
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
export function updateTestPlan(id: number, data: TestPlanCreateRequest) {
  return service.put<any, TestPlan>(`/test-plans/${id}`, data)
}

/** 更新测试计划状态 */
export function updateTestPlanStatus(id: number, status: string) {
  return service.put<any, TestPlan>(`/test-plans/${id}/status`, null, { params: { status } })
}

/** 删除测试计划 */
export function deleteTestPlan(id: number) {
  return service.delete<any, void>(`/test-plans/${id}`)
}

/** 获取测试报告 */
export function getTestReport(projectId: number, planId: number) {
  return service.get<any, TestReport>(`/projects/${projectId}/test-plans/${planId}/report`)
}

// ==================== 测试用例 ====================

/** 创建测试用例 */
export function createTestCase(projectId: number, data: TestCaseCreateRequest) {
  return service.post<any, TestCase>(`/projects/${projectId}/test-cases`, data)
}

/** 获取测试用例列表 */
export function getTestCaseList(projectId: number, params: PageParams & { keyword?: string; module?: string; priority?: string }) {
  return service.get<any, PageResult<TestCase>>(`/projects/${projectId}/test-cases`, { params })
}

/** 获取测试用例详情 */
export function getTestCaseDetail(id: number) {
  return service.get<any, TestCase>(`/test-cases/${id}`)
}

/** 更新测试用例 */
export function updateTestCase(id: number, data: TestCaseCreateRequest) {
  return service.put<any, TestCase>(`/test-cases/${id}`, data)
}

/** 删除测试用例 */
export function deleteTestCase(id: number) {
  return service.delete<any, void>(`/test-cases/${id}`)
}

// ==================== 测试执行 ====================

/** 执行测试用例 */
export function executeTest(planId: number, data: TestExecutionRequest) {
  return service.post<any, TestExecution>(`/test-plans/${planId}/executions`, data)
}

/** 获取测试计划的执行记录 */
export function getExecutionList(planId: number) {
  return service.get<any, TestExecution[]>(`/test-plans/${planId}/executions`)
}

// ==================== 缺陷管理 ====================

/** 创建缺陷 */
export function createBug(projectId: number, data: BugCreateRequest) {
  return service.post<any, Bug>(`/projects/${projectId}/bugs`, data)
}

/** 获取缺陷列表 */
export function getBugList(projectId: number, params: PageParams & { keyword?: string; status?: string; severity?: string }) {
  return service.get<any, PageResult<Bug>>(`/projects/${projectId}/bugs`, { params })
}

/** 获取缺陷详情 */
export function getBugDetail(id: number) {
  return service.get<any, Bug>(`/bugs/${id}`)
}

/** 更新缺陷 */
export function updateBug(id: number, data: BugCreateRequest) {
  return service.put<any, Bug>(`/bugs/${id}`, data)
}

/** 更新缺陷状态 */
export function updateBugStatus(id: number, data: BugStatusUpdateRequest) {
  return service.put<any, Bug>(`/bugs/${id}/status`, data)
}

/** 删除缺陷 */
export function deleteBug(id: number) {
  return service.delete<any, void>(`/bugs/${id}`)
}
