/**
 * 영업관리 모듈 TypeScript 타입 정의
 * 백엔드 DTO와 일치하는 타입들을 정의합니다
 */

// ================================
// 고객 관리 타입
// ================================

export enum CustomerType {
  INDIVIDUAL = 'INDIVIDUAL',     // 개인
  CORPORATE = 'CORPORATE',       // 법인
  GOVERNMENT = 'GOVERNMENT',     // 공공기관
  NONPROFIT = 'NONPROFIT',       // 비영리단체
  PARTNER = 'PARTNER',           // 파트너사
  DISTRIBUTOR = 'DISTRIBUTOR',   // 유통업체
  RETAILER = 'RETAILER',         // 소매업체
  WHOLESALER = 'WHOLESALER'      // 도매업체
}

export enum CustomerStatus {
  PROSPECT = 'PROSPECT',         // 잠재고객
  ACTIVE = 'ACTIVE',             // 활성고객
  INACTIVE = 'INACTIVE',         // 비활성고객
  VIP = 'VIP',                   // VIP고객
  DORMANT = 'DORMANT',           // 휴면고객
  BLACKLIST = 'BLACKLIST'        // 블랙리스트
}

export enum CustomerGrade {
  PLATINUM = 'PLATINUM',         // 플래티넘
  GOLD = 'GOLD',                 // 골드
  SILVER = 'SILVER',             // 실버
  BRONZE = 'BRONZE',             // 브론즈
  GENERAL = 'GENERAL'            // 일반
}

export enum PaymentTerm {
  CASH = 'CASH',                 // 현금
  NET_7 = 'NET_7',               // 7일
  NET_15 = 'NET_15',             // 15일
  NET_30 = 'NET_30',             // 30일
  NET_45 = 'NET_45',             // 45일
  NET_60 = 'NET_60',             // 60일
  NET_90 = 'NET_90',             // 90일
  CUSTOM = 'CUSTOM'              // 사용자정의
}

export interface Customer {
  id: number
  customerCode: string
  customerName: string
  customerNameEn?: string
  companyId: number
  companyName?: string
  customerType: CustomerType
  customerTypeDescription: string
  customerStatus: CustomerStatus
  customerStatusDescription: string
  customerGrade: CustomerGrade
  customerGradeDescription: string
  isActive: boolean

  // 사업자 정보
  businessRegistrationNumber?: string
  representativeName?: string
  businessType?: string
  businessItem?: string

  // 연락처 정보
  phoneNumber?: string
  faxNumber?: string
  email?: string
  website?: string

  // 주소 정보
  postalCode?: string
  address?: string
  addressDetail?: string
  city?: string
  district?: string
  country?: string
  fullAddress?: string

  // 영업 관리 정보
  salesManagerId?: number
  salesManagerName?: string
  firstContactDate?: string
  lastContactDate?: string
  description?: string

  // 거래 조건
  paymentTerm?: PaymentTerm
  paymentTermDescription?: string
  customPaymentDays?: number
  creditLimit?: number
  discountRate?: number
  taxRate?: number

  // 통계 정보
  totalOrderCount?: number
  totalOrderAmount?: number
  lastOrderDate?: string
  averageOrderAmount?: number
  outstandingAmount?: number

  // 추가 정보
  tags?: string
  sortOrder?: number
  metadata?: string
  createdAt?: string
  updatedAt?: string

  // 계산 필드
  isVipCustomer?: boolean
  isActiveCustomer?: boolean
  isCreditLimitExceeded?: boolean
  customerSummary?: string
}

export interface CustomerSummary {
  id: number
  customerCode: string
  customerName: string
  customerType: CustomerType
  customerTypeDescription: string
  customerStatus: CustomerStatus
  customerStatusDescription: string
  customerGrade: CustomerGrade
  customerGradeDescription: string
  isActive: boolean
  phoneNumber?: string
  email?: string
  salesManagerName?: string
  totalOrderCount?: number
  totalOrderAmount?: number
  lastOrderDate?: string
  outstandingAmount?: number
  createdAt?: string
  isVipCustomer?: boolean
  isCreditLimitExceeded?: boolean
}

export interface CustomerCreateRequest {
  customerCode: string
  customerName: string
  customerNameEn?: string
  companyId: number
  customerType: CustomerType
  customerStatus?: CustomerStatus
  customerGrade?: CustomerGrade
  isActive?: boolean

