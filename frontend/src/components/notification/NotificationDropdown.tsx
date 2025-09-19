import { Bell, Check, X } from 'lucide-react'
import { useState } from 'react'
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

/**
 * 알림 타입 정의
 */
interface Notification {
  id: string
  title: string
  message: string
  type: 'info' | 'warning' | 'error' | 'success'
  timestamp: Date
  isRead: boolean
  actionUrl?: string
}

/**
 * 알림 드롭다운 컴포넌트
 * 헤더에서 사용되는 알림 기능을 제공합니다
 */
function NotificationDropdown() {
  const [notifications, setNotifications] = useState<Notification[]>([
    {
      id: '1',
      title: '새로운 주문',
      message: '고객 "김철수"님이 새로운 주문을 생성했습니다.',
      type: 'info',
      timestamp: new Date(Date.now() - 1000 * 60 * 30), // 30분 전
      isRead: false,
      actionUrl: '/orders'
    },
    {
      id: '2',
      title: '재고 부족 알림',
      message: '상품 "노트북"의 재고가 부족합니다. (현재: 5개)',
      type: 'warning',
      timestamp: new Date(Date.now() - 1000 * 60 * 60 * 2), // 2시간 전
      isRead: false,
      actionUrl: '/inventory'
    },
    {
      id: '3',
      title: '시스템 업데이트',
      message: 'ERP 시스템이 성공적으로 업데이트되었습니다.',
      type: 'success',
      timestamp: new Date(Date.now() - 1000 * 60 * 60 * 24), // 1일 전
      isRead: true,
    },
    {
      id: '4',
      title: '결제 오류',
      message: '결제 처리 중 오류가 발생했습니다. 확인이 필요합니다.',
      type: 'error',
      timestamp: new Date(Date.now() - 1000 * 60 * 60 * 24 * 2), // 2일 전
      isRead: true,
      actionUrl: '/payments'
    }
  ])

  const unreadCount = notifications.filter(n => !n.isRead).length

  /**
   * 알림 읽음 처리
   */
  const markAsRead = (id: string) => {
    setNotifications(prev => 
      prev.map(notification => 
        notification.id === id 
          ? { ...notification, isRead: true }
          : notification
      )
    )
  }

  /**
   * 모든 알림 읽음 처리
   */
  const markAllAsRead = () => {
    setNotifications(prev => 
      prev.map(notification => ({ ...notification, isRead: true }))
    )
  }

  /**
   * 알림 삭제
   */
  const deleteNotification = (id: string) => {
    setNotifications(prev => prev.filter(notification => notification.id !== id))
  }

  /**
   * 시간 포맷팅
   */
  const formatTime = (timestamp: Date) => {
    const now = new Date()
    const diff = now.getTime() - timestamp.getTime()
    
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
      case 'info':
        return 'bg-blue-100 text-blue-800'
      case 'warning':
        return 'bg-yellow-100 text-yellow-800'
      case 'error':
        return 'bg-red-100 text-red-800'
      case 'success':
        return 'bg-green-100 text-green-800'
      default:
        return 'bg-gray-100 text-gray-800'
    }
  }

  return (
    <DropdownMenu>
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
              onClick={markAllAsRead}
              className="text-xs"
            >
              모두 읽음
            </Button>
          )}
        </DropdownMenuHeader>
        
        <DropdownMenuSeparator />
        
        <div className="max-h-96 overflow-y-auto">
          {notifications.length === 0 ? (
            <div className="p-4 text-center text-muted-foreground">
              <Bell className="h-8 w-8 mx-auto mb-2 opacity-50" />
              <p>알림이 없습니다</p>
            </div>
          ) : (
            notifications.map((notification) => (
              <DropdownMenuItem 
                key={notification.id}
                className={`p-4 ${!notification.isRead ? 'bg-muted/50' : ''}`}
                onSelect={() => {
                  if (!notification.isRead) {
                    markAsRead(notification.id)
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
                        {formatTime(notification.timestamp)}
                      </span>
                      {!notification.isRead && (
                        <Button
                          variant="ghost"
                          size="sm"
                          className="h-6 px-2 text-xs"
                          onClick={(e) => {
                            e.stopPropagation()
                            markAsRead(notification.id)
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
        
        {notifications.length > 0 && (
          <>
            <DropdownMenuSeparator />
            <div className="p-2">
              <Button 
                variant="ghost" 
                className="w-full text-sm"
                onClick={() => console.log('View all notifications')}
              >
                모든 알림 보기
              </Button>
            </div>
          </>
        )}
      </DropdownMenuContent>
    </DropdownMenu>
  )
}

export { NotificationDropdown }



