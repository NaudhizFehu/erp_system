import { Link, useLocation } from 'react-router-dom'
import { 
  LayoutDashboard, 
  Users, 
  Package, 
  ShoppingCart, 
  Calculator,
  Building2
} from 'lucide-react'
import { cn } from '@/lib/utils'

/**
 * 사이드바 컴포넌트
 * 네비게이션 메뉴를 제공합니다
 */
function Sidebar() {
  const location = useLocation()

  const menuItems = [
    {
      title: '대시보드',
      href: '/',
      icon: LayoutDashboard,
    },
    {
      title: '인사관리',
      icon: Users,
      subItems: [
        { title: '직원 관리', href: '/hr/employees' },
        { title: '부서 관리', href: '/hr/departments' },
        { title: '직급 관리', href: '/hr/positions' },
      ],
    },
    {
      title: '재고관리',
      icon: Package,
      subItems: [
        { title: '제품 관리', href: '/inventory/products' },
        { title: '카테고리 관리', href: '/inventory/categories' },
        { title: '재고 현황', href: '/inventory/stock' },
      ],
    },
    {
      title: '영업관리',
      icon: ShoppingCart,
      subItems: [
        { title: '고객 관리', href: '/sales/customers' },
        { title: '주문 관리', href: '/sales/orders' },
        { title: '견적 관리', href: '/sales/quotes' },
      ],
    },
    {
      title: '회계관리',
      icon: Calculator,
      subItems: [
        { title: '계정과목', href: '/accounting/accounts' },
        { title: '전표 관리', href: '/accounting/vouchers' },
        { title: '재무제표', href: '/accounting/statements' },
      ],
    },
    {
      title: '회사관리',
      icon: Building2,
      subItems: [
        { title: '회사 목록', href: '/companies' },
      ],
    },
  ]

  return (
    <div className="fixed inset-y-0 left-0 z-50 w-64 bg-card border-r">
      {/* 로고 영역 */}
      <div className="flex h-16 items-center px-6 border-b">
        <Building2 className="h-8 w-8 text-primary" />
        <h1 className="ml-3 text-xl font-bold text-foreground">ERP 시스템</h1>
      </div>

      {/* 네비게이션 메뉴 */}
      <nav className="flex-1 space-y-1 p-4">
        {menuItems.map((item) => (
          <div key={item.title}>
            {item.href ? (
              // 단일 메뉴 아이템
              <Link
                to={item.href}
                className={cn(
                  'flex items-center px-3 py-2 text-sm font-medium rounded-md transition-colors',
                  location.pathname === item.href
                    ? 'bg-primary text-primary-foreground'
                    : 'text-muted-foreground hover:bg-accent hover:text-accent-foreground'
                )}
              >
                <item.icon className="mr-3 h-5 w-5" />
                {item.title}
              </Link>
            ) : (
              // 하위 메뉴가 있는 아이템
              <div>
                <div className="flex items-center px-3 py-2 text-sm font-medium text-foreground">
                  <item.icon className="mr-3 h-5 w-5" />
                  {item.title}
                </div>
                <div className="ml-6 space-y-1">
                  {item.subItems?.map((subItem) => (
                    <Link
                      key={subItem.href}
                      to={subItem.href}
                      className={cn(
                        'block px-3 py-2 text-sm rounded-md transition-colors',
                        location.pathname === subItem.href
                          ? 'bg-primary text-primary-foreground'
                          : 'text-muted-foreground hover:bg-accent hover:text-accent-foreground'
                      )}
                    >
                      {subItem.title}
                    </Link>
                  ))}
                </div>
              </div>
            )}
          </div>
        ))}
      </nav>
    </div>
  )
}

export { Sidebar }


