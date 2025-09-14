/**
 * 상품 테이블 컴포넌트
 * 상품 목록을 테이블 형태로 표시하고 관리 기능을 제공합니다
 */

import { useState } from 'react'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Checkbox } from '@/components/ui/checkbox'
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'
import { 
  MoreHorizontal, 
  Edit, 
  Trash2, 
  Eye, 
  Package, 
  AlertTriangle,
  CheckCircle,
  XCircle,
  BarChart3,
  Settings
} from 'lucide-react'
import { Product, ProductStatus, ProductType, StockStatus, KOREAN_LABELS } from '../../types/inventory'
import { formatCurrency, formatNumber, formatDate } from '../../utils/format'

interface ProductTableProps {
  products: Product[]
  selectedProducts: number[]
  onSelectProduct: (productId: number) => void
  onSelectAll: (selected: boolean) => void
  onEdit: (product: Product) => void
  onDelete: (product: Product) => void
  onView: (product: Product) => void
  onToggleActive: (product: Product) => void
  isLoading?: boolean
}

/**
 * 상품 상태에 따른 뱃지 컴포넌트
 */
function ProductStatusBadge({ status }: { status: ProductStatus }) {
  const variants = {
    [ProductStatus.ACTIVE]: { variant: 'default' as const, icon: CheckCircle },
    [ProductStatus.INACTIVE]: { variant: 'secondary' as const, icon: XCircle },
    [ProductStatus.DISCONTINUED]: { variant: 'destructive' as const, icon: XCircle },
    [ProductStatus.PENDING]: { variant: 'outline' as const, icon: AlertTriangle },
    [ProductStatus.DRAFT]: { variant: 'outline' as const, icon: Settings },
  }

  const config = variants[status]
  const Icon = config.icon

  return (
    <Badge variant={config.variant} className="flex items-center space-x-1">
      <Icon className="h-3 w-3" />
      <span>{KOREAN_LABELS[status]}</span>
    </Badge>
  )
}

/**
 * 재고 상태에 따른 뱃지 컴포넌트
 */
function StockStatusBadge({ 
  isLowStock, 
  isOutOfStock, 
  isOverStock, 
  currentStock 
}: { 
  isLowStock: boolean
  isOutOfStock: boolean
  isOverStock: boolean
  currentStock: number
}) {
  if (isOutOfStock) {
    return (
      <Badge variant="destructive" className="flex items-center space-x-1">
        <XCircle className="h-3 w-3" />
        <span>재고없음</span>
      </Badge>
    )
  }
  
  if (isLowStock) {
    return (
      <Badge variant="secondary" className="flex items-center space-x-1 bg-yellow-100 text-yellow-800">
        <AlertTriangle className="h-3 w-3" />
        <span>안전재고 미달</span>
      </Badge>
    )
  }
  
  if (isOverStock) {
    return (
      <Badge variant="outline" className="flex items-center space-x-1 bg-blue-100 text-blue-800">
        <Package className="h-3 w-3" />
        <span>과재고</span>
      </Badge>
    )
  }

  return (
    <Badge variant="default" className="flex items-center space-x-1 bg-green-100 text-green-800">
      <CheckCircle className="h-3 w-3" />
      <span>정상</span>
    </Badge>
  )
}

/**
 * 상품 유형 뱃지 컴포넌트
 */
function ProductTypeBadge({ type }: { type: ProductType }) {
  const colors = {
    [ProductType.FINISHED_GOODS]: 'bg-blue-100 text-blue-800',
    [ProductType.RAW_MATERIAL]: 'bg-green-100 text-green-800',
    [ProductType.SEMI_FINISHED]: 'bg-yellow-100 text-yellow-800',
    [ProductType.CONSUMABLE]: 'bg-purple-100 text-purple-800',
    [ProductType.SERVICE]: 'bg-gray-100 text-gray-800',
    [ProductType.VIRTUAL]: 'bg-indigo-100 text-indigo-800',
    [ProductType.BUNDLE]: 'bg-pink-100 text-pink-800',
    [ProductType.DIGITAL]: 'bg-cyan-100 text-cyan-800',
  }

  return (
    <Badge variant="outline" className={colors[type]}>
      {KOREAN_LABELS[type]}
    </Badge>
  )
}

