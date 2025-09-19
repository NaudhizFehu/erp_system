/**
 * 재고 대시보드 컴포넌트
 * 재고 현황 및 통계를 시각적으로 표시합니다
 */

import { useState, useEffect } from 'react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Progress } from '@/components/ui/progress'
import { Separator } from '@/components/ui/separator'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { 
  BarChart, 
  Bar, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
  LineChart,
  Line,
  Area,
  AreaChart
} from 'recharts'
import { 
  Package, 
  TrendingUp, 
  TrendingDown, 
  AlertTriangle, 
  CheckCircle, 
  XCircle,
  ArrowUpRight,
  ArrowDownRight,
  Warehouse,
  DollarSign,
  Activity,
  RefreshCw
} from 'lucide-react'
import { 
  useInventoryStats, 
  useProductStats, 
  useInventoryAlerts,
  useWarehouseUtilization,
  useInventoryTrend,
  useLowStockProducts,
  useOutOfStockProducts,
  useReorderNeededProducts 
} from '../../hooks/useInventory'
import { KOREAN_LABELS } from '../../types/inventory'
import { formatCurrency, formatNumber, formatDate } from '../../utils/format'
import LoadingSpinner from '../common/LoadingSpinner'

interface InventoryDashboardProps {
  companyId: number
}

/**
 * 통계 카드 컴포넌트
 */
interface StatsCardProps {
  title: string
  value: string | number
  subtitle?: string
  icon: React.ReactNode
  trend?: {
    value: number
    isPositive: boolean
  }
  color?: 'default' | 'success' | 'warning' | 'destructive'
}

function StatsCard({ title, value, subtitle, icon, trend, color = 'default' }: StatsCardProps) {
  const colorClasses = {
    default: 'border-border',
    success: 'border-green-200 bg-green-50',
    warning: 'border-yellow-200 bg-yellow-50',
    destructive: 'border-red-200 bg-red-50',
  }

  return (
    <Card className={`${colorClasses[color]}`}>
      <CardContent className="p-6">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-2">
            {icon}
            <div>
              <p className="text-sm font-medium text-muted-foreground">{title}</p>
              <p className="text-2xl font-bold">{typeof value === 'number' ? formatNumber(value) : value}</p>
              {subtitle && (
                <p className="text-xs text-muted-foreground mt-1">{subtitle}</p>
              )}
            </div>
          </div>
          {trend && (
            <div className={`flex items-center space-x-1 ${trend.isPositive ? 'text-green-600' : 'text-red-600'}`}>
              {trend.isPositive ? <ArrowUpRight className="h-4 w-4" /> : <ArrowDownRight className="h-4 w-4" />}
              <span className="text-sm font-medium">{Math.abs(trend.value)}%</span>
            </div>
          )}
        </div>
      </CardContent>
    </Card>
  )
}

/**
 * 알림 카드 컴포넌트
 */
interface AlertCardProps {
  title: string
  count: number
  items: any[]
  color: 'warning' | 'destructive'
  onViewAll: () => void
}

function AlertCard({ title, count, items, color, onViewAll }: AlertCardProps) {
  const colorClasses = {
    warning: 'border-yellow-200 bg-yellow-50',
    destructive: 'border-red-200 bg-red-50',
  }

  const iconClasses = {
    warning: 'text-yellow-600',
    destructive: 'text-red-600',
  }

  return (
    <Card className={colorClasses[color]}>
      <CardHeader className="pb-3">
        <div className="flex items-center justify-between">
          <CardTitle className="text-lg flex items-center space-x-2">
            <AlertTriangle className={`h-5 w-5 ${iconClasses[color]}`} />
            <span>{title}</span>
          </CardTitle>
          <Badge variant={color === 'warning' ? 'secondary' : 'destructive'}>
            {formatNumber(count)}건
          </Badge>
        </div>
      </CardHeader>
      <CardContent className="pt-0">
        {items.length > 0 ? (
          <div className="space-y-2">
            {items.slice(0, 3).map((item, index) => (
              <div key={index} className="flex justify-between items-center text-sm">
                <span className="truncate flex-1">{item.productName || item.name}</span>
                <span className="text-muted-foreground ml-2">
                  {item.quantity !== undefined ? `${formatNumber(item.quantity)}개` : ''}
                </span>
              </div>
            ))}
            {items.length > 3 && (
              <Button variant="ghost" size="sm" onClick={onViewAll} className="w-full mt-2">
                {items.length - 3}개 더 보기
              </Button>
            )}
          </div>
        ) : (
          <p className="text-sm text-muted-foreground">해당 항목이 없습니다</p>
        )}
      </CardContent>
    </Card>
  )
}

