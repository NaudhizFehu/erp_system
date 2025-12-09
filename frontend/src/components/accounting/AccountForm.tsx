/**
 * 계정과목 등록/수정 폼 컴포넌트
 * 계정과목 정보를 입력하고 수정하는 폼입니다
 */

import { zodResolver } from '@hookform/resolvers/zod'
import { Check, ChevronsUpDown, Loader2 } from 'lucide-react'
import { useEffect, useState } from 'react'
import { useForm } from 'react-hook-form'
import * as z from 'zod'

import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
} from '@/components/ui/command'
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form'
import { Input } from '@/components/ui/input'
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from '@/components/ui/popover'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Separator } from '@/components/ui/separator'
import { Switch } from '@/components/ui/switch'
import { Textarea } from '@/components/ui/textarea'
import { useAuth } from '@/contexts/AuthContext'
import { useCheckAccountCode, useAccountTree } from '@/hooks/useAccounts'
import { cn } from '@/lib/utils'
import {
  AccountType,
  AccountCategory,
  DebitCreditType,
  KOREAN_LABELS,
  type Account,
  type AccountCreateRequest,
  type AccountUpdateRequest,
} from '@/types/accounting'

// 계정 분류 매핑
const categoryByType: Record<AccountType, AccountCategory[]> = {
  [AccountType.ASSET]: [
    AccountCategory.CURRENT_ASSET,
    AccountCategory.NON_CURRENT_ASSET,
  ],
  [AccountType.LIABILITY]: [
    AccountCategory.CURRENT_LIABILITY,
    AccountCategory.NON_CURRENT_LIABILITY,
  ],
  [AccountType.EQUITY]: [
    AccountCategory.PAID_IN_CAPITAL,
    AccountCategory.RETAINED_EARNINGS,
  ],
  [AccountType.REVENUE]: [
    AccountCategory.OPERATING_REVENUE,
    AccountCategory.NON_OPERATING_REVENUE,
  ],
  [AccountType.EXPENSE]: [
    AccountCategory.OPERATING_EXPENSE,
    AccountCategory.NON_OPERATING_EXPENSE,
  ],
}

// 차대구분 기본값
const defaultDebitCreditType: Record<AccountType, DebitCreditType> = {
  [AccountType.ASSET]: DebitCreditType.DEBIT,
  [AccountType.LIABILITY]: DebitCreditType.CREDIT,
  [AccountType.EQUITY]: DebitCreditType.CREDIT,
  [AccountType.REVENUE]: DebitCreditType.CREDIT,
  [AccountType.EXPENSE]: DebitCreditType.DEBIT,
}

// 폼 검증 스키마
const accountFormSchema = z.object({
  accountCode: z
    .string()
    .min(1, '계정코드는 필수입니다')
    .max(20, '계정코드는 20자 이하여야 합니다'),
  accountName: z
    .string()
    .min(2, '계정명은 2자 이상이어야 합니다')
    .max(100, '계정명은 100자 이하여야 합니다'),
  accountNameEn: z
    .string()
    .max(100, '영문 계정명은 100자 이하여야 합니다')
    .optional(),
  accountType: z.nativeEnum(AccountType, {
    required_error: '계정 타입은 필수입니다',
  }),
  accountCategory: z.nativeEnum(AccountCategory, {
    required_error: '계정 분류는 필수입니다',
  }),
  debitCreditType: z.nativeEnum(DebitCreditType, {
    required_error: '차대구분은 필수입니다',
  }),
  accountLevel: z.number({
    required_error: '계정 레벨은 필수입니다',
  }),
  parentAccountId: z.number().optional(),
  sortOrder: z.number().optional(),
  isActive: z.boolean().default(true),
  trackBalance: z.boolean().default(true),
  openingBalance: z.number().optional(),
  budgetAmount: z.number().optional(),
  taxCode: z.string().optional(),
  description: z.string().max(500, '설명은 500자 이하여야 합니다').optional(),
})

type AccountFormValues = z.infer<typeof accountFormSchema>

interface AccountFormProps {
  account?: Account
  onSubmit: (data: AccountCreateRequest | AccountUpdateRequest) => void
  onCancel: () => void
  isSubmitting: boolean
  mode: 'create' | 'edit'
}