  // 사업자 정보
  businessRegistrationNumber?: string
  representativeName?: string
  businessType?: string
  businessItem?: string

  // 연락처 정보
  phoneNumber?: string
  faxNumber?: string
  email?: string
  website?: string

  // 주소 정보
  postalCode?: string
  address?: string
  addressDetail?: string
  city?: string
  district?: string
  country?: string

  // 영업 관리 정보
  salesManagerId?: number
  salesManagerName?: string
  firstContactDate?: string
  description?: string

  // 거래 조건
  paymentTerm?: PaymentTerm
  customPaymentDays?: number
  creditLimit?: number
  discountRate?: number
  taxRate?: number

  // 추가 정보
  tags?: string
  sortOrder?: number
  metadata?: string
}

export interface CustomerUpdateRequest {
  customerName?: string
  customerNameEn?: string
  customerType?: CustomerType
  customerStatus?: CustomerStatus
  customerGrade?: CustomerGrade
  isActive?: boolean

  // 사업자 정보
  representativeName?: string
  businessType?: string
  businessItem?: string

  // 연락처 정보
  phoneNumber?: string
  faxNumber?: string
  email?: string
  website?: string

  // 주소 정보
  postalCode?: string
  address?: string
  addressDetail?: string
  city?: string
  district?: string
  country?: string

  // 영업 관리 정보
  salesManagerId?: number
  salesManagerName?: string
  lastContactDate?: string
  description?: string

  // 거래 조건
  paymentTerm?: PaymentTerm
  customPaymentDays?: number
  creditLimit?: number
  discountRate?: number
  taxRate?: number

  // 추가 정보
  tags?: string
  sortOrder?: number
  metadata?: string
}

export interface CustomerSearchParams {
  searchTerm?: string
  customerType?: CustomerType
  customerStatus?: CustomerStatus
  customerGrade?: CustomerGrade
  isActive?: boolean
  salesManagerId?: number
  city?: string
  businessType?: string
  contactDateFrom?: string
  contactDateTo?: string
  orderDateFrom?: string
  orderDateTo?: string
  orderAmountFrom?: number
  orderAmountTo?: number
  hasOutstanding?: boolean
  isCreditLimitExceeded?: boolean
  tags?: string
}

export interface CustomerStats {
  totalCustomers: number
  activeCustomers: number
  inactiveCustomers: number
  vipCustomers: number
  prospectCustomers: number
  dormantCustomers: number
  totalSalesAmount: number
  averageSalesAmount: number
  totalOutstandingAmount: number
  customersWithOutstanding: number
  customersOverCreditLimit: number
  averageOrdersPerCustomer: number
  averageOrderAmount: number
}

// ================================
// 주문 관리 타입
// ================================

export enum OrderStatus {
  DRAFT = 'DRAFT',               // 임시저장
  PENDING = 'PENDING',           // 대기중
  CONFIRMED = 'CONFIRMED',       // 확정됨
  PROCESSING = 'PROCESSING',     // 처리중
  SHIPPED = 'SHIPPED',           // 배송중
  DELIVERED = 'DELIVERED',       // 배송완료
  COMPLETED = 'COMPLETED',       // 완료
  CANCELLED = 'CANCELLED',       // 취소됨
  REFUNDED = 'REFUNDED',         // 환불됨
  RETURNED = 'RETURNED'          // 반품됨
}

export enum OrderType {
  NORMAL = 'NORMAL',             // 일반주문
  RUSH = 'RUSH',                 // 긴급주문
  BACKORDER = 'BACKORDER',       // 백오더
  PREORDER = 'PREORDER',         // 선주문
  SUBSCRIPTION = 'SUBSCRIPTION', // 정기주문
  SAMPLE = 'SAMPLE',             // 샘플주문
  RETURN = 'RETURN',             // 반품주문
  EXCHANGE = 'EXCHANGE'          // 교환주문
}

export enum PaymentStatus {
  PENDING = 'PENDING',           // 결제대기
  PARTIAL = 'PARTIAL',           // 부분결제
  PAID = 'PAID',                 // 결제완료
  OVERDUE = 'OVERDUE',           // 연체
  CANCELLED = 'CANCELLED',       // 결제취소
  REFUNDED = 'REFUNDED'          // 환불완료
}

