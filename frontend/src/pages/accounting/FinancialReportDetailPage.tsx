/**
 * 재무보고서 상세 페이지
 * 재무보고서의 상세 정보, 재무비율, 보고서 내역, 승인 워크플로우를 표시합니다
 */

import { useQuery } from '@tanstack/react-query'
import {
  ArrowLeft,
  CheckCircle,
  Download,
  FileText,
  Printer,
  TrendingUp,
  X,
} from 'lucide-react'
import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'

import { FinancialRatioCard } from '@/components/accounting/FinancialRatioCard'
import { FinancialStatementTable } from '@/components/accounting/FinancialStatementTable'
import { ReportStatusBadge } from '@/components/accounting/ReportStatusBadge'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { Separator } from '@/components/ui/separator'
import {
  useApproveReport,
  useDeleteReport,
  ACCOUNTING_QUERY_KEYS,
} from '@/hooks/useAccounting'
import { accountingUtils, reportApi } from '@/services/accountingApi'
import { ReportStatus, ReportType } from '@/types/accounting'

/**
 * 보고서 유형 한글 라벨
 */
const REPORT_TYPE_LABELS = {
  [ReportType.BALANCE_SHEET]: '재무상태표',
  [ReportType.INCOME_STATEMENT]: '손익계산서',
  [ReportType.CASH_FLOW_STATEMENT]: '현금흐름표',
  [ReportType.EQUITY_STATEMENT]: '자본변동표',
} as const

