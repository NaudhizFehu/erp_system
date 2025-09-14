/**
 * 회계 관리 React Query 훅
 * 회계 관련 데이터 페칭과 상태 관리를 담당합니다
 */

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import toast from 'react-hot-toast'
import { transactionApi, accountApi, accountingApi, reportApi } from '@/services/accountingApi'
import type {
  Transaction,
  TransactionCreateRequest,
  TrialBalance,
  GeneralLedger,
  BalanceVerification,
  TransactionStatistics,
  FinancialReport,
  AccountingSearchParams,
  TransactionType,
  ReportType
} from '@/types/accounting'

/**
 * 쿼리 키 상수
 */
export const ACCOUNTING_QUERY_KEYS = {
  // 거래 관련
  transactions: ['transactions'] as const,
  transactionSearch: (searchTerm: string, params: AccountingSearchParams) => 
    [...ACCOUNTING_QUERY_KEYS.transactions, 'search', searchTerm, params] as const,
  transactionsByCompany: (companyId: number, searchTerm: string, params: AccountingSearchParams) => 
    [...ACCOUNTING_QUERY_KEYS.transactions, 'company', companyId, searchTerm, params] as const,
  transactionNumber: (companyId: number, type: TransactionType) => 
    [...ACCOUNTING_QUERY_KEYS.transactions, 'number', companyId, type] as const,
  transactionStats: (companyId: number, startDate: string, endDate: string) => 
    [...ACCOUNTING_QUERY_KEYS.transactions, 'stats', companyId, startDate, endDate] as const,

  // 계정과목 관련
  accounts: ['accounts'] as const,
  accountBalance: (accountId: number, asOfDate?: string) => 
    [...ACCOUNTING_QUERY_KEYS.accounts, accountId, 'balance', asOfDate] as const,
  generalLedger: (accountId: number, startDate: string, endDate: string) => 
    [...ACCOUNTING_QUERY_KEYS.accounts, accountId, 'ledger', startDate, endDate] as const,

  // 회계 관리
  accounting: ['accounting'] as const,
  trialBalance: (companyId: number, startDate: string, endDate: string) => 
    [...ACCOUNTING_QUERY_KEYS.accounting, 'trial-balance', companyId, startDate, endDate] as const,
  balanceVerification: (companyId: number, asOfDate?: string) => 
    [...ACCOUNTING_QUERY_KEYS.accounting, 'balance-verification', companyId, asOfDate] as const,

  // 보고서 관련
  reports: ['reports'] as const,
  reportsByCompany: (companyId: number) => 
    [...ACCOUNTING_QUERY_KEYS.reports, 'company', companyId] as const,
  reportsByType: (companyId: number, reportType: ReportType) => 
    [...ACCOUNTING_QUERY_KEYS.reports, 'company', companyId, 'type', reportType] as const,
  latestReports: (companyId: number) => 
    [...ACCOUNTING_QUERY_KEYS.reports, 'company', companyId, 'latest'] as const,
  financialTrends: (companyId: number, periods: number) => 
    [...ACCOUNTING_QUERY_KEYS.reports, 'trends', companyId, periods] as const,
  financialRatios: (companyId: number, fiscalYear: number) => 
    [...ACCOUNTING_QUERY_KEYS.reports, 'ratios', companyId, fiscalYear] as const,
  reportSearch: (searchTerm: string, params: AccountingSearchParams) => 
    [...ACCOUNTING_QUERY_KEYS.reports, 'search', searchTerm, params] as const,
  reportStats: (companyId: number, fiscalYear: number) => 
    [...ACCOUNTING_QUERY_KEYS.reports, 'stats', companyId, fiscalYear] as const
}

/**
 * 거래 검색 훅
 */
export function useTransactionSearch(searchTerm: string, params: AccountingSearchParams = {}) {
  return useQuery({
    queryKey: ACCOUNTING_QUERY_KEYS.transactionSearch(searchTerm, params),
    queryFn: () => transactionApi.searchTransactions(searchTerm, params),
    enabled: !!searchTerm && searchTerm.length >= 2,
    staleTime: 2 * 60 * 1000, // 2분
    retry: 2
  })
}

/**
 * 회사별 거래 검색 훅
 */