/**
 * 차트 색상 팔레트
 */
const CHART_COLORS = [
  '#3b82f6', '#ef4444', '#22c55e', '#f59e0b', '#8b5cf6',
  '#06b6d4', '#f97316', '#84cc16', '#ec4899', '#6b7280'
]

function InventoryDashboard({ companyId }: InventoryDashboardProps) {
  const [refreshKey, setRefreshKey] = useState(0)

  // 데이터 페칭
  const { data: inventoryStats, isLoading: isLoadingInventoryStats } = useInventoryStats(companyId)
  const { data: productStats, isLoading: isLoadingProductStats } = useProductStats(companyId)
  const { data: inventoryAlerts, isLoading: isLoadingAlerts } = useInventoryAlerts(companyId)
  const { data: warehouseUtilization, isLoading: isLoadingWarehouse } = useWarehouseUtilization(companyId)
  const { data: inventoryTrend, isLoading: isLoadingTrend } = useInventoryTrend(companyId, 30)
  const { data: lowStockProducts } = useLowStockProducts(companyId)
  const { data: outOfStockProducts } = useOutOfStockProducts(companyId)
  const { data: reorderNeededProducts } = useReorderNeededProducts(companyId)

  // 로딩 상태
  const isLoading = isLoadingInventoryStats || isLoadingProductStats || isLoadingAlerts

  // 새로고침 핸들러
  const handleRefresh = () => {
    setRefreshKey(prev => prev + 1)
  }

  // 데이터 가공
  const stats = inventoryStats?.data
  const productStatsData = productStats?.data
  const alerts = inventoryAlerts?.data || []
  const warehouseData = warehouseUtilization?.data || []
  const trendData = inventoryTrend?.data || []

  // 알림 분류
  const lowStockAlerts = alerts.filter(alert => alert.type === 'LOW_STOCK')
  const outOfStockAlerts = alerts.filter(alert => alert.type === 'OUT_OF_STOCK')
  const expiringSoonAlerts = alerts.filter(alert => alert.type === 'EXPIRING_SOON')

  // 창고 활용도 차트 데이터
  const warehouseChartData = warehouseData.map(warehouse => ({
    name: warehouse.warehouseName,
    utilization: warehouse.utilizationRate,
    totalValue: warehouse.totalValue,
    itemCount: warehouse.totalItems,
  }))

  // 재고 가치 분포 차트 데이터 (상위 10개 상품)
  const topValueProducts = productStatsData?.topValueProducts?.slice(0, 10) || []
  const valueDistributionData = topValueProducts.map((product, index) => ({
    name: product.productName?.substring(0, 15) + (product.productName?.length > 15 ? '...' : ''),
    value: product.totalStockValue,
    color: CHART_COLORS[index % CHART_COLORS.length],
  }))

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <LoadingSpinner size="lg" />
      </div>
    )
  }

  return (
    <div className="space-y-6">
      {/* 헤더 */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-3xl font-bold tracking-tight">재고 대시보드</h2>
          <p className="text-muted-foreground">실시간 재고 현황 및 통계를 확인하세요</p>
        </div>
        <Button onClick={handleRefresh} variant="outline" size="sm">
          <RefreshCw className="h-4 w-4 mr-2" />
          새로고침
        </Button>
      </div>

      {/* 주요 통계 카드 */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        <StatsCard
          title="총 재고 품목"
          value={stats?.totalInventoryItems || 0}
          subtitle="활성 재고 품목 수"
          icon={<Package className="h-5 w-5 text-blue-600" />}
          color="default"
        />
        <StatsCard
          title="총 재고 가치"
          value={formatCurrency(stats?.totalStockValue || 0)}
          subtitle="현재 재고의 총 가치"
          icon={<DollarSign className="h-5 w-5 text-green-600" />}
          color="success"
        />
        <StatsCard
          title="안전재고 미달"
          value={stats?.lowStockItems || 0}
          subtitle="주의가 필요한 품목"
          icon={<AlertTriangle className="h-5 w-5 text-yellow-600" />}
          color="warning"
        />
        <StatsCard
          title="재고없음"
          value={stats?.outOfStockItems || 0}
          subtitle="즉시 보충이 필요한 품목"
          icon={<XCircle className="h-5 w-5 text-red-600" />}
          color="destructive"
        />
      </div>

      {/* 알림 카드들 */}
      <div className="grid gap-4 md:grid-cols-3">
        <AlertCard
          title="안전재고 미달"
          count={lowStockAlerts.length}
          items={lowStockProducts?.data || []}
          color="warning"
          onViewAll={() => {/* 안전재고 미달 페이지로 이동 */}}
        />
        <AlertCard
          title="재고없음"
          count={outOfStockAlerts.length}
          items={outOfStockProducts?.data || []}
          color="destructive"
          onViewAll={() => {/* 재고없음 페이지로 이동 */}}
        />
        <AlertCard
          title="유효기간 임박"
          count={expiringSoonAlerts.length}
          items={expiringSoonAlerts}
          color="warning"
          onViewAll={() => {/* 유효기간 임박 페이지로 이동 */}}
        />
      </div>

      {/* 차트 섹션 */}
      <div className="grid gap-6 md:grid-cols-2">
        {/* 창고별 활용도 */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center space-x-2">
              <Warehouse className="h-5 w-5" />
              <span>창고별 활용도</span>
            </CardTitle>
            <CardDescription>
              각 창고의 공간 활용률과 재고 현황
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="h-[300px]">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={warehouseChartData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis 
                    dataKey="name" 
                    fontSize={12}
                    tick={{ fontSize: 12 }}
                  />
                  <YAxis 
                    fontSize={12}
                    tick={{ fontSize: 12 }}
                  />
                  <Tooltip 
                    formatter={(value, name) => [
                      name === 'utilization' ? `${value}%` : formatNumber(Number(value)),
                      name === 'utilization' ? '활용률' : '품목 수'
                    ]}
                  />
                  <Bar dataKey="utilization" fill="#3b82f6" radius={[4, 4, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </CardContent>
        </Card>

        {/* 재고 가치 분포 */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center space-x-2">
              <TrendingUp className="h-5 w-5" />
              <span>상위 재고 가치 분포</span>
            </CardTitle>
            <CardDescription>
              가치가 높은 상위 10개 상품의 재고 가치
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="h-[300px]">
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={valueDistributionData}
                    cx="50%"
                    cy="50%"
                    labelLine={false}
                    label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                    outerRadius={80}
                    fill="#8884d8"
                    dataKey="value"
                  >
                    {valueDistributionData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={entry.color} />
                    ))}
                  </Pie>
                  <Tooltip formatter={(value) => formatCurrency(Number(value))} />
                </PieChart>
              </ResponsiveContainer>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* 재고 트렌드 */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center space-x-2">
            <Activity className="h-5 w-5" />
            <span>재고 가치 트렌드 (30일)</span>
          </CardTitle>
          <CardDescription>
            최근 30일간 재고 가치 변동 추이
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="h-[300px]">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={trendData}>
                <defs>
                  <linearGradient id="colorValue" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#3b82f6" stopOpacity={0.8}/>
                    <stop offset="95%" stopColor="#3b82f6" stopOpacity={0.1}/>
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis 
                  dataKey="date" 
                  fontSize={12}
                  tick={{ fontSize: 12 }}
                  tickFormatter={(value) => formatDate(value, 'MM/dd')}
                />
                <YAxis 
                  fontSize={12}
                  tick={{ fontSize: 12 }}
                  tickFormatter={(value) => formatCurrency(value, true)}
                />
                <Tooltip 
                  labelFormatter={(value) => formatDate(value)}
                  formatter={(value) => [formatCurrency(Number(value)), '재고 가치']}
                />
                <Area 
                  type="monotone" 
                  dataKey="value" 
                  stroke="#3b82f6" 
                  fillOpacity={1} 
                  fill="url(#colorValue)" 
                />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </CardContent>
      </Card>

      {/* 추가 통계 정보 */}
      <div className="grid gap-4 md:grid-cols-3">
        <Card>
          <CardHeader className="pb-3">
            <CardTitle className="text-base">재고 회전율</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              {stats?.averageStockTurnover?.toFixed(1) || '0.0'}회
            </div>
            <p className="text-xs text-muted-foreground mt-1">
              연간 평균 재고 회전 횟수
            </p>
            <Progress 
              value={Math.min((stats?.averageStockTurnover || 0) * 10, 100)} 
              className="mt-2" 
            />
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-3">
            <CardTitle className="text-base">평균 재고 가치</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              {formatCurrency(stats?.averageStockValue || 0, true)}
            </div>
            <p className="text-xs text-muted-foreground mt-1">
              품목당 평균 재고 가치
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-3">
            <CardTitle className="text-base">재주문 필요</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-orange-600">
              {reorderNeededProducts?.data?.length || 0}건
            </div>
            <p className="text-xs text-muted-foreground mt-1">
              재주문 포인트 도달 품목
            </p>
            {(reorderNeededProducts?.data?.length || 0) > 0 && (
              <Button variant="outline" size="sm" className="mt-2 w-full">
                재주문 목록 보기
              </Button>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  )
}

export { InventoryDashboard }




