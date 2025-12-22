/**
 * 전표 액션 컴포넌트
 * 전표 상태에 따라 적절한 액션 버튼을 표시하고 처리
 * DRAFT → 수정, 삭제, 승인요청
 * PENDING → 승인, 반려
 * APPROVED → 전기
 * POSTED → 읽기 전용
 * CANCELLED → 읽기 전용
 */

import {
  CheckCircle,
  Send,
  XCircle,
  FileCheck,
  AlertCircle,
} from 'lucide-react'
import { useState } from 'react'
import { useNavigate } from 'react-router-dom'

import { Button } from '@/components/ui/button'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import { useAuth } from '@/contexts/AuthContext'
import {
  useApproveTransaction,
  usePostTransaction,
  useCancelTransaction,
  useUpdateTransaction,
} from '@/hooks/useAccounting'
import { Transaction, TransactionStatus } from '@/types/accounting'

interface TransactionActionsProps {
  transactions: Transaction[]
  onActionComplete: () => void
}

export function TransactionActions({
  transactions,
  onActionComplete,
}: TransactionActionsProps) {
  const navigate = useNavigate()
  const { user } = useAuth()

  const [approveDialogOpen, setApproveDialogOpen] = useState(false)
  const [postDialogOpen, setPostDialogOpen] = useState(false)
  const [cancelDialogOpen, setCancelDialogOpen] = useState(false)
  const [cancelReason, setCancelReason] = useState('')

  const approveMutation = useApproveTransaction()
  const postMutation = usePostTransaction()
  const cancelMutation = useCancelTransaction()

  if (transactions.length === 0) return null

  const firstTransaction = transactions[0]
  const status = firstTransaction.transactionStatus
  const transactionId = firstTransaction.id

  // 승인 처리
  const handleApprove = async () => {
    if (!user) return

    try {
      await approveMutation.mutateAsync({
        id: transactionId,
        approverId: user.id,
      })

      alert('전표가 승인되었습니다.')
      setApproveDialogOpen(false)
      onActionComplete()
    } catch (error) {
      alert(
        '승인 실패: ' +
          (error instanceof Error
            ? error.message
            : '승인 중 오류가 발생했습니다.')
      )
    }
  }

  // 전기 처리
  const handlePost = async () => {
    try {
      await postMutation.mutateAsync(transactionId)

      alert('전표가 전기되어 계정 잔액에 반영되었습니다.')
      setPostDialogOpen(false)
      onActionComplete()
    } catch (error) {
      alert(
        '전기 실패: ' +
          (error instanceof Error
            ? error.message
            : '전기 중 오류가 발생했습니다.')
      )
    }
  }

  // 취소 처리
  const handleCancel = async () => {
    if (!user || !cancelReason.trim()) {
      alert('취소 사유를 입력해주세요.')
      return
    }

    try {
      await cancelMutation.mutateAsync({
        id: transactionId,
        reason: cancelReason,
        cancelById: user.id,
      })

      alert('전표가 취소되었습니다.')
      setCancelDialogOpen(false)
      setCancelReason('')
      onActionComplete()
    } catch (error) {
      alert(
        '취소 실패: ' +
          (error instanceof Error
            ? error.message
            : '취소 중 오류가 발생했습니다.')
      )
    }
  }

  // 상태별 액션 버튼 렌더링
  return (
    <div className="flex gap-2">
      {status === TransactionStatus.DRAFT && (
        <>
          <Button
            variant="outline"
            onClick={() =>
              navigate(`/accounting/transactions/${transactionId}/edit`)
            }
          >
            수정
          </Button>
          <Button onClick={() => setApproveDialogOpen(true)}>
            <Send className="mr-2 h-4 w-4" />
            승인 요청
          </Button>
          <Button
            variant="destructive"
            onClick={() => setCancelDialogOpen(true)}
          >
            <XCircle className="mr-2 h-4 w-4" />
            삭제
          </Button>
        </>
      )}

      {status === TransactionStatus.PENDING && (
        <>
          <Button onClick={() => setApproveDialogOpen(true)}>
            <CheckCircle className="mr-2 h-4 w-4" />
            승인
          </Button>
          <Button
            variant="destructive"
            onClick={() => setCancelDialogOpen(true)}
          >
            <XCircle className="mr-2 h-4 w-4" />
            반려
          </Button>
        </>
      )}

      {status === TransactionStatus.APPROVED && (
        <>
          <Button onClick={() => setPostDialogOpen(true)}>
            <FileCheck className="mr-2 h-4 w-4" />
            전기
          </Button>
          <Button
            variant="destructive"
            onClick={() => setCancelDialogOpen(true)}
          >
            <XCircle className="mr-2 h-4 w-4" />
            취소
          </Button>
        </>
      )}

      {status === TransactionStatus.POSTED && (
        <div className="text-sm text-muted-foreground">
          전기 완료 (수정 불가)
        </div>
      )}

      {status === TransactionStatus.CANCELLED && (
        <div className="text-sm text-destructive">취소됨</div>
      )}

      {/* 승인 다이얼로그 */}
      <Dialog open={approveDialogOpen} onOpenChange={setApproveDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>전표 승인</DialogTitle>
            <DialogDescription>이 전표를 승인하시겠습니까?</DialogDescription>
          </DialogHeader>
          <div className="py-4">
            <p className="text-sm text-muted-foreground">
              전표번호: {firstTransaction.transactionNumber}
            </p>
            <p className="text-sm text-muted-foreground">
              거래일자: {firstTransaction.transactionDate}
            </p>
          </div>
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => setApproveDialogOpen(false)}
            >
              취소
            </Button>
            <Button
              onClick={handleApprove}
              disabled={approveMutation.isPending}
            >
              {approveMutation.isPending ? '처리 중...' : '승인'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* 전기 다이얼로그 */}
      <Dialog open={postDialogOpen} onOpenChange={setPostDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>전표 전기</DialogTitle>
            <DialogDescription className="flex items-start gap-2">
              <AlertCircle className="mt-0.5 h-4 w-4 text-yellow-500" />
              <span>
                전기 후에는 수정이 불가능합니다. 계정 잔액에 즉시 반영됩니다.
              </span>
            </DialogDescription>
          </DialogHeader>
          <div className="py-4">
            <p className="text-sm text-muted-foreground">
              전표번호: {firstTransaction.transactionNumber}
            </p>
            <p className="text-sm text-muted-foreground">
              거래일자: {firstTransaction.transactionDate}
            </p>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setPostDialogOpen(false)}>
              취소
            </Button>
            <Button onClick={handlePost} disabled={postMutation.isPending}>
              {postMutation.isPending ? '처리 중...' : '전기'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* 취소 다이얼로그 */}
      <Dialog open={cancelDialogOpen} onOpenChange={setCancelDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>전표 취소</DialogTitle>
            <DialogDescription className="flex items-start gap-2">
              <AlertCircle className="mt-0.5 h-4 w-4 text-red-500" />
              <span>취소 후 복구가 불가능합니다.</span>
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <div>
              <p className="mb-2 text-sm text-muted-foreground">
                전표번호: {firstTransaction.transactionNumber}
              </p>
              <p className="text-sm text-muted-foreground">
                거래일자: {firstTransaction.transactionDate}
              </p>
            </div>
            <div className="space-y-2">
              <Label htmlFor="cancelReason">취소 사유 (필수)</Label>
              <Textarea
                id="cancelReason"
                placeholder="취소 사유를 입력해주세요"
                value={cancelReason}
                onChange={e => setCancelReason(e.target.value)}
                rows={3}
              />
            </div>
          </div>
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => {
                setCancelDialogOpen(false)
                setCancelReason('')
              }}
            >
              닫기
            </Button>
            <Button
              variant="destructive"
              onClick={handleCancel}
              disabled={cancelMutation.isPending || !cancelReason.trim()}
            >
              {cancelMutation.isPending ? '처리 중...' : '취소'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  )
}