export function useTransactionsByCompany(companyId: number, searchTerm: string, params: AccountingSearchParams = {}) {
  return useQuery({
    queryKey: ACCOUNTING_QUERY_KEYS.transactionsByCompany(companyId, searchTerm, params),
    queryFn: () => transactionApi.searchTransactionsByCompany(companyId, searchTerm, params),
    enabled: !!companyId && !!searchTerm && searchTerm.length >= 2,
    staleTime: 2 * 60 * 1000,
    retry: 2
  })
}

/**
 * 거래번호 생성 훅
 */
export function useTransactionNumber(companyId: number, transactionType: TransactionType) {
  return useQuery({
    queryKey: ACCOUNTING_QUERY_KEYS.transactionNumber(companyId, transactionType),
    queryFn: () => transactionApi.generateTransactionNumber(companyId, transactionType),
    enabled: !!companyId && !!transactionType,
    staleTime: 0, // 항상 최신 번호 생성
    retry: 1
  })
}

/**
 * 거래 통계 훅
 */
export function useTransactionStatistics(companyId: number, startDate: string, endDate: string) {
  return useQuery({
    queryKey: ACCOUNTING_QUERY_KEYS.transactionStats(companyId, startDate, endDate),
    queryFn: () => transactionApi.getTransactionStatistics(companyId, startDate, endDate),
    enabled: !!companyId && !!startDate && !!endDate,
    staleTime: 10 * 60 * 1000, // 10분
    retry: 3
  })
}

/**
 * 계정과목 잔액 조회 훅
 */
export function useAccountBalance(accountId: number, asOfDate?: string) {
  return useQuery({
    queryKey: ACCOUNTING_QUERY_KEYS.accountBalance(accountId, asOfDate),
    queryFn: () => accountApi.getAccountBalance(accountId, asOfDate),
    enabled: !!accountId,
    staleTime: 5 * 60 * 1000, // 5분
    retry: 3
  })
}

/**
 * 총계정원장 조회 훅
 */
export function useGeneralLedger(accountId: number, startDate: string, endDate: string) {
  return useQuery({
    queryKey: ACCOUNTING_QUERY_KEYS.generalLedger(accountId, startDate, endDate),
    queryFn: () => accountApi.getGeneralLedger(accountId, startDate, endDate),
    enabled: !!accountId && !!startDate && !!endDate,
    staleTime: 10 * 60 * 1000,
    retry: 3
  })
}

/**
 * 시산표 생성 훅
 */
export function useTrialBalance(companyId: number, startDate: string, endDate: string) {
  return useQuery({
    queryKey: ACCOUNTING_QUERY_KEYS.trialBalance(companyId, startDate, endDate),
    queryFn: () => accountingApi.generateTrialBalance(companyId, startDate, endDate),
    enabled: !!companyId && !!startDate && !!endDate,
    staleTime: 10 * 60 * 1000,
    retry: 3
  })
}

/**
 * 대차평형 검증 훅
 */
export function useBalanceVerification(companyId: number, asOfDate?: string) {
  return useQuery({
    queryKey: ACCOUNTING_QUERY_KEYS.balanceVerification(companyId, asOfDate),
    queryFn: () => accountingApi.verifyBalance(companyId, asOfDate),
    enabled: !!companyId,
    staleTime: 5 * 60 * 1000,
    retry: 3
  })
}

/**
 * 회사별 보고서 목록 훅
 */
export function useReportsByCompany(companyId: number) {
  return useQuery({
    queryKey: ACCOUNTING_QUERY_KEYS.reportsByCompany(companyId),
    queryFn: () => reportApi.getReportsByCompany(companyId),
    enabled: !!companyId,
    staleTime: 10 * 60 * 1000,
    retry: 3
  })
}

/**
 * 보고서 유형별 조회 훅
 */
export function useReportsByType(companyId: number, reportType: ReportType) {
  return useQuery({
    queryKey: ACCOUNTING_QUERY_KEYS.reportsByType(companyId, reportType),
    queryFn: () => reportApi.getReportsByType(companyId, reportType),
    enabled: !!companyId && !!reportType,
    staleTime: 10 * 60 * 1000,
    retry: 3
  })
}

/**
 * 최신 재무제표 조회 훅
 */
