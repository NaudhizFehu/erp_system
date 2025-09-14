/**
 * 직원 관리 React Query 훅
 * 직원 관련 데이터 페칭과 상태 관리를 담당합니다
 */

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import toast from 'react-hot-toast'
import { employeeApi } from '@/services/hrApi'
import type {
  Employee,
  EmployeeCreateRequest,
  EmployeeUpdateRequest,
  SearchParams,
  StatisticsData
} from '@/types/hr'

/**
 * 쿼리 키 상수
 */
export const EMPLOYEE_QUERY_KEYS = {
  all: ['employees'] as const,
  lists: () => [...EMPLOYEE_QUERY_KEYS.all, 'list'] as const,
  list: (params: SearchParams) => [...EMPLOYEE_QUERY_KEYS.lists(), params] as const,
  details: () => [...EMPLOYEE_QUERY_KEYS.all, 'detail'] as const,
  detail: (id: number) => [...EMPLOYEE_QUERY_KEYS.details(), id] as const,
  search: (searchTerm: string, params: SearchParams) => [...EMPLOYEE_QUERY_KEYS.all, 'search', searchTerm, params] as const,
  active: () => [...EMPLOYEE_QUERY_KEYS.all, 'active'] as const,
  activeByCompany: (companyId: number) => [...EMPLOYEE_QUERY_KEYS.active(), companyId] as const,
  byCompany: (companyId: number, params: SearchParams) => [...EMPLOYEE_QUERY_KEYS.all, 'company', companyId, params] as const,
  byDepartment: (departmentId: number, params: SearchParams) => [...EMPLOYEE_QUERY_KEYS.all, 'department', departmentId, params] as const,
  byHireDateRange: (startDate: string, endDate: string) => [...EMPLOYEE_QUERY_KEYS.all, 'hireDate', startDate, endDate] as const,
  byBirthday: (month: number, day: number) => [...EMPLOYEE_QUERY_KEYS.all, 'birthday', month, day] as const,
  birthdayThisMonth: () => [...EMPLOYEE_QUERY_KEYS.all, 'birthdayThisMonth'] as const,
  statistics: () => [...EMPLOYEE_QUERY_KEYS.all, 'statistics'] as const,
  check: () => [...EMPLOYEE_QUERY_KEYS.all, 'check'] as const
}

/**
 * 직원 목록 조회 훅
 */
export function useEmployees(params: SearchParams = {}) {
  return useQuery({
    queryKey: EMPLOYEE_QUERY_KEYS.list(params),
    queryFn: () => employeeApi.getEmployees(params),
    staleTime: 5 * 60 * 1000, // 5분
    retry: 3
  })
}

/**
 * 직원 상세 조회 훅
 */
export function useEmployee(id: number) {
  return useQuery({
    queryKey: EMPLOYEE_QUERY_KEYS.detail(id),
    queryFn: () => employeeApi.getEmployee(id),
    enabled: !!id,
    staleTime: 5 * 60 * 1000,
    retry: 3
  })
}

/**
 * 직원 검색 훅
 */
export function useEmployeeSearch(searchTerm: string, params: SearchParams = {}) {
  return useQuery({
    queryKey: EMPLOYEE_QUERY_KEYS.search(searchTerm, params),
    queryFn: () => employeeApi.searchEmployees(searchTerm, params),
    enabled: !!searchTerm && searchTerm.length >= 2,
    staleTime: 2 * 60 * 1000, // 2분
    retry: 2
  })
}

/**
 * 재직 중인 직원 목록 조회 훅
 */
export function useActiveEmployees() {
  return useQuery({
    queryKey: EMPLOYEE_QUERY_KEYS.active(),
    queryFn: () => employeeApi.getActiveEmployees(),
    staleTime: 10 * 60 * 1000, // 10분
    retry: 3
  })
}

/**
 * 회사별 재직 중인 직원 목록 조회 훅
 */
export function useActiveEmployeesByCompany(companyId: number) {
  return useQuery({
    queryKey: EMPLOYEE_QUERY_KEYS.activeByCompany(companyId),
    queryFn: () => employeeApi.getActiveEmployeesByCompany(companyId),
    enabled: !!companyId,
    staleTime: 10 * 60 * 1000,
    retry: 3
  })
}

/**
 * 회사별 직원 목록 조회 훅
 */
export function useEmployeesByCompany(companyId: number, params: SearchParams = {}) {
  return useQuery({
    queryKey: EMPLOYEE_QUERY_KEYS.byCompany(companyId, params),
    queryFn: () => employeeApi.getEmployeesByCompany(companyId, params),
    enabled: !!companyId,
    staleTime: 5 * 60 * 1000,
    retry: 3
  })
}

/**
 * 부서별 직원 목록 조회 훅
 */
export function useEmployeesByDepartment(departmentId: number, params: SearchParams = {}) {
  return useQuery({
    queryKey: EMPLOYEE_QUERY_KEYS.byDepartment(departmentId, params),
    queryFn: () => employeeApi.getEmployeesByDepartment(departmentId, params),
    enabled: !!departmentId,
    staleTime: 5 * 60 * 1000,
    retry: 3
  })
}

/**
 * 이번 달 생일인 직원 조회 훅
 */
export function useBirthdayEmployeesThisMonth() {
  return useQuery({
    queryKey: EMPLOYEE_QUERY_KEYS.birthdayThisMonth(),
    queryFn: () => employeeApi.getBirthdayEmployeesThisMonth(),
    staleTime: 60 * 60 * 1000, // 1시간
    retry: 3
  })
}

/**
 * 직원 통계 훅
 */
