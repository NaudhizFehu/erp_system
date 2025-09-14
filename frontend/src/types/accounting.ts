/**
 * 회계 모듈 TypeScript 타입 정의
 * 백엔드 DTO와 매칭되는 인터페이스들을 정의합니다
 */

import { ApiResponse, PageResponse } from './common'

/**
 * 계정과목 유형 열거형
 */
export enum AccountType {
  ASSET = 'ASSET',
  LIABILITY = 'LIABILITY',
  EQUITY = 'EQUITY',
  REVENUE = 'REVENUE',
  EXPENSE = 'EXPENSE'
}

/**
 * 계정과목 분류 열거형
 */
export enum AccountCategory {
  // 자산
  CURRENT_ASSET = 'CURRENT_ASSET',
  NON_CURRENT_ASSET = 'NON_CURRENT_ASSET',
  
  // 부채
  CURRENT_LIABILITY = 'CURRENT_LIABILITY',
  NON_CURRENT_LIABILITY = 'NON_CURRENT_LIABILITY',
  
  // 자본
  PAID_IN_CAPITAL = 'PAID_IN_CAPITAL',
  RETAINED_EARNINGS = 'RETAINED_EARNINGS',
  
  // 수익
  OPERATING_REVENUE = 'OPERATING_REVENUE',
  NON_OPERATING_REVENUE = 'NON_OPERATING_REVENUE',
  
  // 비용
  OPERATING_EXPENSE = 'OPERATING_EXPENSE',
  NON_OPERATING_EXPENSE = 'NON_OPERATING_EXPENSE'
}

/**
 * 차대구분 열거형
 */
export enum DebitCreditType {
  DEBIT = 'DEBIT',
  CREDIT = 'CREDIT'
}

/**
 * 거래 유형 열거형
 */
export enum TransactionType {
  JOURNAL = 'JOURNAL',
  SALES = 'SALES',
  PURCHASE = 'PURCHASE',
  CASH_RECEIPT = 'CASH_RECEIPT',
  CASH_PAYMENT = 'CASH_PAYMENT',
  BANK_RECEIPT = 'BANK_RECEIPT',
  BANK_PAYMENT = 'BANK_PAYMENT',
  ADJUSTMENT = 'ADJUSTMENT',
  CLOSING = 'CLOSING'
}

/**
 * 거래 상태 열거형
 */
export enum TransactionStatus {
  DRAFT = 'DRAFT',
  PENDING = 'PENDING',
  APPROVED = 'APPROVED',
  POSTED = 'POSTED',
  CANCELLED = 'CANCELLED'
}

/**
 * 세금 유형 열거형
 */
export enum TaxType {
  VAT_10 = 'VAT_10',
  VAT_0 = 'VAT_0',
  TAX_FREE = 'TAX_FREE',
  WITHHOLDING = 'WITHHOLDING'
}

/**
 * 증빙서류 유형 열거형
 */
export enum DocumentType {
  TAX_INVOICE = 'TAX_INVOICE',
  CASH_RECEIPT = 'CASH_RECEIPT',
  CREDIT_CARD = 'CREDIT_CARD',
  BANK_TRANSFER = 'BANK_TRANSFER',
  PROMISSORY_NOTE = 'PROMISSORY_NOTE',
  RECEIPT = 'RECEIPT',
  CONTRACT = 'CONTRACT',
  OTHER = 'OTHER'
}

/**
 * 예산 기간 열거형
 */
export enum BudgetPeriod {
  ANNUAL = 'ANNUAL',
  QUARTERLY = 'QUARTERLY',
  MONTHLY = 'MONTHLY'
}

/**
 * 예산 유형 열거형
 */
export enum BudgetType {
  REVENUE = 'REVENUE',
  EXPENSE = 'EXPENSE',
  CAPITAL = 'CAPITAL',
  CASH_FLOW = 'CASH_FLOW'
}

/**
 * 예산 상태 열거형
 */
export enum BudgetStatus {
  DRAFT = 'DRAFT',
  SUBMITTED = 'SUBMITTED',
  APPROVED = 'APPROVED',
  ACTIVE = 'ACTIVE',
  CLOSED = 'CLOSED',
  CANCELLED = 'CANCELLED'
}

/**
 * 재무보고서 유형 열거형
 */
