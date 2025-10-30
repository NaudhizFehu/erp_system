/**
 * 재고 관리 API 서비스
 * 백엔드 재고 관리 API와 통신하는 서비스 레이어입니다
 */

import axios from 'axios'
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
  StocktakingResult,
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
import { ApiResponse, PageResponse } from '../types/common'

// API 기본 URL (api.ts에서 이미 /api가 설정되어 있음)
const API_BASE_URL = ''

// Axios 인스턴스 생성
const inventoryApiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  }
})

// 요청 인터셉터: 인증 토큰 추가
inventoryApiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 응답 인터셉터: 에러 처리
inventoryApiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // 인증 실패시 로그인 페이지로 리다이렉트
      localStorage.removeItem('accessToken')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

/**
 * 상품 관리 API 서비스
 */
export const productApi = {
  /**
   * 상품 생성
   */
  async createProduct(productData: ProductCreateRequest): Promise<ApiResponse<Product>> {
    const response = await inventoryApiClient.post('/products', productData)
    return response.data
  },

  /**
   * 상품 수정
   */
  async updateProduct(id: number, productData: Partial<ProductCreateRequest>): Promise<ApiResponse<Product>> {
    const response = await inventoryApiClient.put(`/products/${id}`, productData)
    return response.data
  },

  /**
   * 상품 삭제
   */
  async deleteProduct(id: number): Promise<ApiResponse<void>> {
    const response = await inventoryApiClient.delete(`/products/${id}`)
    return response.data
  },

  /**
   * 상품 조회 (ID)
   */
  async getProductById(id: number): Promise<ApiResponse<Product>> {
    const response = await inventoryApiClient.get(`/products/${id}`)
    return response.data
  },

  /**
   * 회사별 상품 목록 조회
   */
  async getProductsByCompany(
    companyId: number, 
    params?: InventorySearchParams
  ): Promise<ApiResponse<PageResponse<Product>>> {
    const response = await inventoryApiClient.get(`/products/companies/${companyId}`, { params })
    return response.data
  },

  /**
   * 분류별 상품 목록 조회
   */
  async getProductsByCategory(
    categoryId: number, 
    params?: InventorySearchParams
  ): Promise<ApiResponse<PageResponse<Product>>> {
    const response = await inventoryApiClient.get(`/products/categories/${categoryId}`, { params })
    return response.data
  },

  /**
   * 상품 검색
   */
  async searchProducts(
    companyId: number, 
    searchTerm: string, 
    params?: InventorySearchParams
  ): Promise<ApiResponse<PageResponse<Product>>> {
    const response = await inventoryApiClient.get(`/products/companies/${companyId}/search`, {
      params: { searchTerm, ...params }
    })
    return response.data
  },

  /**
   * 고급 상품 검색
   */
  async searchProductsAdvanced(
    companyId: number, 
    searchParams: InventorySearchParams
  ): Promise<ApiResponse<PageResponse<Product>>> {
    const response = await inventoryApiClient.post(`/products/companies/${companyId}/search/advanced`, searchParams)
    return response.data
  },

  /**
   * 바코드로 상품 조회
   */
  async getProductByBarcode(barcode: string): Promise<ApiResponse<Product>> {
    const response = await inventoryApiClient.get(`/products/barcode/${barcode}`)
    return response.data
  },

  /**
   * 안전재고 미달 상품 조회
   */
  async getLowStockProducts(companyId: number): Promise<ApiResponse<Product[]>> {
    const response = await inventoryApiClient.get(`/products/companies/${companyId}/low-stock`)
    return response.data
  },

  /**
   * 재고없음 상품 조회
   */
  async getOutOfStockProducts(companyId: number): Promise<ApiResponse<Product[]>> {
    const response = await inventoryApiClient.get(`/products/companies/${companyId}/out-of-stock`)
    return response.data
  },

  /**
   * 재주문 필요 상품 조회
   */
  async getReorderNeededProducts(companyId: number): Promise<ApiResponse<Product[]>> {
    const response = await inventoryApiClient.get(`/products/companies/${companyId}/reorder-needed`)
    return response.data
  },

  /**
   * 상품 통계 조회
   */
  async getProductStats(companyId: number): Promise<ApiResponse<ProductStats>> {
    const response = await inventoryApiClient.get(`/products/companies/${companyId}/statistics`)
    return response.data
  },

  /**
   * 브랜드별 상품 통계
   */
  async getBrandStatistics(companyId: number): Promise<ApiResponse<Array<{brand: string, count: number}>>> {
    const response = await inventoryApiClient.get(`/products/companies/${companyId}/brands/statistics`)
    return response.data
  },

  /**
   * 상품 재고 현황 조회
   */
  async getProductStockStatus(id: number): Promise<ApiResponse<any>> {
    const response = await inventoryApiClient.get(`/products/${id}/stock-status`)
    return response.data
  },

  /**
   * 상품 활성화/비활성화
   */
  async toggleProductActive(id: number): Promise<ApiResponse<Product>> {
    const response = await inventoryApiClient.put(`/products/${id}/toggle-active`)
    return response.data
  },

  /**
   * 상품 코드 중복 확인
   */
  async checkProductCodeDuplicate(
    companyId: number, 
    productCode: string, 
    excludeId?: number
  ): Promise<ApiResponse<boolean>> {
    const response = await inventoryApiClient.get(`/products/companies/${companyId}/check-code/${productCode}`, {
      params: { excludeId }
    })
    return response.data
  },

  /**
   * 바코드 중복 확인
   */
  async checkBarcodeDuplicate(barcode: string, excludeId?: number): Promise<ApiResponse<boolean>> {
    const response = await inventoryApiClient.get(`/products/check-barcode/${barcode}`, {
      params: { excludeId }
    })
    return response.data
  }
}

