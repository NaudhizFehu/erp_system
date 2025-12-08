import type {
  Account,
  AccountCreateRequest,
  AccountUpdateRequest,
  AccountTreeNode,
} from '@/types/accounting'

import api from './api'

export interface AccountListParams {
  page?: number
  size?: number
  sort?: string
  searchTerm?: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
}

class AccountService {
  private baseUrl = '/accounting/accounts'

  async getAccounts(
    params: AccountListParams = {}
  ): Promise<PageResponse<Account>> {
    try {
      const response = await api.get(this.baseUrl, { params })
      return response.data.data
    } catch (error) {
      console.error('계정과목 목록 조회 오류:', error)
      throw new Error('계정과목 목록을 불러오는 중 오류가 발생했습니다.')
    }
  }

  async getAccountById(id: number): Promise<Account> {
    try {
      const response = await api.get(`${this.baseUrl}/${id}`)
      return response.data.data
    } catch (error) {
      console.error('계정과목 상세 조회 오류:', error)
      throw new Error('계정과목 정보를 불러오는 중 오류가 발생했습니다.')
    }
  }

  async getAccountTree(companyId?: number): Promise<AccountTreeNode[]> {
    try {
      const response = await api.get(`${this.baseUrl}/tree`, {
        params: { companyId },
      })
      return response.data.data
    } catch (error) {
      console.error('계정과목 트리 조회 오류:', error)
      throw new Error('계정과목 트리를 불러오는 중 오류가 발생했습니다.')
    }
  }

  async createAccount(
    data: AccountCreateRequest & { companyId: number }
  ): Promise<Account> {
    try {
      const response = await api.post(this.baseUrl, data)
      return response.data.data
    } catch (error) {
      console.error('계정과목 등록 오류:', error)
      throw new Error('계정과목 등록 중 오류가 발생했습니다.')
    }
  }

  async updateAccount(
    id: number,
    data: AccountUpdateRequest
  ): Promise<Account> {
    try {
      const response = await api.put(`${this.baseUrl}/${id}`, data)
      return response.data.data
    } catch (error) {
      console.error('계정과목 수정 오류:', error)
      throw new Error('계정과목 수정 중 오류가 발생했습니다.')
    }
  }

  async deleteAccount(id: number): Promise<void> {
    try {
      await api.delete(`${this.baseUrl}/${id}`)
    } catch (error) {
      console.error('계정과목 삭제 오류:', error)
      throw new Error('계정과목 삭제 중 오류가 발생했습니다.')
    }
  }

  async checkAccountCode(
    code: string,
    companyId?: number,
    excludeId?: number
  ): Promise<boolean> {
    try {
      const response = await api.get(`${this.baseUrl}/check/code`, {
        params: { accountCode: code, companyId, excludeId },
      })
      return response.data.data
    } catch (error) {
      console.error('계정코드 중복 확인 오류:', error)
      throw new Error('계정코드 중복 확인 중 오류가 발생했습니다.')
    }
  }
}

export const accountService = new AccountService()
