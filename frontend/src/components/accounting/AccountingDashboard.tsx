/**
 * 회계 대시보드 컴포넌트
 * 재무 현황 및 주요 지표를 표시합니다
 */

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { 
  TrendingUp, 
  TrendingDown,
  DollarSign,
  PieChart,
  BarChart3,
  FileText,
  AlertTriangle,
  CheckCircle,
  Calendar,
  Calculator
} from 'lucide-react'
import {
  ResponsiveContainer,
  LineChart,
  Line,
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  PieChart as RechartsPieChart,
  Cell,
  BarChart,
  Bar,
  Legend
} from 'recharts'
import { 
  useLatestFinancialStatements,
  useFinancialTrends,
  useFinancialRatioAnalysis,
  useTransactionStatistics,
  useBalanceVerification,
  useReportsByCompany
} from '@/hooks/useAccounting'
import { accountingUtils } from '@/services/accountingApi'
import type { ChartData } from '@/types/accounting'

interface AccountingDashboardProps {
  companyId: number
  className?: string
}

/**
 * 회계 대시보드 컴포넌트
 */
export function AccountingDashboard({ companyId, className = '' }: AccountingDashboardProps) {
  const currentYear = new Date().getFullYear()
  const currentDate = new Date().toISOString().split('T')[0]
  const startOfYear = `${currentYear}-01-01`

  // 데이터 조회
  const { data: latestReports, isLoading: isLoadingReports } = useLatestFinancialStatements(companyId)
  const { data: financialTrends, isLoading: isLoadingTrends } = useFinancialTrends(companyId, 12)
  const { data: financialRatios, isLoading: isLoadingRatios } = useFinancialRatioAnalysis(companyId, currentYear)
  const { data: transactionStats, isLoading: isLoadingStats } = useTransactionStatistics(companyId, startOfYear, currentDate)
  const { data: balanceVerification } = useBalanceVerification(companyId)
  const { data: reports } = useReportsByCompany(companyId)

  // 차트 색상
  const chartColors = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4']

  // 재무 현황 카드 데이터
  const balanceSheet = latestReports?.balanceSheet
  const incomeStatement = latestReports?.incomeStatement

  // 재무비율 차트 데이터
  const ratioChartData: ChartData[] = financialRatios ? [
    { name: '유동비율', value: financialRatios.currentRatio, color: chartColors[0] },
    { name: '부채비율', value: financialRatios.debtRatio, color: chartColors[1] },
    { name: '자기자본비율', value: financialRatios.equityRatio, color: chartColors[2] },
    { name: 'ROA', value: financialRatios.roa, color: chartColors[3] },
    { name: 'ROE', value: financialRatios.roe, color: chartColors[4] }
  ] : []

  // 수익/비용 트렌드 데이터 (가상 데이터)
  const revenueTrendData = Array.from({ length: 12 }, (_, i) => ({
    month: `${i + 1}월`,
    revenue: Math.floor(Math.random() * 1000000) + 500000,
    expense: Math.floor(Math.random() * 800000) + 400000
  }))

  if (isLoadingReports || isLoadingTrends || isLoadingRatios || isLoadingStats) {
    return (
      <div className={`space-y-6 ${className}`}>
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          {[...Array(4)].map((_, i) => (
            <Card key={i}>
              <CardContent className="p-6">
                <div className="h-20 bg-gray-100 animate-pulse rounded" />
              </CardContent>
            </Card>
          ))}
        </div>
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {[...Array(4)].map((_, i) => (
            <Card key={i}>
              <CardContent className="p-6">
                <div className="h-64 bg-gray-100 animate-pulse rounded" />
              </CardContent>
            </Card>
          ))}
        </div>
      </div>
    )
  }

  return (
    <div className={`space-y-6 ${className}`}>
      {/* 주요 재무 지표 */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">총 자산</CardTitle>
            <DollarSign className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              {balanceSheet ? accountingUtils.formatCurrency(balanceSheet.totalAssets) : '₩0'}
            </div>
            <p className="text-xs text-muted-foreground">
              전월 대비 +2.5%
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">순이익</CardTitle>
            <TrendingUp className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              {incomeStatement ? accountingUtils.formatCurrency(incomeStatement.netIncome) : '₩0'}
            </div>
            <p className="text-xs text-muted-foreground">
              전월 대비 +12.3%
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">유동비율</CardTitle>
            <Calculator className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              {financialRatios ? `${financialRatios.currentRatio.toFixed(2)}` : '0.00'}
            </div>
            <p className="text-xs text-muted-foreground">
              안정적인 수준
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">거래 건수</CardTitle>
            <FileText className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              {transactionStats ? accountingUtils.formatNumber(transactionStats.totalTransactionCount) : '0'}
            </div>
            <p className="text-xs text-muted-foreground">
              이번 달 총 거래
            </p>
          </CardContent>
        </Card>
      </div>

      {/* 대차평형 상태 */}
      {balanceVerification && (
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              {balanceVerification.isBalanced ? (
                <CheckCircle className="h-5 w-5 text-green-600" />
              ) : (
                <AlertTriangle className="h-5 w-5 text-red-600" />
              )}
              대차평형 상태
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="flex items-center justify-between">
              <div>
                <Badge variant={balanceVerification.isBalanced ? "default" : "destructive"}>
                  {balanceVerification.message}
                </Badge>
                {!balanceVerification.isBalanced && (
                  <p className="text-sm text-red-600 mt-1">
                    차이: {accountingUtils.formatCurrency(balanceVerification.balanceDifference)}
                  </p>
                )}
              </div>
              <div className="text-right text-sm text-muted-foreground">
                기준일: {accountingUtils.formatDate(balanceVerification.asOfDate)}
              </div>
            </div>
          </CardContent>
        </Card>
      )}

      <Tabs defaultValue="trends" className="space-y-4">
        <TabsList className="grid w-full grid-cols-4">
          <TabsTrigger value="trends">재무 트렌드</TabsTrigger>
          <TabsTrigger value="ratios">재무비율</TabsTrigger>
          <TabsTrigger value="transactions">거래 현황</TabsTrigger>
          <TabsTrigger value="reports">보고서</TabsTrigger>
        </TabsList>

        {/* 재무 트렌드 탭 */}
        <TabsContent value="trends" className="space-y-4">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <Card>
              <CardHeader>
                <CardTitle>수익/비용 트렌드</CardTitle>
              </CardHeader>
              <CardContent>
                <ResponsiveContainer width="100%" height={300}>
                  <AreaChart data={revenueTrendData}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="month" />
                    <YAxis />
                    <Tooltip 
                      formatter={(value: number) => accountingUtils.formatCurrency(value)}
                    />
                    <Area 
                      type="monotone" 
                      dataKey="revenue" 
                      stackId="1"
                      stroke="#10b981" 
                      fill="#10b981"
                      fillOpacity={0.6}
                      name="수익"
                    />
                    <Area 
                      type="monotone" 
                      dataKey="expense" 
                      stackId="2"
                      stroke="#ef4444" 
                      fill="#ef4444"
                      fillOpacity={0.6}
                      name="비용"
                    />
                    <Legend />
                  </AreaChart>
                </ResponsiveContainer>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>자산/부채 현황</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {balanceSheet && (
                    <>
                      <div className="flex justify-between items-center">
                        <span className="text-sm font-medium">총 자산</span>
                        <span className="text-lg font-bold text-blue-600">
                          {accountingUtils.formatCurrency(balanceSheet.totalAssets)}
                        </span>
                      </div>
                      <div className="flex justify-between items-center">
                        <span className="text-sm font-medium">총 부채</span>
                        <span className="text-lg font-bold text-red-600">
                          {accountingUtils.formatCurrency(balanceSheet.totalLiabilities)}
                        </span>
                      </div>
                      <div className="flex justify-between items-center">
                        <span className="text-sm font-medium">총 자본</span>
                        <span className="text-lg font-bold text-green-600">
                          {accountingUtils.formatCurrency(balanceSheet.totalEquity)}
                        </span>
                      </div>
                      <div className="pt-4 border-t">
                        <div className="flex justify-between items-center">
                          <span className="text-sm font-medium">자기자본비율</span>
                          <span className={`text-lg font-bold ${accountingUtils.getRatioColor(balanceSheet.equityRatio, 'equity')}`}>
                            {accountingUtils.formatPercentage(balanceSheet.equityRatio)}
                          </span>
                        </div>
                      </div>
                    </>
                  )}
                </div>
              </CardContent>
            </Card>
          </div>
        </TabsContent>

        {/* 재무비율 탭 */}
        <TabsContent value="ratios" className="space-y-4">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <Card>
              <CardHeader>
                <CardTitle>재무비율 현황</CardTitle>
              </CardHeader>
              <CardContent>
                <ResponsiveContainer width="100%" height={300}>
                  <BarChart data={ratioChartData}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="name" />
                    <YAxis />
                    <Tooltip 
                      formatter={(value: number, name: string) => [
                        name.includes('비율') || name.includes('ROA') || name.includes('ROE') 
                          ? accountingUtils.formatPercentage(value) 
                          : value.toFixed(2),
                        name
                      ]}
                    />
                    <Bar dataKey="value" fill="#3b82f6" />
                  </BarChart>
                </ResponsiveContainer>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>주요 재무비율</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {financialRatios && (
                    <>
                      <div className="flex justify-between items-center">
                        <span className="text-sm font-medium">유동비율</span>
                        <span className={`text-lg font-bold ${accountingUtils.getRatioColor(financialRatios.currentRatio, 'current')}`}>
                          {financialRatios.currentRatio.toFixed(2)}
                        </span>
                      </div>
                      <div className="flex justify-between items-center">
                        <span className="text-sm font-medium">부채비율</span>
                        <span className={`text-lg font-bold ${accountingUtils.getRatioColor(financialRatios.debtRatio, 'debt')}`}>
                          {accountingUtils.formatPercentage(financialRatios.debtRatio)}
                        </span>
                      </div>
                      <div className="flex justify-between items-center">
                        <span className="text-sm font-medium">자기자본비율</span>
                        <span className={`text-lg font-bold ${accountingUtils.getRatioColor(financialRatios.equityRatio, 'equity')}`}>
                          {accountingUtils.formatPercentage(financialRatios.equityRatio)}
                        </span>
                      </div>
                      <div className="flex justify-between items-center">
                        <span className="text-sm font-medium">총자산순이익률(ROA)</span>
                        <span className={`text-lg font-bold ${accountingUtils.getRatioColor(financialRatios.roa, 'roa')}`}>
                          {accountingUtils.formatPercentage(financialRatios.roa)}
                        </span>
                      </div>
                      <div className="flex justify-between items-center">
                        <span className="text-sm font-medium">자기자본순이익률(ROE)</span>
                        <span className={`text-lg font-bold ${accountingUtils.getRatioColor(financialRatios.roe, 'roe')}`}>
                          {accountingUtils.formatPercentage(financialRatios.roe)}
                        </span>
                      </div>
                    </>
                  )}
                </div>
              </CardContent>
            </Card>
          </div>
        </TabsContent>

        {/* 거래 현황 탭 */}
        <TabsContent value="transactions" className="space-y-4">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <Card>
              <CardHeader>
                <CardTitle>거래 유형별 현황</CardTitle>
              </CardHeader>
              <CardContent>
                {transactionStats && (
                  <ResponsiveContainer width="100%" height={300}>
                    <RechartsPieChart>
                      <Pie
                        data={Object.entries(transactionStats.transactionCountByType).map(([type, count], index) => ({
                          name: type,
                          value: count,
                          fill: chartColors[index % chartColors.length]
                        }))}
                        cx="50%"
                        cy="50%"
                        labelLine={false}
                        outerRadius={80}
                        fill="#8884d8"
                        dataKey="value"
                        label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                      >
                        {Object.entries(transactionStats.transactionCountByType).map((_, index) => (
                          <Cell key={`cell-${index}`} fill={chartColors[index % chartColors.length]} />
                        ))}
                      </Pie>
                      <Tooltip />
                    </RechartsPieChart>
                  </ResponsiveContainer>
                )}
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>거래 통계</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {transactionStats && (
                    <>
                      <div className="flex justify-between items-center">
                        <span className="text-sm font-medium">총 거래 건수</span>
                        <span className="text-lg font-bold">
                          {accountingUtils.formatNumber(transactionStats.totalTransactionCount)}건
                        </span>
                      </div>
                      <div className="flex justify-between items-center">
                        <span className="text-sm font-medium">총 거래 금액</span>
                        <span className="text-lg font-bold">
                          {accountingUtils.formatCurrency(transactionStats.totalTransactionAmount)}
                        </span>
                      </div>
                      <div className="flex justify-between items-center">
                        <span className="text-sm font-medium">차변 합계</span>
                        <span className="text-lg font-bold text-blue-600">
                          {accountingUtils.formatCurrency(transactionStats.totalDebitAmount)}
                        </span>
                      </div>
                      <div className="flex justify-between items-center">
                        <span className="text-sm font-medium">대변 합계</span>
                        <span className="text-lg font-bold text-green-600">
                          {accountingUtils.formatCurrency(transactionStats.totalCreditAmount)}
                        </span>
                      </div>
                    </>
                  )}
                </div>
              </CardContent>
            </Card>
          </div>
        </TabsContent>

        {/* 보고서 탭 */}
        <TabsContent value="reports" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center justify-between">
                최근 생성된 보고서
                <Button size="sm" variant="outline">
                  <FileText className="mr-2 h-4 w-4" />
                  전체 보고서
                </Button>
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-3">
                {reports && reports.slice(0, 5).map((report) => (
                  <div key={report.id} className="flex items-center justify-between p-3 border rounded-lg">
                    <div className="flex items-center space-x-3">
                      <FileText className="h-5 w-5 text-muted-foreground" />
                      <div>
                        <p className="font-medium">{report.reportTitle}</p>
                        <p className="text-sm text-muted-foreground">
                          {accountingUtils.formatDate(report.createdAt)}
                        </p>
                      </div>
                    </div>
                    <div className="flex items-center space-x-2">
                      <Badge 
                        variant={
                          report.reportStatus === 'APPROVED' ? 'default' :
                          report.reportStatus === 'DRAFT' ? 'secondary' : 'outline'
                        }
                      >
                        {report.reportStatus}
                      </Badge>
                      <Button size="sm" variant="ghost">
                        보기
                      </Button>
                    </div>
                  </div>
                ))}
                {(!reports || reports.length === 0) && (
                  <div className="text-center py-8 text-muted-foreground">
                    생성된 보고서가 없습니다.
                  </div>
                )}
              </div>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  )
}




