import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { ArrowLeft, Package, Edit, Trash2, TrendingUp, AlertTriangle } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { productService, Product } from '@/services/productService'


/**
 * 상품 상세 페이지 컴포넌트
 * 특정 상품의 상세 정보를 표시합니다
 */
function ProductDetail() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [product, setProduct] = useState<Product | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (id) {
      fetchProductDetail(parseInt(id))
    }
  }, [id])

  /**
   * 상품 상세 정보 조회
   */
  const fetchProductDetail = async (productId: number) => {
    try {
      setLoading(true)
      setError(null)
      
      // API 호출
      const productData = await productService.getProductById(productId)
      
      // 안전한 필드 접근
      if (productData) {
        setProduct(productData)
      } else {
        throw new Error('상품 데이터를 받아올 수 없습니다.')
      }
    } catch (err) {
      setError('상품 정보를 불러오는 중 오류가 발생했습니다.')
      console.error('상품 상세 정보 조회 오류:', err)
    } finally {
      setLoading(false)
    }
  }

  /**
   * 상품 상태 배지 색상
   */
  const getStatusColor = (status: string) => {
    switch (status) {
      case 'ACTIVE':
        return 'bg-green-100 text-green-800'
      case 'INACTIVE':
        return 'bg-yellow-100 text-yellow-800'
      case 'DISCONTINUED':
        return 'bg-red-100 text-red-800'
      default:
        return 'bg-gray-100 text-gray-800'
    }
  }

  /**
   * 상품 유형 배지 색상
   */
  const getTypeColor = (type: string) => {
    switch (type) {
      case 'RAW_MATERIAL':
        return 'bg-blue-100 text-blue-800'
      case 'SEMI_FINISHED':
        return 'bg-purple-100 text-purple-800'
      case 'FINISHED_GOODS':
        return 'bg-green-100 text-green-800'
      case 'SERVICE':
        return 'bg-orange-100 text-orange-800'
      default:
        return 'bg-gray-100 text-gray-800'
    }
  }

  /**
   * 재고 상태 확인
   */
  const getStockStatus = (current: number, safety: number, reorder: number) => {
    if (current <= reorder) {
      return { status: 'danger', message: '재고 부족', color: 'text-red-600' }
    } else if (current <= safety) {
      return { status: 'warning', message: '재고 주의', color: 'text-yellow-600' }
    } else {
      return { status: 'good', message: '재고 충분', color: 'text-green-600' }
    }
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto mb-4"></div>
          <p className="text-muted-foreground">상품 정보를 불러오는 중...</p>
        </div>
      </div>
    )
  }

  if (error || !product) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <p className="text-destructive mb-4">{error || '상품 정보를 찾을 수 없습니다.'}</p>
          <Button onClick={() => navigate('/inventory/products')}>
            <ArrowLeft className="h-4 w-4 mr-2" />
            상품 목록으로 돌아가기
          </Button>
        </div>
      </div>
    )
  }

  const stockStatus = getStockStatus(
    product.quantity || 0, 
    product.safetyStock || 0, 
    product.reorderPoint || 0
  )

  return (
    <div className="space-y-6">
      {/* 헤더 */}
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-4">
          <Button
            variant="ghost"
            size="sm"
            onClick={() => navigate('/inventory/products')}
          >
            <ArrowLeft className="h-4 w-4 mr-2" />
            뒤로가기
          </Button>
          <div>
            <h1 className="text-2xl font-bold">{product.productName || '상품명 없음'}</h1>
            <p className="text-muted-foreground">상품 코드: {product.productCode || '코드 없음'}</p>
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
        <Badge className={getStatusColor(product.productStatus || 'ACTIVE')}>
          {product.productStatus === 'ACTIVE' ? '활성' : 
           product.productStatus === 'INACTIVE' ? '비활성' : '단종'}
        </Badge>
        <Badge className={getTypeColor(product.productType || 'FINISHED_GOODS')}>
          {product.productType === 'RAW_MATERIAL' ? '원자재' :
           product.productType === 'SEMI_FINISHED' ? '반제품' :
           product.productType === 'FINISHED_GOODS' ? '완제품' : '서비스'}
        </Badge>
        <Badge variant="outline">
          {product.categoryName || '미분류'}
        </Badge>
      </div>

      {/* 기본 정보 */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center">
            <Package className="h-5 w-5 mr-2" />
            기본 정보
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="text-sm font-medium text-muted-foreground">상품명</label>
              <p className="text-sm">{product.productName}</p>
            </div>
            {product.productNameEn && (
              <div>
                <label className="text-sm font-medium text-muted-foreground">영문명</label>
                <p className="text-sm">{product.productNameEn}</p>
              </div>
            )}
            <div>
              <label className="text-sm font-medium text-muted-foreground">상품 코드</label>
              <p className="text-sm">{product.productCode}</p>
            </div>
            {product.sku && (
              <div>
                <label className="text-sm font-medium text-muted-foreground">SKU</label>
                <p className="text-sm">{product.sku}</p>
              </div>
            )}
            {product.barcode && (
              <div>
                <label className="text-sm font-medium text-muted-foreground">바코드</label>
                <p className="text-sm">{product.barcode}</p>
              </div>
            )}
            <div>
              <label className="text-sm font-medium text-muted-foreground">기본 단위</label>
              <p className="text-sm">{product.baseUnit}</p>
            </div>
          </div>
          {product.description && (
            <div>
              <label className="text-sm font-medium text-muted-foreground">설명</label>
              <p className="text-sm">{product.description}</p>
            </div>
          )}
        </CardContent>
      </Card>

      {/* 가격 정보 */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center">
            <TrendingUp className="h-5 w-5 mr-2" />
            가격 정보
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="text-sm font-medium text-muted-foreground">판매가</label>
              <p className="text-lg font-semibold">{(product.sellingPrice || 0).toLocaleString()}원</p>
            </div>
            {product.standardCost && (
              <div>
                <label className="text-sm font-medium text-muted-foreground">표준 원가</label>
                <p className="text-lg font-semibold">{(product.standardCost || 0).toLocaleString()}원</p>
              </div>
            )}
          </div>
        </CardContent>
      </Card>

      {/* 재고 정보 */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center">
            <AlertTriangle className="h-5 w-5 mr-2" />
            재고 정보
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="text-sm font-medium text-muted-foreground">현재 재고</label>
              <p className={`text-lg font-semibold ${stockStatus.color}`}>
                {(product.quantity || 0).toLocaleString()} {product.baseUnit}
              </p>
              <p className={`text-xs ${stockStatus.color}`}>{stockStatus.message}</p>
            </div>
            <div>
              <label className="text-sm font-medium text-muted-foreground">안전 재고</label>
              <p className="text-sm">{(product.safetyStock || 0).toLocaleString()} {product.baseUnit}</p>
            </div>
            <div>
              <label className="text-sm font-medium text-muted-foreground">발주점</label>
              <p className="text-sm">{(product.reorderPoint || 0).toLocaleString()} {product.baseUnit}</p>
            </div>
            <div>
              <label className="text-sm font-medium text-muted-foreground">발주량</label>
              <p className="text-sm">{(product.reorderQuantity || 0).toLocaleString()} {product.baseUnit}</p>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* 제조/공급 정보 */}
      <Card>
        <CardHeader>
          <CardTitle>제조/공급 정보</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {product.brand && (
              <div>
                <label className="text-sm font-medium text-muted-foreground">브랜드</label>
                <p className="text-sm">{product.brand}</p>
              </div>
            )}
            {product.manufacturer && (
              <div>
                <label className="text-sm font-medium text-muted-foreground">제조사</label>
                <p className="text-sm">{product.manufacturer}</p>
              </div>
            )}
            {product.supplier && (
              <div>
                <label className="text-sm font-medium text-muted-foreground">공급업체</label>
                <p className="text-sm">{product.supplier}</p>
              </div>
            )}
          </div>
        </CardContent>
      </Card>

      {/* 물리적 특성 */}
      {(product.weight || product.dimensions) && (
        <Card>
          <CardHeader>
            <CardTitle>물리적 특성</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {product.weight && (
                <div>
                  <label className="text-sm font-medium text-muted-foreground">무게</label>
                  <p className="text-sm">{product.weight}kg</p>
                </div>
              )}
              {product.dimensions && (
                <div>
                  <label className="text-sm font-medium text-muted-foreground">치수</label>
                  <p className="text-sm">{product.dimensions}</p>
                </div>
              )}
            </div>
          </CardContent>
        </Card>
      )}

      {/* 등록 정보 */}
      <Card>
        <CardHeader>
          <CardTitle>등록 정보</CardTitle>
        </CardHeader>
        <CardContent className="space-y-2">
          <div>
            <label className="text-sm font-medium text-muted-foreground">등록일</label>
            <p className="text-sm">{new Date(product.createdAt).toLocaleDateString('ko-KR')}</p>
          </div>
          {product.updatedAt && (
            <div>
              <label className="text-sm font-medium text-muted-foreground">수정일</label>
              <p className="text-sm">{new Date(product.updatedAt).toLocaleDateString('ko-KR')}</p>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  )
}

export { ProductDetail }
