import axios from 'axios'

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
  departmentId: number
  departmentName: string
  positionId?: number
  positionName?: string
  companyId: number
  companyName: string
  hireDate: string
  employmentStatus: 'ACTIVE' | 'INACTIVE' | 'TERMINATED'
  employmentType: 'FULL_TIME' | 'PART_TIME' | 'CONTRACT' | 'INTERN'
  baseSalary?: number
  address?: string
  emergencyContact?: string
  emergencyRelation?: string
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
  private baseUrl = '/api/employees'

  /**
   * 직원 목록 조회
   */
  async getEmployees(params: EmployeeListParams = {}): Promise<EmployeeListResponse> {
    try {
      const response = await axios.get(this.baseUrl, { params })
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
      const response = await axios.get(`${this.baseUrl}/${id}`)
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
      const response = await axios.post(this.baseUrl, employee)
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
      const response = await axios.put(`${this.baseUrl}/${id}`, employee)
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
      await axios.delete(`${this.baseUrl}/${id}`)
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
      const response = await axios.get(`${this.baseUrl}/search`, {
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



