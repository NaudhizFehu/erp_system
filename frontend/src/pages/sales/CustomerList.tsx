import React, { useState } from 'react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Badge } from '@/components/ui/badge'
import { 
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { 
  Search, 
  Plus, 
  Users, 
  TrendingUp,
  Filter,
  MoreHorizontal
} from 'lucide-react'

/**
 * 고객 목록 페이지
 * 고객 정보 조회 및 관리를 담당하는 페이지
 */
function CustomerList() {
  const [searchTerm, setSearchTerm] = useState('')

  const customers = [
    {
      id: 1,
      name: '김철수',
      company: 'ABC 기업',
      email: 'kim@abc.com',
      phone: '010-1234-5678',
      status: 'active',
      totalOrders: 15,
      totalAmount: 2500000,
      lastOrder: '2024-01-15'
    },
    {
      id: 2,
      name: '이영희',
      company: 'XYZ 회사',
      email: 'lee@xyz.com',
      phone: '010-2345-6789',
      status: 'active',
      totalOrders: 8,
      totalAmount: 1800000,
      lastOrder: '2024-01-10'
    },
    {
      id: 3,
      name: '박민수',
      company: 'DEF 그룹',
      email: 'park@def.com',
      phone: '010-3456-7890',
      status: 'inactive',
      totalOrders: 3,
      totalAmount: 750000,
      lastOrder: '2023-12-20'
    }
  ]

  const stats = [
    {
      title: '총 고객 수',
      value: '850',
      change: '+12.5%',
      trend: 'up',
      icon: Users
    },
    {
      title: '활성 고객',
      value: '720',
      change: '+8.2%',
      trend: 'up',
      icon: TrendingUp
    },
    {
      title: '신규 고객',
      value: '45',
      change: '+15.3%',
      trend: 'up',
      icon: Plus
    }
  ]

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'active':
        return <Badge variant="default" className="bg-green-100 text-green-800">활성</Badge>
      case 'inactive':
        return <Badge variant="secondary">비활성</Badge>
      default:
        return <Badge variant="outline">알 수 없음</Badge>
    }
  }

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('ko-KR', {
      style: 'currency',
      currency: 'KRW'
    }).format(amount)
  }

  return (
    <div className="p-6 space-y-6">
      {/* 헤더 */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">고객 관리</h1>
          <p className="text-gray-600 mt-1">고객 정보를 조회하고 관리하세요</p>
        </div>
        <div className="flex space-x-2">
          <Button variant="outline">
            <Filter className="h-4 w-4 mr-2" />
            필터
          </Button>
          <Button>
            <Plus className="h-4 w-4 mr-2" />
            고객 추가
          </Button>
        </div>
      </div>

      {/* 통계 카드 */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {stats.map((stat, index) => (
          <Card key={index}>
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-gray-600">{stat.title}</p>
                  <p className="text-2xl font-bold text-gray-900">{stat.value}</p>
                  <div className="flex items-center mt-1">
                    <Badge variant="default" className="text-xs">
                      {stat.change}
                    </Badge>
                  </div>
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
          <CardTitle>고객 목록</CardTitle>
          <CardDescription>
            고객 정보를 검색하고 관리할 수 있습니다
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="flex items-center space-x-4 mb-6">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
              <Input
                placeholder="고객명, 회사명, 이메일로 검색..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-10"
              />
            </div>
          </div>

          {/* 고객 테이블 */}
          <div className="rounded-md border">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>고객명</TableHead>
                  <TableHead>회사</TableHead>
                  <TableHead>연락처</TableHead>
                  <TableHead>상태</TableHead>
                  <TableHead>총 주문</TableHead>
                  <TableHead>총 금액</TableHead>
                  <TableHead>최근 주문</TableHead>
                  <TableHead className="w-[50px]"></TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {customers.map((customer) => (
                  <TableRow key={customer.id}>
                    <TableCell className="font-medium">{customer.name}</TableCell>
                    <TableCell>{customer.company}</TableCell>
                    <TableCell>
                      <div>
                        <div className="text-sm">{customer.email}</div>
                        <div className="text-xs text-gray-500">{customer.phone}</div>
                      </div>
                    </TableCell>
                    <TableCell>{getStatusBadge(customer.status)}</TableCell>
                    <TableCell>{customer.totalOrders}건</TableCell>
                    <TableCell>{formatCurrency(customer.totalAmount)}</TableCell>
                    <TableCell>{customer.lastOrder}</TableCell>
                    <TableCell>
                      <Button variant="ghost" size="sm">
                        <MoreHorizontal className="h-4 w-4" />
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}

export { CustomerList }