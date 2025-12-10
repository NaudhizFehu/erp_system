/**
 * 전표 상세 페이지
 * 전표 상세 정보 조회 및 승인 워크플로우 관리
 * 그룹화된 분개 항목 조회 (복식부기)
 */

import { ArrowLeft, Calendar, User, FileText } from 'lucide-react'
import { useMemo } from 'react'
import { useNavigate, useParams } from 'react-router-dom'

import { TransactionActions } from '@/components/accounting/TransactionActions'
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
import { Label } from '@/components/ui/label'
import {
  Table,
  TableBody,
  TableCell,
  TableFooter,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import {
  useTransaction,
  useTransactionsByNumber,
} from '@/hooks/useAccounting'
import { KOREAN_LABELS, TransactionStatus } from '@/types/accounting'
import {
  formatCurrency,
  formatDate,
  formatDateTime,
} from '@/lib/utils/journalEntry'

export default function TransactionDetail() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()

  // 전표 데이터 조회
  const { data: transaction, isLoading } = useTransaction(parseInt(id!))

  // 같은 전표번호의 모든 분개 항목 조회 (복식부기 그룹)
  const { data: journalEntries } = useTransactionsByNumber(
    transaction?.transactionNumber || ''
  )

  // 차변/대변 합계 계산
  const totals = useMemo(() => {
    if (!journalEntries) return { debit: 0, credit: 0, isBalanced: false }

    const debit = journalEntries.reduce(
      (sum, entry) => sum + entry.debitAmount,
      0
    )
    const credit = journalEntries.reduce(
      (sum, entry) => sum + entry.creditAmount,
      0
    )
    const isBalanced = Math.abs(debit - credit) < 0.01

    return { debit, credit, isBalanced }
  }, [journalEntries])

  if (isLoading) {
    return (
      <div className="container mx-auto py-6">
        <div className="text-center">로딩 중...</div>
      </div>
    )
  }

  if (!transaction) {
    return (
      <div className="container mx-auto py-6">
        <div className="text-center text-destructive">
          전표를 찾을 수 없습니다
        </div>
      </div>
    )
  }

  return (
    <div className="container mx-auto py-6 space-y-6">
      {/* 헤더 */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <Button
            variant="ghost"
            onClick={() => navigate('/accounting/transactions')}
          >
            <ArrowLeft className="h-4 w-4 mr-2" />
            목록
          </Button>
          <div>
            <h1 className="text-3xl font-bold">
              전표 {transaction.transactionNumber}
            </h1>
            <p className="text-muted-foreground">전표 상세 정보</p>
          </div>
          <TransactionStatusBadge status={transaction.transactionStatus} />
        </div>
        <TransactionActions
          transactions={journalEntries || [transaction]}
          onActionComplete={() => navigate('/accounting/transactions')}
        />
      </div>

      {/* 기본 정보 카드 */}
      <Card>
        <CardHeader>
          <CardTitle>전표 정보</CardTitle>
          <CardDescription>기본 정보 및 상태</CardDescription>
        </CardHeader>
        <CardContent className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {/* 전표번호 */}
          <div className="space-y-2">
            <Label className="text-muted-foreground flex items-center gap-2">
              <FileText className="h-4 w-4" />
              전표번호
            </Label>
            <div className="font-medium">{transaction.transactionNumber}</div>
          </div>

          {/* 거래일자 */}
          <div className="space-y-2">
            <Label className="text-muted-foreground flex items-center gap-2">
              <Calendar className="h-4 w-4" />
              거래일자
            </Label>
            <div className="font-medium">
              {formatDate(transaction.transactionDate)}
            </div>
          </div>

          {/* 전표 유형 */}
          <div className="space-y-2">
            <Label className="text-muted-foreground">전표 유형</Label>
            <div>
              <Badge variant="outline">
                {KOREAN_LABELS.transactionType[transaction.transactionType]}
              </Badge>
            </div>
          </div>

          {/* 상태 */}
          <div className="space-y-2">
            <Label className="text-muted-foreground">상태</Label>
            <div>
              <TransactionStatusBadge status={transaction.transactionStatus} />
            </div>
          </div>

          {/* 입력자 */}
          <div className="space-y-2">
            <Label className="text-muted-foreground flex items-center gap-2">
              <User className="h-4 w-4" />
              입력자
            </Label>
            <div className="font-medium">
              {transaction.inputBy?.name || '-'}
            </div>
          </div>

          {/* 입력일시 */}
          <div className="space-y-2">
            <Label className="text-muted-foreground">입력일시</Label>
            <div className="font-medium text-sm">
              {formatDateTime(transaction.createdAt)}
            </div>
          </div>

          {/* 승인자 */}
          {transaction.approvedBy && (
            <>
              <div className="space-y-2">
                <Label className="text-muted-foreground flex items-center gap-2">
                  <User className="h-4 w-4" />
                  승인자
                </Label>
                <div className="font-medium">{transaction.approvedBy.name}</div>
              </div>

              <div className="space-y-2">
                <Label className="text-muted-foreground">승인일시</Label>
                <div className="font-medium text-sm">
                  {transaction.approvedAt
                    ? formatDateTime(transaction.approvedAt)
                    : '-'}
                </div>
              </div>
            </>
          )}

          {/* 전기일시 */}
          {transaction.postedAt && (
            <div className="space-y-2">
              <Label className="text-muted-foreground">전기일시</Label>
              <div className="font-medium text-sm">
                {formatDateTime(transaction.postedAt)}
              </div>
            </div>
          )}
        </CardContent>
      </Card>

      {/* 분개 내역 */}
      <Card>
        <CardHeader>
          <CardTitle>분개 내역</CardTitle>
          <CardDescription>
            {journalEntries?.length || 0}개의 분개 항목
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="rounded-md border">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead className="w-[150px]">계정코드</TableHead>
                  <TableHead>계정과목</TableHead>
                  <TableHead className="text-right w-[120px]">차변</TableHead>
                  <TableHead className="text-right w-[120px]">대변</TableHead>
                  <TableHead className="min-w-[200px]">적요</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {journalEntries && journalEntries.length > 0 ? (
                  journalEntries.map((entry) => (
                    <TableRow key={entry.id}>
                      <TableCell className="font-mono text-sm">
                        {entry.account.accountCode}
                      </TableCell>
                      <TableCell className="font-medium">
                        {entry.account.name || entry.account.accountName}
                      </TableCell>
                      <TableCell className="text-right">
                        {entry.debitAmount > 0
                          ? formatCurrency(entry.debitAmount)
                          : ''}
                      </TableCell>
                      <TableCell className="text-right">
                        {entry.creditAmount > 0
                          ? formatCurrency(entry.creditAmount)
                          : ''}
                      </TableCell>
                      <TableCell>{entry.description}</TableCell>
                    </TableRow>
                  ))
                ) : (
                  <TableRow>
                    <TableCell colSpan={5} className="text-center">
                      분개 항목이 없습니다
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
              <TableFooter>
                <TableRow>
                  <TableCell colSpan={2} className="font-bold">
                    합계
                  </TableCell>
                  <TableCell className="text-right font-bold">
                    {formatCurrency(totals.debit)}
                  </TableCell>
                  <TableCell className="text-right font-bold">
                    {formatCurrency(totals.credit)}
                  </TableCell>
                  <TableCell>
                    {totals.isBalanced ? (
                      <Badge className="bg-green-100 text-green-800">
                        대차평형 ✓
                      </Badge>
                    ) : (
                      <Badge className="bg-red-100 text-red-800">
                        불일치 ✗
                      </Badge>
                    )}
                  </TableCell>
                </TableRow>
              </TableFooter>
            </Table>
          </div>
        </CardContent>
      </Card>

      {/* 취소 정보 (취소된 경우만 표시) */}
      {transaction.transactionStatus === TransactionStatus.CANCELLED && (
        <Card className="border-red-300 bg-red-50">
          <CardHeader>
            <CardTitle className="text-red-600">취소 정보</CardTitle>
          </CardHeader>
          <CardContent className="space-y-2">
            <div>
              <Label className="text-muted-foreground">취소 사유</Label>
              <div className="font-medium">{transaction.cancelReason || '-'}</div>
            </div>
            <div>
              <Label className="text-muted-foreground">취소일시</Label>
              <div className="font-medium text-sm">
                {transaction.cancelledAt
                  ? formatDateTime(transaction.cancelledAt)
                  : '-'}
              </div>
            </div>
            {transaction.cancelledBy && (
              <div>
                <Label className="text-muted-foreground">취소자</Label>
                <div className="font-medium">{transaction.cancelledBy.name}</div>
              </div>
            )}
          </CardContent>
        </Card>
      )}

      {/* 추가 정보 (메모, 거래처 등) */}
      {(transaction.memo ||
        transaction.businessPartner ||
        transaction.departmentInfo ||
        transaction.projectCode) && (
        <Card>
          <CardHeader>
            <CardTitle>추가 정보</CardTitle>
          </CardHeader>
          <CardContent className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {transaction.memo && (
              <div className="space-y-2">
                <Label className="text-muted-foreground">메모</Label>
                <div className="font-medium">{transaction.memo}</div>
              </div>
            )}
            {transaction.businessPartner && (
              <div className="space-y-2">
                <Label className="text-muted-foreground">거래처</Label>
                <div className="font-medium">{transaction.businessPartner}</div>
              </div>
            )}
            {transaction.departmentInfo && (
              <div className="space-y-2">
                <Label className="text-muted-foreground">부서</Label>
                <div className="font-medium">{transaction.departmentInfo}</div>
              </div>
            )}
            {transaction.projectCode && (
              <div className="space-y-2">
                <Label className="text-muted-foreground">프로젝트 코드</Label>
                <div className="font-medium">{transaction.projectCode}</div>
              </div>
            )}
          </CardContent>
        </Card>
      )}
    </div>
  )
}