export function useEmployeeStatistics() {
  const positionStats = useQuery({
    queryKey: [...EMPLOYEE_QUERY_KEYS.statistics(), 'position'],
    queryFn: () => employeeApi.getEmployeeCountByPosition(),
    staleTime: 30 * 60 * 1000, // 30분
    retry: 3
  })

  const departmentStats = useQuery({
    queryKey: [...EMPLOYEE_QUERY_KEYS.statistics(), 'department'],
    queryFn: () => employeeApi.getEmployeeCountByDepartment(),
    staleTime: 30 * 60 * 1000,
    retry: 3
  })

  const hireYearStats = useQuery({
    queryKey: [...EMPLOYEE_QUERY_KEYS.statistics(), 'hireYear'],
    queryFn: () => employeeApi.getEmployeeCountByHireYear(),
    staleTime: 30 * 60 * 1000,
    retry: 3
  })

  const ageGroupStats = useQuery({
    queryKey: [...EMPLOYEE_QUERY_KEYS.statistics(), 'ageGroup'],
    queryFn: () => employeeApi.getEmployeeCountByAgeGroup(),
    staleTime: 30 * 60 * 1000,
    retry: 3
  })

  const genderStats = useQuery({
    queryKey: [...EMPLOYEE_QUERY_KEYS.statistics(), 'gender'],
    queryFn: () => employeeApi.getEmployeeCountByGender(),
    staleTime: 30 * 60 * 1000,
    retry: 3
  })

  return {
    positionStats,
    departmentStats,
    hireYearStats,
    ageGroupStats,
    genderStats
  }
}

/**
 * 직원 등록 뮤테이션 훅
 */
export function useCreateEmployee() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (employee: EmployeeCreateRequest) => employeeApi.createEmployee(employee),
    onSuccess: (data) => {
      // 관련 쿼리 무효화
      queryClient.invalidateQueries({ queryKey: EMPLOYEE_QUERY_KEYS.all })
      toast.success('직원이 성공적으로 등록되었습니다')
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || '직원 등록에 실패했습니다'
      toast.error(message)
    }
  })
}

/**
 * 직원 수정 뮤테이션 훅
 */
export function useUpdateEmployee() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, employee }: { id: number; employee: EmployeeUpdateRequest }) => 
      employeeApi.updateEmployee(id, employee),
    onSuccess: (data, variables) => {
      // 특정 직원 쿼리 업데이트
      queryClient.setQueryData(EMPLOYEE_QUERY_KEYS.detail(variables.id), data)
      // 관련 쿼리 무효화
      queryClient.invalidateQueries({ queryKey: EMPLOYEE_QUERY_KEYS.lists() })
      toast.success('직원 정보가 성공적으로 수정되었습니다')
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || '직원 정보 수정에 실패했습니다'
      toast.error(message)
    }
  })
}

/**
 * 직원 퇴직 처리 뮤테이션 훅
 */
export function useTerminateEmployee() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, terminationDate, reason }: { id: number; terminationDate: string; reason: string }) =>
      employeeApi.terminateEmployee(id, terminationDate, reason),
    onSuccess: (_, variables) => {
      // 관련 쿼리 무효화
      queryClient.invalidateQueries({ queryKey: EMPLOYEE_QUERY_KEYS.detail(variables.id) })
      queryClient.invalidateQueries({ queryKey: EMPLOYEE_QUERY_KEYS.lists() })
      queryClient.invalidateQueries({ queryKey: EMPLOYEE_QUERY_KEYS.active() })
      toast.success('직원 퇴직 처리가 완료되었습니다')
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || '직원 퇴직 처리에 실패했습니다'
      toast.error(message)
    }
  })
}

/**
 * 직원 복직 처리 뮤테이션 훅
 */
export function useReactivateEmployee() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => employeeApi.reactivateEmployee(id),
    onSuccess: (_, id) => {
      // 관련 쿼리 무효화
      queryClient.invalidateQueries({ queryKey: EMPLOYEE_QUERY_KEYS.detail(id) })
      queryClient.invalidateQueries({ queryKey: EMPLOYEE_QUERY_KEYS.lists() })
      queryClient.invalidateQueries({ queryKey: EMPLOYEE_QUERY_KEYS.active() })
      toast.success('직원 복직 처리가 완료되었습니다')
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || '직원 복직 처리에 실패했습니다'
      toast.error(message)
    }
  })
}

/**
 * 직원 삭제 뮤테이션 훅
 */
export function useDeleteEmployee() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => employeeApi.deleteEmployee(id),
    onSuccess: (_, id) => {
      // 관련 쿼리 무효화
      queryClient.invalidateQueries({ queryKey: EMPLOYEE_QUERY_KEYS.all })
      toast.success('직원이 성공적으로 삭제되었습니다')
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || '직원 삭제에 실패했습니다'
      toast.error(message)
    }
  })
}

/**
 * 사번 중복 확인 훅
 */
export function useCheckEmployeeNumber(employeeNumber: string, excludeId?: number) {
  return useQuery({
    queryKey: [...EMPLOYEE_QUERY_KEYS.check(), 'employeeNumber', employeeNumber, excludeId],
    queryFn: () => employeeApi.checkEmployeeNumber(employeeNumber, excludeId),
    enabled: !!employeeNumber && employeeNumber.length >= 2,
    staleTime: 0, // 실시간 검증
    retry: 1
  })
}

/**
 * 이메일 중복 확인 훅
 */
export function useCheckEmail(email: string, excludeId?: number) {
  return useQuery({
    queryKey: [...EMPLOYEE_QUERY_KEYS.check(), 'email', email, excludeId],
    queryFn: () => employeeApi.checkEmail(email, excludeId),
    enabled: !!email && email.includes('@'),
    staleTime: 0, // 실시간 검증
    retry: 1
  })
}

