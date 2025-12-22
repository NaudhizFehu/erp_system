/**
 * 재무제표 테이블 컴포넌트
 * 계층적 재무제표 데이터를 표시합니다
 */

import { TrendingUp, TrendingDown, Minus } from 'lucide-react'

import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { accountingUtils } from '@/services/accountingApi'
import { FinancialReportItem } from '@/types/accounting'

interface FinancialStatementTableProps {
  items: FinancialReportItem[]
  showComparison?: boolean
  className?: string
}

/**
 * 재무제표 테이블 컴포넌트
 */
export function FinancialStatementTable({
  items,
  showComparison = true,
  className = '',
}: FinancialStatementTableProps) {
  // 보이는 항목만 필터링
  const visibleItems = items.filter(item => item.isVisible)

  if (visibleItems.length === 0) {
    return (
      <div className="rounded-md border p-8 text-center text-muted-foreground">
        표시할 재무제표 데이터가 없습니다
      </div>
    )
  }

  return (
    <div className={`rounded-md border ${className}`}>
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead className="min-w-[250px]">항목</TableHead>
            <TableHead className="w-[140px] text-right">당기</TableHead>
            {showComparison && (
              <>
                <TableHead className="w-[140px] text-right">전기</TableHead>
                <TableHead className="w-[100px] text-right">증감</TableHead>
                <TableHead className="w-[80px] text-right">증감률</TableHead>
              </>
            )}
          </TableRow>
        </TableHeader>
        <TableBody>
          {visibleItems.map(item => (
            <TableRow
              key={item.id}
              className={item.isTotalItem ? 'bg-muted/50' : ''}
            >
              {/* 항목명 - 계층 구조 표시 */}
              <TableCell>
                <div
                  className={`${item.isBold ? 'font-bold' : 'font-normal'}`}
                  style={{ paddingLeft: `${item.indentLevel * 20}px` }}
                >
                  {item.itemName}
                  {item.itemCode && (
                    <span className="ml-2 text-xs text-muted-foreground">
                      ({item.itemCode})
                    </span>
                  )}
                </div>
              </TableCell>

              {/* 당기 금액 */}
              <TableCell
                className={`text-right ${item.isBold ? 'font-bold' : 'font-normal'}`}
              >
                <span
                  className={
                    item.currentAmount < 0 ? 'text-red-600' : 'text-gray-900'
                  }
                >
                  {accountingUtils.formatCurrency(item.currentAmount)}
                </span>
              </TableCell>

              {showComparison && (
                <>
                  {/* 전기 금액 */}
                  <TableCell
                    className={`text-right ${item.isBold ? 'font-bold' : 'font-normal'}`}
                  >
                    <span
                      className={
                        item.previousAmount < 0
                          ? 'text-red-600'
                          : 'text-gray-900'
                      }
                    >
                      {accountingUtils.formatCurrency(item.previousAmount)}
                    </span>
                  </TableCell>

                  {/* 증감액 */}
                  <TableCell className="text-right">
                    <div className="flex items-center justify-end gap-1">
                      {item.changeAmount > 0 && (
                        <TrendingUp className="h-3 w-3 text-green-600" />
                      )}
                      {item.changeAmount < 0 && (
                        <TrendingDown className="h-3 w-3 text-red-600" />
                      )}
                      {item.changeAmount === 0 && (
                        <Minus className="h-3 w-3 text-gray-400" />
                      )}
                      <span
                        className={
                          item.changeAmount > 0
                            ? 'text-green-600'
                            : item.changeAmount < 0
                              ? 'text-red-600'
                              : 'text-gray-600'
                        }
                      >
                        {accountingUtils.formatCurrency(
                          Math.abs(item.changeAmount)
                        )}
                      </span>
                    </div>
                  </TableCell>

                  {/* 증감률 */}
                  <TableCell className="text-right">
                    {item.previousAmount !== 0 ? (
                      <span
                        className={
                          item.changeRate > 0
                            ? 'text-green-600'
                            : item.changeRate < 0
                              ? 'text-red-600'
                              : 'text-gray-600'
                        }
                      >
                        {item.changeRate > 0 ? '+' : ''}
                        {item.changeRate.toFixed(1)}%
                      </span>
                    ) : (
                      <span className="text-gray-400">-</span>
                    )}
                  </TableCell>
                </>
              )}
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  )
}
