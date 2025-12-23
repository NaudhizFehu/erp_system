/**
 * 재무 트렌드 차트 컴포넌트
 * Recharts 기반으로 재무 지표의 시계열 변화를 시각화합니다
 */

import {
  Area,
  AreaChart,
  CartesianGrid,
  Legend,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts'

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { accountingUtils } from '@/services/accountingApi'

export type ChartType = 'line' | 'area'
export type MetricType = 'amount' | 'ratio' | 'percentage'

interface DataPoint {
  period: string
  [key: string]: string | number
}

export interface MetricConfig {
  key: string
  name: string
  color: string
  type?: MetricType
}

interface TrendChartProps {
  title: string
  data: DataPoint[]
  metrics: MetricConfig[]
  chartType?: ChartType
  height?: number
  className?: string
}

/**
 * 메트릭 유형별 값 포맷팅
 */
function formatValue(value: number, type: MetricType = 'amount'): string {
  switch (type) {
    case 'amount':
      return accountingUtils.formatCurrency(value)
    case 'ratio':
      return value.toFixed(2)
    case 'percentage':
      return `${value.toFixed(1)}%`
    default:
      return value.toString()
  }
}

/**
 * 툴팁 커스텀 컴포넌트
 */
function CustomTooltip({ active, payload, label, metrics }: any) {
  if (!active || !payload || !payload.length) {
    return null
  }

  return (
    <div className="rounded-lg border bg-white p-3 shadow-lg">
      <p className="mb-2 font-semibold text-gray-900">{label}</p>
      {payload.map((entry: any, index: number) => {
        const metric = metrics.find(
          (m: MetricConfig) => m.key === entry.dataKey
        )
        const metricType = metric?.type || 'amount'

        return (
          <div key={index} className="flex items-center gap-2">
            <div
              className="h-3 w-3 rounded-full"
              style={{ backgroundColor: entry.color }}
            />
            <span className="text-sm text-gray-600">{entry.name}:</span>
            <span className="text-sm font-medium text-gray-900">
              {formatValue(entry.value, metricType)}
            </span>
          </div>
        )
      })}
    </div>
  )
}

/**
 * Y축 틱 포맷팅
 */
function formatYAxisTick(value: number, type: MetricType = 'amount'): string {
  switch (type) {
    case 'amount':
      // 억 단위로 변환
      if (Math.abs(value) >= 100000000) {
        return `${(value / 100000000).toFixed(1)}억`
      }
      // 만 단위로 변환
      if (Math.abs(value) >= 10000) {
        return `${(value / 10000).toFixed(0)}만`
      }
      return value.toLocaleString()
    case 'ratio':
      return value.toFixed(1)
    case 'percentage':
      return `${value}%`
    default:
      return value.toString()
  }
}

/**
 * 재무 트렌드 차트 컴포넌트
 */
export function TrendChart({
  title,
  data,
  metrics,
  chartType = 'line',
  height = 300,
  className = '',
}: TrendChartProps) {
  // 데이터가 없으면 빈 상태 표시
  if (!data || data.length === 0) {
    return (
      <Card className={className}>
        <CardHeader>
          <CardTitle>{title}</CardTitle>
        </CardHeader>
        <CardContent>
          <div
            className="flex items-center justify-center text-muted-foreground"
            style={{ height: `${height}px` }}
          >
            표시할 트렌드 데이터가 없습니다
          </div>
        </CardContent>
      </Card>
    )
  }

  // 주 메트릭 타입 결정 (첫 번째 메트릭 기준)
  const primaryMetricType = metrics[0]?.type || 'amount'

  return (
    <Card className={className}>
      <CardHeader>
        <CardTitle>{title}</CardTitle>
      </CardHeader>
      <CardContent>
        <ResponsiveContainer width="100%" height={height}>
          {chartType === 'area' ? (
            <AreaChart
              data={data}
              margin={{ top: 10, right: 30, left: 0, bottom: 0 }}
            >
              <defs>
                {metrics.map((metric, index) => (
                  <linearGradient
                    key={index}
                    id={`color-${metric.key}`}
                    x1="0"
                    y1="0"
                    x2="0"
                    y2="1"
                  >
                    <stop
                      offset="5%"
                      stopColor={metric.color}
                      stopOpacity={0.8}
                    />
                    <stop
                      offset="95%"
                      stopColor={metric.color}
                      stopOpacity={0.1}
                    />
                  </linearGradient>
                ))}
              </defs>
              <CartesianGrid
                strokeDasharray="3 3"
                className="stroke-gray-200"
              />
              <XAxis
                dataKey="period"
                className="text-xs text-gray-600"
                tick={{ fill: '#6B7280' }}
              />
              <YAxis
                className="text-xs text-gray-600"
                tick={{ fill: '#6B7280' }}
                tickFormatter={value =>
                  formatYAxisTick(value, primaryMetricType)
                }
              />
              <Tooltip content={<CustomTooltip metrics={metrics} />} />
              <Legend wrapperStyle={{ paddingTop: '20px' }} iconType="line" />
              {metrics.map((metric, index) => (
                <Area
                  key={index}
                  type="monotone"
                  dataKey={metric.key}
                  name={metric.name}
                  stroke={metric.color}
                  strokeWidth={2}
                  fillOpacity={1}
                  fill={`url(#color-${metric.key})`}
                />
              ))}
            </AreaChart>
          ) : (
            <LineChart
              data={data}
              margin={{ top: 10, right: 30, left: 0, bottom: 0 }}
            >
              <CartesianGrid
                strokeDasharray="3 3"
                className="stroke-gray-200"
              />
              <XAxis
                dataKey="period"
                className="text-xs text-gray-600"
                tick={{ fill: '#6B7280' }}
              />
              <YAxis
                className="text-xs text-gray-600"
                tick={{ fill: '#6B7280' }}
                tickFormatter={value =>
                  formatYAxisTick(value, primaryMetricType)
                }
              />
              <Tooltip content={<CustomTooltip metrics={metrics} />} />
              <Legend wrapperStyle={{ paddingTop: '20px' }} iconType="line" />
              {metrics.map((metric, index) => (
                <Line
                  key={index}
                  type="monotone"
                  dataKey={metric.key}
                  name={metric.name}
                  stroke={metric.color}
                  strokeWidth={2}
                  dot={{ r: 4, fill: metric.color }}
                  activeDot={{ r: 6 }}
                />
              ))}
            </LineChart>
          )}
        </ResponsiveContainer>
      </CardContent>
    </Card>
  )
}

/**
 * 미리 정의된 차트 색상 팔레트
 */
export const CHART_COLORS = {
  primary: '#3b82f6', // blue-500
  success: '#10b981', // green-500
  warning: '#f59e0b', // amber-500
  danger: '#ef4444', // red-500
  purple: '#8b5cf6', // violet-500
  teal: '#14b8a6', // teal-500
  pink: '#ec4899', // pink-500
  indigo: '#6366f1', // indigo-500
} as const
