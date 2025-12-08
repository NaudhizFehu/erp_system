/**
 * 로그인 페이지
 * 사용자 인증을 위한 로그인 폼을 제공합니다
 */

import { Loader2, Eye, EyeOff } from 'lucide-react'
import React, { useState, useEffect } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'

import { Alert, AlertDescription } from '@/components/ui/alert'
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { useAuth } from '@/contexts/AuthContext'
import type { LoginRequest } from '@/types/auth'

/**
 * 로그인 페이지 컴포넌트
 */
export function LoginPage() {
  const navigate = useNavigate()
  const location = useLocation()
  const { login, isAuthenticated, isLoading } = useAuth()

  // 폼 상태
  const [formData, setFormData] = useState<LoginRequest>({
    usernameOrEmail: '',
    password: '',
    rememberMe: false,
  })
  const [showPassword, setShowPassword] = useState(false)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)

  // 이미 로그인된 경우 리다이렉트
  useEffect(() => {
    if (isAuthenticated) {
      const from = location.state?.from?.pathname || '/'
      navigate(from, { replace: true })
    }
  }, [isAuthenticated, navigate, location])

  // 폼 입력 처리
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: value,
    }))
    // 에러 메시지 초기화
    if (error) setError(null)
  }

  // 폼 제출 처리
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    // 입력 검증
    if (!formData.usernameOrEmail.trim()) {
      setError('사용자명 또는 이메일을 입력해주세요')
      return
    }

    if (!formData.password.trim()) {
      setError('비밀번호를 입력해주세요')
      return
    }

    setIsSubmitting(true)
    setError(null)

    try {
      await login(formData)
      // 로그인 성공 시 리다이렉트는 useEffect에서 처리
    } catch (error: any) {
      setError(error.response?.data?.message || '로그인에 실패했습니다')
    } finally {
      setIsSubmitting(false)
    }
  }

  // 로딩 중일 때
  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-gray-50">
        <div className="flex items-center space-x-2">
          <Loader2 className="h-6 w-6 animate-spin" />
          <span>인증 상태를 확인하는 중...</span>
        </div>
      </div>
    )
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-50 px-4 py-12 sm:px-6 lg:px-8">
      <div className="w-full max-w-md space-y-8">
        {/* 헤더 */}
        <div className="text-center">
          <h1 className="text-3xl font-bold text-gray-900">ERP 시스템</h1>
          <p className="mt-2 text-sm text-gray-600">
            계정에 로그인하여 시스템에 접근하세요
          </p>
        </div>

        {/* 로그인 폼 */}
        <Card>
          <CardHeader>
            <CardTitle>로그인</CardTitle>
            <CardDescription>
              사용자명과 비밀번호를 입력해주세요
            </CardDescription>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit} className="space-y-4">
              {/* 에러 메시지 */}
              {error && (
                <Alert variant="destructive">
                  <AlertDescription>{error}</AlertDescription>
                </Alert>
              )}

              {/* 사용자명 또는 이메일 입력 */}
              <div className="space-y-2">
                <Label htmlFor="usernameOrEmail">사용자명 또는 이메일</Label>
                <Input
                  id="usernameOrEmail"
                  name="usernameOrEmail"
                  type="text"
                  value={formData.usernameOrEmail}
                  onChange={handleInputChange}
                  placeholder="사용자명 또는 이메일을 입력하세요"
                  disabled={isSubmitting}
                  required
                />
              </div>

              {/* 비밀번호 입력 */}
              <div className="space-y-2">
                <Label htmlFor="password">비밀번호</Label>
                <div className="relative">
                  <Input
                    id="password"
                    name="password"
                    type={showPassword ? 'text' : 'password'}
                    value={formData.password}
                    onChange={handleInputChange}
                    placeholder="비밀번호를 입력하세요"
                    disabled={isSubmitting}
                    required
                  />
                  <Button
                    type="button"
                    variant="ghost"
                    size="sm"
                    className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
                    onClick={() => setShowPassword(!showPassword)}
                    disabled={isSubmitting}
                  >
                    {showPassword ? (
                      <EyeOff className="h-4 w-4" />
                    ) : (
                      <Eye className="h-4 w-4" />
                    )}
                  </Button>
                </div>
              </div>

              {/* 로그인 버튼 */}
              <Button type="submit" className="w-full" disabled={isSubmitting}>
                {isSubmitting ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    로그인 중...
                  </>
                ) : (
                  '로그인'
                )}
              </Button>
            </form>

            {/* 개발용 테스트 계정 정보 */}
            <div className="mt-6 rounded-lg border border-blue-200 bg-blue-50 p-4">
              <h3 className="mb-3 border-b border-blue-200 pb-2 text-sm font-semibold text-blue-900">
                🔐 개발용 테스트 계정 (총 9개)
              </h3>
              <div className="space-y-3 text-xs text-blue-800">
                {/* 시스템 관리자 */}
                <div className="rounded bg-blue-100 p-2">
                  <p className="mb-1 font-semibold text-blue-900">
                    🔑 시스템 관리자
                  </p>
                  <p className="pl-2">
                    <strong>superadmin</strong> / super123 (전체 관리)
                  </p>
                </div>

                {/* ABC기업 */}
                <div>
                  <p className="mb-1 font-semibold text-blue-900">
                    👔 ABC기업 (4개)
                  </p>
                  <div className="space-y-0.5 pl-2">
                    <p>
                      <strong>admin</strong> / admin123 (회사 관리자)
                    </p>
                    <p>
                      <strong>hr_manager</strong> / hr123 (인사팀 매니저)
                    </p>
                    <p>
                      <strong>manager</strong> / manager123 (개발팀 매니저)
                    </p>
                    <p>
                      <strong>user</strong> / user123 (일반 사용자)
                    </p>
                  </div>
                </div>

                {/* XYZ그룹 */}
                <div>
                  <p className="mb-1 font-semibold text-blue-900">
                    🏢 XYZ그룹 (2개)
                  </p>
                  <div className="space-y-0.5 pl-2">
                    <p>
                      <strong>xyz_admin</strong> / xyz123 (회사 관리자)
                    </p>
                    <p>
                      <strong>xyz_manager</strong> / xyz123 (인사팀 매니저)
                    </p>
                  </div>
                </div>

                {/* DEF코퍼레이션 */}
                <div>
                  <p className="mb-1 font-semibold text-blue-900">
                    🏭 DEF코퍼레이션 (2개)
                  </p>
                  <div className="space-y-0.5 pl-2">
                    <p>
                      <strong>def_admin</strong> / def123 (회사 관리자)
                    </p>
                    <p>
                      <strong>def_user</strong> / def123 (일반 사용자)
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* 푸터 */}
        <div className="text-center text-sm text-gray-500">
          <p>© 2024 ERP 시스템. All rights reserved.</p>
        </div>
      </div>
    </div>
  )
}
