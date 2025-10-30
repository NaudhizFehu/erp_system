import api from './api'

/**
 * 부서 정보 인터페이스
 */
export interface Department {
  id: number
  name: string
  nameEn?: string
  departmentCode: string
  departmentType: string
  description?: string
  manager?: {
    id: number
    name: string
  }
  company: {
    id: number
    name: string
  }
  parentDepartment?: {
    id: number
    name: string
  }
  level: number
  status: string
  location?: string
  budgetAmount?: number
  createdAt: string
  updatedAt: string
}

/**
 * 부서 서비스
 */
class DepartmentService {
  private baseUrl = '/departments'

  /**
   * 모든 부서 목록 조회 (간단한 형태)
   */
  async getAllDepartments(): Promise<{ id: number; name: string }[]> {
    try {
      console.log('부서 목록 조회 API 호출:', `${this.baseUrl}`)
      const response = await api.get(`${this.baseUrl}?page=0&size=1000`)
      console.log('부서 목록 조회 API 응답:', response)
      
      // 응답에서 부서 목록 추출 (Page 객체에서 content 배열 추출)
      const pageData = response.data?.data || response.data
      const departments = pageData?.content || []
      
      if (!Array.isArray(departments)) {
        console.error('부서 목록이 배열이 아닙니다:', departments)
        return []
      }
      
      return departments.map((dept: Department) => ({
        id: dept.id,
        name: dept.name
      }))
    } catch (error) {
      console.error('부서 목록 조회 오류:', error)
      throw new Error('부서 목록을 불러오는 중 오류가 발생했습니다.')
    }
  }

  /**
   * 특정 부서 상세 정보 조회
   */
  async getDepartmentById(departmentId: number): Promise<Department> {
    try {
      console.log('부서 상세 조회 API 호출:', `${this.baseUrl}/${departmentId}`)
      const response = await api.get(`${this.baseUrl}/${departmentId}`)
      console.log('부서 상세 조회 API 응답:', response)
      
      // 응답에서 부서 정보 추출
      const department = response.data?.data || response.data
      
      if (!department) {
        throw new Error('부서 정보를 찾을 수 없습니다.')
      }
      
      return department
    } catch (error) {
      console.error('부서 상세 조회 오류:', error)
      throw new Error('부서 정보를 불러오는 중 오류가 발생했습니다.')
    }
  }

  /**
   * 특정 회사의 부서 목록 조회
   */
  async getDepartmentsByCompany(companyId: number): Promise<{ id: number; name: string }[]> {
    try {
      console.log('회사별 부서 목록 조회 API 호출:', `${this.baseUrl}/company/${companyId}`)
      const response = await api.get(`${this.baseUrl}/company/${companyId}?page=0&size=1000`)
      console.log('회사별 부서 목록 조회 API 응답:', response)
      
      // 응답에서 부서 목록 추출 (Page 객체에서 content 배열 추출)
      const pageData = response.data?.data || response.data
      const departments = pageData?.content || []
      
      if (!Array.isArray(departments)) {
        console.error('회사별 부서 목록이 배열이 아닙니다:', departments)
        return []
      }
      
      return departments.map((dept: Department) => ({
        id: dept.id,
        name: dept.name
      }))
    } catch (error) {
      console.error('회사별 부서 목록 조회 오류:', error)
      throw new Error('부서 목록을 불러오는 중 오류가 발생했습니다.')
    }
  }
}

export const departmentService = new DepartmentService()