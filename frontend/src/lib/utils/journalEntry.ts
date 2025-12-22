import {
  JournalEntryLine,
  Transaction,
  TransactionCreateRequest,
} from '@/types/accounting'

/**
 * 폼 데이터 → API 요청 변환
 * 여러 분개 라인을 개별 Transaction 생성 요청으로 변환
 */
export function formToApiRequest(formData: {
  companyId: number
  transactionDate: string
  transactionType: string
  transactionNumber?: string
  inputById?: number
  lines: JournalEntryLine[]
}): TransactionCreateRequest[] {
  return formData.lines.map(line => ({
    transactionNumber: formData.transactionNumber || '', // 비어있으면 백엔드 자동 생성
    companyId: formData.companyId,
    transactionDate: formData.transactionDate,
    transactionType: formData.transactionType as any,
    accountId: line.accountId!,
    debitAmount: line.debitAmount,
    creditAmount: line.creditAmount,
    description: line.description || '',
    memo: line.memo,
    businessPartner: line.businessPartner,
    departmentInfo: line.departmentInfo,
    projectCode: line.projectCode,
    taxType: line.taxType,
    taxAmount: line.taxAmount,
    documentType: line.documentType,
    documentNumber: line.documentNumber,
    inputById: formData.inputById,
  }))
}

/**
 * API 데이터 → 폼 데이터 변환
 * 복식부기로 그룹화된 Transaction들을 폼 편집용 구조로 변환
 */
export function apiToFormData(transactions: Transaction[]): {
  companyId: number
  transactionDate: string
  transactionType: string
  transactionNumber?: string
  inputById?: number
  lines: JournalEntryLine[]
} {
  if (transactions.length === 0) {
    throw new Error('전표 항목이 없습니다')
  }

  const first = transactions[0]

  return {
    companyId: first.company.id,
    transactionDate: first.transactionDate,
    transactionType: first.transactionType,
    transactionNumber: first.transactionNumber,
    inputById: first.inputBy?.id,
    lines: transactions.map(tx => ({
      id: tx.id.toString(),
      accountId: tx.account.id,
      account: tx.account,
      debitAmount: tx.debitAmount,
      creditAmount: tx.creditAmount,
      description: tx.description || '',
      memo: tx.memo,
      businessPartner: tx.businessPartner,
      departmentInfo: tx.departmentInfo,
      projectCode: tx.projectCode,
      taxType: tx.taxType,
      taxAmount: tx.taxAmount,
      documentType: tx.documentType,
      documentNumber: tx.documentNumber,
    })),
  }
}

/**
 * 차변/대변 합계 계산
 * 복식부기 균형 검증용
 */
export function calculateTotals(lines: JournalEntryLine[]) {
  const totalDebit = lines.reduce(
    (sum, line) => sum + (line.debitAmount || 0),
    0
  )
  const totalCredit = lines.reduce(
    (sum, line) => sum + (line.creditAmount || 0),
    0
  )
  const balance = totalDebit - totalCredit
  const isBalanced = Math.abs(balance) < 0.01 // 부동소수점 허용 오차

  return { totalDebit, totalCredit, balance, isBalanced }
}

/**
 * 빈 분개 라인 생성
 * 폼에서 새 행 추가 시 사용
 */
export function createEmptyLine(): JournalEntryLine {
  return {
    id: crypto.randomUUID(),
    accountId: null,
    account: null,
    debitAmount: 0,
    creditAmount: 0,
    description: '',
    memo: '',
    businessPartner: '',
    departmentInfo: '',
    projectCode: '',
    taxType: undefined,
    taxAmount: 0,
    documentType: undefined,
    documentNumber: '',
  }
}

/**
 * 분개 라인 검증
 * 차변 또는 대변 중 하나만 금액이 있는지 확인
 */
export function isValidLine(line: JournalEntryLine): boolean {
  const hasDebit = line.debitAmount > 0
  const hasCredit = line.creditAmount > 0
  return (hasDebit && !hasCredit) || (!hasDebit && hasCredit)
}

/**
 * 통화 포맷팅 (한국 원화)
 */
export function formatCurrency(amount: number): string {
  return new Intl.NumberFormat('ko-KR', {
    style: 'currency',
    currency: 'KRW',
  }).format(amount)
}

/**
 * 날짜 포맷팅 (한국 형식)
 */
export function formatDate(dateString: string): string {
  return new Date(dateString).toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })
}

/**
 * 날짜/시간 포맷팅 (한국 형식)
 */
export function formatDateTime(dateString: string): string {
  return new Date(dateString).toLocaleString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}
