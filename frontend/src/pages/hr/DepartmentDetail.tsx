import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { ArrowLeft, Building2, Users, Edit, Trash2, Calendar, User } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
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
      const departmentData = await departmentService.getDepartmentById(departmentId)
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
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto mb-4"></div>
          <p className="text-muted-foreground">부서 정보를 불러오는 중...</p>
        </div>
      </div>
    )
  }

  if (error || !department) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <p className="text-destructive mb-4">{error || '부서 정보를 찾을 수 없습니다.'}</p>
          <Button onClick={() => navigate('/hr/departments')}>
            <ArrowLeft className="h-4 w-4 mr-2" />
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
            <ArrowLeft className="h-4 w-4 mr-2" />
            뒤로가기
          </Button>
          <div>
            <h1 className="text-2xl font-bold">{department.departmentName}</h1>
            <p className="text-muted-foreground">부서 코드: {department.departmentCode}</p>
          </div>
        </div>
        <div className="flex items-center space-x-2">
          <Button variant="outline" size="sm">
            <Edit className="h-4 w-4 mr-2" />
            수정
          </Button>
          <Button variant="outline" size="sm" className="text-destructive">
            <Trash2 className="h-4 w-4 mr-2" />
            삭제
          </Button>
        </div>
      </div>

      {/* 상태 */}
      <div className="flex items-center space-x-4">
        <Badge className={department.isActive ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}>
          {department.isActive ? '활성' : '비활성'}
        </Badge>
        <Badge variant="outline">
          {department.companyName}
        </Badge>
      </div>

      {/* 기본 정보 */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center">
            <Building2 className="h-5 w-5 mr-2" />
            기본 정보
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="text-sm font-medium text-muted-foreground">부서명</label>
              <p className="text-sm">{department.departmentName}</p>
            </div>
            {department.departmentNameEn && (
              <div>
                <label className="text-sm font-medium text-muted-foreground">영문명</label>
                <p className="text-sm">{department.departmentNameEn}</p>
              </div>
            )}
            <div>
              <label className="text-sm font-medium text-muted-foreground">부서 코드</label>
              <p className="text-sm">{department.departmentCode}</p>
            </div>
            <div>
              <label className="text-sm font-medium text-muted-foreground">소속 회사</label>
              <p className="text-sm">{department.companyName}</p>
            </div>
            {department.parentDepartmentName && (
              <div>
                <label className="text-sm font-medium text-muted-foreground">상위 부서</label>
                <p className="text-sm">{department.parentDepartmentName}</p>
              </div>
            )}
            {department.location && (
              <div>
                <label className="text-sm font-medium text-muted-foreground">위치</label>
                <p className="text-sm">{department.location}</p>
              </div>
            )}
          </div>
          {department.description && (
            <div>
              <label className="text-sm font-medium text-muted-foreground">설명</label>
              <p className="text-sm">{department.description}</p>
            </div>
          )}
        </CardContent>
      </Card>

      {/* 부서장 정보 */}
      {department.managerName && (
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center">
              <User className="h-5 w-5 mr-2" />
              부서장 정보
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div>
              <label className="text-sm font-medium text-muted-foreground">부서장</label>
              <p className="text-sm">{department.managerName}</p>
            </div>
          </CardContent>
        </Card>
      )}

      {/* 부서 통계 */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center">
            <Users className="h-5 w-5 mr-2" />
            부서 통계
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="text-sm font-medium text-muted-foreground">직원 수</label>
              <p className="text-2xl font-bold">{department.employeeCount}명</p>
            </div>
            {department.budget && (
              <div>
                <label className="text-sm font-medium text-muted-foreground">예산</label>
                <p className="text-2xl font-bold">{department.budget.toLocaleString()}원</p>
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
            <div className="flex justify-between items-center p-3 border rounded">
              <div>
                <p className="font-medium">프론트엔드팀</p>
                <p className="text-sm text-muted-foreground">직원 수: 3명</p>
              </div>
              <Badge variant="outline">활성</Badge>
            </div>
            <div className="flex justify-between items-center p-3 border rounded">
              <div>
                <p className="font-medium">백엔드팀</p>
                <p className="text-sm text-muted-foreground">직원 수: 4명</p>
              </div>
              <Badge variant="outline">활성</Badge>
            </div>
            <div className="flex justify-between items-center p-3 border rounded">
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
            <Calendar className="h-5 w-5 mr-2" />
            등록 정보
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-2">
          <div>
            <label className="text-sm font-medium text-muted-foreground">등록일</label>
            <p className="text-sm">{new Date(department.createdAt).toLocaleDateString('ko-KR')}</p>
          </div>
          {department.updatedAt && (
            <div>
              <label className="text-sm font-medium text-muted-foreground">수정일</label>
              <p className="text-sm">{new Date(department.updatedAt).toLocaleDateString('ko-KR')}</p>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  )
}

export default DepartmentDetail
