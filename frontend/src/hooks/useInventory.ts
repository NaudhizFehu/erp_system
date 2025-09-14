/**
 * 재고 관리 React Query 훅
 * 재고 관련 데이터 페칭 및 뮤테이션을 관리합니다
 */

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import toast from 'react-hot-toast'
import inventoryApiService from '../services/inventoryApi'
import {
  Product,
  Inventory,
  StockMovement,
  Category,
  Warehouse,
  ProductCreateRequest,
  StockReceiptRequest,
  StockIssueRequest,
  StocktakingRequest,
  StockReservationRequest,
  InventoryTransferRequest,
  InventorySearchParams,
  InventoryStats,
  ProductStats,
  WarehouseInventory,
  AbcAnalysisResult,
  InventoryAlert,
  InventoryDashboard
} from '../types/inventory'
import { PageResponse } from '../types/common'

// Query Keys
export const INVENTORY_QUERY_KEYS = {
  // 상품 관련
  products: (companyId: number) => ['products', companyId] as const,
  product: (id: number) => ['products', id] as const,
  productsByCategory: (categoryId: number) => ['products', 'category', categoryId] as const,
  productSearch: (companyId: number, searchTerm: string) => ['products', 'search', companyId, searchTerm] as const,
  productBarcode: (barcode: string) => ['products', 'barcode', barcode] as const,
  lowStockProducts: (companyId: number) => ['products', 'low-stock', companyId] as const,
  outOfStockProducts: (companyId: number) => ['products', 'out-of-stock', companyId] as const,
  reorderNeededProducts: (companyId: number) => ['products', 'reorder-needed', companyId] as const,
  productStats: (companyId: number) => ['products', 'stats', companyId] as const,
  brandStats: (companyId: number) => ['products', 'brand-stats', companyId] as const,
  productStockStatus: (id: number) => ['products', 'stock-status', id] as const,

  // 재고 관련
  inventories: (companyId: number) => ['inventories', companyId] as const,
  inventory: (id: number) => ['inventories', id] as const,
  inventoriesByProduct: (productId: number) => ['inventories', 'product', productId] as const,
  inventoriesByWarehouse: (warehouseId: number) => ['inventories', 'warehouse', warehouseId] as const,
  inventorySearch: (companyId: number, searchTerm: string) => ['inventories', 'search', companyId, searchTerm] as const,
  lowStockInventories: (companyId: number) => ['inventories', 'low-stock', companyId] as const,
  outOfStockInventories: (companyId: number) => ['inventories', 'out-of-stock', companyId] as const,
  inventoryStats: (companyId: number) => ['inventories', 'stats', companyId] as const,
  abcAnalysis: (companyId: number) => ['inventories', 'abc-analysis', companyId] as const,
  inventoryAlerts: (companyId: number) => ['inventories', 'alerts', companyId] as const,

  // 재고이동 관련
  stockMovements: (companyId: number) => ['stock-movements', companyId] as const,
  stockMovement: (id: number) => ['stock-movements', id] as const,
  stockMovementsByProduct: (productId: number) => ['stock-movements', 'product', productId] as const,
  stockMovementsByWarehouse: (warehouseId: number) => ['stock-movements', 'warehouse', warehouseId] as const,

  // 창고 관련
  warehouses: (companyId: number) => ['warehouses', companyId] as const,
  warehouse: (id: number) => ['warehouses', id] as const,
  warehouseInventory: (warehouseId: number) => ['warehouses', 'inventory', warehouseId] as const,

  // 분류 관련
  categories: (companyId: number) => ['categories', companyId] as const,
  category: (id: number) => ['categories', id] as const,

  // 대시보드 관련
  inventoryDashboard: (companyId: number) => ['dashboard', 'inventory', companyId] as const,
  inventoryTrend: (companyId: number, days: number) => ['dashboard', 'inventory-trend', companyId, days] as const,
  warehouseUtilization: (companyId: number) => ['dashboard', 'warehouse-utilization', companyId] as const,
} as const

/**
 * 상품 관련 훅
 */
export function useProducts(companyId: number, params?: InventorySearchParams) {
  return useQuery({
    queryKey: [...INVENTORY_QUERY_KEYS.products(companyId), params],
    queryFn: () => inventoryApiService.product.getProductsByCompany(companyId, params),
    enabled: !!companyId,
    staleTime: 5 * 60 * 1000, // 5분
  })
}

export function useProduct(id: number) {
  return useQuery({
    queryKey: INVENTORY_QUERY_KEYS.product(id),
    queryFn: () => inventoryApiService.product.getProductById(id),
    enabled: !!id,
    staleTime: 5 * 60 * 1000,
  })
}

