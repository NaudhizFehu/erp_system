import api from './api'

/**
 * 사용자 정보 인터페이스
 */
export interface UserProfile {
  id: number
  username: string
  fullName: string
  email: string
  phoneNumber?: string
  department?: string
  position?: string
  isActive: boolean
  createdAt: string
  updatedAt: string
}

/**
 * 사용자 정보 업데이트 요청 DTO
 */
export interface UpdateUserProfileRequest {
  fullName: string
  email: string
  phoneNumber?: string
  department?: string
  position?: string
  currentPassword?: string
  newPassword?: string
}

/**
 * 사용자 정보 서비스
 */
class UserService {
  private baseUrl = '/users'

  /**
   * 현재 사용자 정보 조회
   */
  async getCurrentUser(): Promise<UserProfile> {
    try {
      console.log('현재 사용자 정보 조회 API 호출:', `${this.baseUrl}/me`)
      const response = await api.get(`${this.baseUrl}/me`)
      console.log('현재 사용자 정보 조회 API 응답:', response)
      return response.data
    } catch (error) {
      console.error('현재 사용자 정보 조회 오류:', error)
      throw new Error('사용자 정보를 불러오는 중 오류가 발생했습니다.')
    }
  }

  /**
   * 사용자 정보 업데이트
   */
  async updateProfile(data: UpdateUserProfileRequest): Promise<UserProfile> {
    try {
      console.log('사용자 정보 업데이트 API 호출:', `${this.baseUrl}/profile`)
      const response = await api.put(`${this.baseUrl}/profile`, data)
      console.log('사용자 정보 업데이트 API 응답:', response)
      return response.data
    } catch (error) {
      console.error('사용자 정보 업데이트 오류:', error)
      throw new Error('사용자 정보 업데이트 중 오류가 발생했습니다.')
    }
  }

  /**
   * 비밀번호 변경
   */
  async changePassword(data: {
    currentPassword: string
    newPassword: string
  }): Promise<void> {
    try {
      console.log('비밀번호 변경 API 호출:', `${this.baseUrl}/change-password`)
      const response = await api.put(`${this.baseUrl}/change-password`, data)
      console.log('비밀번호 변경 API 응답:', response)
    } catch (error) {
      console.error('비밀번호 변경 오류:', error)
      throw new Error('비밀번호 변경 중 오류가 발생했습니다.')
    }
  }

  /**
   * 사용자 프로필 이미지 업로드
   */
  async uploadProfileImage(file: File): Promise<{ imageUrl: string }> {
    try {
      const formData = new FormData()
      formData.append('image', file)
      
      console.log('프로필 이미지 업로드 API 호출:', `${this.baseUrl}/profile-image`)
      const response = await api.post(`${this.baseUrl}/profile-image`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      console.log('프로필 이미지 업로드 API 응답:', response)
      return response.data
    } catch (error) {
      console.error('프로필 이미지 업로드 오류:', error)
      throw new Error('프로필 이미지 업로드 중 오류가 발생했습니다.')
    }
  }

  /**
   * 사용자 계정 비활성화
   */
  async deactivateAccount(): Promise<void> {
    try {
      console.log('계정 비활성화 API 호출:', `${this.baseUrl}/deactivate`)
      const response = await api.put(`${this.baseUrl}/deactivate`)
      console.log('계정 비활성화 API 응답:', response)
    } catch (error) {
      console.error('계정 비활성화 오류:', error)
      throw new Error('계정 비활성화 중 오류가 발생했습니다.')
    }
  }
}

export const userService = new UserService()
