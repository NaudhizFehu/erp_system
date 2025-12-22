/**
 * 재무보고서 목록 페이지
 * 재무보고서 조회, 검색, 필터링 및 관리를 담당하는 페이지
 */

import {
  Search,
  Filter as FilterIcon,
  MoreHorizontal,
  Eye,
  CheckCircle,
  X,
  FileText,
  Calendar,
  TrendingUp,
} from 'lucide-react'
import { useState } from 'react'
import { useNavigate } from 'react-router-dom'

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
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { Input } from '@/components/ui/input'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import {
  useReportSearch,
  useApproveReport,
  useDeleteReport,
  useReportStatistics,
} from '@/hooks/useAccounting'
import { useDebounce } from '@/hooks/useDebounce'
import { accountingUtils } from '@/services/accountingApi'
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

export default function FinancialReportListPage() {
  const navigate = useNavigate()
  const currentYear = new Date().getFullYear()

  // 검색 및 필터 상태
  const [searchTerm, setSearchTerm] = useState('')
  const [reportType, setReportType] = useState<ReportType | 'ALL'>('ALL')
  const [reportStatus, setReportStatus] = useState<ReportStatus | 'ALL'>('ALL')
  const [showFilters, setShowFilters] = useState(false)

  // 필터 - 회계연도
  const [fiscalYear, setFiscalYear] = useState<number>(currentYear)

  // 페이지네이션
  const [page, setPage] = useState(0)
  const [size] = useState(20)

  // 검색어 디바운스
  const debouncedSearchTerm = useDebounce(searchTerm, 300)

  // 데이터 조회
  const { data, isLoading, refetch } = useReportSearch(debouncedSearchTerm, {
    page,
    size,
    reportType: reportType !== 'ALL' ? (reportType as ReportType) : undefined,
    reportStatus:
      reportStatus !== 'ALL' ? (reportStatus as ReportStatus) : undefined,
    fiscalYear: fiscalYear || undefined,
  })

  // 통계 조회 (companyId=1 고정)
  const { data: stats } = useReportStatistics(1, fiscalYear)

  // 보고서 승인 mutation
  const approveMutation = useApproveReport()

  // 보고서 삭제 mutation
  const deleteMutation = useDeleteReport()

  // 보고서 승인 핸들러
  const handleApproveReport = async (reportId: number) => {
    if (!confirm('이 보고서를 승인하시겠습니까?')) return

    try {
      // approverId는 실제 구현 시 로그인한 사용자 ID로 대체
      await approveMutation.mutateAsync({ reportId, approverId: 1 })
      refetch()
    } catch (error) {
      console.error('보고서 승인 실패:', error)
    }
  }

  // 보고서 삭제 핸들러
  const handleDeleteReport = async (reportId: number) => {
    if (!confirm('이 보고서를 삭제하시겠습니까? 이 작업은 취소할 수 없습니다.'))
      return

    try {
      await deleteMutation.mutateAsync(reportId)
      refetch()
    } catch (error) {
      console.error('보고서 삭제 실패:', error)
    }
  }

  // 필터 초기화
  const handleResetFilters = () => {
    setSearchTerm('')
    setReportType('ALL')
    setReportStatus('ALL')
    setFiscalYear(currentYear)
    setPage(0)
  }

  // 통계 계산
  const reportStats = {
    total: stats?.totalReports || 0,
    pending:
      data?.content.filter(r => r.reportStatus === ReportStatus.GENERATED)
        .length || 0,
    approved:
      data?.content.filter(r => r.reportStatus === ReportStatus.APPROVED)
        .length || 0,
  }

  return (
    <div className="container mx-auto space-y-6 py-6">
      {/* 헤더 */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">재무보고서</h1>
          <p className="text-muted-foreground">
            재무제표 및 보고서 조회 및 관리
          </p>
        </div>
        <Button onClick={() => navigate('/accounting/financial-statements')}>
          <TrendingUp className="mr-2 h-4 w-4" />
          재무제표 조회
        </Button>
      </div>

      {/* 통계 카드 */}
      <div className="grid grid-cols-1 gap-4 md:grid-cols-4">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">총 보고서</CardTitle>
            <FileText className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{reportStats.total}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">승인 대기</CardTitle>
            <Calendar className="h-4 w-4 text-blue-500" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-blue-600">
              {reportStats.pending}
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">승인 완료</CardTitle>
            <CheckCircle className="h-4 w-4 text-green-500" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-green-600">
              {reportStats.approved}
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">현재 페이지</CardTitle>
            <Search className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              {page + 1} / {data?.totalPages || 1}
            </div>
          </CardContent>
        </Card>
      </div>

      {/* 검색 및 필터 */}
      <Card>
        <CardHeader>
          <div className="flex items-center justify-between">
            <CardTitle>보고서 검색</CardTitle>
            <Button
              variant="ghost"
              size="sm"
              onClick={() => setShowFilters(!showFilters)}
            >
              <FilterIcon className="mr-2 h-4 w-4" />
              {showFilters ? '필터 숨기기' : '필터 표시'}
            </Button>
          </div>
        </CardHeader>
        <CardContent className="space-y-4">
          {/* 검색바 */}
          <div className="flex gap-2">
            <div className="relative flex-1">
              <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
              <Input
                placeholder="보고서명, 회계기간 검색..."
                value={searchTerm}
                onChange={e => setSearchTerm(e.target.value)}
                className="pl-8"
              />
            </div>
            {(searchTerm ||
              reportType !== 'ALL' ||
              reportStatus !== 'ALL' ||
              fiscalYear !== currentYear) && (
              <Button variant="ghost" onClick={handleResetFilters}>
                <X className="mr-2 h-4 w-4" />
                초기화
              </Button>
            )}
          </div>

          {/* 필터 옵션 */}
          {showFilters && (
            <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
              {/* 회계연도 */}
              <div className="space-y-2">
                <label className="text-sm font-medium">회계연도</label>
                <Select
                  value={fiscalYear.toString()}
                  onValueChange={value => setFiscalYear(parseInt(value))}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="회계연도 선택" />
                  </SelectTrigger>
                  <SelectContent>
                    {Array.from(
                      { length: currentYear - 2019 },
                      (_, i) => currentYear - i
                    ).map(year => (
                      <SelectItem key={year} value={year.toString()}>
                        {year}년
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>

              {/* 보고서 유형 */}
              <div className="space-y-2">
                <label className="text-sm font-medium">보고서 유형</label>
                <Select
                  value={reportType}
                  onValueChange={value =>
                    setReportType(value as ReportType | 'ALL')
                  }
                >
                  <SelectTrigger>
                    <SelectValue placeholder="전체" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="ALL">전체</SelectItem>
                    {Object.values(ReportType).map(type => (
                      <SelectItem key={type} value={type}>
                        {REPORT_TYPE_LABELS[type]}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>

              {/* 보고서 상태 */}
              <div className="space-y-2">
                <label className="text-sm font-medium">상태</label>
                <Select
                  value={reportStatus}
                  onValueChange={value =>
                    setReportStatus(value as ReportStatus | 'ALL')
                  }
                >
                  <SelectTrigger>
                    <SelectValue placeholder="전체" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="ALL">전체</SelectItem>
                    {Object.values(ReportStatus).map(status => (
                      <SelectItem key={status} value={status}>
                        {status}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
            </div>
          )}
        </CardContent>
      </Card>

      {/* 보고서 테이블 */}
      <Card>
        <CardHeader>
          <CardTitle>보고서 목록</CardTitle>
          <CardDescription>
            {data?.totalElements || 0}개의 보고서
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="rounded-md border">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>보고서명</TableHead>
                  <TableHead>유형</TableHead>
                  <TableHead>회계기간</TableHead>
                  <TableHead className="text-right">총자산</TableHead>
                  <TableHead className="text-right">총부채</TableHead>
                  <TableHead className="text-right">총자본</TableHead>
                  <TableHead>상태</TableHead>
                  <TableHead className="w-[70px]">작업</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {isLoading ? (
                  <TableRow>
                    <TableCell colSpan={8} className="text-center">
                      로딩 중...
                    </TableCell>
                  </TableRow>
                ) : data?.content && data.content.length > 0 ? (
                  data.content.map(report => (
                    <TableRow key={report.id}>
                      <TableCell className="font-medium">
                        {report.reportTitle}
                      </TableCell>
                      <TableCell>
                        <Badge variant="outline">
                          {REPORT_TYPE_LABELS[report.reportType]}
                        </Badge>
                      </TableCell>
                      <TableCell>
                        {report.fiscalYear}년 {report.fiscalPeriod}
                      </TableCell>
                      <TableCell className="text-right">
                        {accountingUtils.formatCurrency(report.totalAssets)}
                      </TableCell>
                      <TableCell className="text-right">
                        {accountingUtils.formatCurrency(
                          report.totalLiabilities
                        )}
                      </TableCell>
                      <TableCell className="text-right">
                        {accountingUtils.formatCurrency(report.totalEquity)}
                      </TableCell>
                      <TableCell>
                        <ReportStatusBadge status={report.reportStatus} />
                      </TableCell>
                      <TableCell>
                        <DropdownMenu>
                          <DropdownMenuTrigger asChild>
                            <Button variant="ghost" size="icon">
                              <MoreHorizontal className="h-4 w-4" />
                            </Button>
                          </DropdownMenuTrigger>
                          <DropdownMenuContent align="end">
                            <DropdownMenuItem
                              onClick={() =>
                                navigate(`/accounting/reports/${report.id}`)
                              }
                            >
                              <Eye className="mr-2 h-4 w-4" />
                              상세 보기
                            </DropdownMenuItem>
                            {(report.reportStatus === ReportStatus.GENERATED ||
                              report.reportStatus ===
                                ReportStatus.REVIEWED) && (
                              <>
                                <DropdownMenuSeparator />
                                <DropdownMenuItem
                                  onClick={() => handleApproveReport(report.id)}
                                >
                                  <CheckCircle className="mr-2 h-4 w-4" />
                                  승인
                                </DropdownMenuItem>
                              </>
                            )}
                            {report.reportStatus !== ReportStatus.PUBLISHED && (
                              <>
                                <DropdownMenuSeparator />
                                <DropdownMenuItem
                                  onClick={() => handleDeleteReport(report.id)}
                                  className="text-red-600"
                                >
                                  <X className="mr-2 h-4 w-4" />
                                  삭제
                                </DropdownMenuItem>
                              </>
                            )}
                          </DropdownMenuContent>
                        </DropdownMenu>
                      </TableCell>
                    </TableRow>
                  ))
                ) : (
                  <TableRow>
                    <TableCell
                      colSpan={8}
                      className="text-center text-muted-foreground"
                    >
                      보고서가 없습니다
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </div>

          {/* 페이지네이션 */}
          {data && data.totalPages > 1 && (
            <div className="mt-4 flex items-center justify-between">
              <div className="text-sm text-muted-foreground">
                {data.totalElements}개 중 {page * size + 1}-
                {Math.min((page + 1) * size, data.totalElements)}개 표시
              </div>
              <div className="flex gap-2">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setPage(p => Math.max(0, p - 1))}
                  disabled={page === 0}
                >
                  이전
                </Button>
                <div className="flex items-center px-3 text-sm">
                  {page + 1} / {data.totalPages}
                </div>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() =>
                    setPage(p => Math.min(data.totalPages - 1, p + 1))
                  }
                  disabled={page >= data.totalPages - 1}
                >
                  다음
                </Button>
              </div>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  )
}
