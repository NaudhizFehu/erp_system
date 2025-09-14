/**
 * 공통 타입 정의
 * 전체 애플리케이션에서 사용되는 공통 인터페이스들을 정의합니다
 */

/**
 * API 응답 기본 구조
 */
export interface ApiResponse<T = any> {
  success: boolean
  data: T
  message: string
  timestamp: string
  path?: string
}

/**
 * 페이지네이션 응답 구조
 */
export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
  numberOfElements: number
  empty: boolean
}

/**
 * 정렬 정보
 */
export interface Sort {
  property: string
  direction: 'ASC' | 'DESC'
}

/**
 * 페이지 요청 파라미터
 */
export interface PageRequest {
  page?: number
  size?: number
  sort?: Sort[]
}

/**
 * 검색 필터 기본 인터페이스
 */
export interface BaseSearchFilter extends PageRequest {
  searchTerm?: string
  startDate?: string
  endDate?: string
}

/**
 * 선택 옵션 인터페이스
 */
export interface SelectOption {
  value: string | number
  label: string
  disabled?: boolean
}

/**
 * 테이블 컬럼 정의
 */
export interface TableColumn<T = any> {
  key: string
  title: string
  dataIndex?: keyof T
  width?: string | number
  sortable?: boolean
  filterable?: boolean
  render?: (value: any, record: T, index: number) => React.ReactNode
}

/**
 * 폼 필드 에러
 */
export interface FieldError {
  field: string
  message: string
  code?: string
}

/**
 * 업로드 파일 정보
 */
export interface UploadFile {
  id?: string
  name: string
  size: number
  type: string
  url?: string
  status: 'uploading' | 'done' | 'error'
  percent?: number
  error?: string
}

/**
 * 차트 데이터 포인트
 */
export interface ChartDataPoint {
  name: string
  value: number
  color?: string
  percentage?: number
}

/**
 * 알림 메시지
 */
export interface NotificationMessage {
  id: string
  type: 'info' | 'success' | 'warning' | 'error'
  title: string
  message?: string
  timestamp: string
  read: boolean
  actions?: Array<{
    label: string
    action: () => void
  }>
}

/**
 * 사용자 권한
 */
export interface Permission {
  resource: string
  actions: string[]
}

/**
 * 메뉴 아이템
 */
export interface MenuItem {
  id: string
  title: string
  icon?: React.ComponentType<any>
  path?: string
  children?: MenuItem[]
  permission?: string
  badge?: string | number
}

/**
 * 브레드크럼 아이템
 */
export interface BreadcrumbItem {
  title: string
  path?: string
}

/**
 * 통계 카드 데이터
 */
export interface StatCard {
  title: string
  value: string | number
  change?: {
    value: number
    type: 'increase' | 'decrease'
    period?: string
  }
  icon?: React.ComponentType<any>
  color?: 'blue' | 'green' | 'yellow' | 'red' | 'purple' | 'gray'
}

/**
 * 필터 조건
 */
export interface FilterCondition {
  field: string
  operator: 'eq' | 'ne' | 'gt' | 'gte' | 'lt' | 'lte' | 'like' | 'in' | 'between'
  value: any
  values?: any[]
}

/**
 * 내보내기 옵션
 */
export interface ExportOptions {
  format: 'csv' | 'excel' | 'pdf'
  columns?: string[]
  filters?: FilterCondition[]
  filename?: string
}

/**
 * 가져오기 결과
 */
export interface ImportResult {
  totalRows: number
  successRows: number
  errorRows: number
  errors: Array<{
    row: number
    field?: string
    message: string
  }>
}

/**
 * 감사 로그
 */
export interface AuditLog {
  id: string
  entityType: string
  entityId: string
  action: 'CREATE' | 'UPDATE' | 'DELETE' | 'VIEW'
  userId: string
  userName: string
  timestamp: string
  changes?: Array<{
    field: string
    oldValue?: any
    newValue?: any
  }>
  ipAddress?: string
  userAgent?: string
}

/**
 * 시스템 설정
 */
export interface SystemSetting {
  key: string
  value: any
  type: 'string' | 'number' | 'boolean' | 'json'
  description?: string
  category?: string
  editable?: boolean
}

/**
 * 에러 상세 정보
 */
export interface ErrorDetail {
  code: string
  message: string
  field?: string
  rejectedValue?: any
}

/**
 * 비즈니스 규칙 위반 정보
 */
export interface BusinessRuleViolation {
  rule: string
  message: string
  severity: 'ERROR' | 'WARNING' | 'INFO'
}

/**
 * 검증 결과
 */
export interface ValidationResult {
  valid: boolean
  errors: FieldError[]
  warnings?: FieldError[]
}

/**
 * 작업 진행 상태
 */
export interface ProgressStatus {
  current: number
  total: number
  percentage: number
  message?: string
  completed: boolean
  error?: string
}