export function useProductsByCategory(categoryId: number, params?: InventorySearchParams) {
  return useQuery({
    queryKey: [...INVENTORY_QUERY_KEYS.productsByCategory(categoryId), params],
    queryFn: () => inventoryApiService.product.getProductsByCategory(categoryId, params),
    enabled: !!categoryId,
    staleTime: 5 * 60 * 1000,
  })
}

export function useProductSearch(companyId: number, searchTerm: string, params?: InventorySearchParams) {
  return useQuery({
    queryKey: [...INVENTORY_QUERY_KEYS.productSearch(companyId, searchTerm), params],
    queryFn: () => inventoryApiService.product.searchProducts(companyId, searchTerm, params),
    enabled: !!companyId && !!searchTerm.trim(),
    staleTime: 2 * 60 * 1000, // 2분
  })
}

export function useProductByBarcode(barcode: string) {
  return useQuery({
    queryKey: INVENTORY_QUERY_KEYS.productBarcode(barcode),
    queryFn: () => inventoryApiService.product.getProductByBarcode(barcode),
    enabled: !!barcode.trim(),
    staleTime: 10 * 60 * 1000, // 10분
  })
}

export function useLowStockProducts(companyId: number) {
  return useQuery({
    queryKey: INVENTORY_QUERY_KEYS.lowStockProducts(companyId),
    queryFn: () => inventoryApiService.product.getLowStockProducts(companyId),
    enabled: !!companyId,
    staleTime: 2 * 60 * 1000,
  })
}

export function useOutOfStockProducts(companyId: number) {
  return useQuery({
    queryKey: INVENTORY_QUERY_KEYS.outOfStockProducts(companyId),
    queryFn: () => inventoryApiService.product.getOutOfStockProducts(companyId),
    enabled: !!companyId,
    staleTime: 1 * 60 * 1000, // 1분
  })
}

export function useReorderNeededProducts(companyId: number) {
  return useQuery({
    queryKey: INVENTORY_QUERY_KEYS.reorderNeededProducts(companyId),
    queryFn: () => inventoryApiService.product.getReorderNeededProducts(companyId),
    enabled: !!companyId,
    staleTime: 2 * 60 * 1000,
  })
}

export function useProductStats(companyId: number) {
  return useQuery({
    queryKey: INVENTORY_QUERY_KEYS.productStats(companyId),
    queryFn: () => inventoryApiService.product.getProductStats(companyId),
    enabled: !!companyId,
    staleTime: 10 * 60 * 1000,
  })
}

export function useBrandStats(companyId: number) {
  return useQuery({
    queryKey: INVENTORY_QUERY_KEYS.brandStats(companyId),
    queryFn: () => inventoryApiService.product.getBrandStatistics(companyId),
    enabled: !!companyId,
    staleTime: 10 * 60 * 1000,
  })
}

export function useProductStockStatus(id: number) {
  return useQuery({
    queryKey: INVENTORY_QUERY_KEYS.productStockStatus(id),
    queryFn: () => inventoryApiService.product.getProductStockStatus(id),
    enabled: !!id,
    staleTime: 2 * 60 * 1000,
  })
}

/**
 * 재고 관련 훅
 */
export function useInventories(companyId: number, params?: InventorySearchParams) {
  return useQuery({
    queryKey: [...INVENTORY_QUERY_KEYS.inventories(companyId), params],
    queryFn: () => inventoryApiService.inventory.getInventoriesByCompany(companyId, params),
    enabled: !!companyId,
    staleTime: 2 * 60 * 1000,
  })
}

export function useInventory(id: number) {
  return useQuery({
    queryKey: INVENTORY_QUERY_KEYS.inventory(id),
    queryFn: () => inventoryApiService.inventory.getInventoryById(id),
    enabled: !!id,
    staleTime: 2 * 60 * 1000,
  })
}

export function useInventoriesByProduct(productId: number) {
  return useQuery({
    queryKey: INVENTORY_QUERY_KEYS.inventoriesByProduct(productId),
    queryFn: () => inventoryApiService.inventory.getInventoriesByProduct(productId),
    enabled: !!productId,
    staleTime: 2 * 60 * 1000,
  })
}

export function useInventoriesByWarehouse(warehouseId: number, params?: InventorySearchParams) {
  return useQuery({
    queryKey: [...INVENTORY_QUERY_KEYS.inventoriesByWarehouse(warehouseId), params],
    queryFn: () => inventoryApiService.inventory.getInventoriesByWarehouse(warehouseId, params),
    enabled: !!warehouseId,
    staleTime: 2 * 60 * 1000,
  })
}

