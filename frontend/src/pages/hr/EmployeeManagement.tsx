/**
 * 직원 관리 메인 페이지
 * 직원 목록 조회, 검색, 등록, 수정, 삭제 등의 기능을 제공합니다
 */

import { useState, useCallback } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog'
import { 
  Search, 
  Plus, 
  Filter, 
  Download, 
  Upload,
  Users,
  UserCheck,
  UserX,
  Calendar,
  BarChart3,
  Grid3X3,
  List,
  RefreshCw
} from 'lucide-react'
import { EmployeeTable } from '@/components/hr/EmployeeTable'
import { EmployeeCard } from '@/components/hr/EmployeeCard'
import { EmployeeForm } from '@/components/hr/EmployeeForm'
import {
  useEmployees,
  useEmployeeSearch,
  useActiveEmployees,
  useCreateEmployee,
  useUpdateEmployee,
  useDeleteEmployee,
  useTerminateEmployee,
  useEmployeeStatistics
} from '@/hooks/useEmployees'
import type { 
  Employee, 
  EmployeeCreateRequest, 
  EmployeeUpdateRequest,
  SearchParams
} from '@/types/hr'
import { EmploymentStatus } from '@/types/hr'
import { KOREAN_LABELS } from '@/types/hr'
import toast from 'react-hot-toast'
import { useDebounce } from '@/hooks/useDebounce'

/**
 * 직원 관리 페이지 컴포넌트
 */
