/**
 * 상품 등록/수정 폼 컴포넌트
 * 상품 정보를 입력하고 관리하는 폼입니다
 */

import { useState, useEffect } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import { Button } from '@/components/ui/button'
import { Switch } from '@/components/ui/switch'
import { Badge } from '@/components/ui/badge'
import { Separator } from '@/components/ui/separator'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { 
  Package, 
  Barcode, 
  DollarSign, 
  Warehouse, 
  AlertTriangle,
  Info,
  ImageIcon,
  Tag
} from 'lucide-react'
import { 
  Product, 
  ProductCreateRequest, 
  ProductType, 
  ProductStatus, 
  StockManagementType,
  KOREAN_LABELS
} from '../../types/inventory'
import { useCategories } from '../../hooks/useInventory'
import { formatCurrency } from '../../utils/format'

// 폼 스키마 정의
const productFormSchema = z.object({
  productCode: z.string()
    .min(1, '상품코드는 필수입니다')
    .max(50, '상품코드는 50자 이내여야 합니다')
    .regex(/^[A-Z0-9-_]+$/, '상품코드는 영문 대문자, 숫자, 하이픈(-), 언더스코어(_)만 사용 가능합니다'),
  productName: z.string()
    .min(1, '상품명은 필수입니다')
    .max(200, '상품명은 200자 이내여야 합니다'),
  productNameEn: z.string()
    .max(200, '영문 상품명은 200자 이내여야 합니다')
    .optional(),
  description: z.string()
    .max(1000, '설명은 1000자 이내여야 합니다')
    .optional(),
  detailedDescription: z.string()
    .max(5000, '상세설명은 5000자 이내여야 합니다')
    .optional(),
  companyId: z.number().min(1, '회사는 필수입니다'),
  categoryId: z.number().min(1, '분류는 필수입니다'),
  productType: z.nativeEnum(ProductType),
  productStatus: z.nativeEnum(ProductStatus).default(ProductStatus.ACTIVE),
  stockManagementType: z.nativeEnum(StockManagementType).default(StockManagementType.AVERAGE),
  isActive: z.boolean().default(true),
  trackInventory: z.boolean().default(true),
  barcode: z.string()
    .max(50, '바코드는 50자 이내여야 합니다')
    .optional(),
  qrCode: z.string()
    .max(500, 'QR코드는 500자 이내여야 합니다')
    .optional(),
  sku: z.string()
    .max(100, 'SKU는 100자 이내여야 합니다')
    .optional(),
  baseUnit: z.string()
    .min(1, '기본단위는 필수입니다')
    .max(20, '기본단위는 20자 이내여야 합니다'),
  subUnit: z.string()
    .max(20, '보조단위는 20자 이내여야 합니다')
    .optional(),
  unitConversionRate: z.number()
    .min(0, '단위변환비율은 0 이상이어야 합니다')
    .default(1),
  standardCost: z.number()
    .min(0, '표준원가는 0 이상이어야 합니다')
    .default(0),
  sellingPrice: z.number()
    .min(0, '판매가격은 0 이상이어야 합니다')
    .default(0),
  minSellingPrice: z.number()
    .min(0, '최소판매가격은 0 이상이어야 합니다')
    .default(0),
  safetyStock: z.number()
    .min(0, '안전재고는 0 이상이어야 합니다')
    .default(0),
  minStock: z.number()
    .min(0, '최소재고는 0 이상이어야 합니다')
    .default(0),
  maxStock: z.number()
    .min(0, '최대재고는 0 이상이어야 합니다')
    .default(0),
  reorderPoint: z.number()
    .min(0, '재주문포인트는 0 이상이어야 합니다')
    .default(0),
  reorderQuantity: z.number()
    .min(0, '재주문수량은 0 이상이어야 합니다')
    .default(0),
  leadTimeDays: z.number()
    .min(0, '리드타임은 0 이상이어야 합니다')
    .default(0),
  shelfLifeDays: z.number()
    .min(0, '유효기간은 0 이상이어야 합니다')
    .optional(),
  width: z.number()
    .min(0, '너비는 0 이상이어야 합니다')
    .optional(),
  height: z.number()
    .min(0, '높이는 0 이상이어야 합니다')
    .optional(),
  depth: z.number()
    .min(0, '깊이는 0 이상이어야 합니다')
    .optional(),
  weight: z.number()
    .min(0, '무게는 0 이상이어야 합니다')
    .optional(),
  color: z.string()
    .max(50, '색상은 50자 이내여야 합니다')
    .optional(),
  size: z.string()
    .max(50, '크기는 50자 이내여야 합니다')
    .optional(),
  brand: z.string()
    .max(100, '브랜드는 100자 이내여야 합니다')
    .optional(),
  manufacturer: z.string()
    .max(200, '제조업체는 200자 이내여야 합니다')
    .optional(),
  supplier: z.string()
    .max(200, '공급업체는 200자 이내여야 합니다')
    .optional(),
  originCountry: z.string()
    .max(100, '원산지는 100자 이내여야 합니다')
    .optional(),
  hsCode: z.string()
    .max(20, 'HS코드는 20자 이내여야 합니다')
    .optional(),
  taxRate: z.number()
    .min(0, '세율은 0 이상이어야 합니다')
    .max(100, '세율은 100 이하여야 합니다')
    .default(10),
  tags: z.string()
    .max(500, '태그는 500자 이내여야 합니다')
    .optional(),
  sortOrder: z.number()
    .min(0, '정렬순서는 0 이상이어야 합니다')
    .default(0),
  metadata: z.string()
    .max(2000, '메타데이터는 2000자 이내여야 합니다')
    .optional(),
})

