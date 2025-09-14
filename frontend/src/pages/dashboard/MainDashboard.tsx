/**
 * 메인 대시보드 페이지
 * ERP 시스템의 중앙 대시보드를 구현합니다
 */

import React, { useState, useEffect } from 'react'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
  DropdownMenuSeparator,
} from '@/components/ui/dropdown-menu'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import {
  BarChart3,
  TrendingUp,
  Users,
  Package,
  Settings,
  RefreshCw,
  Filter,
  Download,
  Maximize2,
  Grid3X3,
  LayoutDashboard,
  Calendar,
  Bell,
  MoreHorizontal,
  Sun,
  Moon,
  Monitor
} from 'lucide-react'
import toast from 'react-hot-toast'
import { useRouter } from 'next/router'

// 컴포넌트 임포트
import OverviewWidget from '@/components/dashboard/widgets/OverviewWidget'
import RevenueChart from '@/components/dashboard/charts/RevenueChart'
import ActivityWidget from '@/components/dashboard/widgets/ActivityWidget'
import TodoWidget from '@/components/dashboard/widgets/TodoWidget'
import LoadingSpinner from '@/components/common/LoadingSpinner'

// 훅과 타입 임포트
import {
  useDashboardData,
  useOverviewSummary,
  useRevenueChart,
  useOrderChart,
  useRecentActivities,
  useNotifications,
  useTodos,
  useQuickActions,
  useUserDashboardConfig,
  useSaveUserDashboardConfig,
  useMarkNotificationAsRead,
  useMarkAllNotificationsAsRead,
  useCreateTodo,
  useUpdateTodoStatus,
  useRefreshWidget,
  useTimeRangeFilter,
  useAutoRefresh,
  useDashboardLoading
} from '@/hooks/useDashboard'

import type {
  DashboardFilter,
  TimeRange,
  Theme,
  TodoCreateRequest,
  TodoStatus,
  QuickAction,
  Notification
} from '@/types/dashboard'
import { TIME_RANGE_OPTIONS } from '@/types/dashboard'

interface MainDashboardProps {
  companyId: number
  userId: number
  userRole: string
  className?: string
}

/**
 * 대시보드 헤더 컴포넌트
 */
