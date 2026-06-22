import { describe, it, expect, beforeEach } from 'vitest'
import { getToken, setToken, getRefreshToken, setRefreshToken, clearTokens, isAuthenticated } from '../auth'

describe('auth utils', () => {
  beforeEach(() => {
    localStorage.clear()
  })

  describe('getToken / setToken', () => {
    it('returns null when no token is set', () => {
      expect(getToken()).toBeNull()
    })

    it('stores and retrieves access token', () => {
      setToken('test-access-token')
      expect(getToken()).toBe('test-access-token')
    })
  })

  describe('getRefreshToken / setRefreshToken', () => {
    it('returns null when no refresh token is set', () => {
      expect(getRefreshToken()).toBeNull()
    })

    it('stores and retrieves refresh token', () => {
      setRefreshToken('test-refresh-token')
      expect(getRefreshToken()).toBe('test-refresh-token')
    })
  })

  describe('clearTokens', () => {
    it('removes both tokens', () => {
      setToken('access')
      setRefreshToken('refresh')
      clearTokens()
      expect(getToken()).toBeNull()
      expect(getRefreshToken()).toBeNull()
    })
  })

  describe('isAuthenticated', () => {
    it('returns false when no token', () => {
      expect(isAuthenticated()).toBe(false)
    })

    it('returns true when token exists', () => {
      setToken('some-token')
      expect(isAuthenticated()).toBe(true)
    })
  })
})
