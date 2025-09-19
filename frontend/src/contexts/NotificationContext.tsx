import { createContext, useContext, useEffect, useState, ReactNode } from 'react'
import { notificationService } from '@/services/notificationService'
import { useAuth } from '@/contexts/AuthContext'

/**
 * 알림 전역 상태 관리 Context
 */
interface NotificationContextType {
  unreadCount: number
  refreshNotifications: () => Promise<void>
  markAsRead: (notificationId: number) => Promise<void>
  markAllAsRead: () => Promise<void>
  isPolling: boolean
  startPolling: () => void
  stopPolling: () => void
}

const NotificationContext = createContext<NotificationContextType | undefined>(undefined)

interface NotificationProviderProps {
  children: ReactNode
}

/**
 * 알림 상태 관리 Provider
 */
function NotificationProvider({ children }: NotificationProviderProps) {
  const { isAuthenticated } = useAuth()
  const [unreadCount, setUnreadCount] = useState(0)
  const [isPolling, setIsPolling] = useState(false)
  const [pollingInterval, setPollingInterval] = useState<NodeJS.Timeout | null>(null)

  /**
   * 알림 개수 갱신
   */
  const refreshNotifications = async () => {
    // 로그인하지 않은 경우 알림 갱신하지 않음
    if (!isAuthenticated) {
      console.log('로그인하지 않음, 알림 갱신 건너뜀')
      setUnreadCount(0)
      return
    }

    try {
      console.log('알림 개수 갱신 시작')
      const count = await notificationService.getUnreadCount()
      console.log('알림 개수 갱신 완료:', count)
      setUnreadCount(count)
    } catch (error) {
      console.error('알림 개수 갱신 실패:', error)
      // 에러가 발생해도 기존 개수 유지
    }
  }

  /**
   * 알림 읽음 처리
   */
  const markAsRead = async (notificationId: number) => {
    if (!isAuthenticated) {
      console.log('로그인하지 않음, 알림 읽음 처리 건너뜀')
      return
    }

    try {
      await notificationService.markAsRead(notificationId)
      // 읽음 처리 후 개수 갱신
      await refreshNotifications()
    } catch (error) {
      console.error('알림 읽음 처리 실패:', error)
    }
  }

  /**
   * 모든 알림 읽음 처리
   */
  const markAllAsRead = async () => {
    if (!isAuthenticated) {
      console.log('로그인하지 않음, 모든 알림 읽음 처리 건너뜀')
      return
    }

    try {
      await notificationService.markAllAsRead()
      // 모든 알림 읽음 처리 후 개수 갱신
      await refreshNotifications()
    } catch (error) {
      console.error('모든 알림 읽음 처리 실패:', error)
    }
  }

  /**
   * 폴링 시작
   */
  const startPolling = () => {
    if (isPolling || !isAuthenticated) return
    
    console.log('알림 폴링 시작')
    setIsPolling(true)
    
    // 즉시 한 번 실행
    refreshNotifications()
    
    // 30초마다 갱신
    const interval = setInterval(() => {
      refreshNotifications()
    }, 30000) // 30초
    
    setPollingInterval(interval)
  }

  /**
   * 폴링 중지
   */
  const stopPolling = () => {
    if (pollingInterval) {
      console.log('알림 폴링 중지')
      clearInterval(pollingInterval)
      setPollingInterval(null)
      setIsPolling(false)
    }
  }

  /**
   * 로그인 상태 변경 시 폴링 제어
   */
  useEffect(() => {
    if (isAuthenticated) {
      console.log('로그인됨, 알림 폴링 시작')
      startPolling()
    } else {
      console.log('로그아웃됨, 알림 폴링 중지')
      stopPolling()
      setUnreadCount(0) // 로그아웃 시 알림 개수 초기화
    }
    
    // 컴포넌트 언마운트 시 폴링 중지
    return () => {
      stopPolling()
    }
  }, [isAuthenticated])

  /**
   * 페이지 가시성 변경 시 폴링 제어
   */
  useEffect(() => {
    const handleVisibilityChange = () => {
      if (document.hidden) {
        // 페이지가 숨겨지면 폴링 중지
        stopPolling()
      } else if (isAuthenticated) {
        // 페이지가 보이고 로그인된 경우에만 폴링 재시작
        startPolling()
      }
    }

    document.addEventListener('visibilitychange', handleVisibilityChange)
    
    return () => {
      document.removeEventListener('visibilitychange', handleVisibilityChange)
    }
  }, [isAuthenticated])

  const value: NotificationContextType = {
    unreadCount,
    refreshNotifications,
    markAsRead,
    markAllAsRead,
    isPolling,
    startPolling,
    stopPolling
  }

  return (
    <NotificationContext.Provider value={value}>
      {children}
    </NotificationContext.Provider>
  )
}

/**
 * 알림 Context Hook
 */
function useNotifications() {
  const context = useContext(NotificationContext)
  if (context === undefined) {
    throw new Error('useNotifications must be used within a NotificationProvider')
  }
  return context
}

export { NotificationProvider, useNotifications }
