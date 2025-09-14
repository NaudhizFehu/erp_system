/**
 * 대시보드 React Query 훅
 * 대시보드 관련 데이터 페칭과 상태 관리를 담당합니다
 */

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import toast from 'react-hot-toast'
import {
  dashboardApi,
  chartApi,
  activityApi,
  notificationApi,
  todoApi,
  quickActionApi,
  kpiApi,
  configApi,
  widgetApi,
  systemApi,
  getDateRangeFromTimeRange
} from '@/services/dashboardApi'
import type {
  DashboardData,
  DashboardFilter,
  OverviewSummary,
  RevenueChart,
  OrderChart,
  InventoryChart,
  HrChart,
  ActivityLog,
  Notification,
  NotificationStats,
  TodoItem,
  TodoCreateRequest,
  QuickAction,
  KpiMetric,
  UserDashboardConfig,
  WidgetConfig,
  SystemStatus
} from '@/types/dashboard'

// ================================
// 전체 대시보드 데이터
// ================================

/**
 * 대시보드 전체 데이터 조회
 */
export const useDashboardData = (
  companyId: number,
  userId: number,
  filter?: Partial<DashboardFilter>
) => {
  return useQuery({
    queryKey: ['dashboard', 'data', companyId, userId, filter],
    queryFn: () => dashboardApi.getDashboardData(companyId, userId, filter),
    staleTime: 5 * 60 * 1000, // 5분
    cacheTime: 10 * 60 * 1000, // 10분
    refetchOnWindowFocus: false,
    enabled: !!companyId && !!userId
  })
}

/**
 * 전체 현황 요약 조회
 */
export const useOverviewSummary = (
  companyId: number,
  filter?: Partial<DashboardFilter>
) => {
  return useQuery({
    queryKey: ['dashboard', 'overview', companyId, filter],
    queryFn: () => dashboardApi.getOverviewSummary(companyId, filter),
    staleTime: 2 * 60 * 1000, // 2분
    cacheTime: 5 * 60 * 1000, // 5분
    enabled: !!companyId
  })
}

// ================================
// 차트 데이터 훅들
// ================================

/**
 * 매출 차트 데이터 조회
 */
export const useRevenueChart = (
  companyId: number,
  filter?: Partial<DashboardFilter>
) => {
  return useQuery({
    queryKey: ['dashboard', 'chart', 'revenue', companyId, filter],
    queryFn: () => chartApi.getRevenueChart(companyId, filter),
    staleTime: 5 * 60 * 1000, // 5분
    enabled: !!companyId
  })
}

/**
 * 주문 차트 데이터 조회
 */
export const useOrderChart = (
  companyId: number,
  filter?: Partial<DashboardFilter>
) => {
  return useQuery({
    queryKey: ['dashboard', 'chart', 'orders', companyId, filter],
    queryFn: () => chartApi.getOrderChart(companyId, filter),
    staleTime: 5 * 60 * 1000, // 5분
    enabled: !!companyId
  })
}

/**
 * 재고 차트 데이터 조회
 */
export const useInventoryChart = (
  companyId: number,
  filter?: Partial<DashboardFilter>
) => {
  return useQuery({
    queryKey: ['dashboard', 'chart', 'inventory', companyId, filter],
    queryFn: () => chartApi.getInventoryChart(companyId, filter),
    staleTime: 10 * 60 * 1000, // 10분 (재고는 자주 변하지 않음)
    enabled: !!companyId
  })
}

/**
 * 인사 차트 데이터 조회
 */
export const useHrChart = (
  companyId: number,
  filter?: Partial<DashboardFilter>
) => {
  return useQuery({
    queryKey: ['dashboard', 'chart', 'hr', companyId, filter],
    queryFn: () => chartApi.getHrChart(companyId, filter),
    staleTime: 10 * 60 * 1000, // 10분
    enabled: !!companyId
  })
}

// ================================
// 활동 로그 훅
// ================================

/**
 * 최근 활동 로그 조회
 */
export const useRecentActivities = (
  companyId: number,
  userId: number,
  limit: number = 10
) => {
  return useQuery({
    queryKey: ['dashboard', 'activities', companyId, userId, limit],
    queryFn: () => activityApi.getRecentActivities(companyId, userId, limit),
    staleTime: 1 * 60 * 1000, // 1분
    cacheTime: 5 * 60 * 1000, // 5분
    refetchInterval: 30 * 1000, // 30초마다 자동 갱신
    enabled: !!companyId && !!userId
  })
}

// ================================
// 알림 관련 훅들
// ================================

/**
 * 사용자 알림 조회
 */
export const useNotifications = (
  userId: number,
  unreadOnly: boolean = false,
  limit: number = 10
) => {
  return useQuery({
    queryKey: ['dashboard', 'notifications', userId, unreadOnly, limit],
    queryFn: () => notificationApi.getUserNotifications(userId, unreadOnly, limit),
    staleTime: 1 * 60 * 1000, // 1분
    refetchInterval: 30 * 1000, // 30초마다 자동 갱신
    enabled: !!userId
  })
}

/**
 * 알림 통계 조회
 */
