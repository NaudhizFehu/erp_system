import axios from 'axios'

/**
 * 주문 정보 인터페이스
 */
export interface Order {
  id: number
  orderNumber: string
  orderDate: string
  orderStatus: 'PENDING' | 'CONFIRMED' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED'
  paymentStatus: 'UNPAID' | 'PAID' | 'PARTIAL' | 'REFUNDED'
  customerId: number
  customerName: string
  customerType: 'INDIVIDUAL' | 'CORPORATION'
  totalAmount: number
  deliveryDate?: string
  salesManagerId?: number
  salesManagerName?: string
  notes?: string
  createdAt: string
  updatedAt?: string
}

/**
 * 주문 상품 정보 인터페이스
 */
export interface OrderItem {
  id: number
  orderId: number
  productId: number
  productName: string
  productCode: string
  quantity: number
  unitPrice: number
  totalPrice: number
}

/**
 * 주문 목록 조회 파라미터
 */
export interface OrderListParams {
  page?: number
  size?: number
  search?: string
  status?: string
  paymentStatus?: string
  customerId?: number
  startDate?: string
  endDate?: string
}

/**
 * 주문 목록 응답
 */
export interface OrderListResponse {
  content: Order[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
}

/**
 * 주문 상세 정보 (상품 목록 포함)
 */
export interface OrderDetail extends Order {
  items: OrderItem[]
}

/**
 * 주문 서비스 클래스
 * 주문 관련 API 호출을 담당합니다
 */
class OrderService {
  private baseUrl = '/api/orders'

  /**
   * 주문 목록 조회
   */
  async getOrders(params: OrderListParams = {}): Promise<OrderListResponse> {
    try {
      const response = await axios.get(this.baseUrl, { params })
      return response.data.data
    } catch (error) {
      console.error('주문 목록 조회 오류:', error)
      throw new Error('주문 목록을 불러오는 중 오류가 발생했습니다.')
    }
  }

  /**
   * 주문 상세 정보 조회
   */
  async getOrderById(id: number): Promise<OrderDetail> {
    try {
      const response = await axios.get(`${this.baseUrl}/${id}`)
      return response.data.data
    } catch (error) {
      console.error('주문 상세 정보 조회 오류:', error)
      throw new Error('주문 정보를 불러오는 중 오류가 발생했습니다.')
    }
  }

  /**
   * 주문 생성
   */
  async createOrder(order: Omit<Order, 'id' | 'createdAt' | 'updatedAt'>): Promise<Order> {
    try {
      const response = await axios.post(this.baseUrl, order)
      return response.data.data
    } catch (error) {
      console.error('주문 생성 오류:', error)
      throw new Error('주문 생성 중 오류가 발생했습니다.')
    }
  }

  /**
   * 주문 수정
   */
  async updateOrder(id: number, order: Partial<Order>): Promise<Order> {
    try {
      const response = await axios.put(`${this.baseUrl}/${id}`, order)
      return response.data.data
    } catch (error) {
      console.error('주문 수정 오류:', error)
      throw new Error('주문 수정 중 오류가 발생했습니다.')
    }
  }

  /**
   * 주문 삭제
   */
  async deleteOrder(id: number): Promise<void> {
    try {
      await axios.delete(`${this.baseUrl}/${id}`)
    } catch (error) {
      console.error('주문 삭제 오류:', error)
      throw new Error('주문 삭제 중 오류가 발생했습니다.')
    }
  }

  /**
   * 주문 상태 변경
   */
  async updateOrderStatus(id: number, status: Order['orderStatus']): Promise<Order> {
    try {
      const response = await axios.patch(`${this.baseUrl}/${id}/status`, { status })
      return response.data.data
    } catch (error) {
      console.error('주문 상태 변경 오류:', error)
      throw new Error('주문 상태 변경 중 오류가 발생했습니다.')
    }
  }

  /**
   * 결제 상태 변경
   */
  async updatePaymentStatus(id: number, paymentStatus: Order['paymentStatus']): Promise<Order> {
    try {
      const response = await axios.patch(`${this.baseUrl}/${id}/payment-status`, { paymentStatus })
      return response.data.data
    } catch (error) {
      console.error('결제 상태 변경 오류:', error)
      throw new Error('결제 상태 변경 중 오류가 발생했습니다.')
    }
  }

  /**
   * 주문 검색
   */
  async searchOrders(searchTerm: string): Promise<Order[]> {
    try {
      const response = await axios.get(`${this.baseUrl}/search`, {
        params: { q: searchTerm }
      })
      return response.data.data
    } catch (error) {
      console.error('주문 검색 오류:', error)
      throw new Error('주문 검색 중 오류가 발생했습니다.')
    }
  }
}

export const orderService = new OrderService()