export enum ReportType {
  BALANCE_SHEET = 'BALANCE_SHEET',
  INCOME_STATEMENT = 'INCOME_STATEMENT',
  CASH_FLOW_STATEMENT = 'CASH_FLOW_STATEMENT',
  EQUITY_STATEMENT = 'EQUITY_STATEMENT',
  TRIAL_BALANCE = 'TRIAL_BALANCE',
  GENERAL_LEDGER = 'GENERAL_LEDGER',
  BUDGET_REPORT = 'BUDGET_REPORT',
  VARIANCE_ANALYSIS = 'VARIANCE_ANALYSIS',
  AGING_REPORT = 'AGING_REPORT',
  TAX_REPORT = 'TAX_REPORT'
}

/**
 * 보고서 상태 열거형
 */
export enum ReportStatus {
  DRAFT = 'DRAFT',
  GENERATED = 'GENERATED',
  REVIEWED = 'REVIEWED',
  APPROVED = 'APPROVED',
  PUBLISHED = 'PUBLISHED'
}

/**
 * 계정과목 인터페이스
 */
export interface Account {
  id: number
  accountCode: string
  accountName: string
  accountNameEn?: string
  description?: string
  company: {
    id: number
    companyName: string
  }
  accountType: AccountType
  accountCategory: AccountCategory
  debitCreditType: DebitCreditType
  parentAccount?: Account
  accountLevel: number
  sortOrder: number
  isActive: boolean
  trackBalance: boolean
  currentBalance: number
  openingBalance: number
  budgetAmount: number
  taxCode?: string
  controlField1?: string
  controlField2?: string
  fullPath: string
  fullCodePath: string
  isLeafAccount: boolean
  createdAt: string
  updatedAt: string
}

/**
 * 거래 인터페이스
 */
export interface Transaction {
  id: number
  transactionNumber: string
  company: {
    id: number
    companyName: string
  }
  transactionDate: string
  transactionType: TransactionType
  transactionStatus: TransactionStatus
  account: Account
  debitAmount: number
  creditAmount: number
  description?: string
  memo?: string
  fiscalYear: number
  fiscalMonth: number
  fiscalQuarter: number
  businessPartner?: string
  departmentInfo?: string
  projectCode?: string
  taxType?: TaxType
  taxAmount: number
  taxInvoiceNumber?: string
  documentType?: DocumentType
  documentNumber?: string
  attachmentPath?: string
  inputBy?: {
    id: number
    name: string
  }
  approvedBy?: {
    id: number
    name: string
  }
  approvedAt?: string
  cancelReason?: string
  cancelledAt?: string
  originalTransaction?: Transaction
  amount: number
  isDebitTransaction: boolean
  isCreditTransaction: boolean
  createdAt: string
  updatedAt: string
}

/**
 * 예산 인터페이스
 */
export interface Budget {
  id: number
  company: {
    id: number
    companyName: string
  }
  account: Account
  fiscalYear: number
  budgetPeriod: BudgetPeriod
  periodNumber?: number
  budgetType: BudgetType
  budgetStatus: BudgetStatus
  budgetAmount: number
  previousActual: number
  currentActual: number
  achievementRate: number
  varianceAmount: number
  varianceRate: number
  description?: string
  budgetBasis?: string
  responsiblePerson?: {
    id: number
    name: string
  }
  departmentCode?: string
  projectCode?: string
  approvedBy?: {
    id: number
    name: string
  }
  approvedAt?: string
  isOverBudget: boolean
  overBudgetAmount: number
  remainingBudget: number
  progressRate: number
  createdAt: string
  updatedAt: string
}

/**
 * 재무보고서 인터페이스
 */
export interface FinancialReport {
  id: number
  company: {
    id: number
    companyName: string
  }
  reportType: ReportType
  reportTitle: string
  fiscalYear: number
  fiscalPeriod: string
  baseDate: string
  reportStatus: ReportStatus
  reportData?: string
  summaryData?: string
  totalAssets: number
  totalLiabilities: number
  totalEquity: number
  totalRevenue: number
  totalExpenses: number
  netIncome: number
  operatingIncome: number
  incomeBeforeTax: number
  cashAndEquivalents: number
  currentAssets: number
  nonCurrentAssets: number
  currentLiabilities: number
  nonCurrentLiabilities: number
  generatedBy?: {
    id: number
    name: string
  }
  generatedAt?: string
  approvedBy?: {
    id: number
    name: string
  }
  approvedAt?: string
  filePath?: string
  remarks?: string
  reportItems?: FinancialReportItem[]
  // 재무비율
  currentRatio: number
  debtRatio: number
  equityRatio: number
  roa: number
  roe: number
  grossMargin: number
  netMargin: number
  createdAt: string
  updatedAt: string
}

