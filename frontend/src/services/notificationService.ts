import api from './api'

/**
 * 알림 타입 정의
 */
export interface Notification {
  id: number
  title: string
  message: string
  type: 'INFO' | 'WARNING' | 'ERROR' | 'SUCCESS'
  isRead: boolean
  actionUrl?: string
  createdAt: string
  readAt?: string
}

/**
 * 알림 서비스
 * 알림 관련 API 호출을 담당합니다
 */
class NotificationService {
  private baseUrl = '/notifications'

  /**
   * 사용자의 알림 목록 조회 (페이징)
   */
  async getNotifications(page: number = 0, size: number = 20): Promise<{
    content: Notification[]
    totalElements: number
    totalPages: number
    size: number
    number: number
  }> {
    try {
      console.log('알림 목록 조회 API 호출:', `${this.baseUrl}?page=${page}&size=${size}`)
      const response = await api.get(`${this.baseUrl}?page=${page}&size=${size}`)
      console.log('알림 목록 조회 API 응답:', response)
      return response.data
    } catch (error) {
      console.error('알림 목록 조회 오류:', error)
      throw new Error('알림 목록을 불러오는 중 오류가 발생했습니다.')
    }
  }

  /**
   * 읽지 않은 알림 개수 조회
   */
  async getUnreadCount(): Promise<number> {
    try {
      console.log('읽지 않은 알림 개수 조회 API 호출:', `${this.baseUrl}/unread-count`)
      const response = await api.get(`${this.baseUrl}/unread-count`)
      console.log('읽지 않은 알림 개수 조회 API 응답:', response)
      return response.data
    } catch (error) {
      console.error('읽지 않은 알림 개수 조회 오류:', error)
      throw new Error('읽지 않은 알림 개수를 불러오는 중 오류가 발생했습니다.')
    }
  }

  /**
   * 읽지 않은 알림 목록 조회
   */
  async getUnreadNotifications(): Promise<Notification[]> {
    try {
      console.log('읽지 않은 알림 목록 조회 API 호출:', `${this.baseUrl}/unread`)
      const response = await api.get(`${this.baseUrl}/unread`)
      console.log('읽지 않은 알림 목록 조회 API 응답:', response)
      return response.data
    } catch (error) {
      console.error('읽지 않은 알림 목록 조회 오류:', error)
      throw new Error('읽지 않은 알림 목록을 불러오는 중 오류가 발생했습니다.')
    }
  }

  /**
   * 최근 3일 이내 알림 목록 조회 (읽은/읽지 않은 모든 알림)
   */
  async getRecentNotifications(): Promise<Notification[]> {
    try {
      console.log('최근 3일 이내 알림 목록 조회 API 호출:', `${this.baseUrl}/recent`)
      const response = await api.get(`${this.baseUrl}/recent`)
      console.log('최근 3일 이내 알림 목록 조회 API 응답:', response)
      return response.data
    } catch (error) {
      console.error('최근 3일 이내 알림 목록 조회 오류:', error)
      throw new Error('최근 3일 이내 알림 목록을 불러오는 중 오류가 발생했습니다.')
    }
  }

  /**
   * 최근 2주 이내 모든 알림 목록 조회 (읽은/읽지 않은 모든 알림)
   */
  async getAllNotifications(): Promise<Notification[]> {
    try {
      console.log('최근 2주 이내 모든 알림 목록 조회 API 호출:', `${this.baseUrl}/all`)
      const response = await api.get(`${this.baseUrl}/all`)
      console.log('최근 2주 이내 모든 알림 목록 조회 API 응답:', response)
      return response.data
    } catch (error) {
      console.error('최근 2주 이내 모든 알림 목록 조회 오류:', error)
      throw new Error('최근 2주 이내 모든 알림 목록을 불러오는 중 오류가 발생했습니다.')
    }
  }

  /**
   * 특정 알림을 읽음 처리
   */
  async markAsRead(notificationId: number): Promise<void> {
    try {
      console.log('알림 읽음 처리 API 호출:', `${this.baseUrl}/${notificationId}/read`)
      const response = await api.put(`${this.baseUrl}/${notificationId}/read`)
      console.log('알림 읽음 처리 API 응답:', response)
    } catch (error) {
      console.error('알림 읽음 처리 오류:', error)
      throw new Error('알림 읽음 처리 중 오류가 발생했습니다.')
    }
  }

  /**
   * 모든 알림을 읽음 처리
   */
  async markAllAsRead(): Promise<void> {
    try {
      console.log('모든 알림 읽음 처리 API 호출:', `${this.baseUrl}/read-all`)
      const response = await api.put(`${this.baseUrl}/read-all`)
      console.log('모든 알림 읽음 처리 API 응답:', response)
    } catch (error) {
      console.error('모든 알림 읽음 처리 오류:', error)
      throw new Error('모든 알림 읽음 처리 중 오류가 발생했습니다.')
    }
  }

  /**
   * 알림 삭제
   */
  async deleteNotification(notificationId: number): Promise<void> {
    try {
      console.log('알림 삭제 API 호출:', `${this.baseUrl}/${notificationId}`)
      const response = await api.delete(`${this.baseUrl}/${notificationId}`)
      console.log('알림 삭제 API 응답:', response)
    } catch (error) {
      console.error('알림 삭제 오류:', error)
      throw new Error('알림 삭제 중 오류가 발생했습니다.')
    }
  }

  /**
   * 테스트 알림 생성 (개발용)
   */
  async createTestNotification(data: {
    title: string
    message: string
    type: 'INFO' | 'WARNING' | 'ERROR' | 'SUCCESS'
    actionUrl?: string
  }): Promise<Notification> {
    try {
      console.log('테스트 알림 생성 API 호출:', `${this.baseUrl}/test`)
      const response = await api.post(`${this.baseUrl}/test`, data)
      console.log('테스트 알림 생성 API 응답:', response)
      return response.data
    } catch (error) {
      console.error('테스트 알림 생성 오류:', error)
      throw new Error('테스트 알림 생성 중 오류가 발생했습니다.')
    }
  }
}

export const notificationService = new NotificationService()
