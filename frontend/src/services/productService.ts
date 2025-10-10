import api from './api'

/**
 * 상품 정보 인터페이스
 */
export interface Product {
  id: number
  productCode: string
  productName: string
  productNameEn?: string
  description?: string
  detailedDescription?: string
  companyId: number
  companyName: string
  categoryId: number
  categoryName: string
  categoryFullPath?: string
  productType: 'RAW_MATERIAL' | 'SEMI_FINISHED' | 'FINISHED_GOODS' | 'SERVICE'
  productTypeDescription?: string
  productStatus: 'ACTIVE' | 'INACTIVE' | 'DISCONTINUED'
  productStatusDescription?: string
  stockManagementType?: string
  stockManagementTypeDescription?: string
  isActive: boolean
  trackInventory: boolean
  barcode?: string
  qrCode?: string
  sku?: string
  baseUnit: string
  subUnit?: string
  unitConversionRate?: number
  standardCost?: number
  averageCost?: number
  lastPurchasePrice?: number
  sellingPrice: number
  minSellingPrice?: number
  safetyStock?: number
  minStock?: number
  maxStock?: number
  reorderPoint?: number
  reorderQuantity?: number
  leadTimeDays?: number
  shelfLifeDays?: number
  width?: number
  height?: number
  depth?: number
  weight?: number
  volume?: number
  color?: string
  size?: string
  brand?: string
  manufacturer?: string
  supplier?: string
  originCountry?: string
  hsCode?: string
  taxRate?: number
  imagePaths?: string
  attachmentPaths?: string
  tags?: string
  sortOrder?: number
  metadata?: string
  createdAt: string
  updatedAt?: string
  
  // 재고 관련 정보 (백엔드에서 기본값으로 설정됨)
  quantity?: number
  totalStock?: number
  availableStock?: number
  reservedStock?: number
  isLowStock?: boolean
  isOutOfStock?: boolean
  isOverStock?: boolean
  needsReorder?: boolean
  stockStatusSummary?: string
  profitRate?: number
}

/**
 * 상품 목록 조회 파라미터
 */
export interface ProductListParams {
  page?: number
  size?: number
  search?: string
  categoryId?: number
  status?: string
  type?: string
  lowStock?: boolean
}

/**
 * 상품 목록 응답
 */
export interface ProductListResponse {
  content: Product[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
}

/**
 * 상품 서비스 클래스
 * 상품 관련 API 호출을 담당합니다
 */
class ProductService {
  private baseUrl = '/products'

  /**
   * 상품 목록 조회
   */
  async getProducts(params: ProductListParams = {}): Promise<ProductListResponse> {
    try {
      const response = await api.get(this.baseUrl, { params })
      return response.data
    } catch (error) {
      console.error('상품 목록 조회 오류:', error)
      throw new Error('상품 목록을 불러오는 중 오류가 발생했습니다.')
    }
  }

  /**
   * 상품 상세 정보 조회
   */
  async getProductById(id: number): Promise<Product> {
    try {
      const response = await api.get(`${this.baseUrl}/${id}`)
      // 백엔드 응답 구조: {success: true, message: '...', data: {상품데이터}, ...}
      // response.data.data가 실제 상품 데이터이므로 이를 반환
      return response.data.data
    } catch (error) {
      console.error('상품 상세 정보 조회 오류:', error)
      throw new Error('상품 정보를 불러오는 중 오류가 발생했습니다.')
    }
  }

  /**
   * 상품 생성
   */
  async createProduct(product: Omit<Product, 'id' | 'createdAt' | 'updatedAt'>): Promise<Product> {
    try {
      const response = await api.post(this.baseUrl, product)
      return response.data
    } catch (error) {
      console.error('상품 생성 오류:', error)
      throw new Error('상품 생성 중 오류가 발생했습니다.')
    }
  }

  /**
   * 상품 수정
   */
  async updateProduct(id: number, product: Partial<Product>): Promise<Product> {
    try {
      const response = await api.put(`${this.baseUrl}/${id}`, product)
      return response.data
    } catch (error) {
      console.error('상품 수정 오류:', error)
      throw new Error('상품 수정 중 오류가 발생했습니다.')
    }
  }

  /**
   * 상품 삭제
   */
  async deleteProduct(id: number): Promise<void> {
    try {
      await api.delete(`${this.baseUrl}/${id}`)
    } catch (error) {
      console.error('상품 삭제 오류:', error)
      throw new Error('상품 삭제 중 오류가 발생했습니다.')
    }
  }

  /**
   * 상품 검색
   */
  async searchProducts(searchTerm: string): Promise<Product[]> {
    try {
      const response = await api.get(`${this.baseUrl}/search`, {
        params: { q: searchTerm }
      })
      return response.data
    } catch (error) {
      console.error('상품 검색 오류:', error)
      throw new Error('상품 검색 중 오류가 발생했습니다.')
    }
  }

  /**
   * 재고 부족 상품 조회
   */
  async getLowStockProducts(): Promise<Product[]> {
    try {
      const response = await api.get(`${this.baseUrl}/low-stock`)
      return response.data
    } catch (error) {
      console.error('재고 부족 상품 조회 오류:', error)
      throw new Error('재고 부족 상품 조회 중 오류가 발생했습니다.')
    }
  }
}

export const productService = new ProductService()