export function useLatestFinancialStatements(companyId: number) {
  return useQuery({
    queryKey: ACCOUNTING_QUERY_KEYS.latestReports(companyId),
    queryFn: () => reportApi.getLatestFinancialStatements(companyId),
    enabled: !!companyId,
    staleTime: 30 * 60 * 1000, // 30분
    retry: 3
  })
}

/**
 * 재무 트렌드 데이터 훅
 */
export function useFinancialTrends(companyId: number, periods: number = 12) {
  return useQuery({
    queryKey: ACCOUNTING_QUERY_KEYS.financialTrends(companyId, periods),
    queryFn: () => reportApi.getFinancialTrends(companyId, periods),
    enabled: !!companyId,
    staleTime: 30 * 60 * 1000,
    retry: 3
  })
}

/**
 * 재무비율 분석 훅
 */
export function useFinancialRatioAnalysis(companyId: number, fiscalYear: number) {
  return useQuery({
    queryKey: ACCOUNTING_QUERY_KEYS.financialRatios(companyId, fiscalYear),
    queryFn: () => reportApi.getFinancialRatioAnalysis(companyId, fiscalYear),
    enabled: !!companyId && !!fiscalYear,
    staleTime: 30 * 60 * 1000,
    retry: 3
  })
}

/**
 * 보고서 검색 훅
 */
export function useReportSearch(searchTerm: string, params: AccountingSearchParams = {}) {
  return useQuery({
    queryKey: ACCOUNTING_QUERY_KEYS.reportSearch(searchTerm, params),
    queryFn: () => reportApi.searchReports(searchTerm, params),
    enabled: !!searchTerm && searchTerm.length >= 2,
    staleTime: 5 * 60 * 1000,
    retry: 2
  })
}

/**
 * 보고서 통계 훅
 */
export function useReportStatistics(companyId: number, fiscalYear: number) {
  return useQuery({
    queryKey: ACCOUNTING_QUERY_KEYS.reportStats(companyId, fiscalYear),
    queryFn: () => reportApi.getReportStatistics(companyId, fiscalYear),
    enabled: !!companyId && !!fiscalYear,
    staleTime: 30 * 60 * 1000,
    retry: 3
  })
}

// 뮤테이션 훅들

/**
 * 복식부기 분개 생성 뮤테이션
 */
export function useCreateJournalEntry() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (journalEntries: TransactionCreateRequest[]) => 
      transactionApi.createJournalEntry(journalEntries),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ACCOUNTING_QUERY_KEYS.transactions })
      queryClient.invalidateQueries({ queryKey: ACCOUNTING_QUERY_KEYS.accounting })
      toast.success('복식부기 분개가 성공적으로 생성되었습니다')
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || '복식부기 분개 생성에 실패했습니다'
      toast.error(message)
    }
  })
}

/**
 * 거래 생성 뮤테이션
 */
export function useCreateTransaction() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (transaction: TransactionCreateRequest) => 
      transactionApi.createTransaction(transaction),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ACCOUNTING_QUERY_KEYS.transactions })
      queryClient.invalidateQueries({ queryKey: ACCOUNTING_QUERY_KEYS.accounts })
      toast.success('거래가 성공적으로 생성되었습니다')
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || '거래 생성에 실패했습니다'
      toast.error(message)
    }
  })
}

/**
 * 거래 수정 뮤테이션
 */
export function useUpdateTransaction() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, transaction }: { id: number; transaction: TransactionCreateRequest }) => 
      transactionApi.updateTransaction(id, transaction),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ACCOUNTING_QUERY_KEYS.transactions })
      queryClient.invalidateQueries({ queryKey: ACCOUNTING_QUERY_KEYS.accounts })
      toast.success('거래가 성공적으로 수정되었습니다')
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || '거래 수정에 실패했습니다'
      toast.error(message)
    }
  })
}

/**
 * 거래 승인 뮤테이션
 */
export function useApproveTransaction() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, approverId }: { id: number; approverId: number }) => 
      transactionApi.approveTransaction(id, approverId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ACCOUNTING_QUERY_KEYS.transactions })
      toast.success('거래가 성공적으로 승인되었습니다')
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || '거래 승인에 실패했습니다'
      toast.error(message)
    }
  })
}

/**
 * 거래 전기 뮤테이션
 */
