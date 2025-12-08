import { describe, it, expect } from 'vitest'

/**
 * 샘플 테스트 파일
 *
 * 목적:
 * 1. Vitest 프레임워크가 정상 작동하는지 확인
 * 2. CI/CD 파이프라인이 테스트를 실행하는지 검증
 * 3. 개발자를 위한 테스트 작성 예시 제공
 */

describe('Example Test Suite', () => {
  it('should pass basic assertion', () => {
    expect(1 + 1).toBe(2)
  })

  it('should handle string operations', () => {
    expect('hello world').toContain('world')
  })

  it('should work with arrays', () => {
    const numbers = [1, 2, 3, 4, 5]
    expect(numbers).toHaveLength(5)
    expect(numbers).toContain(3)
  })

  it('should handle objects', () => {
    const user = {
      name: 'Test User',
      role: 'ADMIN',
      isActive: true,
    }
    expect(user).toHaveProperty('name', 'Test User')
    expect(user.isActive).toBe(true)
  })

  it('should handle async operations', async () => {
    const promise = Promise.resolve('success')
    await expect(promise).resolves.toBe('success')
  })
})

describe('Math Utilities Example', () => {
  const add = (a: number, b: number) => a + b
  const multiply = (a: number, b: number) => a * b

  it('should add numbers correctly', () => {
    expect(add(2, 3)).toBe(5)
    expect(add(-1, 1)).toBe(0)
  })

  it('should multiply numbers correctly', () => {
    expect(multiply(2, 3)).toBe(6)
    expect(multiply(-2, 3)).toBe(-6)
  })
})
