/**
 * 재무제표 통합 조회 페이지
 * 재무상태표, 손익계산서 등 재무제표를 조회하고 생성합니다
 */

import {
  FileText,
  Download,
  Printer,
  TrendingUp,
  DollarSign,
  Calendar,
  Building2,
} from 'lucide-react'
import { useState } from 'react'

import { FinancialRatioCard } from '@/components/accounting/FinancialRatioCard'
import { FinancialStatementTable } from '@/components/accounting/FinancialStatementTable'
import { ReportStatusBadge } from '@/components/accounting/ReportStatusBadge'
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import {
  useLatestFinancialStatements,
  useReportsByType,
  useGenerateBalanceSheet,
  useGenerateIncomeStatement,
} from '@/hooks/useAccounting'
import { accountingUtils } from '@/services/accountingApi'
import { ReportType } from '@/types/accounting'

export default function FinancialStatementPage() {
  const currentYear = new Date().getFullYear()

  // 상태 관리
  const [companyId] = useState<number>(1) // TODO: 실제 회사 선택 로직
  const [fiscalYear, setFiscalYear] = useState<number>(currentYear)
  const [fiscalPeriod, setFiscalPeriod] = useState<string>('ANNUAL')
  const [activeTab, setActiveTab] = useState<
    'balance-sheet' | 'income-statement' | 'cash-flow'
  >('balance-sheet')

  // 데이터 조회
  const { data: latestReports, isLoading: isLoadingLatest } =
    useLatestFinancialStatements(companyId)

  const { data: balanceSheetReports } = useReportsByType(
    companyId,
    ReportType.BALANCE_SHEET
  )

  const { data: incomeStatementReports } = useReportsByType(
    companyId,
    ReportType.INCOME_STATEMENT
  )

  // 보고서 생성 뮤테이션
  const generateBalanceSheet = useGenerateBalanceSheet()
  const generateIncomeStatement = useGenerateIncomeStatement()

  // 연도 목록 생성 (2020 ~ 현재)
  const years = Array.from({ length: currentYear - 2019 }, (_, i) => 2020 + i)

  // 회계 기간 목록
  const fiscalPeriods = [
    { value: 'ANNUAL', label: '연간' },
    { value: 'Q1', label: '1분기' },
    { value: 'Q2', label: '2분기' },
    { value: 'Q3', label: '3분기' },
    { value: 'Q4', label: '4분기' },
    { value: 'M01', label: '1월' },
    { value: 'M02', label: '2월' },
    { value: 'M03', label: '3월' },
    { value: 'M04', label: '4월' },
    { value: 'M05', label: '5월' },
    { value: 'M06', label: '6월' },
    { value: 'M07', label: '7월' },
    { value: 'M08', label: '8월' },
    { value: 'M09', label: '9월' },
    { value: 'M10', label: '10월' },
    { value: 'M11', label: '11월' },
    { value: 'M12', label: '12월' },
  ]

  // 현재 탭의 보고서 데이터
  const currentReport =
    activeTab === 'balance-sheet'
      ? latestReports?.balanceSheet
      : activeTab === 'income-statement'
        ? latestReports?.incomeStatement
        : latestReports?.cashFlowStatement

  // 보고서 생성 핸들러
  const handleGenerateReport = async () => {
    const baseDate = `${fiscalYear}-12-31` // TODO: 기간별 날짜 계산

    try {
      if (activeTab === 'balance-sheet') {
        await generateBalanceSheet.mutateAsync({
          companyId,
          fiscalYear,
          fiscalPeriod,
          baseDate,
        })
      } else if (activeTab === 'income-statement') {
        const startDate = `${fiscalYear}-01-01`
        const endDate = `${fiscalYear}-12-31`
        await generateIncomeStatement.mutateAsync({
          companyId,
          fiscalYear,
          fiscalPeriod,
          startDate,
          endDate,
        })
      }
    } catch (error) {
      console.error('보고서 생성 실패:', error)
    }
  }

  if (isLoadingLatest) {
    return (
      <div className="space-y-6 p-6">
        <div className="flex items-center justify-between">
          <div className="h-8 w-48 animate-pulse rounded bg-gray-100" />
          <div className="h-10 w-32 animate-pulse rounded bg-gray-100" />
        </div>
        <div className="grid grid-cols-1 gap-4 md:grid-cols-4">
          {[...Array(4)].map((_, i) => (
            <Card key={i}>
              <CardContent className="p-6">
                <div className="h-20 animate-pulse rounded bg-gray-100" />
              </CardContent>
            </Card>
          ))}
        </div>
      </div>
    )
  }

  return (
    <div className="space-y-6 p-6">
      {/* 헤더 */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">재무제표 조회</h1>
          <p className="text-muted-foreground">
            재무상태표, 손익계산서 등 재무제표를 조회합니다
          </p>
        </div>
        <div className="flex gap-2">
          <Button variant="outline" size="sm">
            <Download className="mr-2 h-4 w-4" />
            Excel
          </Button>
          <Button variant="outline" size="sm">
            <Printer className="mr-2 h-4 w-4" />
            인쇄
          </Button>
        </div>
      </div>

      {/* 필터 */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Calendar className="h-5 w-5" />
            조회 조건
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex flex-wrap gap-4">
            {/* 회사 선택 (추후 구현) */}
            <div className="w-full md:w-auto md:min-w-[200px]">
              <label className="mb-2 block text-sm font-medium">회사</label>
              <Select value="1" disabled>
                <SelectTrigger>
                  <SelectValue placeholder="회사 선택" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="1">본사</SelectItem>
                </SelectContent>
              </Select>
            </div>

            {/* 회계연도 */}
            <div className="w-full md:w-auto md:min-w-[150px]">
              <label className="mb-2 block text-sm font-medium">회계연도</label>
              <Select
                value={fiscalYear.toString()}
                onValueChange={value => setFiscalYear(parseInt(value))}
              >
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {years.map(year => (
                    <SelectItem key={year} value={year.toString()}>
                      {year}년
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            {/* 회계기간 */}
            <div className="w-full md:w-auto md:min-w-[150px]">
              <label className="mb-2 block text-sm font-medium">회계기간</label>
              <Select value={fiscalPeriod} onValueChange={setFiscalPeriod}>
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {fiscalPeriods.map(period => (
                    <SelectItem key={period.value} value={period.value}>
                      {period.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            {/* 보고서 생성 버튼 */}
            {!currentReport && (
              <div className="flex items-end">
                <Button
                  onClick={handleGenerateReport}
                  disabled={
                    generateBalanceSheet.isPending ||
                    generateIncomeStatement.isPending
                  }
                >
                  <FileText className="mr-2 h-4 w-4" />
                  보고서 생성
                </Button>
              </div>
            )}
          </div>
        </CardContent>
      </Card>

      {/* 탭 */}
      <Tabs value={activeTab} onValueChange={v => setActiveTab(v as any)}>
        <TabsList className="grid w-full grid-cols-3">
          <TabsTrigger value="balance-sheet">재무상태표</TabsTrigger>
          <TabsTrigger value="income-statement">손익계산서</TabsTrigger>
          <TabsTrigger value="cash-flow">현금흐름표</TabsTrigger>
        </TabsList>

        {/* 재무상태표 탭 */}
        <TabsContent value="balance-sheet" className="space-y-6">
          {latestReports?.balanceSheet ? (
            <>
              {/* 재무비율 카드 */}
              <div className="grid grid-cols-1 gap-4 md:grid-cols-4">
                <FinancialRatioCard
                  title="유동비율"
                  value={latestReports.balanceSheet.currentRatio}
                  type="current"
                />
                <FinancialRatioCard
                  title="부채비율"
                  value={latestReports.balanceSheet.debtRatio}
                  type="debt"
                />
                <FinancialRatioCard
                  title="자기자본비율"
                  value={latestReports.balanceSheet.equityRatio}
                  type="equity"
                />
                <FinancialRatioCard
                  title="ROE"
                  value={latestReports.balanceSheet.roe}
                  type="roe"
                />
              </div>

              {/* 재무제표 테이블 */}
              <Card>
                <CardHeader>
                  <div className="flex items-center justify-between">
                    <div>
                      <CardTitle>
                        {latestReports.balanceSheet.reportTitle}
                      </CardTitle>
                      <CardDescription>
                        기준일: {latestReports.balanceSheet.baseDate}
                      </CardDescription>
                    </div>
                    <ReportStatusBadge
                      status={latestReports.balanceSheet.reportStatus}
                    />
                  </div>
                </CardHeader>
                <CardContent>
                  <FinancialStatementTable
                    items={latestReports.balanceSheet.reportItems || []}
                    showComparison={true}
                  />
                </CardContent>
              </Card>
            </>
          ) : (
            <Card>
              <CardContent className="p-12 text-center">
                <FileText className="mx-auto h-12 w-12 text-muted-foreground" />
                <h3 className="mt-4 text-lg font-semibold">
                  재무상태표가 없습니다
                </h3>
                <p className="mt-2 text-sm text-muted-foreground">
                  상단의 "보고서 생성" 버튼을 클릭하여 재무상태표를 생성하세요
                </p>
              </CardContent>
            </Card>
          )}
        </TabsContent>

        {/* 손익계산서 탭 */}
        <TabsContent value="income-statement" className="space-y-6">
          {latestReports?.incomeStatement ? (
            <>
              {/* 재무비율 카드 */}
              <div className="grid grid-cols-1 gap-4 md:grid-cols-4">
                <FinancialRatioCard
                  title="ROA"
                  value={latestReports.incomeStatement.roa}
                  type="roa"
                />
                <FinancialRatioCard
                  title="ROE"
                  value={latestReports.incomeStatement.roe}
                  type="roe"
                />
                <FinancialRatioCard
                  title="매출총이익률"
                  value={latestReports.incomeStatement.grossMargin}
                  type="grossMargin"
                />
                <FinancialRatioCard
                  title="순이익률"
                  value={latestReports.incomeStatement.netMargin}
                  type="netMargin"
                />
              </div>

              {/* 재무제표 테이블 */}
              <Card>
                <CardHeader>
                  <div className="flex items-center justify-between">
                    <div>
                      <CardTitle>
                        {latestReports.incomeStatement.reportTitle}
                      </CardTitle>
                      <CardDescription>
                        기간: {fiscalYear}년 {fiscalPeriod}
                      </CardDescription>
                    </div>
                    <ReportStatusBadge
                      status={latestReports.incomeStatement.reportStatus}
                    />
                  </div>
                </CardHeader>
                <CardContent>
                  <FinancialStatementTable
                    items={latestReports.incomeStatement.reportItems || []}
                    showComparison={true}
                  />
                </CardContent>
              </Card>
            </>
          ) : (
            <Card>
              <CardContent className="p-12 text-center">
                <FileText className="mx-auto h-12 w-12 text-muted-foreground" />
                <h3 className="mt-4 text-lg font-semibold">
                  손익계산서가 없습니다
                </h3>
                <p className="mt-2 text-sm text-muted-foreground">
                  상단의 "보고서 생성" 버튼을 클릭하여 손익계산서를 생성하세요
                </p>
              </CardContent>
            </Card>
          )}
        </TabsContent>

        {/* 현금흐름표 탭 */}
        <TabsContent value="cash-flow" className="space-y-6">
          <Card>
            <CardContent className="p-12 text-center">
              <FileText className="mx-auto h-12 w-12 text-muted-foreground" />
              <h3 className="mt-4 text-lg font-semibold">준비 중입니다</h3>
              <p className="mt-2 text-sm text-muted-foreground">
                현금흐름표 기능은 향후 업데이트 예정입니다
              </p>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  )
}
