/**
 * 인증 API 서비스
 * 로그인, 로그아웃, 토큰 갱신 등의 인증 관련 API를 제공합니다
 */

import api from './api'
import type { 
  LoginRequest, 
  LoginResponse, 
  RefreshTokenRequest, 
  RefreshTokenResponse,
  UserInfo 
} from '@/types/auth'

// 인증 API 기본 URL (api.ts에서 이미 /api가 설정되어 있음)
const AUTH_BASE_URL = '/auth'

/**
 * 인증 관리 API 서비스
 */
export const authApi = {
  /**
   * 로그인
   */
  login: async (credentials: LoginRequest): Promise<LoginResponse> => {
    const response = await api.post(`${AUTH_BASE_URL}/login`, credentials)
    console.log('로그인 API 전체 응답:', response)
    console.log('로그인 API response.data:', response.data)
    // 백엔드는 ApiResponse<LoginResponse> 형식으로 응답하므로 response.data.data를 반환
    return response.data.data
  },

  /**
   * 로그아웃
   */
  logout: async (): Promise<void> => {
    await api.post(`${AUTH_BASE_URL}/logout`)
  },

  /**
   * 토큰 갱신
   */
  refreshToken: async (refreshToken: string): Promise<RefreshTokenResponse> => {
    const response = await api.post(`${AUTH_BASE_URL}/refresh`, {
      refreshToken
    })
    // 백엔드는 ApiResponse<LoginResponse> 형식으로 응답하므로 response.data.data를 반환
    return response.data.data
  },

  /**
   * 현재 사용자 정보 조회
   */
  getCurrentUser: async (): Promise<UserInfo> => {
    console.log('현재 사용자 정보 조회 API 호출:', `${AUTH_BASE_URL}/me`)
    const response = await api.get(`${AUTH_BASE_URL}/me`)
    console.log('현재 사용자 정보 조회 API 응답:', response)
    console.log('현재 사용자 정보:', response.data)
    console.log('현재 사용자 정보 상세:', JSON.stringify(response.data, null, 2))
    // 백엔드는 ApiResponse<UserInfo> 형식으로 응답하므로 response.data.data를 반환
    return response.data.data
  },

  /**
   * 비밀번호 변경
   */
  changePassword: async (currentPassword: string, newPassword: string): Promise<void> => {
    await api.post(`${AUTH_BASE_URL}/change-password`, {
      currentPassword,
      newPassword
    })
  },

  /**
   * 비밀번호 초기화 요청
   */
  requestPasswordReset: async (email: string): Promise<void> => {
    await api.post(`${AUTH_BASE_URL}/reset-password`, { email })
  }
}

/**
 * 토큰 관리 유틸리티
 */
export const tokenUtils = {
  /**
   * 액세스 토큰 저장
   */
  setAccessToken: (token: string): void => {
    localStorage.setItem('accessToken', token)
  },

  /**
   * 리프레시 토큰 저장
   */
  setRefreshToken: (token: string): void => {
    localStorage.setItem('refreshToken', token)
  },

  /**
   * 액세스 토큰 조회
   */
  getAccessToken: (): string | null => {
    return localStorage.getItem('accessToken')
  },

  /**
   * 리프레시 토큰 조회
   */
  getRefreshToken: (): string | null => {
    return localStorage.getItem('refreshToken')
  },

  /**
   * 모든 토큰 제거
   */
  clearTokens: (): void => {
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
  },

  /**
   * 토큰이 유효한지 확인
   */
  isTokenValid: (token: string): boolean => {
    if (!token) return false
    
    try {
      // JWT 토큰 디코딩하여 만료 시간 확인
      const payload = JSON.parse(atob(token.split('.')[1]))
      const currentTime = Date.now() / 1000
      return payload.exp > currentTime
    } catch {
      return false
    }
  }
}



