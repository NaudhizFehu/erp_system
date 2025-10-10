import api from './api'

/**
 * 직원 정보 인터페이스
 */
export interface Employee {
  id: number
  employeeNumber: string
  name: string
  nameEn?: string
  email: string
  phone?: string
  mobile?: string
  birthDate?: string
  gender?: string
  address?: string
  postalCode?: string
  company: {
    id: number
    name: string
    nameEn?: string
  }
  department: {
    id: number
    name: string
    nameEn?: string
  }
  position?: {
    id: number
    name: string
    nameEn?: string
  }
  hireDate: string
  terminationDate?: string
  employmentStatus: 'ACTIVE' | 'INACTIVE' | 'TERMINATED'
  employmentType: 'FULL_TIME' | 'PART_TIME' | 'CONTRACT' | 'INTERN'
  bankName?: string
  accountNumber?: string
  accountHolder?: string
  emergencyContact?: string
  emergencyRelation?: string
  education?: string
  major?: string
  career?: string
  skills?: string
  certifications?: string
  memo?: string
  profileImageUrl?: string
  yearsOfService?: number
  age?: number
  createdAt: string
  updatedAt?: string
}

/**
 * 직원 목록 조회 파라미터
 */
export interface EmployeeListParams {
  page?: number
  size?: number
  search?: string
  departmentId?: number
  status?: string
  type?: string
}

/**
 * 직원 목록 응답
 */
export interface EmployeeListResponse {
  content: Employee[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
}

/**
 * 직원 서비스 클래스
 * 직원 관련 API 호출을 담당합니다
 */
class EmployeeService {
  private baseUrl = '/hr/employees'

  /**
   * 직원 목록 조회
   */
  async getEmployees(params: EmployeeListParams = {}): Promise<EmployeeListResponse> {
    try {
      const response = await api.get(this.baseUrl, { params })
      return response.data.data
    } catch (error) {
      console.error('직원 목록 조회 오류:', error)
      throw new Error('직원 목록을 불러오는 중 오류가 발생했습니다.')
    }
  }

  /**
   * 직원 상세 정보 조회
   */
  async getEmployeeById(id: number): Promise<Employee> {
    try {
      console.log('직원 조회 API 호출:', `${this.baseUrl}/${id}`)
      const response = await api.get(`${this.baseUrl}/${id}`)
      console.log('직원 조회 API 응답:', response)
      console.log('직원 조회 API 응답 데이터:', response.data)
      console.log('직원 조회 API 응답 데이터.data:', response.data.data)
      
      // 백엔드 응답 구조: {success: true, message: '...', data: {직원데이터}, ...}
      // response.data.data가 실제 직원 데이터이므로 이를 반환
      return response.data.data
    } catch (error) {
      console.error('직원 상세 정보 조회 오류:', error)
      throw new Error('직원 정보를 불러오는 중 오류가 발생했습니다.')
    }
  }

  /**
   * 직원 생성
   */
  async createEmployee(employee: Omit<Employee, 'id' | 'createdAt' | 'updatedAt'>): Promise<Employee> {
    try {
      const response = await api.post(this.baseUrl, employee)
      return response.data.data
    } catch (error) {
      console.error('직원 생성 오류:', error)
      throw new Error('직원 생성 중 오류가 발생했습니다.')
    }
  }

  /**
   * 직원 수정
   */
  async updateEmployee(id: number, employee: Partial<Employee>): Promise<Employee> {
    try {
      const response = await api.put(`${this.baseUrl}/${id}`, employee)
      return response.data.data
    } catch (error) {
      console.error('직원 수정 오류:', error)
      throw new Error('직원 수정 중 오류가 발생했습니다.')
    }
  }

  /**
   * 직원 삭제
   */
  async deleteEmployee(id: number): Promise<void> {
    try {
      await api.delete(`${this.baseUrl}/${id}`)
    } catch (error) {
      console.error('직원 삭제 오류:', error)
      throw new Error('직원 삭제 중 오류가 발생했습니다.')
    }
  }

  /**
   * 직원 검색
   */
  async searchEmployees(searchTerm: string): Promise<Employee[]> {
    try {
      const response = await api.get(`${this.baseUrl}/search`, {
        params: { q: searchTerm }
      })
      return response.data.data
    } catch (error) {
      console.error('직원 검색 오류:', error)
      throw new Error('직원 검색 중 오류가 발생했습니다.')
    }
  }
}

export const employeeService = new EmployeeService()



