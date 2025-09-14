/**
 * 매출 차트 컴포넌트
 * 매출 관련 다양한 차트를 표시합니다
 */

import React, { useState } from 'react'
import {
  LineChart,
  Line,
  AreaChart,
  Area,
  BarChart,
  Bar,
  PieChart,
  Pie,
  Cell,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer
} from 'recharts'
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
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { 
  TrendingUp, 
  TrendingDown, 
  BarChart3, 
  LineChart as LineChartIcon,
  PieChart as PieChartIcon,
  RefreshCw,
  Download,
  Filter
} from 'lucide-react'
import { formatCurrency } from '@/utils/format'
import type { RevenueChart, ChartDataPoint } from '@/types/dashboard'

interface RevenueChartProps {
  data: RevenueChart
  loading?: boolean
  error?: string
  className?: string
  onRefresh?: () => void
  onExport?: () => void
}

/**
 * 차트 데이터를 Recharts 형식으로 변환
 */
const transformChartData = (data: ChartDataPoint[]) => {
  return data.map(item => ({
    name: item.label,
    value: item.value,
    date: item.date,
    color: item.color,
    // 추가 포맷팅
    formattedValue: formatCurrency(item.value),
    shortName: item.label.length > 10 ? item.label.substring(0, 10) + '...' : item.label
  }))
}

/**
 * 커스텀 툴팁 컴포넌트
 */
const CustomTooltip = ({ active, payload, label }: any) => {
  if (active && payload && payload.length) {
    return (
      <div className="bg-white p-3 border rounded-lg shadow-lg">
        <p className="font-medium text-gray-900">{label}</p>
        {payload.map((entry: any, index: number) => (
          <p key={index} className="text-sm" style={{ color: entry.color }}>
            {entry.name}: <span className="font-semibold">{formatCurrency(entry.value)}</span>
          </p>
        ))}
      </div>
    )
  }
  return null
}

/**
 * 트렌드 표시 컴포넌트
 */
const TrendIndicator = ({ current, previous }: { current: number; previous: number }) => {
  const change = ((current - previous) / previous) * 100
  const isPositive = change > 0
  
  return (
    <div className="flex items-center space-x-1">
      {isPositive ? (
        <TrendingUp className="h-4 w-4 text-green-500" />
      ) : (
        <TrendingDown className="h-4 w-4 text-red-500" />
      )}
      <span className={`text-sm font-medium ${isPositive ? 'text-green-600' : 'text-red-600'}`}>
        {Math.abs(change).toFixed(1)}%
      </span>
    </div>
  )
}

