import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { ArrowLeft, ShoppingCart, Edit, Trash2, Calendar, User, Building2 } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { orderService, OrderDetail as OrderDetailType } from '@/services/orderService'


/**
 * 주문 상세 페이지 컴포넌트
 * 특정 주문의 상세 정보를 표시합니다
 */
function OrderDetail() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [order, setOrder] = useState<OrderDetailType | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (id) {
      fetchOrderDetail(parseInt(id))
    }
  }, [id])

  /**
   * 주문 상세 정보 조회
   */
  const fetchOrderDetail = async (orderId: number) => {
    try {
      setLoading(true)
      setError(null)
      
      // API 호출
      const orderData = await orderService.getOrderById(orderId)
      setOrder(orderData)
    } catch (err) {
      setError('주문 정보를 불러오는 중 오류가 발생했습니다.')
      console.error('주문 상세 정보 조회 오류:', err)
    } finally {
      setLoading(false)
    }
  }

  /**
   * 주문 상태 배지 색상
   */
  const getOrderStatusColor = (status: string) => {
    switch (status) {
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800'
      case 'CONFIRMED':
        return 'bg-blue-100 text-blue-800'
      case 'PROCESSING':
        return 'bg-purple-100 text-purple-800'
      case 'SHIPPED':
        return 'bg-indigo-100 text-indigo-800'
      case 'DELIVERED':
        return 'bg-green-100 text-green-800'
      case 'CANCELLED':
        return 'bg-red-100 text-red-800'
      default:
        return 'bg-gray-100 text-gray-800'
    }
  }

  /**
   * 결제 상태 배지 색상
   */
  const getPaymentStatusColor = (status: string) => {
    switch (status) {
      case 'UNPAID':
        return 'bg-red-100 text-red-800'
      case 'PAID':
        return 'bg-green-100 text-green-800'
      case 'PARTIAL':
        return 'bg-yellow-100 text-yellow-800'
      case 'REFUNDED':
        return 'bg-gray-100 text-gray-800'
      default:
        return 'bg-gray-100 text-gray-800'
    }
  }

  /**
   * 주문 상태 한글 변환
   */
  const getOrderStatusText = (status: string) => {
    switch (status) {
      case 'PENDING':
        return '대기'
      case 'CONFIRMED':
        return '확정'
      case 'PROCESSING':
        return '처리중'
      case 'SHIPPED':
        return '배송중'
      case 'DELIVERED':
        return '배송완료'
      case 'CANCELLED':
        return '취소'
      default:
        return status
    }
  }

  /**
   * 결제 상태 한글 변환
   */
  const getPaymentStatusText = (status: string) => {
    switch (status) {
      case 'UNPAID':
        return '미결제'
      case 'PAID':
        return '결제완료'
      case 'PARTIAL':
        return '부분결제'
      case 'REFUNDED':
        return '환불'
      default:
        return status
    }
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto mb-4"></div>
          <p className="text-muted-foreground">주문 정보를 불러오는 중...</p>
        </div>
      </div>
    )
  }

  if (error || !order) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <p className="text-destructive mb-4">{error || '주문 정보를 찾을 수 없습니다.'}</p>
          <Button onClick={() => navigate('/sales/orders')}>
            <ArrowLeft className="h-4 w-4 mr-2" />
            주문 목록으로 돌아가기
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
            onClick={() => navigate('/sales/orders')}
          >
            <ArrowLeft className="h-4 w-4 mr-2" />
            뒤로가기
          </Button>
          <div>
            <h1 className="text-2xl font-bold">{order.orderNumber}</h1>
            <p className="text-muted-foreground">주문일: {new Date(order.orderDate).toLocaleDateString('ko-KR')}</p>
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
        <Badge className={getOrderStatusColor(order.orderStatus)}>
          {getOrderStatusText(order.orderStatus)}
        </Badge>
        <Badge className={getPaymentStatusColor(order.paymentStatus)}>
          {getPaymentStatusText(order.paymentStatus)}
        </Badge>
        <Badge variant="outline">
          {order.customerType === 'CORPORATION' ? '법인' : '개인'}
        </Badge>
      </div>

      {/* 주문 정보 */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center">
            <ShoppingCart className="h-5 w-5 mr-2" />
            주문 정보
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="text-sm font-medium text-muted-foreground">주문번호</label>
              <p className="text-sm">{order.orderNumber}</p>
            </div>
            <div>
              <label className="text-sm font-medium text-muted-foreground">주문일</label>
              <p className="text-sm">{new Date(order.orderDate).toLocaleDateString('ko-KR')}</p>
            </div>
            {order.deliveryDate && (
              <div>
                <label className="text-sm font-medium text-muted-foreground">배송예정일</label>
                <p className="text-sm">{new Date(order.deliveryDate).toLocaleDateString('ko-KR')}</p>
              </div>
            )}
            <div>
              <label className="text-sm font-medium text-muted-foreground">총 금액</label>
              <p className="text-lg font-semibold">{order.totalAmount.toLocaleString()}원</p>
            </div>
          </div>
          {order.notes && (
            <div>
              <label className="text-sm font-medium text-muted-foreground">비고</label>
              <p className="text-sm">{order.notes}</p>
            </div>
          )}
        </CardContent>
      </Card>

      {/* 고객 정보 */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center">
            <User className="h-5 w-5 mr-2" />
            고객 정보
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="text-sm font-medium text-muted-foreground">고객명</label>
              <p className="text-sm">{order.customerName}</p>
            </div>
            <div>
              <label className="text-sm font-medium text-muted-foreground">고객 유형</label>
              <p className="text-sm">{order.customerType === 'CORPORATION' ? '법인' : '개인'}</p>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* 영업 정보 */}
      {order.salesManagerName && (
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center">
              <Building2 className="h-5 w-5 mr-2" />
              영업 정보
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div>
              <label className="text-sm font-medium text-muted-foreground">담당 영업사원</label>
              <p className="text-sm">{order.salesManagerName}</p>
            </div>
          </CardContent>
        </Card>
      )}

      {/* 주문 상품 목록 (실제로는 별도 API로 조회) */}
      <Card>
        <CardHeader>
          <CardTitle>주문 상품</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-2">
            <div className="flex justify-between items-center p-3 border rounded">
              <div>
                <p className="font-medium">노트북</p>
                <p className="text-sm text-muted-foreground">수량: 1개</p>
              </div>
              <p className="font-semibold">1,500,000원</p>
            </div>
            <div className="flex justify-between items-center p-3 border rounded">
              <div>
                <p className="font-medium">모니터</p>
                <p className="text-sm text-muted-foreground">수량: 1개</p>
              </div>
              <p className="font-semibold">500,000원</p>
            </div>
            <div className="flex justify-between items-center p-3 border rounded">
              <div>
                <p className="font-medium">기타 부품</p>
                <p className="text-sm text-muted-foreground">수량: 2개</p>
              </div>
              <p className="font-semibold">500,000원</p>
            </div>
          </div>
          <div className="mt-4 pt-4 border-t">
            <div className="flex justify-between items-center">
              <span className="text-lg font-semibold">총 금액</span>
              <span className="text-lg font-bold">{order.totalAmount.toLocaleString()}원</span>
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
            <p className="text-sm">{new Date(order.createdAt).toLocaleDateString('ko-KR')}</p>
          </div>
          {order.updatedAt && (
            <div>
              <label className="text-sm font-medium text-muted-foreground">수정일</label>
              <p className="text-sm">{new Date(order.updatedAt).toLocaleDateString('ko-KR')}</p>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  )
}

export default OrderDetail
