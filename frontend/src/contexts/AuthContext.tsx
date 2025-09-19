/**
 * 인증 컨텍스트
 * 전역 인증 상태 관리 및 인증 관련 함수들을 제공합니다
 */

import React, { createContext, useContext, useReducer, useEffect, ReactNode } from 'react'
import { authApi, tokenUtils } from '@/services/authApi'
import type { AuthState, LoginRequest, UserInfo } from '@/types/auth'
import toast from 'react-hot-toast'

// 액션 타입 정의
type AuthAction =
  | { type: 'LOGIN_START' }
  | { type: 'LOGIN_SUCCESS'; payload: { user: UserInfo; accessToken: string; refreshToken: string } }
  | { type: 'LOGIN_FAILURE' }
  | { type: 'LOGOUT' }
  | { type: 'SET_LOADING'; payload: boolean }
  | { type: 'SET_USER'; payload: UserInfo | null }

// 초기 상태
const initialState: AuthState = {
  isAuthenticated: false,
  user: null,
  accessToken: null,
  refreshToken: null,
  isLoading: true
}

// 리듀서
const authReducer = (state: AuthState, action: AuthAction): AuthState => {
  switch (action.type) {
    case 'LOGIN_START':
      return { ...state, isLoading: true }
    
    case 'LOGIN_SUCCESS':
      return {
        ...state,
        isAuthenticated: true,
        user: action.payload.user,
        accessToken: action.payload.accessToken,
        refreshToken: action.payload.refreshToken,
        isLoading: false
      }
    
    case 'LOGIN_FAILURE':
      return {
        ...state,
        isAuthenticated: false,
        user: null,
        accessToken: null,
        refreshToken: null,
        isLoading: false
      }
    
    case 'LOGOUT':
      return {
        ...state,
        isAuthenticated: false,
        user: null,
        accessToken: null,
        refreshToken: null,
        isLoading: false
      }
    
    case 'SET_LOADING':
      return { ...state, isLoading: action.payload }
    
    case 'SET_USER':
      return { ...state, user: action.payload }
    
    default:
      return state
  }
}

// 컨텍스트 타입
interface AuthContextType extends AuthState {
  login: (credentials: LoginRequest) => Promise<void>
  logout: () => Promise<void>
  refreshAuth: () => Promise<void>
}

// 컨텍스트 생성
const AuthContext = createContext<AuthContextType | undefined>(undefined)

// 프로바이더 컴포넌트
interface AuthProviderProps {
  children: ReactNode
}