export enum DeliveryStatus {
  PENDING = 'PENDING',           // 배송대기
  PREPARING = 'PREPARING',       // 준비중
  SHIPPED = 'SHIPPED',           // 배송중
  DELIVERED = 'DELIVERED',       // 배송완료
  CANCELLED = 'CANCELLED'        // 취소됨
}

export interface OrderItem {
  id: number
  orderId: number
  productId?: number
  lineNumber: number
  productCode: string
  productName: string
  productDescription?: string
  quantity: number
  shippedQuantity?: number
  deliveredQuantity?: number
  cancelledQuantity?: number
  unit: string
  unitPrice: number
  discountRate?: number
  discountAmount?: number
  totalPrice: number
  deliveryStatus?: DeliveryStatus
  deliveryStatusDescription?: string
  remarks?: string
  sortOrder?: number

  // 계산 필드
  pendingQuantity?: number
  hasPendingQuantity?: boolean
  isPartiallyShipped?: boolean
  isFullyShipped?: boolean
  isFullyDelivered?: boolean
  hasDiscount?: boolean
  discountedUnitPrice?: number
  lineSummary?: string
  deliveryProgress?: number
}

export interface Order {
  id: number
  orderNumber: string
  companyId: number
  companyName?: string
  customerId: number
  customerCode: string
  customerName: string
  quoteId?: number
  quoteNumber?: string
  orderDate: string
  requiredDate?: string
  promisedDate?: string
  deliveryDate?: string
  orderStatus: OrderStatus
  orderStatusDescription: string
  orderType: OrderType
  orderTypeDescription: string
  paymentStatus: PaymentStatus
  paymentStatusDescription: string
  title?: string
  description?: string

  // 영업 담당자 정보
  salesRepId?: number
  salesRepName?: string

  // 금액 정보
  subtotal: number
  discountAmount?: number
  discountRate?: number
  taxAmount?: number
  taxRate?: number
  totalAmount: number
  paidAmount?: number
  outstandingAmount?: number

  // 배송 정보
  deliveryName?: string
  deliveryPhone?: string
  deliveryPostalCode?: string
  deliveryAddress?: string
  deliveryAddressDetail?: string
  fullDeliveryAddress?: string
  deliveryMethod?: string
  deliveryFee?: number
  deliveryMemo?: string
  courierCompany?: string
  trackingNumber?: string

  // 결제 정보
  paymentTerms?: string
  paymentMethod?: string
  paymentDueDate?: string
  paymentCompletedDate?: string

  // 상태 관리
  confirmedDate?: string
  shippedDate?: string
  deliveredDate?: string
  completedDate?: string
  cancelledDate?: string
  cancellationReason?: string

  // 특별 조건
  specialInstructions?: string
  remarks?: string

  // 추가 정보
  tags?: string
  sortOrder?: number
  metadata?: string
  createdAt?: string
  updatedAt?: string

  // 주문 항목들
  orderItems: OrderItem[]

  // 계산 필드
  isEditable?: boolean
  isConfirmable?: boolean
  isCancellable?: boolean
  isShippable?: boolean
  orderSummary?: string
  totalItemCount?: number
  daysUntilDelivery?: number
}

export interface OrderSummary {
  id: number
  orderNumber: string
  customerId: number
  customerCode: string
  customerName: string
  orderDate: string
  requiredDate?: string
  deliveryDate?: string
  orderStatus: OrderStatus
  orderStatusDescription: string
  orderType: OrderType
  orderTypeDescription: string
  paymentStatus: PaymentStatus
  paymentStatusDescription: string
  totalAmount: number
  paidAmount?: number
  outstandingAmount?: number
  salesRepName?: string
  totalItemCount?: number
  createdAt?: string
  isEditable?: boolean
  isCancellable?: boolean
  daysUntilDelivery?: number
}

export interface OrderCreateRequest {
  companyId: number
  customerId: number
  quoteId?: number
  orderDate: string
  requiredDate?: string
  promisedDate?: string
  orderType?: OrderType
  paymentStatus?: PaymentStatus
  title?: string
  description?: string

  // 영업 담당자 정보
  salesRepId?: number
  salesRepName?: string

