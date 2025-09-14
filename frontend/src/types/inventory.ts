/**
 * 재고 관리 모듈 TypeScript 타입 정의
 * 백엔드 DTO와 매칭되는 인터페이스들을 정의합니다
 */

import { ApiResponse, PageResponse } from './common'

/**
 * 상품 유형 열거형
 */
export enum ProductType {
  FINISHED_GOODS = 'FINISHED_GOODS',
  RAW_MATERIAL = 'RAW_MATERIAL',
  SEMI_FINISHED = 'SEMI_FINISHED',
  CONSUMABLE = 'CONSUMABLE',
  SERVICE = 'SERVICE',
  VIRTUAL = 'VIRTUAL',
  BUNDLE = 'BUNDLE',
  DIGITAL = 'DIGITAL'
}

/**
 * 상품 상태 열거형
 */
export enum ProductStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  DISCONTINUED = 'DISCONTINUED',
  PENDING = 'PENDING',
  DRAFT = 'DRAFT'
}

/**
 * 재고 관리 방식 열거형
 */
export enum StockManagementType {
  FIFO = 'FIFO',
  LIFO = 'LIFO',
  AVERAGE = 'AVERAGE',
  SPECIFIC = 'SPECIFIC'
}

/**
 * 재고 상태 열거형
 */
export enum StockStatus {
  NORMAL = 'NORMAL',
  LOW_STOCK = 'LOW_STOCK',
  OUT_OF_STOCK = 'OUT_OF_STOCK',
  OVER_STOCK = 'OVER_STOCK',
  RESERVED = 'RESERVED',
  QUARANTINE = 'QUARANTINE',
  DAMAGED = 'DAMAGED',
  EXPIRED = 'EXPIRED'
}

/**
 * 재고 등급 열거형
 */
export enum StockGrade {
  A = 'A',
  B = 'B',
  C = 'C',
  D = 'D',
  DEFECTIVE = 'DEFECTIVE'
}

/**
 * 재고이동 유형 열거형
 */
export enum MovementType {
  // 입고 관련
  RECEIPT = 'RECEIPT',
  PURCHASE_RECEIPT = 'PURCHASE_RECEIPT',
  PRODUCTION_RECEIPT = 'PRODUCTION_RECEIPT',
  RETURN_RECEIPT = 'RETURN_RECEIPT',
  TRANSFER_IN = 'TRANSFER_IN',
  ADJUSTMENT_IN = 'ADJUSTMENT_IN',
  
  // 출고 관련
  ISSUE = 'ISSUE',
  SALES_ISSUE = 'SALES_ISSUE',
  PRODUCTION_ISSUE = 'PRODUCTION_ISSUE',
  RETURN_ISSUE = 'RETURN_ISSUE',
  TRANSFER_OUT = 'TRANSFER_OUT',
  ADJUSTMENT_OUT = 'ADJUSTMENT_OUT',
  DISPOSAL = 'DISPOSAL',
  
  // 이동 관련
  WAREHOUSE_TRANSFER = 'WAREHOUSE_TRANSFER',
  LOCATION_TRANSFER = 'LOCATION_TRANSFER',
  
  // 실사 관련
  STOCKTAKING_INCREASE = 'STOCKTAKING_INCREASE',
  STOCKTAKING_DECREASE = 'STOCKTAKING_DECREASE',
  
  // 상태 변경
  RESERVE = 'RESERVE',
  UNRESERVE = 'UNRESERVE',
  QUARANTINE = 'QUARANTINE',
  UNQUARANTINE = 'UNQUARANTINE',
  DEFECTIVE = 'DEFECTIVE',
  REPAIR = 'REPAIR'
}

/**
 * 이동 상태 열거형
 */
export enum MovementStatus {
  DRAFT = 'DRAFT',
  PENDING = 'PENDING',
  APPROVED = 'APPROVED',
  PROCESSED = 'PROCESSED',
  CANCELLED = 'CANCELLED',
  REJECTED = 'REJECTED'
}

/**
 * 창고 유형 열거형
 */
export enum WarehouseType {
  MAIN = 'MAIN',
  SUB = 'SUB',
  EXTERNAL = 'EXTERNAL',
  VIRTUAL = 'VIRTUAL',
  CONSIGNMENT = 'CONSIGNMENT',
  QUARANTINE = 'QUARANTINE',
  RETURNED = 'RETURNED',
  DAMAGED = 'DAMAGED'
}