export function useInventorySearch(companyId: number, searchTerm: string, params?: InventorySearchParams) {
  return useQuery({
    queryKey: [...INVENTORY_QUERY_KEYS.inventorySearch(companyId, searchTerm), params],
    queryFn: () => inventoryApiService.inventory.searchInventories(companyId, searchTerm, params),
    enabled: !!companyId && !!searchTerm.trim(),
    staleTime: 2 * 60 * 1000,
  })
}

export function useLowStockInventories(companyId: number) {
  return useQuery({
    queryKey: INVENTORY_QUERY_KEYS.lowStockInventories(companyId),
    queryFn: () => inventoryApiService.inventory.getLowStockInventories(companyId),
    enabled: !!companyId,
    staleTime: 2 * 60 * 1000,
  })
}

export function useOutOfStockInventories(companyId: number) {
  return useQuery({
    queryKey: INVENTORY_QUERY_KEYS.outOfStockInventories(companyId),
    queryFn: () => inventoryApiService.inventory.getOutOfStockInventories(companyId),
    enabled: !!companyId,
    staleTime: 1 * 60 * 1000,
  })
}

export function useInventoryStats(companyId: number) {
  return useQuery({
    queryKey: INVENTORY_QUERY_KEYS.inventoryStats(companyId),
    queryFn: () => inventoryApiService.inventory.getInventoryStats(companyId),
    enabled: !!companyId,
    staleTime: 5 * 60 * 1000,
  })
}

export function useAbcAnalysis(companyId: number) {
  return useQuery({
    queryKey: INVENTORY_QUERY_KEYS.abcAnalysis(companyId),
    queryFn: () => inventoryApiService.inventory.performAbcAnalysis(companyId),
    enabled: !!companyId,
    staleTime: 30 * 60 * 1000, // 30분
  })
}

export function useInventoryAlerts(companyId: number) {
  return useQuery({
    queryKey: INVENTORY_QUERY_KEYS.inventoryAlerts(companyId),
    queryFn: () => inventoryApiService.inventory.getInventoryAlerts(companyId),
    enabled: !!companyId,
    staleTime: 1 * 60 * 1000,
    refetchInterval: 2 * 60 * 1000, // 2분마다 자동 새로고침
  })
}

/**
 * 재고이동 관련 훅
 */
export function useStockMovements(companyId: number, params?: InventorySearchParams) {
  return useQuery({
    queryKey: [...INVENTORY_QUERY_KEYS.stockMovements(companyId), params],
    queryFn: () => inventoryApiService.stockMovement.getStockMovements(companyId, params),
    enabled: !!companyId,
    staleTime: 2 * 60 * 1000,
  })
}

export function useStockMovement(id: number) {
  return useQuery({
    queryKey: INVENTORY_QUERY_KEYS.stockMovement(id),
    queryFn: () => inventoryApiService.stockMovement.getStockMovementById(id),
    enabled: !!id,
    staleTime: 5 * 60 * 1000,
  })
}

export function useStockMovementsByProduct(productId: number, params?: InventorySearchParams) {
  return useQuery({
    queryKey: [...INVENTORY_QUERY_KEYS.stockMovementsByProduct(productId), params],
    queryFn: () => inventoryApiService.stockMovement.getStockMovementsByProduct(productId, params),
    enabled: !!productId,
    staleTime: 2 * 60 * 1000,
  })
}

export function useStockMovementsByWarehouse(warehouseId: number, params?: InventorySearchParams) {
  return useQuery({
    queryKey: [...INVENTORY_QUERY_KEYS.stockMovementsByWarehouse(warehouseId), params],
    queryFn: () => inventoryApiService.stockMovement.getStockMovementsByWarehouse(warehouseId, params),
    enabled: !!warehouseId,
    staleTime: 2 * 60 * 1000,
  })
}

/**
 * 창고 관련 훅
 */
export function useWarehouses(companyId: number) {
  return useQuery({
    queryKey: INVENTORY_QUERY_KEYS.warehouses(companyId),
    queryFn: () => inventoryApiService.warehouse.getWarehouses(companyId),
    enabled: !!companyId,
    staleTime: 10 * 60 * 1000,
  })
}

export function useWarehouse(id: number) {
  return useQuery({
    queryKey: INVENTORY_QUERY_KEYS.warehouse(id),
    queryFn: () => inventoryApiService.warehouse.getWarehouseById(id),
    enabled: !!id,
    staleTime: 10 * 60 * 1000,
  })
}

