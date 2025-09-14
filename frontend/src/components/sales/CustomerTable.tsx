/**
 * 고객 테이블 컴포넌트
 * 고객 목록을 테이블 형태로 표시합니다
 */

import React, { useState } from 'react'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Checkbox } from '@/components/ui/checkbox'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
  DropdownMenuSeparator
} from '@/components/ui/dropdown-menu'
import {
  MoreHorizontal,
  Edit,
  Eye,
  UserCheck,
  UserX,
  User,
  TrendingUp,
  Star,
  AlertTriangle,
  Phone,
  Mail,
  ArrowUpDown
} from 'lucide-react'
import { format } from 'date-fns'
import { ko } from 'date-fns/locale'
import type { CustomerSummary } from '@/types/sales'
import {
  CustomerTypeLabels,
  CustomerStatusLabels,
  CustomerGradeLabels
} from '@/types/sales'
import { formatCurrency } from '@/utils/format'

interface CustomerTableProps {
  customers: CustomerSummary[]
  loading?: boolean
  selectedCustomers?: number[]
  onSelectionChange?: (selectedIds: number[]) => void
  onView?: (customer: CustomerSummary) => void
  onEdit?: (customer: CustomerSummary) => void
  onToggleActive?: (customer: CustomerSummary) => void
  onChangeStatus?: (customer: CustomerSummary) => void
  onChangeGrade?: (customer: CustomerSummary) => void
  onSort?: (field: string, direction: 'asc' | 'desc') => void
  sortField?: string
  sortDirection?: 'asc' | 'desc'
}

/**
 * 고객 상태에 따른 배지 색상 반환
 */
const getStatusBadgeVariant = (status: string) => {
  switch (status) {
    case 'ACTIVE':
      return 'default'
    case 'VIP':
      return 'secondary'
    case 'PROSPECT':
      return 'outline'
    case 'INACTIVE':
      return 'secondary'
    case 'DORMANT':
      return 'destructive'
    case 'BLACKLIST':
      return 'destructive'
    default:
      return 'outline'
  }
}

/**
 * 고객 등급에 따른 배지 색상 반환
 */
const getGradeBadgeVariant = (grade: string) => {
  switch (grade) {
    case 'PLATINUM':
      return 'default'
    case 'GOLD':
      return 'secondary'
    case 'SILVER':
      return 'outline'
    case 'BRONZE':
      return 'outline'
    default:
      return 'outline'
  }
}

