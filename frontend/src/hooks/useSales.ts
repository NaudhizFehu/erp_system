/**
 * 영업관리 React Query 훅
 * 영업 관련 데이터 페칭과 상태 관리를 담당합니다
 */

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import toast from 'react-hot-toast'
import { customerApi, orderApi } from '@/services/salesApi'
import type {
  Customer,
  CustomerSummary,
  CustomerCreateRequest,
  CustomerUpdateRequest,
  CustomerSearchParams,
  CustomerStats,
  Order,
  OrderSummary,
  OrderCreateRequest,
  OrderSearchParams,
  OrderStats,
  PaginationParams,
  PageResponse
} from '@/types/sales'

// ================================
// 고객 관리 훅
// ================================

/**
 * 고객 상세 조회
 */
export const useCustomer = (customerId: number) => {
  return useQuery({
    queryKey: ['customer', customerId],
    queryFn: () => customerApi.getById(customerId),
    enabled: !!customerId
  })
}

/**
 * 회사별 고객 목록 조회
 */
export const useCustomers = (companyId: number, params?: PaginationParams) => {
  return useQuery({
    queryKey: ['customers', companyId, params],
    queryFn: () => customerApi.getByCompany(companyId, params),
    enabled: !!companyId
  })
}

/**
 * 고객 검색
 */
export const useCustomerSearch = (
  companyId: number,
  searchTerm: string,
  params?: PaginationParams
) => {
  return useQuery({
    queryKey: ['customers', 'search', companyId, searchTerm, params],
    queryFn: () => customerApi.search(companyId, searchTerm, params),
    enabled: !!companyId && !!searchTerm
  })
}

/**
 * 고객 고급 검색
 */
export const useCustomerAdvancedSearch = (
  companyId: number,
  searchParams: CustomerSearchParams,
  params?: PaginationParams
) => {
  return useQuery({
    queryKey: ['customers', 'advancedSearch', companyId, searchParams, params],
    queryFn: () => customerApi.searchAdvanced(companyId, searchParams, params),
    enabled: !!companyId
  })
}

/**
 * VIP 고객 조회
 */
export const useVipCustomers = (companyId: number) => {
  return useQuery({
    queryKey: ['customers', 'vip', companyId],
    queryFn: () => customerApi.getVipCustomers(companyId),
    enabled: !!companyId
  })
}

/**
 * 고객 통계 조회
 */
export const useCustomerStats = (companyId: number) => {
  return useQuery({
    queryKey: ['customers', 'stats', companyId],
    queryFn: () => customerApi.getStatistics(companyId),
    enabled: !!companyId
  })
}

/**
 * 고객 생성
 */
export const useCreateCustomer = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: CustomerCreateRequest) => customerApi.create(data),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ['customers'] })
      toast.success('고객이 성공적으로 생성되었습니다')
    },
    onError: (error: any) => {
      toast.error(`고객 생성 실패: ${error.response?.data?.message || error.message}`)
    }
  })
}

/**
 * 고객 수정
 */
export const useUpdateCustomer = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ customerId, data }: { customerId: number; data: CustomerUpdateRequest }) =>
      customerApi.update(customerId, data),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ['customers'] })
      queryClient.invalidateQueries({ queryKey: ['customer', data.id] })
      toast.success('고객 정보가 성공적으로 수정되었습니다')
    },
    onError: (error: any) => {
      toast.error(`고객 수정 실패: ${error.response?.data?.message || error.message}`)
    }
  })
}

/**
 * 고객 삭제
 */
export const useDeleteCustomer = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (customerId: number) => customerApi.delete(customerId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['customers'] })
      toast.success('고객이 성공적으로 삭제되었습니다')
    },
    onError: (error: any) => {
      toast.error(`고객 삭제 실패: ${error.response?.data?.message || error.message}`)
    }
  })
}

/**
 * 고객 상태 변경
 */
export const useChangeCustomerStatus = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ customerId, data }: {
      customerId: number
      data: { customerStatus: string; reason?: string }
    }) => customerApi.changeStatus(customerId, data),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ['customers'] })
      queryClient.invalidateQueries({ queryKey: ['customer', data.id] })
      toast.success('고객 상태가 성공적으로 변경되었습니다')
    },
    onError: (error: any) => {
      toast.error(`고객 상태 변경 실패: ${error.response?.data?.message || error.message}`)
    }
  })
}

