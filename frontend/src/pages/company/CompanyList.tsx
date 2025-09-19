import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Input } from '@/components/ui/input'
import { Search, Building2, Plus, Eye } from 'lucide-react'
import api from '@/services/api'

/**
 * 회사 정보 인터페이스
 */
interface Company {
  id: number
  companyCode: string
  name: string
  nameEn?: string
  businessNumber?: string
  ceoName?: string
  businessType?: string
  address?: string
  phone?: string
  email?: string
  status: string
  companyType?: string
  employeeCount?: number
}

/**
 * 회사 목록 페이지
 */
function CompanyList() {
  const navigate = useNavigate()
  const [companies, setCompanies] = useState<Company[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [searchTerm, setSearchTerm] = useState('')
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    fetchCompanies()
  }, [])

  const fetchCompanies = async () => {
    try {
      setIsLoading(true)
      setError(null)
      
      const response = await api.get('/companies')
      
      if (response.success) {
        setCompanies(response.data || [])
      } else {
        setError(response.message || '회사 목록을 불러올 수 없습니다')
      }
    } catch (error: any) {
      console.error('회사 목록 조회 오류:', error)
      setError('회사 목록을 불러오는 중 오류가 발생했습니다')
    } finally {
      setIsLoading(false)
    }
  }

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'ACTIVE':
        return <Badge variant="default" className="bg-green-100 text-green-800">활성</Badge>
      case 'INACTIVE':
        return <Badge variant="secondary">비활성</Badge>
      case 'SUSPENDED':
        return <Badge variant="destructive">정지</Badge>
      case 'CLOSED':
        return <Badge variant="outline">폐업</Badge>
      default:
        return <Badge variant="outline">{status}</Badge>
    }
  }

  const filteredCompanies = companies.filter(company =>
    company.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    company.companyCode.toLowerCase().includes(searchTerm.toLowerCase()) ||
    (company.businessNumber && company.businessNumber.includes(searchTerm)) ||
    (company.ceoName && company.ceoName.toLowerCase().includes(searchTerm.toLowerCase()))
  )

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-96">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p className="text-gray-600">회사 목록을 불러오는 중...</p>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="flex items-center justify-center min-h-96">
        <div className="text-center">
          <div className="text-red-500 text-6xl mb-4">⚠️</div>
          <h2 className="text-xl font-semibold text-gray-900 mb-2">오류 발생</h2>
          <p className="text-gray-600 mb-4">{error}</p>
          <Button onClick={fetchCompanies} variant="outline">
            다시 시도
          </Button>
        </div>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      {/* 헤더 */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">회사 관리</h1>
          <p className="text-gray-600">등록된 회사 목록을 확인하고 관리할 수 있습니다</p>
        </div>
        <Button onClick={() => navigate('/companies/new')}>
          <Plus className="w-4 h-4 mr-2" />
          회사 등록
        </Button>
      </div>

      {/* 검색 및 필터 */}
      <Card>
        <CardContent className="pt-6">
          <div className="flex items-center space-x-4">
            <div className="relative flex-1 max-w-sm">
              <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
              <Input
                type="text"
                placeholder="회사명, 코드, 사업자번호, 대표자명으로 검색..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-10"
              />
            </div>
            <div className="text-sm text-gray-500">
              총 {filteredCompanies.length}개 회사
            </div>
          </div>
        </CardContent>
      </Card>

      {/* 회사 목록 */}
      {filteredCompanies.length === 0 ? (
        <Card>
          <CardContent className="pt-6">
            <div className="text-center py-12">
              <Building2 className="w-16 h-16 text-gray-300 mx-auto mb-4" />
              <h3 className="text-lg font-semibold text-gray-900 mb-2">
                {searchTerm ? '검색 결과가 없습니다' : '등록된 회사가 없습니다'}
              </h3>
              <p className="text-gray-600 mb-4">
                {searchTerm 
                  ? '다른 검색어로 시도해보세요' 
                  : '새로운 회사를 등록해보세요'
                }
              </p>
              {!searchTerm && (
                <Button onClick={() => navigate('/companies/new')}>
                  <Plus className="w-4 h-4 mr-2" />
                  회사 등록
                </Button>
              )}
            </div>
          </CardContent>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredCompanies.map((company) => (
            <Card key={company.id} className="hover:shadow-lg transition-shadow cursor-pointer">
              <CardHeader>
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <CardTitle className="text-lg">{company.name}</CardTitle>
                    <CardDescription>{company.nameEn}</CardDescription>
                  </div>
                  {getStatusBadge(company.status)}
                </div>
              </CardHeader>
              <CardContent className="space-y-3">
                <div className="space-y-2 text-sm">
                  <div className="flex justify-between">
                    <span className="text-gray-500">회사 코드:</span>
                    <span className="font-medium">{company.companyCode}</span>
                  </div>
                  {company.businessNumber && (
                    <div className="flex justify-between">
                      <span className="text-gray-500">사업자번호:</span>
                      <span className="font-medium">{company.businessNumber}</span>
                    </div>
                  )}
                  {company.ceoName && (
                    <div className="flex justify-between">
                      <span className="text-gray-500">대표자:</span>
                      <span className="font-medium">{company.ceoName}</span>
                    </div>
                  )}
                  {company.businessType && (
                    <div className="flex justify-between">
                      <span className="text-gray-500">업종:</span>
                      <span className="font-medium">{company.businessType}</span>
                    </div>
                  )}
                  {company.employeeCount && (
                    <div className="flex justify-between">
                      <span className="text-gray-500">직원 수:</span>
                      <span className="font-medium">{company.employeeCount.toLocaleString()}명</span>
                    </div>
                  )}
                </div>
                
                <div className="pt-3 border-t">
                  <Button 
                    variant="outline" 
                    size="sm" 
                    className="w-full"
                    onClick={() => navigate(`/companies/${company.id}`)}
                  >
                    <Eye className="w-4 h-4 mr-2" />
                    상세보기
                  </Button>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  )
}

export { CompanyList }