export const useNotificationStats = (userId: number) => {
  return useQuery({
    queryKey: ['dashboard', 'notifications', 'stats', userId],
    queryFn: () => notificationApi.getNotificationStats(userId),
    staleTime: 2 * 60 * 1000, // 2분
    refetchInterval: 60 * 1000, // 1분마다 자동 갱신
    enabled: !!userId
  })
}

/**
 * 알림 읽음 처리
 */
export const useMarkNotificationAsRead = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ notificationId, userId }: { notificationId: number; userId: number }) =>
      notificationApi.markAsRead(notificationId, userId),
    onSuccess: (_, { userId }) => {
      queryClient.invalidateQueries({ queryKey: ['dashboard', 'notifications', userId] })
      queryClient.invalidateQueries({ queryKey: ['dashboard', 'notifications', 'stats', userId] })
    },
    onError: (error: any) => {
      toast.error(`알림 읽음 처리 실패: ${error.response?.data?.message || error.message}`)
    }
  })
}

/**
 * 모든 알림 읽음 처리
 */
export const useMarkAllNotificationsAsRead = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (userId: number) => notificationApi.markAllAsRead(userId),
    onSuccess: (_, userId) => {
      queryClient.invalidateQueries({ queryKey: ['dashboard', 'notifications', userId] })
      queryClient.invalidateQueries({ queryKey: ['dashboard', 'notifications', 'stats', userId] })
      toast.success('모든 알림이 읽음 처리되었습니다')
    },
    onError: (error: any) => {
      toast.error(`알림 읽음 처리 실패: ${error.response?.data?.message || error.message}`)
    }
  })
}

// ================================
// 할일 관련 훅들
// ================================

/**
 * 사용자 할일 목록 조회
 */
export const useTodos = (
  userId: number,
  status?: string,
  limit: number = 10
) => {
  return useQuery({
    queryKey: ['dashboard', 'todos', userId, status, limit],
    queryFn: () => todoApi.getUserTodos(userId, status, limit),
    staleTime: 2 * 60 * 1000, // 2분
    enabled: !!userId
  })
}

/**
 * 새 할일 생성
 */
export const useCreateTodo = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ todoData, userId }: { todoData: TodoCreateRequest; userId: number }) =>
      todoApi.createTodo(todoData, userId),
    onSuccess: (_, { userId }) => {
      queryClient.invalidateQueries({ queryKey: ['dashboard', 'todos', userId] })
      toast.success('새 할일이 생성되었습니다')
    },
    onError: (error: any) => {
      toast.error(`할일 생성 실패: ${error.response?.data?.message || error.message}`)
    }
  })
}

/**
 * 할일 상태 변경
 */
export const useUpdateTodoStatus = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ todoId, status, userId }: { todoId: number; status: string; userId: number }) =>
      todoApi.updateTodoStatus(todoId, status, userId),
    onSuccess: (_, { userId }) => {
      queryClient.invalidateQueries({ queryKey: ['dashboard', 'todos', userId] })
      toast.success('할일 상태가 변경되었습니다')
    },
    onError: (error: any) => {
      toast.error(`할일 상태 변경 실패: ${error.response?.data?.message || error.message}`)
    }
  })
}

// ================================
// 빠른 액션 훅
// ================================

/**
 * 빠른 액션 목록 조회
 */
export const useQuickActions = (userRole: string) => {
  return useQuery({
    queryKey: ['dashboard', 'quickActions', userRole],
    queryFn: () => quickActionApi.getQuickActions(userRole),
    staleTime: 10 * 60 * 1000, // 10분 (자주 변하지 않음)
    cacheTime: 30 * 60 * 1000, // 30분
    enabled: !!userRole
  })
}

// ================================
// KPI 지표 훅
// ================================

/**
 * KPI 지표 조회
 */
export const useKpiMetrics = (
  companyId: number,
  category?: string,
  filter?: Partial<DashboardFilter>
) => {
  return useQuery({
    queryKey: ['dashboard', 'kpi', companyId, category, filter],
    queryFn: () => kpiApi.getKpiMetrics(companyId, category, filter),
    staleTime: 5 * 60 * 1000, // 5분
    enabled: !!companyId
  })
}

// ================================
// 사용자 설정 훅들
// ================================

/**
 * 사용자 대시보드 설정 조회
 */
export const useUserDashboardConfig = (userId: number) => {
  return useQuery({
    queryKey: ['dashboard', 'config', userId],
    queryFn: () => configApi.getUserConfig(userId),
    staleTime: 10 * 60 * 1000, // 10분
    cacheTime: 30 * 60 * 1000, // 30분
    enabled: !!userId
  })
}

/**
 * 사용자 대시보드 설정 저장
 */
export const useSaveUserDashboardConfig = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ userId, config }: { userId: number; config: UserDashboardConfig }) =>
      configApi.saveUserConfig(userId, config),
    onSuccess: (data, { userId }) => {
      queryClient.setQueryData(['dashboard', 'config', userId], data)
      toast.success('대시보드 설정이 저장되었습니다')
    },
    onError: (error: any) => {
      toast.error(`설정 저장 실패: ${error.response?.data?.message || error.message}`)
    }
  })
}