export default function FinancialReportDetailPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const reportId = parseInt(id || '0')

  // 보고서 상세 조회
  const { data: report, isLoading } = useQuery({
    queryKey: [...ACCOUNTING_QUERY_KEYS.reports, 'detail', reportId],
    queryFn: () => reportApi.getReportById(reportId),
    enabled: !!reportId,
    staleTime: 2 * 60 * 1000,
    retry: 3,
  })

  // 보고서 승인 mutation
  const approveMutation = useApproveReport()

  // 보고서 삭제 mutation
  const deleteMutation = useDeleteReport()

  // 보고서 승인 핸들러
  const handleApprove = async () => {
    if (!report || !confirm('이 보고서를 승인하시겠습니까?')) return

    try {
      // approverId는 실제 구현 시 로그인한 사용자 ID로 대체
      await approveMutation.mutateAsync({ reportId: report.id, approverId: 1 })
      navigate('/accounting/reports')
    } catch (error) {
      console.error('보고서 승인 실패:', error)
    }
  }

  // 보고서 삭제 핸들러
  const handleDelete = async () => {
    if (
      !report ||
      !confirm('이 보고서를 삭제하시겠습니까? 이 작업은 취소할 수 없습니다.')
    )
      return

    try {
      await deleteMutation.mutateAsync(report.id)
      navigate('/accounting/reports')
    } catch (error) {
      console.error('보고서 삭제 실패:', error)
    }
  }

  // PDF 내보내기 (플레이스홀더)
  const handleExportPDF = () => {
    alert('PDF 내보내기 기능은 곧 지원됩니다.')
  }

  // Excel 내보내기 (플레이스홀더)
  const handleExportExcel = () => {
    alert('Excel 내보내기 기능은 곧 지원됩니다.')
  }

  // 인쇄 (플레이스홀더)
  const handlePrint = () => {
    window.print()
  }

  if (isLoading) {
    return (
      <div className="container mx-auto space-y-6 py-6">
        <div className="h-12 w-3/4 animate-pulse rounded bg-gray-200" />
        <div className="h-64 w-full animate-pulse rounded bg-gray-200" />
        <div className="h-96 w-full animate-pulse rounded bg-gray-200" />
      </div>
    )
  }

  if (!report) {
    return (
      <div className="container mx-auto py-6">
        <Card>
          <CardContent className="p-12 text-center">
            <FileText className="mx-auto mb-4 h-12 w-12 text-muted-foreground" />
            <h2 className="mb-2 text-xl font-semibold">
              보고서를 찾을 수 없습니다
            </h2>
            <p className="mb-4 text-muted-foreground">
              요청하신 보고서가 존재하지 않거나 삭제되었습니다.
            </p>
            <Button onClick={() => navigate('/accounting/reports')}>
              <ArrowLeft className="mr-2 h-4 w-4" />
              목록으로 돌아가기
            </Button>
          </CardContent>
        </Card>
      </div>
    )
  }

  return (
    <div className="container mx-auto space-y-6 py-6">
      {/* 헤더 */}
      <div className="flex items-start justify-between">
        <div className="flex items-center gap-4">
          <Button
            variant="ghost"
            size="icon"
            onClick={() => navigate('/accounting/reports')}
          >
            <ArrowLeft className="h-4 w-4" />
          </Button>
          <div>
            <h1 className="text-3xl font-bold">{report.reportTitle}</h1>
            <p className="text-muted-foreground">
              {REPORT_TYPE_LABELS[report.reportType]} • {report.fiscalYear}년{' '}
              {report.fiscalPeriod}
            </p>
          </div>
        </div>
        <div className="flex gap-2">
          <Button variant="outline" size="sm" onClick={handleExportPDF}>
            <Download className="mr-2 h-4 w-4" />
            PDF
          </Button>
          <Button variant="outline" size="sm" onClick={handleExportExcel}>
            <Download className="mr-2 h-4 w-4" />
            Excel
          </Button>
          <Button variant="outline" size="sm" onClick={handlePrint}>
            <Printer className="mr-2 h-4 w-4" />
            인쇄
          </Button>
        </div>
      </div>

      {/* 보고서 기본 정보 */}
      <Card>
        <CardHeader>
          <div className="flex items-center justify-between">
            <CardTitle>보고서 정보</CardTitle>
            <ReportStatusBadge status={report.reportStatus} />
          </div>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-2 gap-4 md:grid-cols-4">
            <div>
              <p className="text-sm font-medium text-muted-foreground">
                보고서 유형
              </p>
              <Badge variant="outline">
                {REPORT_TYPE_LABELS[report.reportType]}
              </Badge>
            </div>
            <div>
              <p className="text-sm font-medium text-muted-foreground">
                회계기간
              </p>
              <p className="text-sm">
                {report.fiscalYear}년 {report.fiscalPeriod}
              </p>
            </div>
            <div>
              <p className="text-sm font-medium text-muted-foreground">
                기준일자
              </p>
              <p className="text-sm">{report.baseDate}</p>
            </div>
            <div>
              <p className="text-sm font-medium text-muted-foreground">
                생성일시
              </p>
              <p className="text-sm">
                {new Date(report.createdAt).toLocaleString()}
              </p>
            </div>
          </div>

          {report.approvedBy && (
            <>
              <Separator />
              <div className="grid grid-cols-2 gap-4 md:grid-cols-4">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">
                    승인자
                  </p>
                  <p className="text-sm">{report.approvedBy.name}</p>
                </div>
                <div>
                  <p className="text-sm font-medium text-muted-foreground">
                    승인일시
                  </p>
                  <p className="text-sm">
                    {report.approvedAt
                      ? new Date(report.approvedAt).toLocaleString()
                      : '-'}
                  </p>
                </div>
              </div>
            </>
          )}
        </CardContent>
      </Card>

      {/* 재무 요약 */}
      <Card>
        <CardHeader>
          <CardTitle>재무 요약</CardTitle>
          <CardDescription>핵심 재무 지표</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 gap-4 md:grid-cols-4">
            <div className="rounded-lg bg-blue-50 p-4 text-center">
              <p className="mb-1 text-sm font-medium text-muted-foreground">
                총자산
              </p>
              <p className="text-2xl font-bold text-blue-700">
                {accountingUtils.formatCurrency(report.totalAssets)}
              </p>
            </div>
            <div className="rounded-lg bg-red-50 p-4 text-center">
              <p className="mb-1 text-sm font-medium text-muted-foreground">
                총부채
              </p>
              <p className="text-2xl font-bold text-red-700">
                {accountingUtils.formatCurrency(report.totalLiabilities)}
              </p>
            </div>
            <div className="rounded-lg bg-green-50 p-4 text-center">
              <p className="mb-1 text-sm font-medium text-muted-foreground">
                총자본
              </p>
              <p className="text-2xl font-bold text-green-700">
                {accountingUtils.formatCurrency(report.totalEquity)}
              </p>
            </div>
            <div className="rounded-lg bg-purple-50 p-4 text-center">
              <p className="mb-1 text-sm font-medium text-muted-foreground">
                당기순이익
              </p>
              <p className="text-2xl font-bold text-purple-700">
                {accountingUtils.formatCurrency(report.netIncome)}
              </p>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* 재무비율 */}
      <div>
        <h2 className="mb-4 text-xl font-semibold">재무비율 분석</h2>
        <div className="grid grid-cols-1 gap-4 md:grid-cols-4">
          <FinancialRatioCard
            title="유동비율"
            value={report.currentRatio}
            type="current"
          />
          <FinancialRatioCard
            title="부채비율"
            value={report.debtRatio}
            type="debt"
          />
          <FinancialRatioCard
            title="자기자본비율"
            value={report.equityRatio}
            type="equity"
          />
          <FinancialRatioCard
            title="ROA (총자산이익률)"
            value={report.roa}
            type="roa"
          />
        </div>
        <div className="mt-4 grid grid-cols-1 gap-4 md:grid-cols-3">
          <FinancialRatioCard
            title="ROE (자기자본이익률)"
            value={report.roe}
            type="roe"
          />
          <FinancialRatioCard
            title="매출총이익률"
            value={report.grossMargin}
            type="grossMargin"
          />
          <FinancialRatioCard
            title="순이익률"
            value={report.netMargin}
            type="netMargin"
          />
        </div>
      </div>

      {/* 보고서 내역 테이블 */}
      {report.reportItems && report.reportItems.length > 0 && (
        <Card>
          <CardHeader>
            <CardTitle>보고서 내역</CardTitle>
            <CardDescription>
              {report.reportItems.filter(item => item.isVisible).length}개 항목
            </CardDescription>
          </CardHeader>
          <CardContent>
            <FinancialStatementTable
              items={report.reportItems}
              showComparison={true}
            />
          </CardContent>
        </Card>
      )}

      {/* 승인 워크플로우 액션 */}
      {report.reportStatus !== ReportStatus.PUBLISHED && (
        <Card>
          <CardHeader>
            <CardTitle>보고서 관리</CardTitle>
            <CardDescription>보고서 상태 변경 및 관리 작업</CardDescription>
          </CardHeader>
          <CardContent className="flex gap-2">
            {(report.reportStatus === ReportStatus.GENERATED ||
              report.reportStatus === ReportStatus.REVIEWED) && (
              <Button
                onClick={handleApprove}
                disabled={approveMutation.isPending}
              >
                <CheckCircle className="mr-2 h-4 w-4" />
                {approveMutation.isPending ? '승인 중...' : '승인'}
              </Button>
            )}
            <Button
              variant="destructive"
              onClick={handleDelete}
              disabled={deleteMutation.isPending}
            >
              <X className="mr-2 h-4 w-4" />
              {deleteMutation.isPending ? '삭제 중...' : '삭제'}
            </Button>
          </CardContent>
        </Card>
      )}
    </div>
  )
}
