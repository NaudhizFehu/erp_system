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
  user: UserInfo
}

export interface UserInfo {
  id: number
  username: string
  email: string
  fullName: string
  role: string
  phone?: string
  phoneNumber?: string
  department?: DepartmentInfo
  position?: string
  company?: CompanyInfo
  lastLoginAt?: string
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
  user: UserInfo | null
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