/**
 * 고객 등급 변경
 */
export const useChangeCustomerGrade = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ customerId, data }: {
      customerId: number
      data: { customerGrade: string; reason?: string }
    }) => customerApi.changeGrade(customerId, data),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ['customers'] })
      queryClient.invalidateQueries({ queryKey: ['customer', data.id] })
      toast.success('고객 등급이 성공적으로 변경되었습니다')
    },
    onError: (error: any) => {
      toast.error(`고객 등급 변경 실패: ${error.response?.data?.message || error.message}`)
    }
  })
}

/**
 * 고객 활성화 토글
 */
export const useToggleCustomerActive = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (customerId: number) => customerApi.toggleActive(customerId),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ['customers'] })
      queryClient.invalidateQueries({ queryKey: ['customer', data.id] })
      toast.success('고객 활성화 상태가 성공적으로 변경되었습니다')
    },
    onError: (error: any) => {
      toast.error(`고객 활성화 상태 변경 실패: ${error.response?.data?.message || error.message}`)
    }
  })
}

// ================================
// 주문 관리 훅
// ================================

/**
 * 주문 상세 조회
 */
export const useOrder = (orderId: number) => {
  return useQuery({
    queryKey: ['order', orderId],
    queryFn: () => orderApi.getById(orderId),
    enabled: !!orderId
  })
}

/**
 * 회사별 주문 목록 조회
 */
export const useOrders = (companyId: number, params?: PaginationParams) => {
  return useQuery({
    queryKey: ['orders', companyId, params],
    queryFn: () => orderApi.getByCompany(companyId, params),
    enabled: !!companyId
  })
}

/**
 * 고객별 주문 조회
 */
export const useCustomerOrders = (customerId: number, params?: PaginationParams) => {
  return useQuery({
    queryKey: ['orders', 'customer', customerId, params],
    queryFn: () => orderApi.getByCustomer(customerId, params),
    enabled: !!customerId
  })
}

/**
 * 주문 검색
 */
export const useOrderSearch = (
  companyId: number,
  searchTerm: string,
  params?: PaginationParams
) => {
  return useQuery({
    queryKey: ['orders', 'search', companyId, searchTerm, params],
    queryFn: () => orderApi.search(companyId, searchTerm, params),
    enabled: !!companyId && !!searchTerm
  })
}

/**
 * 주문 고급 검색
 */
export const useOrderAdvancedSearch = (
  companyId: number,
  searchParams: OrderSearchParams,
  params?: PaginationParams
) => {
  return useQuery({
    queryKey: ['orders', 'advancedSearch', companyId, searchParams, params],
    queryFn: () => orderApi.searchAdvanced(companyId, searchParams, params),
    enabled: !!companyId
  })
}

/**
 * 오늘 주문 조회
 */
export const useTodayOrders = (companyId: number) => {
  return useQuery({
    queryKey: ['orders', 'today', companyId],
    queryFn: () => orderApi.getTodayOrders(companyId),
    enabled: !!companyId
  })
}

/**
 * 긴급 주문 조회
 */
export const useUrgentOrders = (companyId: number) => {
  return useQuery({
    queryKey: ['orders', 'urgent', companyId],
    queryFn: () => orderApi.getUrgentOrders(companyId),
    enabled: !!companyId
  })
}

/**
 * 주문 통계 조회
 */
export const useOrderStats = (companyId: number) => {
  return useQuery({
    queryKey: ['orders', 'stats', companyId],
    queryFn: () => orderApi.getStatistics(companyId),
    enabled: !!companyId
  })
}

/**
 * 주문 생성
 */
export const useCreateOrder = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: OrderCreateRequest) => orderApi.create(data),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ['orders'] })
      queryClient.invalidateQueries({ queryKey: ['customers'] }) // 고객 통계 업데이트를 위해
      toast.success('주문이 성공적으로 생성되었습니다')
    },
    onError: (error: any) => {
      toast.error(`주문 생성 실패: ${error.response?.data?.message || error.message}`)
    }
  })
}

/**
 * 견적서에서 주문 생성
 */
