/**
 * 보호된 라우트 컴포넌트
 * 인증이 필요한 페이지에 접근할 때 인증 상태를 확인합니다
 */

import React from 'react'
import { Navigate, useLocation } from 'react-router-dom'
import { useAuth } from '@/contexts/AuthContext'
import { Loader2 } from 'lucide-react'

interface ProtectedRouteProps {
  children: React.ReactNode
}

/**
 * 보호된 라우트 컴포넌트
 * 인증되지 않은 사용자는 로그인 페이지로 리다이렉트합니다
 */
export function ProtectedRoute({ children }: ProtectedRouteProps) {
  const { isAuthenticated, isLoading } = useAuth()
  const location = useLocation()

  // 로딩 중일 때
  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="flex items-center space-x-2">
          <Loader2 className="h-6 w-6 animate-spin" />
          <span>인증 상태를 확인하는 중...</span>
        </div>
      </div>
    )
  }

  // 인증되지 않은 경우 로그인 페이지로 리다이렉트
  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />
  }

  // 인증된 경우 자식 컴포넌트 렌더링
  return <>{children}</>
}



