/**
 * 포맷팅 유틸리티 함수들
 * 숫자, 날짜, 통화 등을 한국 로케일에 맞게 포맷팅합니다
 */

/**
 * 숫자를 한국 로케일로 포맷팅 (천 단위 콤마)
 */
export function formatNumber(value: number | undefined | null): string {
  if (value === undefined || value === null) return '0'
  return value.toLocaleString('ko-KR')
}

/**
 * 통화를 한국 원화로 포맷팅
 */
export function formatCurrency(
  value: number | undefined | null, 
  showSymbol: boolean = true,
  abbreviated: boolean = false
): string {
  if (value === undefined || value === null) return showSymbol ? '₩0' : '0'
  
  if (abbreviated) {
    if (value >= 1000000000) {
      return `${showSymbol ? '₩' : ''}${(value / 1000000000).toFixed(1)}억`
    }
    if (value >= 100000000) {
      return `${showSymbol ? '₩' : ''}${(value / 100000000).toFixed(1)}억`
    }
    if (value >= 10000) {
      return `${showSymbol ? '₩' : ''}${(value / 10000).toFixed(0)}만`
    }
  }
  
  const formatted = value.toLocaleString('ko-KR')
  return showSymbol ? `₩${formatted}` : formatted
}

/**
 * 날짜를 한국 로케일로 포맷팅
 */
export function formatDate(
  date: string | Date | undefined | null,
  format: 'full' | 'date' | 'time' | 'datetime' | 'MM/dd' | 'relative' = 'date'
): string {
  if (!date) return '-'
  
  const dateObj = typeof date === 'string' ? new Date(date) : date
  
  if (isNaN(dateObj.getTime())) return '-'
  
  switch (format) {
    case 'full':
      return dateObj.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        weekday: 'long'
      })
    
    case 'date':
      return dateObj.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      })
    
    case 'time':
      return dateObj.toLocaleTimeString('ko-KR', {
        hour: '2-digit',
        minute: '2-digit'
      })
    
    case 'datetime':
      return dateObj.toLocaleString('ko-KR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    
    case 'MM/dd':
      return dateObj.toLocaleDateString('ko-KR', {
        month: '2-digit',
        day: '2-digit'
      })
    
    case 'relative':
      return formatRelativeDate(dateObj)
    
    default:
      return dateObj.toLocaleDateString('ko-KR')
  }
}

/**
 * 상대적 날짜 포맷팅 (예: "3일 전", "방금 전")
 */
export function formatRelativeDate(date: Date): string {
  const now = new Date()
  const diffInMs = now.getTime() - date.getTime()
  const diffInSeconds = Math.floor(diffInMs / 1000)
  const diffInMinutes = Math.floor(diffInSeconds / 60)
  const diffInHours = Math.floor(diffInMinutes / 60)
  const diffInDays = Math.floor(diffInHours / 24)
  
  if (diffInSeconds < 60) {
    return '방금 전'
  } else if (diffInMinutes < 60) {
    return `${diffInMinutes}분 전`
  } else if (diffInHours < 24) {
    return `${diffInHours}시간 전`
  } else if (diffInDays < 7) {
    return `${diffInDays}일 전`
  } else if (diffInDays < 30) {
    const weeks = Math.floor(diffInDays / 7)
    return `${weeks}주 전`
  } else if (diffInDays < 365) {
    const months = Math.floor(diffInDays / 30)
    return `${months}개월 전`
  } else {
    const years = Math.floor(diffInDays / 365)
    return `${years}년 전`
  }
}

/**
 * 파일 크기를 사람이 읽기 쉬운 형태로 포맷팅
 */
export function formatFileSize(bytes: number | undefined | null): string {
  if (!bytes || bytes === 0) return '0 B'
  
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  
  return `${parseFloat((bytes / Math.pow(k, i)).toFixed(1))} ${sizes[i]}`
}

/**
 * 퍼센트 포맷팅
 */
export function formatPercentage(
  value: number | undefined | null,
  decimals: number = 1
): string {
  if (value === undefined || value === null) return '0%'
  return `${value.toFixed(decimals)}%`
}

/**
 * 전화번호 포맷팅
 */