  // 할인 정보
  discountRate?: number
  discountAmount?: number
  taxRate?: number

  // 배송 정보
  deliveryName?: string
  deliveryPhone?: string
  deliveryPostalCode?: string
  deliveryAddress?: string
  deliveryAddressDetail?: string
  deliveryMethod?: string
  deliveryFee?: number
  deliveryMemo?: string

  // 결제 정보
  paymentTerms?: string
  paymentMethod?: string
  paymentDueDate?: string

  // 특별 조건
  specialInstructions?: string
  remarks?: string

  // 주문 항목들
  orderItems: OrderItemCreateRequest[]

  // 추가 정보
  tags?: string
  sortOrder?: number
  metadata?: string
}

export interface OrderItemCreateRequest {
  productId?: number
  productCode: string
  productName: string
  productDescription?: string
  quantity: number
  unit: string
  unitPrice: number
  discountRate?: number
  discountAmount?: number
  remarks?: string
  sortOrder?: number
}

export interface OrderSearchParams {
  searchTerm?: string
  customerId?: number
  orderStatus?: OrderStatus
  orderType?: OrderType
  paymentStatus?: PaymentStatus
  salesRepId?: number
  orderDateFrom?: string
  orderDateTo?: string
  requiredDateFrom?: string
  requiredDateTo?: string
  deliveryDateFrom?: string
  deliveryDateTo?: string
  totalAmountFrom?: number
  totalAmountTo?: number
  hasOutstanding?: boolean
  deliveryMethod?: string
  tags?: string
}

export interface OrderStats {
  totalOrders: number
  pendingOrders: number
  confirmedOrders: number
  shippedOrders: number
  deliveredOrders: number
  completedOrders: number
  cancelledOrders: number
  totalOrderAmount: number
  averageOrderAmount: number
  totalPaidAmount: number
  totalOutstandingAmount: number
  averageItemsPerOrder: number
  overDueOrders: number
  urgentOrders: number
}

// ================================
// 견적서 관리 타입
// ================================

export enum QuoteStatus {
  DRAFT = 'DRAFT',               // 임시저장
  SENT = 'SENT',                 // 발송됨
  VIEWED = 'VIEWED',             // 확인됨
  ACCEPTED = 'ACCEPTED',         // 승인됨
  REJECTED = 'REJECTED',         // 거부됨
  EXPIRED = 'EXPIRED',           // 만료됨
  CONVERTED = 'CONVERTED',       // 주문전환
  CANCELLED = 'CANCELLED'        // 취소됨
}

export enum QuotePriority {
  LOW = 'LOW',                   // 낮음
  NORMAL = 'NORMAL',             // 보통
  HIGH = 'HIGH',                 // 높음
  URGENT = 'URGENT'              // 긴급
}

export interface Quote {
  id: number
  quoteNumber: string
  companyId: number
  companyName?: string
  customerId: number
  customerCode: string
  customerName: string
  quoteDate: string
  validUntil: string
  quoteStatus: QuoteStatus
  quoteStatusDescription: string
  priority: QuotePriority
  priorityDescription: string
  title?: string
  description?: string

  // 영업 담당자 정보
  salesRepId?: number
  salesRepName?: string
  salesRepPhone?: string
  salesRepEmail?: string

  // 금액 정보
  subtotal: number
  discountAmount?: number
  discountRate?: number
  taxAmount?: number
  taxRate?: number
  totalAmount: number

  // 배송 정보
  deliveryAddress?: string
  deliveryDate?: string
  deliveryMethod?: string
  deliveryFee?: number

  // 결제 정보
  paymentTerms?: string
  paymentMethod?: string

  // 특별 조건
  specialTerms?: string
  remarks?: string

  // 상태 관리
  sentDate?: string
  viewedDate?: string
  respondedDate?: string
  convertedDate?: string
  rejectionReason?: string

  // 추가 정보
  tags?: string
  sortOrder?: number
  metadata?: string
  createdAt?: string
  updatedAt?: string

  // 견적 항목들
  quoteItems: QuoteItem[]

  // 계산 필드
  isExpired?: boolean
  isEditable?: boolean
  isSendable?: boolean
  isConvertible?: boolean
  quoteSummary?: string
  totalItemCount?: number
  daysUntilExpiry?: number
}

