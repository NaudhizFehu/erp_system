/**
 * 전체 현황 위젯 컴포넌트
 * 주요 지표들을 한눈에 볼 수 있는 요약 위젯입니다
 */

import React from 'react'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import {
  TrendingUp,
  TrendingDown,
  Users,
  ShoppingCart,
  Package,
  UserCheck,
  DollarSign,
  AlertTriangle,
  RefreshCw,
  MoreHorizontal,
  ArrowRight
} from 'lucide-react'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { formatCurrency, formatNumber } from '@/utils/format'
import type { OverviewSummary } from '@/types/dashboard'

interface OverviewWidgetProps {
  data: OverviewSummary
  loading?: boolean
  error?: string
  className?: string
  onRefresh?: () => void
  onViewDetails?: (category: string) => void
}

/**
 * 지표 카드 컴포넌트
 */
interface MetricCardProps {
  title: string
  value: number | string
  previousValue?: number
  growthRate?: number
  icon: React.ReactNode
  color: string
  format?: 'currency' | 'number' | 'percentage'
  onClick?: () => void
  badge?: string
  alert?: boolean
}

const MetricCard = ({
  title,
  value,
  previousValue,
  growthRate,
  icon,
  color,
  format = 'number',
  onClick,
  badge,
  alert = false
}: MetricCardProps) => {
  const formatValue = (val: number | string) => {
    if (typeof val === 'string') return val
    switch (format) {
      case 'currency':
        return formatCurrency(val)
      case 'percentage':
        return `${val.toFixed(1)}%`
      default:
        return formatNumber(val)
    }
  }

  const isPositiveGrowth = growthRate !== undefined && growthRate > 0
  const hasGrowth = growthRate !== undefined && growthRate !== 0

  return (
    <Card 
      className={`relative overflow-hidden transition-all duration-200 hover:shadow-lg ${
        onClick ? 'cursor-pointer hover:scale-105' : ''
      } ${alert ? 'ring-2 ring-red-200' : ''}`}
      onClick={onClick}
    >
      <CardContent className="p-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-3">
            <div 
              className="p-2 rounded-lg"
              style={{ backgroundColor: `${color}20`, color }}
            >
              {icon}
            </div>
            <div>
              <p className="text-sm font-medium text-gray-600">{title}</p>
              <div className="flex items-center space-x-2">
                <p className="text-2xl font-bold text-gray-900">
                  {formatValue(value)}
                </p>
                {badge && (
                  <Badge variant="secondary" className="text-xs">
                    {badge}
                  </Badge>
                )}
                {alert && (
                  <AlertTriangle className="h-4 w-4 text-red-500" />
                )}
              </div>
            </div>
          </div>
          {onClick && (
            <ArrowRight className="h-4 w-4 text-gray-400 opacity-0 group-hover:opacity-100 transition-opacity" />
          )}
        </div>
        
        {hasGrowth && (
          <div className="mt-3 flex items-center space-x-2">
            {isPositiveGrowth ? (
              <TrendingUp className="h-4 w-4 text-green-500" />
            ) : (
              <TrendingDown className="h-4 w-4 text-red-500" />
            )}
            <span className={`text-sm font-medium ${
              isPositiveGrowth ? 'text-green-600' : 'text-red-600'
            }`}>
              {Math.abs(growthRate!).toFixed(1)}%
            </span>
            <span className="text-xs text-gray-500">vs 이전 기간</span>
          </div>
        )}
      </CardContent>
    </Card>
  )
}