export function AuthProvider({ children }: AuthProviderProps) {
  const [state, dispatch] = useReducer(authReducer, initialState)

  // 초기 인증 상태 확인
  useEffect(() => {
    const initializeAuth = async () => {
      const accessToken = tokenUtils.getAccessToken()
      const refreshToken = tokenUtils.getRefreshToken()

      console.log('초기 인증 상태 확인:', { 
        hasAccessToken: !!accessToken, 
        hasRefreshToken: !!refreshToken,
        accessTokenValid: accessToken ? tokenUtils.isTokenValid(accessToken) : false,
        refreshTokenValid: refreshToken ? tokenUtils.isTokenValid(refreshToken) : false
      })

      // 토큰이 없으면 즉시 로그아웃 상태로 설정
      if (!accessToken && !refreshToken) {
        console.log('토큰이 없음, 로그아웃 상태로 설정')
        dispatch({ type: 'LOGIN_FAILURE' })
        return
      }

      if (accessToken && tokenUtils.isTokenValid(accessToken)) {
        try {
          // 토큰이 유효하면 사용자 정보 조회
          console.log('액세스 토큰이 유효함, 사용자 정보 조회 시도')
          const user = await authApi.getCurrentUser()
          dispatch({
            type: 'LOGIN_SUCCESS',
            payload: { user, accessToken, refreshToken: refreshToken || '' }
          })
        } catch (error) {
          console.warn('토큰이 유효하지 않음, 로그아웃 처리:', error)
          // 토큰이 유효하지 않으면 로그아웃 처리
          tokenUtils.clearTokens()
          dispatch({ type: 'LOGIN_FAILURE' })
        }
      } else if (refreshToken && tokenUtils.isTokenValid(refreshToken)) {
        try {
          // 리프레시 토큰으로 새 액세스 토큰 발급
          console.log('리프레시 토큰으로 새 액세스 토큰 발급 시도')
          const response = await authApi.refreshToken(refreshToken)
          tokenUtils.setAccessToken(response.accessToken)
          tokenUtils.setRefreshToken(response.refreshToken)
          
          const user = await authApi.getCurrentUser()
          dispatch({
            type: 'LOGIN_SUCCESS',
            payload: { 
              user, 
              accessToken: response.accessToken, 
              refreshToken: response.refreshToken 
            }
          })
        } catch (error) {
          console.warn('리프레시 토큰이 유효하지 않음, 로그아웃 처리:', error)
          // 리프레시 토큰도 유효하지 않으면 로그아웃 처리
          tokenUtils.clearTokens()
          dispatch({ type: 'LOGIN_FAILURE' })
        }
      } else {
        // 토큰이 없거나 유효하지 않으면 로그아웃 상태
        console.log('토큰이 없거나 유효하지 않음, 로그아웃 상태로 설정')
        dispatch({ type: 'LOGIN_FAILURE' })
      }
    }

    initializeAuth()
  }, [])

  // 로그인 함수
  const login = async (credentials: LoginRequest): Promise<void> => {
    try {
      dispatch({ type: 'LOGIN_START' })
      
      const response = await authApi.login(credentials)
      
      // 토큰 저장
      tokenUtils.setAccessToken(response.accessToken)
      tokenUtils.setRefreshToken(response.refreshToken)
      
      dispatch({
        type: 'LOGIN_SUCCESS',
        payload: {
          user: response.user,
          accessToken: response.accessToken,
          refreshToken: response.refreshToken
        }
      })
      
      toast.success('로그인에 성공했습니다')
    } catch (error: any) {
      dispatch({ type: 'LOGIN_FAILURE' })
      const errorMessage = error.response?.data?.message || '로그인에 실패했습니다'
      toast.error(errorMessage)
      throw error
    }
  }

  // 로그아웃 함수
  const logout = async (): Promise<void> => {
    try {
      await authApi.logout()
    } catch (error) {
      // 로그아웃 API 호출 실패해도 클라이언트에서는 로그아웃 처리
      console.warn('로그아웃 API 호출 실패:', error)
    } finally {
      // 토큰 제거 및 상태 초기화
      tokenUtils.clearTokens()
      dispatch({ type: 'LOGOUT' })
      toast.success('로그아웃되었습니다')
    }
  }

  // 인증 갱신 함수
  const refreshAuth = async (): Promise<void> => {
    const refreshToken = tokenUtils.getRefreshToken()
    
    if (!refreshToken || !tokenUtils.isTokenValid(refreshToken)) {
      dispatch({ type: 'LOGOUT' })
      return
    }

    try {
      const response = await authApi.refreshToken(refreshToken)
      tokenUtils.setAccessToken(response.accessToken)
      tokenUtils.setRefreshToken(response.refreshToken)
      
      const user = await authApi.getCurrentUser()
      dispatch({
        type: 'LOGIN_SUCCESS',
        payload: {
          user,
          accessToken: response.accessToken,
          refreshToken: response.refreshToken
        }
      })
    } catch (error) {
      tokenUtils.clearTokens()
      dispatch({ type: 'LOGOUT' })
    }
  }

  const value: AuthContextType = {
    ...state,
    login,
    logout,
    refreshAuth
  }

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  )
}

// 훅
export function useAuth(): AuthContextType {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}



