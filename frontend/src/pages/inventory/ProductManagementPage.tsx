/**
 * 상품 관리 페이지
 * 상품 목록 조회, 등록, 수정, 삭제 등의 기능을 제공합니다
 */

import { useState, useEffect } from 'react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Badge } from '@/components/ui/badge'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { 
  Plus, 
  Search, 
  Filter, 
  Download, 
  Upload,
  MoreHorizontal,
  Package,
  AlertTriangle,
  CheckCircle,
  XCircle,
  BarChart3,
  RefreshCw
} from 'lucide-react'
import { ProductTable } from '../../components/inventory/ProductTable'
import { ProductForm } from '../../components/inventory/ProductForm'
import { 
  useProducts, 
  useCreateProduct, 
  useUpdateProduct, 
  useDeleteProduct,
  useToggleProductActive,
  useCategories,
  useLowStockProducts,
  useOutOfStockProducts,
  useProductStats
} from '../../hooks/useInventory'
import { useDebounce } from '../../hooks/useDebounce'
import { 
  Product, 
  ProductCreateRequest, 
  ProductType, 
  ProductStatus, 
  InventorySearchParams,
  KOREAN_LABELS 
} from '../../types/inventory'
import { formatNumber, formatCurrency } from '../../utils/format'
import { LoadingSpinner } from '../../components/common/LoadingSpinner'
import toast from 'react-hot-toast'

interface ProductManagementPageProps {
  companyId: number
}

