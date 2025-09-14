/**
 * 고객 카드 컴포넌트
 * 고객 정보를 카드 형태로 표시합니다
 */

import React from 'react'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
  DropdownMenuSeparator
} from '@/components/ui/dropdown-menu'
import {
  Building2,
  Phone,
  Mail,
  MapPin,
  User,
  MoreHorizontal,
  Edit,
  Eye,
  UserCheck,
  UserX,
  Star,
  CreditCard,
  AlertTriangle,
  TrendingUp
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

interface CustomerCardProps {
  customer: CustomerSummary
  onView?: (customer: CustomerSummary) => void
  onEdit?: (customer: CustomerSummary) => void
  onToggleActive?: (customer: CustomerSummary) => void
  onChangeStatus?: (customer: CustomerSummary) => void
  onChangeGrade?: (customer: CustomerSummary) => void
  className?: string
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

export default function CustomerCard({
  customer,
  onView,
  onEdit,
  onToggleActive,
  onChangeStatus,
  onChangeGrade,
  className = ''
}: CustomerCardProps) {
  // 고객명의 첫 글자를 아바타로 사용
  const getInitial = (name: string) => {
    return name.charAt(0).toUpperCase()
  }

  // 마지막 주문일로부터 경과 일수 계산
  const getDaysSinceLastOrder = (lastOrderDate?: string) => {
    if (!lastOrderDate) return null
    const lastOrder = new Date(lastOrderDate)
    const today = new Date()
    const diffTime = Math.abs(today.getTime() - lastOrder.getTime())
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
    return diffDays
  }

  const daysSinceLastOrder = getDaysSinceLastOrder(customer.lastOrderDate)

  return (
    <Card className={`hover:shadow-lg transition-shadow duration-200 ${className}`}>
      <CardHeader className="pb-3">
        <div className="flex items-start justify-between">
          <div className="flex items-center space-x-3">
            <Avatar className="h-12 w-12">
              <AvatarFallback className="bg-primary/10 text-primary font-semibold">
                {getInitial(customer.customerName)}
              </AvatarFallback>
            </Avatar>
            <div className="flex-1">
              <CardTitle className="text-lg font-semibold text-gray-900">
                {customer.customerName}
              </CardTitle>
              <p className="text-sm text-gray-500 mt-1">
                {customer.customerCode}
              </p>
            </div>
          </div>
          <div className="flex items-center space-x-2">
            {/* VIP 고객 표시 */}
            {customer.isVipCustomer && (
              <Star className="h-4 w-4 text-yellow-500 fill-yellow-500" />
            )}
            {/* 신용한도 초과 경고 */}
            {customer.isCreditLimitExceeded && (
              <AlertTriangle className="h-4 w-4 text-red-500" />
            )}
            {/* 액션 메뉴 */}
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
          </div>
        </div>
      </CardHeader>

      <CardContent className="space-y-4">
        {/* 고객 상태 및 등급 배지 */}
        <div className="flex items-center space-x-2">
          <Badge variant={getStatusBadgeVariant(customer.customerStatus)}>
            {CustomerStatusLabels[customer.customerStatus]}
          </Badge>
          <Badge variant={getGradeBadgeVariant(customer.customerGrade)}>
            {CustomerGradeLabels[customer.customerGrade]}
          </Badge>
          <Badge variant="outline">
            {CustomerTypeLabels[customer.customerType]}
          </Badge>
          {!customer.isActive && (
            <Badge variant="destructive" className="text-xs">
              비활성
            </Badge>
          )}
        </div>

        {/* 연락처 정보 */}
        <div className="space-y-2">
          {customer.phoneNumber && (
            <div className="flex items-center space-x-2 text-sm text-gray-600">
              <Phone className="h-4 w-4" />
              <span>{customer.phoneNumber}</span>
            </div>
          )}
          {customer.email && (
            <div className="flex items-center space-x-2 text-sm text-gray-600">
              <Mail className="h-4 w-4" />
              <span>{customer.email}</span>
            </div>
          )}
          {customer.salesManagerName && (
            <div className="flex items-center space-x-2 text-sm text-gray-600">
              <User className="h-4 w-4" />
              <span>담당자: {customer.salesManagerName}</span>
            </div>
          )}
        </div>

        {/* 거래 정보 */}
        <div className="grid grid-cols-2 gap-4 pt-2 border-t">
          <div>
            <p className="text-xs text-gray-500">총 주문 건수</p>
            <p className="text-sm font-semibold">
              {customer.totalOrderCount?.toLocaleString() || 0}건
            </p>
          </div>
          <div>
            <p className="text-xs text-gray-500">총 주문 금액</p>
            <p className="text-sm font-semibold">
              {customer.totalOrderAmount 
                ? formatCurrency(customer.totalOrderAmount)
                : '₩0'
              }
            </p>
          </div>
        </div>

        {/* 미수금 정보 */}
        {customer.outstandingAmount && customer.outstandingAmount > 0 && (
          <div className="flex items-center space-x-2 p-2 bg-red-50 rounded-md">
            <CreditCard className="h-4 w-4 text-red-500" />
            <div>
              <p className="text-xs text-red-600">미수금</p>
              <p className="text-sm font-semibold text-red-700">
                {formatCurrency(customer.outstandingAmount)}
              </p>
            </div>
          </div>
        )}

        {/* 마지막 주문일 */}
        <div className="flex justify-between items-center text-xs text-gray-500">
          <span>
            {customer.lastOrderDate ? (
              <>
                마지막 주문: {format(new Date(customer.lastOrderDate), 'yyyy.MM.dd', { locale: ko })}
                {daysSinceLastOrder && daysSinceLastOrder > 30 && (
                  <span className="ml-1 text-orange-600">
                    ({daysSinceLastOrder}일 전)
                  </span>
                )}
              </>
            ) : (
              '주문 이력 없음'
            )}
          </span>
        </div>

        {/* 액션 버튼들 */}
        <div className="flex space-x-2 pt-2">
          <Button
            variant="outline"
            size="sm"
            className="flex-1"
            onClick={() => onView?.(customer)}
          >
            <Eye className="mr-1 h-3 w-3" />
            상세
          </Button>
          <Button
            variant="default"
            size="sm"
            className="flex-1"
            onClick={() => onEdit?.(customer)}
          >
            <Edit className="mr-1 h-3 w-3" />
            수정
          </Button>
        </div>
      </CardContent>
    </Card>
  )
}