/**
 * 재고 관리 API 서비스
 */
export const inventoryApi = {
  /**
   * 재고 생성
   */
  async createInventory(inventoryData: any): Promise<ApiResponse<Inventory>> {
    const response = await inventoryApiClient.post('/inventory', inventoryData)
    return response.data
  },

  /**
   * 재고 수정
   */
  async updateInventory(id: number, inventoryData: any): Promise<ApiResponse<Inventory>> {
    const response = await inventoryApiClient.put(`/inventory/${id}`, inventoryData)
    return response.data
  },

  /**
   * 재고 삭제
   */
  async deleteInventory(id: number): Promise<ApiResponse<void>> {
    const response = await inventoryApiClient.delete(`/inventory/${id}`)
    return response.data
  },

  /**
   * 재고 조회 (ID)
   */
  async getInventoryById(id: number): Promise<ApiResponse<Inventory>> {
    const response = await inventoryApiClient.get(`/inventory/${id}`)
    return response.data
  },

  /**
   * 회사별 재고 목록 조회
   */
  async getInventoriesByCompany(
    companyId: number, 
    params?: InventorySearchParams
  ): Promise<ApiResponse<PageResponse<Inventory>>> {
    const response = await inventoryApiClient.get(`/inventory/companies/${companyId}`, { params })
    return response.data
  },

  /**
   * 상품별 재고 목록 조회
   */
  async getInventoriesByProduct(productId: number): Promise<ApiResponse<Inventory[]>> {
    const response = await inventoryApiClient.get(`/inventory/products/${productId}`)
    return response.data
  },

  /**
   * 창고별 재고 목록 조회
   */
  async getInventoriesByWarehouse(
    warehouseId: number, 
    params?: InventorySearchParams
  ): Promise<ApiResponse<PageResponse<Inventory>>> {
    const response = await inventoryApiClient.get(`/inventory/warehouses/${warehouseId}`, { params })
    return response.data
  },

  /**
   * 재고 검색
   */
  async searchInventories(
    companyId: number, 
    searchTerm: string, 
    params?: InventorySearchParams
  ): Promise<ApiResponse<PageResponse<Inventory>>> {
    const response = await inventoryApiClient.get(`/inventory/companies/${companyId}/search`, {
      params: { searchTerm, ...params }
    })
    return response.data
  },

  /**
   * 재고 입고 처리
   */
  async receiveStock(request: StockReceiptRequest): Promise<ApiResponse<Inventory>> {
    const { inventoryId, quantity, unitCost, reason } = request
    const response = await inventoryApiClient.post(`/inventory/${inventoryId}/receive`, null, {
      params: { quantity, unitCost, reason }
    })
    return response.data
  },

  /**
   * 재고 출고 처리
   */
  async issueStock(request: StockIssueRequest): Promise<ApiResponse<Inventory>> {
    const { inventoryId, quantity, reason } = request
    const response = await inventoryApiClient.post(`/inventory/${inventoryId}/issue`, null, {
      params: { quantity, reason }
    })
    return response.data
  },

  /**
   * 재고 예약
   */
  async reserveStock(request: StockReservationRequest): Promise<ApiResponse<Inventory>> {
    const response = await inventoryApiClient.post('/inventory/reserve', request)
    return response.data
  },

  /**
   * 재고 예약 해제
   */
  async unreserveStock(inventoryId: number, quantity: number, reason?: string): Promise<ApiResponse<Inventory>> {
    const response = await inventoryApiClient.post(`/inventory/${inventoryId}/unreserve`, null, {
      params: { quantity, reason }
    })
    return response.data
  },

  /**
   * 재고 실사 처리
   */
  async performStocktaking(request: StocktakingRequest): Promise<ApiResponse<StocktakingResult>> {
    const response = await inventoryApiClient.post('/inventory/stocktaking', request)
    return response.data
  },

  /**
   * 재고 조정
   */
  async adjustInventory(request: any): Promise<ApiResponse<Inventory>> {
    const response = await inventoryApiClient.post('/inventory/adjust', request)
    return response.data
  },

  /**
   * 재고 이동 (창고간)
   */
  async transferInventory(request: InventoryTransferRequest): Promise<ApiResponse<Inventory[]>> {
    const response = await inventoryApiClient.post('/inventory/transfer', request)
    return response.data
  },

  /**
   * 재고 위치 이동
   */
  async moveInventoryLocation(
    inventoryId: number, 
    newLocationCode: string, 
    newLocationDescription?: string
  ): Promise<ApiResponse<Inventory>> {
    const response = await inventoryApiClient.post(`/inventory/${inventoryId}/move-location`, null, {
      params: { newLocationCode, newLocationDescription }
    })
    return response.data
  },

  /**
   * 안전재고 미달 재고 조회
   */
  async getLowStockInventories(companyId: number): Promise<ApiResponse<Inventory[]>> {
    const response = await inventoryApiClient.get(`/inventory/companies/${companyId}/low-stock`)
    return response.data
  },

  /**
   * 재고없음 재고 조회
   */
  async getOutOfStockInventories(companyId: number): Promise<ApiResponse<Inventory[]>> {
    const response = await inventoryApiClient.get(`/inventory/companies/${companyId}/out-of-stock`)
    return response.data
  },

  /**
   * 재고 통계 조회
   */
  async getInventoryStats(companyId: number): Promise<ApiResponse<InventoryStats>> {
    const response = await inventoryApiClient.get(`/inventory/companies/${companyId}/statistics`)
    return response.data
  },

  /**
   * ABC 분석
   */
  async performAbcAnalysis(companyId: number): Promise<ApiResponse<AbcAnalysisResult[]>> {
    const response = await inventoryApiClient.get(`/inventory/companies/${companyId}/abc-analysis`)
    return response.data
  },

  /**
   * 재고 알림 대상 조회
   */
  async getInventoryAlerts(companyId: number): Promise<ApiResponse<InventoryAlert[]>> {
    const response = await inventoryApiClient.get(`/inventory/companies/${companyId}/alerts`)
    return response.data
  }
}

