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
  useEmployeeStatistics,
  useBirthdayEmployeesThisMonth,
  useEmployeeCountsByStatus,
  useHrPermissions,
  useCompanies,
  useDepartments,
  usePositions,
  useExportEmployees,
  useImportEmployees
} from '@/hooks/useEmployees'
import type { 
  Employee, 
  EmployeeCreateRequest, 
  EmployeeUpdateRequest,
  SearchParams,
  ImportResult,
  ExportFormat
} from '@/types/hr'
import { EmploymentStatus } from '@/types/hr'
import { KOREAN_LABELS } from '@/types/hr'
import toast from 'react-hot-toast'
import { useDebounce } from '@/hooks/useDebounce'
import { useAuth } from '@/contexts/AuthContext'

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
  
  // 내보내기/가져오기 상태
  const [showExportDialog, setShowExportDialog] = useState(false)
  const [showImportDialog, setShowImportDialog] = useState(false)
  const [showImportResultDialog, setShowImportResultDialog] = useState(false)
  const [importResult, setImportResult] = useState<ImportResult | null>(null)
  const [selectedCompanyForExport, setSelectedCompanyForExport] = useState<number | undefined>()
  const [selectedCompanyForImport, setSelectedCompanyForImport] = useState<number | undefined>()
  const [selectedFormat, setSelectedFormat] = useState<ExportFormat>('excel')
  
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

  // 권한 체크
  const { canView, canEdit, canManage, canCreate, canDelete } = useHrPermissions()

  // API 훅들
  const { 
    data: employeesData, 
    isLoading: isLoadingEmployees,
    refetch: refetchEmployees
  } = debouncedSearchTerm 
    ? useEmployeeSearch(debouncedSearchTerm, searchParamsObj)
    : useEmployees(searchParamsObj)
  
  // 현재 사용자 정보 가져오기
  const { user } = useAuth()

  // 통계용 데이터
  const { data: statusCounts } = useEmployeeCountsByStatus()
  const { data: birthdayEmployees } = useBirthdayEmployeesThisMonth()
  const { 
    positionStats, 
    departmentStats, 
    genderStats, 
    ageGroupStats 
  } = useEmployeeStatistics()

  // 폼용 데이터
  const { data: companies = [] } = useCompanies()
  const { data: departments = [] } = useDepartments()
  const { data: positions = [], isLoading: positionsLoading, error: positionsError } = usePositions()
  

  // 뮤테이션 훅들
  const createEmployeeMutation = useCreateEmployee()
  const updateEmployeeMutation = useUpdateEmployee()
  const deleteEmployeeMutation = useDeleteEmployee()
  const terminateEmployeeMutation = useTerminateEmployee()
  const exportMutation = useExportEmployees()
  const importMutation = useImportEmployees()

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

  // 내보내기 핸들러
  const handleExport = () => {
    setShowExportDialog(true)
  }

  const confirmExport = () => {
    exportMutation.mutate({
      format: selectedFormat,
      companyId: user?.role === 'SUPER_ADMIN' ? selectedCompanyForExport : undefined
    })
    setShowExportDialog(false)
  }

  // 가져오기 핸들러
  const handleImport = () => {
    setShowImportDialog(true)
  }

  const handleFileSelect = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0]
    if (!file) return
    
    // SUPER_ADMIN은 회사 선택 필수
    let targetCompanyId = user?.company?.id
    if (user?.role === 'SUPER_ADMIN') {
      if (!selectedCompanyForImport) {
        toast.error('가져올 회사를 선택해주세요')
        return
      }
      targetCompanyId = selectedCompanyForImport
    }
    
    if (!targetCompanyId) {
      toast.error('회사 정보가 없습니다')
      return
    }
    
    try {
      const result = await importMutation.mutateAsync({
        file,
        format: selectedFormat,
        companyId: targetCompanyId
      })
      
      setImportResult(result)
      setShowImportDialog(false)
      setShowImportResultDialog(true)
    } catch (error) {
      console.error('가져오기 오류:', error)
    }
    
    // 파일 input 초기화
    event.target.value = ''
  }

  // 통계 데이터 계산
  // 총 직원수 = ACTIVE + ON_LEAVE + INACTIVE + SUSPENDED (퇴직자 제외)
  const totalEmployees = (statusCounts?.ACTIVE || 0) + 
                         (statusCounts?.ON_LEAVE || 0) + 
                         (statusCounts?.INACTIVE || 0) + 
                         (statusCounts?.SUSPENDED || 0)

  // 근무자수 = ACTIVE만 (실제 출근하여 근무 중)
  const activeCount = statusCounts?.ACTIVE || 0

  // 퇴직자수 = TERMINATED만
  const terminatedCount = statusCounts?.TERMINATED || 0

  // 이번달 생일자수
  const birthdayCount = birthdayEmployees?.length || 0

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
          {canManage && (
            <>
              <Button variant="outline" size="sm" onClick={handleExport}>
                <Download className="mr-2 h-4 w-4" />
                내보내기
              </Button>
              <Button variant="outline" size="sm" onClick={handleImport}>
                <Upload className="mr-2 h-4 w-4" />
                가져오기
              </Button>
            </>
          )}
          {canCreate && (
            <Button size="sm" onClick={handleCreateEmployee}>
              <Plus className="mr-2 h-4 w-4" />
              직원 등록
            </Button>
          )}
        </div>
      </div>

      {/* 통계 카드 */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        {/* 총 직원 */}
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">총 직원</CardTitle>
            <Users className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{totalEmployees.toLocaleString()}</div>
            <p className="text-xs text-muted-foreground">
              재직/휴가/휴직/정직
            </p>
          </CardContent>
        </Card>

        {/* 근무자 (ACTIVE만) */}
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">근무자</CardTitle>
            <UserCheck className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-green-600">
              {activeCount.toLocaleString()}
            </div>
            <p className="text-xs text-muted-foreground">
              현재 출근 근무 중
            </p>
          </CardContent>
        </Card>

        {/* 퇴직자 */}
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">퇴직자</CardTitle>
            <UserX className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-red-600">
              {terminatedCount.toLocaleString()}
            </div>
            <p className="text-xs text-muted-foreground">
              퇴사 처리 완료
            </p>
          </CardContent>
        </Card>

        {/* 이번달 생일 */}
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">이번달 생일</CardTitle>
            <Calendar className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-blue-600">
              {birthdayCount.toLocaleString()}
            </div>
            <p className="text-xs text-muted-foreground">
              생일 축하 대상
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
                <SelectItem value={EmploymentStatus.ACTIVE}>재직</SelectItem>
                <SelectItem value={EmploymentStatus.ON_LEAVE}>휴가</SelectItem>
                <SelectItem value={EmploymentStatus.INACTIVE}>휴직</SelectItem>
                <SelectItem value={EmploymentStatus.SUSPENDED}>정직</SelectItem>
                <SelectItem value={EmploymentStatus.TERMINATED}>퇴직</SelectItem>
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
              onEdit={canEdit ? handleEditEmployee : undefined}
              onView={canView ? handleViewEmployee : undefined}
              onDelete={canDelete ? handleDeleteEmployee : undefined}
              onTerminate={canEdit ? handleTerminateEmployee : undefined}
              onSelectionChange={setSelectedEmployees}
              showSelection={canManage}
              showActions={canEdit || canDelete}
            />
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
              {(employeesData?.content || []).map((employee) => (
                <EmployeeCard
                  key={employee.id}
                  employee={employee}
                  onEdit={canEdit ? handleEditEmployee : undefined}
                  onView={canView ? handleViewEmployee : undefined}
                  onDelete={canDelete ? handleDeleteEmployee : undefined}
                  onTerminate={canEdit ? handleTerminateEmployee : undefined}
                  showActions={canEdit || canDelete}
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
            companies={companies}
            departments={departments}
            positions={positions}
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

      {/* 내보내기 다이얼로그 */}
      <Dialog open={showExportDialog} onOpenChange={setShowExportDialog}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>직원 데이터 내보내기</DialogTitle>
            <DialogDescription>
              내보낼 파일 형식을 선택하세요
            </DialogDescription>
          </DialogHeader>
          
          <div className="space-y-4">
            {/* 형식 선택 */}
            <div>
              <label className="text-sm font-medium mb-2 block">파일 형식</label>
              <Select value={selectedFormat} onValueChange={(value) => setSelectedFormat(value as ExportFormat)}>
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="excel">Excel (.xlsx)</SelectItem>
                  <SelectItem value="csv">CSV (.csv)</SelectItem>
                </SelectContent>
              </Select>
            </div>

            {/* SUPER_ADMIN만 회사 선택 */}
            {user?.role === 'SUPER_ADMIN' && (
              <div>
                <label className="text-sm font-medium mb-2 block">회사 선택</label>
                <Select 
                  value={selectedCompanyForExport?.toString()} 
                  onValueChange={(value) => setSelectedCompanyForExport(value === 'all' ? undefined : Number(value))}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="전체 회사" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="all">전체 회사</SelectItem>
                    {companies.map((company) => (
                      <SelectItem key={company.id} value={company.id.toString()}>
                        {company.name}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
            )}
          </div>

          <div className="flex justify-end gap-2 mt-4">
            <Button variant="outline" onClick={() => setShowExportDialog(false)}>
              취소
            </Button>
            <Button onClick={confirmExport} disabled={exportMutation.isPending}>
              내보내기
            </Button>
          </div>
        </DialogContent>
      </Dialog>

      {/* 가져오기 다이얼로그 */}
      <Dialog open={showImportDialog} onOpenChange={setShowImportDialog}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>직원 데이터 가져오기</DialogTitle>
            <DialogDescription>
              가져올 파일을 선택하세요
            </DialogDescription>
          </DialogHeader>
          
          <div className="space-y-4">
            {/* 형식 선택 */}
            <div>
              <label className="text-sm font-medium mb-2 block">파일 형식</label>
              <Select value={selectedFormat} onValueChange={(value) => setSelectedFormat(value as ExportFormat)}>
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="excel">Excel (.xlsx)</SelectItem>
                  <SelectItem value="csv">CSV (.csv)</SelectItem>
                </SelectContent>
              </Select>
            </div>

            {/* SUPER_ADMIN은 회사 선택 필수 */}
            {user?.role === 'SUPER_ADMIN' && (
              <div>
                <label className="text-sm font-medium mb-2 block">
                  가져올 회사 <span className="text-red-500">*</span>
                </label>
                <Select 
                  value={selectedCompanyForImport?.toString()} 
                  onValueChange={(value) => setSelectedCompanyForImport(Number(value))}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="회사를 선택하세요" />
                  </SelectTrigger>
                  <SelectContent>
                    {companies.map((company) => (
                      <SelectItem key={company.id} value={company.id.toString()}>
                        {company.name}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
            )}

            {/* 파일 선택 */}
            <div>
              <label className="text-sm font-medium mb-2 block">파일 선택</label>
              <Input
                type="file"
                accept={selectedFormat === 'excel' ? '.xlsx' : '.csv'}
                onChange={handleFileSelect}
              />
            </div>
          </div>

          <div className="flex justify-end mt-4">
            <Button variant="outline" onClick={() => setShowImportDialog(false)}>
              취소
            </Button>
          </div>
        </DialogContent>
      </Dialog>

      {/* 가져오기 결과 다이얼로그 */}
      <Dialog open={showImportResultDialog} onOpenChange={setShowImportResultDialog}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>가져오기 결과</DialogTitle>
            <DialogDescription>
              데이터 가져오기 결과를 확인하세요
            </DialogDescription>
          </DialogHeader>
          
          {importResult && (
            <div className="space-y-4">
              <div className="grid grid-cols-3 gap-4">
                <div>
                  <p className="text-sm text-muted-foreground">전체 행</p>
                  <p className="text-2xl font-bold">{importResult.totalRows}</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">성공</p>
                  <p className="text-2xl font-bold text-green-600">{importResult.successCount}</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">실패</p>
                  <p className="text-2xl font-bold text-red-600">{importResult.failCount}</p>
                </div>
              </div>

              {importResult.errors.length > 0 && (
                <div>
                  <p className="text-sm font-medium mb-2">에러 목록</p>
                  <div className="max-h-48 overflow-y-auto border rounded p-2">
                    {importResult.errors.map((error, index) => (
                      <p key={index} className="text-sm text-red-600 py-1">
                        {error}
                      </p>
                    ))}
                  </div>
                </div>
              )}
            </div>
          )}

          <div className="flex justify-end mt-4">
            <Button onClick={() => setShowImportResultDialog(false)}>
              확인
            </Button>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  )
}

