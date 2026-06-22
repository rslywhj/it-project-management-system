import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { formatDate, debounce } from '../index'

describe('formatDate', () => {
  it('formats date with default pattern', () => {
    const result = formatDate('2025-06-15T10:30:00')
    expect(result).toBe('2025-06-15')
  })

  it('formats date with time', () => {
    const result = formatDate('2025-06-15T10:30:45', 'YYYY-MM-DD HH:mm:ss')
    expect(result).toBe('2025-06-15 10:30:45')
  })

  it('handles Date object input', () => {
    const d = new Date(2025, 0, 5) // Jan 5, 2025
    const result = formatDate(d)
    expect(result).toBe('2025-01-05')
  })

  it('pads single digit month and day', () => {
    const result = formatDate('2025-03-07')
    expect(result).toBe('2025-03-07')
  })
})

describe('debounce', () => {
  beforeEach(() => {
    vi.useFakeTimers()
  })

  afterEach(() => {
    vi.useRealTimers()
  })

  it('delays function execution', () => {
    const fn = vi.fn()
    const debounced = debounce(fn, 100)

    debounced()
    expect(fn).not.toHaveBeenCalled()

    vi.advanceTimersByTime(100)
    expect(fn).toHaveBeenCalledOnce()
  })

  it('resets timer on subsequent calls', () => {
    const fn = vi.fn()
    const debounced = debounce(fn, 100)

    debounced()
    vi.advanceTimersByTime(50)
    debounced()
    vi.advanceTimersByTime(50)
    expect(fn).not.toHaveBeenCalled()

    vi.advanceTimersByTime(50)
    expect(fn).toHaveBeenCalledOnce()
  })

  it('passes arguments to the debounced function', () => {
    const fn = vi.fn()
    const debounced = debounce(fn, 100)

    debounced('a', 'b')
    vi.advanceTimersByTime(100)
    expect(fn).toHaveBeenCalledWith('a', 'b')
  })
})
