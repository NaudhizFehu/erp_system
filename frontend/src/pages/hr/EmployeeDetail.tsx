import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { ArrowLeft, User, Phone, Mail, MapPin, Calendar, Edit, Trash2, Building2 } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { employeeService, Employee } from '@/services/employeeService'


/**
 * 직원 상세 페이지 컴포넌트
 * 특정 직원의 상세 정보를 표시합니다
 */
function EmployeeDetail() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [employee, setEmployee] = useState<Employee | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (id) {
      fetchEmployeeDetail(parseInt(id))
    }
  }, [id])

  /**
   * 직원 상세 정보 조회
   */
  const fetchEmployeeDetail = async (employeeId: number) => {
    try {
      setLoading(true)
      setError(null)
      
      console.log('직원 상세 정보 조회 시작:', employeeId)
      // API 호출
      const employeeData = await employeeService.getEmployeeById(employeeId)
      console.log('직원 상세 정보 조회 성공:', employeeData)
      setEmployee(employeeData)
    } catch (err) {
      console.error('직원 상세 정보 조회 오류:', err)
      setError('직원 정보를 불러오는 중 오류가 발생했습니다.')
    } finally {
      setLoading(false)
    }
  }

  /**
   * 고용 상태 배지 색상
   */
  const getStatusColor = (status: string) => {
    switch (status) {
      case 'ACTIVE':
        return 'bg-green-100 text-green-800'
      case 'INACTIVE':
        return 'bg-yellow-100 text-yellow-800'
      case 'TERMINATED':
        return 'bg-red-100 text-red-800'
      default:
        return 'bg-gray-100 text-gray-800'
    }
  }

  /**
   * 고용 유형 배지 색상
   */
  const getEmploymentTypeColor = (type: string) => {
    switch (type) {
      case 'FULL_TIME':
        return 'bg-blue-100 text-blue-800'
      case 'PART_TIME':
        return 'bg-purple-100 text-purple-800'
      case 'CONTRACT':
        return 'bg-orange-100 text-orange-800'
      case 'INTERN':
        return 'bg-pink-100 text-pink-800'
      default:
        return 'bg-gray-100 text-gray-800'
    }
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto mb-4"></div>
          <p className="text-muted-foreground">직원 정보를 불러오는 중...</p>
        </div>
      </div>
    )
  }

  if (error || !employee) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <p className="text-destructive mb-4">{error || '직원 정보를 찾을 수 없습니다.'}</p>
          <Button onClick={() => navigate('/hr/employees')}>
            <ArrowLeft className="h-4 w-4 mr-2" />
            직원 목록으로 돌아가기
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
            onClick={() => navigate('/hr/employees')}
          >
            <ArrowLeft className="h-4 w-4 mr-2" />
            뒤로가기
          </Button>
          <div>
            <h1 className="text-2xl font-bold">{employee.name}</h1>
            <p className="text-muted-foreground">사번: {employee.employeeNumber}</p>
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

      {/* 상태 및 유형 */}
      <div className="flex items-center space-x-4">
        <Badge className={getStatusColor(employee.employmentStatus)}>
          {employee.employmentStatus === 'ACTIVE' ? '재직' : 
           employee.employmentStatus === 'INACTIVE' ? '휴직' : '퇴사'}
        </Badge>
        <Badge className={getEmploymentTypeColor(employee.employmentType)}>
          {employee.employmentType === 'FULL_TIME' ? '정규직' :
           employee.employmentType === 'PART_TIME' ? '시간제' :
           employee.employmentType === 'CONTRACT' ? '계약직' : '인턴'}
        </Badge>
      </div>

      {/* 기본 정보 */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center">
            <User className="h-5 w-5 mr-2" />
            기본 정보
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="text-sm font-medium text-muted-foreground">이름</label>
              <p className="text-sm">{employee.name}</p>
            </div>
            {employee.nameEn && (
              <div>
                <label className="text-sm font-medium text-muted-foreground">영문명</label>
                <p className="text-sm">{employee.nameEn}</p>
              </div>
            )}
            <div>
              <label className="text-sm font-medium text-muted-foreground">사번</label>
              <p className="text-sm">{employee.employeeNumber}</p>
            </div>
            <div>
              <label className="text-sm font-medium text-muted-foreground">이메일</label>
              <p className="text-sm">{employee.email}</p>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* 소속 정보 */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center">
            <Building2 className="h-5 w-5 mr-2" />
            소속 정보
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="text-sm font-medium text-muted-foreground">회사</label>
              <p className="text-sm">{employee.company.name}</p>
            </div>
            <div>
              <label className="text-sm font-medium text-muted-foreground">부서</label>
              <p className="text-sm">{employee.department.name}</p>
            </div>
            {employee.position && (
              <div>
                <label className="text-sm font-medium text-muted-foreground">직급</label>
                <p className="text-sm">{employee.position.name}</p>
              </div>
            )}
            <div>
              <label className="text-sm font-medium text-muted-foreground">입사일</label>
              <p className="text-sm">{new Date(employee.hireDate).toLocaleDateString('ko-KR')}</p>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* 연락처 정보 */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center">
            <Phone className="h-5 w-5 mr-2" />
            연락처 정보
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {employee.phone && (
              <div>
                <label className="text-sm font-medium text-muted-foreground">전화번호</label>
                <p className="text-sm">{employee.phone}</p>
              </div>
            )}
            {employee.mobile && (
              <div>
                <label className="text-sm font-medium text-muted-foreground">휴대폰</label>
                <p className="text-sm">{employee.mobile}</p>
              </div>
            )}
            {employee.address && (
              <div className="md:col-span-2">
                <label className="text-sm font-medium text-muted-foreground">주소</label>
                <p className="text-sm">{employee.address}</p>
              </div>
            )}
          </div>
        </CardContent>
      </Card>

      {/* 급여 정보 */}
      {(employee.bankName || employee.accountNumber) && (
        <Card>
          <CardHeader>
            <CardTitle>급여 정보</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {employee.bankName && (
                <div>
                  <label className="text-sm font-medium text-muted-foreground">은행명</label>
                  <p className="text-sm">{employee.bankName}</p>
                </div>
              )}
              {employee.accountNumber && (
                <div>
                  <label className="text-sm font-medium text-muted-foreground">계좌번호</label>
                  <p className="text-sm">{employee.accountNumber}</p>
                </div>
              )}
              {employee.accountHolder && (
                <div>
                  <label className="text-sm font-medium text-muted-foreground">예금주</label>
                  <p className="text-sm">{employee.accountHolder}</p>
                </div>
              )}
            </div>
          </CardContent>
        </Card>
      )}

      {/* 비상 연락처 */}
      {(employee.emergencyContact || employee.emergencyRelation) && (
        <Card>
          <CardHeader>
            <CardTitle>비상 연락처</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {employee.emergencyContact && (
                <div>
                  <label className="text-sm font-medium text-muted-foreground">비상 연락처</label>
                  <p className="text-sm">{employee.emergencyContact}</p>
                </div>
              )}
              {employee.emergencyRelation && (
                <div>
                  <label className="text-sm font-medium text-muted-foreground">관계</label>
                  <p className="text-sm">{employee.emergencyRelation}</p>
                </div>
              )}
            </div>
          </CardContent>
        </Card>
      )}

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
            <p className="text-sm">{new Date(employee.createdAt).toLocaleDateString('ko-KR')}</p>
          </div>
          {employee.updatedAt && (
            <div>
              <label className="text-sm font-medium text-muted-foreground">수정일</label>
              <p className="text-sm">{new Date(employee.updatedAt).toLocaleDateString('ko-KR')}</p>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  )
}

export default EmployeeDetail
