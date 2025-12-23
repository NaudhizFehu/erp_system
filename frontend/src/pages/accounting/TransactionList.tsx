/**
 * 전표 목록 페이지
 * 회계 전표 조회, 검색, 필터링 및 관리를 담당하는 페이지
 */

import {
  Search,
  Plus,
  Filter as FilterIcon,
  MoreHorizontal,
  Eye,
  Edit,
  X,
  Calendar,
  FileText,
  Download,
} from 'lucide-react'
import { useState } from 'react'
import { useNavigate } from 'react-router-dom'

import { JournalEntryForm } from '@/components/accounting/JournalEntryForm'
import { TransactionStatusBadge } from '@/components/accounting/TransactionStatusBadge'
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
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
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
  useTransactionSearch,
  useCreateJournalEntry,
} from '@/hooks/useAccounting'
import { useDebounce } from '@/hooks/useDebounce'
import {
  exportToCSV,
  exportToExcel,
  generateFilename,
} from '@/lib/utils/exportData'
import {
  formToApiRequest,
  formatCurrency,
  formatDate,
} from '@/lib/utils/journalEntry'
import { JournalEntryFormData } from '@/schemas/journalEntrySchema'
import {
  TransactionType,
  TransactionStatus,
  KOREAN_LABELS,
} from '@/types/accounting'

