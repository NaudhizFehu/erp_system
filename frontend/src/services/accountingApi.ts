/**
 * 회계 모듈 API 서비스
 * 백엔드 REST API와 통신하는 함수들을 정의합니다
 */

import api from './api'
import type { ApiResponse, PageResponse } from '@/types/common'
import type {
  Account,
  Transaction,
  Budget,
  FinancialReport,
  TransactionCreateRequest,
  BudgetCreateRequest,
  TrialBalance,
  GeneralLedger,
  BalanceVerification,
  TransactionStatistics,
  AccountingSearchParams,
  ChartData,
  FinancialTrend,
  FinancialRatioAnalysis,
  TransactionType,
  ReportType
} from '@/types/accounting'

// API 기본 설정
const ACCOUNTING_API_BASE = '/api/accounting'
const REPORTS_API_BASE = '/api/accounting/reports'

/**
 * 거래 관리 API 서비스
 */
export const transactionApi = {
  /**
   * 복식부기 분개 생성
   */
  createJournalEntry: async (journalEntries: TransactionCreateRequest[]): Promise<Transaction[]> => {
    const { data } = await api.post<ApiResponse<Transaction[]>>(
      `${ACCOUNTING_API_BASE}/journal-entries`, 
      journalEntries
    )
    return data.data
  },

  /**
   * 단일 거래 생성
   */
  createTransaction: async (transaction: TransactionCreateRequest): Promise<Transaction> => {
    const { data } = await api.post<ApiResponse<Transaction>>(
      `${ACCOUNTING_API_BASE}/transactions`, 
      transaction
    )
    return data.data
  },

  /**
   * 거래 수정
   */
  updateTransaction: async (id: number, transaction: TransactionCreateRequest): Promise<Transaction> => {
    const { data } = await api.put<ApiResponse<Transaction>>(
      `${ACCOUNTING_API_BASE}/transactions/${id}`, 
      transaction
    )
    return data.data
  },

  /**
   * 거래 승인
   */
  approveTransaction: async (id: number, approverId: number): Promise<Transaction> => {
    const { data } = await api.post<ApiResponse<Transaction>>(
      `${ACCOUNTING_API_BASE}/transactions/${id}/approve`,
      null,
      { params: { approverId } }
    )
    return data.data
  },

  /**
   * 거래 전기
   */
  postTransaction: async (id: number): Promise<Transaction> => {
    const { data } = await api.post<ApiResponse<Transaction>>(
      `${ACCOUNTING_API_BASE}/transactions/${id}/post`
    )
    return data.data
  },

  /**
   * 거래 취소
   */
  cancelTransaction: async (id: number, reason: string, cancelById: number): Promise<void> => {
    await api.post<ApiResponse<void>>(
      `${ACCOUNTING_API_BASE}/transactions/${id}/cancel`,
      null,
      { params: { reason, cancelById } }
    )
  },

  /**
   * 수정분개 생성
   */
  createAdjustingEntry: async (originalId: number, newTransaction: TransactionCreateRequest): Promise<Transaction[]> => {
    const { data } = await api.post<ApiResponse<Transaction[]>>(
      `${ACCOUNTING_API_BASE}/transactions/${originalId}/adjusting-entry`,
      newTransaction
    )
    return data.data
  },

  /**
   * 거래 검색
   */
  searchTransactions: async (searchTerm: string, params: AccountingSearchParams = {}): Promise<PageResponse<Transaction>> => {
    const { data } = await api.get<ApiResponse<PageResponse<Transaction>>>(
      `${ACCOUNTING_API_BASE}/transactions/search`,
      {
        params: {
          searchTerm,
          page: params.page || 0,
          size: params.size || 20,
          sort: params.sort || 'transactionDate,desc'
        }
      }
    )
    return data.data
  },

  /**
   * 회사별 거래 검색
   */
  searchTransactionsByCompany: async (companyId: number, searchTerm: string, params: AccountingSearchParams = {}): Promise<PageResponse<Transaction>> => {
    const { data } = await api.get<ApiResponse<PageResponse<Transaction>>>(
      `${ACCOUNTING_API_BASE}/companies/${companyId}/transactions/search`,
      {
        params: {
          searchTerm,
          page: params.page || 0,
          size: params.size || 20,
          sort: params.sort || 'transactionDate,desc'
        }
      }
    )
    return data.data
  },

  /**
   * 거래번호 자동 생성
   */
  generateTransactionNumber: async (companyId: number, transactionType: TransactionType): Promise<string> => {
    const { data } = await api.get<ApiResponse<string>>(
      `${ACCOUNTING_API_BASE}/companies/${companyId}/generate-transaction-number`,
      { params: { transactionType } }
    )
    return data.data
  },

  /**
   * 거래 통계
   */
  getTransactionStatistics: async (companyId: number, startDate: string, endDate: string): Promise<TransactionStatistics> => {
    const { data } = await api.get<ApiResponse<TransactionStatistics>>(
      `${ACCOUNTING_API_BASE}/companies/${companyId}/transaction-statistics`,
      { params: { startDate, endDate } }
    )
    return data.data
  }
}

