/**
 * 대시보드 API 서비스
 * 대시보드 관련 API 통신을 담당합니다
 */

import api, { AxiosResponse } from 'api'
import type {
  DashboardApiResponse,
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
  TodoUpdateRequest,
  QuickAction,
  KpiMetric,
  UserDashboardConfig,
  WidgetConfig,
  DepartmentPerformance,
  SystemStatus
} from '@/types/dashboard'

// API 기본 설정
const api = api.create({
  baseURL: '/api/dashboard',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 요청 인터셉터 - 인증 토큰 추가
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 응답 인터셉터 - 에러 처리
api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('Dashboard API Error:', error)
    if (error.response?.status === 401) {
      localStorage.removeItem('accessToken')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

// ================================
// 전체 현황 및 요약
// ================================

export const dashboardApi = {
  /**
   * 전체 현황 요약 조회
   */
  getOverviewSummary: async (
    companyId: number,
    filter?: Partial<DashboardFilter>
  ): Promise<OverviewSummary> => {
    const params = {
      startDate: filter?.startDate,
      endDate: filter?.endDate,
      timeRange: filter?.timeRange
    }
    
    const response: AxiosResponse<DashboardApiResponse<OverviewSummary>> = await api.get(
      `/overview/${companyId}`,
      { params }
    )
    return response.data.data!
  },

  /**
   * 대시보드 전체 데이터 조회
   */
  getDashboardData: async (
    companyId: number,
    userId: number,
    filter?: Partial<DashboardFilter>
  ): Promise<DashboardData> => {
    const params = {
      userId,
      startDate: filter?.startDate,
      endDate: filter?.endDate,
      timeRange: filter?.timeRange,
      userRole: filter?.userRole
    }
    
    const response: AxiosResponse<DashboardApiResponse<DashboardData>> = await api.get(
      `/data/${companyId}`,
      { params }
    )
    return response.data.data!
  }
}

// ================================
// 차트 데이터 API
// ================================

export const chartApi = {
  /**
   * 매출 차트 데이터 조회
   */
  getRevenueChart: async (
    companyId: number,
    filter?: Partial<DashboardFilter>
  ): Promise<RevenueChart> => {
    const params = {
      startDate: filter?.startDate,
      endDate: filter?.endDate,
      timeRange: filter?.timeRange
    }
    
    const response: AxiosResponse<DashboardApiResponse<RevenueChart>> = await api.get(
      `/charts/revenue/${companyId}`,
      { params }
    )
    return response.data.data!
  },

  /**
   * 주문 차트 데이터 조회
   */
  getOrderChart: async (
    companyId: number,
    filter?: Partial<DashboardFilter>
  ): Promise<OrderChart> => {
    const params = {
      startDate: filter?.startDate,
      endDate: filter?.endDate,
      timeRange: filter?.timeRange
    }
    
    const response: AxiosResponse<DashboardApiResponse<OrderChart>> = await api.get(
      `/charts/orders/${companyId}`,
      { params }
    )
    return response.data.data!
  },

  /**
   * 재고 차트 데이터 조회
   */
  getInventoryChart: async (
    companyId: number,
    filter?: Partial<DashboardFilter>
  ): Promise<InventoryChart> => {
    const params = {
      startDate: filter?.startDate,
      endDate: filter?.endDate
    }
    
    const response: AxiosResponse<DashboardApiResponse<InventoryChart>> = await api.get(
      `/charts/inventory/${companyId}`,
      { params }
    )
    return response.data.data!
  },

  /**
   * 인사 차트 데이터 조회
   */
  getHrChart: async (
    companyId: number,
    filter?: Partial<DashboardFilter>
  ): Promise<HrChart> => {
    const params = {
      startDate: filter?.startDate,
      endDate: filter?.endDate,
      timeRange: filter?.timeRange
    }
    
    const response: AxiosResponse<DashboardApiResponse<HrChart>> = await api.get(
      `/charts/hr/${companyId}`,
      { params }
    )
    return response.data.data!
  }
}

// ================================
// 활동 로그 API
// ================================

export const activityApi = {
  /**
   * 최근 활동 로그 조회
   */
  getRecentActivities: async (
    companyId: number,
    userId: number,
    limit: number = 10
  ): Promise<ActivityLog[]> => {
    const response: AxiosResponse<DashboardApiResponse<ActivityLog[]>> = await api.get(
      `/activities/${companyId}`,
      { params: { userId, limit } }
    )
    return response.data.data!
  }
}

// ================================
// 알림 API
// ================================

export const notificationApi = {
  /**
   * 사용자 알림 조회
   */
  getUserNotifications: async (
    userId: number,
    unreadOnly: boolean = false,
    limit: number = 10
  ): Promise<Notification[]> => {
    const response: AxiosResponse<DashboardApiResponse<Notification[]>> = await api.get(
      `/notifications/${userId}`,
      { params: { unreadOnly, limit } }
    )
    return response.data.data!
  },

  /**
   * 알림 통계 조회
   */
  getNotificationStats: async (userId: number): Promise<NotificationStats> => {
    const response: AxiosResponse<DashboardApiResponse<NotificationStats>> = await api.get(
      `/notifications/stats/${userId}`
    )
    return response.data.data!
  },

  /**
   * 알림 읽음 처리
   */
  markAsRead: async (notificationId: number, userId: number): Promise<void> => {
    await api.put(`/notifications/${notificationId}/read`, null, {
      params: { userId }
    })
  },

  /**
   * 모든 알림 읽음 처리
   */
  markAllAsRead: async (userId: number): Promise<void> => {
    await api.put(`/notifications/read-all/${userId}`)
  }
}

// ================================
// 할일 API
// ================================

export const todoApi = {
  /**
   * 사용자 할일 목록 조회
   */
  getUserTodos: async (
    userId: number,
    status?: string,
    limit: number = 10
  ): Promise<TodoItem[]> => {
    const response: AxiosResponse<DashboardApiResponse<TodoItem[]>> = await api.get(
      `/todos/${userId}`,
      { params: { status, limit } }
    )
    return response.data.data!
  },

  /**
   * 새 할일 생성
   */
  createTodo: async (
    todoData: TodoCreateRequest,
    userId: number
  ): Promise<TodoItem> => {
    const response: AxiosResponse<DashboardApiResponse<TodoItem>> = await api.post(
      '/todos',
      todoData,
      { params: { userId } }
    )
    return response.data.data!
  },

  /**
   * 할일 상태 변경
   */
  updateTodoStatus: async (
    todoId: number,
    status: string,
    userId: number
  ): Promise<TodoItem> => {
    const response: AxiosResponse<DashboardApiResponse<TodoItem>> = await api.put(
      `/todos/${todoId}/status`,
      null,
      { params: { status, userId } }
    )
    return response.data.data!
  }
}

// ================================
// 빠른 액션 API
// ================================

export const quickActionApi = {
  /**
   * 사용자 역할별 빠른 액션 조회
   */
  getQuickActions: async (userRole: string): Promise<QuickAction[]> => {
    const response: AxiosResponse<DashboardApiResponse<QuickAction[]>> = await api.get(
      '/quick-actions',
      { params: { userRole } }
    )
    return response.data.data!
  }
}

// ================================
// KPI 지표 API
// ================================

export const kpiApi = {
  /**
   * KPI 지표 조회
   */
  getKpiMetrics: async (
    companyId: number,
    category?: string,
    filter?: Partial<DashboardFilter>
  ): Promise<KpiMetric[]> => {
    const params = {
      category,
      startDate: filter?.startDate,
      endDate: filter?.endDate
    }
    
    const response: AxiosResponse<DashboardApiResponse<KpiMetric[]>> = await api.get(
      `/kpi/${companyId}`,
      { params }
    )
    return response.data.data!
  }
}

// ================================
// 사용자 설정 API
// ================================

export const configApi = {
  /**
   * 사용자 대시보드 설정 조회
   */
  getUserConfig: async (userId: number): Promise<UserDashboardConfig> => {
    const response: AxiosResponse<DashboardApiResponse<UserDashboardConfig>> = await api.get(
      `/config/${userId}`
    )
    return response.data.data!
  },

  /**
   * 사용자 대시보드 설정 저장
   */
  saveUserConfig: async (
    userId: number,
    config: UserDashboardConfig
  ): Promise<UserDashboardConfig> => {
    const response: AxiosResponse<DashboardApiResponse<UserDashboardConfig>> = await api.put(
      `/config/${userId}`,
      config
    )
    return response.data.data!
  },

  /**
   * 위젯 설정 업데이트
   */
  updateWidgetConfig: async (
    userId: number,
    widgetId: string,
    config: Partial<WidgetConfig>
  ): Promise<UserDashboardConfig> => {
    // 현재 설정을 가져와서 특정 위젯만 업데이트
    const currentConfig = await configApi.getUserConfig(userId)
    const updatedWidgets = currentConfig.widgets.map(widget =>
      widget.id === widgetId ? { ...widget, ...config } : widget
    )
    
    const updatedConfig = {
      ...currentConfig,
      widgets: updatedWidgets,
      lastUpdated: new Date().toISOString()
    }
    
    return configApi.saveUserConfig(userId, updatedConfig)
  },

  /**
   * 위젯 표시/숨김 토글
   */
  toggleWidgetVisibility: async (
    userId: number,
    widgetId: string
  ): Promise<UserDashboardConfig> => {
    const currentConfig = await configApi.getUserConfig(userId)
    const updatedWidgets = currentConfig.widgets.map(widget =>
      widget.id === widgetId ? { ...widget, isVisible: !widget.isVisible } : widget
    )
    
    const updatedConfig = {
      ...currentConfig,
      widgets: updatedWidgets,
      lastUpdated: new Date().toISOString()
    }
    
    return configApi.saveUserConfig(userId, updatedConfig)
  }
}

// ================================
// 위젯 데이터 API
// ================================

export const widgetApi = {
  /**
   * 위젯 데이터 새로고침
   */
  refreshWidget: async (
    widgetId: string,
    companyId: number,
    userId: number,
    parameters?: any
  ): Promise<any> => {
    const response: AxiosResponse<DashboardApiResponse<any>> = await api.get(
      `/widget/${widgetId}/refresh`,
      { params: { companyId, userId, parameters } }
    )
    return response.data.data!
  }
}

// ================================
// 시스템 관리 API (관리자용)
// ================================

export const systemApi = {
  /**
   * 시스템 상태 조회
   */
  getSystemStatus: async (): Promise<SystemStatus> => {
    const response: AxiosResponse<DashboardApiResponse<SystemStatus>> = await api.get(
      '/system/status'
    )
    return response.data.data!
  },

  /**
   * 부서별 성과 조회
   */
  getDepartmentPerformance: async (
    companyId: number,
    filter?: Partial<DashboardFilter>
  ): Promise<DepartmentPerformance[]> => {
    // 실제 API가 구현되면 사용
    // const response = await api.get(`/performance/departments/${companyId}`, { params: filter })
    // return response.data.data!
    
    // 현재는 빈 배열 반환
    return []
  }
}

// ================================
// 실시간 데이터 API
// ================================

export const realtimeApi = {
  /**
   * 실시간 데이터 조회
   */
  getRealTimeData: async (
    dataType: string,
    companyId: number,
    userId: number
  ): Promise<any> => {
    // WebSocket 또는 Server-Sent Events를 사용할 수 있음
    // 현재는 HTTP 폴링으로 구현
    const response: AxiosResponse<DashboardApiResponse<any>> = await api.get(
      `/realtime/${dataType}`,
      { params: { companyId, userId } }
    )
    return response.data.data!
  }
}

// ================================
// 유틸리티 함수들
// ================================

/**
 * 시간 범위를 날짜로 변환
 */
export const getDateRangeFromTimeRange = (timeRange: string): { startDate: string; endDate: string } => {
  const now = new Date()
  const endDate = now.toISOString().split('T')[0]
  let startDate: string
  
  switch (timeRange) {
    case 'today':
      startDate = endDate
      break
    case 'yesterday':
      const yesterday = new Date(now)
      yesterday.setDate(yesterday.getDate() - 1)
      startDate = yesterday.toISOString().split('T')[0]
      break
    case 'this_week':
      const weekStart = new Date(now)
      weekStart.setDate(weekStart.getDate() - weekStart.getDay())
      startDate = weekStart.toISOString().split('T')[0]
      break
    case 'last_week':
      const lastWeekEnd = new Date(now)
      lastWeekEnd.setDate(lastWeekEnd.getDate() - lastWeekEnd.getDay() - 1)
      const lastWeekStart = new Date(lastWeekEnd)
      lastWeekStart.setDate(lastWeekStart.getDate() - 6)
      startDate = lastWeekStart.toISOString().split('T')[0]
      break
    case 'this_month':
      startDate = new Date(now.getFullYear(), now.getMonth(), 1).toISOString().split('T')[0]
      break
    case 'last_month':
      const lastMonth = new Date(now.getFullYear(), now.getMonth() - 1, 1)
      startDate = lastMonth.toISOString().split('T')[0]
      break
    case 'last_3_months':
      const threeMonthsAgo = new Date(now)
      threeMonthsAgo.setMonth(threeMonthsAgo.getMonth() - 3)
      startDate = threeMonthsAgo.toISOString().split('T')[0]
      break
    case 'last_6_months':
      const sixMonthsAgo = new Date(now)
      sixMonthsAgo.setMonth(sixMonthsAgo.getMonth() - 6)
      startDate = sixMonthsAgo.toISOString().split('T')[0]
      break
    case 'this_year':
      startDate = new Date(now.getFullYear(), 0, 1).toISOString().split('T')[0]
      break
    case 'last_year':
      startDate = new Date(now.getFullYear() - 1, 0, 1).toISOString().split('T')[0]
      break
    default:
      // 기본적으로 최근 30일
      const defaultStart = new Date(now)
      defaultStart.setDate(defaultStart.getDate() - 30)
      startDate = defaultStart.toISOString().split('T')[0]
  }
  
  return { startDate, endDate }
}

// 기본 export
export default {
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
  realtimeApi,
  getDateRangeFromTimeRange
}