export interface QuoteItem {
  id: number
  quoteId: number
  productId?: number
  lineNumber: number
  productCode: string
  productName: string
  productDescription?: string
  quantity: number
  unit: string
  unitPrice: number
  discountRate?: number
  discountAmount?: number
  totalPrice: number
  remarks?: string
  sortOrder?: number

  // 계산 필드
  hasDiscount?: boolean
  discountedUnitPrice?: number
  lineSummary?: string
}

// ================================
// 계약 관리 타입
// ================================

export enum ContractStatus {
  DRAFT = 'DRAFT',               // 임시저장
  PENDING = 'PENDING',           // 검토중
  APPROVED = 'APPROVED',         // 승인됨
  ACTIVE = 'ACTIVE',             // 활성
  SUSPENDED = 'SUSPENDED',       // 중단
  COMPLETED = 'COMPLETED',       // 완료
  TERMINATED = 'TERMINATED',     // 해지
  EXPIRED = 'EXPIRED',           // 만료
  CANCELLED = 'CANCELLED'        // 취소
}

export enum ContractType {
  SALES = 'SALES',               // 판매계약
  SERVICE = 'SERVICE',           // 서비스계약
  MAINTENANCE = 'MAINTENANCE',   // 유지보수계약
  SUPPLY = 'SUPPLY',             // 공급계약
  DISTRIBUTION = 'DISTRIBUTION', // 유통계약
  LICENSE = 'LICENSE',           // 라이선스계약
  PARTNERSHIP = 'PARTNERSHIP',   // 파트너십계약
  SUBSCRIPTION = 'SUBSCRIPTION', // 구독계약
  LEASE = 'LEASE',               // 임대계약
  FRAMEWORK = 'FRAMEWORK'        // 기본계약
}

export enum RenewalType {
  MANUAL = 'MANUAL',             // 수동갱신
  AUTOMATIC = 'AUTOMATIC',       // 자동갱신
  NONE = 'NONE'                  // 갱신없음
}

export interface Contract {
  id: number
  contractNumber: string
  companyId: number
  companyName?: string
  customerId: number
  customerCode: string
  customerName: string
  orderId?: number
  orderNumber?: string
  title: string
  description?: string
  contractStatus: ContractStatus
  contractStatusDescription: string
  contractType: ContractType
  contractTypeDescription: string
  startDate: string
  endDate: string
  signedDate?: string
  effectiveDate?: string

  // 담당자 정보
  ourRepresentativeId?: number
  ourRepresentativeName?: string
  ourRepresentativeDepartment?: string
  customerRepresentativeName?: string
  customerRepresentativeDepartment?: string
  customerRepresentativePhone?: string
  customerRepresentativeEmail?: string

  // 계약 금액 정보
  contractAmount: number
  taxAmount?: number
  taxRate?: number
  totalAmount: number

  // 결제 조건
  paymentTerms?: string
  paymentMethod?: string
  paymentCycleDays?: number

  // 갱신 조건
  renewalType?: RenewalType
  renewalTypeDescription?: string
  renewalPeriodMonths?: number
  renewalNoticeDays?: number
  autoRenewalEnabled?: boolean

  // 계약 조건
  termsAndConditions?: string
  specialClauses?: string
  deliveryTerms?: string
  warrantyTerms?: string
  liabilityTerms?: string
  terminationTerms?: string

  // 상태 관리
  approvedDate?: string
  activatedDate?: string
  suspendedDate?: string
  completedDate?: string
  terminatedDate?: string
  terminationReason?: string

  // 첨부 파일
  attachmentPaths?: string

  // 추가 정보
  remarks?: string
  tags?: string
  sortOrder?: number
  metadata?: string
  createdAt?: string
  updatedAt?: string

  // 계산 필드
  contractDurationDays?: number
  daysUntilExpiry?: number
  isExpired?: boolean
  isActive?: boolean
  isExpiringSoon?: boolean
  needsRenewalNotice?: boolean
  contractSummary?: string
  progress?: number
}

// ================================
// 공통 타입
// ================================

export interface ApiResponse<T = any> {
  success: boolean
  message: string
  data?: T
  error?: string
  timestamp: string
}

