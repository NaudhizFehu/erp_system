/**
 * 사번 확인 도우미 컴포넌트
 * 회사별 최근 사번을 확인하여 중복 방지를 도와줍니다
 */

import { useState, useMemo, useEffect } from 'react'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { useAuth } from '@/contexts/AuthContext'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Badge } from '@/components/ui/badge'
import { AlertCircle, Users, Hash } from 'lucide-react'
import { useCompanies } from '@/hooks/useEmployees'
import { useRecentEmployeesByCompany } from '@/hooks/useEmployees'
import type { Company, Employee } from '@/types/hr'

interface EmployeeNumberHelperProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  onSelectEmployeeNumber?: (employeeNumber: string, companyId: number) => void
}

export function EmployeeNumberHelper({ open, onOpenChange, onSelectEmployeeNumber }: EmployeeNumberHelperProps) {
  const { user } = useAuth()
  const [selectedCompanyId, setSelectedCompanyId] = useState<number | null>(null)
  
  // 데이터 조회
  const { data: allCompanies = [], isLoading: isLoadingCompanies } = useCompanies()
  const { data: recentEmployees = [], isLoading } = useRecentEmployeesByCompany(selectedCompanyId || 0)

  // 권한별 회사 목록 필터링
  const filteredCompanies = useMemo(() => {
    if (!user) return []
    
    // SUPER_ADMIN: 모든 회사
    if (user.role === 'SUPER_ADMIN') {
      return allCompanies
    }
    
    // ADMIN, MANAGER: 자신의 회사만
    if (user.role === 'ADMIN' || user.role === 'MANAGER') {
      if (user.company) {
        return allCompanies.filter(company => company.id === user.company?.id)
      }
      return []
    }
    
    return []
  }, [user, allCompanies])

  // 회사가 1개인 경우 자동 선택
  useEffect(() => {
    if (filteredCompanies.length === 1 && !selectedCompanyId) {
      setSelectedCompanyId(filteredCompanies[0].id)
    }
  }, [filteredCompanies, selectedCompanyId])

  const handleCompanyChange = (value: string) => {
    setSelectedCompanyId(Number(value))
  }

  const getLastEmployeeNumber = (employees: Employee[]): string | null => {
    if (employees.length === 0) return null
    
    // 사번을 문자열로 정렬하여 가장 마지막 사번 반환
    const sortedEmployees = [...employees].sort((a, b) => 
      b.employeeNumber.localeCompare(a.employeeNumber)
    )
    
    return sortedEmployees[0]?.employeeNumber || null
  }

  const getNextEmployeeNumber = (employees: Employee[]): string | null => {
    const lastNumber = getLastEmployeeNumber(employees)
    if (!lastNumber) return null
    
    // 숫자 부분 추출하여 +1 (예: "ABC008" -> "ABC009")
    const match = lastNumber.match(/^([A-Z]+)(\d+)$/)
    if (match && match[1] && match[2]) {
      const prefix = match[1]  // "ABC"
      const number = parseInt(match[2]) + 1  // 8 + 1 = 9
      const paddedNumber = String(number).padStart(match[2].length, '0')  // "009"
      return `${prefix}${paddedNumber}`
    }
    return null
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-2xl">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <Hash className="h-5 w-5" />
            사번 확인 도우미
          </DialogTitle>
          <DialogDescription>
            회사를 선택하여 최근 사번과 다음 사번을 확인하세요.
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-4">
          {/* 회사 선택 */}
          <div className="space-y-2">
            <label className="text-sm font-medium">회사 선택</label>
            {isLoadingCompanies ? (
              <div className="text-sm text-muted-foreground p-3 border rounded-md">
                회사 목록을 불러오는 중...
              </div>
            ) : filteredCompanies.length === 0 ? (
              <div className="text-sm text-muted-foreground p-3 border rounded-md bg-muted/50">
                등록된 회사가 없습니다.
              </div>
            ) : filteredCompanies.length === 1 ? (
              // 회사가 1개인 경우 자동 선택 및 표시
              <div className="text-sm p-3 border rounded-md bg-muted/50">
                {filteredCompanies[0].name}
              </div>
            ) : (
              // 회사가 여러 개인 경우 드롭다운 표시
              <Select 
                onValueChange={handleCompanyChange}
                value={selectedCompanyId?.toString()}
              >
                <SelectTrigger>
                  <SelectValue placeholder="회사를 선택하세요" />
                </SelectTrigger>
                <SelectContent>
                  {filteredCompanies.map((company) => (
                    <SelectItem key={company.id} value={company.id.toString()}>
                      {company.name}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            )}
          </div>

          {/* 결과 표시 */}
          {selectedCompanyId && (
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2 text-lg">
                  <Users className="h-4 w-4" />
                  사번 정보
                </CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                {isLoading ? (
                  <div className="text-center py-4">
                    <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-gray-900 mx-auto"></div>
                    <p className="text-sm text-gray-500 mt-2">로딩 중...</p>
                  </div>
                ) : recentEmployees.length > 0 ? (
                  <>
                    {/* 최근 사번 */}
                    <div className="space-y-2">
                      <label className="text-sm font-medium text-gray-700">최근 사번</label>
                      <div className="flex items-center gap-2">
                        <Badge variant="outline" className="font-mono">
                          {getLastEmployeeNumber(recentEmployees)}
                        </Badge>
                        <span className="text-sm text-gray-500">
                          (총 {recentEmployees.length}명 중 마지막)
                        </span>
                      </div>
                    </div>

                    {/* 다음 추천 사번 */}
                    <div className="space-y-2">
                      <label className="text-sm font-medium text-gray-700">추천 사번</label>
                      <div className="flex items-center gap-2">
                        <Badge 
                          variant="default" 
                          className="font-mono cursor-pointer hover:bg-primary/80 transition-colors"
                          onClick={() => {
                            const nextNumber = getNextEmployeeNumber(recentEmployees)
                            if (nextNumber && onSelectEmployeeNumber && selectedCompanyId) {
                              onSelectEmployeeNumber(nextNumber, selectedCompanyId)
                              onOpenChange(false)
                            }
                          }}
                        >
                          {getNextEmployeeNumber(recentEmployees)}
                        </Badge>
                        <span className="text-sm text-gray-500">
                          (다음 순번)
                        </span>
                      </div>
                    </div>

                    {/* 최근 직원 목록 */}
                    <div className="space-y-2">
                      <label className="text-sm font-medium text-gray-700">최근 등록된 직원</label>
                      <div className="max-h-32 overflow-y-auto border rounded-md p-2 bg-gray-50">
                        {recentEmployees.map((employee) => (
                          <div key={employee.id} className="flex justify-between items-center py-1 text-sm">
                            <span className="font-mono">{employee.employeeNumber}</span>
                            <span>{employee.name}</span>
                          </div>
                        ))}
                      </div>
                    </div>
                  </>
                ) : (
                  <div className="text-center py-4">
                    <AlertCircle className="h-8 w-8 text-gray-400 mx-auto mb-2" />
                    <p className="text-sm text-gray-500">해당 회사에 등록된 직원이 없습니다.</p>
                    <p className="text-xs text-gray-400 mt-1">첫 번째 직원의 사번을 자유롭게 입력하세요.</p>
                  </div>
                )}
              </CardContent>
            </Card>
          )}

          {/* 안내 메시지 */}
          <div className="bg-blue-50 border border-blue-200 rounded-md p-3">
            <div className="flex items-start gap-2">
              <AlertCircle className="h-4 w-4 text-blue-600 mt-0.5 flex-shrink-0" />
              <div className="text-sm text-blue-800">
                <p className="font-medium">사번 중복 방지 안내</p>
                <p className="mt-1">
                  추천 사번을 사용하거나, 기존 사번과 중복되지 않도록 확인 후 입력하세요.
                </p>
              </div>
            </div>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  )
}