export function formatPhoneNumber(phone: string | undefined | null): string {
  if (!phone) return '-'
  
  // 숫자만 추출
  const numbers = phone.replace(/\D/g, '')
  
  // 휴대폰 번호 (010-xxxx-xxxx)
  if (numbers.length === 11 && numbers.startsWith('010')) {
    return `${numbers.slice(0, 3)}-${numbers.slice(3, 7)}-${numbers.slice(7)}`
  }
  
  // 일반 전화번호 (02-xxx-xxxx, 031-xxx-xxxx 등)
  if (numbers.length >= 9 && numbers.length <= 11) {
    if (numbers.startsWith('02')) {
      // 서울 지역번호
      return `${numbers.slice(0, 2)}-${numbers.slice(2, -4)}-${numbers.slice(-4)}`
    } else {
      // 기타 지역번호
      return `${numbers.slice(0, 3)}-${numbers.slice(3, -4)}-${numbers.slice(-4)}`
    }
  }
  
  return phone
}

/**
 * 사업자등록번호 포맷팅
 */
export function formatBusinessNumber(number: string | undefined | null): string {
  if (!number) return '-'
  
  const numbers = number.replace(/\D/g, '')
  
  if (numbers.length === 10) {
    return `${numbers.slice(0, 3)}-${numbers.slice(3, 5)}-${numbers.slice(5)}`
  }
  
  return number
}

/**
 * 주민등록번호 마스킹
 */
export function maskResidentNumber(number: string | undefined | null): string {
  if (!number) return '-'
  
  const numbers = number.replace(/\D/g, '')
  
  if (numbers.length === 13) {
    return `${numbers.slice(0, 6)}-${numbers.slice(6, 7)}******`
  }
  
  return number
}

/**
 * 계좌번호 마스킹
 */
export function maskAccountNumber(account: string | undefined | null): string {
  if (!account) return '-'
  
  if (account.length > 8) {
    const start = account.slice(0, 4)
    const end = account.slice(-4)
    const middle = '*'.repeat(account.length - 8)
    return `${start}${middle}${end}`
  }
  
  return account
}

/**
 * 문자열 길이 제한 및 말줄임표 추가
 */
export function truncateText(
  text: string | undefined | null,
  maxLength: number = 50
): string {
  if (!text) return ''
  
  if (text.length <= maxLength) return text
  
  return `${text.slice(0, maxLength)}...`
}

/**
 * 한국어 조사 처리 (은/는, 이/가, 을/를)
 */
export function getKoreanParticle(
  word: string,
  type: '은는' | '이가' | '을를'
): string {
  if (!word) return ''
  
  const lastChar = word[word.length - 1]
  const lastCharCode = lastChar.charCodeAt(0)
  
  // 한글이 아닌 경우
  if (lastCharCode < 0xAC00 || lastCharCode > 0xD7A3) {
    return type === '은는' ? '는' : type === '이가' ? '가' : '를'
  }
  
  // 받침 있는지 확인
  const hasJongseong = (lastCharCode - 0xAC00) % 28 !== 0
  
  switch (type) {
    case '은는':
      return hasJongseong ? '은' : '는'
    case '이가':
      return hasJongseong ? '이' : '가'
    case '을를':
      return hasJongseong ? '을' : '를'
    default:
      return ''
  }
}

/**
 * 배열을 한국어로 나열 ("A, B, C" -> "A, B 및 C")
 */
export function formatKoreanList(items: string[]): string {
  if (items.length === 0) return ''
  if (items.length === 1) return items[0]
  if (items.length === 2) return `${items[0]} 및 ${items[1]}`
  
  const lastItem = items[items.length - 1]
  const otherItems = items.slice(0, -1).join(', ')
  
  return `${otherItems} 및 ${lastItem}`
}

/**
 * 숫자를 한국어 단위로 변환 (예: 10000 -> "1만")
 */
export function formatKoreanNumber(num: number): string {
  if (num === 0) return '0'
  
  const units = ['', '만', '억', '조']
  let result = ''
  let unitIndex = 0
  
  while (num > 0 && unitIndex < units.length) {
    const remainder = num % 10000
    
    if (remainder > 0) {
      result = `${remainder.toLocaleString()}${units[unitIndex]} ${result}`.trim()
    }
    
    num = Math.floor(num / 10000)
    unitIndex++
  }
  
  return result || '0'
}