export interface PaginationParams {
  page?: number
  size?: number
  sort?: string[]
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
  empty: boolean
}

// ================================
// 한글 라벨 매핑
// ================================

export const CustomerTypeLabels: Record<CustomerType, string> = {
  [CustomerType.INDIVIDUAL]: '개인',
  [CustomerType.CORPORATE]: '법인',
  [CustomerType.GOVERNMENT]: '공공기관',
  [CustomerType.NONPROFIT]: '비영리단체',
  [CustomerType.PARTNER]: '파트너사',
  [CustomerType.DISTRIBUTOR]: '유통업체',
  [CustomerType.RETAILER]: '소매업체',
  [CustomerType.WHOLESALER]: '도매업체'
}

export const CustomerStatusLabels: Record<CustomerStatus, string> = {
  [CustomerStatus.PROSPECT]: '잠재고객',
  [CustomerStatus.ACTIVE]: '활성고객',
  [CustomerStatus.INACTIVE]: '비활성고객',
  [CustomerStatus.VIP]: 'VIP고객',
  [CustomerStatus.DORMANT]: '휴면고객',
  [CustomerStatus.BLACKLIST]: '블랙리스트'
}

export const CustomerGradeLabels: Record<CustomerGrade, string> = {
  [CustomerGrade.PLATINUM]: '플래티넘',
  [CustomerGrade.GOLD]: '골드',
  [CustomerGrade.SILVER]: '실버',
  [CustomerGrade.BRONZE]: '브론즈',
  [CustomerGrade.GENERAL]: '일반'
}

export const OrderStatusLabels: Record<OrderStatus, string> = {
  [OrderStatus.DRAFT]: '임시저장',
  [OrderStatus.PENDING]: '대기중',
  [OrderStatus.CONFIRMED]: '확정됨',
  [OrderStatus.PROCESSING]: '처리중',
  [OrderStatus.SHIPPED]: '배송중',
  [OrderStatus.DELIVERED]: '배송완료',
  [OrderStatus.COMPLETED]: '완료',
  [OrderStatus.CANCELLED]: '취소됨',
  [OrderStatus.REFUNDED]: '환불됨',
  [OrderStatus.RETURNED]: '반품됨'
}

export const OrderTypeLabels: Record<OrderType, string> = {
  [OrderType.NORMAL]: '일반주문',
  [OrderType.RUSH]: '긴급주문',
  [OrderType.BACKORDER]: '백오더',
  [OrderType.PREORDER]: '선주문',
  [OrderType.SUBSCRIPTION]: '정기주문',
  [OrderType.SAMPLE]: '샘플주문',
  [OrderType.RETURN]: '반품주문',
  [OrderType.EXCHANGE]: '교환주문'
}

export const PaymentStatusLabels: Record<PaymentStatus, string> = {
  [PaymentStatus.PENDING]: '결제대기',
  [PaymentStatus.PARTIAL]: '부분결제',
  [PaymentStatus.PAID]: '결제완료',
  [PaymentStatus.OVERDUE]: '연체',
  [PaymentStatus.CANCELLED]: '결제취소',
  [PaymentStatus.REFUNDED]: '환불완료'
}

export const QuoteStatusLabels: Record<QuoteStatus, string> = {
  [QuoteStatus.DRAFT]: '임시저장',
  [QuoteStatus.SENT]: '발송됨',
  [QuoteStatus.VIEWED]: '확인됨',
  [QuoteStatus.ACCEPTED]: '승인됨',
  [QuoteStatus.REJECTED]: '거부됨',
  [QuoteStatus.EXPIRED]: '만료됨',
  [QuoteStatus.CONVERTED]: '주문전환',
  [QuoteStatus.CANCELLED]: '취소됨'
}

export const ContractStatusLabels: Record<ContractStatus, string> = {
  [ContractStatus.DRAFT]: '임시저장',
  [ContractStatus.PENDING]: '검토중',
  [ContractStatus.APPROVED]: '승인됨',
  [ContractStatus.ACTIVE]: '활성',
  [ContractStatus.SUSPENDED]: '중단',
  [ContractStatus.COMPLETED]: '완료',
  [ContractStatus.TERMINATED]: '해지',
  [ContractStatus.EXPIRED]: '만료',
  [ContractStatus.CANCELLED]: '취소'
}




