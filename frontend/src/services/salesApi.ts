/**
 * 영업관리 API 서비스
 * 백엔드 API와 통신하는 서비스 레이어입니다
 */

import api from './api'
import type {
  ApiResponse,
  PageResponse,
  PaginationParams,
  Customer,
  CustomerSummary,
  CustomerCreateRequest,
  CustomerUpdateRequest,
  CustomerSearchParams,
  CustomerStats,
  Order,
  OrderSummary,
  OrderCreateRequest,
  OrderSearchParams,
  OrderStats,
  Quote,
  Contract
} from '@/types/sales'

// API 기본 설정
const api = api.create({
  baseURL: '/api/sales',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 요청 인터셉터 - 인증 토큰 추가
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 응답 인터셉터 - 에러 처리
api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Error:', error)
    if (error.response?.status === 401) {
      // 인증 실패시 로그인 페이지로 리다이렉트
      localStorage.removeItem('accessToken')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

// ================================
// 고객 관리 API
// ================================

export const customerApi = {
  /**
   * 고객 생성
   */
  create: async (data: CustomerCreateRequest): Promise<Customer> => {
    const response: AxiosResponse<ApiResponse<Customer>> = await api.post('/customers', data)
    return response.data.data!
  },

  /**
   * 고객 수정
   */
  update: async (customerId: number, data: CustomerUpdateRequest): Promise<Customer> => {
    const response: AxiosResponse<ApiResponse<Customer>> = await api.put(`/customers/${customerId}`, data)
    return response.data.data!
  },

  /**
   * 고객 삭제
   */
  delete: async (customerId: number): Promise<void> => {
    await api.delete(`/customers/${customerId}`)
  },

  /**
   * 고객 상세 조회
   */
  getById: async (customerId: number): Promise<Customer> => {
    const response: AxiosResponse<ApiResponse<Customer>> = await api.get(`/customers/${customerId}`)
    return response.data.data!
  },

  /**
   * 고객코드로 조회
   */
  getByCode: async (customerCode: string): Promise<Customer> => {
    const response: AxiosResponse<ApiResponse<Customer>> = await api.get(`/customers/code/${customerCode}`)
    return response.data.data!
  },

  /**
   * 회사별 고객 목록 조회
   */
  getByCompany: async (
    companyId: number, 
    params?: PaginationParams
  ): Promise<PageResponse<CustomerSummary>> => {
    const response: AxiosResponse<ApiResponse<PageResponse<CustomerSummary>>> = await api.get(
      `/customers/company/${companyId}`,
      { params }
    )
    return response.data.data!
  },

  /**
   * 고객 검색
   */
  search: async (
    companyId: number,
    searchTerm: string,
    params?: PaginationParams
  ): Promise<PageResponse<CustomerSummary>> => {
    const response: AxiosResponse<ApiResponse<PageResponse<CustomerSummary>>> = await api.get(
      `/customers/company/${companyId}/search`,
      { params: { searchTerm, ...params } }
    )
    return response.data.data!
  },

  /**
   * 고객 고급 검색
   */
  searchAdvanced: async (
    companyId: number,
    searchParams: CustomerSearchParams,
    params?: PaginationParams
  ): Promise<PageResponse<CustomerSummary>> => {
    const response: AxiosResponse<ApiResponse<PageResponse<CustomerSummary>>> = await api.post(
      `/customers/company/${companyId}/search/advanced?${new URLSearchParams(params as any).toString()}`,
      searchParams
    )
    return response.data.data!
  },

  /**
   * 영업담당자별 고객 조회
   */
  getBySalesManager: async (
    salesManagerId: number,
    params?: PaginationParams
  ): Promise<PageResponse<CustomerSummary>> => {
    const response: AxiosResponse<ApiResponse<PageResponse<CustomerSummary>>> = await api.get(
      `/customers/sales-manager/${salesManagerId}`,
      { params }
    )
    return response.data.data!
  },

  /**
   * 고객 유형별 조회
   */
  getByType: async (
    companyId: number,
    customerType: string,
    params?: PaginationParams
  ): Promise<PageResponse<CustomerSummary>> => {
    const response: AxiosResponse<ApiResponse<PageResponse<CustomerSummary>>> = await api.get(
      `/customers/company/${companyId}/type/${customerType}`,
      { params }
    )
    return response.data.data!
  },

  /**
   * 고객 상태별 조회
   */
  getByStatus: async (
    companyId: number,
    customerStatus: string,
    params?: PaginationParams
  ): Promise<PageResponse<CustomerSummary>> => {
    const response: AxiosResponse<ApiResponse<PageResponse<CustomerSummary>>> = await api.get(
      `/customers/company/${companyId}/status/${customerStatus}`,
      { params }
    )
    return response.data.data!
  },

  /**
   * 고객 등급별 조회
   */
  getByGrade: async (
    companyId: number,
    customerGrade: string,
    params?: PaginationParams
  ): Promise<PageResponse<CustomerSummary>> => {
    const response: AxiosResponse<ApiResponse<PageResponse<CustomerSummary>>> = await api.get(
      `/customers/company/${companyId}/grade/${customerGrade}`,
      { params }
    )
    return response.data.data!
  },

  /**
   * VIP 고객 조회
   */
  getVipCustomers: async (companyId: number): Promise<CustomerSummary[]> => {
    const response: AxiosResponse<ApiResponse<CustomerSummary[]>> = await api.get(
      `/customers/company/${companyId}/vip`
    )
    return response.data.data!
  },

  /**
   * 휴면 고객 조회
   */
  getDormantCustomers: async (companyId: number, dormantDays: number = 90): Promise<CustomerSummary[]> => {
    const response: AxiosResponse<ApiResponse<CustomerSummary[]>> = await api.get(
      `/customers/company/${companyId}/dormant`,
      { params: { dormantDays } }
    )
    return response.data.data!
  },

  /**
   * 미수금 고객 조회
   */
  getCustomersWithOutstanding: async (
    companyId: number,
    params?: PaginationParams
  ): Promise<PageResponse<CustomerSummary>> => {
    const response: AxiosResponse<ApiResponse<PageResponse<CustomerSummary>>> = await api.get(
      `/customers/company/${companyId}/outstanding`,
      { params }
    )
    return response.data.data!
  },

  /**
   * 신용한도 초과 고객 조회
   */
  getCustomersOverCreditLimit: async (
    companyId: number,
    params?: PaginationParams
  ): Promise<PageResponse<CustomerSummary>> => {
    const response: AxiosResponse<ApiResponse<PageResponse<CustomerSummary>>> = await api.get(
      `/customers/company/${companyId}/over-credit-limit`,
      { params }
    )
    return response.data.data!
  },

  /**
   * 상위 고객 조회
   */
  getTopCustomers: async (
    companyId: number,
    params?: PaginationParams
  ): Promise<PageResponse<CustomerSummary>> => {
    const response: AxiosResponse<ApiResponse<PageResponse<CustomerSummary>>> = await api.get(
      `/customers/company/${companyId}/top`,
      { params }
    )
    return response.data.data!
  },

  /**
   * 고객 통계 조회
   */
  getStatistics: async (companyId: number): Promise<CustomerStats> => {
    const response: AxiosResponse<ApiResponse<CustomerStats>> = await api.get(
      `/customers/company/${companyId}/statistics`
    )
    return response.data.data!
  },

  /**
   * 고객 연락처 업데이트
   */
  updateContact: async (customerId: number, data: {
    phoneNumber?: string
    faxNumber?: string
    email?: string
    website?: string
    lastContactDate?: string
  }): Promise<Customer> => {
    const response: AxiosResponse<ApiResponse<Customer>> = await api.put(
      `/customers/${customerId}/contact`,
      data
    )
    return response.data.data!
  },

  /**
   * 고객 주소 업데이트
   */
  updateAddress: async (customerId: number, data: {
    postalCode?: string
    address?: string
    addressDetail?: string
    city?: string
    district?: string
    country?: string
  }): Promise<Customer> => {
    const response: AxiosResponse<ApiResponse<Customer>> = await api.put(
      `/customers/${customerId}/address`,
      data
    )
    return response.data.data!
  },

  /**
   * 고객 거래조건 업데이트
   */
  updateTerms: async (customerId: number, data: {
    paymentTerm?: string
    customPaymentDays?: number
    creditLimit?: number
    discountRate?: number
    taxRate?: number
  }): Promise<Customer> => {
    const response: AxiosResponse<ApiResponse<Customer>> = await api.put(
      `/customers/${customerId}/terms`,
      data
    )
    return response.data.data!
  },

  /**
   * 고객 등급 변경
   */
  changeGrade: async (customerId: number, data: {
    customerGrade: string
    reason?: string
  }): Promise<Customer> => {
    const response: AxiosResponse<ApiResponse<Customer>> = await api.put(
      `/customers/${customerId}/grade`,
      data
    )
    return response.data.data!
  },

  /**
   * 고객 상태 변경
   */
  changeStatus: async (customerId: number, data: {
    customerStatus: string
    reason?: string
  }): Promise<Customer> => {
    const response: AxiosResponse<ApiResponse<Customer>> = await api.put(
      `/customers/${customerId}/status`,
      data
    )
    return response.data.data!
  },

  /**
   * 고객 활성화 토글
   */
  toggleActive: async (customerId: number): Promise<Customer> => {
    const response: AxiosResponse<ApiResponse<Customer>> = await api.put(
      `/customers/${customerId}/toggle-active`
    )
    return response.data.data!
  },

  /**
   * 고객코드 중복 확인
   */
  checkCodeDuplicate: async (
    companyId: number,
    customerCode: string,
    excludeCustomerId?: number
  ): Promise<boolean> => {
    const response: AxiosResponse<ApiResponse<boolean>> = await api.get(
      '/customers/check-duplicate/code',
      { params: { companyId, customerCode, excludeCustomerId } }
    )
    return response.data.data!
  },

  /**
   * 고객 주문 통계 일괄 업데이트
   */
  updateAllOrderStatistics: async (companyId: number): Promise<void> => {
    await api.post(`/customers/company/${companyId}/update-order-statistics`)
  },

  /**
   * 고객 등급 자동 업데이트
   */
  updateGradesBasedOnOrderAmount: async (companyId: number): Promise<void> => {
    await api.post(`/customers/company/${companyId}/update-grades`)
  },

  /**
   * 휴면 고객 자동 전환
   */
  convertDormantCustomers: async (companyId: number, dormantDays: number = 90): Promise<number> => {
    const response: AxiosResponse<ApiResponse<number>> = await api.post(
      `/customers/company/${companyId}/convert-dormant`,
      null,
      { params: { dormantDays } }
    )
    return response.data.data!
  },

  /**
   * 고객 라이프타임 가치 계산
   */
  calculateLifetimeValue: async (customerId: number): Promise<number> => {
    const response: AxiosResponse<ApiResponse<number>> = await api.get(
      `/customers/${customerId}/lifetime-value`
    )
    return response.data.data!
  },

  /**
   * 이탈 위험 고객 분석
   */
  getChurnRiskCustomers: async (companyId: number): Promise<CustomerSummary[]> => {
    const response: AxiosResponse<ApiResponse<CustomerSummary[]>> = await api.get(
      `/customers/company/${companyId}/churn-risk`
    )
    return response.data.data!
  }
}

// ================================
// 주문 관리 API
// ================================

export const orderApi = {
  /**
   * 주문 생성
   */
  create: async (data: OrderCreateRequest): Promise<Order> => {
    const response: AxiosResponse<ApiResponse<Order>> = await api.post('/orders', data)
    return response.data.data!
  },

  /**
   * 견적서에서 주문 생성
   */
  createFromQuote: async (data: {
    quoteId: number
    orderDate: string
    requiredDate?: string
    deliveryAddress?: string
    deliveryMemo?: string
    paymentTerms?: string
    specialInstructions?: string
    remarks?: string
  }): Promise<Order> => {
    const response: AxiosResponse<ApiResponse<Order>> = await api.post('/orders/from-quote', data)
    return response.data.data!
  },

  /**
   * 주문 수정
   */
  update: async (orderId: number, data: Partial<OrderCreateRequest>): Promise<Order> => {
    const response: AxiosResponse<ApiResponse<Order>> = await api.put(`/orders/${orderId}`, data)
    return response.data.data!
  },

  /**
   * 주문 삭제
   */
  delete: async (orderId: number): Promise<void> => {
    await api.delete(`/orders/${orderId}`)
  },

  /**
   * 주문 상세 조회
   */
  getById: async (orderId: number): Promise<Order> => {
    const response: AxiosResponse<ApiResponse<Order>> = await api.get(`/orders/${orderId}`)
    return response.data.data!
  },

  /**
   * 주문번호로 조회
   */
  getByNumber: async (orderNumber: string): Promise<Order> => {
    const response: AxiosResponse<ApiResponse<Order>> = await api.get(`/orders/number/${orderNumber}`)
    return response.data.data!
  },

  /**
   * 회사별 주문 목록 조회
   */
  getByCompany: async (
    companyId: number,
    params?: PaginationParams
  ): Promise<PageResponse<OrderSummary>> => {
    const response: AxiosResponse<ApiResponse<PageResponse<OrderSummary>>> = await api.get(
      `/orders/company/${companyId}`,
      { params }
    )
    return response.data.data!
  },

  /**
   * 고객별 주문 조회
   */
  getByCustomer: async (
    customerId: number,
    params?: PaginationParams
  ): Promise<PageResponse<OrderSummary>> => {
    const response: AxiosResponse<ApiResponse<PageResponse<OrderSummary>>> = await api.get(
      `/orders/customer/${customerId}`,
      { params }
    )
    return response.data.data!
  },

  /**
   * 영업담당자별 주문 조회
   */
  getBySalesRep: async (
    salesRepId: number,
    params?: PaginationParams
  ): Promise<PageResponse<OrderSummary>> => {
    const response: AxiosResponse<ApiResponse<PageResponse<OrderSummary>>> = await api.get(
      `/orders/sales-rep/${salesRepId}`,
      { params }
    )
    return response.data.data!
  },

  /**
   * 주문 검색
   */
  search: async (
    companyId: number,
    searchTerm: string,
    params?: PaginationParams
  ): Promise<PageResponse<OrderSummary>> => {
    const response: AxiosResponse<ApiResponse<PageResponse<OrderSummary>>> = await api.get(
      `/orders/company/${companyId}/search`,
      { params: { searchTerm, ...params } }
    )
    return response.data.data!
  },

  /**
   * 주문 고급 검색
   */
  searchAdvanced: async (
    companyId: number,
    searchParams: OrderSearchParams,
    params?: PaginationParams
  ): Promise<PageResponse<OrderSummary>> => {
    const response: AxiosResponse<ApiResponse<PageResponse<OrderSummary>>> = await api.post(
      `/orders/company/${companyId}/search/advanced?${new URLSearchParams(params as any).toString()}`,
      searchParams
    )
    return response.data.data!
  },

  /**
   * 주문 상태별 조회
   */
  getByStatus: async (
    companyId: number,
    orderStatus: string,
    params?: PaginationParams
  ): Promise<PageResponse<OrderSummary>> => {
    const response: AxiosResponse<ApiResponse<PageResponse<OrderSummary>>> = await api.get(
      `/orders/company/${companyId}/status/${orderStatus}`,
      { params }
    )
    return response.data.data!
  },

  /**
   * 주문 유형별 조회
   */
  getByType: async (
    companyId: number,
    orderType: string,
    params?: PaginationParams
  ): Promise<PageResponse<OrderSummary>> => {
    const response: AxiosResponse<ApiResponse<PageResponse<OrderSummary>>> = await api.get(
      `/orders/company/${companyId}/type/${orderType}`,
      { params }
    )
    return response.data.data!
  },

  /**
   * 결제 상태별 조회
   */
  getByPaymentStatus: async (
    companyId: number,
    paymentStatus: string,
    params?: PaginationParams
  ): Promise<PageResponse<OrderSummary>> => {
    const response: AxiosResponse<ApiResponse<PageResponse<OrderSummary>>> = await api.get(
      `/orders/company/${companyId}/payment-status/${paymentStatus}`,
      { params }
    )
    return response.data.data!
  },

  /**
   * 미수금 주문 조회
   */
  getOrdersWithOutstanding: async (
    companyId: number,
    params?: PaginationParams
  ): Promise<PageResponse<OrderSummary>> => {
    const response: AxiosResponse<ApiResponse<PageResponse<OrderSummary>>> = await api.get(
      `/orders/company/${companyId}/outstanding`,
      { params }
    )
    return response.data.data!
  },

  /**
   * 연체 주문 조회
   */
  getOverdueOrders: async (
    companyId: number,
    params?: PaginationParams
  ): Promise<PageResponse<OrderSummary>> => {
    const response: AxiosResponse<ApiResponse<PageResponse<OrderSummary>>> = await api.get(
      `/orders/company/${companyId}/overdue`,
      { params }
    )
    return response.data.data!
  },

  /**
   * 긴급 주문 조회
   */
  getUrgentOrders: async (companyId: number): Promise<OrderSummary[]> => {
    const response: AxiosResponse<ApiResponse<OrderSummary[]>> = await api.get(
      `/orders/company/${companyId}/urgent`
    )
    return response.data.data!
  },

  /**
   * 배송 지연 주문 조회
   */
  getDelayedOrders: async (companyId: number): Promise<OrderSummary[]> => {
    const response: AxiosResponse<ApiResponse<OrderSummary[]>> = await api.get(
      `/orders/company/${companyId}/delayed`
    )
    return response.data.data!
  },

  /**
   * 상위 주문 조회
   */
  getTopOrders: async (
    companyId: number,
    params?: PaginationParams
  ): Promise<PageResponse<OrderSummary>> => {
    const response: AxiosResponse<ApiResponse<PageResponse<OrderSummary>>> = await api.get(
      `/orders/company/${companyId}/top`,
      { params }
    )
    return response.data.data!
  },

  /**
   * 최근 주문 조회
   */
  getRecentOrders: async (
    companyId: number,
    params?: PaginationParams
  ): Promise<PageResponse<OrderSummary>> => {
    const response: AxiosResponse<ApiResponse<PageResponse<OrderSummary>>> = await api.get(
      `/orders/company/${companyId}/recent`,
      { params }
    )
    return response.data.data!
  },

  /**
   * 오늘 주문 조회
   */
  getTodayOrders: async (companyId: number): Promise<OrderSummary[]> => {
    const response: AxiosResponse<ApiResponse<OrderSummary[]>> = await api.get(
      `/orders/company/${companyId}/today`
    )
    return response.data.data!
  },

  /**
   * 배송 예정 주문 조회
   */
  getOrdersForDelivery: async (companyId: number): Promise<OrderSummary[]> => {
    const response: AxiosResponse<ApiResponse<OrderSummary[]>> = await api.get(
      `/orders/company/${companyId}/for-delivery`
    )
    return response.data.data!
  },

  /**
   * 주문 통계 조회
   */
  getStatistics: async (companyId: number): Promise<OrderStats> => {
    const response: AxiosResponse<ApiResponse<OrderStats>> = await api.get(
      `/orders/company/${companyId}/statistics`
    )
    return response.data.data!
  },

  /**
   * 주문 상태 변경
   */
  changeStatus: async (orderId: number, data: {
    orderStatus: string
    reason?: string
  }): Promise<Order> => {
    const response: AxiosResponse<ApiResponse<Order>> = await api.put(
      `/orders/${orderId}/status`,
      data
    )
    return response.data.data!
  },

  /**
   * 주문 확정
   */
  confirm: async (orderId: number): Promise<Order> => {
    const response: AxiosResponse<ApiResponse<Order>> = await api.put(`/orders/${orderId}/confirm`)
    return response.data.data!
  },

  /**
   * 주문 배송 처리
   */
  ship: async (orderId: number, data: {
    courierCompany: string
    trackingNumber: string
    shippedDate?: string
    remarks?: string
  }): Promise<Order> => {
    const response: AxiosResponse<ApiResponse<Order>> = await api.put(
      `/orders/${orderId}/ship`,
      data
    )
    return response.data.data!
  },

  /**
   * 주문 배송완료 처리
   */
  markAsDelivered: async (orderId: number): Promise<Order> => {
    const response: AxiosResponse<ApiResponse<Order>> = await api.put(`/orders/${orderId}/delivered`)
    return response.data.data!
  },

  /**
   * 주문 완료 처리
   */
  complete: async (orderId: number): Promise<Order> => {
    const response: AxiosResponse<ApiResponse<Order>> = await api.put(`/orders/${orderId}/complete`)
    return response.data.data!
  },

  /**
   * 주문 취소
   */
  cancel: async (orderId: number, data: {
    cancellationReason: string
  }): Promise<Order> => {
    const response: AxiosResponse<ApiResponse<Order>> = await api.put(
      `/orders/${orderId}/cancel`,
      data
    )
    return response.data.data!
  },

  /**
   * 주문 결제 처리
   */
  processPayment: async (orderId: number, data: {
    paymentAmount: number
    paymentMethod: string
    paymentDate?: string
    paymentReference?: string
    remarks?: string
  }): Promise<Order> => {
    const response: AxiosResponse<ApiResponse<Order>> = await api.put(
      `/orders/${orderId}/payment`,
      data
    )
    return response.data.data!
  },

  /**
   * 주문번호 중복 확인
   */
  checkNumberDuplicate: async (companyId: number, orderNumber: string): Promise<boolean> => {
    const response: AxiosResponse<ApiResponse<boolean>> = await api.get(
      '/orders/check-duplicate/number',
      { params: { companyId, orderNumber } }
    )
    return response.data.data!
  },

  /**
   * 주문번호 생성
   */
  generateNumber: async (companyId: number): Promise<string> => {
    const response: AxiosResponse<ApiResponse<string>> = await api.get(
      `/orders/company/${companyId}/generate-number`
    )
    return response.data.data!
  },

  /**
   * 영업담당자별 주문 통계
   */
  getStatsBySalesRep: async (companyId: number): Promise<any[]> => {
    const response: AxiosResponse<ApiResponse<any[]>> = await api.get(
      `/orders/company/${companyId}/stats/sales-rep`
    )
    return response.data.data!
  },

  /**
   * 월별 주문 통계
   */
  getStatsByMonth: async (companyId: number, fromDate?: string): Promise<any[]> => {
    const response: AxiosResponse<ApiResponse<any[]>> = await api.get(
      `/orders/company/${companyId}/stats/monthly`,
      { params: { fromDate } }
    )
    return response.data.data!
  },

  /**
   * 고객별 주문 통계
   */
  getStatsByCustomer: async (companyId: number): Promise<any[]> => {
    const response: AxiosResponse<ApiResponse<any[]>> = await api.get(
      `/orders/company/${companyId}/stats/customer`
    )
    return response.data.data!
  }
}

// ================================
// 견적서 관리 API (기본 구조)
// ================================

export const quoteApi = {
  // TODO: 견적서 API 구현
  getById: async (quoteId: number): Promise<Quote> => {
    const response: AxiosResponse<ApiResponse<Quote>> = await api.get(`/quotes/${quoteId}`)
    return response.data.data!
  }
}

// ================================
// 계약 관리 API (기본 구조)
// ================================

export const contractApi = {
  // TODO: 계약 API 구현
  getById: async (contractId: number): Promise<Contract> => {
    const response: AxiosResponse<ApiResponse<Contract>> = await api.get(`/contracts/${contractId}`)
    return response.data.data!
  }
}

// 기본 export
export default {
  customerApi,
  orderApi,
  quoteApi,
  contractApi
}

