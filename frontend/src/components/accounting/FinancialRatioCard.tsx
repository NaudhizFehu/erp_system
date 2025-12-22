/**
 * 재무비율 카드 컴포넌트
 * 재무비율 지표를 시각적으로 표시하고 색상 코딩을 통해 건전성을 나타냅니다
 */

import { TrendingUp, TrendingDown, Percent, DollarSign } from 'lucide-react'

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'

export type FinancialRatioType =
  | 'current'
  | 'debt'
  | 'equity'
  | 'roa'
  | 'roe'
  | 'grossMargin'
  | 'netMargin'

interface FinancialRatioCardProps {
  title: string
  value: number
  type: FinancialRatioType
  previousValue?: number
  icon?: React.ReactNode
  className?: string
}

/**
 * 재무비율 유형별 아이콘 매핑
 */
const defaultIcons: Record<FinancialRatioType, React.ReactNode> = {
  current: <Percent className="h-4 w-4 text-muted-foreground" />,
  debt: <Percent className="h-4 w-4 text-muted-foreground" />,
  equity: <Percent className="h-4 w-4 text-muted-foreground" />,
  roa: <DollarSign className="h-4 w-4 text-muted-foreground" />,
  roe: <DollarSign className="h-4 w-4 text-muted-foreground" />,
  grossMargin: <Percent className="h-4 w-4 text-muted-foreground" />,
  netMargin: <Percent className="h-4 w-4 text-muted-foreground" />,
}

/**
 * 재무비율 색상 결정
 * 건전한 비율은 초록색, 위험한 비율은 빨간색으로 표시
 */
function getRatioColor(type: FinancialRatioType, value: number): string {
  switch (type) {
    case 'current':
      // 유동비율: 1.0 이상 양호, 2.0 이상 우수
      if (value >= 2.0) return 'text-green-600'
      if (value >= 1.0) return 'text-blue-600'
      return 'text-red-600'

    case 'debt':
      // 부채비율: 100% 이하 양호, 200% 이상 위험
      if (value >= 200) return 'text-red-600'
      if (value >= 100) return 'text-yellow-600'
      return 'text-green-600'

    case 'equity':
      // 자기자본비율: 50% 이상 양호, 30% 이하 위험
      if (value >= 50) return 'text-green-600'
      if (value >= 30) return 'text-blue-600'
      return 'text-red-600'

    case 'roa':
    case 'roe':
      // ROA, ROE: 5% 이상 양호, 10% 이상 우수
      if (value >= 10) return 'text-green-600'
      if (value >= 5) return 'text-blue-600'
      if (value >= 0) return 'text-yellow-600'
      return 'text-red-600'

    case 'grossMargin':
    case 'netMargin':
      // 마진율: 10% 이상 양호, 20% 이상 우수
      if (value >= 20) return 'text-green-600'
      if (value >= 10) return 'text-blue-600'
      if (value >= 0) return 'text-yellow-600'
      return 'text-red-600'

    default:
      return 'text-gray-600'
  }
}

/**
 * 전기 대비 변동률 계산
 */
function calculateChangeRate(
  current: number,
  previous?: number
): number | null {
  if (!previous || previous === 0) return null
  return ((current - previous) / previous) * 100
}

/**
 * 재무비율 카드 컴포넌트
 */
export function FinancialRatioCard({
  title,
  value,
  type,
  previousValue,
  icon,
  className = '',
}: FinancialRatioCardProps) {
  const colorClass = getRatioColor(type, value)
  const changeRate = calculateChangeRate(value, previousValue)
  const isPositiveChange = changeRate !== null && changeRate > 0

  // 백분율 또는 비율로 표시
  const displayValue =
    type === 'current' ? value.toFixed(2) : `${value.toFixed(2)}%`

  return (
    <Card className={className}>
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
        <CardTitle className="text-sm font-medium">{title}</CardTitle>
        {icon || defaultIcons[type]}
      </CardHeader>
      <CardContent>
        <div className={`text-2xl font-bold ${colorClass}`}>{displayValue}</div>

        {/* 전기 대비 변동 표시 */}
        {changeRate !== null && (
          <div className="mt-1 flex items-center gap-1 text-xs text-muted-foreground">
            {isPositiveChange ? (
              <TrendingUp className="h-3 w-3 text-green-600" />
            ) : (
              <TrendingDown className="h-3 w-3 text-red-600" />
            )}
            <span
              className={isPositiveChange ? 'text-green-600' : 'text-red-600'}
            >
              {isPositiveChange ? '+' : ''}
              {changeRate.toFixed(1)}%
            </span>
            <span>전기 대비</span>
          </div>
        )}

        {/* 변동 정보가 없으면 설명 표시 */}
        {changeRate === null && (
          <p className="mt-1 text-xs text-muted-foreground">
            {getRatioDescription(type, value)}
          </p>
        )}
      </CardContent>
    </Card>
  )
}

/**
 * 재무비율 설명 텍스트
 */
function getRatioDescription(type: FinancialRatioType, value: number): string {
  switch (type) {
    case 'current':
      if (value >= 2.0) return '유동성 우수'
      if (value >= 1.0) return '유동성 양호'
      return '유동성 부족'

    case 'debt':
      if (value >= 200) return '부채 비중 높음'
      if (value >= 100) return '부채 비중 보통'
      return '부채 비중 낮음'

    case 'equity':
      if (value >= 50) return '재무 안정성 우수'
      if (value >= 30) return '재무 안정성 양호'
      return '재무 안정성 주의'

    case 'roa':
      if (value >= 10) return '자산 활용 우수'
      if (value >= 5) return '자산 활용 양호'
      return '자산 활용 개선 필요'

    case 'roe':
      if (value >= 10) return '자기자본 수익성 우수'
      if (value >= 5) return '자기자본 수익성 양호'
      return '자기자본 수익성 개선 필요'

    case 'grossMargin':
      if (value >= 20) return '매출총이익률 우수'
      if (value >= 10) return '매출총이익률 양호'
      return '매출총이익률 개선 필요'

    case 'netMargin':
      if (value >= 20) return '순이익률 우수'
      if (value >= 10) return '순이익률 양호'
      return '순이익률 개선 필요'

    default:
      return ''
  }
}
