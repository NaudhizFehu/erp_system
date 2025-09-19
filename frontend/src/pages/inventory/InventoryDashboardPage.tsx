import React from 'react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Progress } from '@/components/ui/progress'
import { Separator } from '@/components/ui/separator'
import { 
  Package, 
  TrendingUp, 
  AlertTriangle, 
  CheckCircle,
  BarChart3,
  Activity
} from 'lucide-react'

interface InventoryDashboardPageProps {
  companyId: number
}

/**
 * 재고 관리 대시보드 페이지
 * 재고 현황 및 통계를 보여주는 대시보드
 */
function InventoryDashboardPage({ companyId }: InventoryDashboardPageProps) {
  const inventoryStats = [
    {
      title: '총 상품 수',
      value: '1,250',
      change: '+5.2%',
      trend: 'up',
      icon: Package
    },
    {
      title: '재고 가치',
      value: '₩2,450,000,000',
      change: '+8.1%',
      trend: 'up',
      icon: TrendingUp
    },
    {
      title: '재고 부족 상품',
      value: '23',
      change: '-12.5%',
      trend: 'down',
      icon: AlertTriangle
    },
    {
      title: '재고 회전율',
      value: '4.2회',
      change: '+2.3%',
      trend: 'up',
      icon: BarChart3
    }
  ]

  const lowStockProducts = [
    { name: '상품 A-001', current: 5, min: 10, category: '전자제품' },
    { name: '상품 B-002', current: 8, min: 15, category: '의류' },
    { name: '상품 C-003', current: 3, min: 20, category: '가구' },
    { name: '상품 D-004', current: 12, min: 25, category: '도서' }
  ]

  const topCategories = [
    { name: '전자제품', count: 450, percentage: 36 },
    { name: '의류', count: 320, percentage: 25.6 },
    { name: '가구', count: 280, percentage: 22.4 },
    { name: '도서', count: 200, percentage: 16 }
  ]

  return (
    <div className="p-6 space-y-6">
      {/* 헤더 */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">재고 관리</h1>
          <p className="text-gray-600 mt-1">재고 현황 및 통계를 확인하세요</p>
        </div>
        <div className="flex space-x-2">
          <Button variant="outline">상품 관리</Button>
          <Button>재고 조정</Button>
        </div>
      </div>

      {/* 통계 카드 */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {inventoryStats.map((stat, index) => (
          <Card key={index}>
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-gray-600">{stat.title}</p>
                  <p className="text-2xl font-bold text-gray-900">{stat.value}</p>
                  <div className="flex items-center mt-1">
                    <Badge 
                      variant={stat.trend === 'up' ? 'default' : 'destructive'}
                      className="text-xs"
                    >
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

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* 재고 부족 상품 */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center space-x-2">
              <AlertTriangle className="h-5 w-5 text-orange-500" />
              <span>재고 부족 상품</span>
            </CardTitle>
            <CardDescription>
              최소 재고량 이하로 떨어진 상품들입니다
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {lowStockProducts.map((product, index) => (
                <div key={index} className="flex items-center justify-between p-3 bg-orange-50 rounded-lg">
                  <div>
                    <p className="font-medium text-sm">{product.name}</p>
                    <p className="text-xs text-gray-500">{product.category}</p>
                  </div>
                  <div className="text-right">
                    <p className="text-sm font-medium text-orange-600">
                      {product.current} / {product.min}
                    </p>
                    <Progress 
                      value={(product.current / product.min) * 100} 
                      className="w-20 h-2 mt-1"
                    />
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        {/* 카테고리별 분포 */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center space-x-2">
              <BarChart3 className="h-5 w-5 text-blue-500" />
              <span>카테고리별 분포</span>
            </CardTitle>
            <CardDescription>
              상품 카테고리별 재고 분포 현황입니다
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {topCategories.map((category, index) => (
                <div key={index}>
                  <div className="flex items-center justify-between mb-2">
                    <span className="text-sm font-medium">{category.name}</span>
                    <span className="text-sm text-gray-500">{category.count}개</span>
                  </div>
                  <Progress value={category.percentage} className="h-2" />
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>

      {/* 최근 재고 변동 */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center space-x-2">
            <Activity className="h-5 w-5 text-green-500" />
            <span>최근 재고 변동</span>
          </CardTitle>
          <CardDescription>
            최근 7일간의 재고 변동 내역입니다
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-3">
            <div className="flex items-center justify-between p-3 bg-green-50 rounded-lg">
              <div className="flex items-center space-x-3">
                <CheckCircle className="h-4 w-4 text-green-600" />
                <div>
                  <p className="text-sm font-medium">상품 A-001 입고</p>
                  <p className="text-xs text-gray-500">+100개</p>
                </div>
              </div>
              <span className="text-xs text-gray-400">2시간 전</span>
            </div>
            
            <div className="flex items-center justify-between p-3 bg-blue-50 rounded-lg">
              <div className="flex items-center space-x-3">
                <Package className="h-4 w-4 text-blue-600" />
                <div>
                  <p className="text-sm font-medium">상품 B-002 출고</p>
                  <p className="text-xs text-gray-500">-25개</p>
                </div>
              </div>
              <span className="text-xs text-gray-400">4시간 전</span>
            </div>
            
            <div className="flex items-center justify-between p-3 bg-orange-50 rounded-lg">
              <div className="flex items-center space-x-3">
                <AlertTriangle className="h-4 w-4 text-orange-600" />
                <div>
                  <p className="text-sm font-medium">상품 C-003 재고 조정</p>
                  <p className="text-xs text-gray-500">-5개 (손실)</p>
                </div>
              </div>
              <span className="text-xs text-gray-400">6시간 전</span>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}

export { InventoryDashboardPage }