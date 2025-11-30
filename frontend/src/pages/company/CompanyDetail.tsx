import {
  ArrowLeft,
  Building2,
  MapPin,
  Phone,
  Mail,
  Globe,
  Users,
  Calendar,
} from 'lucide-react'
import React, { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'

import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
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
  corporationNumber?: string
  ceoName?: string
  businessType?: string
  businessItem?: string
  address?: string
  addressDetail?: string
  postalCode?: string
  phone?: string
  fax?: string
  email?: string
  website?: string
  establishedDate?: string
  status: string
  companyType?: string
  employeeCount?: number
  capitalAmount?: number
  description?: string
  logoUrl?: string
}

/**
 * 회사 상세보기 페이지
 */
function CompanyDetail() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [company, setCompany] = useState<Company | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (id) {
      fetchCompany(parseInt(id))
    }
  }, [id])

  const fetchCompany = async (companyId: number) => {
    try {
      setIsLoading(true)
      setError(null)

      const response = await api.get(`/companies/${companyId}`)

      if (response.data.success) {
        setCompany(response.data)
      } else {
        setError(response.data.message || '회사 정보를 불러올 수 없습니다')
      }
    } catch (error: any) {
      console.error('회사 정보 조회 오류:', error)
      setError('회사 정보를 불러오는 중 오류가 발생했습니다')
    } finally {
      setIsLoading(false)
    }
  }

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'ACTIVE':
        return (
          <Badge variant="default" className="bg-green-100 text-green-800">
            활성
          </Badge>
        )
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

  const getCompanyTypeBadge = (type?: string) => {
    switch (type) {
      case 'CORPORATION':
        return <Badge variant="outline">법인</Badge>
      case 'INDIVIDUAL':
        return <Badge variant="outline">개인사업자</Badge>
      case 'PARTNERSHIP':
        return <Badge variant="outline">합명회사</Badge>
      case 'LIMITED_PARTNERSHIP':
        return <Badge variant="outline">합자회사</Badge>
      case 'LIMITED_LIABILITY':
        return <Badge variant="outline">유한회사</Badge>
      default:
        return type ? <Badge variant="outline">{type}</Badge> : null
    }
  }

  if (isLoading) {
    return (
      <div className="flex min-h-96 items-center justify-center">
        <div className="text-center">
          <div className="mx-auto mb-4 h-12 w-12 animate-spin rounded-full border-b-2 border-blue-600" />
          <p className="text-gray-600">회사 정보를 불러오는 중...</p>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="flex min-h-96 items-center justify-center">
        <div className="text-center">
          <div className="mb-4 text-6xl text-red-500">⚠️</div>
          <h2 className="mb-2 text-xl font-semibold text-gray-900">
            오류 발생
          </h2>
          <p className="mb-4 text-gray-600">{error}</p>
          <Button onClick={() => navigate(-1)} variant="outline">
            <ArrowLeft className="mr-2 h-4 w-4" />
            뒤로 가기
          </Button>
        </div>
      </div>
    )
  }

  if (!company) {
    return (
      <div className="flex min-h-96 items-center justify-center">
        <div className="text-center">
          <div className="mb-4 text-6xl text-gray-400">🏢</div>
          <h2 className="mb-2 text-xl font-semibold text-gray-900">
            회사 정보 없음
          </h2>
          <p className="mb-4 text-gray-600">
            요청하신 회사 정보를 찾을 수 없습니다
          </p>
          <Button onClick={() => navigate(-1)} variant="outline">
            <ArrowLeft className="mr-2 h-4 w-4" />
            뒤로 가기
          </Button>
        </div>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      {/* 헤더 */}
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-4">
          <Button onClick={() => navigate(-1)} variant="outline" size="sm">
            <ArrowLeft className="mr-2 h-4 w-4" />
            뒤로 가기
          </Button>
          <div>
            <h1 className="text-2xl font-bold text-gray-900">{company.name}</h1>
            <p className="text-gray-600">{company.nameEn}</p>
          </div>
        </div>
        <div className="flex items-center space-x-2">
          {getStatusBadge(company.status)}
          {getCompanyTypeBadge(company.companyType)}
        </div>
      </div>

      {/* 회사 기본 정보 */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center">
            <Building2 className="mr-2 h-5 w-5" />
            기본 정보
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
            <div>
              <label className="text-sm font-medium text-gray-500">
                회사 코드
              </label>
              <p className="text-lg font-semibold">{company.companyCode}</p>
            </div>
            <div>
              <label className="text-sm font-medium text-gray-500">
                대표자
              </label>
              <p className="text-lg">{company.ceoName || '미입력'}</p>
            </div>
            <div>
              <label className="text-sm font-medium text-gray-500">
                사업자등록번호
              </label>
              <p className="text-lg">{company.businessNumber || '미입력'}</p>
            </div>
            <div>
              <label className="text-sm font-medium text-gray-500">
                법인등록번호
              </label>
              <p className="text-lg">{company.corporationNumber || '미입력'}</p>
            </div>
            <div>
              <label className="text-sm font-medium text-gray-500">업종</label>
              <p className="text-lg">{company.businessType || '미입력'}</p>
            </div>
            <div>
              <label className="text-sm font-medium text-gray-500">업태</label>
              <p className="text-lg">{company.businessItem || '미입력'}</p>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* 연락처 정보 */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center">
            <Phone className="mr-2 h-5 w-5" />
            연락처 정보
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
            <div className="flex items-center space-x-3">
              <MapPin className="h-5 w-5 text-gray-400" />
              <div>
                <p className="font-medium">{company.address}</p>
                {company.addressDetail && (
                  <p className="text-sm text-gray-600">
                    {company.addressDetail}
                  </p>
                )}
                {company.postalCode && (
                  <p className="text-sm text-gray-500">
                    우편번호: {company.postalCode}
                  </p>
                )}
              </div>
            </div>
            <div className="flex items-center space-x-3">
              <Phone className="h-5 w-5 text-gray-400" />
              <div>
                <p className="font-medium">{company.phone || '미입력'}</p>
                {company.fax && (
                  <p className="text-sm text-gray-600">팩스: {company.fax}</p>
                )}
              </div>
            </div>
            <div className="flex items-center space-x-3">
              <Mail className="h-5 w-5 text-gray-400" />
              <p className="font-medium">{company.email || '미입력'}</p>
            </div>
            <div className="flex items-center space-x-3">
              <Globe className="h-5 w-5 text-gray-400" />
              <p className="font-medium">{company.website || '미입력'}</p>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* 회사 상세 정보 */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center">
            <Users className="mr-2 h-5 w-5" />
            상세 정보
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
            <div className="flex items-center space-x-3">
              <Calendar className="h-5 w-5 text-gray-400" />
              <div>
                <label className="text-sm font-medium text-gray-500">
                  설립일
                </label>
                <p className="font-medium">
                  {company.establishedDate || '미입력'}
                </p>
              </div>
            </div>
            <div>
              <label className="text-sm font-medium text-gray-500">
                직원 수
              </label>
              <p className="text-lg font-semibold">
                {company.employeeCount?.toLocaleString() || '미입력'}명
              </p>
            </div>
            <div>
              <label className="text-sm font-medium text-gray-500">
                자본금
              </label>
              <p className="text-lg font-semibold">
                {company.capitalAmount
                  ? `${company.capitalAmount.toLocaleString()}원`
                  : '미입력'}
              </p>
            </div>
          </div>

          {company.description && (
            <div>
              <label className="text-sm font-medium text-gray-500">
                회사 설명
              </label>
              <p className="mt-2 whitespace-pre-wrap text-gray-700">
                {company.description}
              </p>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  )
}

export { CompanyDetail }