const DashboardHeader = ({
  timeRange,
  onTimeRangeChange,
  theme,
  onThemeChange,
  onRefresh,
  onExport,
  onSettings,
  refreshing = false
}: {
  timeRange: TimeRange
  onTimeRangeChange: (range: TimeRange) => void
  theme: Theme
  onThemeChange: (theme: Theme) => void
  onRefresh: () => void
  onExport: () => void
  onSettings: () => void
  refreshing?: boolean
}) => {
  return (
    <div className="flex items-center justify-between mb-6">
      <div>
        <h1 className="text-3xl font-bold text-gray-900">대시보드</h1>
        <p className="text-gray-600 mt-1">
          ERP 시스템 전체 현황을 한눈에 확인하세요
        </p>
      </div>
      <div className="flex items-center space-x-4">
        {/* 시간 범위 선택 */}
        <Select value={timeRange} onValueChange={onTimeRangeChange}>
          <SelectTrigger className="w-40">
            <Calendar className="h-4 w-4 mr-2" />
            <SelectValue placeholder="기간 선택" />
          </SelectTrigger>
          <SelectContent>
            {TIME_RANGE_OPTIONS.map((option) => (
              <SelectItem key={option.value} value={option.value}>
                {option.label}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>

        {/* 새로고침 버튼 */}
        <Button 
          variant="outline" 
          size="sm" 
          onClick={onRefresh}
          disabled={refreshing}
        >
          <RefreshCw className={`h-4 w-4 mr-2 ${refreshing ? 'animate-spin' : ''}`} />
          새로고침
        </Button>

        {/* 더보기 메뉴 */}
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="outline" size="sm">
              <MoreHorizontal className="h-4 w-4" />
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end">
            <DropdownMenuItem onClick={onExport}>
              <Download className="h-4 w-4 mr-2" />
              내보내기
            </DropdownMenuItem>
            <DropdownMenuItem onClick={onSettings}>
              <Settings className="h-4 w-4 mr-2" />
              설정
            </DropdownMenuItem>
            <DropdownMenuSeparator />
            <DropdownMenuItem onClick={() => onThemeChange('light')}>
              <Sun className="h-4 w-4 mr-2" />
              라이트 모드
            </DropdownMenuItem>
            <DropdownMenuItem onClick={() => onThemeChange('dark')}>
              <Moon className="h-4 w-4 mr-2" />
              다크 모드
            </DropdownMenuItem>
            <DropdownMenuItem onClick={() => onThemeChange('auto')}>
              <Monitor className="h-4 w-4 mr-2" />
              시스템 설정
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>
    </div>
  )
}

/**
 * 빠른 통계 카드 컴포넌트
 */
const QuickStatsCards = ({ 
  data, 
  loading 
}: { 
  data?: any
  loading: boolean 
}) => {
  if (loading) {
    return (
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-6">
        {[...Array(4)].map((_, index) => (
          <Card key={index}>
            <CardContent className="p-6">
              <div className="animate-pulse">
                <div className="h-4 bg-gray-200 rounded w-3/4 mb-2"></div>
                <div className="h-8 bg-gray-200 rounded w-1/2 mb-2"></div>
                <div className="h-3 bg-gray-200 rounded w-2/3"></div>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>
    )
  }

  const stats = [
    {
      title: '월 매출',
      value: data?.monthlyRevenue || 0,
      change: data?.revenueGrowthRate || 0,
      icon: TrendingUp,
      color: 'text-green-600',
      format: 'currency'
    },
    {
      title: '활성 고객',
      value: data?.activeCustomers || 0,
      change: data?.customerGrowthRate || 0,
      icon: Users,
      color: 'text-blue-600',
      format: 'number'
    },
    {
      title: '대기 주문',
      value: data?.pendingOrders || 0,
      icon: Package,
      color: 'text-orange-600',
      format: 'number',
      alert: (data?.pendingOrders || 0) > 10
    },
    {
      title: '재고 부족',
      value: data?.lowStockProducts || 0,
      icon: Package,
      color: 'text-red-600',
      format: 'number',
      alert: (data?.lowStockProducts || 0) > 5
    }
  ]

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-6">
      {stats.map((stat, index) => {
        const Icon = stat.icon
        return (
          <Card key={index} className={stat.alert ? 'ring-2 ring-red-200' : ''}>
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-gray-600">{stat.title}</p>
                  <p className="text-2xl font-bold text-gray-900">
                    {stat.format === 'currency' 
                      ? new Intl.NumberFormat('ko-KR', { 
                          style: 'currency', 
                          currency: 'KRW',
                          maximumFractionDigits: 0
                        }).format(stat.value)
                      : stat.value.toLocaleString()
                    }
                  </p>
                  {stat.change !== undefined && (
                    <p className={`text-sm ${stat.change >= 0 ? 'text-green-600' : 'text-red-600'}`}>
                      {stat.change >= 0 ? '+' : ''}{stat.change.toFixed(1)}% vs 이전 기간
                    </p>
                  )}
                </div>
                <div className={`p-3 rounded-full bg-gray-100 ${stat.color}`}>
                  <Icon className="h-6 w-6" />
                </div>
              </div>
            </CardContent>
          </Card>
        )
      })}
    </div>
  )
}

export default function MainDashboard({
  companyId,
  userId,
  userRole,
  className = ''
}: MainDashboardProps) {
  const router = useRouter()
  
  // 상태 관리
  const [timeRange, setTimeRange] = useState<TimeRange>('this_month')
  const [theme, setTheme] = useState<Theme>('light')
  const [refreshing, setRefreshing] = useState(false)

  // 필터 설정
  const timeFilter = useTimeRangeFilter(timeRange)
  const filter: Partial<DashboardFilter> = {
    ...timeFilter,
    userRole
  }

  // 데이터 훅들
  const overviewQuery = useOverviewSummary(companyId, filter)
  const revenueChartQuery = useRevenueChart(companyId, filter)
  const orderChartQuery = useOrderChart(companyId, filter)
  const activitiesQuery = useRecentActivities(companyId, userId, 10)
  const notificationsQuery = useNotifications(userId, false, 10)
  const todosQuery = useTodos(userId, undefined, 10)
  const quickActionsQuery = useQuickActions(userRole)
  const userConfigQuery = useUserDashboardConfig(userId)

  // 뮤테이션 훅들
  const markNotificationAsRead = useMarkNotificationAsRead()
  const markAllNotificationsAsRead = useMarkAllNotificationsAsRead()
  const createTodo = useCreateTodo()
  const updateTodoStatus = useUpdateTodoStatus()
  const refreshWidget = useRefreshWidget()
  const saveUserConfig = useSaveUserDashboardConfig()

  // 로딩 상태 관리
  const { isLoading, hasError, loadingProgress } = useDashboardLoading(companyId, userId, filter)

  // 자동 새로고침 (5분마다)
  useAutoRefresh(companyId, userId, true, 5 * 60 * 1000)

  /**
   * 전체 새로고침
   */
  const handleRefresh = async () => {
    setRefreshing(true)
    try {
      await Promise.all([
        overviewQuery.refetch(),
        revenueChartQuery.refetch(),
        orderChartQuery.refetch(),
        activitiesQuery.refetch(),
        notificationsQuery.refetch(),
        todosQuery.refetch()
      ])
      toast.success('대시보드가 새로고침되었습니다')
    } catch (error) {
      toast.error('새로고침 중 오류가 발생했습니다')
    } finally {
      setRefreshing(false)
    }
  }

  /**
   * 데이터 내보내기
   */
  const handleExport = () => {
    toast.info('데이터 내보내기 기능은 준비 중입니다')
  }

  /**
   * 설정 페이지로 이동
   */
  const handleSettings = () => {
    router.push('/dashboard/settings')
  }

  /**
   * 상세보기 페이지로 이동
   */
  const handleViewDetails = (category: string) => {
    const routes: Record<string, string> = {
      revenue: '/sales/dashboard',
      orders: '/sales/orders',
      customers: '/sales/customers',
      inventory: '/inventory/dashboard',
      hr: '/hr/dashboard'
    }
    
    if (routes[category]) {
      router.push(routes[category])
    }
  }

  /**
   * 알림 클릭 처리
   */
  const handleNotificationClick = (notification: Notification) => {
    if (notification.actionUrl) {
      router.push(notification.actionUrl)
    }
  }

  /**
   * 알림 읽음 처리
   */
  const handleMarkAsRead = (notificationId: number) => {
    markNotificationAsRead.mutate({ notificationId, userId })
  }

  /**
   * 모든 알림 읽음 처리
   */
  const handleMarkAllAsRead = () => {
    markAllNotificationsAsRead.mutate(userId)
  }

  /**
   * 새 할일 생성
   */
  const handleCreateTodo = (todoData: TodoCreateRequest) => {
    createTodo.mutate({ todoData, userId })
  }

  /**
   * 할일 상태 변경
   */
  const handleUpdateTodoStatus = (todoId: number, status: TodoStatus) => {
    updateTodoStatus.mutate({ todoId, status, userId })
  }

  /**
   * 빠른 액션 클릭
   */
  const handleQuickActionClick = (action: QuickAction) => {
    if (action.url) {
      router.push(action.url)
    }
  }

  // 에러 상태 처리
  if (hasError) {
    return (
      <div className={`container mx-auto px-4 py-8 ${className}`}>
        <div className="text-center py-12">
          <h2 className="text-2xl font-bold text-gray-900 mb-4">
            데이터 로딩 중 오류가 발생했습니다
          </h2>
          <p className="text-gray-600 mb-6">
            네트워크 연결을 확인하고 다시 시도해주세요.
          </p>
          <Button onClick={handleRefresh}>
            <RefreshCw className="h-4 w-4 mr-2" />
            다시 시도
          </Button>
        </div>
      </div>
    )
  }

  return (
    <div className={`container mx-auto px-4 py-8 ${className}`}>
      {/* 헤더 */}
      <DashboardHeader
        timeRange={timeRange}
        onTimeRangeChange={setTimeRange}
        theme={theme}
        onThemeChange={setTheme}
        onRefresh={handleRefresh}
        onExport={handleExport}
        onSettings={handleSettings}
        refreshing={refreshing}
      />

      {/* 로딩 진행률 */}
      {isLoading && (
        <div className="mb-6">
          <div className="flex items-center justify-between text-sm text-gray-600 mb-2">
            <span>데이터 로딩 중...</span>
            <span>{loadingProgress.toFixed(0)}%</span>
          </div>
          <div className="w-full bg-gray-200 rounded-full h-2">
            <div 
              className="bg-primary h-2 rounded-full transition-all duration-300"
              style={{ width: `${loadingProgress}%` }}
            />
          </div>
        </div>
      )}

      {/* 빠른 통계 카드 */}
      <QuickStatsCards 
        data={overviewQuery.data}
        loading={overviewQuery.isLoading}
      />

      {/* 메인 콘텐츠 그리드 */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* 왼쪽 열 - 전체 현황 & 매출 차트 */}
        <div className="lg:col-span-2 space-y-6">
          {/* 전체 현황 위젯 */}
          <OverviewWidget
            data={overviewQuery.data!}
            loading={overviewQuery.isLoading}
            error={overviewQuery.error?.message}
            onRefresh={() => overviewQuery.refetch()}
            onViewDetails={handleViewDetails}
          />

          {/* 매출 차트 */}
          <RevenueChart
            data={revenueChartQuery.data!}
            loading={revenueChartQuery.isLoading}
            error={revenueChartQuery.error?.message}
            onRefresh={() => revenueChartQuery.refetch()}
            onExport={handleExport}
          />
        </div>

        {/* 오른쪽 열 - 활동 로그 & 할일 */}
        <div className="space-y-6">
          {/* 활동 로그 & 알림 위젯 */}
          <ActivityWidget
            activities={activitiesQuery.data || []}
            notifications={notificationsQuery.data || []}
            loading={activitiesQuery.isLoading || notificationsQuery.isLoading}
            error={activitiesQuery.error?.message || notificationsQuery.error?.message}
            onRefresh={() => {
              activitiesQuery.refetch()
              notificationsQuery.refetch()
            }}
            onNotificationClick={handleNotificationClick}
            onMarkAsRead={handleMarkAsRead}
            onMarkAllAsRead={handleMarkAllAsRead}
            onViewAll={(type) => {
              router.push(type === 'activities' ? '/dashboard/activities' : '/dashboard/notifications')
            }}
          />

          {/* 할일 & 빠른 액션 위젯 */}
          <TodoWidget
            todos={todosQuery.data || []}
            quickActions={quickActionsQuery.data || []}
            loading={todosQuery.isLoading || quickActionsQuery.isLoading}
            error={todosQuery.error?.message || quickActionsQuery.error?.message}
            onRefresh={() => {
              todosQuery.refetch()
              quickActionsQuery.refetch()
            }}
            onCreateTodo={handleCreateTodo}
            onUpdateTodoStatus={handleUpdateTodoStatus}
            onQuickActionClick={handleQuickActionClick}
            onViewAll={() => router.push('/dashboard/todos')}
          />
        </div>
      </div>

      {/* 추가 차트 섹션 (역할에 따라 표시) */}
      {(userRole === 'ADMIN' || userRole === 'MANAGER') && (
        <div className="mt-6">
          <Tabs defaultValue="orders" className="space-y-4">
            <TabsList>
              <TabsTrigger value="orders">주문 분석</TabsTrigger>
              <TabsTrigger value="inventory">재고 현황</TabsTrigger>
              <TabsTrigger value="hr">인사 현황</TabsTrigger>
            </TabsList>
            
            <TabsContent value="orders">
              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center space-x-2">
                    <BarChart3 className="h-5 w-5" />
                    <span>주문 분석</span>
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  {orderChartQuery.isLoading ? (
                    <div className="h-80 flex items-center justify-center">
                      <LoadingSpinner />
                    </div>
                  ) : (
                    <div className="h-80">
                      <p className="text-gray-500 text-center mt-32">
                        주문 차트 컴포넌트가 여기에 표시됩니다
                      </p>
                    </div>
                  )}
                </CardContent>
              </Card>
            </TabsContent>
            
            <TabsContent value="inventory">
              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center space-x-2">
                    <Package className="h-5 w-5" />
                    <span>재고 현황</span>
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="h-80 flex items-center justify-center text-gray-500">
                    재고 차트 컴포넌트가 여기에 표시됩니다
                  </div>
                </CardContent>
              </Card>
            </TabsContent>
            
            <TabsContent value="hr">
              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center space-x-2">
                    <Users className="h-5 w-5" />
                    <span>인사 현황</span>
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="h-80 flex items-center justify-center text-gray-500">
                    인사 차트 컴포넌트가 여기에 표시됩니다
                  </div>
                </CardContent>
              </Card>
            </TabsContent>
          </Tabs>
        </div>
      )}
    </div>
  )
}

