import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { Plus, Search, Filter, Edit, Trash2 } from 'lucide-react'
import { Employee, EmploymentStatus } from '@/types/hr'
import { formatDate, formatCurrency } from '@/lib/utils'
import { useEmployees } from '@/hooks/useEmployees'
import { useAuth } from '@/contexts/AuthContext'

/**
 * 직원 목록 페이지
 * 직원 정보를 조회하고 관리하는 페이지입니다
 */
function EmployeeList() {
  const navigate = useNavigate()
  const { user } = useAuth()
  const [searchTerm, setSearchTerm] = useState('')
  const [statusFilter, setStatusFilter] = useState<EmploymentStatus | ''>('')

  // API를 통해 직원 목록 조회
  const { data: employeesData, isLoading, error } = useEmployees()
  const employees = employeesData?.content || []

  /**
   * 직원 상세 페이지로 이동
   */
  const handleRowClick = (employeeId: number, event: React.MouseEvent) => {
    // 액션 버튼 클릭 시에는 상세 페이지로 이동하지 않음
    const target = event.target as HTMLElement
    if (target.closest('button') || target.closest('a')) {
      return
    }
    navigate(`/hr/employees/${employeeId}`)
  }

  const filteredEmployees = employees.filter(employee => {
    const matchesSearch = employee.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         employee.employeeNumber.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         employee.email.toLowerCase().includes(searchTerm.toLowerCase())
    
    const matchesStatus = !statusFilter || employee.employmentStatus === statusFilter
    
    return matchesSearch && matchesStatus
  })

  // 로딩 상태 처리
  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto mb-4"></div>
          <p className="text-muted-foreground">직원 목록을 불러오는 중...</p>
        </div>
      </div>
    )
  }

  // 에러 상태 처리
  if (error) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <p className="text-destructive mb-4">직원 목록을 불러올 수 없습니다.</p>
          <button 
            onClick={() => window.location.reload()} 
            className="btn btn-primary"
          >
            다시 시도
          </button>
        </div>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      {/* 페이지 헤더 */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-foreground">직원 관리</h1>
          <p className="text-muted-foreground">
            직원 정보를 조회하고 관리할 수 있습니다
          </p>
        </div>
        {(user?.role === 'SUPER_ADMIN' || user?.role === 'ADMIN' || user?.role === 'MANAGER') && (
          <Link to="/hr/employees/new" className="btn btn-primary">
            <Plus className="mr-2 h-4 w-4" />
            직원 등록
          </Link>
        )}
      </div>

      {/* 검색 및 필터 */}
      <div className="flex items-center space-x-4">
        <div className="relative flex-1 max-w-sm">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <input
            type="text"
            placeholder="이름, 직원번호, 이메일로 검색..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="form-input pl-10 w-full"
          />
        </div>
        
        <div className="flex items-center space-x-2">
          <Filter className="h-4 w-4 text-muted-foreground" />
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value as EmploymentStatus | '')}
            className="form-input"
          >
            <option value="">전체 상태</option>
            <option value={EmploymentStatus.ACTIVE}>재직</option>
            <option value={EmploymentStatus.INACTIVE}>휴직</option>
            <option value={EmploymentStatus.TERMINATED}>퇴사</option>
          </select>
        </div>
      </div>

      {/* 직원 목록 테이블 */}
      <div className="table-container">
        <table className="table">
          <thead>
            <tr>
              <th>직원번호</th>
              <th>이름</th>
              <th>이메일</th>
              <th>전화번호</th>
              <th>부서</th>
              <th>직급</th>
              <th>급여</th>
              <th>상태</th>
              <th>입사일</th>
              <th>액션</th>
            </tr>
          </thead>
          <tbody>
            {filteredEmployees.map((employee) => (
              <tr 
                key={employee.id}
                onClick={(e) => handleRowClick(employee.id, e)}
                className="cursor-pointer hover:bg-muted/50 transition-colors"
              >
                <td className="font-medium">{employee.employeeNumber}</td>
                <td className="font-medium">{employee.name}</td>
                <td>{employee.email}</td>
                <td>{employee.phone || '-'}</td>
                <td>{employee.department?.name || '-'}</td>
                <td>{employee.position?.name || '-'}</td>
                <td>-</td>
                <td>
                  <span
                    className={`inline-flex items-center justify-center rounded-full px-2 py-1 text-xs font-medium w-16 ${
                      employee.employmentStatus === EmploymentStatus.ACTIVE
                        ? 'bg-green-100 text-green-800'
                        : employee.employmentStatus === EmploymentStatus.INACTIVE
                        ? 'bg-yellow-100 text-yellow-800'
                        : 'bg-red-100 text-red-800'
                    }`}
                  >
                    {employee.employmentStatus === EmploymentStatus.ACTIVE ? '재직' :
                     employee.employmentStatus === EmploymentStatus.INACTIVE ? '휴직' : '퇴사'}
                  </span>
                </td>
                <td>{formatDate(employee.hireDate)}</td>
                <td>
                  <div className="flex items-center space-x-2">
                    <Link
                      to={`/hr/employees/${employee.id}/edit`}
                      className="p-1 text-muted-foreground hover:text-foreground"
                    >
                      <Edit className="h-4 w-4" />
                    </Link>
                    <button className="p-1 text-muted-foreground hover:text-destructive">
                      <Trash2 className="h-4 w-4" />
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* 결과 없음 */}
      {filteredEmployees.length === 0 && (
        <div className="text-center py-12">
          <p className="text-muted-foreground">검색 조건에 맞는 직원이 없습니다.</p>
        </div>
      )}
    </div>
  )
}

export { EmployeeList }





