import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { Bell, Check, X, ArrowLeft, Trash2 } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { notificationService, type Notification } from '@/services/notificationService'
import { useNotifications } from '@/contexts/NotificationContext'
import { LoadingSpinner } from '@/components/common/LoadingSpinner'

/**
 * 모든 알림 조회 페이지
 * 최근 2주 이내의 모든 알림을 조회하고 관리할 수 있습니다
 */
function NotificationListPage() {
  const navigate = useNavigate()
  const { unreadCount, refreshNotifications, markAsRead, markAllAsRead } = useNotifications()
  const [notifications, setNotifications] = useState<Notification[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  /**
   * 알림 목록 로드
   */
  const loadNotifications = async () => {
    try {
      setLoading(true)
      setError(null)
      const allNotifications = await notificationService.getAllNotifications()
      setNotifications(allNotifications)
    } catch (error) {
      console.error('알림 목록 로드 실패:', error)
      setError('알림 목록을 불러오는 중 오류가 발생했습니다.')
    } finally {
      setLoading(false)
    }
  }

  /**
   * 알림 읽음 처리
   */
  const handleMarkAsRead = async (id: number) => {
    try {
      await markAsRead(id)
      // 로컬 상태 업데이트
      setNotifications(prev => 
        prev.map(notification => 
          notification.id === id 
            ? { ...notification, isRead: true }
            : notification
        )
      )
    } catch (error) {
      console.error('알림 읽음 처리 실패:', error)
    }
  }

  /**
   * 알림 삭제
   */
  const deleteNotification = async (id: number) => {
    try {
      await notificationService.deleteNotification(id)
      // 로컬 상태에서 제거
      setNotifications(prev => prev.filter(notification => notification.id !== id))
    } catch (error) {
      console.error('알림 삭제 실패:', error)
    }
  }

  /**
   * 모든 알림 읽음 처리
   */
  const handleMarkAllAsRead = async () => {
    try {
      await markAllAsRead()
      // 로컬 상태 업데이트
      setNotifications(prev => 
        prev.map(notification => ({ ...notification, isRead: true }))
      )
    } catch (error) {
      console.error('모든 알림 읽음 처리 실패:', error)
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

  /**
   * 알림 타입별 아이콘
   */
  const getTypeIcon = (type: Notification['type']) => {
    switch (type) {
      case 'INFO':
        return 'ℹ️'
      case 'WARNING':
        return '⚠️'
      case 'ERROR':
        return '❌'
      case 'SUCCESS':
        return '✅'
      default:
        return '📢'
    }
  }

  useEffect(() => {
    loadNotifications()
  }, [])

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <LoadingSpinner />
      </div>
    )
  }

  if (error) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="flex items-center justify-between mb-6">
          <Button
            variant="ghost"
            onClick={() => navigate(-1)}
            className="flex items-center gap-2"
          >
            <ArrowLeft className="h-4 w-4" />
            뒤로가기
          </Button>
        </div>
        <Card>
          <CardContent className="p-6 text-center">
            <p className="text-red-600">{error}</p>
            <Button onClick={loadNotifications} className="mt-4">
              다시 시도
            </Button>
          </CardContent>
        </Card>
      </div>
    )
  }

  // 전역 상태의 unreadCount 사용 (실시간 동기화)

  return (
    <div className="container mx-auto px-4 py-8">
      {/* 헤더 */}
      <div className="flex items-center justify-between mb-6">
        <div className="flex items-center gap-4">
          <Button
            variant="ghost"
            onClick={() => navigate(-1)}
            className="flex items-center gap-2"
          >
            <ArrowLeft className="h-4 w-4" />
            뒤로가기
          </Button>
          <div>
            <h1 className="text-2xl font-bold flex items-center gap-2">
              <Bell className="h-6 w-6" />
              모든 알림
            </h1>
            <p className="text-muted-foreground">
              최근 2주 이내의 모든 알림 ({notifications.length}개)
              {unreadCount > 0 && (
                <span className="ml-2 text-primary font-medium">
                  (읽지 않음 {unreadCount}개)
                </span>
              )}
            </p>
          </div>
        </div>
        
        <div className="flex items-center gap-2">
          {unreadCount > 0 && (
            <Button onClick={handleMarkAllAsRead} variant="outline" className="flex items-center gap-2">
              <Check className="h-4 w-4" />
              모두 읽음 처리
            </Button>
          )}
          <Button 
            onClick={() => navigate('/notifications/test')} 
            variant="outline" 
            className="flex items-center gap-2"
          >
            <Bell className="h-4 w-4" />
            알림 테스트
          </Button>
        </div>
      </div>

      {/* 알림 목록 */}
      {notifications.length === 0 ? (
        <Card>
          <CardContent className="p-12 text-center">
            <Bell className="h-16 w-16 mx-auto mb-4 opacity-50" />
            <h3 className="text-lg font-semibold mb-2">알림이 없습니다</h3>
            <p className="text-muted-foreground">
              최근 2주 이내에 받은 알림이 없습니다.
            </p>
          </CardContent>
        </Card>
      ) : (
        <div className="space-y-4">
          {notifications.map((notification, index) => (
            <Card 
              key={notification.id} 
              className={`transition-all duration-200 hover:shadow-md ${
                !notification.isRead ? 'border-l-4 border-l-primary bg-muted/30' : ''
              }`}
            >
              <CardContent className="p-6">
                <div className="flex items-start justify-between">
                  <div className="flex items-start gap-4 flex-1">
                    {/* 읽음 상태 표시 */}
                    <div className={`w-3 h-3 rounded-full mt-2 flex-shrink-0 ${
                      !notification.isRead ? 'bg-primary' : 'bg-muted'
                    }`} />
                    
                    {/* 알림 타입 아이콘 */}
                    <div className="text-2xl flex-shrink-0">
                      {getTypeIcon(notification.type)}
                    </div>
                    
                    {/* 알림 내용 */}
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center gap-2 mb-2">
                        <h3 className="text-lg font-semibold truncate">
                          {notification.title}
                        </h3>
                        <Badge 
                          variant="secondary" 
                          className={`text-xs ${getTypeColor(notification.type)}`}
                        >
                          {notification.type}
                        </Badge>
                        {!notification.isRead && (
                          <Badge variant="default" className="text-xs">
                            새 알림
                          </Badge>
                        )}
                      </div>
                      
                      <p className="text-muted-foreground mb-3 leading-relaxed">
                        {notification.message}
                      </p>
                      
                      <div className="flex items-center gap-4 text-sm text-muted-foreground">
                        <span>{formatTime(notification.createdAt)}</span>
                        {notification.isRead && notification.readAt && (
                          <span>읽음: {formatTime(notification.readAt)}</span>
                        )}
                      </div>
                    </div>
                  </div>
                  
                  {/* 액션 버튼들 */}
                  <div className="flex items-center gap-2 ml-4">
                    {!notification.isRead && (
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => handleMarkAsRead(notification.id)}
                        className="flex items-center gap-1"
                      >
                        <Check className="h-3 w-3" />
                        읽음
                      </Button>
                    )}
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => deleteNotification(notification.id)}
                      className="flex items-center gap-1 text-red-600 hover:text-red-700"
                    >
                      <Trash2 className="h-3 w-3" />
                      삭제
                    </Button>
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  )
}

export { NotificationListPage }
