/**
 * 사용자 관리 페이지
 * ADMIN 이상 권한 필요
 */

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'

import {
  userManagementService,
  type UserDto,
  type UserRole,
} from '@/services/userManagementService'

/**
 * 역할 배지 색상
 */
const getRoleColor = (role: UserRole): string => {
  const colors: Record<UserRole, string> = {
    SUPER_ADMIN: 'bg-red-100 text-red-800',
    ADMIN: 'bg-orange-100 text-orange-800',
    MANAGER: 'bg-yellow-100 text-yellow-800',
    USER: 'bg-blue-100 text-blue-800',
    READONLY: 'bg-gray-100 text-gray-800',
  }
  return colors[role] || 'bg-gray-100 text-gray-800'
}

/**
 * 역할 한글명
 */
const getRoleLabel = (role: UserRole): string => {
  const labels: Record<UserRole, string> = {
    SUPER_ADMIN: '시스템 관리자',
    ADMIN: '관리자',
    MANAGER: '매니저',
    USER: '일반 사용자',
    READONLY: '읽기 전용',
  }
  return labels[role] || role
}

export default function UserManagementPage() {
  const queryClient = useQueryClient()
  const [searchQuery, setSearchQuery] = useState('')
  const [page, setPage] = useState(0)
  const [pageSize] = useState(20)

  // 사용자 목록 조회
  const { data: usersData, isLoading } = useQuery({
    queryKey: ['users', page, pageSize, searchQuery],
    queryFn: async () => {
      if (searchQuery) {
        return await userManagementService.searchUsers({
          query: searchQuery,
          page,
          size: pageSize,
        })
      }
      return await userManagementService.getAllUsers({ page, size: pageSize })
    },
  })

  // 계정 활성화/비활성화
  const toggleActiveMutation = useMutation({
    mutationFn: ({ userId, isActive }: { userId: number; isActive: boolean }) =>
      userManagementService.toggleUserActiveStatus(userId, isActive),
    onSuccess: () => {
      alert('계정 상태가 변경되었습니다')
      queryClient.invalidateQueries({ queryKey: ['users'] })
    },
    onError: () => {
      alert('계정 상태 변경에 실패했습니다')
    },
  })

  // 계정 잠금/잠금해제
  const toggleLockMutation = useMutation({
    mutationFn: ({ userId, isLocked }: { userId: number; isLocked: boolean }) =>
      userManagementService.toggleUserLockStatus(userId, isLocked),
    onSuccess: () => {
      alert('계정 잠금 상태가 변경되었습니다')
      queryClient.invalidateQueries({ queryKey: ['users'] })
    },
    onError: () => {
      alert('계정 잠금 상태 변경에 실패했습니다')
    },
  })

  const handleSearch = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    const formData = new FormData(e.currentTarget)
    const query = formData.get('search') as string
    setSearchQuery(query)
    setPage(0)
  }

  const handleToggleActive = (userId: number, currentStatus: boolean) => {
    if (
      confirm(`계정을 ${currentStatus ? '비활성화' : '활성화'}하시겠습니까?`)
    ) {
      toggleActiveMutation.mutate({ userId, isActive: !currentStatus })
    }
  }

  const handleToggleLock = (userId: number, currentStatus: boolean) => {
    if (
      confirm(`계정을 ${currentStatus ? '잠금 해제' : '잠금'}하시겠습니까?`)
    ) {
      toggleLockMutation.mutate({ userId, isLocked: !currentStatus })
    }
  }

  return (
    <div className="p-6">
      <div className="mb-6">
        <h1 className="text-2xl font-bold">사용자 관리</h1>
        <p className="text-gray-600">시스템 사용자를 관리합니다</p>
      </div>

      {/* 검색 */}
      <div className="mb-4">
        <form onSubmit={handleSearch} className="flex gap-2">
          <input
            type="text"
            name="search"
            placeholder="사용자명 또는 실명으로 검색"
            className="max-w-md flex-1 rounded border border-gray-300 px-3 py-2"
          />
          <button
            type="submit"
            className="rounded bg-blue-600 px-4 py-2 text-white hover:bg-blue-700"
          >
            검색
          </button>
          <button
            type="button"
            onClick={() => {
              setSearchQuery('')
              setPage(0)
              queryClient.invalidateQueries({ queryKey: ['users'] })
            }}
            className="rounded border border-gray-300 px-4 py-2 hover:bg-gray-50"
          >
            새로고침
          </button>
        </form>
      </div>

      {/* 테이블 */}
      {isLoading ? (
        <div className="py-8 text-center">로딩 중...</div>
      ) : (
        <>
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">
                    ID
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">
                    사용자명
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">
                    실명
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">
                    이메일
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">
                    역할
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">
                    상태
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">
                    작업
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200 bg-white">
                {usersData?.data?.content?.map((user: UserDto) => (
                  <tr key={user.id}>
                    <td className="whitespace-nowrap px-6 py-4 text-sm text-gray-900">
                      {user.id}
                    </td>
                    <td className="whitespace-nowrap px-6 py-4 text-sm text-gray-900">
                      {user.username}
                    </td>
                    <td className="whitespace-nowrap px-6 py-4 text-sm text-gray-900">
                      {user.fullName}
                    </td>
                    <td className="whitespace-nowrap px-6 py-4 text-sm text-gray-900">
                      {user.email}
                    </td>
                    <td className="whitespace-nowrap px-6 py-4 text-sm">
                      <span
                        className={`inline-flex rounded-full px-2 py-1 text-xs font-semibold ${getRoleColor(user.role)}`}
                      >
                        {getRoleLabel(user.role)}
                      </span>
                    </td>
                    <td className="whitespace-nowrap px-6 py-4 text-sm">
                      <div className="flex gap-1">
                        <span
                          className={`inline-flex rounded-full px-2 py-1 text-xs font-semibold ${
                            user.isActive
                              ? 'bg-green-100 text-green-800'
                              : 'bg-red-100 text-red-800'
                          }`}
                        >
                          {user.isActive ? '활성' : '비활성'}
                        </span>
                        {user.isLocked && (
                          <span className="inline-flex rounded-full bg-orange-100 px-2 py-1 text-xs font-semibold text-orange-800">
                            잠금
                          </span>
                        )}
                      </div>
                    </td>
                    <td className="whitespace-nowrap px-6 py-4 text-sm">
                      <div className="flex gap-2">
                        <button
                          onClick={() =>
                            handleToggleActive(user.id, user.isActive)
                          }
                          className="text-blue-600 hover:text-blue-900"
                        >
                          {user.isActive ? '비활성화' : '활성화'}
                        </button>
                        <button
                          onClick={() =>
                            handleToggleLock(user.id, user.isLocked)
                          }
                          className="text-orange-600 hover:text-orange-900"
                        >
                          {user.isLocked ? '잠금해제' : '잠금'}
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* 페이지네이션 */}
          <div className="mt-4 flex items-center justify-between">
            <div className="text-sm text-gray-700">
              전체 {usersData?.data?.totalElements || 0}개
            </div>
            <div className="flex gap-2">
              <button
                onClick={() => setPage(p => Math.max(0, p - 1))}
                disabled={page === 0}
                className="rounded border border-gray-300 px-4 py-2 disabled:opacity-50"
              >
                이전
              </button>
              <span className="px-4 py-2">
                {page + 1} / {usersData?.data?.totalPages || 1}
              </span>
              <button
                onClick={() => setPage(p => p + 1)}
                disabled={
                  page >= (usersData?.data?.totalPages || 1) - 1 ||
                  !usersData?.data?.content?.length
                }
                className="rounded border border-gray-300 px-4 py-2 disabled:opacity-50"
              >
                다음
              </button>
            </div>
          </div>
        </>
      )}
    </div>
  )
}