/**
 * 재고이동 관리 API 서비스
 */
export const stockMovementApi = {
  /**
   * 재고이동 목록 조회
   */
  async getStockMovements(
    companyId: number, 
    params?: InventorySearchParams
  ): Promise<ApiResponse<PageResponse<StockMovement>>> {
    const response = await inventoryApiClient.get(`/stock-movements/companies/${companyId}`, { params })
    return response.data
  },

  /**
   * 재고이동 상세 조회
   */
  async getStockMovementById(id: number): Promise<ApiResponse<StockMovement>> {
    const response = await inventoryApiClient.get(`/stock-movements/${id}`)
    return response.data
  },

  /**
   * 상품별 재고이동 이력 조회
   */
  async getStockMovementsByProduct(
    productId: number, 
    params?: InventorySearchParams
  ): Promise<ApiResponse<PageResponse<StockMovement>>> {
    const response = await inventoryApiClient.get(`/stock-movements/products/${productId}`, { params })
    return response.data
  },

  /**
   * 창고별 재고이동 이력 조회
   */
  async getStockMovementsByWarehouse(
    warehouseId: number, 
    params?: InventorySearchParams
  ): Promise<ApiResponse<PageResponse<StockMovement>>> {
    const response = await inventoryApiClient.get(`/stock-movements/warehouses/${warehouseId}`, { params })
    return response.data
  }
}

