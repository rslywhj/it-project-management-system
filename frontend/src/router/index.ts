import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'

/** 公开路由（无需登录） */
const publicRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/LoginView.vue'),
    meta: { title: '登录', hidden: true },
  },
]

/** 业务路由（需要登录 + 权限） */
const businessRoutes: RouteRecordRaw[] = [
  {
    path: '/',
    component: MainLayout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/DashboardView.vue'),
        meta: { title: '工作台', icon: 'Odometer', affix: true },
      },
    ],
  },
  {
    path: '/project',
    component: MainLayout,
    redirect: '/project/list',
    meta: { title: '项目管理', icon: 'Folder' },
    children: [
      {
        path: 'list',
        name: 'ProjectList',
        component: () => import('@/views/project/ProjectListView.vue'),
        meta: { title: '项目列表', icon: 'List', permissions: ['project:view'] },
      },
      {
        path: ':id',
        name: 'ProjectDetail',
        component: () => import('@/views/project/ProjectDetailView.vue'),
        meta: { title: '项目详情', hidden: true, permissions: ['project:view'] },
      },
    ],
  },
  {
    path: '/requirement',
    component: MainLayout,
    children: [
      {
        path: '',
        name: 'RequirementGlobal',
        component: () => import('@/views/requirement/RequirementListView.vue'),
        meta: { title: '需求管理', icon: 'Document', permissions: ['requirement:view'] },
      },
    ],
  },
  {
    path: '/task',
    component: MainLayout,
    children: [
      {
        path: '',
        name: 'TaskGlobal',
        component: () => import('@/views/task/TaskListView.vue'),
        meta: { title: '任务管理', icon: 'Finished', permissions: ['task:view'] },
      },
    ],
  },
  {
    path: '/promotion',
    component: MainLayout,
    redirect: '/promotion/units',
    meta: { title: '推广管理', icon: 'Promotion', permissions: ['promotion:view'] },
    children: [
      {
        path: 'units',
        name: 'PromotionUnits',
        component: () => import('@/views/promotion/PromotionUnitListView.vue'),
        meta: { title: '推广单元', icon: 'OfficeBuilding', permissions: ['promotion:view'] },
      },
      {
        path: 'dashboard',
        name: 'PromotionDashboard',
        component: () => import('@/views/promotion/PromotionDashboardView.vue'),
        meta: { title: '推广看板', icon: 'DataAnalysis', permissions: ['promotion:view'] },
      },
    ],
  },
  {
    path: '/test',
    component: MainLayout,
    redirect: '/test/plans',
    meta: { title: '测试管理', icon: 'Monitor', permissions: ['test:view'] },
    children: [
      {
        path: 'plans',
        name: 'TestPlans',
        component: () => import('@/views/test/TestPlanListView.vue'),
        meta: { title: '测试计划', icon: 'Calendar', permissions: ['test:view'] },
      },
      {
        path: 'cases',
        name: 'TestCases',
        component: () => import('@/views/test/TestCaseListView.vue'),
        meta: { title: '用例管理', icon: 'DocumentChecked', permissions: ['test:view'] },
      },
      {
        path: 'defects',
        name: 'Defects',
        component: () => import('@/views/test/DefectListView.vue'),
        meta: { title: '缺陷跟踪', icon: 'Warning', permissions: ['test:view'] },
      },
      {
        path: 'report',
        name: 'TestReport',
        component: () => import('@/views/test/TestReportView.vue'),
        meta: { title: '测试报告', icon: 'DataAnalysis', permissions: ['test:view'] },
      },
    ],
  },
  {
    path: '/system',
    component: MainLayout,
    redirect: '/system/user',
    meta: { title: '系统管理', icon: 'Setting', permissions: ['system:user'] },
    children: [
      {
        path: 'user',
        name: 'UserManage',
        component: () => import('@/views/dashboard/DashboardView.vue'), // TODO: 替换为用户管理页
        meta: { title: '用户管理', icon: 'User', permissions: ['system:user'] },
      },
      {
        path: 'role',
        name: 'RoleManage',
        component: () => import('@/views/dashboard/DashboardView.vue'), // TODO: 替换为角色管理页
        meta: { title: '角色管理', icon: 'UserFilled', permissions: ['system:role'] },
      },
    ],
  },
]

/** 错误路由 */
const errorRoutes: RouteRecordRaw[] = [
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/error/403View.vue'),
    meta: { title: '无权限', hidden: true },
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404View.vue'),
    meta: { title: '页面不存在', hidden: true },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes: [...publicRoutes, ...businessRoutes, ...errorRoutes],
  scrollBehavior: () => ({ top: 0 }),
})

export default router