export function usePostTransaction() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => transactionApi.postTransaction(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ACCOUNTING_QUERY_KEYS.transactions })
      queryClient.invalidateQueries({ queryKey: ACCOUNTING_QUERY_KEYS.accounts })
      queryClient.invalidateQueries({ queryKey: ACCOUNTING_QUERY_KEYS.accounting })
      toast.success('거래가 성공적으로 전기되었습니다')
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || '거래 전기에 실패했습니다'
      toast.error(message)
    }
  })
}

/**
 * 거래 취소 뮤테이션
 */
export function useCancelTransaction() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, reason, cancelById }: { id: number; reason: string; cancelById: number }) => 
      transactionApi.cancelTransaction(id, reason, cancelById),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ACCOUNTING_QUERY_KEYS.transactions })
      toast.success('거래가 성공적으로 취소되었습니다')
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || '거래 취소에 실패했습니다'
      toast.error(message)
    }
  })
}

/**
 * 재무상태표 생성 뮤테이션
 */
export function useGenerateBalanceSheet() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ companyId, fiscalYear, fiscalPeriod, baseDate }: 
      { companyId: number; fiscalYear: number; fiscalPeriod: string; baseDate: string }) => 
      reportApi.generateBalanceSheet(companyId, fiscalYear, fiscalPeriod, baseDate),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ACCOUNTING_QUERY_KEYS.reports })
      toast.success('재무상태표가 성공적으로 생성되었습니다')
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || '재무상태표 생성에 실패했습니다'
      toast.error(message)
    }
  })
}

/**
 * 손익계산서 생성 뮤테이션
 */
export function useGenerateIncomeStatement() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ companyId, fiscalYear, fiscalPeriod, startDate, endDate }: 
      { companyId: number; fiscalYear: number; fiscalPeriod: string; startDate: string; endDate: string }) => 
      reportApi.generateIncomeStatement(companyId, fiscalYear, fiscalPeriod, startDate, endDate),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ACCOUNTING_QUERY_KEYS.reports })
      toast.success('손익계산서가 성공적으로 생성되었습니다')
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || '손익계산서 생성에 실패했습니다'
      toast.error(message)
    }
  })
}

/**
 * 보고서 승인 뮤테이션
 */
export function useApproveReport() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ reportId, approverId }: { reportId: number; approverId: number }) => 
      reportApi.approveReport(reportId, approverId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ACCOUNTING_QUERY_KEYS.reports })
      toast.success('보고서가 성공적으로 승인되었습니다')
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || '보고서 승인에 실패했습니다'
      toast.error(message)
    }
  })
}

/**
 * 보고서 삭제 뮤테이션
 */
export function useDeleteReport() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (reportId: number) => reportApi.deleteReport(reportId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ACCOUNTING_QUERY_KEYS.reports })
      toast.success('보고서가 성공적으로 삭제되었습니다')
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || '보고서 삭제에 실패했습니다'
      toast.error(message)
    }
  })
}

/**
 * 회계기간 마감 뮤테이션
 */
export function useCloseFiscalPeriod() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ companyId, fiscalYear, fiscalMonth }: 
      { companyId: number; fiscalYear: number; fiscalMonth: number }) => 
      accountingApi.closeFiscalPeriod(companyId, fiscalYear, fiscalMonth),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ACCOUNTING_QUERY_KEYS.accounting })
      queryClient.invalidateQueries({ queryKey: ACCOUNTING_QUERY_KEYS.accounts })
      toast.success('회계기간이 성공적으로 마감되었습니다')
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || '회계기간 마감에 실패했습니다'
      toast.error(message)
    }
  })
}

/**
 * 회계연도 마감 뮤테이션
 */
export function useCloseFiscalYear() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ companyId, fiscalYear }: { companyId: number; fiscalYear: number }) => 
      accountingApi.closeFiscalYear(companyId, fiscalYear),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ACCOUNTING_QUERY_KEYS.accounting })
      queryClient.invalidateQueries({ queryKey: ACCOUNTING_QUERY_KEYS.accounts })
      toast.success('회계연도가 성공적으로 마감되었습니다')
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || '회계연도 마감에 실패했습니다'
      toast.error(message)
    }
  })
}

