/**
 * 역할 기반 접근 제어 컴포넌트
 * 특정 역할만 접근 가능한 페이지에 사용합니다
 */

import React from 'react'
import { Navigate } from 'react-router-dom'
import { useAuth } from '@/contexts/AuthContext'

interface RoleProtectedRouteProps {
  children: React.ReactNode
  requiredRoles: ('SUPER_ADMIN' | 'ADMIN' | 'MANAGER' | 'USER')[]
}

/**
 * 역할 보호 라우트 컴포넌트
 * 필요한 역할이 없으면 접근 권한 없음 메시지 표시
 */
export function RoleProtectedRoute({ children, requiredRoles }: RoleProtectedRouteProps) {
  const { user } = useAuth()

  // 권한 체크
  if (user?.role && !requiredRoles.includes(user.role as any)) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <h1 className="text-2xl font-bold text-gray-900 mb-2">접근 권한 없음</h1>
          <p className="text-gray-600 mb-4">이 페이지에 접근할 권한이 없습니다.</p>
          <Navigate to="/" replace />
        </div>
      </div>
    )
  }

  return <>{children}</>
}
