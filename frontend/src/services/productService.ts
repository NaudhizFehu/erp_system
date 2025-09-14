import axios from 'axios'

/**
 * 상품 정보 인터페이스
 */
export interface Product {
  id: number
  productCode: string
  productName: string
  productNameEn?: string
  description?: string
  categoryId: number
  categoryName: string
  productType: 'RAW_MATERIAL' | 'SEMI_FINISHED' | 'FINISHED_GOODS' | 'SERVICE'
  productStatus: 'ACTIVE' | 'INACTIVE' | 'DISCONTINUED'
  barcode?: string
  sku?: string
  baseUnit: string
  sellingPrice: number
  standardCost?: number
  currentStock: number
  safetyStock: number
  reorderPoint: number
  reorderQuantity: number
  supplier?: string
  brand?: string
  manufacturer?: string
  weight?: number
  dimensions?: string
  createdAt: string
  updatedAt?: string
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
  private baseUrl = '/api/products'

  /**
   * 상품 목록 조회
   */
  async getProducts(params: ProductListParams = {}): Promise<ProductListResponse> {
    try {
      const response = await axios.get(this.baseUrl, { params })
      return response.data.data
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
      const response = await axios.get(`${this.baseUrl}/${id}`)
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
      const response = await axios.post(this.baseUrl, product)
      return response.data.data
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
      const response = await axios.put(`${this.baseUrl}/${id}`, product)
      return response.data.data
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
      await axios.delete(`${this.baseUrl}/${id}`)
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
      const response = await axios.get(`${this.baseUrl}/search`, {
        params: { q: searchTerm }
      })
      return response.data.data
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
      const response = await axios.get(`${this.baseUrl}/low-stock`)
      return response.data.data
    } catch (error) {
      console.error('재고 부족 상품 조회 오류:', error)
      throw new Error('재고 부족 상품 조회 중 오류가 발생했습니다.')
    }
  }
}

export const productService = new ProductService()