/**
 * 재무보고서 항목 인터페이스
 */
export interface FinancialReportItem {
  id: number
  reportId: number
  account?: Account
  itemName: string
  itemNameEn?: string
  itemCode?: string
  lineNumber: number
  itemLevel: number
  parentItem?: FinancialReportItem
  currentAmount: number
  previousAmount: number
  changeAmount: number
  changeRate: number
  compositionRatio: number
  itemType: string
  calculationFormula?: string
  isVisible: boolean
  isBold: boolean
  indentLevel: number
  note?: string
  isIncrease: boolean
  isDecrease: boolean
  isTotalItem: boolean
  isAccountItem: boolean
  formattedCurrentAmount: string
  formattedPreviousAmount: string
}

/**
 * 거래 생성 요청 인터페이스
 */
export interface TransactionCreateRequest {
  transactionNumber: string
  companyId: number
  transactionDate: string
  transactionType: TransactionType
  accountId: number
  debitAmount: number
  creditAmount: number
  description?: string
  memo?: string
  businessPartner?: string
  departmentInfo?: string
  projectCode?: string
  taxType?: TaxType
  taxAmount?: number
  taxInvoiceNumber?: string
  documentType?: DocumentType
  documentNumber?: string
  attachmentPath?: string
  inputById?: number
}

/**
 * 예산 생성 요청 인터페이스
 */
export interface BudgetCreateRequest {
  companyId: number
  accountId: number
  fiscalYear: number
  budgetPeriod: BudgetPeriod
  periodNumber?: number
  budgetType: BudgetType
  budgetAmount: number
  description?: string
  budgetBasis?: string
  responsiblePersonId?: number
  departmentCode?: string
  projectCode?: string
}

/**
 * 시산표 DTO
 */
export interface TrialBalance {
  accountCode: string
  accountName: string
  accountType: string
  debitAmount: number
  creditAmount: number
  balance: number
}

/**
 * 총계정원장 DTO
 */
export interface GeneralLedger {
  transactionDate: string
  transactionNumber: string
  description?: string
  debitAmount: number
  creditAmount: number
  balance: number
}

/**
 * 대차평형 검증 DTO
 */
export interface BalanceVerification {
  asOfDate: string
  totalAssets: number
  totalLiabilities: number
  totalEquity: number
  totalDebits: number
  totalCredits: number
  balanceDifference: number
  isBalanced: boolean
  message: string
}

/**
 * 거래 통계 DTO
 */
export interface TransactionStatistics {
  startDate: string
  endDate: string
  totalTransactionCount: number
  totalTransactionAmount: number
  totalDebitAmount: number
  totalCreditAmount: number
  transactionCountByType: Record<string, number>
  transactionAmountByType: Record<string, number>
  transactionCountByStatus: Record<string, number>
  dailyTransactionCounts: Record<string, number>
  dailyTransactionAmounts: Record<string, number>
}

/**
 * 검색 파라미터 인터페이스
 */
export interface AccountingSearchParams {
  searchTerm?: string
  page?: number
  size?: number
  sort?: string
  companyId?: number
  accountId?: number
  transactionType?: TransactionType
  transactionStatus?: TransactionStatus
  fiscalYear?: number
  fiscalMonth?: number
  startDate?: string
  endDate?: string
  budgetType?: BudgetType
  budgetStatus?: BudgetStatus
  reportType?: ReportType
  reportStatus?: ReportStatus
}

/**
 * 차트 데이터 인터페이스
 */
export interface ChartData {
  name: string
  value: number
  percentage?: number
  color?: string
}

/**
 * 재무 트렌드 데이터 인터페이스
 */
export interface FinancialTrend {
  period: string
  totalAssets: number
  totalLiabilities: number
  totalEquity: number
  totalRevenue: number
  totalExpenses: number
  netIncome: number
}

/**
 * 재무비율 분석 인터페이스
 */
export interface FinancialRatioAnalysis {
  currentRatio: number
  debtRatio: number
  equityRatio: number
  roa: number
  roe: number
  grossMargin: number
  netMargin: number
}

/**
 * 한국어 라벨 매핑
 */