export default function RevenueChart({
  data,
  loading = false,
  error,
  className = '',
  onRefresh,
  onExport
}: RevenueChartProps) {
  const [activeTab, setActiveTab] = useState('monthly')
  const [chartType, setChartType] = useState<'line' | 'bar' | 'area'>('line')

  // 차트 데이터 변환
  const monthlyData = transformChartData(data?.monthlyRevenue || [])
  const dailyData = transformChartData(data?.dailyRevenue || [])
  const categoryData = transformChartData(data?.revenueByCategory || [])
  const customerData = transformChartData(data?.revenueByCustomer || [])

  // 총 매출 계산
  const totalRevenue = monthlyData.reduce((sum, item) => sum + item.value, 0)
  const previousTotal = monthlyData.length > 1 ? 
    monthlyData.slice(0, -1).reduce((sum, item) => sum + item.value, 0) : totalRevenue

  if (loading) {
    return (
      <Card className={className}>
        <CardHeader>
          <div className="flex items-center justify-between">
            <CardTitle className="flex items-center space-x-2">
              <BarChart3 className="h-5 w-5" />
              <span>매출 분석</span>
            </CardTitle>
          </div>
        </CardHeader>
        <CardContent>
          <div className="h-80 flex items-center justify-center">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
          </div>
        </CardContent>
      </Card>
    )
  }

  if (error) {
    return (
      <Card className={className}>
        <CardHeader>
          <CardTitle className="flex items-center space-x-2">
            <BarChart3 className="h-5 w-5" />
            <span>매출 분석</span>
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="h-80 flex items-center justify-center">
            <div className="text-center">
              <p className="text-red-500 mb-2">데이터 로딩 중 오류가 발생했습니다</p>
              <Button variant="outline" size="sm" onClick={onRefresh}>
                <RefreshCw className="h-4 w-4 mr-2" />
                다시 시도
              </Button>
            </div>
          </div>
        </CardContent>
      </Card>
    )
  }

  return (
    <Card className={className}>
      <CardHeader>
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <BarChart3 className="h-5 w-5" />
            <CardTitle>매출 분석</CardTitle>
            <Badge variant="secondary">{formatCurrency(totalRevenue)}</Badge>
          </div>
          <div className="flex items-center space-x-2">
            <TrendIndicator current={totalRevenue} previous={previousTotal} />
            <Select value={chartType} onValueChange={(value: any) => setChartType(value)}>
              <SelectTrigger className="w-24">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="line">
                  <div className="flex items-center space-x-2">
                    <LineChartIcon className="h-4 w-4" />
                    <span>선형</span>
                  </div>
                </SelectItem>
                <SelectItem value="bar">
                  <div className="flex items-center space-x-2">
                    <BarChart3 className="h-4 w-4" />
                    <span>막대</span>
                  </div>
                </SelectItem>
                <SelectItem value="area">
                  <div className="flex items-center space-x-2">
                    <PieChartIcon className="h-4 w-4" />
                    <span>영역</span>
                  </div>
                </SelectItem>
              </SelectContent>
            </Select>
            {onRefresh && (
              <Button variant="ghost" size="sm" onClick={onRefresh}>
                <RefreshCw className="h-4 w-4" />
              </Button>
            )}
            {onExport && (
              <Button variant="ghost" size="sm" onClick={onExport}>
                <Download className="h-4 w-4" />
              </Button>
            )}
          </div>
        </div>
      </CardHeader>
      <CardContent>
        <Tabs value={activeTab} onValueChange={setActiveTab} className="space-y-4">
          <TabsList className="grid w-full grid-cols-4">
            <TabsTrigger value="monthly">월별</TabsTrigger>
            <TabsTrigger value="daily">일별</TabsTrigger>
            <TabsTrigger value="category">카테고리별</TabsTrigger>
            <TabsTrigger value="customer">고객별</TabsTrigger>
          </TabsList>

          <TabsContent value="monthly" className="space-y-4">
            <div className="h-80">
              <ResponsiveContainer width="100%" height="100%">
                {chartType === 'line' && (
                  <LineChart data={monthlyData}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                    <XAxis 
                      dataKey="name" 
                      tick={{ fontSize: 12 }}
                      tickLine={false}
                    />
                    <YAxis 
                      tick={{ fontSize: 12 }}
                      tickLine={false}
                      tickFormatter={(value) => formatCurrency(value, true)}
                    />
                    <Tooltip content={<CustomTooltip />} />
                    <Legend />
                    <Line 
                      type="monotone" 
                      dataKey="value" 
                      stroke="#3b82f6" 
                      strokeWidth={3}
                      dot={{ fill: '#3b82f6', strokeWidth: 2, r: 4 }}
                      activeDot={{ r: 6, stroke: '#3b82f6', strokeWidth: 2 }}
                      name="매출액"
                    />
                  </LineChart>
                )}
                {chartType === 'bar' && (
                  <BarChart data={monthlyData}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                    <XAxis 
                      dataKey="name" 
                      tick={{ fontSize: 12 }}
                      tickLine={false}
                    />
                    <YAxis 
                      tick={{ fontSize: 12 }}
                      tickLine={false}
                      tickFormatter={(value) => formatCurrency(value, true)}
                    />
                    <Tooltip content={<CustomTooltip />} />
                    <Legend />
                    <Bar 
                      dataKey="value" 
                      fill="#3b82f6" 
                      radius={[4, 4, 0, 0]}
                      name="매출액"
                    />
                  </BarChart>
                )}
                {chartType === 'area' && (
                  <AreaChart data={monthlyData}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                    <XAxis 
                      dataKey="name" 
                      tick={{ fontSize: 12 }}
                      tickLine={false}
                    />
                    <YAxis 
                      tick={{ fontSize: 12 }}
                      tickLine={false}
                      tickFormatter={(value) => formatCurrency(value, true)}
                    />
                    <Tooltip content={<CustomTooltip />} />
                    <Legend />
                    <Area 
                      type="monotone" 
                      dataKey="value" 
                      stroke="#3b82f6" 
                      fill="#3b82f6" 
                      fillOpacity={0.1}
                      strokeWidth={2}
                      name="매출액"
                    />
                  </AreaChart>
                )}
              </ResponsiveContainer>
            </div>
          </TabsContent>

          <TabsContent value="daily" className="space-y-4">
            <div className="h-80">
              <ResponsiveContainer width="100%" height="100%">
                <AreaChart data={dailyData}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                  <XAxis 
                    dataKey="name" 
                    tick={{ fontSize: 12 }}
                    tickLine={false}
                  />
                  <YAxis 
                    tick={{ fontSize: 12 }}
                    tickLine={false}
                    tickFormatter={(value) => formatCurrency(value, true)}
                  />
                  <Tooltip content={<CustomTooltip />} />
                  <Legend />
                  <Area 
                    type="monotone" 
                    dataKey="value" 
                    stroke="#10b981" 
                    fill="#10b981" 
                    fillOpacity={0.1}
                    strokeWidth={2}
                    name="일별 매출"
                  />
                </AreaChart>
              </ResponsiveContainer>
            </div>
          </TabsContent>

          <TabsContent value="category" className="space-y-4">
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
              <div className="h-80">
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie
                      data={categoryData}
                      cx="50%"
                      cy="50%"
                      innerRadius={60}
                      outerRadius={120}
                      paddingAngle={5}
                      dataKey="value"
                    >
                      {categoryData.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={entry.color} />
                      ))}
                    </Pie>
                    <Tooltip content={<CustomTooltip />} />
                    <Legend />
                  </PieChart>
                </ResponsiveContainer>
              </div>
              <div className="space-y-3">
                <h4 className="font-medium text-gray-900">카테고리별 매출</h4>
                {categoryData.map((item, index) => (
                  <div key={index} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                    <div className="flex items-center space-x-3">
                      <div 
                        className="w-4 h-4 rounded-full" 
                        style={{ backgroundColor: item.color }}
                      />
                      <span className="font-medium">{item.name}</span>
                    </div>
                    <div className="text-right">
                      <div className="font-semibold">{formatCurrency(item.value)}</div>
                      <div className="text-sm text-gray-500">
                        {((item.value / totalRevenue) * 100).toFixed(1)}%
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </TabsContent>

          <TabsContent value="customer" className="space-y-4">
            <div className="h-80">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={customerData} layout="horizontal">
                  <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                  <XAxis 
                    type="number"
                    tick={{ fontSize: 12 }}
                    tickLine={false}
                    tickFormatter={(value) => formatCurrency(value, true)}
                  />
                  <YAxis 
                    type="category"
                    dataKey="shortName" 
                    tick={{ fontSize: 12 }}
                    tickLine={false}
                    width={100}
                  />
                  <Tooltip content={<CustomTooltip />} />
                  <Legend />
                  <Bar 
                    dataKey="value" 
                    fill="#f59e0b" 
                    radius={[0, 4, 4, 0]}
                    name="매출액"
                  />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </TabsContent>
        </Tabs>
      </CardContent>
    </Card>
  )
}




