/**
 * 디바운스 훅
 * 입력값의 변경을 지연시켜 불필요한 API 호출을 방지합니다
 */

import { useState, useEffect } from 'react'

/**
 * 디바운스 훅
 * @param value 디바운스할 값
 * @param delay 지연 시간 (밀리초)
 * @returns 디바운스된 값
 */
export function useDebounce<T>(value: T, delay: number): T {
  const [debouncedValue, setDebouncedValue] = useState<T>(value)

  useEffect(() => {
    // 지연 시간 후에 값을 업데이트하는 타이머 설정
    const handler = setTimeout(() => {
      setDebouncedValue(value)
    }, delay)

    // 값이 변경되거나 컴포넌트가 언마운트될 때 타이머 정리
    return () => {
      clearTimeout(handler)
    }
  }, [value, delay])

  return debouncedValue
}




