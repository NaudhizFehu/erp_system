import {
  Search,
  Plus,
  Calculator,
  TrendingUp,
  Filter as FilterIcon,
  MoreHorizontal,
  DollarSign,
  RefreshCw,
  Edit,
  Trash2,
  Eye,
  X,
} from 'lucide-react'
import { useState, useMemo } from 'react'
import { useNavigate } from 'react-router-dom'

import { AccountForm } from '@/components/accounting/AccountForm'
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog'
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
  useAccounts,
  useDeleteAccount,
  useCreateAccount,
  useUpdateAccount,
} from '@/hooks/useAccounts'
import { useDebounce } from '@/hooks/useDebounce'
import { AccountType, KOREAN_LABELS } from '@/types/accounting'
import type {
  Account,
  AccountCreateRequest,
  AccountUpdateRequest,
} from '@/types/accounting'

/**
 * 계정 목록 페이지
 * 회계 계정 정보 조회 및 관리를 담당하는 페이지
 */
function AccountList() {
  const navigate = useNavigate()

  // 검색 및 필터 상태
  const [searchTerm, setSearchTerm] = useState('')
  const [accountType, setAccountType] = useState<string>('ALL')
  const [accountLevel, setAccountLevel] = useState<string>('ALL')
  const [isActive, setIsActive] = useState<string>('ALL')
  const [showFilters, setShowFilters] = useState(false)

  // 페이지네이션 상태
  const [page, setPage] = useState(0)
  const [size] = useState(20)

  // 삭제 다이얼로그 상태
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false)
  const [accountToDelete, setAccountToDelete] = useState<Account | null>(null)

  // 모달 상태
  const [isFormOpen, setIsFormOpen] = useState(false)
  const [formMode, setFormMode] = useState<'create' | 'edit'>('create')
  const [selectedAccount, setSelectedAccount] = useState<Account | null>(null)

  // 디바운스된 검색어 (300ms)
  const debouncedSearchTerm = useDebounce(searchTerm, 300)

  // API 호출 파라미터
  const params = useMemo(() => {
    const p: any = {
      page,
      size,
      searchTerm: debouncedSearchTerm || undefined,
    }

    if (accountType !== 'ALL') {
      p.accountType = accountType
    }

    if (accountLevel !== 'ALL') {
      p.accountLevel = parseInt(accountLevel)
    }

    if (isActive !== 'ALL') {
      p.isActive = isActive === 'ACTIVE'
    }

    return p
  }, [page, size, debouncedSearchTerm, accountType, accountLevel, isActive])

  // 계정과목 목록 조회
  const { data, isLoading, error, refetch } = useAccounts(params)

  // 계정과목 삭제
  const deleteAccount = useDeleteAccount()

  // 계정과목 등록 및 수정
  const createAccount = useCreateAccount()
  const updateAccount = useUpdateAccount()

  // 계정 타입별 Badge
  const getTypeBadge = (type: AccountType) => {
    const label = KOREAN_LABELS[type]
    switch (type) {
      case AccountType.ASSET:
        return (
          <Badge variant="default" className="bg-blue-100 text-blue-800">
            {label}
          </Badge>
        )
      case AccountType.LIABILITY:
        return (
          <Badge variant="destructive" className="bg-red-100 text-red-800">
            {label}
          </Badge>
        )
      case AccountType.EQUITY:
        return (
          <Badge variant="secondary" className="bg-green-100 text-green-800">
            {label}
          </Badge>
        )
      case AccountType.REVENUE:
        return (
          <Badge variant="outline" className="bg-purple-100 text-purple-800">
            {label}
          </Badge>
        )
      case AccountType.EXPENSE:
        return (
          <Badge variant="outline" className="bg-orange-100 text-orange-800">
            {label}
          </Badge>
        )
      default:
        return <Badge variant="outline">알 수 없음</Badge>
    }
  }

  // 금액 포맷팅
  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('ko-KR', {
      style: 'currency',
      currency: 'KRW',
    }).format(amount)
  }

  // 통계 계산
  const stats = useMemo(() => {
    if (!data?.content) {
      return [
        {
          title: '총 계정 수',
          value: '0',
          icon: Calculator,
        },
        {
          title: '총 자산',
          value: '₩0',
          icon: DollarSign,
        },
        {
          title: '활성 계정',
          value: '0',
          icon: TrendingUp,
        },
      ]
    }

    const totalCount = data.totalElements
    const activeCount = data.content.filter((a: Account) => a.isActive).length

    // 자산 계정의 잔액 합계
    const totalAssets = data.content
      .filter((a: Account) => a.accountType === AccountType.ASSET)
      .reduce((sum: number, a: Account) => sum + (a.currentBalance || 0), 0)

    return [
      {
        title: '총 계정 수',
        value: totalCount.toString(),
        icon: Calculator,
      },
      {
        title: '총 자산',
        value: formatCurrency(totalAssets),
        icon: DollarSign,
      },
      {
        title: '활성 계정',
        value: activeCount.toString(),
        icon: TrendingUp,
      },
    ]
  }, [data])

  // 삭제 핸들러
  const handleDelete = (account: Account) => {
    setAccountToDelete(account)
    setDeleteDialogOpen(true)
  }

  const confirmDelete = () => {
    if (accountToDelete) {
      deleteAccount.mutate(accountToDelete.id, {
        onSuccess: () => {
          setDeleteDialogOpen(false)
          setAccountToDelete(null)
        },
      })
    }
  }

  // 필터 초기화
  const resetFilters = () => {
    setSearchTerm('')
    setAccountType('ALL')
    setAccountLevel('ALL')
    setIsActive('ALL')
    setPage(0)
  }

  // 계정 추가 핸들러
  const handleCreate = () => {
    setFormMode('create')
    setSelectedAccount(null)
    setIsFormOpen(true)
  }

  // 계정 수정 핸들러
  const handleEdit = (account: Account) => {
    setFormMode('edit')
    setSelectedAccount(account)
    setIsFormOpen(true)
  }

  // 폼 제출 핸들러
  const handleFormSubmit = (
    formData: AccountCreateRequest | AccountUpdateRequest
  ) => {
    if (formMode === 'create') {
      createAccount.mutate(
        formData as AccountCreateRequest & { companyId: number },
        {
          onSuccess: () => {
            setIsFormOpen(false)
            refetch()
          },
        }
      )
    } else {
      if (selectedAccount) {
        updateAccount.mutate(
          { id: selectedAccount.id, data: formData as AccountUpdateRequest },
          {
            onSuccess: () => {
              setIsFormOpen(false)
              refetch()
            },
          }
        )
      }
    }
  }

  return (
    <div className="space-y-6 p-6">
      {/* 헤더 */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">계정 관리</h1>
          <p className="mt-1 text-gray-600">
            회계 계정 정보를 조회하고 관리하세요
          </p>
        </div>
        <div className="flex space-x-2">
          <Button
            variant="outline"
            onClick={() => setShowFilters(!showFilters)}
          >
            <FilterIcon className="mr-2 h-4 w-4" />
            필터
          </Button>
          <Button variant="outline" onClick={() => refetch()}>
            <RefreshCw className="mr-2 h-4 w-4" />
            새로고침
          </Button>
          <Button onClick={handleCreate}>
            <Plus className="mr-2 h-4 w-4" />
            계정 추가
          </Button>
        </div>
      </div>

      {/* 통계 카드 */}
      <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
        {stats.map((stat, index) => (
          <Card key={index}>
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-gray-600">
                    {stat.title}
                  </p>
                  <p className="text-2xl font-bold text-gray-900">
                    {stat.value}
                  </p>
                </div>
                <stat.icon className="h-8 w-8 text-gray-400" />
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      {/* 검색 및 필터 */}
      <Card>
        <CardHeader>
          <CardTitle>계정 목록</CardTitle>
          <CardDescription>
            회계 계정 정보를 검색하고 관리할 수 있습니다
          </CardDescription>
        </CardHeader>
        <CardContent>
          {/* 검색바 */}
          <div className="mb-6 flex items-center space-x-4">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 transform text-gray-400" />
              <Input
                placeholder="계정코드, 계정명으로 검색..."
                value={searchTerm}
                onChange={e => setSearchTerm(e.target.value)}
                className="pl-10"
              />
            </div>
          </div>

          {/* 필터 섹션 */}
          {showFilters && (
            <div className="mb-6 grid grid-cols-1 gap-4 rounded-lg border p-4 md:grid-cols-4">
              <div>
                <label className="mb-2 block text-sm font-medium text-gray-700">
                  계정 타입
                </label>
                <Select value={accountType} onValueChange={setAccountType}>
                  <SelectTrigger>
                    <SelectValue placeholder="전체" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="ALL">전체</SelectItem>
                    <SelectItem value={AccountType.ASSET}>자산</SelectItem>
                    <SelectItem value={AccountType.LIABILITY}>부채</SelectItem>
                    <SelectItem value={AccountType.EQUITY}>자본</SelectItem>
                    <SelectItem value={AccountType.REVENUE}>수익</SelectItem>
                    <SelectItem value={AccountType.EXPENSE}>비용</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div>
                <label className="mb-2 block text-sm font-medium text-gray-700">
                  계정 레벨
                </label>
                <Select value={accountLevel} onValueChange={setAccountLevel}>
                  <SelectTrigger>
                    <SelectValue placeholder="전체" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="ALL">전체</SelectItem>
                    <SelectItem value="1">대분류 (1)</SelectItem>
                    <SelectItem value="2">중분류 (2)</SelectItem>
                    <SelectItem value="3">소분류 (3)</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div>
                <label className="mb-2 block text-sm font-medium text-gray-700">
                  사용 여부
                </label>
                <Select value={isActive} onValueChange={setIsActive}>
                  <SelectTrigger>
                    <SelectValue placeholder="전체" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="ALL">전체</SelectItem>
                    <SelectItem value="ACTIVE">사용</SelectItem>
                    <SelectItem value="INACTIVE">미사용</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="flex items-end">
                <Button
                  variant="outline"
                  onClick={resetFilters}
                  className="w-full"
                >
                  <X className="mr-2 h-4 w-4" />
                  초기화
                </Button>
              </div>
            </div>
          )}

          {/* 로딩 상태 */}
          {isLoading && (
            <div className="flex items-center justify-center py-12">
              <div className="h-8 w-8 animate-spin rounded-full border-4 border-gray-200 border-t-blue-600" />
              <span className="ml-3 text-gray-600">로딩 중...</span>
            </div>
          )}

          {/* 에러 상태 */}
          {error && (
            <div className="rounded-lg bg-red-50 p-4 text-red-800">
              <p className="font-medium">데이터를 불러오는데 실패했습니다</p>
              <p className="mt-1 text-sm">
                {error instanceof Error ? error.message : '알 수 없는 오류'}
              </p>
            </div>
          )}

          {/* 계정 테이블 */}
          {!isLoading && !error && (
            <>
              {data?.content && data.content.length > 0 ? (
                <div className="rounded-md border">
                  <Table>
                    <TableHeader>
                      <TableRow>
                        <TableHead>계정코드</TableHead>
                        <TableHead>계정명</TableHead>
                        <TableHead>계정유형</TableHead>
                        <TableHead>계정분류</TableHead>
                        <TableHead>레벨</TableHead>
                        <TableHead>잔액</TableHead>
                        <TableHead>상태</TableHead>
                        <TableHead className="w-[50px]" />
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {data.content.map((account: Account) => (
                        <TableRow key={account.id}>
                          <TableCell className="font-medium">
                            {account.accountCode}
                          </TableCell>
                          <TableCell>{account.accountName}</TableCell>
                          <TableCell>
                            {getTypeBadge(account.accountType)}
                          </TableCell>
                          <TableCell>
                            {KOREAN_LABELS[account.accountCategory]}
                          </TableCell>
                          <TableCell>{account.accountLevel}</TableCell>
                          <TableCell className="text-right">
                            {formatCurrency(account.currentBalance || 0)}
                          </TableCell>
                          <TableCell>
                            <Badge
                              variant={
                                account.isActive ? 'default' : 'secondary'
                              }
                            >
                              {account.isActive ? '활성' : '비활성'}
                            </Badge>
                          </TableCell>
                          <TableCell>
                            <DropdownMenu>
                              <DropdownMenuTrigger asChild>
                                <Button variant="ghost" size="sm">
                                  <MoreHorizontal className="h-4 w-4" />
                                </Button>
                              </DropdownMenuTrigger>
                              <DropdownMenuContent align="end">
                                <DropdownMenuItem
                                  onClick={() =>
                                    navigate(
                                      `/accounting/accounts/${account.id}`
                                    )
                                  }
                                >
                                  <Eye className="mr-2 h-4 w-4" />
                                  상세보기
                                </DropdownMenuItem>
                                <DropdownMenuItem
                                  onClick={() => handleEdit(account)}
                                >
                                  <Edit className="mr-2 h-4 w-4" />
                                  수정
                                </DropdownMenuItem>
                                <DropdownMenuItem
                                  onClick={() => handleDelete(account)}
                                  className="text-red-600"
                                >
                                  <Trash2 className="mr-2 h-4 w-4" />
                                  삭제
                                </DropdownMenuItem>
                              </DropdownMenuContent>
                            </DropdownMenu>
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </div>
              ) : (
                <div className="flex flex-col items-center justify-center py-12 text-gray-500">
                  <Calculator className="mb-4 h-12 w-12 text-gray-400" />
                  <p className="text-lg font-medium">
                    등록된 계정과목이 없습니다
                  </p>
                  <p className="mt-1 text-sm">
                    "계정 추가" 버튼을 클릭하여 새 계정을 등록하세요
                  </p>
                </div>
              )}

              {/* 페이지네이션 */}
              {data && data.totalPages > 1 && (
                <div className="mt-4 flex items-center justify-between">
                  <p className="text-sm text-gray-600">
                    총 {data.totalElements}개 중 {data.number * data.size + 1}-
                    {Math.min(
                      (data.number + 1) * data.size,
                      data.totalElements
                    )}
                    개 표시
                  </p>
                  <div className="flex space-x-2">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setPage(page - 1)}
                      disabled={data.first}
                    >
                      이전
                    </Button>
                    <span className="flex items-center px-4 text-sm">
                      {data.number + 1} / {data.totalPages}
                    </span>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setPage(page + 1)}
                      disabled={data.last}
                    >
                      다음
                    </Button>
                  </div>
                </div>
              )}
            </>
          )}
        </CardContent>
      </Card>

      {/* 삭제 확인 다이얼로그 */}
      <AlertDialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>계정과목 삭제</AlertDialogTitle>
            <AlertDialogDescription>
              정말 "{accountToDelete?.accountName}" 계정을 삭제하시겠습니까?
              <br />이 작업은 되돌릴 수 없습니다.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>취소</AlertDialogCancel>
            <AlertDialogAction
              onClick={confirmDelete}
              className="bg-red-600 hover:bg-red-700"
            >
              {deleteAccount.isPending ? '삭제 중...' : '삭제'}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>

      {/* 계정과목 등록/수정 모달 */}
      <Dialog open={isFormOpen} onOpenChange={setIsFormOpen}>
        <DialogContent className="max-h-[90vh] max-w-3xl overflow-y-auto">
          <DialogHeader>
            <DialogTitle>
              {formMode === 'create' ? '계정과목 등록' : '계정과목 수정'}
            </DialogTitle>
            <DialogDescription>
              {formMode === 'create'
                ? '새로운 계정과목을 등록합니다. 모든 필수 항목을 입력해주세요.'
                : '계정과목 정보를 수정합니다. 모든 필드를 확인한 후 저장해주세요.'}
            </DialogDescription>
          </DialogHeader>
          <AccountForm
            account={
              formMode === 'edit' ? selectedAccount || undefined : undefined
            }
            onSubmit={handleFormSubmit}
            onCancel={() => setIsFormOpen(false)}
            isSubmitting={createAccount.isPending || updateAccount.isPending}
            mode={formMode}
          />
        </DialogContent>
      </Dialog>
    </div>
  )
}

export { AccountList }