export default function TransactionList() {
  const navigate = useNavigate()

  // 검색 및 필터 상태
  const [searchTerm, setSearchTerm] = useState('')
  const [transactionType, setTransactionType] = useState<
    TransactionType | 'ALL'
  >('ALL')
  const [transactionStatus, setTransactionStatus] = useState<
    TransactionStatus | 'ALL'
  >('ALL')
  const [showFilters, setShowFilters] = useState(false)

  // 날짜 필터
  const [dateFrom, setDateFrom] = useState('')
  const [dateTo, setDateTo] = useState('')

  // 페이지네이션
  const [page, setPage] = useState(0)
  const [size] = useState(20)

  // 전표 입력 폼 다이얼로그
  const [isFormOpen, setIsFormOpen] = useState(false)

  // 검색어 디바운스
  const debouncedSearchTerm = useDebounce(searchTerm, 300)

  // 데이터 조회
  const { data, isLoading, refetch } = useTransactionSearch(
    debouncedSearchTerm,
    {
      page,
      size,
      transactionType:
        transactionType !== 'ALL'
          ? (transactionType as TransactionType)
          : undefined,
      transactionStatus:
        transactionStatus !== 'ALL'
          ? (transactionStatus as TransactionStatus)
          : undefined,
      startDate: dateFrom || undefined,
      endDate: dateTo || undefined,
    }
  )

  // 전표 생성 mutation
  const createMutation = useCreateJournalEntry()

  // 전표 생성 핸들러
  const handleCreateTransaction = async (formData: JournalEntryFormData) => {
    try {
      const apiRequests = formToApiRequest(formData as any)
      await createMutation.mutateAsync(apiRequests)

      alert('전표가 성공적으로 생성되었습니다.')
      setIsFormOpen(false)
      refetch()
    } catch (error) {
      alert(
        '전표 생성 실패: ' +
          (error instanceof Error
            ? error.message
            : '전표 생성 중 오류가 발생했습니다.')
      )
    }
  }

  // 필터 초기화
  const handleResetFilters = () => {
    setSearchTerm('')
    setTransactionType('ALL')
    setTransactionStatus('ALL')
    setDateFrom('')
    setDateTo('')
    setPage(0)
  }

  // CSV 내보내기
  const handleExportCSV = () => {
    if (!data?.content || data.content.length === 0) {
      alert('내보낼 데이터가 없습니다.')
      return
    }

    const headers = [
      { key: 'transactionNumber' as const, label: '전표번호' },
      { key: 'transactionDate' as const, label: '거래일자' },
      { key: 'transactionType' as const, label: '유형' },
      { key: 'description' as const, label: '적요' },
      { key: 'debitAmount' as const, label: '차변' },
      { key: 'creditAmount' as const, label: '대변' },
      { key: 'transactionStatus' as const, label: '상태' },
    ]

    const exportData = data.content.map(t => ({
      transactionNumber: t.transactionNumber,
      transactionDate: formatDate(t.transactionDate),
      transactionType: KOREAN_LABELS.transactionType[t.transactionType],
      description: t.description,
      debitAmount: t.debitAmount > 0 ? formatCurrency(t.debitAmount) : '',
      creditAmount: t.creditAmount > 0 ? formatCurrency(t.creditAmount) : '',
      transactionStatus: KOREAN_LABELS.transactionStatus[t.transactionStatus],
    }))

    const filename = generateFilename('회계전표')
    exportToCSV(exportData, filename, headers)
  }

  // Excel 내보내기
  const handleExportExcel = () => {
    if (!data?.content || data.content.length === 0) {
      alert('내보낼 데이터가 없습니다.')
      return
    }

    const headers = [
      { key: 'transactionNumber' as const, label: '전표번호' },
      { key: 'transactionDate' as const, label: '거래일자' },
      { key: 'transactionType' as const, label: '유형' },
      { key: 'description' as const, label: '적요' },
      { key: 'debitAmount' as const, label: '차변' },
      { key: 'creditAmount' as const, label: '대변' },
      { key: 'transactionStatus' as const, label: '상태' },
    ]

    const exportData = data.content.map(t => ({
      transactionNumber: t.transactionNumber,
      transactionDate: formatDate(t.transactionDate),
      transactionType: KOREAN_LABELS.transactionType[t.transactionType],
      description: t.description,
      debitAmount: t.debitAmount > 0 ? formatCurrency(t.debitAmount) : '',
      creditAmount: t.creditAmount > 0 ? formatCurrency(t.creditAmount) : '',
      transactionStatus: KOREAN_LABELS.transactionStatus[t.transactionStatus],
    }))

    const filename = generateFilename('회계전표')
    exportToExcel(exportData, filename, headers)
  }

  // 통계 계산
  const stats = {
    total: data?.totalElements || 0,
    pending:
      data?.content.filter(
        t => t.transactionStatus === TransactionStatus.PENDING
      ).length || 0,
    posted:
      data?.content.filter(
        t => t.transactionStatus === TransactionStatus.POSTED
      ).length || 0,
  }

  return (
    <div className="container mx-auto space-y-6 py-6">
      {/* 헤더 */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">회계 전표</h1>
          <p className="text-muted-foreground">복식부기 전표 조회 및 관리</p>
        </div>
        <div className="flex gap-2">
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="outline">
                <Download className="mr-2 h-4 w-4" />
                내보내기
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              <DropdownMenuItem onClick={handleExportCSV}>
                CSV 파일로 내보내기
              </DropdownMenuItem>
              <DropdownMenuItem onClick={handleExportExcel}>
                Excel 파일로 내보내기
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
          <Button onClick={() => setIsFormOpen(true)}>
            <Plus className="mr-2 h-4 w-4" />
            전표 입력
          </Button>
        </div>
      </div>

      {/* 통계 카드 */}
      <div className="grid grid-cols-1 gap-4 md:grid-cols-4">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">총 전표</CardTitle>
            <FileText className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stats.total}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">승인 대기</CardTitle>
            <Calendar className="h-4 w-4 text-yellow-500" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-yellow-600">
              {stats.pending}
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">전기 완료</CardTitle>
            <FileText className="h-4 w-4 text-green-500" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-green-600">
              {stats.posted}
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
            <CardTitle>전표 검색</CardTitle>
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
                placeholder="전표번호, 적요 검색..."
                value={searchTerm}
                onChange={e => setSearchTerm(e.target.value)}
                className="pl-8"
              />
            </div>
            {(searchTerm ||
              transactionType !== 'ALL' ||
              transactionStatus !== 'ALL' ||
              dateFrom ||
              dateTo) && (
              <Button variant="ghost" onClick={handleResetFilters}>
                <X className="mr-2 h-4 w-4" />
                초기화
              </Button>
            )}
          </div>

          {/* 필터 옵션 */}
          {showFilters && (
            <div className="grid grid-cols-1 gap-4 md:grid-cols-4">
              {/* 시작일 */}
              <div className="space-y-2">
                <label className="text-sm font-medium">시작일</label>
                <Input
                  type="date"
                  value={dateFrom}
                  onChange={e => setDateFrom(e.target.value)}
                />
              </div>

              {/* 종료일 */}
              <div className="space-y-2">
                <label className="text-sm font-medium">종료일</label>
                <Input
                  type="date"
                  value={dateTo}
                  onChange={e => setDateTo(e.target.value)}
                />
              </div>

              {/* 전표 유형 */}
              <div className="space-y-2">
                <label className="text-sm font-medium">전표 유형</label>
                <Select
                  value={transactionType}
                  onValueChange={value =>
                    setTransactionType(value as TransactionType | 'ALL')
                  }
                >
                  <SelectTrigger>
                    <SelectValue placeholder="전체" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="ALL">전체</SelectItem>
                    {Object.values(TransactionType).map(type => (
                      <SelectItem key={type} value={type}>
                        {KOREAN_LABELS.transactionType[type]}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>

              {/* 전표 상태 */}
              <div className="space-y-2">
                <label className="text-sm font-medium">상태</label>
                <Select
                  value={transactionStatus}
                  onValueChange={value =>
                    setTransactionStatus(value as TransactionStatus | 'ALL')
                  }
                >
                  <SelectTrigger>
                    <SelectValue placeholder="전체" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="ALL">전체</SelectItem>
                    {Object.values(TransactionStatus).map(status => (
                      <SelectItem key={status} value={status}>
                        {KOREAN_LABELS.transactionStatus[status]}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
            </div>
          )}
        </CardContent>
      </Card>

      {/* 전표 테이블 */}
      <Card>
        <CardHeader>
          <CardTitle>전표 목록</CardTitle>
          <CardDescription>{data?.totalElements || 0}개의 전표</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="rounded-md border">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>전표번호</TableHead>
                  <TableHead>거래일자</TableHead>
                  <TableHead>유형</TableHead>
                  <TableHead>적요</TableHead>
                  <TableHead className="text-right">차변</TableHead>
                  <TableHead className="text-right">대변</TableHead>
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
                  data.content.map(transaction => (
                    <TableRow key={transaction.id}>
                      <TableCell className="font-medium">
                        {transaction.transactionNumber}
                      </TableCell>
                      <TableCell>
                        {formatDate(transaction.transactionDate)}
                      </TableCell>
                      <TableCell>
                        <Badge variant="outline">
                          {
                            KOREAN_LABELS.transactionType[
                              transaction.transactionType
                            ]
                          }
                        </Badge>
                      </TableCell>
                      <TableCell className="max-w-[200px] truncate">
                        {transaction.description}
                      </TableCell>
                      <TableCell className="text-right">
                        {transaction.debitAmount > 0
                          ? formatCurrency(transaction.debitAmount)
                          : ''}
                      </TableCell>
                      <TableCell className="text-right">
                        {transaction.creditAmount > 0
                          ? formatCurrency(transaction.creditAmount)
                          : ''}
                      </TableCell>
                      <TableCell>
                        <TransactionStatusBadge
                          status={transaction.transactionStatus}
                        />
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
                                navigate(
                                  `/accounting/transactions/${transaction.id}`
                                )
                              }
                            >
                              <Eye className="mr-2 h-4 w-4" />
                              상세 보기
                            </DropdownMenuItem>
                            {transaction.transactionStatus ===
                              TransactionStatus.DRAFT && (
                              <DropdownMenuItem
                                onClick={() =>
                                  navigate(
                                    `/accounting/transactions/${transaction.id}/edit`
                                  )
                                }
                              >
                                <Edit className="mr-2 h-4 w-4" />
                                수정
                              </DropdownMenuItem>
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
                      전표가 없습니다
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

      {/* 전표 입력 폼 다이얼로그 */}
      <Dialog open={isFormOpen} onOpenChange={setIsFormOpen}>
        <DialogContent className="max-h-[90vh] max-w-5xl overflow-y-auto">
          <DialogHeader>
            <DialogTitle>전표 입력</DialogTitle>
            <DialogDescription>
              복식부기 원칙에 따라 차변과 대변의 합계가 일치해야 합니다.
            </DialogDescription>
          </DialogHeader>
          <JournalEntryForm
            onSubmit={handleCreateTransaction}
            onCancel={() => setIsFormOpen(false)}
            isSubmitting={createMutation.isPending}
            mode="create"
          />
        </DialogContent>
      </Dialog>
    </div>
  )
}
