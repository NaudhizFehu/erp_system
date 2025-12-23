import api from './api'
import type { ApiResponse, PageResponse } from '@/types/common'

/**
 * 사용자 역할
 */
export type UserRole = 'SUPER_ADMIN' | 'ADMIN' | 'MANAGER' | 'USER' | 'READONLY'

/**
 * 사용자 DTO
 */
export interface UserDto {
  id: number
  username: string
  email: string
  fullName: string
  phone?: string
  role: UserRole
  isActive: boolean
  isLocked: boolean
  isPasswordExpired: boolean
  company?: {
    id: number
    name: string
  }
  department?: {
    id: number
    name: string
  }
  lastLoginAt?: string
  passwordChangedAt?: string
  createdAt: string
  updatedAt: string
}

/**
 * 사용자 수정 요청 DTO
 */
export interface UserUpdateDto {
  email?: string
  fullName?: string
  phone?: string
  phoneNumber?: string
  position?: string
}

/**
 * 사용자 관리 서비스
 * ADMIN 이상 권한 필요
 */
class UserManagementService {
  private readonly BASE_URL = '/admin/users'

  /**
   * 전체 사용자 목록 조회 (페이징)
   */
  async getAllUsers(params: {
    page?: number
    size?: number
    sort?: string
  }): Promise<ApiResponse<PageResponse<UserDto>>> {
    const { page = 0, size = 20, sort = 'id,asc' } = params
    const response = await api.get<ApiResponse<PageResponse<UserDto>>>(
      this.BASE_URL,
      {
        params: { page, size, sort },
      }
    )
    return response.data
  }

  /**
   * 사용자 검색 (페이징)
   */
  async searchUsers(params: {
    query: string
    page?: number
    size?: number
  }): Promise<ApiResponse<PageResponse<UserDto>>> {
    const { query, page = 0, size = 20 } = params
    const response = await api.get<ApiResponse<PageResponse<UserDto>>>(
      `${this.BASE_URL}/search`,
      {
        params: { query, page, size },
      }
    )
    return response.data
  }

  /**
   * 특정 사용자 조회
   */
  async getUserById(userId: number): Promise<ApiResponse<UserDto>> {
    const response = await api.get<ApiResponse<UserDto>>(
      `${this.BASE_URL}/${userId}`
    )
    return response.data
  }

  /**
   * 역할별 사용자 조회
   */
  async getUsersByRole(role: UserRole): Promise<ApiResponse<UserDto[]>> {
    const response = await api.get<ApiResponse<UserDto[]>>(
      `${this.BASE_URL}/by-role/${role}`
    )
    return response.data
  }

  /**
   * 사용자 계정 활성화/비활성화
   */
  async toggleUserActiveStatus(
    userId: number,
    isActive: boolean
  ): Promise<ApiResponse<UserDto>> {
    const response = await api.patch<ApiResponse<UserDto>>(
      `${this.BASE_URL}/${userId}/active`,
      null,
      {
        params: { isActive },
      }
    )
    return response.data
  }

  /**
   * 사용자 계정 잠금/잠금해제
   */
  async toggleUserLockStatus(
    userId: number,
    isLocked: boolean
  ): Promise<ApiResponse<UserDto>> {
    const response = await api.patch<ApiResponse<UserDto>>(
      `${this.BASE_URL}/${userId}/lock`,
      null,
      {
        params: { isLocked },
      }
    )
    return response.data
  }

  /**
   * 사용자 역할 변경 (SUPER_ADMIN만 가능)
   */
  async updateUserRole(
    userId: number,
    role: UserRole
  ): Promise<ApiResponse<UserDto>> {
    const response = await api.patch<ApiResponse<UserDto>>(
      `${this.BASE_URL}/${userId}/role`,
      null,
      {
        params: { role },
      }
    )
    return response.data
  }

  /**
   * 사용자 비밀번호 재설정 (SUPER_ADMIN만 가능)
   */
  async resetUserPassword(
    userId: number,
    newPassword: string
  ): Promise<ApiResponse<string>> {
    const response = await api.patch<ApiResponse<string>>(
      `${this.BASE_URL}/${userId}/reset-password`,
      null,
      {
        params: { newPassword },
      }
    )
    return response.data
  }

  /**
   * 사용자 정보 수정
   */
  async updateUser(
    userId: number,
    updateDto: UserUpdateDto
  ): Promise<ApiResponse<UserDto>> {
    const response = await api.put<ApiResponse<UserDto>>(
      `${this.BASE_URL}/${userId}`,
      updateDto
    )
    return response.data
  }

  /**
   * 사용자 삭제 (논리 삭제, SUPER_ADMIN만 가능)
   */
  async deleteUser(userId: number): Promise<ApiResponse<string>> {
    const response = await api.delete<ApiResponse<string>>(
      `${this.BASE_URL}/${userId}`
    )
    return response.data
  }
}

export const userManagementService = new UserManagementService()