/**
 * 창고 관리 API 서비스
 */
export const warehouseApi = {
  /**
   * 창고 목록 조회
   */
  async getWarehouses(companyId: number): Promise<ApiResponse<Warehouse[]>> {
    const response = await inventoryApiClient.get(`/warehouses/companies/${companyId}`)
    return response.data
  },

  /**
   * 창고 상세 조회
   */
  async getWarehouseById(id: number): Promise<ApiResponse<Warehouse>> {
    const response = await inventoryApiClient.get(`/warehouses/${id}`)
    return response.data
  },

  /**
   * 창고별 재고 현황 조회
   */
  async getWarehouseInventory(warehouseId: number): Promise<ApiResponse<WarehouseInventory>> {
    const response = await inventoryApiClient.get(`/warehouses/${warehouseId}/inventory`)
    return response.data
  }
}

/**
 * 상품분류 관리 API 서비스
 */
export const categoryApi = {
  /**
   * 분류 목록 조회 (계층구조)
   */
  async getCategories(companyId: number): Promise<ApiResponse<Category[]>> {
    const response = await inventoryApiClient.get(`/products/categories/companies/${companyId}`)
    return response.data
  },

  /**
   * 분류 상세 조회
   */
  async getCategoryById(id: number): Promise<ApiResponse<Category>> {
    const response = await inventoryApiClient.get(`/categories/${id}`)
    return response.data
  }
}

/**
 * 대시보드 API 서비스
 */
export const dashboardApi = {
  /**
   * 재고 대시보드 데이터 조회
   */
  async getInventoryDashboard(companyId: number): Promise<ApiResponse<InventoryDashboard>> {
    const response = await inventoryApiClient.get(`/dashboard/inventory/companies/${companyId}`)
    return response.data
  },

  /**
   * 재고 트렌드 데이터 조회
   */
  async getInventoryTrend(companyId: number, days: number = 30): Promise<ApiResponse<any[]>> {
    const response = await inventoryApiClient.get(`/dashboard/inventory/companies/${companyId}/trend`, {
      params: { days }
    })
    return response.data
  },

  /**
   * 창고 활용도 조회
   */
  async getWarehouseUtilization(companyId: number): Promise<ApiResponse<WarehouseInventory[]>> {
    const response = await inventoryApiClient.get(`/dashboard/inventory/companies/${companyId}/warehouse-utilization`)
    return response.data
  }
}

// 기본 내보내기
export default {
  product: productApi,
  inventory: inventoryApi,
  stockMovement: stockMovementApi,
  warehouse: warehouseApi,
  category: categoryApi,
  dashboard: dashboardApi
}