/**
 * 창고 상태 열거형
 */
export enum WarehouseStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  MAINTENANCE = 'MAINTENANCE',
  CLOSED = 'CLOSED'
}

/**
 * 상품분류 인터페이스
 */
export interface Category {
  id: number
  categoryCode: string
  categoryName: string
  categoryNameEn?: string
  description?: string
  companyId: number
  companyName: string
  parentCategoryId?: number
  parentCategoryName?: string
  categoryLevel: number
  sortOrder: number
  isActive: boolean
  manageProducts: boolean
  imagePath?: string
  icon?: string
  color?: string
  metadata?: string
  fullPath: string
  fullCodePath: string
  isLeafCategory: boolean
  productCount: number
  createdAt: string
  updatedAt: string
  subCategories?: Category[]
  totalProductCount: number
  activeProductCount: number
}

/**
 * 상품 인터페이스
 */
export interface Product {
  id: number
  productCode: string
  productName: string
  productNameEn?: string
  description?: string
  detailedDescription?: string
  companyId: number
  companyName: string
  categoryId: number
  categoryName: string
  categoryFullPath: string
  productType: ProductType
  productTypeDescription: string
  productStatus: ProductStatus
  productStatusDescription: string
  stockManagementType: StockManagementType
  stockManagementTypeDescription: string
  isActive: boolean
  trackInventory: boolean
  barcode?: string
  qrCode?: string
  sku?: string
  baseUnit: string
  subUnit?: string
  unitConversionRate: number
  standardCost: number
  averageCost: number
  lastPurchasePrice: number
  sellingPrice: number
  minSellingPrice: number
  safetyStock: number
  minStock: number
  maxStock: number
  reorderPoint: number
  reorderQuantity: number
  leadTimeDays: number
  shelfLifeDays?: number
  width?: number
  height?: number
  depth?: number
  weight?: number
  volume?: number
  color?: string
  size?: string
  brand?: string
  manufacturer?: string
  supplier?: string
  originCountry?: string
  hsCode?: string
  taxRate: number
  imagePaths?: string
  attachmentPaths?: string
  tags?: string
  sortOrder: number
  metadata?: string
  createdAt: string
  updatedAt: string
  
  // 재고 관련 정보
  totalStock: number
  availableStock: number
  reservedStock: number
  isLowStock: boolean
  isOutOfStock: boolean
  isOverStock: boolean
  needsReorder: boolean
  stockStatusSummary: string
  profitRate: number
}

/**
 * 창고 인터페이스
 */
export interface Warehouse {
  id: number
  warehouseCode: string
  warehouseName: string
  warehouseNameEn?: string
  description?: string
  companyId: number
  companyName: string
  warehouseType: WarehouseType
  warehouseTypeDescription: string
  warehouseStatus: WarehouseStatus
  warehouseStatusDescription: string
  isActive: boolean
  address?: string
  addressDetail?: string
  postalCode?: string
  fullAddress: string
  phoneNumber?: string
  faxNumber?: string
  email?: string
  managerName?: string
  managerPhone?: string
  totalArea?: number
  usableArea?: number
  maxCapacity?: number
  currentUsage: number
  usageRate: number
  remainingCapacity?: number
  temperatureMin?: number
  temperatureMax?: number
  humidityMin?: number
  humidityMax?: number
  specialConditions?: string
  securityLevel?: string
  accessPermissions?: string
  operatingHours?: string
  latitude?: number
  longitude?: number
  sortOrder: number
  metadata?: string
  createdAt: string
  updatedAt: string
  
  // 통계 정보
  totalProductCount: number
  totalStockQuantity: number
  lowStockCount: number
  outOfStockCount: number
  overStockCount: number
  isOperational: boolean
  isCapacityExceeded: boolean
  statusDescription: string
}

/**
 * 재고 인터페이스
 */
