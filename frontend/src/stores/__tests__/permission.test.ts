import { describe, it, expect, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useUserStore } from '../user'

describe('useUserStore - permission logic', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  describe('hasPermission', () => {
    it('returns true when user has wildcard permission', () => {
      const store = useUserStore()
      store.$patch({ permissions: ['*'] })
      expect(store.hasPermission('project:create')).toBe(true)
      expect(store.hasPermission(['project:create', 'project:edit'])).toBe(true)
    })

    it('returns true when user has the specific permission', () => {
      const store = useUserStore()
      store.$patch({ permissions: ['project:create', 'project:view'] })
      expect(store.hasPermission('project:create')).toBe(true)
    })

    it('returns false when user lacks the permission', () => {
      const store = useUserStore()
      store.$patch({ permissions: ['project:view'] })
      expect(store.hasPermission('project:create')).toBe(false)
    })

    it('returns true if user has at least one of the permissions', () => {
      const store = useUserStore()
      store.$patch({ permissions: ['project:view'] })
      expect(store.hasPermission(['project:create', 'project:view'])).toBe(true)
    })

    it('returns false if user has none of the permissions', () => {
      const store = useUserStore()
      store.$patch({ permissions: ['project:view'] })
      expect(store.hasPermission(['project:create', 'project:edit'])).toBe(false)
    })

    it('returns false when permissions list is empty', () => {
      const store = useUserStore()
      store.$patch({ permissions: [] })
      expect(store.hasPermission('project:create')).toBe(false)
    })
  })

  describe('hasRole', () => {
    it('returns true when user has the role', () => {
      const store = useUserStore()
      store.$patch({ roles: ['admin', 'pm'] })
      expect(store.hasRole('admin')).toBe(true)
    })

    it('returns false when user lacks the role', () => {
      const store = useUserStore()
      store.$patch({ roles: ['pm'] })
      expect(store.hasRole('admin')).toBe(false)
    })
  })

  describe('hasAllPermissions', () => {
    it('returns true when user has all permissions', () => {
      const store = useUserStore()
      store.$patch({ permissions: ['project:create', 'project:edit', 'project:view'] })
      expect(store.hasAllPermissions(['project:create', 'project:edit'])).toBe(true)
    })

    it('returns false when user is missing some permissions', () => {
      const store = useUserStore()
      store.$patch({ permissions: ['project:create'] })
      expect(store.hasAllPermissions(['project:create', 'project:edit'])).toBe(false)
    })

    it('returns true with wildcard permission', () => {
      const store = useUserStore()
      store.$patch({ permissions: ['*'] })
      expect(store.hasAllPermissions(['project:create', 'project:edit'])).toBe(true)
    })
  })
})