/**
 * 계정과목 폼 컴포넌트
 */
export function AccountForm({
  account,
  onSubmit,
  onCancel,
  isSubmitting,
  mode,
}: AccountFormProps) {
  const { user } = useAuth()
  const [openParentAccountSelect, setOpenParentAccountSelect] = useState(false)

  // 폼 초기화
  const form = useForm<AccountFormValues>({
    resolver: zodResolver(accountFormSchema),
    defaultValues: {
      accountCode: account?.accountCode || '',
      accountName: account?.accountName || '',
      accountNameEn: account?.accountNameEn || '',
      accountType: account?.accountType || AccountType.ASSET,
      accountCategory:
        account?.accountCategory || AccountCategory.CURRENT_ASSET,
      debitCreditType: account?.debitCreditType || DebitCreditType.DEBIT,
      accountLevel: account?.accountLevel || 1,
      parentAccountId: account?.parentAccount?.id,
      sortOrder: account?.sortOrder || 0,
      isActive: account?.isActive ?? true,
      trackBalance: account?.trackBalance ?? true,
      openingBalance: account?.openingBalance || 0,
      budgetAmount: account?.budgetAmount || 0,
      taxCode: account?.taxCode || '',
      description: account?.description || '',
    },
  })

  // 현재 선택된 값들
  const accountType = form.watch('accountType')
  const accountLevel = form.watch('accountLevel')
  const accountCode = form.watch('accountCode')

  // 계정과목 트리 조회
  const { data: accountTree } = useAccountTree(user?.company?.id)

  // 계정코드 중복 확인
  const { data: codeCheckData } = useCheckAccountCode(
    accountCode,
    user?.company?.id,
    account?.id
  )

  // 계정 타입 변경 시 처리
  useEffect(() => {
    if (accountType) {
      // 기본 차대구분 설정
      form.setValue('debitCreditType', defaultDebitCreditType[accountType])

      // 해당 타입의 첫 번째 분류로 설정
      const categories = categoryByType[accountType]
      if (categories && categories.length > 0) {
        form.setValue('accountCategory', categories[0])
      }
    }
  }, [accountType, form])

  // 계정 레벨 변경 시 상위 계정 초기화
  useEffect(() => {
    if (accountLevel === 1) {
      form.setValue('parentAccountId', undefined)
    }
  }, [accountLevel, form])

  // 상위 계정 목록 필터링
  const filteredParentAccounts = accountTree?.filter(acc => {
    // 자기 자신 제외
    if (account && acc.id === account.id) return false

    // 레벨에 따라 필터링
    if (accountLevel === 1) return false // 대분류는 상위 계정 없음
    if (accountLevel === 2) return acc.accountLevel === 1 // 중분류는 대분류만
    if (accountLevel === 3) return acc.accountLevel === 2 // 소분류는 중분류만

    return false
  })

  // 폼 제출
  const handleSubmit = (values: AccountFormValues) => {
    // companyId 추가
    const submitData = {
      ...values,
      companyId: user?.company?.id || 0,
    }

    onSubmit(submitData)
  }

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-6">
        <Card>
          <CardHeader>
            <CardTitle>기본 정보</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            {/* 계정코드 */}
            <FormField
              control={form.control}
              name="accountCode"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>
                    계정코드 <span className="text-red-500">*</span>
                  </FormLabel>
                  <div className="flex space-x-2">
                    <FormControl>
                      <Input
                        placeholder="예: 1000"
                        {...field}
                        disabled={mode === 'edit'}
                      />
                    </FormControl>
                    {mode === 'create' && accountCode && (
                      <div className="flex items-center">
                        {codeCheckData ? (
                          <span className="text-sm text-red-600">중복</span>
                        ) : (
                          <span className="text-sm text-green-600">
                            사용 가능
                          </span>
                        )}
                      </div>
                    )}
                  </div>
                  <FormDescription>
                    계정 레벨에 따라: 대분류(4자리), 중분류(6자리),
                    소분류(8자리)
                  </FormDescription>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* 계정명 */}
            <FormField
              control={form.control}
              name="accountName"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>
                    계정명 <span className="text-red-500">*</span>
                  </FormLabel>
                  <FormControl>
                    <Input placeholder="예: 현금" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* 계정명 (영문) */}
            <FormField
              control={form.control}
              name="accountNameEn"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>계정명 (영문)</FormLabel>
                  <FormControl>
                    <Input placeholder="예: Cash" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>분류 정보</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            {/* 계정 타입 */}
            <FormField
              control={form.control}
              name="accountType"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>
                    계정 타입 <span className="text-red-500">*</span>
                  </FormLabel>
                  <Select
                    onValueChange={field.onChange}
                    defaultValue={field.value}
                  >
                    <FormControl>
                      <SelectTrigger>
                        <SelectValue placeholder="계정 타입 선택" />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent>
                      {Object.values(AccountType).map(type => (
                        <SelectItem key={type} value={type}>
                          {KOREAN_LABELS[type]}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* 계정 분류 */}
            <FormField
              control={form.control}
              name="accountCategory"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>
                    계정 분류 <span className="text-red-500">*</span>
                  </FormLabel>
                  <Select
                    onValueChange={field.onChange}
                    defaultValue={field.value}
                  >
                    <FormControl>
                      <SelectTrigger>
                        <SelectValue placeholder="계정 분류 선택" />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent>
                      {accountType &&
                        categoryByType[accountType]?.map(category => (
                          <SelectItem key={category} value={category}>
                            {KOREAN_LABELS[category]}
                          </SelectItem>
                        ))}
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* 차대구분 */}
            <FormField
              control={form.control}
              name="debitCreditType"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>
                    차대구분 <span className="text-red-500">*</span>
                  </FormLabel>
                  <Select
                    onValueChange={field.onChange}
                    defaultValue={field.value}
                  >
                    <FormControl>
                      <SelectTrigger>
                        <SelectValue placeholder="차대구분 선택" />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent>
                      {Object.values(DebitCreditType).map(type => (
                        <SelectItem key={type} value={type}>
                          {KOREAN_LABELS[type]}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              )}
            />
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>계층 정보</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            {/* 계정 레벨 */}
            <FormField
              control={form.control}
              name="accountLevel"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>
                    계정 레벨 <span className="text-red-500">*</span>
                  </FormLabel>
                  <Select
                    onValueChange={value => field.onChange(parseInt(value))}
                    defaultValue={field.value?.toString()}
                  >
                    <FormControl>
                      <SelectTrigger>
                        <SelectValue placeholder="계정 레벨 선택" />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent>
                      <SelectItem value="1">대분류 (1)</SelectItem>
                      <SelectItem value="2">중분류 (2)</SelectItem>
                      <SelectItem value="3">소분류 (3)</SelectItem>
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* 상위 계정 */}
            {accountLevel > 1 && (
              <FormField
                control={form.control}
                name="parentAccountId"
                render={({ field }) => (
                  <FormItem className="flex flex-col">
                    <FormLabel>
                      상위 계정{' '}
                      {accountLevel > 1 && (
                        <span className="text-red-500">*</span>
                      )}
                    </FormLabel>
                    <Popover
                      open={openParentAccountSelect}
                      onOpenChange={setOpenParentAccountSelect}
                    >
                      <PopoverTrigger asChild>
                        <FormControl>
                          <Button
                            variant="outline"
                            role="combobox"
                            className={cn(
                              'justify-between',
                              !field.value && 'text-muted-foreground'
                            )}
                          >
                            {field.value
                              ? filteredParentAccounts?.find(
                                  acc => acc.id === field.value
                                )?.accountCode +
                                ' ' +
                                filteredParentAccounts?.find(
                                  acc => acc.id === field.value
                                )?.accountName
                              : '상위 계정 선택'}
                            <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                          </Button>
                        </FormControl>
                      </PopoverTrigger>
                      <PopoverContent className="w-[400px] p-0">
                        <Command>
                          <CommandInput placeholder="계정 검색..." />
                          <CommandEmpty>계정을 찾을 수 없습니다.</CommandEmpty>
                          <CommandGroup className="max-h-64 overflow-auto">
                            {filteredParentAccounts?.map(acc => (
                              <CommandItem
                                value={`${acc.accountCode} ${acc.accountName}`}
                                key={acc.id}
                                onSelect={() => {
                                  form.setValue('parentAccountId', acc.id)
                                  setOpenParentAccountSelect(false)
                                }}
                              >
                                <Check
                                  className={cn(
                                    'mr-2 h-4 w-4',
                                    acc.id === field.value
                                      ? 'opacity-100'
                                      : 'opacity-0'
                                  )}
                                />
                                {acc.accountCode} - {acc.accountName}
                              </CommandItem>
                            ))}
                          </CommandGroup>
                        </Command>
                      </PopoverContent>
                    </Popover>
                    <FormDescription>
                      {accountLevel === 2 && '대분류 계정을 선택하세요'}
                      {accountLevel === 3 && '중분류 계정을 선택하세요'}
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />
            )}

            {/* 정렬 순서 */}
            <FormField
              control={form.control}
              name="sortOrder"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>정렬 순서</FormLabel>
                  <FormControl>
                    <Input
                      type="number"
                      placeholder="0"
                      {...field}
                      onChange={e =>
                        field.onChange(
                          e.target.value ? parseInt(e.target.value) : 0
                        )
                      }
                    />
                  </FormControl>
                  <FormDescription>
                    숫자가 작을수록 먼저 표시됩니다
                  </FormDescription>
                  <FormMessage />
                </FormItem>
              )}
            />
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>금액 정보</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            {/* 기초 잔액 */}
            <FormField
              control={form.control}
              name="openingBalance"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>기초 잔액</FormLabel>
                  <FormControl>
                    <Input
                      type="number"
                      placeholder="0"
                      {...field}
                      onChange={e =>
                        field.onChange(
                          e.target.value ? parseFloat(e.target.value) : 0
                        )
                      }
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* 예산 금액 */}
            <FormField
              control={form.control}
              name="budgetAmount"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>예산 금액</FormLabel>
                  <FormControl>
                    <Input
                      type="number"
                      placeholder="0"
                      {...field}
                      onChange={e =>
                        field.onChange(
                          e.target.value ? parseFloat(e.target.value) : 0
                        )
                      }
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* 세금 코드 */}
            <FormField
              control={form.control}
              name="taxCode"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>세금 코드</FormLabel>
                  <FormControl>
                    <Input placeholder="예: VAT10" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>추가 정보</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            {/* 사용 여부 */}
            <FormField
              control={form.control}
              name="isActive"
              render={({ field }) => (
                <FormItem className="flex flex-row items-center justify-between rounded-lg border p-4">
                  <div className="space-y-0.5">
                    <FormLabel className="text-base">사용 여부</FormLabel>
                    <FormDescription>
                      계정과목을 사용할지 여부를 설정합니다
                    </FormDescription>
                  </div>
                  <FormControl>
                    <Switch
                      checked={field.value}
                      onCheckedChange={field.onChange}
                    />
                  </FormControl>
                </FormItem>
              )}
            />

            {/* 잔액 추적 */}
            <FormField
              control={form.control}
              name="trackBalance"
              render={({ field }) => (
                <FormItem className="flex flex-row items-center justify-between rounded-lg border p-4">
                  <div className="space-y-0.5">
                    <FormLabel className="text-base">잔액 추적</FormLabel>
                    <FormDescription>
                      계정과목의 잔액을 추적할지 여부를 설정합니다
                    </FormDescription>
                  </div>
                  <FormControl>
                    <Switch
                      checked={field.value}
                      onCheckedChange={field.onChange}
                    />
                  </FormControl>
                </FormItem>
              )}
            />

            {/* 설명 */}
            <FormField
              control={form.control}
              name="description"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>설명</FormLabel>
                  <FormControl>
                    <Textarea
                      placeholder="계정과목에 대한 설명을 입력하세요"
                      className="resize-none"
                      {...field}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          </CardContent>
        </Card>

        <Separator />

        {/* 버튼 */}
        <div className="flex justify-end space-x-2">
          <Button type="button" variant="outline" onClick={onCancel}>
            취소
          </Button>
          <Button type="submit" disabled={isSubmitting}>
            {isSubmitting && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
            {mode === 'create' ? '등록' : '수정'}
          </Button>
        </div>
      </form>
    </Form>
  )
}