export interface Inventory {
  id: number
  companyId: number
  companyName: string
  productId: number
  productCode: string
  productName: string
  categoryName: string
  warehouseId: number
  warehouseName: string
  locationCode: string
  locationDescription?: string
  fullLocation: string
  currentStock: number
  availableStock: number
  reservedStock: number
  orderedStock: number
  defectiveStock: number
  quarantineStock: number
  safetyStock?: number
  minStock?: number
  maxStock?: number
  reorderPoint?: number
  reorderQuantity?: number
  averageCost: number
  lastPurchasePrice?: number
  totalStockValue: number
  stockStatus: StockStatus
  stockStatusDescription: string
  stockGrade: StockGrade
  stockGradeDescription: string
  isLowStock: boolean
  isOutOfStock: boolean
  isOverStock: boolean
  needsReorder: boolean
  lastReceiptDate?: string
  lastIssueDate?: string
  lastStocktakingDate?: string
  lastStockUpdate: string
  movementCount: number
  temperature?: number
  humidity?: number
  expiryDate?: string
  manufactureDate?: string
  lotNumber?: string
  serialNumber?: string
  supplierInfo?: string
  remarks?: string
  metadata?: string
  createdAt: string
  updatedAt: string
  
  // 계산 필드들
  usageRate?: number
  daysInStock?: number
  isExpiringSoon: boolean
  isExpired: boolean
  stockTurnoverRate?: number
}

/**
 * 재고이동 인터페이스
 */
export interface StockMovement {
  id: number
  movementNumber: string
  companyId: number
  companyName: string
  productId: number
  productCode: string
  productName: string
  categoryName: string
  warehouseId: number
  warehouseName: string
  inventoryId?: number
  movementType: MovementType
  movementTypeDescription: string
  movementStatus: MovementStatus
  movementStatusDescription: string
  movementDate: string
  quantity: number
  unit: string
  unitPrice: number
  totalAmount: number
  beforeStock?: number
  afterStock?: number
  fromWarehouseId?: number
  fromWarehouseName?: string
  fromLocation?: string
  fromLocationInfo: string
  toWarehouseId?: number
  toWarehouseName?: string
  toLocation?: string
  toLocationInfo: string
  referenceNumber?: string
  referenceType?: string
  businessPartner?: string
  lotNumber?: string
  serialNumber?: string
  expiryDate?: string
  manufactureDate?: string
  reason?: string
  description?: string
  processedById?: number
  processedByName?: string
  processedAt?: string
  approvedById?: number
  approvedByName?: string
  approvedAt?: string
  cancelReason?: string
  cancelledAt?: string
  attachmentPath?: string
  metadata?: string
  createdAt: string
  updatedAt: string
  
  // 계산 필드들
  isInbound: boolean
  isOutbound: boolean
  isTransfer: boolean
  isProcessed: boolean
  isApproved: boolean
  isCancellable: boolean
  stockChange: number
  movementDirection: string
  movementSummary: string
  statusColorClass: string
}

/**
 * 상품 생성 요청 인터페이스
 */
export interface ProductCreateRequest {
  productCode: string
  productName: string
  productNameEn?: string
  description?: string
  detailedDescription?: string
  companyId: number
  categoryId: number
  productType: ProductType
  productStatus?: ProductStatus
  stockManagementType?: StockManagementType
  isActive?: boolean
  trackInventory?: boolean
  barcode?: string
  qrCode?: string
  sku?: string
  baseUnit: string
  subUnit?: string
  unitConversionRate?: number
  standardCost?: number
  sellingPrice?: number
  minSellingPrice?: number
  safetyStock?: number
  minStock?: number
  maxStock?: number
  reorderPoint?: number
  reorderQuantity?: number
  leadTimeDays?: number
  shelfLifeDays?: number
  width?: number
  height?: number
  depth?: number
  weight?: number
  color?: string
  size?: string
  brand?: string
  manufacturer?: string
  supplier?: string
  originCountry?: string
  hsCode?: string
  taxRate?: number
  imagePaths?: string
  attachmentPaths?: string
  tags?: string
  sortOrder?: number
  metadata?: string
}

/**
 * 재고 입고 요청 인터페이스
 */
export interface StockReceiptRequest {
  inventoryId: number
  quantity: number
  unitCost: number
  reason?: string
}

/**
 * 재고 출고 요청 인터페이스
 */
export interface StockIssueRequest {
  inventoryId: number
  quantity: number
  reason?: string
}

/**
 * 재고 실사 요청 인터페이스
 */
export interface StocktakingRequest {
  inventoryId: number
  actualQuantity: number
  reason?: string
  remarks?: string
  stocktakingDate?: string
}