export function useWarehouseInventory(warehouseId: number) {
  return useQuery({
    queryKey: INVENTORY_QUERY_KEYS.warehouseInventory(warehouseId),
    queryFn: () => inventoryApiService.warehouse.getWarehouseInventory(warehouseId),
    enabled: !!warehouseId,
    staleTime: 5 * 60 * 1000,
  })
}

/**
 * 분류 관련 훅
 */
export function useCategories(companyId: number) {
  return useQuery({
    queryKey: INVENTORY_QUERY_KEYS.categories(companyId),
    queryFn: () => inventoryApiService.category.getCategories(companyId),
    enabled: !!companyId,
    staleTime: 10 * 60 * 1000,
  })
}

export function useCategory(id: number) {
  return useQuery({
    queryKey: INVENTORY_QUERY_KEYS.category(id),
    queryFn: () => inventoryApiService.category.getCategoryById(id),
    enabled: !!id,
    staleTime: 10 * 60 * 1000,
  })
}

/**
 * 대시보드 관련 훅
 */
export function useInventoryDashboard(companyId: number) {
  return useQuery({
    queryKey: INVENTORY_QUERY_KEYS.inventoryDashboard(companyId),
    queryFn: () => inventoryApiService.dashboard.getInventoryDashboard(companyId),
    enabled: !!companyId,
    staleTime: 5 * 60 * 1000,
    refetchInterval: 5 * 60 * 1000, // 5분마다 자동 새로고침
  })
}

export function useInventoryTrend(companyId: number, days: number = 30) {
  return useQuery({
    queryKey: INVENTORY_QUERY_KEYS.inventoryTrend(companyId, days),
    queryFn: () => inventoryApiService.dashboard.getInventoryTrend(companyId, days),
    enabled: !!companyId,
    staleTime: 10 * 60 * 1000,
  })
}

export function useWarehouseUtilization(companyId: number) {
  return useQuery({
    queryKey: INVENTORY_QUERY_KEYS.warehouseUtilization(companyId),
    queryFn: () => inventoryApiService.dashboard.getWarehouseUtilization(companyId),
    enabled: !!companyId,
    staleTime: 10 * 60 * 1000,
  })
}

/**
 * 뮤테이션 훅들
 */
export function useCreateProduct() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (productData: ProductCreateRequest) => 
      inventoryApiService.product.createProduct(productData),
    onSuccess: (data, variables) => {
      toast.success('상품이 성공적으로 생성되었습니다')
      // 관련 쿼리 무효화
      queryClient.invalidateQueries({ 
        queryKey: INVENTORY_QUERY_KEYS.products(variables.companyId) 
      })
      queryClient.invalidateQueries({ 
        queryKey: INVENTORY_QUERY_KEYS.productStats(variables.companyId) 
      })
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || '상품 생성에 실패했습니다')
    },
  })
}

export function useUpdateProduct() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, productData }: { id: number; productData: Partial<ProductCreateRequest> }) =>
      inventoryApiService.product.updateProduct(id, productData),
    onSuccess: (data, variables) => {
      toast.success('상품이 성공적으로 수정되었습니다')
      // 특정 상품 쿼리 무효화
      queryClient.invalidateQueries({ 
        queryKey: INVENTORY_QUERY_KEYS.product(variables.id) 
      })
      // 목록 쿼리들 무효화
      queryClient.invalidateQueries({ 
        predicate: (query) => 
          query.queryKey[0] === 'products' && Array.isArray(query.queryKey)
      })
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || '상품 수정에 실패했습니다')
    },
  })
}

export function useDeleteProduct() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => inventoryApiService.product.deleteProduct(id),
    onSuccess: () => {
      toast.success('상품이 성공적으로 삭제되었습니다')
      // 모든 상품 관련 쿼리 무효화
      queryClient.invalidateQueries({ 
        predicate: (query) => 
          query.queryKey[0] === 'products' && Array.isArray(query.queryKey)
      })
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || '상품 삭제에 실패했습니다')
    },
  })
}

export function useToggleProductActive() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => inventoryApiService.product.toggleProductActive(id),
    onSuccess: (data, variables) => {
      toast.success('상품 활성화 상태가 변경되었습니다')
      queryClient.invalidateQueries({ 
        queryKey: INVENTORY_QUERY_KEYS.product(variables) 
      })
      queryClient.invalidateQueries({ 
        predicate: (query) => 
          query.queryKey[0] === 'products' && Array.isArray(query.queryKey)
      })
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || '상품 활성화 상태 변경에 실패했습니다')
    },
  })
}

