import { User, LogOut, Settings } from 'lucide-react'
import { useNavigate } from 'react-router-dom'

import { NotificationDropdown } from '@/components/notification/NotificationDropdown'
import { GlobalSearch } from '@/components/search/GlobalSearch'
import { Button } from '@/components/ui/button'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { useAuth } from '@/contexts/AuthContext'

/**
 * 헤더 컴포넌트
 * 상단 네비게이션 바를 제공합니다
 */
function Header() {
  const { employee, logout } = useAuth()
  const navigate = useNavigate()
  return (
    <header className="sticky top-0 z-40 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
      <div className="flex h-16 items-center justify-between px-6">
        {/* 검색 영역 */}
        <div className="flex flex-1 items-center space-x-4">
          <GlobalSearch />
        </div>

        {/* 우측 액션 영역 */}
        <div className="flex flex-shrink-0 items-center space-x-4">
          {/* 알림 (로그인된 경우에만 표시) */}
          {employee && <NotificationDropdown />}

          {/* 사용자 프로필 드롭다운 */}
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="ghost" className="flex items-center space-x-2">
                <User className="h-5 w-5" />
                <span className="text-sm font-medium">
                  {employee?.name || '사용자'}
                </span>
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end" className="w-56">
              <DropdownMenuLabel>
                <div className="flex flex-col space-y-1">
                  <p className="text-sm font-medium leading-none">
                    {employee?.name}
                  </p>
                  <p className="text-xs leading-none text-muted-foreground">
                    {employee?.email}
                  </p>
                </div>
              </DropdownMenuLabel>
              <DropdownMenuSeparator />
              <DropdownMenuItem onClick={() => navigate('/profile')}>
                <Settings className="mr-2 h-4 w-4" />
                <span>사용자 정보 변경</span>
              </DropdownMenuItem>
              <DropdownMenuSeparator />
              <DropdownMenuItem onClick={logout}>
                <LogOut className="mr-2 h-4 w-4" />
                <span>로그아웃</span>
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
      </div>
    </header>
  )
}

export { Header }
