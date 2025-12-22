/**
 * 전표 상태 배지 컴포넌트
 * 전표의 승인 워크플로우 상태를 시각적으로 표시
 */

import { Badge } from '@/components/ui/badge'
import {
  TransactionStatus,
  TRANSACTION_STATUS_COLORS,
  KOREAN_LABELS,
} from '@/types/accounting'

interface TransactionStatusBadgeProps {
  status: TransactionStatus
  className?: string
}

export function TransactionStatusBadge({
  status,
  className,
}: TransactionStatusBadgeProps) {
  return (
    <Badge
      className={`${TRANSACTION_STATUS_COLORS[status]} ${className || ''}`}
    >
      {KOREAN_LABELS.transactionStatus[status]}
    </Badge>
  )
}