export default function CustomerTable({
  customers,
  loading = false,
  selectedCustomers = [],
  onSelectionChange,
  onView,
  onEdit,
  onToggleActive,
  onChangeStatus,
  onChangeGrade,
  onSort,
  sortField,
  sortDirection
}: CustomerTableProps) {
  const [selectAll, setSelectAll] = useState(false)

  /**
   * 전체 선택/해제 처리
   */
  const handleSelectAll = (checked: boolean) => {
    setSelectAll(checked)
    if (onSelectionChange) {
      if (checked) {
        onSelectionChange(customers.map(c => c.id))
      } else {
        onSelectionChange([])
      }
    }
  }

  /**
   * 개별 선택 처리
   */
  const handleSelectCustomer = (customerId: number, checked: boolean) => {
    if (onSelectionChange) {
      if (checked) {
        onSelectionChange([...selectedCustomers, customerId])
      } else {
        onSelectionChange(selectedCustomers.filter(id => id !== customerId))
      }
    }
  }

  /**
   * 정렬 처리
   */
  const handleSort = (field: string) => {
    if (onSort) {
      const direction = sortField === field && sortDirection === 'asc' ? 'desc' : 'asc'
      onSort(field, direction)
    }
  }

  /**
   * 정렬 아이콘 표시
   */
  const getSortIcon = (field: string) => {
    if (sortField === field) {
      return (
        <ArrowUpDown 
          className={`ml-2 h-4 w-4 ${
            sortDirection === 'asc' ? 'transform rotate-180' : ''
          }`} 
        />
      )
    }
    return <ArrowUpDown className="ml-2 h-4 w-4 opacity-50" />
  }

  if (loading) {
    return (
      <div className="border rounded-lg">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead className="w-12">
                <Checkbox disabled />
              </TableHead>
              <TableHead>고객명</TableHead>
              <TableHead>상태</TableHead>
              <TableHead>등급</TableHead>
              <TableHead>연락처</TableHead>
              <TableHead>주문 정보</TableHead>
              <TableHead>마지막 주문</TableHead>
              <TableHead className="w-12"></TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {[...Array(5)].map((_, index) => (
              <TableRow key={index}>
                <TableCell><div className="h-4 w-4 bg-gray-200 rounded animate-pulse" /></TableCell>
                <TableCell><div className="h-4 w-24 bg-gray-200 rounded animate-pulse" /></TableCell>
                <TableCell><div className="h-4 w-16 bg-gray-200 rounded animate-pulse" /></TableCell>
                <TableCell><div className="h-4 w-16 bg-gray-200 rounded animate-pulse" /></TableCell>
                <TableCell><div className="h-4 w-32 bg-gray-200 rounded animate-pulse" /></TableCell>
                <TableCell><div className="h-4 w-20 bg-gray-200 rounded animate-pulse" /></TableCell>
                <TableCell><div className="h-4 w-20 bg-gray-200 rounded animate-pulse" /></TableCell>
                <TableCell><div className="h-4 w-4 bg-gray-200 rounded animate-pulse" /></TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>
    )
  }

  if (customers.length === 0) {
    return (
      <div className="border rounded-lg p-8 text-center">
        <div className="text-gray-500">
          <User className="mx-auto h-12 w-12 mb-4 opacity-50" />
          <p className="text-lg font-medium">고객이 없습니다</p>
          <p className="text-sm mt-2">새로운 고객을 등록해보세요.</p>
        </div>
      </div>
    )
  }

  return (
    <div className="border rounded-lg">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead className="w-12">
              <Checkbox
                checked={selectAll}
                onCheckedChange={handleSelectAll}
              />
            </TableHead>
            <TableHead 
              className="cursor-pointer hover:bg-gray-50"
              onClick={() => handleSort('customerName')}
            >
              <div className="flex items-center">
                고객명
                {getSortIcon('customerName')}
              </div>
            </TableHead>
            <TableHead>상태</TableHead>
            <TableHead>등급</TableHead>
            <TableHead>유형</TableHead>
            <TableHead>연락처</TableHead>
            <TableHead 
              className="cursor-pointer hover:bg-gray-50"
              onClick={() => handleSort('totalOrderAmount')}
            >
              <div className="flex items-center">
                주문 정보
                {getSortIcon('totalOrderAmount')}
              </div>
            </TableHead>
            <TableHead 
              className="cursor-pointer hover:bg-gray-50"
              onClick={() => handleSort('lastOrderDate')}
            >
              <div className="flex items-center">
                마지막 주문
                {getSortIcon('lastOrderDate')}
              </div>
            </TableHead>
            <TableHead className="w-12"></TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {customers.map((customer) => (
            <TableRow 
              key={customer.id} 
              className={`hover:bg-gray-50 ${!customer.isActive ? 'opacity-60' : ''}`}
            >
              {/* 선택 체크박스 */}
              <TableCell>
                <Checkbox
                  checked={selectedCustomers.includes(customer.id)}
                  onCheckedChange={(checked) => 
                    handleSelectCustomer(customer.id, checked as boolean)
                  }
                />
              </TableCell>

              {/* 고객명 및 코드 */}
              <TableCell>
                <div className="flex items-center space-x-2">
                  <div>
                    <div className="flex items-center space-x-1">
                      <p className="font-medium text-gray-900">
                        {customer.customerName}
                      </p>
                      {customer.isVipCustomer && (
                        <Star className="h-3 w-3 text-yellow-500 fill-yellow-500" />
                      )}
                      {customer.isCreditLimitExceeded && (
                        <AlertTriangle className="h-3 w-3 text-red-500" />
                      )}
                    </div>
                    <p className="text-xs text-gray-500">
                      {customer.customerCode}
                    </p>
                  </div>
                </div>
              </TableCell>

              {/* 상태 */}
              <TableCell>
                <Badge variant={getStatusBadgeVariant(customer.customerStatus)}>
                  {CustomerStatusLabels[customer.customerStatus]}
                </Badge>
                {!customer.isActive && (
                  <Badge variant="destructive" className="ml-1 text-xs">
                    비활성
                  </Badge>
                )}
              </TableCell>

              {/* 등급 */}
              <TableCell>
                <Badge variant={getGradeBadgeVariant(customer.customerGrade)}>
                  {CustomerGradeLabels[customer.customerGrade]}
                </Badge>
              </TableCell>

              {/* 유형 */}
              <TableCell>
                <Badge variant="outline">
                  {CustomerTypeLabels[customer.customerType]}
                </Badge>
              </TableCell>

              {/* 연락처 */}
              <TableCell>
                <div className="space-y-1">
                  {customer.phoneNumber && (
                    <div className="flex items-center space-x-1 text-sm">
                      <Phone className="h-3 w-3 text-gray-400" />
                      <span>{customer.phoneNumber}</span>
                    </div>
                  )}
                  {customer.email && (
                    <div className="flex items-center space-x-1 text-sm">
                      <Mail className="h-3 w-3 text-gray-400" />
                      <span className="truncate max-w-32">{customer.email}</span>
                    </div>
                  )}
                  {customer.salesManagerName && (
                    <div className="flex items-center space-x-1 text-xs text-gray-500">
                      <User className="h-3 w-3" />
                      <span>{customer.salesManagerName}</span>
                    </div>
                  )}
                </div>
              </TableCell>

              {/* 주문 정보 */}
              <TableCell>
                <div className="space-y-1">
                  <div className="text-sm">
                    <span className="font-medium">
                      {customer.totalOrderCount?.toLocaleString() || 0}
                    </span>
                    <span className="text-gray-500 ml-1">건</span>
                  </div>
                  <div className="text-sm font-medium">
                    {customer.totalOrderAmount 
                      ? formatCurrency(customer.totalOrderAmount)
                      : '₩0'
                    }
                  </div>
                  {customer.outstandingAmount && customer.outstandingAmount > 0 && (
                    <div className="text-xs text-red-600">
                      미수금: {formatCurrency(customer.outstandingAmount)}
                    </div>
                  )}
                </div>
              </TableCell>

              {/* 마지막 주문일 */}
              <TableCell>
                {customer.lastOrderDate ? (
                  <div className="text-sm">
                    {format(new Date(customer.lastOrderDate), 'yyyy.MM.dd', { locale: ko })}
                  </div>
                ) : (
                  <span className="text-xs text-gray-400">주문 이력 없음</span>
                )}
              </TableCell>

              {/* 액션 메뉴 */}
              <TableCell>
                <DropdownMenu>
                  <DropdownMenuTrigger asChild>
                    <Button variant="ghost" size="sm" className="h-8 w-8 p-0">
                      <MoreHorizontal className="h-4 w-4" />
                    </Button>
                  </DropdownMenuTrigger>
                  <DropdownMenuContent align="end" className="w-48">
                    <DropdownMenuItem onClick={() => onView?.(customer)}>
                      <Eye className="mr-2 h-4 w-4" />
                      상세 보기
                    </DropdownMenuItem>
                    <DropdownMenuItem onClick={() => onEdit?.(customer)}>
                      <Edit className="mr-2 h-4 w-4" />
                      수정
                    </DropdownMenuItem>
                    <DropdownMenuSeparator />
                    <DropdownMenuItem onClick={() => onToggleActive?.(customer)}>
                      {customer.isActive ? (
                        <>
                          <UserX className="mr-2 h-4 w-4" />
                          비활성화
                        </>
                      ) : (
                        <>
                          <UserCheck className="mr-2 h-4 w-4" />
                          활성화
                        </>
                      )}
                    </DropdownMenuItem>
                    <DropdownMenuItem onClick={() => onChangeStatus?.(customer)}>
                      <User className="mr-2 h-4 w-4" />
                      상태 변경
                    </DropdownMenuItem>
                    <DropdownMenuItem onClick={() => onChangeGrade?.(customer)}>
                      <TrendingUp className="mr-2 h-4 w-4" />
                      등급 변경
                    </DropdownMenuItem>
                  </DropdownMenuContent>
                </DropdownMenu>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  )
}