/**
 * 계정과목 관리 API 서비스
 */
export const accountApi = {
  /**
   * 계정과목 잔액 조회
   */
  getAccountBalance: async (accountId: number, asOfDate?: string): Promise<number> => {
    const { data } = await api.get<ApiResponse<number>>(
      `${ACCOUNTING_API_BASE}/accounts/${accountId}/balance`,
      { params: asOfDate ? { asOfDate } : {} }
    )
    return data.data
  },

  /**
   * 계정과목 잔액 업데이트
   */
  updateAccountBalance: async (accountId: number): Promise<void> => {
    await api.post<ApiResponse<void>>(
      `${ACCOUNTING_API_BASE}/accounts/${accountId}/update-balance`
    )
  },

  /**
   * 총계정원장 조회
   */
  getGeneralLedger: async (accountId: number, startDate: string, endDate: string): Promise<GeneralLedger[]> => {
    const { data } = await api.get<ApiResponse<GeneralLedger[]>>(
      `${ACCOUNTING_API_BASE}/accounts/${accountId}/general-ledger`,
      { params: { startDate, endDate } }
    )
    return data.data
  }
}

/**
 * 회계 관리 API 서비스
 */
export const accountingApi = {
  /**
   * 시산표 생성
   */
  generateTrialBalance: async (companyId: number, startDate: string, endDate: string): Promise<TrialBalance[]> => {
    const { data } = await api.get<ApiResponse<TrialBalance[]>>(
      `${ACCOUNTING_API_BASE}/companies/${companyId}/trial-balance`,
      { params: { startDate, endDate } }
    )
    return data.data
  },

  /**
   * 대차평형 검증
   */
  verifyBalance: async (companyId: number, asOfDate?: string): Promise<BalanceVerification> => {
    const { data } = await api.get<ApiResponse<BalanceVerification>>(
      `${ACCOUNTING_API_BASE}/companies/${companyId}/balance-verification`,
      { params: asOfDate ? { asOfDate } : {} }
    )
    return data.data
  },

  /**
   * 회계기간 마감
   */
  closeFiscalPeriod: async (companyId: number, fiscalYear: number, fiscalMonth: number): Promise<void> => {
    await api.post<ApiResponse<void>>(
      `${ACCOUNTING_API_BASE}/companies/${companyId}/close-period`,
      null,
      { params: { fiscalYear, fiscalMonth } }
    )
  },

  /**
   * 회계연도 마감
   */
  closeFiscalYear: async (companyId: number, fiscalYear: number): Promise<void> => {
    await api.post<ApiResponse<void>>(
      `${ACCOUNTING_API_BASE}/companies/${companyId}/close-year`,
      null,
      { params: { fiscalYear } }
    )
  }
}

/**
 * 재무보고서 관리 API 서비스
 */
