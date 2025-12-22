/**
 * 분개 전표 입력 폼 컴포넌트
 * 복식부기 다중 라인 입력 폼
 * - 계정과목 선택 (leaf account만)
 * - 차변/대변 입력 (한 줄당 하나만)
 * - 실시간 대차평형 검증
 * - React Hook Form + Zod 검증
 */

import { zodResolver } from '@hookform/resolvers/zod'
import { Plus, Trash2 } from 'lucide-react'
import { useEffect } from 'react'
import { useFieldArray, useForm } from 'react-hook-form'

import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form'
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
  TableFooter,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { useAuth } from '@/contexts/AuthContext'
import {
  calculateTotals,
  createEmptyLine,
  formatCurrency,
} from '@/lib/utils/journalEntry'
import {
  journalEntryFormSchema,
  JournalEntryFormData,
} from '@/schemas/journalEntrySchema'
import { TransactionType, KOREAN_LABELS } from '@/types/accounting'

import { AccountSelector } from './AccountSelector'

interface JournalEntryFormProps {
  onSubmit: (data: JournalEntryFormData) => void
  onCancel: () => void
  isSubmitting?: boolean
  mode?: 'create' | 'edit'
  initialData?: JournalEntryFormData
}

export function JournalEntryForm({
  onSubmit,
  onCancel,
  isSubmitting = false,
  mode = 'create',
  initialData,
}: JournalEntryFormProps) {
  const { user } = useAuth()

  // 오늘 날짜 (최대 허용 날짜)
  const today = new Date().toISOString().split('T')[0]

  // 기본값 설정
  const defaultValues: JournalEntryFormData = initialData || {
    companyId: user?.company?.id || 0,
    transactionDate: today,
    transactionType: TransactionType.JOURNAL,
    transactionNumber: '',
    inputById: user?.id,
    lines: [createEmptyLine(), createEmptyLine()], // 최소 2행
  }

  // Form 초기화
  const form = useForm<JournalEntryFormData>({
    resolver: zodResolver(journalEntryFormSchema),
    defaultValues,
    mode: 'onChange',
  })

  // 분개 라인 배열 관리
  const { fields, append, remove } = useFieldArray({
    control: form.control,
    name: 'lines',
  })

  // 차변/대변 합계 계산
  const lines = form.watch('lines')
  const { totalDebit, totalCredit, balance, isBalanced } = calculateTotals(
    lines as any
  )

  // 행 추가
  const handleAddLine = () => {
    append(createEmptyLine())
  }

  // 행 삭제 (최소 2행 유지)
  const handleRemoveLine = (index: number) => {
    if (fields.length > 2) {
      remove(index)
    }
  }

  // 차변 입력 시 대변 0으로
  const handleDebitChange = (index: number, value: string) => {
    const amount = parseFloat(value) || 0
    form.setValue(`lines.${index}.debitAmount`, amount)
    if (amount > 0) {
      form.setValue(`lines.${index}.creditAmount`, 0)
    }
  }

  // 대변 입력 시 차변 0으로
  const handleCreditChange = (index: number, value: string) => {
    const amount = parseFloat(value) || 0
    form.setValue(`lines.${index}.creditAmount`, amount)
    if (amount > 0) {
      form.setValue(`lines.${index}.debitAmount`, 0)
    }
  }

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
        {/* 헤더 섹션 */}
        <Card>
          <CardHeader>
            <CardTitle>전표 정보</CardTitle>
          </CardHeader>
          <CardContent className="grid grid-cols-1 gap-4 md:grid-cols-3">
            {/* 거래일자 */}
            <FormField
              control={form.control}
              name="transactionDate"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>거래일자</FormLabel>
                  <FormControl>
                    <Input
                      type="date"
                      max={today}
                      {...field}
                      disabled={isSubmitting}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* 전표 유형 */}
            <FormField
              control={form.control}
              name="transactionType"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>전표 유형</FormLabel>
                  <Select
                    onValueChange={field.onChange}
                    defaultValue={field.value}
                    disabled={isSubmitting}
                  >
                    <FormControl>
                      <SelectTrigger>
                        <SelectValue placeholder="유형 선택" />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent>
                      {Object.values(TransactionType).map(type => (
                        <SelectItem key={type} value={type}>
                          {KOREAN_LABELS.transactionType[type]}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* 전표번호 (자동 생성) */}
            <FormField
              control={form.control}
              name="transactionNumber"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>전표번호</FormLabel>
                  <FormControl>
                    <Input
                      {...field}
                      disabled
                      placeholder="자동 생성"
                      className="bg-muted"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          </CardContent>
        </Card>

        {/* 분개 라인 테이블 */}
        <Card>
          <CardHeader>
            <CardTitle>분개 내역</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="overflow-x-auto rounded-md border">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead className="w-[250px]">계정과목</TableHead>
                    <TableHead className="w-[120px]">차변</TableHead>
                    <TableHead className="w-[120px]">대변</TableHead>
                    <TableHead className="min-w-[200px]">적요</TableHead>
                    <TableHead className="w-[80px]">작업</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {fields.map((field, index) => (
                    <TableRow key={field.id}>
                      {/* 계정과목 */}
                      <TableCell>
                        <FormField
                          control={form.control}
                          name={`lines.${index}.accountId`}
                          render={({ field: accountField }) => (
                            <FormItem>
                              <FormControl>
                                <AccountSelector
                                  value={accountField.value}
                                  onChange={(accountId, account) => {
                                    form.setValue(
                                      `lines.${index}.accountId`,
                                      accountId
                                    )
                                    form.setValue(
                                      `lines.${index}.account`,
                                      account
                                    )
                                  }}
                                  companyId={form.watch('companyId')}
                                  error={
                                    form.formState.errors.lines?.[index]
                                      ?.accountId?.message
                                  }
                                  disabled={isSubmitting}
                                />
                              </FormControl>
                            </FormItem>
                          )}
                        />
                      </TableCell>

                      {/* 차변 */}
                      <TableCell>
                        <FormField
                          control={form.control}
                          name={`lines.${index}.debitAmount`}
                          render={({ field: debitField }) => (
                            <FormItem>
                              <FormControl>
                                <Input
                                  type="number"
                                  min="0"
                                  step="0.01"
                                  value={debitField.value || ''}
                                  onChange={e =>
                                    handleDebitChange(index, e.target.value)
                                  }
                                  disabled={isSubmitting}
                                  className="text-right"
                                />
                              </FormControl>
                            </FormItem>
                          )}
                        />
                      </TableCell>

                      {/* 대변 */}
                      <TableCell>
                        <FormField
                          control={form.control}
                          name={`lines.${index}.creditAmount`}
                          render={({ field: creditField }) => (
                            <FormItem>
                              <FormControl>
                                <Input
                                  type="number"
                                  min="0"
                                  step="0.01"
                                  value={creditField.value || ''}
                                  onChange={e =>
                                    handleCreditChange(index, e.target.value)
                                  }
                                  disabled={isSubmitting}
                                  className="text-right"
                                />
                              </FormControl>
                            </FormItem>
                          )}
                        />
                      </TableCell>

                      {/* 적요 */}
                      <TableCell>
                        <FormField
                          control={form.control}
                          name={`lines.${index}.description`}
                          render={({ field: descField }) => (
                            <FormItem>
                              <FormControl>
                                <Input
                                  {...descField}
                                  disabled={isSubmitting}
                                  placeholder="거래 설명"
                                />
                              </FormControl>
                            </FormItem>
                          )}
                        />
                      </TableCell>

                      {/* 삭제 버튼 */}
                      <TableCell>
                        <Button
                          type="button"
                          variant="ghost"
                          size="icon"
                          onClick={() => handleRemoveLine(index)}
                          disabled={isSubmitting || fields.length <= 2}
                        >
                          <Trash2 className="h-4 w-4" />
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
                <TableFooter>
                  <TableRow>
                    <TableCell className="font-bold">합계</TableCell>
                    <TableCell className="text-right font-bold">
                      {formatCurrency(totalDebit)}
                    </TableCell>
                    <TableCell className="text-right font-bold">
                      {formatCurrency(totalCredit)}
                    </TableCell>
                    <TableCell colSpan={2}>
                      <div className="flex items-center gap-2">
                        <span className="text-sm text-muted-foreground">
                          잔액: {formatCurrency(Math.abs(balance))}
                        </span>
                        {isBalanced ? (
                          <Badge className="bg-green-100 text-green-800">
                            대차평형 ✓
                          </Badge>
                        ) : (
                          <Badge className="bg-red-100 text-red-800">
                            불일치 ✗
                          </Badge>
                        )}
                      </div>
                    </TableCell>
                  </TableRow>
                </TableFooter>
              </Table>
            </div>

            {/* 행 추가 버튼 */}
            <Button
              type="button"
              variant="outline"
              onClick={handleAddLine}
              disabled={isSubmitting}
              className="mt-4"
            >
              <Plus className="mr-2 h-4 w-4" />행 추가
            </Button>
          </CardContent>
        </Card>

        {/* 액션 버튼 */}
        <div className="flex justify-end gap-2">
          <Button
            type="button"
            variant="outline"
            onClick={onCancel}
            disabled={isSubmitting}
          >
            취소
          </Button>
          <Button type="submit" disabled={!isBalanced || isSubmitting}>
            {isSubmitting ? '처리 중...' : mode === 'create' ? '저장' : '수정'}
          </Button>
        </div>

        {/* 폼 에러 표시 */}
        {form.formState.errors.lines && (
          <div className="text-sm text-destructive">
            {form.formState.errors.lines.message}
          </div>
        )}
      </form>
    </Form>
  )
}
