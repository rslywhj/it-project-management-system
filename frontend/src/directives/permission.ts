import type { Directive, DirectiveBinding } from 'vue'
import { useUserStore } from '@/stores/user'

/**
 * v-permission 按钮级权限指令
 *
 * 用法：
 *   <el-button v-permission="'project:create'">新建项目</el-button>
 *   <el-button v-permission="['project:edit', 'project:manage']">编辑</el-button>
 *
 * 无权限时移除 DOM 元素。
 */
export const vPermission: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding<string | string[]>) {
    const userStore = useUserStore()
    const perms = Array.isArray(binding.value) ? binding.value : [binding.value]
    if (!userStore.hasPermission(perms)) {
      el.parentNode?.removeChild(el)
    }
  },
}