export const reportApi = {
  /**
   * 재무상태표 생성
   */
  generateBalanceSheet: async (companyId: number, fiscalYear: number, fiscalPeriod: string, baseDate: string): Promise<FinancialReport> => {
    const { data } = await api.post<ApiResponse<FinancialReport>>(
      `${REPORTS_API_BASE}/balance-sheet`,
      null,
      { params: { companyId, fiscalYear, fiscalPeriod, baseDate } }
    )
    return data.data
  },

  /**
   * 손익계산서 생성
   */
  generateIncomeStatement: async (companyId: number, fiscalYear: number, fiscalPeriod: string, startDate: string, endDate: string): Promise<FinancialReport> => {
    const { data } = await api.post<ApiResponse<FinancialReport>>(
      `${REPORTS_API_BASE}/income-statement`,
      null,
      { params: { companyId, fiscalYear, fiscalPeriod, startDate, endDate } }
    )
    return data.data
  },

  /**
   * 현금흐름표 생성
   */
  generateCashFlowStatement: async (companyId: number, fiscalYear: number, fiscalPeriod: string, startDate: string, endDate: string): Promise<FinancialReport> => {
    const { data } = await api.post<ApiResponse<FinancialReport>>(
      `${REPORTS_API_BASE}/cash-flow-statement`,
      null,
      { params: { companyId, fiscalYear, fiscalPeriod, startDate, endDate } }
    )
    return data.data
  },

  /**
   * 시산표 보고서 생성
   */
  generateTrialBalanceReport: async (companyId: number, fiscalYear: number, fiscalPeriod: string, startDate: string, endDate: string): Promise<FinancialReport> => {
    const { data } = await api.post<ApiResponse<FinancialReport>>(
      `${REPORTS_API_BASE}/trial-balance`,
      null,
      { params: { companyId, fiscalYear, fiscalPeriod, startDate, endDate } }
    )
    return data.data
  },

  /**
   * 예산보고서 생성
   */
  generateBudgetReport: async (companyId: number, fiscalYear: number, fiscalPeriod: string): Promise<FinancialReport> => {
    const { data } = await api.post<ApiResponse<FinancialReport>>(
      `${REPORTS_API_BASE}/budget-report`,
      null,
      { params: { companyId, fiscalYear, fiscalPeriod } }
    )
    return data.data
  },

  /**
   * 보고서 승인
   */
  approveReport: async (reportId: number, approverId: number): Promise<FinancialReport> => {
    const { data } = await api.post<ApiResponse<FinancialReport>>(
      `${REPORTS_API_BASE}/${reportId}/approve`,
      null,
      { params: { approverId } }
    )
    return data.data
  },

  /**
   * 보고서 재생성
   */
  regenerateReport: async (reportId: number): Promise<FinancialReport> => {
    const { data } = await api.post<ApiResponse<FinancialReport>>(
      `${REPORTS_API_BASE}/${reportId}/regenerate`
    )
    return data.data
  },

  /**
   * 보고서 삭제
   */
  deleteReport: async (reportId: number): Promise<void> => {
    await api.delete<ApiResponse<void>>(`${REPORTS_API_BASE}/${reportId}`)
  },

  /**
   * 회사별 보고서 목록 조회
   */
  getReportsByCompany: async (companyId: number): Promise<FinancialReport[]> => {
    const { data } = await api.get<ApiResponse<FinancialReport[]>>(
      `${REPORTS_API_BASE}/companies/${companyId}`
    )
    return data.data
  },

  /**
   * 보고서 유형별 조회
   */
  getReportsByType: async (companyId: number, reportType: ReportType): Promise<FinancialReport[]> => {
    const { data } = await api.get<ApiResponse<FinancialReport[]>>(
      `${REPORTS_API_BASE}/companies/${companyId}/type/${reportType}`
    )
    return data.data
  },

  /**
   * 최신 재무제표 조회
   */
  getLatestFinancialStatements: async (companyId: number): Promise<Record<string, FinancialReport>> => {
    const { data } = await api.get<ApiResponse<Record<string, FinancialReport>>>(
      `${REPORTS_API_BASE}/companies/${companyId}/latest`
    )
    return data.data
  },

  /**
   * 재무 트렌드 데이터 조회
   */
  getFinancialTrends: async (companyId: number, periods: number = 12): Promise<any> => {
    const { data } = await api.get<ApiResponse<any>>(
      `${REPORTS_API_BASE}/companies/${companyId}/trends`,
      { params: { periods } }
    )
    return data.data
  },

  /**
   * 재무비율 분석
   */
  getFinancialRatioAnalysis: async (companyId: number, fiscalYear: number): Promise<FinancialRatioAnalysis> => {
    const { data } = await api.get<ApiResponse<FinancialRatioAnalysis>>(
      `${REPORTS_API_BASE}/companies/${companyId}/ratios`,
      { params: { fiscalYear } }
    )
    return data.data
  },

  /**
   * 보고서 검색
   */
  searchReports: async (searchTerm: string, params: AccountingSearchParams = {}): Promise<PageResponse<FinancialReport>> => {
    const { data } = await api.get<ApiResponse<PageResponse<FinancialReport>>>(
      `${REPORTS_API_BASE}/search`,
      {
        params: {
          searchTerm,
          page: params.page || 0,
          size: params.size || 20,
          sort: params.sort || 'fiscalYear,desc'
        }
      }
    )
    return data.data
  },

  /**
   * 회사별 보고서 검색
   */
  searchReportsByCompany: async (companyId: number, searchTerm: string, params: AccountingSearchParams = {}): Promise<PageResponse<FinancialReport>> => {
    const { data } = await api.get<ApiResponse<PageResponse<FinancialReport>>>(
      `${REPORTS_API_BASE}/companies/${companyId}/search`,
      {
        params: {
          searchTerm,
          page: params.page || 0,
          size: params.size || 20,
          sort: params.sort || 'fiscalYear,desc'
        }
      }
    )
    return data.data
  },

  /**
   * 보고서 통계
   */
  getReportStatistics: async (companyId: number, fiscalYear: number): Promise<any> => {
    const { data } = await api.get<ApiResponse<any>>(
      `${REPORTS_API_BASE}/companies/${companyId}/statistics`,
      { params: { fiscalYear } }
    )
    return data.data
  }
}

