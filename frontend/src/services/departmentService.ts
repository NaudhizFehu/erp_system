import axios from 'axios'

/**
 * 부서 정보 인터페이스
 */
export interface Department {
  id: number
  departmentCode: string
  departmentName: string
  departmentNameEn?: string
  description?: string
  parentDepartmentId?: number
  parentDepartmentName?: string
  companyId: number
  companyName: string
  managerId?: number
  managerName?: string
  budget?: number
  location?: string
  isActive: boolean
  employeeCount: number
  createdAt: string
  updatedAt?: string
}

/**
 * 부서 목록 조회 파라미터
 */
export interface DepartmentListParams {
  page?: number
  size?: number
  search?: string
  companyId?: number
  parentDepartmentId?: number
  isActive?: boolean
}

/**
 * 부서 목록 응답
 */
export interface DepartmentListResponse {
  content: Department[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
}

/**
 * 부서 서비스 클래스
 * 부서 관련 API 호출을 담당합니다
 */
class DepartmentService {
  private baseUrl = '/api/departments'

  /**
   * 부서 목록 조회
   */
  async getDepartments(params: DepartmentListParams = {}): Promise<DepartmentListResponse> {
    try {
      const response = await axios.get(this.baseUrl, { params })
      return response.data.data
    } catch (error) {
      console.error('부서 목록 조회 오류:', error)
      throw new Error('부서 목록을 불러오는 중 오류가 발생했습니다.')
    }
  }

  /**
   * 부서 상세 정보 조회
   */
  async getDepartmentById(id: number): Promise<Department> {
    try {
      const response = await axios.get(`${this.baseUrl}/${id}`)
      return response.data.data
    } catch (error) {
      console.error('부서 상세 정보 조회 오류:', error)
      throw new Error('부서 정보를 불러오는 중 오류가 발생했습니다.')
    }
  }

  /**
   * 부서 생성
   */
  async createDepartment(department: Omit<Department, 'id' | 'createdAt' | 'updatedAt' | 'employeeCount'>): Promise<Department> {
    try {
      const response = await axios.post(this.baseUrl, department)
      return response.data.data
    } catch (error) {
      console.error('부서 생성 오류:', error)
      throw new Error('부서 생성 중 오류가 발생했습니다.')
    }
  }

  /**
   * 부서 수정
   */
  async updateDepartment(id: number, department: Partial<Department>): Promise<Department> {
    try {
      const response = await axios.put(`${this.baseUrl}/${id}`, department)
      return response.data.data
    } catch (error) {
      console.error('부서 수정 오류:', error)
      throw new Error('부서 수정 중 오류가 발생했습니다.')
    }
  }

  /**
   * 부서 삭제
   */
  async deleteDepartment(id: number): Promise<void> {
    try {
      await axios.delete(`${this.baseUrl}/${id}`)
    } catch (error) {
      console.error('부서 삭제 오류:', error)
      throw new Error('부서 삭제 중 오류가 발생했습니다.')
    }
  }

  /**
   * 부서 검색
   */
  async searchDepartments(searchTerm: string): Promise<Department[]> {
    try {
      const response = await axios.get(`${this.baseUrl}/search`, {
        params: { q: searchTerm }
      })
      return response.data.data
    } catch (error) {
      console.error('부서 검색 오류:', error)
      throw new Error('부서 검색 중 오류가 발생했습니다.')
    }
  }

  /**
   * 하위 부서 조회
   */
  async getSubDepartments(parentId: number): Promise<Department[]> {
    try {
      const response = await axios.get(`${this.baseUrl}/${parentId}/sub-departments`)
      return response.data.data
    } catch (error) {
      console.error('하위 부서 조회 오류:', error)
      throw new Error('하위 부서 조회 중 오류가 발생했습니다.')
    }
  }

  /**
   * 부서 직원 목록 조회
   */
  async getDepartmentEmployees(departmentId: number): Promise<any[]> {
    try {
      const response = await axios.get(`${this.baseUrl}/${departmentId}/employees`)
      return response.data.data
    } catch (error) {
      console.error('부서 직원 목록 조회 오류:', error)
      throw new Error('부서 직원 목록 조회 중 오류가 발생했습니다.')
    }
  }
}

export const departmentService = new DepartmentService()



