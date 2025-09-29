import api from './api'

/**
 * 직급 정보 인터페이스
 */
export interface Position {
  id: number
  name: string
  nameEn?: string
  positionCode: string
  description?: string
  level: number
  company: {
    id: number
    name: string
  }
  isActive: boolean
  createdAt: string
  updatedAt: string
}

/**
 * 직급 서비스
 */
class PositionService {
  private baseUrl = '/positions'

  /**
   * 모든 직급 목록 조회 (간단한 형태)
   */
  async getAllPositions(): Promise<{ id: number; name: string }[]> {
    try {
      console.log('직급 목록 조회 API 호출:', `${this.baseUrl}`)
      const response = await api.get(`${this.baseUrl}?page=0&size=1000`)
      console.log('직급 목록 조회 API 응답:', response)
      
      // 응답에서 직급 목록 추출 (Page 객체에서 content 배열 추출)
      const pageData = response.data?.data || response.data
      const positions = pageData?.content || []
      
      if (!Array.isArray(positions)) {
        console.error('직급 목록이 배열이 아닙니다:', positions)
        return []
      }
      
      return positions.map((pos: Position) => ({
        id: pos.id,
        name: pos.name
      }))
    } catch (error) {
      console.error('직급 목록 조회 오류:', error)
      throw new Error('직급 목록을 불러오는 중 오류가 발생했습니다.')
    }
  }

  /**
   * 특정 회사의 직급 목록 조회
   */
  async getPositionsByCompany(companyId: number): Promise<{ id: number; name: string }[]> {
    try {
      console.log('회사별 직급 목록 조회 API 호출:', `${this.baseUrl}/company/${companyId}`)
      const response = await api.get(`${this.baseUrl}/company/${companyId}?page=0&size=1000`)
      console.log('회사별 직급 목록 조회 API 응답:', response)
      
      // 응답에서 직급 목록 추출 (Page 객체에서 content 배열 추출)
      const pageData = response.data?.data || response.data
      const positions = pageData?.content || []
      
      if (!Array.isArray(positions)) {
        console.error('회사별 직급 목록이 배열이 아닙니다:', positions)
        return []
      }
      
      return positions.map((pos: Position) => ({
        id: pos.id,
        name: pos.name
      }))
    } catch (error) {
      console.error('회사별 직급 목록 조회 오류:', error)
      throw new Error('직급 목록을 불러오는 중 오류가 발생했습니다.')
    }
  }
}

export const positionService = new PositionService()