/**
 * 재고 실사 결과 인터페이스
 */
export interface StocktakingResult {
  inventoryId: number
  productCode: string
  productName: string
  warehouseName: string
  locationCode: string
  systemStock: number
  actualStock: number
  differenceQuantity: number
  differenceValue: number
  reason?: string
  remarks?: string
  stocktakingDate: string
  processorName: string
}

/**
 * 재고 예약 요청 인터페이스
 */
export interface StockReservationRequest {
  inventoryId: number
  reservationQuantity: number
  reason?: string
  referenceNumber?: string
  reservationDate?: string
  expiryDate?: string
}

/**
 * 재고 이동 요청 인터페이스
 */
export interface InventoryTransferRequest {
  fromInventoryId: number
  toWarehouseId: number
  toLocationCode?: string
  transferQuantity: number
  reason?: string
  remarks?: string
  transferDate?: string
}

/**
 * 재고 통계 인터페이스
 */
export interface InventoryStats {
  totalInventoryItems: number
  lowStockItems: number
  outOfStockItems: number
  overStockItems: number
  reorderNeededItems: number
  expiringSoonItems: number
  expiredItems: number
  totalStockValue: number
  lowStockValue: number
  averageStockValue: number
  averageStockTurnover: number
}

/**
 * 상품 통계 인터페이스
 */
export interface ProductStats {
  totalProducts: number
  activeProducts: number
  inactiveProducts: number
  lowStockProducts: number
  outOfStockProducts: number
  overStockProducts: number
  reorderNeededProducts: number
  totalStockValue: number
  averageStockValue: number
}

/**
 * 창고별 재고 현황 인터페이스
 */
export interface WarehouseInventory {
  warehouseId: number
  warehouseName: string
  totalItems: number
  totalQuantity: number
  totalValue: number
  lowStockItems: number
  outOfStockItems: number
  utilizationRate: number
  topItems: Inventory[]
}

/**
 * ABC 분석 결과 인터페이스
 */
export interface AbcAnalysisResult {
  productId: number
  productCode: string
  productName: string
  annualUsageValue: number
  cumulativePercentage: number
  abcClass: string
  recommendation: string
}

/**
 * 검색 파라미터 인터페이스
 */
export interface InventorySearchParams {
  searchTerm?: string
  page?: number
  size?: number
  sort?: string
  categoryId?: number
  warehouseId?: number
  productType?: ProductType
  productStatus?: ProductStatus
  stockStatus?: StockStatus
  stockGrade?: StockGrade
  isLowStock?: boolean
  isOutOfStock?: boolean
  isOverStock?: boolean
  needsReorder?: boolean
  isExpiringSoon?: boolean
  isExpired?: boolean
  lotNumber?: string
  serialNumber?: string
  brand?: string
  manufacturer?: string
  supplier?: string
}

/**
 * 차트 데이터 인터페이스
 */
export interface ChartData {
  name: string
  value: number
  percentage?: number
  color?: string
}

/**
 * 재고 알림 인터페이스
 */
export interface InventoryAlert {
  id: number
  type: 'LOW_STOCK' | 'OUT_OF_STOCK' | 'OVER_STOCK' | 'EXPIRING_SOON' | 'EXPIRED'
  title: string
  message: string
  productId: number
  productCode: string
  productName: string
  warehouseId: number
  warehouseName: string
  currentStock: number
  threshold?: number
  expiryDate?: string
  severity: 'HIGH' | 'MEDIUM' | 'LOW'
  isRead: boolean
  createdAt: string
}

/**
 * 재고 대시보드 데이터 인터페이스
 */
export interface InventoryDashboard {
  totalProducts: number
  totalInventoryValue: number
  lowStockAlerts: number
  outOfStockAlerts: number
  expiringSoonAlerts: number
  recentMovements: StockMovement[]
  topValueProducts: Product[]
  warehouseUtilization: WarehouseInventory[]
  stockTrend: ChartData[]
  categoryDistribution: ChartData[]
  alerts: InventoryAlert[]
}

/**
 * 한국어 라벨 매핑
 */
