/**
 * 직원 테이블 컴포넌트
 * 직원 목록을 테이블 형태로 표시합니다
 */

import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'
import { Checkbox } from '@/components/ui/checkbox'
import { 
  MoreHorizontal,
  User,
  Edit,
  Trash2,
  Calendar,
  ArrowUpDown,
  ChevronUp,
  ChevronDown
} from 'lucide-react'
import type { Employee } from '@/types/hr'
import { KOREAN_LABELS } from '@/types/hr'
import { hrUtils } from '@/services/hrApi'

interface EmployeeTableProps {
  employees: Employee[]
  loading?: boolean
  onEdit?: (employee: Employee) => void
  onView?: (employee: Employee) => void
  onDelete?: (employee: Employee) => void
  onTerminate?: (employee: Employee) => void
  onSelectionChange?: (selectedIds: number[]) => void
  showActions?: boolean
  showSelection?: boolean
  className?: string
}

type SortField = 'name' | 'employeeNumber' | 'department' | 'position' | 'hireDate' | 'employmentStatus'
type SortDirection = 'asc' | 'desc'

/**
 * 직원 테이블 컴포넌트
 */
export function EmployeeTable({
  employees,
  loading = false,
  onEdit,
  onView,
  onDelete,
  onTerminate,
  onSelectionChange,
  showActions = true,
  showSelection = false,
  className = ''
}: EmployeeTableProps) {
  const navigate = useNavigate()
  const [selectedIds, setSelectedIds] = useState<number[]>([])
  const [sortField, setSortField] = useState<SortField>('employeeNumber')
  const [sortDirection, setSortDirection] = useState<SortDirection>('asc')

  /**
   * 직원 행 클릭 시 상세 페이지로 이동
   */
  const handleRowClick = (employeeId: number, event: React.MouseEvent) => {
    // 액션 버튼이나 체크박스 클릭 시에는 상세 페이지로 이동하지 않음
    const target = event.target as HTMLElement
    if (target.closest('button') || target.closest('[role="checkbox"]') || target.closest('a')) {
      return
    }
    navigate(`/hr/employees/${employeeId}`)
  }

  // 정렬 처리
  const handleSort = (field: SortField) => {
    if (sortField === field) {
      setSortDirection(sortDirection === 'asc' ? 'desc' : 'asc')
    } else {
      setSortField(field)
      setSortDirection('asc')
    }
  }

  // 정렬된 직원 목록
  const sortedEmployees = [...employees].sort((a, b) => {
    let aValue: any
    let bValue: any

    switch (sortField) {
      case 'name':
        aValue = a.name
        bValue = b.name
        break
      case 'employeeNumber':
        aValue = a.employeeNumber
        bValue = b.employeeNumber
        break
      case 'department':
        aValue = a.department.name
        bValue = b.department.name
        break
      case 'position':
        aValue = a.position.name
        bValue = b.position.name
        break
      case 'hireDate':
        aValue = new Date(a.hireDate)
        bValue = new Date(b.hireDate)
        break
      case 'employmentStatus':
        aValue = a.employmentStatus
        bValue = b.employmentStatus
        break
      default:
        return 0
    }

    if (aValue < bValue) return sortDirection === 'asc' ? -1 : 1
    if (aValue > bValue) return sortDirection === 'asc' ? 1 : -1
    return 0
  })

  // 선택 처리
  const handleSelectAll = (checked: boolean) => {
    if (checked) {
      const allIds = employees.map(emp => emp.id)
      setSelectedIds(allIds)
      onSelectionChange?.(allIds)
    } else {
      setSelectedIds([])
      onSelectionChange?.([])
    }
  }

  const handleSelectRow = (id: number, checked: boolean) => {
    let newSelectedIds: number[]
    if (checked) {
      newSelectedIds = [...selectedIds, id]
    } else {
      newSelectedIds = selectedIds.filter(selectedId => selectedId !== id)
    }
    setSelectedIds(newSelectedIds)
    onSelectionChange?.(newSelectedIds)
  }

  // 상태에 따른 뱃지 색상
  const getStatusBadgeVariant = (status: string) => {
    switch (status) {
      case 'ACTIVE':
        return 'default'          // 재직 - 파랑
      case 'ON_LEAVE':
        return 'secondary'        // 휴가 - 회색
      case 'INACTIVE':
        return 'purple'           // 휴직 - 연보라색
      case 'SUSPENDED':
        return 'black'            // 정직 - 검은색
      case 'TERMINATED':
        return 'destructive'      // 퇴직 - 빨강
      default:
        return 'secondary'
    }
  }

  // 정렬 아이콘 렌더링
  const renderSortIcon = (field: SortField) => {
    if (sortField !== field) {
      return <ArrowUpDown className="ml-2 h-4 w-4" />
    }
    return sortDirection === 'asc' 
      ? <ChevronUp className="ml-2 h-4 w-4" />
      : <ChevronDown className="ml-2 h-4 w-4" />
  }

  // 직원 이름 첫 글자 추출
  const getInitials = (name: string) => {
    return name.charAt(0).toUpperCase()
  }

  if (loading) {
    return (
      <div className="space-y-3">
        {[...Array(5)].map((_, i) => (
          <div key={i} className="h-16 bg-gray-100 animate-pulse rounded" />
        ))}
      </div>
    )
  }

  return (
    <div className={className}>
      <Table>
        <TableHeader>
          <TableRow>
            {showSelection && (
              <TableHead className="w-12">
                <Checkbox
                  checked={selectedIds.length === employees.length && employees.length > 0}
                  onCheckedChange={handleSelectAll}
                  aria-label="전체 선택"
                />
              </TableHead>
            )}
            
            <TableHead className="w-16">프로필</TableHead>
            
            <TableHead>
              <Button 
                variant="ghost" 
                onClick={() => handleSort('name')}
                className="h-auto p-0 font-semibold"
              >
                이름
                {renderSortIcon('name')}
              </Button>
            </TableHead>
            
            <TableHead>
              <Button 
                variant="ghost" 
                onClick={() => handleSort('employeeNumber')}
                className="h-auto p-0 font-semibold"
              >
                사번
                {renderSortIcon('employeeNumber')}
              </Button>
            </TableHead>
            
            <TableHead>
              <Button 
                variant="ghost" 
                onClick={() => handleSort('department')}
                className="h-auto p-0 font-semibold"
              >
                부서
                {renderSortIcon('department')}
              </Button>
            </TableHead>
            
            <TableHead>
              <Button 
                variant="ghost" 
                onClick={() => handleSort('position')}
                className="h-auto p-0 font-semibold"
              >
                직급
                {renderSortIcon('position')}
              </Button>
            </TableHead>
            
            <TableHead>연락처</TableHead>
            
            <TableHead>
              <Button 
                variant="ghost" 
                onClick={() => handleSort('hireDate')}
                className="h-auto p-0 font-semibold"
              >
                입사일
                {renderSortIcon('hireDate')}
              </Button>
            </TableHead>
            
            <TableHead>
              <Button 
                variant="ghost" 
                onClick={() => handleSort('employmentStatus')}
                className="h-auto p-0 font-semibold"
              >
                상태
                {renderSortIcon('employmentStatus')}
              </Button>
            </TableHead>

            {showActions && (
              <TableHead className="w-12">액션</TableHead>
            )}
          </TableRow>
        </TableHeader>

        <TableBody>
          {sortedEmployees.length === 0 ? (
            <TableRow>
              <TableCell 
                colSpan={showSelection && showActions ? 10 : showSelection || showActions ? 9 : 8}
                className="text-center py-8 text-muted-foreground"
              >
                직원 데이터가 없습니다.
              </TableCell>
            </TableRow>
          ) : (
            sortedEmployees.map((employee) => (
              <TableRow 
                key={employee.id}
                onClick={(e) => handleRowClick(employee.id, e)}
                className={`cursor-pointer hover:bg-muted/50 transition-colors ${
                  selectedIds.includes(employee.id) ? 'bg-muted/50' : ''
                }`}
              >
                {showSelection && (
                  <TableCell>
                    <Checkbox
                      checked={selectedIds.includes(employee.id)}
                      onCheckedChange={(checked) => 
                        handleSelectRow(employee.id, checked as boolean)
                      }
                      aria-label={`${employee.name} 선택`}
                    />
                  </TableCell>
                )}

                {/* 프로필 이미지 */}
                <TableCell>
                  <Avatar className="h-10 w-10">
                    <AvatarImage 
                      src={employee.profileImageUrl} 
                      alt={employee.name}
                    />
                    <AvatarFallback className="bg-primary text-primary-foreground text-sm">
                      {getInitials(employee.name)}
                    </AvatarFallback>
                  </Avatar>
                </TableCell>

                {/* 이름 */}
                <TableCell>
                  <div className="font-medium">{employee.name}</div>
                  {employee.nameEn && (
                    <div className="text-sm text-muted-foreground">
                      {employee.nameEn}
                    </div>
                  )}
                </TableCell>

                {/* 사번 */}
                <TableCell className="font-mono text-sm">
                  {employee.employeeNumber}
                </TableCell>

                {/* 부서 */}
                <TableCell>{employee.department.name}</TableCell>

                {/* 직급 */}
                <TableCell>
                  <div>{employee.position.name}</div>
                  <div className="text-sm text-muted-foreground">
                    Level {employee.position.positionLevel}
                  </div>
                </TableCell>

                {/* 연락처 */}
                <TableCell>
                  <div className="text-sm">
                    {employee.email && (
                      <div className="truncate max-w-[200px]" title={employee.email}>
                        {employee.email}
                      </div>
                    )}
                    {employee.mobile && (
                      <div className="text-muted-foreground">
                        {employee.mobile}
                      </div>
                    )}
                  </div>
                </TableCell>

                {/* 입사일 */}
                <TableCell>
                  <div className="text-sm">
                    {hrUtils.formatDate(employee.hireDate)}
                  </div>
                  <div className="text-xs text-muted-foreground">
                    근속 {employee.yearsOfService}년
                  </div>
                </TableCell>

                {/* 상태 */}
                <TableCell>
                  <div className="flex flex-col gap-1">
                    <Badge 
                      variant={getStatusBadgeVariant(employee.employmentStatus)}
                      className="w-16 text-xs px-2 py-1 flex items-center justify-center"
                    >
                      {KOREAN_LABELS[employee.employmentStatus]}
                    </Badge>
                    {employee.employmentType && (
                      <Badge 
                        variant="outline" 
                        className="w-16 text-xs px-2 py-1 flex items-center justify-center"
                      >
                        {KOREAN_LABELS[employee.employmentType]}
                      </Badge>
                    )}
                  </div>
                </TableCell>

                {/* 액션 메뉴 */}
                {showActions && (
                  <TableCell>
                    <DropdownMenu>
                      <DropdownMenuTrigger asChild>
                        <Button variant="ghost" size="sm">
                          <MoreHorizontal className="h-4 w-4" />
                        </Button>
                      </DropdownMenuTrigger>
                      <DropdownMenuContent align="end">
                        {onView && (
                          <DropdownMenuItem onClick={() => onView(employee)}>
                            <User className="mr-2 h-4 w-4" />
                            상세보기
                          </DropdownMenuItem>
                        )}
                        {onEdit && (
                          <DropdownMenuItem onClick={() => onEdit(employee)}>
                            <Edit className="mr-2 h-4 w-4" />
                            수정
                          </DropdownMenuItem>
                        )}
                        {onTerminate && employee.employmentStatus === 'ACTIVE' && (
                          <DropdownMenuItem 
                            onClick={() => onTerminate(employee)}
                            className="text-orange-600"
                          >
                            <Calendar className="mr-2 h-4 w-4" />
                            퇴직처리
                          </DropdownMenuItem>
                        )}
                        {onDelete && (
                          <DropdownMenuItem 
                            onClick={() => onDelete(employee)}
                            className="text-destructive"
                          >
                            <Trash2 className="mr-2 h-4 w-4" />
                            삭제
                          </DropdownMenuItem>
                        )}
                      </DropdownMenuContent>
                    </DropdownMenu>
                  </TableCell>
                )}
              </TableRow>
            ))
          )}
        </TableBody>
      </Table>
    </div>
  )
}

