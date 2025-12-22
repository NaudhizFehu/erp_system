/**
 * 보고서 상태 배지 컴포넌트
 * 재무보고서의 승인 워크플로우 상태를 시각적으로 표시
 */

import { Badge } from '@/components/ui/badge'
import { ReportStatus } from '@/types/accounting'

interface ReportStatusBadgeProps {
  status: ReportStatus
  className?: string
}

/**
 * 보고서 상태별 색상 매핑
 */
export const REPORT_STATUS_COLORS = {
  [ReportStatus.DRAFT]: 'bg-gray-100 text-gray-800',
  [ReportStatus.GENERATED]: 'bg-blue-100 text-blue-800',
  [ReportStatus.REVIEWED]: 'bg-yellow-100 text-yellow-800',
  [ReportStatus.APPROVED]: 'bg-green-100 text-green-800',
  [ReportStatus.PUBLISHED]: 'bg-purple-100 text-purple-800',
} as const

/**
 * 보고서 상태 한글 라벨
 */
export const REPORT_STATUS_LABELS = {
  [ReportStatus.DRAFT]: '초안',
  [ReportStatus.GENERATED]: '생성됨',
  [ReportStatus.REVIEWED]: '검토됨',
  [ReportStatus.APPROVED]: '승인됨',
  [ReportStatus.PUBLISHED]: '발행됨',
} as const

export function ReportStatusBadge({
  status,
  className,
}: ReportStatusBadgeProps) {
  return (
    <Badge className={`${REPORT_STATUS_COLORS[status]} ${className || ''}`}>
      {REPORT_STATUS_LABELS[status]}
    </Badge>
  )
}
