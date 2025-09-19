import { useState } from 'react'
import { Link } from 'react-router-dom'
import { Plus, Search, Filter, Edit, Trash2 } from 'lucide-react'
import { Employee, EmployeeStatus, EmployeeStatusLabels } from '@/types/hr'
import { formatDate, formatCurrency } from '@/lib/utils'

/**
 * 직원 목록 페이지
 * 직원 정보를 조회하고 관리하는 페이지입니다
 */
function EmployeeList() {
  const [searchTerm, setSearchTerm] = useState('')
  const [statusFilter, setStatusFilter] = useState<EmployeeStatus | ''>('')

  // 임시 데이터 (실제로는 API에서 가져옴)
  const employees: Employee[] = [
    {
      id: 1,
      employeeNumber: 'EMP001',
      name: '김철수',
      email: 'kim@company.com',
      phone: '010-1234-5678',
      hireDate: '2023-01-15',
      department: {
        id: 1,
        departmentCode: 'DEV',
        name: '개발팀',
        createdAt: '2023-01-01',
        updatedAt: '2023-01-01'
      },
      position: {
        id: 1,
        positionCode: 'SENIOR',
        name: '선임',
        levelOrder: 3,
        createdAt: '2023-01-01',
        updatedAt: '2023-01-01'
      },
      salary: 4500000,
      status: EmployeeStatus.ACTIVE,
      createdAt: '2023-01-15T09:00:00',
      updatedAt: '2023-01-15T09:00:00'
    },
    {
      id: 2,
      employeeNumber: 'EMP002',
      name: '이영희',
      email: 'lee@company.com',
      phone: '010-2345-6789',
      hireDate: '2023-03-20',
      department: {
        id: 2,
        departmentCode: 'MKT',
        name: '마케팅팀',
        createdAt: '2023-01-01',
        updatedAt: '2023-01-01'
      },
      position: {
        id: 2,
        positionCode: 'JUNIOR',
        name: '주니어',
        levelOrder: 1,
        createdAt: '2023-01-01',
        updatedAt: '2023-01-01'
      },
      salary: 3200000,
      status: EmployeeStatus.ACTIVE,
      createdAt: '2023-03-20T09:00:00',
      updatedAt: '2023-03-20T09:00:00'
    },
  ]

  const filteredEmployees = employees.filter(employee => {
    const matchesSearch = employee.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         employee.employeeNumber.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         employee.email.toLowerCase().includes(searchTerm.toLowerCase())
    
    const matchesStatus = !statusFilter || employee.status === statusFilter
    
    return matchesSearch && matchesStatus
  })

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
        <Link to="/hr/employees/new" className="btn btn-primary">
          <Plus className="mr-2 h-4 w-4" />
          직원 등록
        </Link>
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
            onChange={(e) => setStatusFilter(e.target.value as EmployeeStatus | '')}
            className="form-input"
          >
            <option value="">전체 상태</option>
            {Object.entries(EmployeeStatusLabels).map(([status, label]) => (
              <option key={status} value={status}>{label}</option>
            ))}
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
              <tr key={employee.id}>
                <td className="font-medium">{employee.employeeNumber}</td>
                <td className="font-medium">{employee.name}</td>
                <td>{employee.email}</td>
                <td>{employee.phone || '-'}</td>
                <td>{employee.department?.name || '-'}</td>
                <td>{employee.position?.name || '-'}</td>
                <td>{employee.salary ? formatCurrency(employee.salary) : '-'}</td>
                <td>
                  <span
                    className={`inline-flex items-center rounded-full px-2 py-1 text-xs font-medium ${
                      employee.status === EmployeeStatus.ACTIVE
                        ? 'bg-green-100 text-green-800'
                        : employee.status === EmployeeStatus.INACTIVE
                        ? 'bg-yellow-100 text-yellow-800'
                        : 'bg-red-100 text-red-800'
                    }`}
                  >
                    {EmployeeStatusLabels[employee.status]}
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