export default function ProductManagementPage({ companyId }: ProductManagementPageProps) {
  const [selectedTab, setSelectedTab] = useState('all')
  const [searchTerm, setSearchTerm] = useState('')
  const [selectedCategory, setSelectedCategory] = useState<string>('all')
  const [selectedType, setSelectedType] = useState<string>('all')
  const [selectedStatus, setSelectedStatus] = useState<string>('all')
  const [selectedProducts, setSelectedProducts] = useState<number[]>([])
  const [isProductFormOpen, setIsProductFormOpen] = useState(false)
  const [editingProduct, setEditingProduct] = useState<Product | null>(null)
  const [deletingProduct, setDeletingProduct] = useState<Product | null>(null)
  const [currentPage, setCurrentPage] = useState(0)
  const [pageSize, setPageSize] = useState(20)

  // 검색어 디바운스
  const debouncedSearchTerm = useDebounce(searchTerm, 500)

  // 검색 파라미터 구성
  const searchParams: InventorySearchParams = {
    searchTerm: debouncedSearchTerm,
    page: currentPage,
    size: pageSize,
    categoryId: selectedCategory && selectedCategory !== 'all' ? Number(selectedCategory) : undefined,
    productType: selectedType && selectedType !== 'all' ? (selectedType as ProductType) : undefined,
    productStatus: selectedStatus && selectedStatus !== 'all' ? (selectedStatus as ProductStatus) : undefined,
  }

  // 탭별 추가 필터
  const getTabSearchParams = (): InventorySearchParams => {
    const baseParams = { ...searchParams }
    
    switch (selectedTab) {
      case 'low-stock':
        return { ...baseParams, isLowStock: true }
      case 'out-of-stock':
        return { ...baseParams, isOutOfStock: true }
      case 'inactive':
        return { ...baseParams, productStatus: ProductStatus.INACTIVE }
      default:
        return baseParams
    }
  }

  // 데이터 페칭
  const { data: productsResponse, isLoading: isLoadingProducts, refetch: refetchProducts } = useProducts(
    companyId, 
    getTabSearchParams()
  )
  const { data: categoriesResponse } = useCategories(companyId)
  const { data: lowStockResponse } = useLowStockProducts(companyId)
  const { data: outOfStockResponse } = useOutOfStockProducts(companyId)
  const { data: statsResponse } = useProductStats(companyId)

  // 뮤테이션 훅
  const createProductMutation = useCreateProduct()
  const updateProductMutation = useUpdateProduct()
  const deleteProductMutation = useDeleteProduct()
  const toggleActiveMutation = useToggleProductActive()

  // 데이터 추출
  const products = productsResponse?.data?.content || []
  const totalElements = productsResponse?.data?.totalElements || 0
  const totalPages = productsResponse?.data?.totalPages || 0
  const categories = categoriesResponse?.data || []
  const lowStockProducts = lowStockResponse?.data || []
  const outOfStockProducts = outOfStockResponse?.data || []
  const stats = statsResponse?.data

  // 탭별 카운트
  const tabCounts = {
    all: totalElements,
    lowStock: lowStockProducts.length,
    outOfStock: outOfStockProducts.length,
    inactive: products.filter(p => !p.isActive).length,
  }

  // 상품 선택 핸들러
  const handleSelectProduct = (productId: number) => {
    setSelectedProducts(prev => 
      prev.includes(productId)
        ? prev.filter(id => id !== productId)
        : [...prev, productId]
    )
  }

  // 전체 선택 핸들러
  const handleSelectAll = (selected: boolean) => {
    if (selected) {
      setSelectedProducts(products.map(p => p.id))
    } else {
      setSelectedProducts([])
    }
  }

  // 상품 등록 핸들러
  const handleCreateProduct = () => {
    setEditingProduct(null)
    setIsProductFormOpen(true)
  }

  // 상품 수정 핸들러
  const handleEditProduct = (product: Product) => {
    setEditingProduct(product)
    setIsProductFormOpen(true)
  }

  // 상품 삭제 핸들러
  const handleDeleteProduct = (product: Product) => {
    setDeletingProduct(product)
  }

  // 상품 상세보기 핸들러
  const handleViewProduct = (product: Product) => {
    // 상품 상세 페이지로 이동하거나 모달 열기
    toast.info('상품 상세보기 기능은 준비중입니다')
  }

  // 상품 활성화/비활성화 핸들러
  const handleToggleActive = (product: Product) => {
    toggleActiveMutation.mutate(product.id)
  }

  // 폼 제출 핸들러
  const handleFormSubmit = (data: ProductCreateRequest) => {
    if (editingProduct) {
      updateProductMutation.mutate({
        id: editingProduct.id,
        productData: data
      }, {
        onSuccess: () => {
          setIsProductFormOpen(false)
          setEditingProduct(null)
          refetchProducts()
        }
      })
    } else {
      createProductMutation.mutate(data, {
        onSuccess: () => {
          setIsProductFormOpen(false)
          refetchProducts()
        }
      })
    }
  }

  // 삭제 확인 핸들러
  const handleConfirmDelete = () => {
    if (deletingProduct) {
      deleteProductMutation.mutate(deletingProduct.id, {
        onSuccess: () => {
          setDeletingProduct(null)
          refetchProducts()
        }
      })
    }
  }

  // 검색 초기화
  const handleResetFilters = () => {
    setSearchTerm('')
    setSelectedCategory('all')
    setSelectedType('all')
    setSelectedStatus('all')
    setCurrentPage(0)
  }

  // 일괄 작업 핸들러
  const handleBulkAction = (action: string) => {
    if (selectedProducts.length === 0) {
      toast.warning('선택된 상품이 없습니다')
      return
    }

    switch (action) {
      case 'activate':
        // 일괄 활성화 로직
        toast.info('일괄 활성화 기능은 준비중입니다')
        break
      case 'deactivate':
        // 일괄 비활성화 로직
        toast.info('일괄 비활성화 기능은 준비중입니다')
        break
      case 'delete':
        // 일괄 삭제 로직
        toast.info('일괄 삭제 기능은 준비중입니다')
        break
      case 'export':
        // 선택된 상품 내보내기
        toast.info('상품 내보내기 기능은 준비중입니다')
        break
    }
  }

  return (
    <div className="container mx-auto py-6 space-y-6">
      {/* 헤더 */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">상품 관리</h1>
          <p className="text-muted-foreground">
            상품 정보를 등록하고 관리하세요
          </p>
        </div>
        <div className="flex items-center space-x-2">
          <Button variant="outline" size="sm">
            <Upload className="h-4 w-4 mr-2" />
            가져오기
          </Button>
          <Button variant="outline" size="sm">
            <Download className="h-4 w-4 mr-2" />
            내보내기
          </Button>
          <Button onClick={handleCreateProduct} size="sm">
            <Plus className="h-4 w-4 mr-2" />
            상품 등록
          </Button>
        </div>
      </div>

      {/* 통계 카드 */}
      {stats && (
        <div className="grid gap-4 md:grid-cols-4">
          <Card>
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">전체 상품</p>
                  <p className="text-2xl font-bold">{formatNumber(stats.totalProducts)}</p>
                </div>
                <Package className="h-8 w-8 text-blue-600" />
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">활성 상품</p>
                  <p className="text-2xl font-bold text-green-600">{formatNumber(stats.activeProducts)}</p>
                </div>
                <CheckCircle className="h-8 w-8 text-green-600" />
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">안전재고 미달</p>
                  <p className="text-2xl font-bold text-yellow-600">{formatNumber(stats.lowStockProducts)}</p>
                </div>
                <AlertTriangle className="h-8 w-8 text-yellow-600" />
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">재고없음</p>
                  <p className="text-2xl font-bold text-red-600">{formatNumber(stats.outOfStockProducts)}</p>
                </div>
                <XCircle className="h-8 w-8 text-red-600" />
              </div>
            </CardContent>
          </Card>
        </div>
      )}

      {/* 검색 및 필터 */}
      <Card>
        <CardContent className="p-6">
          <div className="flex flex-col space-y-4">
            <div className="flex items-center space-x-4">
              <div className="flex-1">
                <div className="relative">
                  <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
                  <Input
                    placeholder="상품명, 상품코드, 브랜드로 검색..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="pl-10"
                  />
                </div>
              </div>
              <Button variant="outline" onClick={() => refetchProducts()}>
                <RefreshCw className="h-4 w-4 mr-2" />
                새로고침
              </Button>
            </div>

            <div className="flex items-center space-x-4">
              <Select value={selectedCategory} onValueChange={setSelectedCategory}>
                <SelectTrigger className="w-[200px]">
                  <SelectValue placeholder="분류 선택" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">전체 분류</SelectItem>
                  {categories.map((category) => (
                    <SelectItem key={category.id} value={category.id.toString()}>
                      {category.fullPath}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>

              <Select value={selectedType} onValueChange={setSelectedType}>
                <SelectTrigger className="w-[150px]">
                  <SelectValue placeholder="상품유형" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">전체 유형</SelectItem>
                  {Object.values(ProductType).map((type) => (
                    <SelectItem key={type} value={type}>
                      {KOREAN_LABELS[type]}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>

              <Select value={selectedStatus} onValueChange={setSelectedStatus}>
                <SelectTrigger className="w-[150px]">
                  <SelectValue placeholder="상품상태" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">전체 상태</SelectItem>
                  {Object.values(ProductStatus).map((status) => (
                    <SelectItem key={status} value={status}>
                      {KOREAN_LABELS[status]}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>

              <Button variant="outline" onClick={handleResetFilters}>
                초기화
              </Button>
            </div>

            {/* 선택된 상품 일괄 작업 */}
            {selectedProducts.length > 0 && (
              <div className="flex items-center justify-between p-4 bg-blue-50 rounded-lg">
                <div className="flex items-center space-x-2">
                  <Badge variant="secondary">
                    {selectedProducts.length}개 선택됨
                  </Badge>
                </div>
                <div className="flex items-center space-x-2">
                  <Button 
                    variant="outline" 
                    size="sm"
                    onClick={() => handleBulkAction('activate')}
                  >
                    활성화
                  </Button>
                  <Button 
                    variant="outline" 
                    size="sm"
                    onClick={() => handleBulkAction('deactivate')}
                  >
                    비활성화
                  </Button>
                  <Button 
                    variant="outline" 
                    size="sm"
                    onClick={() => handleBulkAction('export')}
                  >
                    내보내기
                  </Button>
                  <Button 
                    variant="destructive" 
                    size="sm"
                    onClick={() => handleBulkAction('delete')}
                  >
                    삭제
                  </Button>
                </div>
              </div>
            )}
          </div>
        </CardContent>
      </Card>

      {/* 탭 네비게이션 */}
      <Tabs value={selectedTab} onValueChange={setSelectedTab}>
        <TabsList>
          <TabsTrigger value="all" className="flex items-center space-x-2">
            <Package className="h-4 w-4" />
            <span>전체</span>
            {tabCounts.all > 0 && (
              <Badge variant="secondary">{formatNumber(tabCounts.all)}</Badge>
            )}
          </TabsTrigger>
          <TabsTrigger value="low-stock" className="flex items-center space-x-2">
            <AlertTriangle className="h-4 w-4" />
            <span>안전재고 미달</span>
            {tabCounts.lowStock > 0 && (
              <Badge variant="secondary">{formatNumber(tabCounts.lowStock)}</Badge>
            )}
          </TabsTrigger>
          <TabsTrigger value="out-of-stock" className="flex items-center space-x-2">
            <XCircle className="h-4 w-4" />
            <span>재고없음</span>
            {tabCounts.outOfStock > 0 && (
              <Badge variant="destructive">{formatNumber(tabCounts.outOfStock)}</Badge>
            )}
          </TabsTrigger>
          <TabsTrigger value="inactive" className="flex items-center space-x-2">
            <XCircle className="h-4 w-4" />
            <span>비활성</span>
          </TabsTrigger>
        </TabsList>

        <div className="mt-6">
          <TabsContent value={selectedTab}>
            <Card>
              <CardHeader>
                <div className="flex items-center justify-between">
                  <div>
                    <CardTitle>상품 목록</CardTitle>
                    <CardDescription>
                      총 {formatNumber(totalElements)}개의 상품이 있습니다
                    </CardDescription>
                  </div>
                  <div className="flex items-center space-x-2">
                    <Select value={pageSize.toString()} onValueChange={(value) => setPageSize(Number(value))}>
                      <SelectTrigger className="w-[100px]">
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="10">10개</SelectItem>
                        <SelectItem value="20">20개</SelectItem>
                        <SelectItem value="50">50개</SelectItem>
                        <SelectItem value="100">100개</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                </div>
              </CardHeader>
              <CardContent>
                <ProductTable
                  products={products}
                  selectedProducts={selectedProducts}
                  onSelectProduct={handleSelectProduct}
                  onSelectAll={handleSelectAll}
                  onEdit={handleEditProduct}
                  onDelete={handleDeleteProduct}
                  onView={handleViewProduct}
                  onToggleActive={handleToggleActive}
                  isLoading={isLoadingProducts}
                />

                {/* 페이지네이션 */}
                {totalPages > 1 && (
                  <div className="flex items-center justify-between mt-6">
                    <div className="text-sm text-muted-foreground">
                      {currentPage * pageSize + 1}-{Math.min((currentPage + 1) * pageSize, totalElements)} / {formatNumber(totalElements)}
                    </div>
                    <div className="flex items-center space-x-2">
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => setCurrentPage(Math.max(0, currentPage - 1))}
                        disabled={currentPage === 0}
                      >
                        이전
                      </Button>
                      <div className="text-sm">
                        {currentPage + 1} / {totalPages}
                      </div>
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => setCurrentPage(Math.min(totalPages - 1, currentPage + 1))}
                        disabled={currentPage === totalPages - 1}
                      >
                        다음
                      </Button>
                    </div>
                  </div>
                )}
              </CardContent>
            </Card>
          </TabsContent>
        </div>
      </Tabs>

      {/* 상품 등록/수정 폼 */}
      <ProductForm
        isOpen={isProductFormOpen}
        onClose={() => {
          setIsProductFormOpen(false)
          setEditingProduct(null)
        }}
        onSubmit={handleFormSubmit}
        product={editingProduct}
        companyId={companyId}
        isLoading={createProductMutation.isPending || updateProductMutation.isPending}
      />

      {/* 삭제 확인 다이얼로그 */}
      <AlertDialog open={!!deletingProduct} onOpenChange={() => setDeletingProduct(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>상품 삭제</AlertDialogTitle>
            <AlertDialogDescription>
              '{deletingProduct?.productName}' 상품을 정말 삭제하시겠습니까?
              <br />
              이 작업은 되돌릴 수 없습니다.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>취소</AlertDialogCancel>
            <AlertDialogAction
              onClick={handleConfirmDelete}
              className="bg-red-600 hover:bg-red-700"
            >
              삭제
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  )
}