/**
 * 위젯 설정 업데이트
 */
export const useUpdateWidgetConfig = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ userId, widgetId, config }: {
      userId: number
      widgetId: string
      config: Partial<WidgetConfig>
    }) => configApi.updateWidgetConfig(userId, widgetId, config),
    onSuccess: (data, { userId }) => {
      queryClient.setQueryData(['dashboard', 'config', userId], data)
    },
    onError: (error: any) => {
      toast.error(`위젯 설정 업데이트 실패: ${error.response?.data?.message || error.message}`)
    }
  })
}

/**
 * 위젯 표시/숨김 토글
 */
export const useToggleWidgetVisibility = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ userId, widgetId }: { userId: number; widgetId: string }) =>
      configApi.toggleWidgetVisibility(userId, widgetId),
    onSuccess: (data, { userId }) => {
      queryClient.setQueryData(['dashboard', 'config', userId], data)
    },
    onError: (error: any) => {
      toast.error(`위젯 표시 설정 변경 실패: ${error.response?.data?.message || error.message}`)
    }
  })
}

// ================================
// 위젯 데이터 훅
// ================================

/**
 * 위젯 데이터 새로고침
 */
export const useRefreshWidget = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ widgetId, companyId, userId, parameters }: {
      widgetId: string
      companyId: number
      userId: number
      parameters?: any
    }) => widgetApi.refreshWidget(widgetId, companyId, userId, parameters),
    onSuccess: (_, { widgetId, companyId, userId }) => {
      // 관련된 쿼리들을 무효화하여 데이터 새로고침
      queryClient.invalidateQueries({ queryKey: ['dashboard', 'data', companyId, userId] })
      queryClient.invalidateQueries({ queryKey: ['dashboard', 'chart'] })
      queryClient.invalidateQueries({ queryKey: ['dashboard', 'overview'] })
      toast.success('위젯 데이터가 새로고침되었습니다')
    },
    onError: (error: any) => {
      toast.error(`위젯 새로고침 실패: ${error.response?.data?.message || error.message}`)
    }
  })
}

// ================================
// 시스템 관리 훅 (관리자용)
// ================================

/**
 * 시스템 상태 조회
 */
export const useSystemStatus = () => {
  return useQuery({
    queryKey: ['dashboard', 'system', 'status'],
    queryFn: () => systemApi.getSystemStatus(),
    staleTime: 30 * 1000, // 30초
    refetchInterval: 60 * 1000, // 1분마다 자동 갱신
    retry: 2
  })
}

// ================================
// 유틸리티 훅들
// ================================

/**
 * 시간 범위에 따른 필터 생성
 */
export const useTimeRangeFilter = (timeRange?: string) => {
  if (!timeRange || timeRange === 'custom') {
    return {}
  }
  
  return getDateRangeFromTimeRange(timeRange)
}

/**
 * 대시보드 데이터 자동 새로고침
 */
export const useAutoRefresh = (
  companyId: number,
  userId: number,
  enabled: boolean = true,
  interval: number = 5 * 60 * 1000 // 5분
) => {
  const queryClient = useQueryClient()

  return useQuery({
    queryKey: ['dashboard', 'autoRefresh', companyId, userId],
    queryFn: async () => {
      // 주요 데이터들을 새로고침
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ['dashboard', 'overview', companyId] }),
        queryClient.invalidateQueries({ queryKey: ['dashboard', 'activities', companyId, userId] }),
        queryClient.invalidateQueries({ queryKey: ['dashboard', 'notifications', userId] })
      ])
      return Date.now()
    },
    enabled: enabled && !!companyId && !!userId,
    refetchInterval: interval,
    refetchIntervalInBackground: false,
    refetchOnWindowFocus: false
  })
}

/**
 * 실시간 알림 수 조회
 */
export const useRealTimeNotificationCount = (userId: number) => {
  const { data: stats } = useNotificationStats(userId)
  return stats?.unreadNotifications || 0
}

/**
 * 대시보드 로딩 상태 통합 관리
 */
export const useDashboardLoading = (
  companyId: number,
  userId: number,
  filter?: Partial<DashboardFilter>
) => {
  const overviewQuery = useOverviewSummary(companyId, filter)
  const activitiesQuery = useRecentActivities(companyId, userId, 5)
  const notificationsQuery = useNotifications(userId, false, 5)
  const todosQuery = useTodos(userId, undefined, 5)

  const isLoading = overviewQuery.isLoading || 
                   activitiesQuery.isLoading || 
                   notificationsQuery.isLoading || 
                   todosQuery.isLoading

  const hasError = overviewQuery.isError || 
                  activitiesQuery.isError || 
                  notificationsQuery.isError || 
                  todosQuery.isError

  return {
    isLoading,
    hasError,
    loadingProgress: [
      overviewQuery.isSuccess,
      activitiesQuery.isSuccess,
      notificationsQuery.isSuccess,
      todosQuery.isSuccess
    ].filter(Boolean).length / 4 * 100
  }
}

