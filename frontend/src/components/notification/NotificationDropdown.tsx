import { Bell, Check, X } from 'lucide-react'
import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuHeader,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { Badge } from '@/components/ui/badge'
import { notificationService, type Notification } from '@/services/notificationService'
import { useNotifications } from '@/contexts/NotificationContext'
import { useAuth } from '@/contexts/AuthContext'

/**
 * 알림 드롭다운 컴포넌트
 * 헤더에서 사용되는 알림 기능을 제공합니다
 */
function NotificationDropdown() {
  const navigate = useNavigate()
  const { isAuthenticated } = useAuth()
  const { unreadCount, refreshNotifications, markAsRead, markAllAsRead } = useNotifications()
  const [notifications, setNotifications] = useState<Notification[]>([])
  const [loading, setLoading] = useState(false)
  const [open, setOpen] = useState(false)

  // 로그인하지 않은 경우 컴포넌트 렌더링하지 않음
  if (!isAuthenticated) {
    return null
  }

  /**
   * 알림 목록 로드 (읽지 않은 알림만)
   */
  const loadNotifications = async () => {
    try {
      setLoading(true)
      const unreadNotifications = await notificationService.getUnreadNotifications()
      setNotifications(unreadNotifications)
      // unreadCount는 전역 상태에서 관리되므로 로컬에서 설정하지 않음
    } catch (error) {
      console.error('알림 목록 로드 실패:', error)
    } finally {
      setLoading(false)
    }
  }

  // 전역 상태의 unreadCount 사용 (폴링으로 자동 갱신됨)

  // 드롭다운이 열릴 때 알림 목록 로드
  const handleOpenChange = (isOpen: boolean) => {
    setOpen(isOpen)
    if (isOpen) {
      loadNotifications()
    }
  }

  /**
   * 알림 읽음 처리
   */
  const handleMarkAsRead = async (id: number) => {
    try {
      await markAsRead(id)
      // 읽은 알림을 바로 제거
      setNotifications(prev => prev.filter(notification => notification.id !== id))
    } catch (error) {
      console.error('알림 읽음 처리 실패:', error)
    }
  }

  /**
   * 모든 알림 읽음 처리
   */
  const handleMarkAllAsRead = async () => {
    try {
      await markAllAsRead()
      // 모든 알림을 제거
      setNotifications([])
    } catch (error) {
      console.error('모든 알림 읽음 처리 실패:', error)
    }
  }

  /**
   * 알림 삭제
   */
  const deleteNotification = async (id: number) => {
    try {
      await notificationService.deleteNotification(id)
      // 로컬 상태 업데이트
      setNotifications(prev => prev.filter(notification => notification.id !== id))
      // unreadCount는 전역 상태에서 자동으로 갱신됨
    } catch (error) {
      console.error('알림 삭제 실패:', error)
    }
  }

  /**
   * 시간 포맷팅
   */
  const formatTime = (timestamp: string) => {
    const now = new Date()
    const date = new Date(timestamp)
    const diff = now.getTime() - date.getTime()
    
    if (diff < 1000 * 60) {
      return '방금 전'
    } else if (diff < 1000 * 60 * 60) {
      return `${Math.floor(diff / (1000 * 60))}분 전`
    } else if (diff < 1000 * 60 * 60 * 24) {
      return `${Math.floor(diff / (1000 * 60 * 60))}시간 전`
    } else {
      return `${Math.floor(diff / (1000 * 60 * 60 * 24))}일 전`
    }
  }

  /**
   * 알림 타입별 색상
   */
  const getTypeColor = (type: Notification['type']) => {
    switch (type) {
      case 'INFO':
        return 'bg-blue-100 text-blue-800'
      case 'WARNING':
        return 'bg-yellow-100 text-yellow-800'
      case 'ERROR':
        return 'bg-red-100 text-red-800'
      case 'SUCCESS':
        return 'bg-green-100 text-green-800'
      default:
        return 'bg-gray-100 text-gray-800'
    }
  }

  return (
    <DropdownMenu onOpenChange={handleOpenChange}>
      <DropdownMenuTrigger asChild>
        <Button 
          variant="ghost" 
          size="sm"
          className="relative p-2 text-muted-foreground hover:text-foreground"
        >
          <Bell className="h-5 w-5" />
          {unreadCount > 0 && (
            <Badge 
              variant="destructive" 
              className="absolute -top-1 -right-1 h-5 w-5 flex items-center justify-center p-0 text-xs"
            >
              {unreadCount > 9 ? '9+' : unreadCount}
            </Badge>
          )}
        </Button>
      </DropdownMenuTrigger>
      
      <DropdownMenuContent align="end" className="w-80">
        <DropdownMenuHeader className="flex items-center justify-between">
          <div>
            <h3 className="font-semibold">알림</h3>
            <p className="text-xs text-muted-foreground">
              {unreadCount}개의 읽지 않은 알림
            </p>
          </div>
          {unreadCount > 0 && (
            <Button 
              variant="ghost" 
              size="sm" 
              onClick={handleMarkAllAsRead}
              className="text-xs"
            >
              모두 읽음
            </Button>
          )}
        </DropdownMenuHeader>
        
        <DropdownMenuSeparator />
        
        <div className="max-h-96 overflow-y-auto">
          {loading ? (
            <div className="p-4 text-center text-muted-foreground">
              <p>알림을 불러오는 중...</p>
            </div>
          ) : notifications.length === 0 ? (
            <div className="p-4 text-center text-muted-foreground">
              <Bell className="h-8 w-8 mx-auto mb-2 opacity-50" />
              <p>읽지 않은 알림이 없습니다</p>
            </div>
          ) : (
            notifications.map((notification) => (
              <DropdownMenuItem 
                key={notification.id}
                className={`p-4 ${!notification.isRead ? 'bg-muted/50' : ''}`}
                onSelect={(e) => e.preventDefault()}
                onClick={() => {
                  if (!notification.isRead) {
                    handleMarkAsRead(notification.id)
                  }
                  if (notification.actionUrl) {
                    // 실제로는 라우터를 사용하여 페이지 이동
                    console.log('Navigate to:', notification.actionUrl)
                  }
                }}
              >
                <div className="flex items-start space-x-3 w-full">
                  <div className={`w-2 h-2 rounded-full mt-2 flex-shrink-0 ${
                    !notification.isRead ? 'bg-primary' : 'bg-transparent'
                  }`} />
                  
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center justify-between mb-1">
                      <h4 className="text-sm font-medium truncate">
                        {notification.title}
                      </h4>
                      <div className="flex items-center space-x-1">
                        <Badge 
                          variant="secondary" 
                          className={`text-xs ${getTypeColor(notification.type)}`}
                        >
                          {notification.type}
                        </Badge>
                        <Button
                          variant="ghost"
                          size="sm"
                          className="h-6 w-6 p-0"
                          onClick={(e) => {
                            e.stopPropagation()
                            deleteNotification(notification.id)
                          }}
                        >
                          <X className="h-3 w-3" />
                        </Button>
                      </div>
                    </div>
                    
                    <p className="text-xs text-muted-foreground mb-2 line-clamp-2">
                      {notification.message}
                    </p>
                    
                    <div className="flex items-center justify-between">
                      <span className="text-xs text-muted-foreground">
                        {formatTime(notification.createdAt)}
                      </span>
                      {!notification.isRead && (
                        <Button
                          variant="ghost"
                          size="sm"
                          className="h-6 px-2 text-xs"
                          onClick={(e) => {
                            e.stopPropagation()
                            handleMarkAsRead(notification.id)
                          }}
                        >
                          <Check className="h-3 w-3 mr-1" />
                          읽음
                        </Button>
                      )}
                    </div>
                  </div>
                </div>
              </DropdownMenuItem>
            ))
          )}
        </div>
        
        <DropdownMenuSeparator />
        <DropdownMenuItem 
          className="p-2 cursor-pointer"
          onSelect={() => {
            console.log('모든 알림 보기 메뉴 아이템 선택됨')
            navigate('/notifications')
            console.log('페이지 이동 시도: /notifications')
          }}
        >
          <div className="w-full text-sm text-center">
            모든 알림 보기
          </div>
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  )
}

export { NotificationDropdown }