export default function ProductTable({
  products,
  selectedProducts,
  onSelectProduct,
  onSelectAll,
  onEdit,
  onDelete,
  onView,
  onToggleActive,
  isLoading = false
}: ProductTableProps) {
  const [sortField, setSortField] = useState<keyof Product>('productName')
  const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('asc')

  // 정렬 핸들러
  const handleSort = (field: keyof Product) => {
    if (sortField === field) {
      setSortDirection(sortDirection === 'asc' ? 'desc' : 'asc')
    } else {
      setSortField(field)
      setSortDirection('asc')
    }
  }

  // 정렬된 상품 목록
  const sortedProducts = [...products].sort((a, b) => {
    const aValue = a[sortField]
    const bValue = b[sortField]
    
    if (typeof aValue === 'string' && typeof bValue === 'string') {
      return sortDirection === 'asc' 
        ? aValue.localeCompare(bValue)
        : bValue.localeCompare(aValue)
    }
    
    if (typeof aValue === 'number' && typeof bValue === 'number') {
      return sortDirection === 'asc' 
        ? aValue - bValue
        : bValue - aValue
    }
    
    return 0
  })

  // 전체 선택 상태
  const isAllSelected = products.length > 0 && selectedProducts.length === products.length
  const isPartiallySelected = selectedProducts.length > 0 && selectedProducts.length < products.length

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-8">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
      </div>
    )
  }

  if (products.length === 0) {
    return (
      <div className="text-center py-8">
        <Package className="mx-auto h-12 w-12 text-gray-400" />
        <h3 className="mt-2 text-sm font-semibold text-gray-900">상품이 없습니다</h3>
        <p className="mt-1 text-sm text-gray-500">새로운 상품을 등록해보세요.</p>
      </div>
    )
  }

  return (
    <div className="rounded-md border">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead className="w-[50px]">
              <Checkbox
                checked={isAllSelected}
                indeterminate={isPartiallySelected}
                onCheckedChange={onSelectAll}
              />
            </TableHead>
            <TableHead className="w-[80px]">이미지</TableHead>
            <TableHead 
              className="cursor-pointer hover:bg-gray-50"
              onClick={() => handleSort('productCode')}
            >
              상품코드
            </TableHead>
            <TableHead 
              className="cursor-pointer hover:bg-gray-50"
              onClick={() => handleSort('productName')}
            >
              상품명
            </TableHead>
            <TableHead>분류</TableHead>
            <TableHead>유형</TableHead>
            <TableHead>상태</TableHead>
            <TableHead className="text-right">표준가격</TableHead>
            <TableHead className="text-right">현재재고</TableHead>
            <TableHead>재고상태</TableHead>
            <TableHead>등록일</TableHead>
            <TableHead className="w-[50px]"></TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {sortedProducts.map((product) => (
            <TableRow 
              key={product.id}
              className={selectedProducts.includes(product.id) ? 'bg-gray-50' : ''}
            >
              <TableCell>
                <Checkbox
                  checked={selectedProducts.includes(product.id)}
                  onCheckedChange={() => onSelectProduct(product.id)}
                />
              </TableCell>
              
              <TableCell>
                <Avatar className="h-10 w-10">
                  <AvatarImage 
                    src={product.imagePaths?.split(',')[0]} 
                    alt={product.productName}
                  />
                  <AvatarFallback>
                    <Package className="h-4 w-4" />
                  </AvatarFallback>
                </Avatar>
              </TableCell>
              
              <TableCell className="font-mono text-sm">
                {product.productCode}
              </TableCell>
              
              <TableCell>
                <div className="space-y-1">
                  <div className="font-medium">{product.productName}</div>
                  {product.productNameEn && (
                    <div className="text-sm text-muted-foreground">{product.productNameEn}</div>
                  )}
                  {product.brand && (
                    <div className="text-xs text-muted-foreground">브랜드: {product.brand}</div>
                  )}
                </div>
              </TableCell>
              
              <TableCell>
                <div className="text-sm">{product.categoryName}</div>
              </TableCell>
              
              <TableCell>
                <ProductTypeBadge type={product.productType} />
              </TableCell>
              
              <TableCell>
                <ProductStatusBadge status={product.productStatus} />
              </TableCell>
              
              <TableCell className="text-right font-mono">
                {formatCurrency(product.sellingPrice)}
              </TableCell>
              
              <TableCell className="text-right">
                <div className="space-y-1">
                  <div className="font-mono">
                    {formatNumber(product.totalStock)} {product.baseUnit}
                  </div>
                  {product.trackInventory && product.availableStock !== product.totalStock && (
                    <div className="text-xs text-muted-foreground">
                      사용가능: {formatNumber(product.availableStock)}
                    </div>
                  )}
                </div>
              </TableCell>
              
              <TableCell>
                <StockStatusBadge
                  isLowStock={product.isLowStock}
                  isOutOfStock={product.isOutOfStock}
                  isOverStock={product.isOverStock}
                  currentStock={product.totalStock}
                />
              </TableCell>
              
              <TableCell className="text-sm text-muted-foreground">
                {formatDate(product.createdAt)}
              </TableCell>
              
              <TableCell>
                <DropdownMenu>
                  <DropdownMenuTrigger asChild>
                    <Button variant="ghost" className="h-8 w-8 p-0">
                      <span className="sr-only">메뉴 열기</span>
                      <MoreHorizontal className="h-4 w-4" />
                    </Button>
                  </DropdownMenuTrigger>
                  <DropdownMenuContent align="end">
                    <DropdownMenuLabel>작업</DropdownMenuLabel>
                    <DropdownMenuItem onClick={() => onView(product)}>
                      <Eye className="mr-2 h-4 w-4" />
                      상세보기
                    </DropdownMenuItem>
                    <DropdownMenuItem onClick={() => onEdit(product)}>
                      <Edit className="mr-2 h-4 w-4" />
                      수정
                    </DropdownMenuItem>
                    <DropdownMenuSeparator />
                    <DropdownMenuItem onClick={() => onToggleActive(product)}>
                      {product.isActive ? (
                        <>
                          <XCircle className="mr-2 h-4 w-4" />
                          비활성화
                        </>
                      ) : (
                        <>
                          <CheckCircle className="mr-2 h-4 w-4" />
                          활성화
                        </>
                      )}
                    </DropdownMenuItem>
                    <DropdownMenuSeparator />
                    <DropdownMenuItem 
                      onClick={() => onDelete(product)}
                      className="text-red-600"
                    >
                      <Trash2 className="mr-2 h-4 w-4" />
                      삭제
                    </DropdownMenuItem>
                  </DropdownMenuContent>
                </DropdownMenu>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  )
}