export const KOREAN_LABELS = {
  // 계정과목 유형
  [AccountType.ASSET]: '자산',
  [AccountType.LIABILITY]: '부채',
  [AccountType.EQUITY]: '자본',
  [AccountType.REVENUE]: '수익',
  [AccountType.EXPENSE]: '비용',

  // 계정과목 분류
  [AccountCategory.CURRENT_ASSET]: '유동자산',
  [AccountCategory.NON_CURRENT_ASSET]: '비유동자산',
  [AccountCategory.CURRENT_LIABILITY]: '유동부채',
  [AccountCategory.NON_CURRENT_LIABILITY]: '비유동부채',
  [AccountCategory.PAID_IN_CAPITAL]: '납입자본',
  [AccountCategory.RETAINED_EARNINGS]: '이익잉여금',
  [AccountCategory.OPERATING_REVENUE]: '영업수익',
  [AccountCategory.NON_OPERATING_REVENUE]: '영업외수익',
  [AccountCategory.OPERATING_EXPENSE]: '영업비용',
  [AccountCategory.NON_OPERATING_EXPENSE]: '영업외비용',

  // 차대구분
  [DebitCreditType.DEBIT]: '차변',
  [DebitCreditType.CREDIT]: '대변',

  // 거래 유형
  [TransactionType.JOURNAL]: '일반분개',
  [TransactionType.SALES]: '매출',
  [TransactionType.PURCHASE]: '매입',
  [TransactionType.CASH_RECEIPT]: '현금수입',
  [TransactionType.CASH_PAYMENT]: '현금지출',
  [TransactionType.BANK_RECEIPT]: '예금수입',
  [TransactionType.BANK_PAYMENT]: '예금지출',
  [TransactionType.ADJUSTMENT]: '수정분개',
  [TransactionType.CLOSING]: '결산분개',

  // 거래 상태
  [TransactionStatus.DRAFT]: '임시저장',
  [TransactionStatus.PENDING]: '승인대기',
  [TransactionStatus.APPROVED]: '승인완료',
  [TransactionStatus.POSTED]: '전기완료',
  [TransactionStatus.CANCELLED]: '취소',

  // 세금 유형
  [TaxType.VAT_10]: '부가세 10%',
  [TaxType.VAT_0]: '부가세 0%',
  [TaxType.TAX_FREE]: '면세',
  [TaxType.WITHHOLDING]: '원천세',

  // 증빙서류 유형
  [DocumentType.TAX_INVOICE]: '세금계산서',
  [DocumentType.CASH_RECEIPT]: '현금영수증',
  [DocumentType.CREDIT_CARD]: '신용카드',
  [DocumentType.BANK_TRANSFER]: '계좌이체',
  [DocumentType.PROMISSORY_NOTE]: '약속어음',
  [DocumentType.RECEIPT]: '영수증',
  [DocumentType.CONTRACT]: '계약서',
  [DocumentType.OTHER]: '기타',

  // 예산 기간
  [BudgetPeriod.ANNUAL]: '연간',
  [BudgetPeriod.QUARTERLY]: '분기',
  [BudgetPeriod.MONTHLY]: '월간',

  // 예산 유형
  [BudgetType.REVENUE]: '수익예산',
  [BudgetType.EXPENSE]: '비용예산',
  [BudgetType.CAPITAL]: '자본예산',
  [BudgetType.CASH_FLOW]: '현금흐름예산',

  // 예산 상태
  [BudgetStatus.DRAFT]: '임시저장',
  [BudgetStatus.SUBMITTED]: '제출',
  [BudgetStatus.APPROVED]: '승인',
  [BudgetStatus.ACTIVE]: '활성',
  [BudgetStatus.CLOSED]: '마감',
  [BudgetStatus.CANCELLED]: '취소',

  // 보고서 유형
  [ReportType.BALANCE_SHEET]: '재무상태표',
  [ReportType.INCOME_STATEMENT]: '손익계산서',
  [ReportType.CASH_FLOW_STATEMENT]: '현금흐름표',
  [ReportType.EQUITY_STATEMENT]: '자본변동표',
  [ReportType.TRIAL_BALANCE]: '시산표',
  [ReportType.GENERAL_LEDGER]: '총계정원장',
  [ReportType.BUDGET_REPORT]: '예산보고서',
  [ReportType.VARIANCE_ANALYSIS]: '차이분석표',
  [ReportType.AGING_REPORT]: '연령분석표',
  [ReportType.TAX_REPORT]: '세무보고서',

  // 보고서 상태
  [ReportStatus.DRAFT]: '임시저장',
  [ReportStatus.GENERATED]: '생성완료',
  [ReportStatus.REVIEWED]: '검토완료',
  [ReportStatus.APPROVED]: '승인완료',
  [ReportStatus.PUBLISHED]: '공시완료'
} as const




