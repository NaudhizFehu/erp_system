import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { ArrowLeft, Building2, Phone, Mail, MapPin, Calendar, Edit, Trash2 } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { customerService, Customer } from '@/services/customerService'


/**
 * 고객 상세 페이지 컴포넌트
 * 특정 고객의 상세 정보를 표시합니다
 */
function CustomerDetail() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [customer, setCustomer] = useState<Customer | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (id) {
      fetchCustomerDetail(parseInt(id))
    }
  }, [id])

  /**
   * 고객 상세 정보 조회
   */
  const fetchCustomerDetail = async (customerId: number) => {
    try {
      setLoading(true)
      setError(null)
      
      // 실제 API 호출
      const response = await fetch(`/api/sales/customers/${customerId}`)
      const data = await response.json()
      
      if (data.success) {
        setCustomer(data.data)
      } else {
        setError(data.message || '고객 정보를 불러오는 중 오류가 발생했습니다.')
      }
    } catch (err) {
      setError('고객 정보를 불러오는 중 오류가 발생했습니다.')
      console.error('고객 상세 정보 조회 오류:', err)
    } finally {
      setLoading(false)
    }
  }

  /**
   * 고객 상태 배지 색상
   */
  const getStatusColor = (status: string) => {
    switch (status) {
      case 'ACTIVE':
        return 'bg-green-100 text-green-800'
      case 'INACTIVE':
        return 'bg-gray-100 text-gray-800'
      case 'SUSPENDED':
        return 'bg-red-100 text-red-800'
      default:
        return 'bg-gray-100 text-gray-800'
    }
  }

  /**
   * 고객 등급 배지 색상
   */
  const getGradeColor = (grade: string) => {
    switch (grade) {
      case 'A':
        return 'bg-blue-100 text-blue-800'
      case 'B':
        return 'bg-green-100 text-green-800'
      case 'C':
        return 'bg-yellow-100 text-yellow-800'
      case 'D':
        return 'bg-red-100 text-red-800'
      default:
        return 'bg-gray-100 text-gray-800'
    }
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto mb-4"></div>
          <p className="text-muted-foreground">고객 정보를 불러오는 중...</p>
        </div>
      </div>
    )
  }

  if (error || !customer) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <p className="text-destructive mb-4">{error || '고객 정보를 찾을 수 없습니다.'}</p>
          <Button onClick={() => navigate('/sales/customers')}>
            <ArrowLeft className="h-4 w-4 mr-2" />
            고객 목록으로 돌아가기
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
            onClick={() => navigate('/sales/customers')}
          >
            <ArrowLeft className="h-4 w-4 mr-2" />
            뒤로가기
          </Button>
          <div>
            <h1 className="text-2xl font-bold">{customer.customerName}</h1>
            <p className="text-muted-foreground">고객 코드: {customer.customerCode}</p>
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

      {/* 상태 및 등급 */}
      <div className="flex items-center space-x-4">
        <Badge className={getStatusColor(customer.customerStatus)}>
          {customer.customerStatus === 'ACTIVE' ? '활성' : 
           customer.customerStatus === 'INACTIVE' ? '비활성' : '중단'}
        </Badge>
        <Badge className={getGradeColor(customer.customerGrade)}>
          {customer.customerGrade}등급
        </Badge>
        <Badge variant="outline">
          {customer.customerType === 'CORPORATION' ? '법인' : '개인'}
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
              <label className="text-sm font-medium text-muted-foreground">고객명</label>
              <p className="text-sm">{customer.customerName}</p>
            </div>
            <div>
              <label className="text-sm font-medium text-muted-foreground">고객 코드</label>
              <p className="text-sm">{customer.customerCode}</p>
            </div>
            {customer.businessRegistrationNumber && (
              <div>
                <label className="text-sm font-medium text-muted-foreground">사업자등록번호</label>
                <p className="text-sm">{customer.businessRegistrationNumber}</p>
              </div>
            )}
            {customer.ceoName && (
              <div>
                <label className="text-sm font-medium text-muted-foreground">대표자명</label>
                <p className="text-sm">{customer.ceoName}</p>
              </div>
            )}
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
            {customer.phone && (
              <div>
                <label className="text-sm font-medium text-muted-foreground">전화번호</label>
                <p className="text-sm">{customer.phone}</p>
              </div>
            )}
            {customer.email && (
              <div>
                <label className="text-sm font-medium text-muted-foreground">이메일</label>
                <p className="text-sm">{customer.email}</p>
              </div>
            )}
            {customer.address && (
              <div className="md:col-span-2">
                <label className="text-sm font-medium text-muted-foreground">주소</label>
                <p className="text-sm">{customer.address}</p>
              </div>
            )}
          </div>
        </CardContent>
      </Card>

      {/* 영업 정보 */}
      <Card>
        <CardHeader>
          <CardTitle>영업 정보</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {customer.salesManagerName && (
              <div>
                <label className="text-sm font-medium text-muted-foreground">담당 영업사원</label>
                <p className="text-sm">{customer.salesManagerName}</p>
              </div>
            )}
            {customer.creditLimit && (
              <div>
                <label className="text-sm font-medium text-muted-foreground">신용한도</label>
                <p className="text-sm">{customer.creditLimit.toLocaleString()}원</p>
              </div>
            )}
            {customer.paymentTerms && (
              <div>
                <label className="text-sm font-medium text-muted-foreground">결제 조건</label>
                <p className="text-sm">{customer.paymentTerms}</p>
              </div>
            )}
          </div>
        </CardContent>
      </Card>

      {/* 추가 정보 */}
      {customer.description && (
        <Card>
          <CardHeader>
            <CardTitle>추가 정보</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-sm text-muted-foreground">{customer.description}</p>
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
            <p className="text-sm">{new Date(customer.createdAt).toLocaleDateString('ko-KR')}</p>
          </div>
          {customer.updatedAt && (
            <div>
              <label className="text-sm font-medium text-muted-foreground">수정일</label>
              <p className="text-sm">{new Date(customer.updatedAt).toLocaleDateString('ko-KR')}</p>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  )
}

export { CustomerDetail }
