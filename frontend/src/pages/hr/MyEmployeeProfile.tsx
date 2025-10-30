/**
 * 본인 직원 정보 조회 페이지
 * 모든 권한(SUPER_ADMIN, ADMIN, MANAGER, USER)이 본인의 직원 정보를 조회할 수 있습니다
 */

import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '@/contexts/AuthContext'
import { useEmployee } from '@/hooks/useEmployees'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Loader2 } from 'lucide-react'
import toast from 'react-hot-toast'

export function MyEmployeeProfile() {
  const navigate = useNavigate()
  const { user } = useAuth()
  const [employeeId, setEmployeeId] = useState<number | null>(null)
  
  // TODO: User 엔티티에 employeeId 필드가 추가되면 이것을 사용
  // 현재는 user?.employeeId가 null이므로 임시로 처리
  
  const { data: employee, isLoading, error } = useEmployee(employeeId || 0)

  useEffect(() => {
    if (!user) {
      toast.error('사용자 정보를 불러올 수 없습니다')
      navigate('/')
      return
    }

    // TODO: 실제로는 user.employeeId를 사용해야 함
    // 현재는 임시로 1번 직원을 조회하도록 설정
    // 백엔드 UserPrincipal.getEmployeeId()가 구현되면 연동 필요
    setEmployeeId(1)
  }, [user, navigate])

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <Loader2 className="h-8 w-8 animate-spin" />
      </div>
    )
  }

  if (error || !employee) {
    return (
      <div className="container mx-auto py-8">
        <Card>
          <CardHeader>
            <CardTitle>직원 정보를 찾을 수 없습니다</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-muted-foreground mb-4">
              등록된 직원 정보가 없습니다. 관리자에게 문의하세요.
            </p>
            <Button onClick={() => navigate('/')}>
              홈으로 돌아가기
            </Button>
          </CardContent>
        </Card>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      {/* 헤더 */}
      <div className="flex justify-between items-center">
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
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <p className="text-sm text-muted-foreground">사번</p>
              <p className="text-lg font-medium">{employee.employeeNumber}</p>
            </div>
            <div>
              <p className="text-sm text-muted-foreground">이름</p>
              <p className="text-lg font-medium">{employee.name}</p>
            </div>
            <div>
              <p className="text-sm text-muted-foreground">이메일</p>
              <p className="text-lg">{employee.email}</p>
            </div>
            <div>
              <p className="text-sm text-muted-foreground">전화번호</p>
              <p className="text-lg">{employee.phone || '-'}</p>
            </div>
            {employee.birthDate && (
              <div>
                <p className="text-sm text-muted-foreground">생년월일</p>
                <p className="text-lg">{new Date(employee.birthDate).toLocaleDateString('ko-KR')}</p>
              </div>
            )}
            {employee.gender && (
              <div>
                <p className="text-sm text-muted-foreground">성별</p>
                <p className="text-lg">{employee.gender === 'MALE' ? '남성' : '여성'}</p>
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
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <p className="text-sm text-muted-foreground">회사</p>
              <p className="text-lg font-medium">{employee.company.name}</p>
            </div>
            <div>
              <p className="text-sm text-muted-foreground">부서</p>
              <p className="text-lg font-medium">{employee.department.name}</p>
            </div>
            <div>
              <p className="text-sm text-muted-foreground">직급</p>
              <p className="text-lg font-medium">{employee.position.name}</p>
            </div>
            <div>
              <p className="text-sm text-muted-foreground">입사일</p>
              <p className="text-lg">{new Date(employee.hireDate).toLocaleDateString('ko-KR')}</p>
            </div>
            <div>
              <p className="text-sm text-muted-foreground">재직 상태</p>
              <p className="text-lg">{employee.employmentStatus === 'ACTIVE' ? '재직' : '퇴직'}</p>
            </div>
            <div>
              <p className="text-sm text-muted-foreground">근속년수</p>
              <p className="text-lg">{employee.yearsOfService}년</p>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* 계좌 정보 */}
      {(employee.bankName || employee.accountNumber) && (
        <Card>
          <CardHeader>
            <CardTitle>계좌 정보</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <p className="text-sm text-muted-foreground">은행</p>
                <p className="text-lg">{employee.bankName || '-'}</p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">계좌번호</p>
                <p className="text-lg">{employee.accountNumber || '-'}</p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">예금주</p>
                <p className="text-lg">{employee.accountHolder || '-'}</p>
              </div>
            </div>
          </CardContent>
        </Card>
      )}

      {/* 비고 */}
      {employee.memo && (
        <Card>
          <CardHeader>
            <CardTitle>비고</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-sm whitespace-pre-wrap">{employee.memo}</p>
          </CardContent>
        </Card>
      )}
    </div>
  )
}