export function useReceiveStock() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (request: StockReceiptRequest) => 
      inventoryApiService.inventory.receiveStock(request),
    onSuccess: (data, variables) => {
      toast.success('재고 입고가 성공적으로 처리되었습니다')
      // 재고 관련 쿼리들 무효화
      queryClient.invalidateQueries({ 
        queryKey: INVENTORY_QUERY_KEYS.inventory(variables.inventoryId) 
      })
      queryClient.invalidateQueries({ 
        predicate: (query) => 
          (query.queryKey[0] === 'inventories' || query.queryKey[0] === 'stock-movements') && 
          Array.isArray(query.queryKey)
      })
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || '재고 입고 처리에 실패했습니다')
    },
  })
}

export function useIssueStock() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (request: StockIssueRequest) => 
      inventoryApiService.inventory.issueStock(request),
    onSuccess: (data, variables) => {
      toast.success('재고 출고가 성공적으로 처리되었습니다')
      queryClient.invalidateQueries({ 
        queryKey: INVENTORY_QUERY_KEYS.inventory(variables.inventoryId) 
      })
      queryClient.invalidateQueries({ 
        predicate: (query) => 
          (query.queryKey[0] === 'inventories' || query.queryKey[0] === 'stock-movements') && 
          Array.isArray(query.queryKey)
      })
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || '재고 출고 처리에 실패했습니다')
    },
  })
}

export function useReserveStock() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (request: StockReservationRequest) => 
      inventoryApiService.inventory.reserveStock(request),
    onSuccess: (data, variables) => {
      toast.success('재고 예약이 성공적으로 처리되었습니다')
      queryClient.invalidateQueries({ 
        queryKey: INVENTORY_QUERY_KEYS.inventory(variables.inventoryId) 
      })
      queryClient.invalidateQueries({ 
        predicate: (query) => 
          query.queryKey[0] === 'inventories' && Array.isArray(query.queryKey)
      })
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || '재고 예약에 실패했습니다')
    },
  })
}

export function useUnreserveStock() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ inventoryId, quantity, reason }: { inventoryId: number; quantity: number; reason?: string }) => 
      inventoryApiService.inventory.unreserveStock(inventoryId, quantity, reason),
    onSuccess: (data, variables) => {
      toast.success('재고 예약 해제가 성공적으로 처리되었습니다')
      queryClient.invalidateQueries({ 
        queryKey: INVENTORY_QUERY_KEYS.inventory(variables.inventoryId) 
      })
      queryClient.invalidateQueries({ 
        predicate: (query) => 
          query.queryKey[0] === 'inventories' && Array.isArray(query.queryKey)
      })
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || '재고 예약 해제에 실패했습니다')
    },
  })
}

export function usePerformStocktaking() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (request: StocktakingRequest) => 
      inventoryApiService.inventory.performStocktaking(request),
    onSuccess: (data, variables) => {
      toast.success('재고 실사가 성공적으로 처리되었습니다')
      queryClient.invalidateQueries({ 
        queryKey: INVENTORY_QUERY_KEYS.inventory(variables.inventoryId) 
      })
      queryClient.invalidateQueries({ 
        predicate: (query) => 
          (query.queryKey[0] === 'inventories' || query.queryKey[0] === 'stock-movements') && 
          Array.isArray(query.queryKey)
      })
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || '재고 실사 처리에 실패했습니다')
    },
  })
}

export function useTransferInventory() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (request: InventoryTransferRequest) => 
      inventoryApiService.inventory.transferInventory(request),
    onSuccess: () => {
      toast.success('재고 이동이 성공적으로 처리되었습니다')
      // 모든 재고 관련 쿼리 무효화
      queryClient.invalidateQueries({ 
        predicate: (query) => 
          (query.queryKey[0] === 'inventories' || query.queryKey[0] === 'stock-movements') && 
          Array.isArray(query.queryKey)
      })
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || '재고 이동에 실패했습니다')
    },
  })
}

export function useMoveInventoryLocation() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ inventoryId, newLocationCode, newLocationDescription }: { 
      inventoryId: number; 
      newLocationCode: string; 
      newLocationDescription?: string 
    }) => 
      inventoryApiService.inventory.moveInventoryLocation(inventoryId, newLocationCode, newLocationDescription),
    onSuccess: (data, variables) => {
      toast.success('재고 위치 이동이 성공적으로 처리되었습니다')
      queryClient.invalidateQueries({ 
        queryKey: INVENTORY_QUERY_KEYS.inventory(variables.inventoryId) 
      })
      queryClient.invalidateQueries({ 
        predicate: (query) => 
          (query.queryKey[0] === 'inventories' || query.queryKey[0] === 'stock-movements') && 
          Array.isArray(query.queryKey)
      })
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || '재고 위치 이동에 실패했습니다')
    },
  })
}

