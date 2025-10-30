/**
 * HR 모듈 API 서비스
 * 백엔드 REST API와 통신하는 함수들을 정의합니다
 */

import api from './api'
import type {
  PageResponse,
  Employee,
  Position,
  EmployeeCreateRequest,
  EmployeeUpdateRequest,
  PositionCreateRequest,
  PositionUpdateRequest,
  SearchParams,
  StatisticsData,
  EmploymentStatus,
  PositionCategory,
  PositionType,
  Company,
  Department,
  ImportResult,
  ExportFormat
} from '@/types/hr'
import type { ApiResponse } from '@/types/common'

// Mock API import
import { mockEmployeeApi, mockPositionApi } from './hrMockApi'

// API 기본 설정
const API_BASE_URL = '/hr'

// Mock API 사용 여부 (운영 환경에서는 false로 설정)
const USE_MOCK_API = false

/**
 * 회사 관리 API 서비스
 */
export const companyApi = {
  /**
   * 전체 회사 목록 조회
   */
  getCompanies: async (): Promise<Company[]> => {
    try {
      const response = await api.get('/companies', {
        params: { page: 0, size: 1000 }
      })
      
      // 백엔드는 ApiResponse<PageResponse<Company>> 형식으로 반환
      // response.data.data는 PageResponse<Company>
      // response.data.data.content는 Company[]
      return response.data?.data?.content || []
    } catch (error) {
      console.error('회사 목록 조회 오류:', error)
      return []
    }
  }
}

/**
 * 부서 관리 API 서비스
 */
export const departmentApi = {
  /**
   * 전체 부서 목록 조회
   */
  getDepartments: async (): Promise<Department[]> => {
    try {
      const response = await api.get('/departments', {
        params: { page: 0, size: 1000 }
      })
      
      // 백엔드는 ApiResponse<PageResponse<Department>> 형식으로 반환
      // response.data.data는 PageResponse<Department>
      // response.data.data.content는 Department[]
      return response.data?.data?.content || []
    } catch (error) {
      console.error('부서 목록 조회 오류:', error)
      return []
    }
  },

  /**
   * 회사별 부서 목록 조회
   */
  getDepartmentsByCompany: async (companyId: number): Promise<Department[]> => {
    try {
      const response = await api.get(`/departments/company/${companyId}`, {
        params: { page: 0, size: 1000 }
      })
      
      // 백엔드는 ApiResponse<PageResponse<Department>> 형식으로 반환
      return response.data?.data?.content || []
    } catch (error) {
      console.error('회사별 부서 목록 조회 오류:', error)
      return []
    }
  }
}


/**
 * 직원 관리 API 서비스
 */