export default function OverviewWidget({
  data,
  loading = false,
  error,
  className = '',
  onRefresh,
  onViewDetails
}: OverviewWidgetProps) {
  if (loading) {
    return (
      <Card className={className}>
        <CardHeader>
          <div className="flex items-center justify-between">
            <CardTitle>전체 현황</CardTitle>
            <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-primary"></div>
          </div>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            {[...Array(8)].map((_, index) => (
              <div key={index} className="h-24 bg-gray-100 rounded-lg animate-pulse" />
            ))}
          </div>
        </CardContent>
      </Card>
    )
  }

  if (error) {
    return (
      <Card className={className}>
        <CardHeader>
          <div className="flex items-center justify-between">
            <CardTitle>전체 현황</CardTitle>
            <Button variant="ghost" size="sm" onClick={onRefresh}>
              <RefreshCw className="h-4 w-4" />
            </Button>
          </div>
        </CardHeader>
        <CardContent>
          <div className="text-center py-8">
            <p className="text-red-500 mb-4">데이터 로딩 중 오류가 발생했습니다</p>
            <Button variant="outline" onClick={onRefresh}>
              <RefreshCw className="h-4 w-4 mr-2" />
              다시 시도
            </Button>
          </div>
        </CardContent>
      </Card>
    )
  }

  return (
    <Card className={className}>
      <CardHeader>
        <div className="flex items-center justify-between">
          <CardTitle className="flex items-center space-x-2">
            <DollarSign className="h-5 w-5" />
            <span>전체 현황</span>
          </CardTitle>
          <div className="flex items-center space-x-2">
            {onRefresh && (
              <Button variant="ghost" size="sm" onClick={onRefresh}>
                <RefreshCw className="h-4 w-4" />
              </Button>
            )}
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="ghost" size="sm">
                  <MoreHorizontal className="h-4 w-4" />
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end">
                <DropdownMenuItem onClick={() => onViewDetails?.('sales')}>
                  매출 상세보기
                </DropdownMenuItem>
                <DropdownMenuItem onClick={() => onViewDetails?.('orders')}>
                  주문 상세보기
                </DropdownMenuItem>
                <DropdownMenuItem onClick={() => onViewDetails?.('customers')}>
                  고객 상세보기
                </DropdownMenuItem>
                <DropdownMenuItem onClick={() => onViewDetails?.('inventory')}>
                  재고 상세보기
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          </div>
        </div>
      </CardHeader>
      <CardContent>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          {/* 매출 관련 지표 */}
          <MetricCard
            title="월 매출"
            value={data.monthlyRevenue}
            growthRate={data.revenueGrowthRate}
            icon={<DollarSign className="h-5 w-5" />}
            color="#3b82f6"
            format="currency"
            onClick={() => onViewDetails?.('revenue')}
          />
          
          <MetricCard
            title="총 매출"
            value={data.totalRevenue}
            icon={<TrendingUp className="h-5 w-5" />}
            color="#10b981"
            format="currency"
            onClick={() => onViewDetails?.('revenue')}
          />

          {/* 주문 관련 지표 */}
          <MetricCard
            title="월 주문"
            value={data.monthlyOrders}
            growthRate={data.orderGrowthRate}
            icon={<ShoppingCart className="h-5 w-5" />}
            color="#f59e0b"
            onClick={() => onViewDetails?.('orders')}
          />

          <MetricCard
            title="대기 주문"
            value={data.pendingOrders}
            icon={<AlertTriangle className="h-5 w-5" />}
            color="#ef4444"
            onClick={() => onViewDetails?.('orders')}
            alert={data.pendingOrders > 10}
            badge={data.pendingOrders > 10 ? '주의' : undefined}
          />

          {/* 고객 관련 지표 */}
          <MetricCard
            title="총 고객"
            value={data.totalCustomers}
            icon={<Users className="h-5 w-5" />}
            color="#8b5cf6"
            onClick={() => onViewDetails?.('customers')}
          />

          <MetricCard
            title="활성 고객"
            value={data.activeCustomers}
            icon={<UserCheck className="h-5 w-5" />}
            color="#06b6d4"
            onClick={() => onViewDetails?.('customers')}
          />

          <MetricCard
            title="신규 고객"
            value={data.newCustomers}
            growthRate={data.customerGrowthRate}
            icon={<Users className="h-5 w-5" />}
            color="#84cc16"
            onClick={() => onViewDetails?.('customers')}
          />

          {/* 재고 관련 지표 */}
          <MetricCard
            title="재고 부족"
            value={data.lowStockProducts}
            icon={<Package className="h-5 w-5" />}
            color="#f97316"
            onClick={() => onViewDetails?.('inventory')}
            alert={data.lowStockProducts > 5}
            badge={data.lowStockProducts > 5 ? '긴급' : undefined}
          />

          {/* 인력 관련 지표 */}
          <MetricCard
            title="총 직원"
            value={data.totalEmployees}
            icon={<Users className="h-5 w-5" />}
            color="#ec4899"
            onClick={() => onViewDetails?.('hr')}
          />

          <MetricCard
            title="출석률"
            value={data.attendanceRate}
            icon={<UserCheck className="h-5 w-5" />}
            color="#14b8a6"
            format="percentage"
            onClick={() => onViewDetails?.('hr')}
            alert={data.attendanceRate < 90}
          />

          {/* 재고 가치 */}
          <MetricCard
            title="재고 가치"
            value={data.totalInventoryValue}
            icon={<Package className="h-5 w-5" />}
            color="#6366f1"
            format="currency"
            onClick={() => onViewDetails?.('inventory')}
          />

          {/* 재고 회전율 */}
          <MetricCard
            title="재고 회전율"
            value={data.inventoryTurnover}
            icon={<TrendingUp className="h-5 w-5" />}
            color="#f43f5e"
            format="number"
            onClick={() => onViewDetails?.('inventory')}
            badge={data.inventoryTurnover > 6 ? '우수' : data.inventoryTurnover > 4 ? '양호' : '개선필요'}
          />
        </div>

        {/* 요약 정보 */}
        <div className="mt-6 pt-4 border-t">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-sm">
            <div className="text-center">
              <p className="text-gray-500">이번 달 성과</p>
              <div className="flex items-center justify-center space-x-2 mt-1">
                <span className="font-semibold text-green-600">
                  +{((data.revenueGrowthRate + data.orderGrowthRate + data.customerGrowthRate) / 3).toFixed(1)}%
                </span>
                <span className="text-gray-500">평균 성장률</span>
              </div>
            </div>
            <div className="text-center">
              <p className="text-gray-500">주의 항목</p>
              <div className="flex items-center justify-center space-x-2 mt-1">
                <span className="font-semibold text-red-600">
                  {(data.pendingOrders > 10 ? 1 : 0) + (data.lowStockProducts > 5 ? 1 : 0) + (data.attendanceRate < 90 ? 1 : 0)}건
                </span>
                <span className="text-gray-500">확인 필요</span>
              </div>
            </div>
            <div className="text-center">
              <p className="text-gray-500">전체 평가</p>
              <div className="flex items-center justify-center space-x-2 mt-1">
                <Badge 
                  variant={
                    data.revenueGrowthRate > 10 ? 'default' : 
                    data.revenueGrowthRate > 5 ? 'secondary' : 
                    'destructive'
                  }
                >
                  {data.revenueGrowthRate > 10 ? '우수' : 
                   data.revenueGrowthRate > 5 ? '양호' : 
                   '개선필요'}
                </Badge>
              </div>
            </div>
          </div>
        </div>
      </CardContent>
    </Card>
  )
}