export const KOREAN_LABELS = {
  // 상품 유형
  [ProductType.FINISHED_GOODS]: '완제품',
  [ProductType.RAW_MATERIAL]: '원재료',
  [ProductType.SEMI_FINISHED]: '반제품',
  [ProductType.CONSUMABLE]: '소모품',
  [ProductType.SERVICE]: '서비스',
  [ProductType.VIRTUAL]: '가상상품',
  [ProductType.BUNDLE]: '번들상품',
  [ProductType.DIGITAL]: '디지털상품',

  // 상품 상태
  [ProductStatus.ACTIVE]: '판매중',
  [ProductStatus.INACTIVE]: '판매중단',
  [ProductStatus.DISCONTINUED]: '단종',
  [ProductStatus.PENDING]: '승인대기',
  [ProductStatus.DRAFT]: '임시저장',

  // 재고 관리 방식
  [StockManagementType.FIFO]: '선입선출',
  [StockManagementType.LIFO]: '후입선출',
  [StockManagementType.AVERAGE]: '평균법',
  [StockManagementType.SPECIFIC]: '개별법',

  // 재고 상태
  [StockStatus.NORMAL]: '정상',
  [StockStatus.LOW_STOCK]: '안전재고 미달',
  [StockStatus.OUT_OF_STOCK]: '재고없음',
  [StockStatus.OVER_STOCK]: '과재고',
  [StockStatus.RESERVED]: '예약됨',
  [StockStatus.QUARANTINE]: '격리',
  [StockStatus.DAMAGED]: '불량',
  [StockStatus.EXPIRED]: '유효기간 만료',

  // 재고 등급
  [StockGrade.A]: '우수',
  [StockGrade.B]: '양호',
  [StockGrade.C]: '보통',
  [StockGrade.D]: '불량',
  [StockGrade.DEFECTIVE]: '결함',

  // 재고이동 유형
  [MovementType.RECEIPT]: '입고',
  [MovementType.PURCHASE_RECEIPT]: '매입입고',
  [MovementType.PRODUCTION_RECEIPT]: '생산입고',
  [MovementType.RETURN_RECEIPT]: '반품입고',
  [MovementType.TRANSFER_IN]: '이고입고',
  [MovementType.ADJUSTMENT_IN]: '조정입고',
  [MovementType.ISSUE]: '출고',
  [MovementType.SALES_ISSUE]: '매출출고',
  [MovementType.PRODUCTION_ISSUE]: '생산출고',
  [MovementType.RETURN_ISSUE]: '반품출고',
  [MovementType.TRANSFER_OUT]: '이고출고',
  [MovementType.ADJUSTMENT_OUT]: '조정출고',
  [MovementType.DISPOSAL]: '폐기',
  [MovementType.WAREHOUSE_TRANSFER]: '창고간이동',
  [MovementType.LOCATION_TRANSFER]: '위치이동',
  [MovementType.STOCKTAKING_INCREASE]: '실사증가',
  [MovementType.STOCKTAKING_DECREASE]: '실사감소',
  [MovementType.RESERVE]: '예약',
  [MovementType.UNRESERVE]: '예약해제',
  [MovementType.QUARANTINE]: '격리',
  [MovementType.UNQUARANTINE]: '격리해제',
  [MovementType.DEFECTIVE]: '불량처리',
  [MovementType.REPAIR]: '수리완료',

  // 이동 상태
  [MovementStatus.DRAFT]: '임시저장',
  [MovementStatus.PENDING]: '승인대기',
  [MovementStatus.APPROVED]: '승인완료',
  [MovementStatus.PROCESSED]: '처리완료',
  [MovementStatus.CANCELLED]: '취소됨',
  [MovementStatus.REJECTED]: '반려됨',

  // 창고 유형
  [WarehouseType.MAIN]: '본창고',
  [WarehouseType.SUB]: '보조창고',
  [WarehouseType.EXTERNAL]: '외부창고',
  [WarehouseType.VIRTUAL]: '가상창고',
  [WarehouseType.CONSIGNMENT]: '위탁창고',
  [WarehouseType.QUARANTINE]: '격리창고',
  [WarehouseType.RETURNED]: '반품창고',
  [WarehouseType.DAMAGED]: '불량창고',

  // 창고 상태
  [WarehouseStatus.ACTIVE]: '운영중',
  [WarehouseStatus.INACTIVE]: '중단',
  [WarehouseStatus.MAINTENANCE]: '점검중',
  [WarehouseStatus.CLOSED]: '폐쇄'
} as const




