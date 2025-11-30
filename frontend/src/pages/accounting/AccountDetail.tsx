import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { ArrowLeft, Edit, Trash2, Plus } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
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
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import toast from 'react-hot-toast'
import {
  useAccount,
  useAccountTree,
  useUpdateAccount,
  useDeleteAccount,
} from '@/hooks/useAccounts'
import { AccountForm } from '@/components/accounting/AccountForm'
import {
  AccountType,
  AccountCategory,
  DebitCreditType,
  Account,
  AccountUpdateRequest,
  AccountTreeNode,
} from '@/types/accounting'

// 한글 레이블 매핑
const KOREAN_LABELS = {
  accountType: {
    [AccountType.ASSET]: '자산',
    [AccountType.LIABILITY]: '부채',
    [AccountType.EQUITY]: '자본',
    [AccountType.REVENUE]: '수익',
    [AccountType.EXPENSE]: '비용',
  },
  accountCategory: {
    [AccountCategory.CURRENT_ASSET]: '유동자산',
    [AccountCategory.NON_CURRENT_ASSET]: '비유동자산',
    [AccountCategory.CURRENT_LIABILITY]: '유동부채',
    [AccountCategory.NON_CURRENT_LIABILITY]: '비유동부채',
    [AccountCategory.PAID_IN_CAPITAL]: '납입자본',
    [AccountCategory.RETAINED_EARNINGS]: '이익잉여금',
    [AccountCategory.OPERATING_REVENUE]: '영업수익',
    [AccountCategory.NON_OPERATING_REVENUE]: '영업외수익',
    [AccountCategory.OPERATING_EXPENSE]: '영업비용',
    [AccountCategory.NON_OPERATING_EXPENSE]: '영업외비용',
  },
  debitCreditType: {
    [DebitCreditType.DEBIT]: '차변',
    [DebitCreditType.CREDIT]: '대변',
  },
  accountLevel: {
    1: '대분류 (Level 1)',
    2: '중분류 (Level 2)',
    3: '소분류 (Level 3)',
  },
}