export const useCreateOrderFromQuote = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: {
      quoteId: number
      orderDate: string
      requiredDate?: string
      deliveryAddress?: string
      deliveryMemo?: string
      paymentTerms?: string
      specialInstructions?: string
      remarks?: string
    }) => orderApi.createFromQuote(data),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ['orders'] })
      queryClient.invalidateQueries({ queryKey: ['quotes'] })
      queryClient.invalidateQueries({ queryKey: ['customers'] })
      toast.success('견적서에서 주문이 성공적으로 생성되었습니다')
    },
    onError: (error: any) => {
      toast.error(`주문 생성 실패: ${error.response?.data?.message || error.message}`)
    }
  })
}

/**
 * 주문 수정
 */
export const useUpdateOrder = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ orderId, data }: { orderId: number; data: Partial<OrderCreateRequest> }) =>
      orderApi.update(orderId, data),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ['orders'] })
      queryClient.invalidateQueries({ queryKey: ['order', data.id] })
      toast.success('주문 정보가 성공적으로 수정되었습니다')
    },
    onError: (error: any) => {
      toast.error(`주문 수정 실패: ${error.response?.data?.message || error.message}`)
    }
  })
}

/**
 * 주문 삭제
 */
export const useDeleteOrder = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (orderId: number) => orderApi.delete(orderId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['orders'] })
      queryClient.invalidateQueries({ queryKey: ['customers'] })
      toast.success('주문이 성공적으로 삭제되었습니다')
    },
    onError: (error: any) => {
      toast.error(`주문 삭제 실패: ${error.response?.data?.message || error.message}`)
    }
  })
}

/**
 * 주문 상태 변경
 */
export const useChangeOrderStatus = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ orderId, data }: {
      orderId: number
      data: { orderStatus: string; reason?: string }
    }) => orderApi.changeStatus(orderId, data),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ['orders'] })
      queryClient.invalidateQueries({ queryKey: ['order', data.id] })
      toast.success('주문 상태가 성공적으로 변경되었습니다')
    },
    onError: (error: any) => {
      toast.error(`주문 상태 변경 실패: ${error.response?.data?.message || error.message}`)
    }
  })
}

/**
 * 주문 확정
 */
export const useConfirmOrder = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (orderId: number) => orderApi.confirm(orderId),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ['orders'] })
      queryClient.invalidateQueries({ queryKey: ['order', data.id] })
      toast.success('주문이 성공적으로 확정되었습니다')
    },
    onError: (error: any) => {
      toast.error(`주문 확정 실패: ${error.response?.data?.message || error.message}`)
    }
  })
}

/**
 * 주문 배송 처리
 */
export const useShipOrder = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ orderId, data }: {
      orderId: number
      data: {
        courierCompany: string
        trackingNumber: string
        shippedDate?: string
        remarks?: string
      }
    }) => orderApi.ship(orderId, data),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ['orders'] })
      queryClient.invalidateQueries({ queryKey: ['order', data.id] })
      toast.success('주문 배송이 성공적으로 처리되었습니다')
    },
    onError: (error: any) => {
      toast.error(`주문 배송 처리 실패: ${error.response?.data?.message || error.message}`)
    }
  })
}

/**
 * 주문 취소
 */
export const useCancelOrder = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ orderId, data }: {
      orderId: number
      data: { cancellationReason: string }
    }) => orderApi.cancel(orderId, data),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ['orders'] })
      queryClient.invalidateQueries({ queryKey: ['order', data.id] })
      queryClient.invalidateQueries({ queryKey: ['customers'] })
      toast.success('주문이 성공적으로 취소되었습니다')
    },
    onError: (error: any) => {
      toast.error(`주문 취소 실패: ${error.response?.data?.message || error.message}`)
    }
  })
}

/**
 * 주문 결제 처리
 */
export const useProcessOrderPayment = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ orderId, data }: {
      orderId: number
      data: {
        paymentAmount: number
        paymentMethod: string
        paymentDate?: string
        paymentReference?: string
        remarks?: string
      }
    }) => orderApi.processPayment(orderId, data),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ['orders'] })
      queryClient.invalidateQueries({ queryKey: ['order', data.id] })
      queryClient.invalidateQueries({ queryKey: ['customers'] })
      toast.success('주문 결제가 성공적으로 처리되었습니다')
    },
    onError: (error: any) => {
      toast.error(`주문 결제 처리 실패: ${error.response?.data?.message || error.message}`)
    }
  })
}

