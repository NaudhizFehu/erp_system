/**
 * HR 모듈 Mock API 서비스
 * 백엔드 서버 없이 프론트엔드 테스트를 위한 Mock API
 */

import type {
  PageResponse,
  Employee,
  Position,
  EmployeeCreateRequest,
  EmployeeUpdateRequest,
  SearchParams,
  StatisticsData
} from '@/types/hr'
import { EmploymentStatus } from '@/types/hr'
import {
  mockEmployees,
  mockPositions,
  mockDepartments,
  mockCompanies,
  mockPositionStats,
  mockDepartmentStats,
  mockGenderStats,
  mockAgeGroupStats,
  createMockPageResponse
} from '@/mocks/hrMockData'

// Mock API 서비스
export const mockEmployeeApi = {
  /**
   * 직원 목록 조회 (페이징)
   */
  getEmployees: async (params: SearchParams = {}): Promise<PageResponse<Employee>> => {
    // 실제 API 호출을 시뮬레이션하기 위한 지연
    await new Promise(resolve => setTimeout(resolve, 500))
    
    let filteredEmployees = [...mockEmployees]
    
    // 상태 필터링
    if (params.employmentStatus && params.employmentStatus !== 'all') {
      filteredEmployees = filteredEmployees.filter(
        emp => emp.employmentStatus === params.employmentStatus
      )
    }
    
    // 검색어 필터링
    if (params.searchTerm) {
      const searchTerm = params.searchTerm.toLowerCase()
      filteredEmployees = filteredEmployees.filter(emp =>
        emp.name.toLowerCase().includes(searchTerm) ||
        emp.employeeNumber.toLowerCase().includes(searchTerm) ||
        emp.email.toLowerCase().includes(searchTerm)
      )
    }
    
    return createMockPageResponse(
      filteredEmployees,
      params.page || 0,
      params.size || 20
    )
  },

  /**
   * 직원 상세 조회
   */
  getEmployee: async (id: number): Promise<Employee> => {
    await new Promise(resolve => setTimeout(resolve, 300))
    const employee = mockEmployees.find(emp => emp.id === id)
    if (!employee) {
      throw new Error('직원을 찾을 수 없습니다')
    }
    return employee
  },

  /**
   * 사번으로 직원 조회
   */
  getEmployeeByNumber: async (employeeNumber: string): Promise<Employee> => {
    await new Promise(resolve => setTimeout(resolve, 300))
    const employee = mockEmployees.find(emp => emp.employeeNumber === employeeNumber)
    if (!employee) {
      throw new Error('직원을 찾을 수 없습니다')
    }
    return employee
  },

  /**
   * 이메일로 직원 조회
   */
  getEmployeeByEmail: async (email: string): Promise<Employee> => {
    await new Promise(resolve => setTimeout(resolve, 300))
    const employee = mockEmployees.find(emp => emp.email === email)
    if (!employee) {
      throw new Error('직원을 찾을 수 없습니다')
    }
    return employee
  },

  /**
   * 직원 검색
   */
  searchEmployees: async (searchTerm: string, params: SearchParams = {}): Promise<PageResponse<Employee>> => {
    await new Promise(resolve => setTimeout(resolve, 500))
    
    const filteredEmployees = mockEmployees.filter(emp => {
      const term = searchTerm.toLowerCase()
      return emp.name.toLowerCase().includes(term) ||
             emp.employeeNumber.toLowerCase().includes(term) ||
             emp.email.toLowerCase().includes(term) ||
             emp.department.name.toLowerCase().includes(term) ||
             emp.position.name.toLowerCase().includes(term)
    })
    
    return createMockPageResponse(
      filteredEmployees,
      params.page || 0,
      params.size || 20
    )
  },

  /**
   * 회사별 직원 목록 조회
   */
  getEmployeesByCompany: async (companyId: number, params: SearchParams = {}): Promise<PageResponse<Employee>> => {
    await new Promise(resolve => setTimeout(resolve, 500))
    
    const filteredEmployees = mockEmployees.filter(emp => emp.company.id === companyId)
    
    return createMockPageResponse(
      filteredEmployees,
      params.page || 0,
      params.size || 20
    )
  },

  /**
   * 부서별 직원 목록 조회
   */
  getEmployeesByDepartment: async (departmentId: number, params: SearchParams = {}): Promise<PageResponse<Employee>> => {
    await new Promise(resolve => setTimeout(resolve, 500))
    
    const filteredEmployees = mockEmployees.filter(emp => emp.department.id === departmentId)
    
    return createMockPageResponse(
      filteredEmployees,
      params.page || 0,
      params.size || 20
    )
  },

  /**
   * 재직 중인 직원 목록 조회
   */
  getActiveEmployees: async (): Promise<Employee[]> => {
    await new Promise(resolve => setTimeout(resolve, 300))
    return mockEmployees.filter(emp => emp.employmentStatus === EmploymentStatus.ACTIVE)
  },

  /**
   * 회사별 재직 중인 직원 목록 조회
   */
  getActiveEmployeesByCompany: async (companyId: number): Promise<Employee[]> => {
    await new Promise(resolve => setTimeout(resolve, 300))
    return mockEmployees.filter(
      emp => emp.company.id === companyId && emp.employmentStatus === EmploymentStatus.ACTIVE
    )
  },

  /**
   * 직원 등록
   */
  createEmployee: async (employee: EmployeeCreateRequest): Promise<Employee> => {
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const newEmployee: Employee = {
      id: mockEmployees.length + 1,
      employeeNumber: employee.employeeNumber,
      name: employee.name,
      nameEn: employee.nameEn || '',
      email: employee.email,
      phone: employee.phone || '',
      mobile: employee.mobile || '',
      birthDate: employee.birthDate?.toString().split('T')[0] || '',
      gender: employee.gender,
      address: employee.address || '',
      addressDetail: employee.addressDetail || '',
      postalCode: employee.postalCode || '',
      company: mockCompanies[0],
      department: mockDepartments.find(d => d.id === employee.departmentId)!,
      position: mockPositions.find(p => p.id === employee.positionId)!,
      hireDate: employee.hireDate.toString().split('T')[0],
      employmentStatus: employee.employmentStatus || EmploymentStatus.ACTIVE,
      employmentType: employee.employmentType || 'FULL_TIME' as any,
      baseSalary: employee.baseSalary || 0,
      bankName: employee.bankName || '',
      accountNumber: employee.accountNumber || '',
      accountHolder: employee.accountHolder || '',
      emergencyContact: employee.emergencyContact || '',
      emergencyRelation: employee.emergencyRelation || '',
      education: employee.education || '',
      major: employee.major || '',
      career: employee.career || '',
      skills: employee.skills || '',
      certifications: employee.certifications || '',
      memo: employee.memo || '',
      profileImageUrl: employee.profileImageUrl || '',
      terminationDate: null,
      yearsOfService: Math.floor(Math.random() * 10) + 1,
      age: Math.floor(Math.random() * 20) + 25,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    }
    
    mockEmployees.push(newEmployee)
    return newEmployee
  },

  /**
   * 직원 정보 수정
   */
  updateEmployee: async (id: number, employee: EmployeeUpdateRequest): Promise<Employee> => {
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const index = mockEmployees.findIndex(emp => emp.id === id)
    if (index === -1) {
      throw new Error('직원을 찾을 수 없습니다')
    }
    
    const updatedEmployee = {
      ...mockEmployees[index],
      ...employee,
      updatedAt: new Date().toISOString()
    }
    
    mockEmployees[index] = updatedEmployee
    return updatedEmployee
  },

  /**
   * 직원 삭제 (소프트 삭제)
   */
  deleteEmployee: async (id: number): Promise<void> => {
    await new Promise(resolve => setTimeout(resolve, 500))
    
    const index = mockEmployees.findIndex(emp => emp.id === id)
    if (index === -1) {
      throw new Error('직원을 찾을 수 없습니다')
    }
    
    mockEmployees.splice(index, 1)
  },

  /**
   * 사번 중복 확인
   */
  checkEmployeeNumber: async (employeeNumber: string, excludeId?: number): Promise<boolean> => {
    await new Promise(resolve => setTimeout(resolve, 300))
    
    return mockEmployees.some(emp => 
      emp.employeeNumber === employeeNumber && emp.id !== excludeId
    )
  },

  /**
   * 이메일 중복 확인
   */
  checkEmail: async (email: string, excludeId?: number): Promise<boolean> => {
    await new Promise(resolve => setTimeout(resolve, 300))
    
    return mockEmployees.some(emp => 
      emp.email === email && emp.id !== excludeId
    )
  },

  /**
   * 직급별 직원 수 통계
   */
  getEmployeeCountByPosition: async (): Promise<StatisticsData[]> => {
    await new Promise(resolve => setTimeout(resolve, 300))
    return mockPositionStats
  },

  /**
   * 부서별 직원 수 통계
   */
  getEmployeeCountByDepartment: async (): Promise<StatisticsData[]> => {
    await new Promise(resolve => setTimeout(resolve, 300))
    return mockDepartmentStats
  },

  /**
   * 성별 직원 수 통계
   */
  getEmployeeCountByGender: async (): Promise<StatisticsData[]> => {
    await new Promise(resolve => setTimeout(resolve, 300))
    return mockGenderStats
  },

  /**
   * 연령대별 직원 수 통계
   */
  getEmployeeCountByAgeGroup: async (): Promise<StatisticsData[]> => {
    await new Promise(resolve => setTimeout(resolve, 300))
    return mockAgeGroupStats
  },

  /**
   * 회사별 최근 직원 목록 조회 (사번 중복 방지용)
   */
  getRecentEmployeesByCompany: async (companyId: number): Promise<Employee[]> => {
    await new Promise(resolve => setTimeout(resolve, 300))
    return mockEmployees
      .filter(emp => emp.company.id === companyId)
      .sort((a, b) => b.employeeNumber.localeCompare(a.employeeNumber))
      .slice(0, 5)
  }
}

export const mockPositionApi = {
  /**
   * 직급 목록 조회
   */
  getPositions: async (params: SearchParams = {}): Promise<PageResponse<Position>> => {
    await new Promise(resolve => setTimeout(resolve, 300))
    return createMockPageResponse(mockPositions, params.page || 0, params.size || 20)
  },

  /**
   * 직급 상세 조회
   */
  getPosition: async (id: number): Promise<Position> => {
    await new Promise(resolve => setTimeout(resolve, 300))
    const position = mockPositions.find(pos => pos.id === id)
    if (!position) {
      throw new Error('직급을 찾을 수 없습니다')
    }
    return position
  },

  /**
   * 회사별 직급 목록 조회
   */
  getPositionsByCompany: async (companyId: number): Promise<Position[]> => {
    await new Promise(resolve => setTimeout(resolve, 300))
    return mockPositions.filter(pos => pos.company.id === companyId)
  },

  /**
   * 활성 직급 목록 조회
   */
  getActivePositions: async (): Promise<Position[]> => {
    await new Promise(resolve => setTimeout(resolve, 300))
    return mockPositions.filter(pos => pos.isActive)
  }
}



