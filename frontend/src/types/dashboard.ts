/**
 * 대시보드 관련 TypeScript 타입 정의
 * 백엔드 DTO와 일치하는 타입들을 정의합니다
 */

// ================================
// 기본 타입들
// ================================

export interface ChartDataPoint {
  label: string
  value: number
  category: string
  date: string
  color: string
}

export interface DashboardFilter {
  startDate?: string
  endDate?: string
  timeRange?: string
  modules?: string[]
  departments?: string[]
  categories?: string[]
  userRole?: string
}

// ================================
// 전체 현황 요약
// ================================

export interface OverviewSummary {
  // 매출 관련
  totalRevenue: number
  monthlyRevenue: number
  revenueGrowthRate: number
  
  // 주문 관련
  totalOrders: number
  monthlyOrders: number
  orderGrowthRate: number
  pendingOrders: number
  
  // 고객 관련
  totalCustomers: number
  activeCustomers: number
  newCustomers: number
  customerGrowthRate: number
  
  // 재고 관련
  totalProducts: number
  lowStockProducts: number
  totalInventoryValue: number
  inventoryTurnover: number
  
  // 인력 관련
  totalEmployees: number
  activeEmployees: number
  newEmployees: number
  attendanceRate: number
  
  // 회계 관련
  totalAssets: number
  totalLiabilities: number
  netIncome: number
  cashFlow: number
}

// ================================
// 차트 데이터 타입들
// ================================

export interface RevenueChart {
  monthlyRevenue: ChartDataPoint[]
  dailyRevenue: ChartDataPoint[]
  revenueByCategory: ChartDataPoint[]
  revenueByCustomer: ChartDataPoint[]
}

export interface OrderChart {
  ordersByStatus: ChartDataPoint[]
  ordersByMonth: ChartDataPoint[]
  ordersByType: ChartDataPoint[]
  ordersBySalesRep: ChartDataPoint[]
}

export interface InventoryChart {
  stockLevels: ChartDataPoint[]
  inventoryByCategory: ChartDataPoint[]
  topSellingProducts: ChartDataPoint[]
  slowMovingProducts: ChartDataPoint[]
}

export interface HrChart {
  attendanceByMonth: ChartDataPoint[]
  employeesByDepartment: ChartDataPoint[]
  salaryByDepartment: ChartDataPoint[]
  newHiresByMonth: ChartDataPoint[]
}

// ================================
// 활동 로그 및 알림
// ================================

export interface ActivityLog {
  id: number
  activityType: string
  activityDescription: string
  module: string
  userName: string
  userId?: number
  timestamp: string
  severity: 'INFO' | 'WARNING' | 'ERROR' | 'SUCCESS'
  details?: string
  ipAddress?: string
}

export interface Notification {
  id: number
  title: string
  message: string
  type: 'info' | 'warning' | 'error' | 'success'
  priority: 'low' | 'medium' | 'high' | 'critical'
  module: string
  createdAt: string
  isRead: boolean
  actionUrl?: string
  actionLabel?: string
}

export interface NotificationStats {
  totalNotifications: number
  unreadNotifications: number
  criticalNotifications: number
  warningNotifications: number
  infoNotifications: number
}

// ================================
// 할일 관리
// ================================

export interface TodoItem {
  id: number
  title: string
  description?: string
  priority: 'low' | 'medium' | 'high' | 'critical'
  status: 'pending' | 'in_progress' | 'completed' | 'cancelled'
  dueDate?: string
  assignedTo?: string
  assignedToId?: number
  module: string
  createdAt: string
  updatedAt: string
}

export interface TodoCreateRequest {
  title: string
  description?: string
  priority: 'low' | 'medium' | 'high' | 'critical'
  dueDate?: string
  assignedToId?: number
  module: string
}

export interface TodoUpdateRequest {
  title?: string
  description?: string
  priority?: 'low' | 'medium' | 'high' | 'critical'
  status?: 'pending' | 'in_progress' | 'completed' | 'cancelled'
  dueDate?: string
  assignedToId?: number
}

// ================================
// 빠른 액션
// ================================

