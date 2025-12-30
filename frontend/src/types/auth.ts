/**
 * 인증 관련 타입 정의
 */

export interface LoginRequest {
  usernameOrEmail: string
  password: string
  rememberMe?: boolean
}

export interface LoginResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
  employee: EmployeeInfo
}

export interface EmployeeInfo {
  id: number
  username: string
  email: string
  name: string
  role: string
  phoneNumber?: string
  phone?: string
  mobile?: string
  fullName?: string
  department?: DepartmentInfo
  position?: PositionInfo | string
  company?: CompanyInfo
  employeeNumber: string
  employeeId?: number
  lastLoginAt?: string
  birthDate?: string
  gender?: string
  hireDate?: string
  employmentStatus?: string
  yearsOfService?: number
  bankName?: string
  accountNumber?: string
  accountHolder?: string
  memo?: string
  isActive?: boolean
  createdAt?: string
  updatedAt?: string
}

export interface PositionInfo {
  id: number
  name: string
  level?: number
}

export interface CompanyInfo {
  id: number
  name: string
  companyCode?: string | null
}

export interface DepartmentInfo {
  id: number
  name: string
  departmentCode?: string | null
}

export interface AuthState {
  isAuthenticated: boolean
  employee: EmployeeInfo | null
  accessToken: string | null
  refreshToken: string | null
  isLoading: boolean
}

export interface RefreshTokenRequest {
  refreshToken: string
}

export interface RefreshTokenResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
}
