import React from 'react'
import { useNavigate } from 'react-router-dom'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { 
  Users, 
  Package, 
  ShoppingCart, 
  Calculator,
  TrendingUp,
  Activity,
  BarChart3,
  PieChart
} from 'lucide-react'

/**
 * 메인 대시보드 페이지
 * ERP 시스템의 전체 현황을 한눈에 볼 수 있는 대시보드
 */
function Dashboard() {
  const navigate = useNavigate()

  const dashboardCards = [
    {
      title: '인사관리',
      description: '직원 정보 및 조직 관리',
      icon: Users,
      path: '/hr/employees',
      color: 'bg-blue-500',
      stats: '직원 120명'
    },
    {
      title: '재고관리',
      description: '상품 및 재고 현황 관리',
      icon: Package,
      path: '/inventory',
      color: 'bg-green-500',
      stats: '상품 1,250개'
    },
    {
      title: '영업관리',
      description: '고객 및 주문 관리',
      icon: ShoppingCart,
      path: '/sales/customers',
      color: 'bg-purple-500',
      stats: '고객 850명'
    },
    {
      title: '회계관리',
      description: '재무 및 회계 관리',
      icon: Calculator,
      path: '/accounting/accounts',
      color: 'bg-orange-500',
      stats: '거래 2,340건'
    }
  ]

  const quickStats = [
    {
      title: '이번 달 매출',
      value: '₩2,450,000,000',
      change: '+12.5%',
      trend: 'up',
      icon: TrendingUp
    },
    {
      title: '신규 주문',
      value: '156건',
      change: '+8.2%',
      trend: 'up',
      icon: Activity
    },
    {
      title: '재고 회전율',
      value: '4.2회',
      change: '-2.1%',
      trend: 'down',
      icon: BarChart3
    },
    {
      title: '고객 만족도',
      value: '94.5%',
      change: '+1.3%',
      trend: 'up',
      icon: PieChart
    }
  ]

  return (
    <div className="p-6 space-y-6">
      {/* 헤더 */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">대시보드</h1>
          <p className="text-gray-600 mt-1">ERP 시스템 전체 현황을 확인하세요</p>
        </div>
        <Badge variant="outline" className="text-sm">
          실시간 업데이트
        </Badge>
      </div>

      {/* 빠른 통계 */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {quickStats.map((stat, index) => (
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

      {/* 모듈 카드 */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {dashboardCards.map((card, index) => (
          <Card key={index} className="hover:shadow-lg transition-shadow cursor-pointer">
            <CardHeader className="pb-3">
              <div className="flex items-center space-x-3">
                <div className={`p-2 rounded-lg ${card.color}`}>
                  <card.icon className="h-6 w-6 text-white" />
                </div>
                <div>
                  <CardTitle className="text-lg">{card.title}</CardTitle>
                  <CardDescription className="text-sm">
                    {card.description}
                  </CardDescription>
                </div>
              </div>
            </CardHeader>
            <CardContent className="pt-0">
              <div className="flex items-center justify-between">
                <Badge variant="secondary" className="text-xs">
                  {card.stats}
                </Badge>
                <Button 
                  size="sm" 
                  onClick={() => navigate(card.path)}
                  className="text-xs"
                >
                  바로가기
                </Button>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      {/* 최근 활동 */}
      <Card>
        <CardHeader>
          <CardTitle>최근 활동</CardTitle>
          <CardDescription>
            시스템에서 발생한 최근 활동들을 확인하세요
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            <div className="flex items-center space-x-4 p-3 bg-gray-50 rounded-lg">
              <div className="p-2 bg-blue-100 rounded-full">
                <Users className="h-4 w-4 text-blue-600" />
              </div>
              <div className="flex-1">
                <p className="text-sm font-medium">새로운 직원이 등록되었습니다</p>
                <p className="text-xs text-gray-500">김철수 - 개발팀</p>
              </div>
              <span className="text-xs text-gray-400">2시간 전</span>
            </div>
            
            <div className="flex items-center space-x-4 p-3 bg-gray-50 rounded-lg">
              <div className="p-2 bg-green-100 rounded-full">
                <Package className="h-4 w-4 text-green-600" />
              </div>
              <div className="flex-1">
                <p className="text-sm font-medium">재고가 업데이트되었습니다</p>
                <p className="text-xs text-gray-500">상품 A-001 재고: 150개</p>
              </div>
              <span className="text-xs text-gray-400">4시간 전</span>
            </div>
            
            <div className="flex items-center space-x-4 p-3 bg-gray-50 rounded-lg">
              <div className="p-2 bg-purple-100 rounded-full">
                <ShoppingCart className="h-4 w-4 text-purple-600" />
              </div>
              <div className="flex-1">
                <p className="text-sm font-medium">새로운 주문이 접수되었습니다</p>
                <p className="text-xs text-gray-500">주문번호: ORD-2024-001</p>
              </div>
              <span className="text-xs text-gray-400">6시간 전</span>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}

export { Dashboard }