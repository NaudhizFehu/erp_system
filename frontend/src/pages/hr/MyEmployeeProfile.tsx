/**
 * 본인 직원 정보 조회 페이지
 * 모든 권한(SUPER_ADMIN, ADMIN, MANAGER, USER)이 본인의 직원 정보를 조회할 수 있습니다
 */

import { Loader2 } from 'lucide-react'
import { useEffect, useState } from 'react'
import toast from 'react-hot-toast'
import { useNavigate } from 'react-router-dom'

import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { useAuth } from '@/contexts/AuthContext'
import { useEmployee } from '@/hooks/useEmployees'

export function MyEmployeeProfile() {
  const navigate = useNavigate()
  const { currentUser } = useAuth()
  const [employeeId, setEmployeeId] = useState<number | null>(null)

  const { data: employeeData, isLoading, error } = useEmployee(employeeId || 0)

  useEffect(() => {
    if (!currentUser) {
      toast.error('사용자 정보를 불러올 수 없습니다')
      navigate('/')
      return
    }

    console.log('MyEmployeeProfile - currentUser:', currentUser)
    console.log('MyEmployeeProfile - currentUser.id:', currentUser.id)

    // Employee ID 사용 (SUPER_ADMIN 포함 모든 권한)
    // id가 0일 수 있으므로 !== undefined로 체크
    if (currentUser.id !== undefined && currentUser.id !== null) {
      console.log('MyEmployeeProfile - employeeId 설정:', currentUser.id)
      setEmployeeId(currentUser.id)
    } else {
      console.error('MyEmployeeProfile - currentUser.id가 없습니다')
      toast.error('직원 정보가 연결되지 않았습니다. 관리자에게 문의하세요.')
      navigate('/')
    }
  }, [currentUser, navigate])

  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin" />
      </div>
    )
  }

  if (error || !currentUser) {
    return (
      <div className="container mx-auto py-8">
        <Card>
          <CardHeader>
            <CardTitle>직원 정보를 찾을 수 없습니다</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="mb-4 text-muted-foreground">
              등록된 직원 정보가 없습니다. 관리자에게 문의하세요.
            </p>
            <Button onClick={() => navigate('/')}>홈으로 돌아가기</Button>
          </CardContent>
        </Card>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      {/* 헤더 */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">내 정보</h1>
          <p className="text-muted-foreground">
            본인의 직원 정보를 조회할 수 있습니다
          </p>
        </div>
        <Button variant="outline" onClick={() => navigate('/')}>
          돌아가기
        </Button>
      </div>

      {/* 기본 정보 */}
      <Card>
        <CardHeader>
          <CardTitle>기본 정보</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
            <div>
              <p className="text-sm text-muted-foreground">사번</p>
              <p className="text-lg font-medium">
                {currentUser.employeeNumber}
              </p>
            </div>
            <div>
              <p className="text-sm text-muted-foreground">이름</p>
              <p className="text-lg font-medium">{currentUser.name}</p>
            </div>
            <div>
              <p className="text-sm text-muted-foreground">이메일</p>
              <p className="text-lg">{currentUser.email}</p>
            </div>
            <div>
              <p className="text-sm text-muted-foreground">전화번호</p>
              <p className="text-lg">{currentUser.phone || '-'}</p>
            </div>
            {currentUser.birthDate && (
              <div>
                <p className="text-sm text-muted-foreground">생년월일</p>
                <p className="text-lg">
                  {new Date(currentUser.birthDate).toLocaleDateString('ko-KR')}
                </p>
              </div>
            )}
            {currentUser.gender && (
              <div>
                <p className="text-sm text-muted-foreground">성별</p>
                <p className="text-lg">
                  {currentUser.gender === 'MALE' ? '남성' : '여성'}
                </p>
              </div>
            )}
          </div>
        </CardContent>
      </Card>

      {/* 소속 정보 */}
      <Card>
        <CardHeader>
          <CardTitle>소속 정보</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
            {currentUser.company && (
              <div>
                <p className="text-sm text-muted-foreground">회사</p>
                <p className="text-lg font-medium">{currentUser.company.name}</p>
              </div>
            )}
            {currentUser.department && (
              <div>
                <p className="text-sm text-muted-foreground">부서</p>
                <p className="text-lg font-medium">
                  {currentUser.department.name}
                </p>
              </div>
            )}
            {currentUser.position && (
              <div>
                <p className="text-sm text-muted-foreground">직급</p>
                <p className="text-lg font-medium">
                  {typeof currentUser.position === 'string'
                    ? currentUser.position
                    : currentUser.position?.name}
                </p>
              </div>
            )}
            {currentUser.hireDate && (
              <div>
                <p className="text-sm text-muted-foreground">입사일</p>
                <p className="text-lg">
                  {new Date(currentUser.hireDate).toLocaleDateString('ko-KR')}
                </p>
              </div>
            )}
            {currentUser.employmentStatus && (
              <div>
                <p className="text-sm text-muted-foreground">재직 상태</p>
                <p className="text-lg">
                  {currentUser.employmentStatus === 'ACTIVE' ? '재직' : '퇴직'}
                </p>
              </div>
            )}
            {currentUser.yearsOfService !== undefined && (
              <div>
                <p className="text-sm text-muted-foreground">근속년수</p>
                <p className="text-lg">{currentUser.yearsOfService}년</p>
              </div>
            )}
          </div>
        </CardContent>
      </Card>

      {/* 계좌 정보 */}
      {(currentUser.bankName || currentUser.accountNumber) && (
        <Card>
          <CardHeader>
            <CardTitle>계좌 정보</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
              <div>
                <p className="text-sm text-muted-foreground">은행</p>
                <p className="text-lg">{currentUser.bankName || '-'}</p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">계좌번호</p>
                <p className="text-lg">{currentUser.accountNumber || '-'}</p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">예금주</p>
                <p className="text-lg">{currentUser.accountHolder || '-'}</p>
              </div>
            </div>
          </CardContent>
        </Card>
      )}

      {/* 비고 */}
      {currentUser.memo && (
        <Card>
          <CardHeader>
            <CardTitle>비고</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="whitespace-pre-wrap text-sm">{currentUser.memo}</p>
          </CardContent>
        </Card>
      )}
    </div>
  )
}