export interface QuickAction {
  id: string
  title: string
  description: string
  icon: string
  url: string
  permission: string
  category: string
  sortOrder: number
  isEnabled: boolean
}

// ================================
// 위젯 설정
// ================================

export interface WidgetConfig {
  id: string
  title: string
  type: 'summary' | 'chart' | 'list' | 'table' | 'metric' | 'custom'
  width: number
  height: number
  positionX: number
  positionY: number
  isVisible: boolean
  dataSource: string
  chartType?: 'line' | 'bar' | 'pie' | 'doughnut' | 'area' | 'scatter'
  timeRange?: string
  settings?: Record<string, any>
}

export interface UserDashboardConfig {
  userId: number
  userRole: string
  widgets: WidgetConfig[]
  theme: 'light' | 'dark' | 'auto'
  layout: 'grid' | 'masonry' | 'custom'
  lastUpdated: string
}

// ================================
// KPI 지표
// ================================

export interface KpiMetric {
  name: string
  displayName: string
  currentValue: number
  previousValue: number
  changePercentage: number
  trend: 'up' | 'down' | 'stable'
  unit: string
  format: 'number' | 'currency' | 'percentage' | 'decimal'
  category: string
  description?: string
  lastUpdated: string
}

// ================================
// 성과 지표
// ================================

export interface PerformanceMetric {
  metricName: string
  value: number
  unit: string
  targetValue?: number
  achievementRate?: number
  status: 'excellent' | 'good' | 'average' | 'poor'
  trend: 'up' | 'down' | 'stable'
  periodStart: string
  periodEnd: string
}

export interface DepartmentPerformance {
  departmentId: number
  departmentName: string
  metrics: PerformanceMetric[]
  overallScore: number
  ranking: string
  employeeCount: number
}

// ================================
// 시스템 상태
// ================================

export interface SystemStatus {
  status: 'healthy' | 'warning' | 'critical'
  cpuUsage: number
  memoryUsage: number
  diskUsage: number
  activeUsers: number
  totalSessions: number
  lastUpdate: string
  systemAlerts: string[]
}

// ================================
// 대시보드 전체 데이터
// ================================

export interface DashboardData {
  overview: OverviewSummary
  revenueChart: RevenueChart
  orderChart: OrderChart
  inventoryChart: InventoryChart
  hrChart: HrChart
  recentActivities: ActivityLog[]
  notifications: Notification[]
  todoItems: TodoItem[]
  quickActions: QuickAction[]
  kpiMetrics: KpiMetric[]
  userConfig: UserDashboardConfig
}

// ================================
// 차트 설정 타입들
// ================================

export interface ChartConfig {
  type: 'line' | 'bar' | 'pie' | 'doughnut' | 'area' | 'scatter'
  title?: string
  subtitle?: string
  showLegend?: boolean
  showTooltip?: boolean
  showGrid?: boolean
  colors?: string[]
  height?: number
  responsive?: boolean
  animation?: boolean
  dataLabels?: boolean
}

export interface LineChartConfig extends ChartConfig {
  type: 'line'
  smooth?: boolean
  showDots?: boolean
  strokeWidth?: number
  fillArea?: boolean
}

export interface BarChartConfig extends ChartConfig {
  type: 'bar'
  horizontal?: boolean
  stacked?: boolean
  barSize?: number
}

export interface PieChartConfig extends ChartConfig {
  type: 'pie' | 'doughnut'
  innerRadius?: number
  outerRadius?: number
  startAngle?: number
  endAngle?: number
}

// ================================
// 위젯 컴포넌트 Props
// ================================

export interface BaseWidgetProps {
  id: string
  title: string
  loading?: boolean
  error?: string
  onRefresh?: () => void
  onSettings?: () => void
  className?: string
}

export interface ChartWidgetProps extends BaseWidgetProps {
  data: ChartDataPoint[]
  config: ChartConfig
}

export interface MetricWidgetProps extends BaseWidgetProps {
  metric: KpiMetric
  showTrend?: boolean
  showComparison?: boolean
}