export default function AccountDetail() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [editDialogOpen, setEditDialogOpen] = useState(false)
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false)

  // API Hooks
  const { data: account, isLoading, error } = useAccount(Number(id))
  const { data: accountTree } = useAccountTree()
  const updateAccount = useUpdateAccount()
  const deleteAccount = useDeleteAccount()

  // 하위 계정 찾기
  const findChildren = (accountId: number, tree?: AccountTreeNode[]): AccountTreeNode[] => {
    if (!tree) return []

    for (const node of tree) {
      if (node.id === accountId) {
        return node.children || []
      }
      const found = findChildren(accountId, node.children)
      if (found.length > 0) return found
    }
    return []
  }

  const childAccounts = account ? findChildren(account.id, accountTree) : []

  // 수정 핸들러
  const handleUpdate = async (data: AccountUpdateRequest) => {
    if (!account) return

    try {
      await updateAccount.mutateAsync({ id: account.id, data })
      toast.success(`${data.accountName} 계정과목이 성공적으로 수정되었습니다.`)
      setEditDialogOpen(false)
    } catch (error: any) {
      toast.error(error.response?.data?.message || '계정과목 수정에 실패했습니다.')
    }
  }

  // 삭제 핸들러
  const handleDelete = async () => {
    if (!account) return

    try {
      await deleteAccount.mutateAsync(account.id)
      toast.success(`${account.accountName} 계정과목이 성공적으로 삭제되었습니다.`)
      navigate('/accounting/accounts')
    } catch (error: any) {
      toast.error(error.response?.data?.message || '계정과목 삭제에 실패했습니다.')
    }
  }

  // 뒤로가기
  const handleBack = () => {
    navigate('/accounting/accounts')
  }

  // 로딩 상태
  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto mb-4"></div>
          <p className="text-muted-foreground">계정과목 정보를 불러오는 중...</p>
        </div>
      </div>
    )
  }

  // 에러 상태
  if (error || !account) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="text-center">
          <p className="text-destructive text-lg mb-2">계정과목을 불러올 수 없습니다.</p>
          <p className="text-muted-foreground mb-4">
            {error?.message || '계정과목이 존재하지 않거나 접근 권한이 없습니다.'}
          </p>
          <Button onClick={handleBack}>목록으로 돌아가기</Button>
        </div>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      {/* 헤더 */}
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-4">
          <Button variant="ghost" size="icon" onClick={handleBack}>
            <ArrowLeft className="h-5 w-5" />
          </Button>
          <div>
            <h1 className="text-3xl font-bold">{account.accountName}</h1>
            <p className="text-muted-foreground">{account.accountCode}</p>
          </div>
          <Badge variant={account.isActive ? 'default' : 'secondary'}>
            {account.isActive ? '활성' : '비활성'}
          </Badge>
        </div>
        <div className="flex space-x-2">
          <Button variant="outline" onClick={() => setEditDialogOpen(true)}>
            <Edit className="h-4 w-4 mr-2" />
            수정
          </Button>
          <Button variant="destructive" onClick={() => setDeleteDialogOpen(true)}>
            <Trash2 className="h-4 w-4 mr-2" />
            삭제
          </Button>
        </div>
      </div>

      {/* 기본 정보 카드 */}
      <Card>
        <CardHeader>
          <CardTitle>기본 정보</CardTitle>
          <CardDescription>계정과목의 기본 정보를 확인할 수 있습니다.</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <h3 className="text-sm font-medium text-muted-foreground mb-1">계정코드</h3>
              <p className="text-base">{account.accountCode}</p>
            </div>
            <div>
              <h3 className="text-sm font-medium text-muted-foreground mb-1">계정명</h3>
              <p className="text-base">{account.accountName}</p>
            </div>
            {account.accountNameEn && (
              <div>
                <h3 className="text-sm font-medium text-muted-foreground mb-1">계정명 (영문)</h3>
                <p className="text-base">{account.accountNameEn}</p>
              </div>
            )}
            <div>
              <h3 className="text-sm font-medium text-muted-foreground mb-1">계정 타입</h3>
              <p className="text-base">{KOREAN_LABELS.accountType[account.accountType]}</p>
            </div>
            <div>
              <h3 className="text-sm font-medium text-muted-foreground mb-1">계정 분류</h3>
              <p className="text-base">{KOREAN_LABELS.accountCategory[account.accountCategory]}</p>
            </div>
            <div>
              <h3 className="text-sm font-medium text-muted-foreground mb-1">차변/대변</h3>
              <p className="text-base">{KOREAN_LABELS.debitCreditType[account.debitCreditType]}</p>
            </div>
            <div>
              <h3 className="text-sm font-medium text-muted-foreground mb-1">계정 레벨</h3>
              <p className="text-base">{KOREAN_LABELS.accountLevel[account.accountLevel as 1 | 2 | 3]}</p>
            </div>
            <div>
              <h3 className="text-sm font-medium text-muted-foreground mb-1">정렬 순서</h3>
              <p className="text-base">{account.sortOrder}</p>
            </div>
            {account.parentAccount && (
              <div>
                <h3 className="text-sm font-medium text-muted-foreground mb-1">상위 계정</h3>
                <p className="text-base">
                  {account.parentAccount.accountName} ({account.parentAccount.accountCode})
                </p>
              </div>
            )}
            {account.taxCode && (
              <div>
                <h3 className="text-sm font-medium text-muted-foreground mb-1">세금 코드</h3>
                <p className="text-base">{account.taxCode}</p>
              </div>
            )}
            <div>
              <h3 className="text-sm font-medium text-muted-foreground mb-1">잔액 추적</h3>
              <p className="text-base">{account.trackBalance ? '사용' : '미사용'}</p>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* 잔액 정보 카드 */}
      <Card>
        <CardHeader>
          <CardTitle>잔액 정보</CardTitle>
          <CardDescription>계정과목의 잔액 및 예산 정보를 확인할 수 있습니다.</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div>
              <h3 className="text-sm font-medium text-muted-foreground mb-1">기초 잔액</h3>
              <p className="text-2xl font-bold">
                {account.openingBalance?.toLocaleString() || 0} 원
              </p>
            </div>
            <div>
              <h3 className="text-sm font-medium text-muted-foreground mb-1">현재 잔액</h3>
              <p className="text-2xl font-bold">
                {account.currentBalance?.toLocaleString() || 0} 원
              </p>
            </div>
            <div>
              <h3 className="text-sm font-medium text-muted-foreground mb-1">예산 금액</h3>
              <p className="text-2xl font-bold">
                {account.budgetAmount?.toLocaleString() || 0} 원
              </p>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* 탭 섹션 */}
      <Tabs defaultValue="children" className="w-full">
        <TabsList>
          <TabsTrigger value="children">
            하위 계정 ({childAccounts.length})
          </TabsTrigger>
          <TabsTrigger value="transactions" disabled>
            최근 거래 내역 (준비 중)
          </TabsTrigger>
        </TabsList>

        {/* 하위 계정 탭 */}
        <TabsContent value="children">
          <Card>
            <CardHeader>
              <div className="flex items-center justify-between">
                <div>
                  <CardTitle>하위 계정 목록</CardTitle>
                  <CardDescription>
                    이 계정의 하위 계정 목록입니다.
                  </CardDescription>
                </div>
                <Button
                  size="sm"
                  onClick={() => navigate('/accounting/accounts/new', {
                    state: { parentAccountId: account.id }
                  })}
                >
                  <Plus className="h-4 w-4 mr-2" />
                  하위 계정 추가
                </Button>
              </div>
            </CardHeader>
            <CardContent>
              {childAccounts.length === 0 ? (
                <div className="text-center py-8 text-muted-foreground">
                  하위 계정이 없습니다.
                </div>
              ) : (
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>계정코드</TableHead>
                      <TableHead>계정명</TableHead>
                      <TableHead>레벨</TableHead>
                      <TableHead className="text-center">상태</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {childAccounts.map((child) => (
                      <TableRow
                        key={child.id}
                        className="cursor-pointer hover:bg-muted/50"
                        onClick={() => navigate(`/accounting/accounts/${child.id}`)}
                      >
                        <TableCell className="font-mono">{child.accountCode}</TableCell>
                        <TableCell className="font-medium">{child.accountName}</TableCell>
                        <TableCell>
                          {KOREAN_LABELS.accountLevel[child.accountLevel as 1 | 2 | 3]}
                        </TableCell>
                        <TableCell className="text-center">
                          <Badge variant={child.isActive ? 'default' : 'secondary'}>
                            {child.isActive ? '활성' : '비활성'}
                          </Badge>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              )}
            </CardContent>
          </Card>
        </TabsContent>

        {/* 최근 거래 내역 탭 (PHASE 2에서 구현) */}
        <TabsContent value="transactions">
          <Card>
            <CardHeader>
              <CardTitle>최근 거래 내역</CardTitle>
              <CardDescription>이 계정의 최근 거래 내역입니다.</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="text-center py-8 text-muted-foreground">
                거래 내역 기능은 PHASE 2에서 구현 예정입니다.
              </div>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>

      {/* 설명 카드 */}
      {account.description && (
        <Card>
          <CardHeader>
            <CardTitle>설명</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-base whitespace-pre-wrap">{account.description}</p>
          </CardContent>
        </Card>
      )}

      {/* 수정 모달 */}
      <Dialog open={editDialogOpen} onOpenChange={setEditDialogOpen}>
        <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>계정과목 수정</DialogTitle>
            <DialogDescription>
              계정과목 정보를 수정합니다. 모든 필드를 확인한 후 저장해주세요.
            </DialogDescription>
          </DialogHeader>
          <AccountForm
            account={account}
            onSubmit={handleUpdate}
            onCancel={() => setEditDialogOpen(false)}
            isSubmitting={updateAccount.isPending}
            mode="edit"
          />
        </DialogContent>
      </Dialog>

      {/* 삭제 확인 대화상자 */}
      <AlertDialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>계정과목 삭제</AlertDialogTitle>
            <AlertDialogDescription>
              정말로 <strong>{account.accountName}</strong> 계정과목을 삭제하시겠습니까?
              {childAccounts.length > 0 && (
                <div className="mt-2 p-2 bg-destructive/10 rounded-md">
                  <p className="text-destructive font-medium">
                    ⚠️ 이 계정에는 {childAccounts.length}개의 하위 계정이 있습니다.
                  </p>
                  <p className="text-destructive text-sm">
                    하위 계정을 먼저 삭제하거나 다른 상위 계정으로 이동해주세요.
                  </p>
                </div>
              )}
              <p className="mt-2">이 작업은 되돌릴 수 없습니다.</p>
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>취소</AlertDialogCancel>
            <AlertDialogAction
              onClick={handleDelete}
              disabled={childAccounts.length > 0 || deleteAccount.isPending}
              className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
            >
              {deleteAccount.isPending ? '삭제 중...' : '삭제'}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  )
}
