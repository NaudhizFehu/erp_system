/**
 * 검색 타입 설정
 * 전역 검색에서 지원하는 검색 타입들을 정의합니다
 */

export interface SearchTypeInfo {
  value: string
  label: string
  color: string
  bgColor: string
  icon: string
}

/**
 * 지원되는 검색 타입 정보
 */
export const SEARCH_TYPES: Record<string, SearchTypeInfo> = {
  employee: {
    value: 'employee',
    label: '직원',
    color: 'text-blue-600',
    bgColor: 'bg-blue-50',
    icon: 'Users'
  },
  product: {
    value: 'product',
    label: '상품',
    color: 'text-green-600',
    bgColor: 'bg-green-50',
    icon: 'Package'
  },
  order: {
    value: 'order',
    label: '주문',
    color: 'text-purple-600',
    bgColor: 'bg-purple-50',
    icon: 'ShoppingCart'
  },
  customer: {
    value: 'customer',
    label: '고객',
    color: 'text-orange-600',
    bgColor: 'bg-orange-50',
    icon: 'Building2'
  },
  department: {
    value: 'department',
    label: '부서',
    color: 'text-indigo-600',
    bgColor: 'bg-indigo-50',
    icon: 'FolderOpen'
  },
  company: {
    value: 'company',
    label: '회사',
    color: 'text-gray-600',
    bgColor: 'bg-gray-50',
    icon: 'Building2'
  }
}

/**
 * 검색 타입 정보를 가져오는 헬퍼 함수
 */
export const getSearchTypeInfo = (type: string): SearchTypeInfo | null => {
  return SEARCH_TYPES[type] || null
}

/**
 * 모든 검색 타입 목록을 반환하는 헬퍼 함수
 */
export const getAllSearchTypes = (): SearchTypeInfo[] => {
  return Object.values(SEARCH_TYPES)
}

/**
 * 활성화된 검색 타입 목록을 반환하는 헬퍼 함수
 * 향후 사용자 권한이나 설정에 따라 동적으로 필터링 가능
 */
export const getActiveSearchTypes = (): SearchTypeInfo[] => {
  return getAllSearchTypes()
}