/**
 * 공통 유틸리티 함수들
 */
export const accountingUtils = {
  /**
   * 금액을 한국어 형식으로 포맷팅
   */
  formatCurrency: (amount: number): string => {
    return new Intl.NumberFormat('ko-KR', {
      style: 'currency',
      currency: 'KRW'
    }).format(amount)
  },

  /**
   * 숫자를 콤마 형식으로 포맷팅
   */
  formatNumber: (value: number): string => {
    return new Intl.NumberFormat('ko-KR').format(value)
  },

  /**
   * 퍼센트 포맷팅
   */
  formatPercentage: (value: number, decimals: number = 2): string => {
    return `${value.toFixed(decimals)}%`
  },

  /**
   * 날짜를 한국어 형식으로 포맷팅
   */
  formatDate: (date: string): string => {
    return new Intl.DateTimeFormat('ko-KR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    }).format(new Date(date))
  },

  /**
   * 회계기간 포맷팅
   */
  formatFiscalPeriod: (year: number, period: string): string => {
    return `${year}년 ${period}`
  },

  /**
   * 재무비율 색상 결정
   */
  getRatioColor: (ratio: number, type: 'current' | 'debt' | 'equity' | 'roa' | 'roe'): string => {
    switch (type) {
      case 'current':
        return ratio >= 2.0 ? 'text-green-600' : ratio >= 1.0 ? 'text-yellow-600' : 'text-red-600'
      case 'debt':
        return ratio <= 30 ? 'text-green-600' : ratio <= 50 ? 'text-yellow-600' : 'text-red-600'
      case 'equity':
        return ratio >= 70 ? 'text-green-600' : ratio >= 50 ? 'text-yellow-600' : 'text-red-600'
      case 'roa':
      case 'roe':
        return ratio >= 10 ? 'text-green-600' : ratio >= 5 ? 'text-yellow-600' : 'text-red-600'
      default:
        return 'text-gray-600'
    }
  },

  /**
   * 거래 상태 색상 결정
   */
  getTransactionStatusColor: (status: string): string => {
    switch (status) {
      case 'DRAFT': return 'text-gray-600'
      case 'PENDING': return 'text-yellow-600'
      case 'APPROVED': return 'text-blue-600'
      case 'POSTED': return 'text-green-600'
      case 'CANCELLED': return 'text-red-600'
      default: return 'text-gray-600'
    }
  },

  /**
   * 예산 상태 색상 결정
   */
  getBudgetStatusColor: (status: string): string => {
    switch (status) {
      case 'DRAFT': return 'text-gray-600'
      case 'SUBMITTED': return 'text-yellow-600'
      case 'APPROVED': return 'text-blue-600'
      case 'ACTIVE': return 'text-green-600'
      case 'CLOSED': return 'text-purple-600'
      case 'CANCELLED': return 'text-red-600'
      default: return 'text-gray-600'
    }
  },

  /**
   * 예산 달성률 색상 결정
   */
  getAchievementRateColor: (rate: number): string => {
    if (rate >= 100) return 'text-red-600'
    if (rate >= 80) return 'text-yellow-600'
    return 'text-green-600'
  }
}