export const employeeApi = {
  /**
   * 직원 목록 조회 (페이징)
   */
  getEmployees: async (params: SearchParams = {}): Promise<PageResponse<Employee>> => {
    if (USE_MOCK_API) {
      return mockEmployeeApi.getEmployees(params)
    }
    
    const response = await api.get(`${API_BASE_URL}/employees`, {
      params: {
        page: params.page || 0,
        size: params.size || 20,
        sort: params.sort || 'employeeNumber',
        ...params
      }
    })
    
    // 백엔드는 ApiResponse<PageResponse<Employee>> 형식으로 응답하므로 response.data.data를 사용
    const pageData = response.data.data
    
    // 빈 결과를 반환하는 경우를 대비한 기본값 설정
    if (!pageData || !pageData.content) {
      return {
        content: [],
        pageable: {
          sort: { sorted: false, unsorted: true, empty: true },
          pageNumber: params.page || 0,
          pageSize: params.size || 20,
          offset: (params.page || 0) * (params.size || 20),
          paged: true,
          unpaged: false
        },
        totalElements: 0,
        totalPages: 0,
        last: true,
        first: true,
        numberOfElements: 0,
        size: params.size || 20,
        number: params.page || 0,
        sort: { sorted: false, unsorted: true, empty: true },
        empty: true
      }
    }
    
    return pageData
  },

  /**
   * 직원 상세 조회
   */
  getEmployee: async (id: number): Promise<Employee> => {
    if (USE_MOCK_API) {
      return mockEmployeeApi.getEmployee(id)
    }
    
    const { data } = await api.get<ApiResponse<Employee>>(`${API_BASE_URL}/employees/${id}`)
    return data.data
  },

  /**
   * 사번으로 직원 조회
   */
  getEmployeeByNumber: async (employeeNumber: string): Promise<Employee> => {
    if (USE_MOCK_API) {
      return mockEmployeeApi.getEmployeeByNumber(employeeNumber)
    }
    
    const { data } = await api.get<ApiResponse<Employee>>(`${API_BASE_URL}/employees/number/${employeeNumber}`)
    return data.data
  },

  /**
   * 이메일로 직원 조회
   */
  getEmployeeByEmail: async (email: string): Promise<Employee> => {
    if (USE_MOCK_API) {
      return mockEmployeeApi.getEmployeeByEmail(email)
    }
    
    const { data } = await api.get<ApiResponse<Employee>>(`${API_BASE_URL}/employees/email/${email}`)
    return data.data
  },

  /**
   * 직원 검색
   */
  searchEmployees: async (searchTerm: string, params: SearchParams = {}): Promise<PageResponse<Employee>> => {
    if (USE_MOCK_API) {
      return mockEmployeeApi.searchEmployees(searchTerm, params)
    }
    
    const response = await api.get(`${API_BASE_URL}/employees/search`, {
      params: {
        searchTerm,
        page: params.page || 0,
        size: params.size || 20,
        sort: params.sort || 'employeeNumber'
      }
    })
    
    // 백엔드는 ApiResponse<PageResponse<Employee>> 형식으로 응답하므로 response.data.data를 사용
    const pageData = response.data.data
    
    // 빈 결과를 반환하는 경우를 대비한 기본값 설정
    if (!pageData || !pageData.content) {
      return {
        content: [],
        pageable: {
          sort: { sorted: false, unsorted: true, empty: true },
          pageNumber: params.page || 0,
          pageSize: params.size || 20,
          offset: (params.page || 0) * (params.size || 20),
          paged: true,
          unpaged: false
        },
        totalElements: 0,
        totalPages: 0,
        last: true,
        first: true,
        numberOfElements: 0,
        size: params.size || 20,
        number: params.page || 0,
        sort: { sorted: false, unsorted: true, empty: true },
        empty: true
      }
    }
    
    return pageData
  },

  /**
   * 회사별 직원 목록 조회
   */
  getEmployeesByCompany: async (companyId: number, params: SearchParams = {}): Promise<PageResponse<Employee>> => {
    if (USE_MOCK_API) {
      return mockEmployeeApi.getEmployeesByCompany(companyId, params)
    }
    
    const { data } = await api.get<ApiResponse<PageResponse<Employee>>>(`${API_BASE_URL}/employees/company/${companyId}`, {
      params: {
        page: params.page || 0,
        size: params.size || 20,
        sort: params.sort || 'employeeNumber'
      }
    })
    return data.data
  },

  /**
   * 부서별 직원 목록 조회
   */
  getEmployeesByDepartment: async (departmentId: number, params: SearchParams = {}): Promise<PageResponse<Employee>> => {
    if (USE_MOCK_API) {
      return mockEmployeeApi.getEmployeesByDepartment(departmentId, params)
    }
    
    const { data } = await api.get<ApiResponse<PageResponse<Employee>>>(`${API_BASE_URL}/employees/department/${departmentId}`, {
      params: {
        page: params.page || 0,
        size: params.size || 20,
        sort: params.sort || 'employeeNumber'
      }
    })
    return data.data
  },

  /**
   * 재직 중인 직원 목록 조회
   */
  getActiveEmployees: async (): Promise<Employee[]> => {
    if (USE_MOCK_API) {
      return mockEmployeeApi.getActiveEmployees()
    }
    
    const { data } = await api.get<Employee[]>(`${API_BASE_URL}/employees/active`)
    return data || []
  },

  /**
   * 회사별 재직 중인 직원 목록 조회
   */
  getActiveEmployeesByCompany: async (companyId: number): Promise<Employee[]> => {
    if (USE_MOCK_API) {
      return mockEmployeeApi.getActiveEmployeesByCompany(companyId)
    }
    
    const { data } = await api.get<Employee[]>(`${API_BASE_URL}/employees/active/company/${companyId}`)
    return data || []
  },

  /**
   * 입사일 범위로 직원 조회
   */
  getEmployeesByHireDateRange: async (startDate: string, endDate: string): Promise<Employee[]> => {
    const { data } = await api.get<Employee[]>(`${API_BASE_URL}/employees/hire-date`, {
      params: { startDate, endDate }
    })
    return data || []
  },

  /**
   * 생일인 직원 조회
   */
  getEmployeesByBirthday: async (month: number, day: number): Promise<Employee[]> => {
    const { data } = await api.get<ApiResponse<Employee[]>>(`${API_BASE_URL}/employees/birthday`, {
      params: { month, day }
    })
    return data.data
  },

  /**
   * 이번 달 생일인 직원 조회
   */
  getBirthdayEmployeesThisMonth: async (): Promise<Employee[]> => {
    const { data } = await api.get<ApiResponse<Employee[]>>(`${API_BASE_URL}/employees/birthday/this-month`)
    return data.data
  },

  /**
   * 직원 등록
   */
  createEmployee: async (employee: EmployeeCreateRequest): Promise<Employee> => {
    if (USE_MOCK_API) {
      return mockEmployeeApi.createEmployee(employee)
    }
    
    const { data } = await api.post<ApiResponse<Employee>>(`${API_BASE_URL}/employees`, employee)
    return data.data
  },

  /**
   * 직원 정보 수정
   */
  updateEmployee: async (id: number, employee: EmployeeUpdateRequest): Promise<Employee> => {
    if (USE_MOCK_API) {
      return mockEmployeeApi.updateEmployee(id, employee)
    }
    
    const { data } = await api.put<ApiResponse<Employee>>(`${API_BASE_URL}/employees/${id}`, employee)
    return data.data
  },

  /**
   * 직원 퇴직 처리
   */
  terminateEmployee: async (id: number, terminationDate: string, reason: string): Promise<void> => {
    await api.patch<ApiResponse<void>>(`${API_BASE_URL}/employees/${id}/terminate`, null, {
      params: { terminationDate, reason }
    })
  },

  /**
   * 직원 복직 처리
   */
  reactivateEmployee: async (id: number): Promise<void> => {
    await api.patch<ApiResponse<void>>(`${API_BASE_URL}/employees/${id}/reactivate`)
  },

  /**
   * 직원 삭제 (소프트 삭제)
   */
  deleteEmployee: async (id: number): Promise<void> => {
    if (USE_MOCK_API) {
      return mockEmployeeApi.deleteEmployee(id)
    }
    
    await api.delete<ApiResponse<void>>(`${API_BASE_URL}/employees/${id}`)
  },

  /**
   * 사번 중복 확인
   */
  checkEmployeeNumber: async (employeeNumber: string, excludeId?: number): Promise<boolean> => {
    if (USE_MOCK_API) {
      return mockEmployeeApi.checkEmployeeNumber(employeeNumber, excludeId)
    }
    
    try {
      const { data } = await api.get<ApiResponse<boolean>>(`${API_BASE_URL}/employees/check/employee-number`, {
        params: { employeeNumber, excludeId }
      })
      return data.data ?? false
    } catch (error) {
      console.error('사번 중복 확인 오류:', error)
      return false
    }
  },

  /**
   * 이메일 중복 확인
   */
  checkEmail: async (email: string, excludeId?: number): Promise<boolean> => {
    if (USE_MOCK_API) {
      return mockEmployeeApi.checkEmail(email, excludeId)
    }
    
    try {
      const { data } = await api.get<ApiResponse<boolean>>(`${API_BASE_URL}/employees/check/email`, {
        params: { email, excludeId }
      })
      return data.data ?? false
    } catch (error) {
      console.error('이메일 중복 확인 오류:', error)
      return false
    }
  },

  /**
   * 회사별 최근 직원 목록 조회 (사번 중복 방지용)
   */
  getRecentEmployeesByCompany: async (companyId: number): Promise<Employee[]> => {
    if (USE_MOCK_API) {
      return mockEmployeeApi.getRecentEmployeesByCompany(companyId)
    }
    
    try {
      const response = await api.get<ApiResponse<Employee[]>>(`${API_BASE_URL}/employees/recent/company/${companyId}`)
      
      // api.ts 인터셉터가 response.data를 반환하므로
      // response 자체가 ApiResponse 구조: { success, message, data, ... }
      // 실제 직원 목록은 response.data에 있음
      return Array.isArray(response) ? response : (response.data?.data || [])
    } catch (error) {
      console.error('회사별 최근 직원 목록 조회 오류:', error)
      return []
    }
  },

  /**
   * 직급별 직원 수 통계
   */
  getEmployeeCountByPosition: async (): Promise<StatisticsData[]> => {
    if (USE_MOCK_API) {
      return mockEmployeeApi.getEmployeeCountByPosition()
    }
    
    const { data } = await api.get<ApiResponse<[string, number][]>>(`${API_BASE_URL}/employees/statistics/position`)
    return data.data.map(([label, count]) => ({ label, count }))
  },

  /**
   * 부서별 직원 수 통계
   */
  getEmployeeCountByDepartment: async (): Promise<StatisticsData[]> => {
    if (USE_MOCK_API) {
      return mockEmployeeApi.getEmployeeCountByDepartment()
    }
    
    const { data } = await api.get<ApiResponse<[string, number][]>>(`${API_BASE_URL}/employees/statistics/department`)
    return data.data.map(([label, count]) => ({ label, count }))
  },

  /**
   * 입사년도별 직원 수 통계
   */
  getEmployeeCountByHireYear: async (): Promise<StatisticsData[]> => {
    if (USE_MOCK_API) {
      return mockEmployeeApi.getEmployeeCountByAgeGroup() // 임시로 연령대 통계 사용
    }
    
    const { data } = await api.get<ApiResponse<[number, number][]>>(`${API_BASE_URL}/employees/statistics/hire-year`)
    return data.data.map(([year, count]) => ({ label: `${year}년`, count }))
  },

  /**
   * 연령대별 직원 수 통계
   */
  getEmployeeCountByAgeGroup: async (): Promise<StatisticsData[]> => {
    if (USE_MOCK_API) {
      return mockEmployeeApi.getEmployeeCountByAgeGroup()
    }
    
    const { data } = await api.get<ApiResponse<[string, number][]>>(`${API_BASE_URL}/employees/statistics/age-group`)
    return data.data.map(([label, count]) => ({ label, count }))
  },

  /**
   * 성별 직원 수 통계
   */
  getEmployeeCountByGender: async (): Promise<StatisticsData[]> => {
    if (USE_MOCK_API) {
      return mockEmployeeApi.getEmployeeCountByGender()
    }
    
    const { data } = await api.get<ApiResponse<[string, number][]>>(`${API_BASE_URL}/employees/statistics/gender`)
    return data.data.map(([gender, count]) => ({ 
      label: gender === 'MALE' ? '남성' : '여성', 
      count 
    }))
  },

  /**
   * 엑셀로 직원 데이터 내보내기
   */
  exportToExcel: async (companyId?: number): Promise<Blob> => {
    const params = companyId ? { companyId } : {}
    const response = await api.get(`${API_BASE_URL}/employees/export/excel`, {
      params,
      responseType: 'blob'
    })
    return response.data
  },

  /**
   * CSV로 직원 데이터 내보내기
   */
  exportToCsv: async (companyId?: number): Promise<Blob> => {
    const params = companyId ? { companyId } : {}
    const response = await api.get(`${API_BASE_URL}/employees/export/csv`, {
      params,
      responseType: 'blob'
    })
    return response.data
  },

  /**
   * 엑셀에서 직원 데이터 가져오기
   */
  importFromExcel: async (file: File, companyId: number): Promise<ImportResult> => {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('companyId', companyId.toString())
    
    const response = await api.post(`${API_BASE_URL}/employees/import/excel`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    return response.data.data
  },

  /**
   * CSV에서 직원 데이터 가져오기
   */
  importFromCsv: async (file: File, companyId: number): Promise<ImportResult> => {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('companyId', companyId.toString())
    
    const response = await api.post(`${API_BASE_URL}/employees/import/csv`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    return response.data.data
  },

  /**
   * 상태별 직원 수 조회
   */
  getCountsByStatus: async (): Promise<Record<string, number>> => {
    if (USE_MOCK_API) {
      return {
        ACTIVE: 3,
        ON_LEAVE: 7,
        INACTIVE: 6,
        SUSPENDED: 8,
        TERMINATED: 6
      }
    }
    
    const { data } = await api.get<ApiResponse<Record<string, number>>>(
      `${API_BASE_URL}/employees/count/by-status`
    )
    return data.data
  }
}

/**
 * 직급 관리 API 서비스
 */
export const positionApi = {
  /**
   * 직급 목록 조회 (페이징)
   */
  getPositions: async (params: SearchParams = {}): Promise<PageResponse<Position>> => {
    if (USE_MOCK_API) {
      return mockPositionApi.getPositions(params)
    }
    
    const { data } = await api.get<ApiResponse<PageResponse<Position>>>('/positions', {
      params: {
        page: params.page || 0,
        size: params.size || 20,
        sort: params.sort || 'positionLevel',
        ...params
      }
    })
    return data.data
  },

  /**
   * 전체 직급 목록 조회 (페이징 없음)
   */
  getAllPositions: async (): Promise<Position[]> => {
    if (USE_MOCK_API) {
      const result = await mockPositionApi.getPositions({ page: 0, size: 1000 })
      return result.content || []
    }
    
    try {
      const response = await api.get<ApiResponse<PageResponse<Position>>>('/positions', {
        params: { page: 0, size: 1000 }
      })
      return response.data?.data?.content || []
    } catch (error) {
      console.error('전체 직급 목록 조회 오류:', error)
      return []
    }
  },

  /**
   * 직급 상세 조회
   */
  getPosition: async (id: number): Promise<Position> => {
    if (USE_MOCK_API) {
      return mockPositionApi.getPosition(id)
    }
    
    const { data } = await api.get<ApiResponse<Position>>(`/positions/${id}`)
    return data.data
  },

  /**
   * 직급 코드로 조회
   */
  getPositionByCode: async (positionCode: string): Promise<Position> => {
    const { data } = await api.get<ApiResponse<Position>>(`/positions/code/${positionCode}`)
    return data.data
  },


  /**
   * 회사별 직급 목록 조회
   */
  getPositionsByCompany: async (companyId: number): Promise<Position[]> => {
    if (USE_MOCK_API) {
      return mockPositionApi.getPositionsByCompany(companyId)
    }
    
    const { data } = await api.get<ApiResponse<Position[]>>(`/positions/company/${companyId}`)
    return data.data
  },

  /**
   * 회사별 활성 직급 목록 조회
   */
  getActivePositionsByCompany: async (companyId: number): Promise<Position[]> => {
    const { data } = await api.get<ApiResponse<Position[]>>(`/positions/company/${companyId}/active`)
    return data.data
  },

  /**
   * 직급 레벨별 조회
   */
  getPositionsByLevel: async (level: number): Promise<Position[]> => {
    const { data } = await api.get<ApiResponse<Position[]>>(`/positions/level/${level}`)
    return data.data
  },

  /**
   * 직급 분류별 조회
   */
  getPositionsByCategory: async (category: PositionCategory): Promise<Position[]> => {
    const { data } = await api.get<ApiResponse<Position[]>>(`/positions/category/${category}`)
    return data.data
  },

  /**
   * 직급 유형별 조회
   */
  getPositionsByType: async (type: PositionType): Promise<Position[]> => {
    const { data } = await api.get<ApiResponse<Position[]>>(`/positions/type/${type}`)
    return data.data
  },

  /**
   * 활성 직급 목록 조회
   */
  getActivePositions: async (): Promise<Position[]> => {
    if (USE_MOCK_API) {
      return mockPositionApi.getActivePositions()
    }
    
    const { data } = await api.get<ApiResponse<Position[]>>(`/positions/active`)
    return data.data
  },

  /**
   * 직급 검색
   */
  searchPositions: async (searchTerm: string, params: SearchParams = {}): Promise<PageResponse<Position>> => {
    const { data } = await api.get<ApiResponse<PageResponse<Position>>>(`/positions/search`, {
      params: {
        searchTerm,
        page: params.page || 0,
        size: params.size || 20,
        sort: params.sort || 'positionLevel'
      }
    })
    return data.data
  },

  /**
   * 승진 가능한 직급 조회
   */
  getPromotablePositions: async (companyId: number, currentLevel: number): Promise<Position[]> => {
    const { data } = await api.get<ApiResponse<Position[]>>(`/positions/promotable`, {
      params: { companyId, currentLevel }
    })
    return data.data
  },

  /**
   * 급여 범위별 직급 조회
   */
  getPositionsBySalaryRange: async (salary: number): Promise<Position[]> => {
    const { data } = await api.get<ApiResponse<Position[]>>(`/positions/salary-range`, {
      params: { salary }
    })
    return data.data
  },

  /**
   * 직급 등록
   */
  createPosition: async (position: PositionCreateRequest): Promise<Position> => {
    const { data } = await api.post<ApiResponse<Position>>(`/positions`, position)
    return data.data
  },

  /**
   * 직급 정보 수정
   */
  updatePosition: async (id: number, position: PositionUpdateRequest): Promise<Position> => {
    const { data } = await api.put<ApiResponse<Position>>(`/positions/${id}`, position)
    return data.data
  },

  /**
   * 직급 활성화/비활성화 토글
   */
  togglePositionStatus: async (id: number): Promise<Position> => {
    const { data } = await api.patch<ApiResponse<Position>>(`/positions/${id}/toggle-status`)
    return data.data
  },

  /**
   * 직급 삭제 (소프트 삭제)
   */
  deletePosition: async (id: number): Promise<void> => {
    await api.delete<ApiResponse<void>>(`/positions/${id}`)
  },

  /**
   * 직급 코드 중복 확인
   */
  checkPositionCode: async (positionCode: string, excludeId?: number): Promise<boolean> => {
    const { data } = await api.get<ApiResponse<boolean>>(`/positions/check/position-code`, {
      params: { positionCode, excludeId }
    })
    return data.data
  },

  /**
   * 회사별 직급 코드 중복 확인
   */
  checkPositionCodeInCompany: async (companyId: number, positionCode: string, excludeId?: number): Promise<boolean> => {
    const { data } = await api.get<ApiResponse<boolean>>(`/positions/check/position-code/company/${companyId}`, {
      params: { positionCode, excludeId }
    })
    return data.data
  },

  /**
   * 직급 분류별 통계
   */
  getPositionCountByCategory: async (): Promise<StatisticsData[]> => {
    const { data } = await api.get<ApiResponse<[PositionCategory, number][]>>(`/positions/statistics/category`)
    return data.data.map(([category, count]) => ({ 
      label: category === 'EXECUTIVE' ? '임원' : 
             category === 'MANAGEMENT' ? '관리직' :
             category === 'SENIOR' ? '선임' :
             category === 'JUNIOR' ? '주니어' : '인턴', 
      count 
    }))
  },

  /**
   * 직급 유형별 통계
   */
  getPositionCountByType: async (): Promise<StatisticsData[]> => {
    const { data } = await api.get<ApiResponse<[PositionType, number][]>>(`/positions/statistics/type`)
    return data.data.map(([type, count]) => ({ 
      label: type === 'PERMANENT' ? '정규직' :
             type === 'CONTRACT' ? '계약직' :
             type === 'TEMPORARY' ? '임시직' : '컨설턴트',
      count 
    }))
  },

  /**
   * 회사별 직급 수 통계
   */
  getPositionCountByCompany: async (): Promise<StatisticsData[]> => {
    const { data } = await api.get<ApiResponse<[string, number][]>>(`/positions/statistics/company`)
    return data.data.map(([label, count]) => ({ label, count }))
  },

  /**
   * 레벨별 직급 수 통계
   */
  getPositionCountByLevel: async (): Promise<StatisticsData[]> => {
    const { data } = await api.get<ApiResponse<[number, number][]>>(`/positions/statistics/level`)
    return data.data.map(([level, count]) => ({ label: `${level}레벨`, count }))
  }
}

/**
 * 공통 유틸리티 함수들
 */
export const hrUtils = {
  /**
   * 금액을 한국어 형식으로 포맷팅
   */
  formatCurrency: (amount: number): string => {
    return new Intl.NumberFormat('ko-KR', {
      style: 'currency',
      currency: 'KRW'
    }).format(amount)
  },

  /**
   * 날짜를 한국어 형식으로 포맷팅
   */
  formatDate: (date: string): string => {
    return new Intl.DateTimeFormat('ko-KR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    }).format(new Date(date))
  },

  /**
   * 날짜시간을 한국어 형식으로 포맷팅
   */
  formatDateTime: (datetime: string): string => {
    return new Intl.DateTimeFormat('ko-KR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    }).format(new Date(datetime))
  },

  /**
   * 근속년수 계산
   */
  calculateYearsOfService: (hireDate: string, terminationDate?: string): number => {
    const start = new Date(hireDate)
    const end = terminationDate ? new Date(terminationDate) : new Date()
    return end.getFullYear() - start.getFullYear()
  },

  /**
   * 나이 계산
   */
  calculateAge: (birthDate: string): number => {
    const today = new Date()
    const birth = new Date(birthDate)
    let age = today.getFullYear() - birth.getFullYear()
    const monthDiff = today.getMonth() - birth.getMonth()
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
      age--
    }
    return age
  }
}