type ProductFormValues = z.infer<typeof productFormSchema>

interface ProductFormProps {
  isOpen: boolean
  onClose: () => void
  onSubmit: (data: ProductCreateRequest) => void
  product?: Product | null
  companyId: number
  isLoading?: boolean
}

export default function ProductForm({
  isOpen,
  onClose,
  onSubmit,
  product,
  companyId,
  isLoading = false
}: ProductFormProps) {
  const [activeTab, setActiveTab] = useState('basic')
  
  // 분류 목록 조회
  const { data: categoriesResponse } = useCategories(companyId)
  const categories = categoriesResponse?.data || []

  // 폼 초기화
  const form = useForm<ProductFormValues>({
    resolver: zodResolver(productFormSchema),
    defaultValues: {
      companyId,
      productType: ProductType.FINISHED_GOODS,
      productStatus: ProductStatus.ACTIVE,
      stockManagementType: StockManagementType.AVERAGE,
      isActive: true,
      trackInventory: true,
      baseUnit: '개',
      unitConversionRate: 1,
      standardCost: 0,
      sellingPrice: 0,
      minSellingPrice: 0,
      safetyStock: 0,
      minStock: 0,
      maxStock: 0,
      reorderPoint: 0,
      reorderQuantity: 0,
      leadTimeDays: 0,
      taxRate: 10,
      sortOrder: 0,
    }
  })

  // 상품 데이터로 폼 초기화
  useEffect(() => {
    if (product) {
      form.reset({
        productCode: product.productCode,
        productName: product.productName,
        productNameEn: product.productNameEn || '',
        description: product.description || '',
        detailedDescription: product.detailedDescription || '',
        companyId: product.companyId,
        categoryId: product.categoryId,
        productType: product.productType,
        productStatus: product.productStatus,
        stockManagementType: product.stockManagementType,
        isActive: product.isActive,
        trackInventory: product.trackInventory,
        barcode: product.barcode || '',
        qrCode: product.qrCode || '',
        sku: product.sku || '',
        baseUnit: product.baseUnit,
        subUnit: product.subUnit || '',
        unitConversionRate: product.unitConversionRate,
        standardCost: product.standardCost,
        sellingPrice: product.sellingPrice,
        minSellingPrice: product.minSellingPrice,
        safetyStock: product.safetyStock,
        minStock: product.minStock,
        maxStock: product.maxStock,
        reorderPoint: product.reorderPoint,
        reorderQuantity: product.reorderQuantity,
        leadTimeDays: product.leadTimeDays,
        shelfLifeDays: product.shelfLifeDays,
        width: product.width,
        height: product.height,
        depth: product.depth,
        weight: product.weight,
        color: product.color || '',
        size: product.size || '',
        brand: product.brand || '',
        manufacturer: product.manufacturer || '',
        supplier: product.supplier || '',
        originCountry: product.originCountry || '',
        hsCode: product.hsCode || '',
        taxRate: product.taxRate,
        tags: product.tags || '',
        sortOrder: product.sortOrder,
        metadata: product.metadata || '',
      })
    } else {
      form.reset({
        companyId,
        productType: ProductType.FINISHED_GOODS,
        productStatus: ProductStatus.ACTIVE,
        stockManagementType: StockManagementType.AVERAGE,
        isActive: true,
        trackInventory: true,
        baseUnit: '개',
        unitConversionRate: 1,
        standardCost: 0,
        sellingPrice: 0,
        minSellingPrice: 0,
        safetyStock: 0,
        minStock: 0,
        maxStock: 0,
        reorderPoint: 0,
        reorderQuantity: 0,
        leadTimeDays: 0,
        taxRate: 10,
        sortOrder: 0,
      })
    }
  }, [product, companyId, form])

  // 폼 제출 핸들러
  const handleSubmit = (values: ProductFormValues) => {
    onSubmit(values as ProductCreateRequest)
  }

  // 이익률 계산
  const sellingPrice = form.watch('sellingPrice')
  const standardCost = form.watch('standardCost')
  const profitRate = standardCost > 0 ? ((sellingPrice - standardCost) / standardCost * 100) : 0

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="max-w-4xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="flex items-center space-x-2">
            <Package className="h-5 w-5" />
            <span>{product ? '상품 수정' : '상품 등록'}</span>
          </DialogTitle>
          <DialogDescription>
            {product ? '상품 정보를 수정합니다' : '새로운 상품을 등록합니다'}
          </DialogDescription>
        </DialogHeader>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-6">
            <Tabs value={activeTab} onValueChange={setActiveTab}>
              <TabsList className="grid w-full grid-cols-5">
                <TabsTrigger value="basic">기본정보</TabsTrigger>
                <TabsTrigger value="pricing">가격정보</TabsTrigger>
                <TabsTrigger value="inventory">재고정보</TabsTrigger>
                <TabsTrigger value="physical">물리정보</TabsTrigger>
                <TabsTrigger value="additional">추가정보</TabsTrigger>
              </TabsList>

              {/* 기본정보 탭 */}
              <TabsContent value="basic" className="space-y-4">
                <Card>
                  <CardHeader>
                    <CardTitle className="text-lg">기본 상품 정보</CardTitle>
                    <CardDescription>
                      상품의 기본적인 정보를 입력해주세요
                    </CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-4">
                    <div className="grid grid-cols-2 gap-4">
                      <FormField
                        control={form.control}
                        name="productCode"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>상품코드 *</FormLabel>
                            <FormControl>
                              <Input placeholder="PROD-001" {...field} />
                            </FormControl>
                            <FormDescription>
                              영문 대문자, 숫자, 하이픈(-), 언더스코어(_) 사용 가능
                            </FormDescription>
                            <FormMessage />
                          </FormItem>
                        )}
                      />
                      
                      <FormField
                        control={form.control}
                        name="categoryId"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>상품분류 *</FormLabel>
                            <Select 
                              onValueChange={(value) => field.onChange(Number(value))}
                              value={field.value?.toString()}
                            >
                              <FormControl>
                                <SelectTrigger>
                                  <SelectValue placeholder="분류를 선택하세요" />
                                </SelectTrigger>
                              </FormControl>
                              <SelectContent>
                                {categories.map((category) => (
                                  <SelectItem 
                                    key={category.id} 
                                    value={category.id.toString()}
                                  >
                                    {category.fullPath}
                                  </SelectItem>
                                ))}
                              </SelectContent>
                            </Select>
                            <FormMessage />
                          </FormItem>
                        )}
                      />
                    </div>

                    <FormField
                      control={form.control}
                      name="productName"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>상품명 *</FormLabel>
                          <FormControl>
                            <Input placeholder="상품명을 입력하세요" {...field} />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />

                    <FormField
                      control={form.control}
                      name="productNameEn"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>영문 상품명</FormLabel>
                          <FormControl>
                            <Input placeholder="Product Name (English)" {...field} />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />

                    <div className="grid grid-cols-3 gap-4">
                      <FormField
                        control={form.control}
                        name="productType"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>상품유형 *</FormLabel>
                            <Select 
                              onValueChange={field.onChange}
                              value={field.value}
                            >
                              <FormControl>
                                <SelectTrigger>
                                  <SelectValue />
                                </SelectTrigger>
                              </FormControl>
                              <SelectContent>
                                {Object.values(ProductType).map((type) => (
                                  <SelectItem key={type} value={type}>
                                    {KOREAN_LABELS[type]}
                                  </SelectItem>
                                ))}
                              </SelectContent>
                            </Select>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={form.control}
                        name="productStatus"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>상품상태</FormLabel>
                            <Select 
                              onValueChange={field.onChange}
                              value={field.value}
                            >
                              <FormControl>
                                <SelectTrigger>
                                  <SelectValue />
                                </SelectTrigger>
                              </FormControl>
                              <SelectContent>
                                {Object.values(ProductStatus).map((status) => (
                                  <SelectItem key={status} value={status}>
                                    {KOREAN_LABELS[status]}
                                  </SelectItem>
                                ))}
                              </SelectContent>
                            </Select>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={form.control}
                        name="stockManagementType"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>재고관리방식</FormLabel>
                            <Select 
                              onValueChange={field.onChange}
                              value={field.value}
                            >
                              <FormControl>
                                <SelectTrigger>
                                  <SelectValue />
                                </SelectTrigger>
                              </FormControl>
                              <SelectContent>
                                {Object.values(StockManagementType).map((type) => (
                                  <SelectItem key={type} value={type}>
                                    {KOREAN_LABELS[type]}
                                  </SelectItem>
                                ))}
                              </SelectContent>
                            </Select>
                            <FormMessage />
                          </FormItem>
                        )}
                      />
                    </div>

                    <FormField
                      control={form.control}
                      name="description"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>간단 설명</FormLabel>
                          <FormControl>
                            <Textarea 
                              placeholder="상품에 대한 간단한 설명을 입력하세요"
                              className="min-h-[80px]"
                              {...field} 
                            />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />

                    <FormField
                      control={form.control}
                      name="detailedDescription"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>상세 설명</FormLabel>
                          <FormControl>
                            <Textarea 
                              placeholder="상품에 대한 상세한 설명을 입력하세요"
                              className="min-h-[120px]"
                              {...field} 
                            />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />

                    <div className="flex items-center space-x-6">
                      <FormField
                        control={form.control}
                        name="isActive"
                        render={({ field }) => (
                          <FormItem className="flex flex-row items-center justify-between rounded-lg border p-3 shadow-sm">
                            <div className="space-y-0.5">
                              <FormLabel>활성 상태</FormLabel>
                              <FormDescription>
                                상품의 활성화 여부를 설정합니다
                              </FormDescription>
                            </div>
                            <FormControl>
                              <Switch
                                checked={field.value}
                                onCheckedChange={field.onChange}
                              />
                            </FormControl>
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={form.control}
                        name="trackInventory"
                        render={({ field }) => (
                          <FormItem className="flex flex-row items-center justify-between rounded-lg border p-3 shadow-sm">
                            <div className="space-y-0.5">
                              <FormLabel>재고 추적</FormLabel>
                              <FormDescription>
                                재고 수량을 추적할지 설정합니다
                              </FormDescription>
                            </div>
                            <FormControl>
                              <Switch
                                checked={field.value}
                                onCheckedChange={field.onChange}
                              />
                            </FormControl>
                          </FormItem>
                        )}
                      />
                    </div>
                  </CardContent>
                </Card>
              </TabsContent>

              {/* 가격정보 탭 */}
              <TabsContent value="pricing" className="space-y-4">
                <Card>
                  <CardHeader>
                    <CardTitle className="text-lg flex items-center space-x-2">
                      <DollarSign className="h-5 w-5" />
                      <span>가격 정보</span>
                    </CardTitle>
                    <CardDescription>
                      상품의 가격 관련 정보를 입력해주세요
                    </CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-4">
                    <div className="grid grid-cols-2 gap-4">
                      <FormField
                        control={form.control}
                        name="standardCost"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>표준원가</FormLabel>
                            <FormControl>
                              <Input 
                                type="number" 
                                step="0.01"
                                placeholder="0"
                                {...field}
                                onChange={(e) => field.onChange(Number(e.target.value))}
                              />
                            </FormControl>
                            <FormDescription>
                              상품의 표준 원가를 입력하세요
                            </FormDescription>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={form.control}
                        name="sellingPrice"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>판매가격</FormLabel>
                            <FormControl>
                              <Input 
                                type="number" 
                                step="0.01"
                                placeholder="0"
                                {...field}
                                onChange={(e) => field.onChange(Number(e.target.value))}
                              />
                            </FormControl>
                            <FormDescription>
                              기본 판매가격을 입력하세요
                            </FormDescription>
                            <FormMessage />
                          </FormItem>
                        )}
                      />
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                      <FormField
                        control={form.control}
                        name="minSellingPrice"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>최소판매가격</FormLabel>
                            <FormControl>
                              <Input 
                                type="number" 
                                step="0.01"
                                placeholder="0"
                                {...field}
                                onChange={(e) => field.onChange(Number(e.target.value))}
                              />
                            </FormControl>
                            <FormDescription>
                              최소 판매 가능 가격을 입력하세요
                            </FormDescription>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={form.control}
                        name="taxRate"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>세율 (%)</FormLabel>
                            <FormControl>
                              <Input 
                                type="number" 
                                step="0.1"
                                min="0"
                                max="100"
                                placeholder="10"
                                {...field}
                                onChange={(e) => field.onChange(Number(e.target.value))}
                              />
                            </FormControl>
                            <FormDescription>
                              적용할 세율을 입력하세요
                            </FormDescription>
                            <FormMessage />
                          </FormItem>
                        )}
                      />
                    </div>

                    {/* 이익률 표시 */}
                    {standardCost > 0 && (
                      <Alert>
                        <Info className="h-4 w-4" />
                        <AlertDescription>
                          <div className="flex items-center justify-between">
                            <span>예상 이익률</span>
                            <Badge variant={profitRate > 0 ? 'default' : 'destructive'}>
                              {profitRate.toFixed(1)}%
                            </Badge>
                          </div>
                          <div className="text-sm text-muted-foreground mt-1">
                            이익: {formatCurrency(sellingPrice - standardCost)}
                          </div>
                        </AlertDescription>
                      </Alert>
                    )}
                  </CardContent>
                </Card>
              </TabsContent>

              {/* 재고정보 탭 */}
              <TabsContent value="inventory" className="space-y-4">
                <Card>
                  <CardHeader>
                    <CardTitle className="text-lg flex items-center space-x-2">
                      <Warehouse className="h-5 w-5" />
                      <span>재고 관리 정보</span>
                    </CardTitle>
                    <CardDescription>
                      재고 관리와 관련된 정보를 입력해주세요
                    </CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-4">
                    <div className="grid grid-cols-3 gap-4">
                      <FormField
                        control={form.control}
                        name="baseUnit"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>기본단위 *</FormLabel>
                            <FormControl>
                              <Input placeholder="개" {...field} />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={form.control}
                        name="subUnit"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>보조단위</FormLabel>
                            <FormControl>
                              <Input placeholder="박스" {...field} />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={form.control}
                        name="unitConversionRate"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>단위변환비율</FormLabel>
                            <FormControl>
                              <Input 
                                type="number" 
                                step="0.01"
                                min="0"
                                placeholder="1"
                                {...field}
                                onChange={(e) => field.onChange(Number(e.target.value))}
                              />
                            </FormControl>
                            <FormDescription>
                              보조단위 1개 = 기본단위 N개
                            </FormDescription>
                            <FormMessage />
                          </FormItem>
                        )}
                      />
                    </div>

                    <Separator />

                    <div className="grid grid-cols-2 gap-4">
                      <FormField
                        control={form.control}
                        name="safetyStock"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>안전재고</FormLabel>
                            <FormControl>
                              <Input 
                                type="number" 
                                min="0"
                                placeholder="0"
                                {...field}
                                onChange={(e) => field.onChange(Number(e.target.value))}
                              />
                            </FormControl>
                            <FormDescription>
                              최소 유지해야 할 재고 수량
                            </FormDescription>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={form.control}
                        name="reorderPoint"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>재주문포인트</FormLabel>
                            <FormControl>
                              <Input 
                                type="number" 
                                min="0"
                                placeholder="0"
                                {...field}
                                onChange={(e) => field.onChange(Number(e.target.value))}
                              />
                            </FormControl>
                            <FormDescription>
                              자동 재주문 알림 기준 수량
                            </FormDescription>
                            <FormMessage />
                          </FormItem>
                        )}
                      />
                    </div>

                    <div className="grid grid-cols-3 gap-4">
                      <FormField
                        control={form.control}
                        name="minStock"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>최소재고</FormLabel>
                            <FormControl>
                              <Input 
                                type="number" 
                                min="0"
                                placeholder="0"
                                {...field}
                                onChange={(e) => field.onChange(Number(e.target.value))}
                              />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={form.control}
                        name="maxStock"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>최대재고</FormLabel>
                            <FormControl>
                              <Input 
                                type="number" 
                                min="0"
                                placeholder="0"
                                {...field}
                                onChange={(e) => field.onChange(Number(e.target.value))}
                              />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={form.control}
                        name="reorderQuantity"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>재주문수량</FormLabel>
                            <FormControl>
                              <Input 
                                type="number" 
                                min="0"
                                placeholder="0"
                                {...field}
                                onChange={(e) => field.onChange(Number(e.target.value))}
                              />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                      <FormField
                        control={form.control}
                        name="leadTimeDays"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>리드타임 (일)</FormLabel>
                            <FormControl>
                              <Input 
                                type="number" 
                                min="0"
                                placeholder="0"
                                {...field}
                                onChange={(e) => field.onChange(Number(e.target.value))}
                              />
                            </FormControl>
                            <FormDescription>
                              주문부터 입고까지 소요 일수
                            </FormDescription>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={form.control}
                        name="shelfLifeDays"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>유효기간 (일)</FormLabel>
                            <FormControl>
                              <Input 
                                type="number" 
                                min="0"
                                placeholder=""
                                {...field}
                                onChange={(e) => field.onChange(e.target.value ? Number(e.target.value) : undefined)}
                              />
                            </FormControl>
                            <FormDescription>
                              상품의 유효기간 (비어있으면 무제한)
                            </FormDescription>
                            <FormMessage />
                          </FormItem>
                        )}
                      />
                    </div>
                  </CardContent>
                </Card>
              </TabsContent>

              {/* 물리정보 탭 */}
              <TabsContent value="physical" className="space-y-4">
                <Card>
                  <CardHeader>
                    <CardTitle className="text-lg">물리적 속성</CardTitle>
                    <CardDescription>
                      상품의 물리적 특성을 입력해주세요
                    </CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-4">
                    <div className="grid grid-cols-4 gap-4">
                      <FormField
                        control={form.control}
                        name="width"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>너비 (cm)</FormLabel>
                            <FormControl>
                              <Input 
                                type="number" 
                                step="0.1"
                                min="0"
                                placeholder=""
                                {...field}
                                onChange={(e) => field.onChange(e.target.value ? Number(e.target.value) : undefined)}
                              />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={form.control}
                        name="height"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>높이 (cm)</FormLabel>
                            <FormControl>
                              <Input 
                                type="number" 
                                step="0.1"
                                min="0"
                                placeholder=""
                                {...field}
                                onChange={(e) => field.onChange(e.target.value ? Number(e.target.value) : undefined)}
                              />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={form.control}
                        name="depth"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>깊이 (cm)</FormLabel>
                            <FormControl>
                              <Input 
                                type="number" 
                                step="0.1"
                                min="0"
                                placeholder=""
                                {...field}
                                onChange={(e) => field.onChange(e.target.value ? Number(e.target.value) : undefined)}
                              />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={form.control}
                        name="weight"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>무게 (kg)</FormLabel>
                            <FormControl>
                              <Input 
                                type="number" 
                                step="0.01"
                                min="0"
                                placeholder=""
                                {...field}
                                onChange={(e) => field.onChange(e.target.value ? Number(e.target.value) : undefined)}
                              />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                      <FormField
                        control={form.control}
                        name="color"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>색상</FormLabel>
                            <FormControl>
                              <Input placeholder="빨강, 파랑, 검정 등" {...field} />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={form.control}
                        name="size"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>크기</FormLabel>
                            <FormControl>
                              <Input placeholder="S, M, L, XL 등" {...field} />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                      <FormField
                        control={form.control}
                        name="barcode"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel className="flex items-center space-x-2">
                              <Barcode className="h-4 w-4" />
                              <span>바코드</span>
                            </FormLabel>
                            <FormControl>
                              <Input placeholder="1234567890123" {...field} />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={form.control}
                        name="sku"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>SKU</FormLabel>
                            <FormControl>
                              <Input placeholder="Stock Keeping Unit" {...field} />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />
                    </div>
                  </CardContent>
                </Card>
              </TabsContent>

              {/* 추가정보 탭 */}
              <TabsContent value="additional" className="space-y-4">
                <Card>
                  <CardHeader>
                    <CardTitle className="text-lg">추가 정보</CardTitle>
                    <CardDescription>
                      기타 상품 관련 정보를 입력해주세요
                    </CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-4">
                    <div className="grid grid-cols-2 gap-4">
                      <FormField
                        control={form.control}
                        name="brand"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>브랜드</FormLabel>
                            <FormControl>
                              <Input placeholder="브랜드명" {...field} />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={form.control}
                        name="manufacturer"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>제조업체</FormLabel>
                            <FormControl>
                              <Input placeholder="제조업체명" {...field} />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                      <FormField
                        control={form.control}
                        name="supplier"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>공급업체</FormLabel>
                            <FormControl>
                              <Input placeholder="공급업체명" {...field} />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={form.control}
                        name="originCountry"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>원산지</FormLabel>
                            <FormControl>
                              <Input placeholder="대한민국" {...field} />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                      <FormField
                        control={form.control}
                        name="hsCode"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>HS코드</FormLabel>
                            <FormControl>
                              <Input placeholder="0000.00.0000" {...field} />
                            </FormControl>
                            <FormDescription>
                              수출입 관세 분류 코드
                            </FormDescription>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={form.control}
                        name="sortOrder"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>정렬순서</FormLabel>
                            <FormControl>
                              <Input 
                                type="number" 
                                min="0"
                                placeholder="0"
                                {...field}
                                onChange={(e) => field.onChange(Number(e.target.value))}
                              />
                            </FormControl>
                            <FormDescription>
                              목록에서의 표시 순서
                            </FormDescription>
                            <FormMessage />
                          </FormItem>
                        )}
                      />
                    </div>

                    <FormField
                      control={form.control}
                      name="tags"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel className="flex items-center space-x-2">
                            <Tag className="h-4 w-4" />
                            <span>태그</span>
                          </FormLabel>
                          <FormControl>
                            <Input placeholder="태그1, 태그2, 태그3" {...field} />
                          </FormControl>
                          <FormDescription>
                            쉼표(,)로 구분하여 입력하세요
                          </FormDescription>
                          <FormMessage />
                        </FormItem>
                      )}
                    />

                    <FormField
                      control={form.control}
                      name="metadata"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>메타데이터</FormLabel>
                          <FormControl>
                            <Textarea 
                              placeholder="추가적인 정보를 JSON 형태로 입력하세요"
                              className="min-h-[100px]"
                              {...field} 
                            />
                          </FormControl>
                          <FormDescription>
                            JSON 형태의 추가 정보 (예: 색상 옵션, 사이즈 정보 등)
                          </FormDescription>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                  </CardContent>
                </Card>
              </TabsContent>
            </Tabs>

            <DialogFooter>
              <Button type="button" variant="outline" onClick={onClose}>
                취소
              </Button>
              <Button type="submit" disabled={isLoading}>
                {isLoading ? '처리중...' : (product ? '수정' : '등록')}
              </Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  )
}