export function EmployeeManagement() {
  const navigate = useNavigate()
  const [searchParams, setSearchParams] = useSearchParams()
  
  // 상태 관리
  const [viewMode, setViewMode] = useState<'table' | 'grid'>('table')
  const [searchTerm, setSearchTerm] = useState('')
  const [statusFilter, setStatusFilter] = useState<EmploymentStatus | 'all'>('all')
  const [selectedEmployees, setSelectedEmployees] = useState<number[]>([])
  const [showForm, setShowForm] = useState(false)
  const [showDeleteDialog, setShowDeleteDialog] = useState(false)
  const [showTerminateDialog, setShowTerminateDialog] = useState(false)
  const [selectedEmployee, setSelectedEmployee] = useState<Employee | null>(null)
  const [formMode, setFormMode] = useState<'create' | 'edit'>('create')
  
  // 검색어 디바운싱
  const debouncedSearchTerm = useDebounce(searchTerm, 300)
  
  // 페이지네이션
  const currentPage = Number(searchParams.get('page')) || 0
  const pageSize = Number(searchParams.get('size')) || 20

  // 검색 파라미터 구성
  const searchParamsObj: SearchParams = {
    page: currentPage,
    size: pageSize,
    employmentStatus: statusFilter !== 'all' ? statusFilter : undefined
  }

  // API 훅들
  const { 
    data: employeesData, 
    isLoading: isLoadingEmployees,
    refetch: refetchEmployees
  } = debouncedSearchTerm 
    ? useEmployeeSearch(debouncedSearchTerm, searchParamsObj)
    : useEmployees(searchParamsObj)

  const { data: activeEmployees } = useActiveEmployees()
  const { 
    positionStats, 
    departmentStats, 
    genderStats, 
    ageGroupStats 
  } = useEmployeeStatistics()

  // 뮤테이션 훅들
  const createEmployeeMutation = useCreateEmployee()
  const updateEmployeeMutation = useUpdateEmployee()
  const deleteEmployeeMutation = useDeleteEmployee()
  const terminateEmployeeMutation = useTerminateEmployee()

  // 검색 처리
  const handleSearch = useCallback((value: string) => {
    setSearchTerm(value)
    setSearchParams(prev => {
      const newParams = new URLSearchParams(prev)
      newParams.set('page', '0') // 검색 시 첫 페이지로
      return newParams
    })
  }, [setSearchParams])

  // 상태 필터 변경
  const handleStatusFilter = (status: EmploymentStatus | 'all') => {
    setStatusFilter(status)
    setSearchParams(prev => {
      const newParams = new URLSearchParams(prev)
      newParams.set('page', '0')
      if (status !== 'all') {
        newParams.set('status', status)
      } else {
        newParams.delete('status')
      }
      return newParams
    })
  }

  // 페이지 변경
  const handlePageChange = (page: number) => {
    setSearchParams(prev => {
      const newParams = new URLSearchParams(prev)
      newParams.set('page', page.toString())
      return newParams
    })
  }

  // 직원 등록 모달 열기
  const handleCreateEmployee = () => {
    setSelectedEmployee(null)
    setFormMode('create')
    setShowForm(true)
  }

  // 직원 수정 모달 열기
  const handleEditEmployee = (employee: Employee) => {
    setSelectedEmployee(employee)
    setFormMode('edit')
    setShowForm(true)
  }

  // 직원 상세보기
  const handleViewEmployee = (employee: Employee) => {
    navigate(`/hr/employees/${employee.id}`)
  }

  // 직원 삭제 확인
  const handleDeleteEmployee = (employee: Employee) => {
    setSelectedEmployee(employee)
    setShowDeleteDialog(true)
  }

  // 직원 퇴직 확인
  const handleTerminateEmployee = (employee: Employee) => {
    setSelectedEmployee(employee)
    setShowTerminateDialog(true)
  }

  // 폼 제출 처리
  const handleFormSubmit = async (data: EmployeeCreateRequest | EmployeeUpdateRequest) => {
    try {
      if (formMode === 'create') {
        await createEmployeeMutation.mutateAsync(data as EmployeeCreateRequest)
      } else if (selectedEmployee) {
        await updateEmployeeMutation.mutateAsync({
          id: selectedEmployee.id,
          employee: data as EmployeeUpdateRequest
        })
      }
      setShowForm(false)
      refetchEmployees()
    } catch (error) {
      console.error('폼 제출 오류:', error)
    }
  }

  // 삭제 확인
  const confirmDelete = async () => {
    if (!selectedEmployee) return
    
    try {
      await deleteEmployeeMutation.mutateAsync(selectedEmployee.id)
      setShowDeleteDialog(false)
      refetchEmployees()
    } catch (error) {
      console.error('삭제 오류:', error)
    }
  }

  // 퇴직 확인
  const confirmTerminate = async () => {
    if (!selectedEmployee) return
    
    try {
      await terminateEmployeeMutation.mutateAsync({
        id: selectedEmployee.id,
        terminationDate: new Date().toISOString().split('T')[0],
        reason: '퇴직'
      })
      setShowTerminateDialog(false)
      refetchEmployees()
    } catch (error) {
      console.error('퇴직 처리 오류:', error)
    }
  }

  // 통계 데이터 계산
  const totalEmployees = employeesData?.totalElements || 0
  const activeCount = activeEmployees?.length || 0
  const inactiveCount = totalEmployees - activeCount

  return (
    <div className="space-y-6">
      {/* 헤더 */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">직원 관리</h1>
          <p className="text-muted-foreground">
            직원 정보를 관리하고 조회할 수 있습니다
          </p>
        </div>
        <div className="flex items-center gap-2">
          <Button variant="outline" size="sm">
            <Download className="mr-2 h-4 w-4" />
            내보내기
          </Button>
          <Button variant="outline" size="sm">
            <Upload className="mr-2 h-4 w-4" />
            가져오기
          </Button>
          <Button onClick={handleCreateEmployee}>
            <Plus className="mr-2 h-4 w-4" />
            직원 등록
          </Button>
        </div>
      </div>

      {/* 통계 카드 */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">전체 직원</CardTitle>
            <Users className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{totalEmployees.toLocaleString()}</div>
            <p className="text-xs text-muted-foreground">
              등록된 전체 직원 수
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">재직자</CardTitle>
            <UserCheck className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-green-600">
              {activeCount.toLocaleString()}
            </div>
            <p className="text-xs text-muted-foreground">
              현재 재직 중인 직원
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">퇴직자</CardTitle>
            <UserX className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-red-600">
              {inactiveCount.toLocaleString()}
            </div>
            <p className="text-xs text-muted-foreground">
              퇴직한 직원 수
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">이번 달 생일</CardTitle>
            <Calendar className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-blue-600">12</div>
            <p className="text-xs text-muted-foreground">
              이번 달 생일인 직원
            </p>
          </CardContent>
        </Card>
      </div>

      {/* 필터 및 검색 */}
      <Card>
        <CardContent className="pt-6">
          <div className="flex flex-col sm:flex-row gap-4">
            {/* 검색 */}
            <div className="flex-1">
              <div className="relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground h-4 w-4" />
                <Input
                  placeholder="이름, 사번, 이메일로 검색..."
                  value={searchTerm}
                  onChange={(e) => handleSearch(e.target.value)}
                  className="pl-10"
                />
              </div>
            </div>

            {/* 상태 필터 */}
            <Select value={statusFilter} onValueChange={handleStatusFilter}>
              <SelectTrigger className="w-[180px]">
                <SelectValue placeholder="상태 필터" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">전체</SelectItem>
                <SelectItem value="ACTIVE">재직</SelectItem>
                <SelectItem value="ON_LEAVE">휴직</SelectItem>
                <SelectItem value="TERMINATED">퇴직</SelectItem>
                <SelectItem value="SUSPENDED">정직</SelectItem>
              </SelectContent>
            </Select>

            {/* 보기 모드 전환 */}
            <div className="flex items-center gap-2">
              <Button
                variant={viewMode === 'table' ? 'default' : 'outline'}
                size="sm"
                onClick={() => setViewMode('table')}
              >
                <List className="h-4 w-4" />
              </Button>
              <Button
                variant={viewMode === 'grid' ? 'default' : 'outline'}
                size="sm"
                onClick={() => setViewMode('grid')}
              >
                <Grid3X3 className="h-4 w-4" />
              </Button>
            </div>

            {/* 새로고침 */}
            <Button 
              variant="outline" 
              size="sm"
              onClick={() => refetchEmployees()}
              disabled={isLoadingEmployees}
            >
              <RefreshCw className={`h-4 w-4 ${isLoadingEmployees ? 'animate-spin' : ''}`} />
            </Button>
          </div>
        </CardContent>
      </Card>

      {/* 직원 목록 */}
      <Card>
        <CardHeader>
          <div className="flex justify-between items-center">
            <CardTitle>직원 목록</CardTitle>
            {employeesData && (
              <Badge variant="secondary">
                총 {employeesData.totalElements.toLocaleString()}명
              </Badge>
            )}
          </div>
        </CardHeader>
        <CardContent>
          {viewMode === 'table' ? (
            <EmployeeTable
              employees={employeesData?.content || []}
              loading={isLoadingEmployees}
              onEdit={handleEditEmployee}
              onView={handleViewEmployee}
              onDelete={handleDeleteEmployee}
              onTerminate={handleTerminateEmployee}
              onSelectionChange={setSelectedEmployees}
              showSelection={true}
              showActions={true}
            />
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
              {(employeesData?.content || []).map((employee) => (
                <EmployeeCard
                  key={employee.id}
                  employee={employee}
                  onEdit={handleEditEmployee}
                  onView={handleViewEmployee}
                  onDelete={handleDeleteEmployee}
                  onTerminate={handleTerminateEmployee}
                  showActions={true}
                />
              ))}
            </div>
          )}

          {/* 페이지네이션 */}
          {employeesData && employeesData.totalPages > 1 && (
            <div className="flex justify-center mt-6">
              <div className="flex items-center space-x-2">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => handlePageChange(currentPage - 1)}
                  disabled={currentPage === 0}
                >
                  이전
                </Button>
                
                <div className="flex items-center space-x-1">
                  {Array.from({ length: Math.min(5, employeesData.totalPages) }, (_, i) => {
                    const pageNum = currentPage < 3 ? i : 
                                   currentPage >= employeesData.totalPages - 2 ? 
                                   employeesData.totalPages - 5 + i : 
                                   currentPage - 2 + i
                    
                    if (pageNum < 0 || pageNum >= employeesData.totalPages) return null
                    
                    return (
                      <Button
                        key={pageNum}
                        variant={currentPage === pageNum ? 'default' : 'outline'}
                        size="sm"
                        onClick={() => handlePageChange(pageNum)}
                      >
                        {pageNum + 1}
                      </Button>
                    )
                  })}
                </div>

                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => handlePageChange(currentPage + 1)}
                  disabled={currentPage >= employeesData.totalPages - 1}
                >
                  다음
                </Button>
              </div>
            </div>
          )}
        </CardContent>
      </Card>

      {/* 직원 등록/수정 모달 */}
      <Dialog open={showForm} onOpenChange={setShowForm}>
        <DialogContent className="max-w-4xl max-h-[90vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>
              {formMode === 'create' ? '새 직원 등록' : '직원 정보 수정'}
            </DialogTitle>
            <DialogDescription>
              {formMode === 'create' 
                ? '새로운 직원의 정보를 입력해주세요.' 
                : '수정할 직원 정보를 입력해주세요.'
              }
            </DialogDescription>
          </DialogHeader>
          
          <EmployeeForm
            employee={selectedEmployee || undefined}
            companies={[]} // TODO: 회사 목록 API 연동
            departments={[]} // TODO: 부서 목록 API 연동  
            positions={[]} // TODO: 직급 목록 API 연동
            onSubmit={handleFormSubmit}
            onCancel={() => setShowForm(false)}
            loading={createEmployeeMutation.isPending || updateEmployeeMutation.isPending}
            mode={formMode}
          />
        </DialogContent>
      </Dialog>

      {/* 삭제 확인 다이얼로그 */}
      <AlertDialog open={showDeleteDialog} onOpenChange={setShowDeleteDialog}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>직원 삭제</AlertDialogTitle>
            <AlertDialogDescription>
              {selectedEmployee?.name}님의 정보를 삭제하시겠습니까?
              <br />
              이 작업은 되돌릴 수 없습니다.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>취소</AlertDialogCancel>
            <AlertDialogAction 
              onClick={confirmDelete}
              className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
            >
              삭제
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>

      {/* 퇴직 확인 다이얼로그 */}
      <AlertDialog open={showTerminateDialog} onOpenChange={setShowTerminateDialog}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>직원 퇴직 처리</AlertDialogTitle>
            <AlertDialogDescription>
              {selectedEmployee?.name}님을 퇴직 처리하시겠습니까?
              <br />
              퇴직일은 오늘 날짜로 설정됩니다.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>취소</AlertDialogCancel>
            <AlertDialogAction 
              onClick={confirmTerminate}
              className="bg-orange-600 text-white hover:bg-orange-700"
            >
              퇴직 처리
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  )
}