export interface ListWidgetProps extends BaseWidgetProps {
  items: any[]
  renderItem: (item: any, index: number) => React.ReactNode
  emptyMessage?: string
  showMore?: boolean
  onShowMore?: () => void
}

// ================================
// 대시보드 레이아웃
// ================================

export interface DashboardLayout {
  id: string
  name: string
  description?: string
  isDefault: boolean
  userRole: string
  widgets: WidgetConfig[]
  createdAt: string
  updatedAt: string
}

export interface GridItem {
  id: string
  x: number
  y: number
  w: number
  h: number
  minW?: number
  minH?: number
  maxW?: number
  maxH?: number
  isDraggable?: boolean
  isResizable?: boolean
}

// ================================
// 필터 옵션
// ================================

export interface TimeRangeOption {
  value: string
  label: string
  days: number
}

export interface ModuleOption {
  value: string
  label: string
  icon?: string
  color?: string
}

export interface DepartmentOption {
  value: string
  label: string
  parentId?: string
}

// ================================
// API 응답 타입들
// ================================

export interface DashboardApiResponse<T = any> {
  success: boolean
  message: string
  data?: T
  error?: string
  timestamp: string
}

// ================================
// 상수 및 옵션들
// ================================

export const TIME_RANGE_OPTIONS: TimeRangeOption[] = [
  { value: 'today', label: '오늘', days: 1 },
  { value: 'yesterday', label: '어제', days: 1 },
  { value: 'this_week', label: '이번 주', days: 7 },
  { value: 'last_week', label: '지난 주', days: 7 },
  { value: 'this_month', label: '이번 달', days: 30 },
  { value: 'last_month', label: '지난 달', days: 30 },
  { value: 'last_3_months', label: '최근 3개월', days: 90 },
  { value: 'last_6_months', label: '최근 6개월', days: 180 },
  { value: 'this_year', label: '올해', days: 365 },
  { value: 'last_year', label: '작년', days: 365 },
  { value: 'custom', label: '사용자 정의', days: 0 }
]

export const MODULE_OPTIONS: ModuleOption[] = [
  { value: 'sales', label: '영업관리', icon: 'TrendingUp', color: '#3b82f6' },
  { value: 'inventory', label: '재고관리', icon: 'Package', color: '#10b981' },
  { value: 'hr', label: '인사관리', icon: 'Users', color: '#f59e0b' },
  { value: 'accounting', label: '회계관리', icon: 'Calculator', color: '#ef4444' },
  { value: 'system', label: '시스템', icon: 'Settings', color: '#6b7280' }
]

export const CHART_COLORS = [
  '#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6',
  '#06b6d4', '#84cc16', '#f97316', '#ec4899', '#6366f1'
]

export const SEVERITY_COLORS = {
  INFO: '#3b82f6',
  SUCCESS: '#10b981',
  WARNING: '#f59e0b',
  ERROR: '#ef4444'
}

export const PRIORITY_COLORS = {
  low: '#6b7280',
  medium: '#f59e0b',
  high: '#ef4444',
  critical: '#dc2626'
}

// ================================
// 유틸리티 타입들
// ================================

export type ChartType = 'line' | 'bar' | 'pie' | 'doughnut' | 'area' | 'scatter'
export type WidgetType = 'summary' | 'chart' | 'list' | 'table' | 'metric' | 'custom'
export type TimeRange = 'today' | 'yesterday' | 'this_week' | 'last_week' | 'this_month' | 'last_month' | 'last_3_months' | 'last_6_months' | 'this_year' | 'last_year' | 'custom'
export type Theme = 'light' | 'dark' | 'auto'
export type Layout = 'grid' | 'masonry' | 'custom'
export type Severity = 'INFO' | 'WARNING' | 'ERROR' | 'SUCCESS'
export type Priority = 'low' | 'medium' | 'high' | 'critical'
export type NotificationType = 'info' | 'warning' | 'error' | 'success'
export type TodoStatus = 'pending' | 'in_progress' | 'completed' | 'cancelled'
export type Trend = 'up' | 'down' | 'stable'
export type SystemHealth = 'healthy' | 'warning' | 'critical'




