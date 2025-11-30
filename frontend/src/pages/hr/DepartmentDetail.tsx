import {
  ArrowLeft,
  Building2,
  Users,
  Edit,
  Trash2,
  Calendar,
  User,
} from 'lucide-react'
import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'

import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { departmentService, Department } from '@/services/departmentService'

/**
 * 부서 상세 페이지 컴포넌트
 * 특정 부서의 상세 정보를 표시합니다
 */
function DepartmentDetail() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [department, setDepartment] = useState<Department | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (id) {
      fetchDepartmentDetail(parseInt(id))
    }
  }, [id])

  /**
   * 부서 상세 정보 조회
   */
  const fetchDepartmentDetail = async (departmentId: number) => {
    try {
      setLoading(true)
      setError(null)

      // API 호출
      const departmentData =
        await departmentService.getDepartmentById(departmentId)
      setDepartment(departmentData)
    } catch (err) {
      setError('부서 정보를 불러오는 중 오류가 발생했습니다.')
      console.error('부서 상세 정보 조회 오류:', err)
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return (
      <div className="flex h-64 items-center justify-center">
        <div className="text-center">
          <div className="mx-auto mb-4 h-8 w-8 animate-spin rounded-full border-b-2 border-primary" />
          <p className="text-muted-foreground">부서 정보를 불러오는 중...</p>
        </div>
      </div>
    )
  }

  if (error || !department) {
    return (
      <div className="flex h-64 items-center justify-center">
        <div className="text-center">
          <p className="mb-4 text-destructive">
            {error || '부서 정보를 찾을 수 없습니다.'}
          </p>
          <Button onClick={() => navigate('/hr/departments')}>
            <ArrowLeft className="mr-2 h-4 w-4" />
            부서 목록으로 돌아가기
          </Button>
        </div>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      {/* 헤더 */}
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-4">
          <Button
            variant="ghost"
            size="sm"
            onClick={() => navigate('/hr/departments')}
          >
            <ArrowLeft className="mr-2 h-4 w-4" />
            뒤로가기
          </Button>
          <div>
            <h1 className="text-2xl font-bold">{department.name}</h1>
            <p className="text-muted-foreground">
              부서 코드: {department.departmentCode}
            </p>
          </div>
        </div>
        <div className="flex items-center space-x-2">
          <Button variant="outline" size="sm">
            <Edit className="mr-2 h-4 w-4" />
            수정
          </Button>
          <Button variant="outline" size="sm" className="text-destructive">
            <Trash2 className="mr-2 h-4 w-4" />
            삭제
          </Button>
        </div>
      </div>

      {/* 상태 */}
      <div className="flex items-center space-x-4">
        <Badge
          className={
            department.status === 'ACTIVE'
              ? 'bg-green-100 text-green-800'
              : 'bg-red-100 text-red-800'
          }
        >
          {department.status === 'ACTIVE' ? '활성' : '비활성'}
        </Badge>
        <Badge variant="outline">{department.company.name}</Badge>
      </div>

      {/* 기본 정보 */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center">
            <Building2 className="mr-2 h-5 w-5" />
            기본 정보
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
            <div>
              <label className="text-sm font-medium text-muted-foreground">
                부서명
              </label>
              <p className="text-sm">{department.name}</p>
            </div>
            {department.nameEn && (
              <div>
                <label className="text-sm font-medium text-muted-foreground">
                  영문명
                </label>
                <p className="text-sm">{department.nameEn}</p>
              </div>
            )}
            <div>
              <label className="text-sm font-medium text-muted-foreground">
                부서 코드
              </label>
              <p className="text-sm">{department.departmentCode}</p>
            </div>
            <div>
              <label className="text-sm font-medium text-muted-foreground">
                소속 회사
              </label>
              <p className="text-sm">{department.company.name}</p>
            </div>
            {department.parentDepartment && (
              <div>
                <label className="text-sm font-medium text-muted-foreground">
                  상위 부서
                </label>
                <p className="text-sm">{department.parentDepartment.name}</p>
              </div>
            )}
            {department.location && (
              <div>
                <label className="text-sm font-medium text-muted-foreground">
                  위치
                </label>
                <p className="text-sm">{department.location}</p>
              </div>
            )}
          </div>
          {department.description && (
            <div>
              <label className="text-sm font-medium text-muted-foreground">
                설명
              </label>
              <p className="text-sm">{department.description}</p>
            </div>
          )}
        </CardContent>
      </Card>

      {/* 부서장 정보 */}
      {department.manager && (
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center">
              <User className="mr-2 h-5 w-5" />
              부서장 정보
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div>
              <label className="text-sm font-medium text-muted-foreground">
                부서장
              </label>
              <p className="text-sm">{department.manager.name}</p>
            </div>
          </CardContent>
        </Card>
      )}

      {/* 부서 통계 */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center">
            <Users className="mr-2 h-5 w-5" />
            부서 통계
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
            <div>
              <label className="text-sm font-medium text-muted-foreground">
                부서 레벨
              </label>
              <p className="text-2xl font-bold">{department.level}레벨</p>
            </div>
            {department.budgetAmount && (
              <div>
                <label className="text-sm font-medium text-muted-foreground">
                  예산
                </label>
                <p className="text-2xl font-bold">
                  {department.budgetAmount.toLocaleString()}원
                </p>
              </div>
            )}
          </div>
        </CardContent>
      </Card>

      {/* 하위 부서 목록 (실제로는 별도 API로 조회) */}
      <Card>
        <CardHeader>
          <CardTitle>하위 부서</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-2">
            <div className="flex items-center justify-between rounded border p-3">
              <div>
                <p className="font-medium">프론트엔드팀</p>
                <p className="text-sm text-muted-foreground">직원 수: 3명</p>
              </div>
              <Badge variant="outline">활성</Badge>
            </div>
            <div className="flex items-center justify-between rounded border p-3">
              <div>
                <p className="font-medium">백엔드팀</p>
                <p className="text-sm text-muted-foreground">직원 수: 4명</p>
              </div>
              <Badge variant="outline">활성</Badge>
            </div>
            <div className="flex items-center justify-between rounded border p-3">
              <div>
                <p className="font-medium">QA팀</p>
                <p className="text-sm text-muted-foreground">직원 수: 1명</p>
              </div>
              <Badge variant="outline">활성</Badge>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* 등록 정보 */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center">
            <Calendar className="mr-2 h-5 w-5" />
            등록 정보
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-2">
          <div>
            <label className="text-sm font-medium text-muted-foreground">
              등록일
            </label>
            <p className="text-sm">
              {new Date(department.createdAt).toLocaleDateString('ko-KR')}
            </p>
          </div>
          {department.updatedAt && (
            <div>
              <label className="text-sm font-medium text-muted-foreground">
                수정일
              </label>
              <p className="text-sm">
                {new Date(department.updatedAt).toLocaleDateString('ko-KR')}
              </p>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  )
}

export { DepartmentDetail }
