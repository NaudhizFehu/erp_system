/**
 * 계정과목 관리 React Query 훅
 * 계정과목 관련 데이터 페칭과 상태 관리를 담당합니다
 */

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import toast from 'react-hot-toast'

import {
  accountService,
  type AccountListParams,
} from '@/services/accountService'
import type {
  AccountCreateRequest,
  AccountUpdateRequest,
} from '@/types/accounting'

/**
 * 쿼리 키 상수
 */
export const ACCOUNT_QUERY_KEYS = {
  all: ['accounts'] as const,
  lists: () => [...ACCOUNT_QUERY_KEYS.all, 'list'] as const,
  list: (params: AccountListParams) =>
    [...ACCOUNT_QUERY_KEYS.lists(), params] as const,
  details: () => [...ACCOUNT_QUERY_KEYS.all, 'detail'] as const,
  detail: (id: number) => [...ACCOUNT_QUERY_KEYS.details(), id] as const,
  tree: (companyId?: number) =>
    [...ACCOUNT_QUERY_KEYS.all, 'tree', companyId] as const,
  check: () => [...ACCOUNT_QUERY_KEYS.all, 'check'] as const,
}

/**
 * 계정과목 목록 조회 훅
 */
export function useAccounts(params: AccountListParams = {}) {
  return useQuery({
    queryKey: ACCOUNT_QUERY_KEYS.list(params),
    queryFn: async () => {
      try {
        const result = await accountService.getAccounts(params)
        return (
          result || {
            content: [],
            totalElements: 0,
            totalPages: 0,
            size: params.size || 20,
            number: params.page || 0,
            first: true,
            last: true,
          }
        )
      } catch (error) {
        console.error('계정과목 목록 조회 오류:', error)
        throw error
      }
    },
    staleTime: 5 * 60 * 1000, // 5분
    retry: 3,
  })
}

/**
 * 계정과목 상세 조회 훅
 */
export function useAccount(id: number) {
  return useQuery({
    queryKey: ACCOUNT_QUERY_KEYS.detail(id),
    queryFn: () => accountService.getAccountById(id),
    enabled: !!id,
    staleTime: 5 * 60 * 1000,
    retry: 3,
  })
}

/**
 * 계정과목 트리 구조 조회 훅
 */
export function useAccountTree(companyId?: number) {
  return useQuery({
    queryKey: ACCOUNT_QUERY_KEYS.tree(companyId),
    queryFn: async () => {
      try {
        const result = await accountService.getAccountTree(companyId)
        return result || []
      } catch (error) {
        console.error('계정과목 트리 조회 오류:', error)
        throw error
      }
    },
    staleTime: 10 * 60 * 1000, // 10분
    retry: 3,
  })
}

/**
 * 계정과목 등록 뮤테이션 훅
 */
export function useCreateAccount() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: AccountCreateRequest & { companyId: number }) =>
      accountService.createAccount(data),
    onSuccess: () => {
      // 관련 쿼리 무효화
      queryClient.invalidateQueries({ queryKey: ACCOUNT_QUERY_KEYS.all })
      toast.success('계정과목이 성공적으로 등록되었습니다')
    },
    onError: (error: any) => {
      const message =
        error.response?.data?.message || '계정과목 등록에 실패했습니다'
      toast.error(message)
    },
  })
}

/**
 * 계정과목 수정 뮤테이션 훅
 */
export function useUpdateAccount() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: AccountUpdateRequest }) =>
      accountService.updateAccount(id, data),
    onSuccess: (data, variables) => {
      // 특정 계정과목 쿼리 업데이트
      queryClient.setQueryData(ACCOUNT_QUERY_KEYS.detail(variables.id), data)
      // 관련 쿼리 무효화
      queryClient.invalidateQueries({ queryKey: ACCOUNT_QUERY_KEYS.lists() })
      queryClient.invalidateQueries({ queryKey: ACCOUNT_QUERY_KEYS.tree() })
      toast.success('계정과목 정보가 성공적으로 수정되었습니다')
    },
    onError: (error: any) => {
      const message =
        error.response?.data?.message || '계정과목 정보 수정에 실패했습니다'
      toast.error(message)
    },
  })
}

/**
 * 계정과목 삭제 뮤테이션 훅
 */
export function useDeleteAccount() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => accountService.deleteAccount(id),
    onSuccess: () => {
      // 관련 쿼리 무효화
      queryClient.invalidateQueries({ queryKey: ACCOUNT_QUERY_KEYS.all })
      toast.success('계정과목이 성공적으로 삭제되었습니다')
    },
    onError: (error: any) => {
      const message =
        error.response?.data?.message || '계정과목 삭제에 실패했습니다'
      toast.error(message)
    },
  })
}

/**
 * 계정코드 중복 확인 훅
 */
export function useCheckAccountCode(
  code: string,
  companyId?: number,
  excludeId?: number
) {
  return useQuery({
    queryKey: [
      ...ACCOUNT_QUERY_KEYS.check(),
      'code',
      code,
      companyId,
      excludeId,
    ],
    queryFn: () => accountService.checkAccountCode(code, companyId, excludeId),
    enabled: !!code && code.length >= 1,
    staleTime: 0, // 실시간 검증
    retry: 1,
  })
}
