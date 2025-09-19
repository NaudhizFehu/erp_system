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
  Calculator, 
  TrendingUp,
  Filter,
  MoreHorizontal,
  DollarSign
} from 'lucide-react'

/**
 * 계정 목록 페이지
 * 회계 계정 정보 조회 및 관리를 담당하는 페이지
 */
function AccountList() {
  const [searchTerm, setSearchTerm] = useState('')

  const accounts = [
    {
      id: 1,
      code: '1000',
      name: '현금',
      type: '자산',
      category: '유동자산',
      balance: 50000000,
      status: 'active'
    },
    {
      id: 2,
      code: '1100',
      name: '예금',
      type: '자산',
      category: '유동자산',
      balance: 200000000,
      status: 'active'
    },
    {
      id: 3,
      code: '1200',
      name: '매출채권',
      type: '자산',
      category: '유동자산',
      balance: 150000000,
      status: 'active'
    },
    {
      id: 4,
      code: '2000',
      name: '매입채무',
      type: '부채',
      category: '유동부채',
      balance: 80000000,
      status: 'active'
    },
    {
      id: 5,
      code: '3000',
      name: '자본금',
      type: '자본',
      category: '자본',
      balance: 500000000,
      status: 'active'
    }
  ]

  const stats = [
    {
      title: '총 계정 수',
      value: '156',
      change: '+3.2%',
      trend: 'up',
      icon: Calculator
    },
    {
      title: '총 자산',
      value: '₩2,450,000,000',
      change: '+8.5%',
      trend: 'up',
      icon: DollarSign
    },
    {
      title: '활성 계정',
      value: '142',
      change: '+2.1%',
      trend: 'up',
      icon: TrendingUp
    }
  ]

  const getTypeBadge = (type: string) => {
    switch (type) {
      case '자산':
        return <Badge variant="default" className="bg-blue-100 text-blue-800">자산</Badge>
      case '부채':
        return <Badge variant="destructive" className="bg-red-100 text-red-800">부채</Badge>
      case '자본':
        return <Badge variant="secondary" className="bg-green-100 text-green-800">자본</Badge>
      case '수익':
        return <Badge variant="outline" className="bg-purple-100 text-purple-800">수익</Badge>
      case '비용':
        return <Badge variant="outline" className="bg-orange-100 text-orange-800">비용</Badge>
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
          <h1 className="text-3xl font-bold text-gray-900">계정 관리</h1>
          <p className="text-gray-600 mt-1">회계 계정 정보를 조회하고 관리하세요</p>
        </div>
        <div className="flex space-x-2">
          <Button variant="outline">
            <Filter className="h-4 w-4 mr-2" />
            필터
          </Button>
          <Button>
            <Plus className="h-4 w-4 mr-2" />
            계정 추가
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
          <CardTitle>계정 목록</CardTitle>
          <CardDescription>
            회계 계정 정보를 검색하고 관리할 수 있습니다
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="flex items-center space-x-4 mb-6">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
              <Input
                placeholder="계정코드, 계정명으로 검색..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-10"
              />
            </div>
          </div>

          {/* 계정 테이블 */}
          <div className="rounded-md border">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>계정코드</TableHead>
                  <TableHead>계정명</TableHead>
                  <TableHead>계정유형</TableHead>
                  <TableHead>계정분류</TableHead>
                  <TableHead>잔액</TableHead>
                  <TableHead>상태</TableHead>
                  <TableHead className="w-[50px]"></TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {accounts.map((account) => (
                  <TableRow key={account.id}>
                    <TableCell className="font-medium">{account.code}</TableCell>
                    <TableCell>{account.name}</TableCell>
                    <TableCell>{getTypeBadge(account.type)}</TableCell>
                    <TableCell>{account.category}</TableCell>
                    <TableCell className="text-right">{formatCurrency(account.balance)}</TableCell>
                    <TableCell>
                      <Badge variant={account.status === 'active' ? 'default' : 'secondary'}>
                        {account.status === 'active' ? '활성' : '비활성'}
                      </Badge>
                    </TableCell>
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

export { AccountList }