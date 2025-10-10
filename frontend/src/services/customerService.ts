import api from './api'

/**
 * 고객 정보 인터페이스
 */
export interface Customer {
  id: number
  customerCode: string
  customerName: string
  customerType: 'INDIVIDUAL' | 'CORPORATION'
  customerStatus: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED'
  customerGrade: 'A' | 'B' | 'C' | 'D'
  businessRegistrationNumber?: string
  ceoName?: string
  phone?: string
  email?: string
  address?: string
  salesManagerId?: number
  salesManagerName?: string
  creditLimit?: number
  paymentTerms?: string
  createdAt: string
  updatedAt?: string
  description?: string
}

/**
 * 고객 목록 조회 파라미터
 */
export interface CustomerListParams {
  page?: number
  size?: number
  search?: string
  status?: string
  type?: string
  grade?: string
}

/**
 * 고객 목록 응답
 */
export interface CustomerListResponse {
  content: Customer[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
}

/**
 * 고객 서비스 클래스
 * 고객 관련 API 호출을 담당합니다
 */
class CustomerService {
  private baseUrl = '/sales/customers'

  /**
   * 고객 목록 조회
   */
  async getCustomers(params: CustomerListParams = {}): Promise<CustomerListResponse> {
    try {
      const response = await api.get(this.baseUrl, { params })
      return response.data
    } catch (error) {
      console.error('고객 목록 조회 오류:', error)
      throw new Error('고객 목록을 불러오는 중 오류가 발생했습니다.')
    }
  }

  /**
   * 고객 상세 정보 조회
   */
  async getCustomerById(id: number): Promise<Customer> {
    try {
      console.log('고객 상세 조회 API 호출:', `${this.baseUrl}/${id}`)
      const response = await api.get(`${this.baseUrl}/${id}`)
      console.log('고객 상세 조회 API 응답:', response)
      console.log('고객 상세 조회 API 응답 데이터:', response.data)
      // 백엔드 응답 구조: {success: true, message: '...', data: {고객데이터}, ...}
      // response.data.data가 실제 고객 데이터이므로 이를 반환
      return response.data.data
    } catch (error) {
      console.error('고객 상세 정보 조회 오류:', error)
      throw new Error('고객 정보를 불러오는 중 오류가 발생했습니다.')
    }
  }

  /**
   * 고객 생성
   */
  async createCustomer(customer: Omit<Customer, 'id' | 'createdAt' | 'updatedAt'>): Promise<Customer> {
    try {
      const response = await api.post(this.baseUrl, customer)
      return response.data
    } catch (error) {
      console.error('고객 생성 오류:', error)
      throw new Error('고객 생성 중 오류가 발생했습니다.')
    }
  }

  /**
   * 고객 수정
   */
  async updateCustomer(id: number, customer: Partial<Customer>): Promise<Customer> {
    try {
      const response = await api.put(`${this.baseUrl}/${id}`, customer)
      return response.data
    } catch (error) {
      console.error('고객 수정 오류:', error)
      throw new Error('고객 수정 중 오류가 발생했습니다.')
    }
  }

  /**
   * 고객 삭제
   */
  async deleteCustomer(id: number): Promise<void> {
    try {
      await api.delete(`${this.baseUrl}/${id}`)
    } catch (error) {
      console.error('고객 삭제 오류:', error)
      throw new Error('고객 삭제 중 오류가 발생했습니다.')
    }
  }

  /**
   * 고객 검색
   */
  async searchCustomers(searchTerm: string): Promise<Customer[]> {
    try {
      const response = await api.get(`${this.baseUrl}/search`, {
        params: { q: searchTerm }
      })
      return response.data
    } catch (error) {
      console.error('고객 검색 오류:', error)
      throw new Error('고객 검색 중 오류가 발생했습니다.')
    }
  }
}

export const customerService = new CustomerService()



