import axios, { AxiosResponse } from 'axios'
import toast from 'react-hot-toast'

/**
 * API 응답 타입 정의
 * 백엔드의 ApiResponse와 일치하는 구조
 */
export interface ApiResponse<T = any> {
  success: boolean
  message: string
  data: T
  errorCode?: string
}

/**
 * Axios 인스턴스 생성
 * 기본 설정 및 인터셉터를 포함합니다
 */
const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

/**
 * 요청 인터셉터
 * 모든 요청에 공통 헤더나 인증 토큰을 추가할 수 있습니다
 */
api.interceptors.request.use(
  (config) => {
    // 인증 토큰 추가
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

/**
 * 응답 인터셉터
 * 공통 에러 처리 및 응답 데이터 변환을 수행합니다
 */
api.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    // 성공 응답인 경우 data 필드만 반환
    return response.data
  },
  (error) => {
    // 에러 응답 처리
    if (error.response?.data?.message) {
      // 백엔드에서 제공하는 한국어 에러 메시지 표시
      toast.error(error.response.data.message)
    } else if (error.code === 'ECONNABORTED') {
      toast.error('요청 시간이 초과되었습니다')
    } else if (error.code === 'ERR_NETWORK') {
      toast.error('네트워크 연결을 확인해주세요')
    } else {
      toast.error('알 수 없는 오류가 발생했습니다')
    }
    
    return Promise.reject(error)
  }
)

export default api


