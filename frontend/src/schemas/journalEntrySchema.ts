import { z } from 'zod'
import { TransactionType, TaxType, DocumentType } from '@/types/accounting'

/**
 * 분개 라인 검증 스키마
 */
export const journalEntryLineSchema = z
  .object({
    id: z.string(),
    accountId: z
      .number({ required_error: '계정과목을 선택해주세요' })
      .positive('유효한 계정과목을 선택해주세요'),
    account: z.any().nullable(),
    debitAmount: z
      .number()
      .min(0, '차변 금액은 0 이상이어야 합니다')
      .default(0),
    creditAmount: z
      .number()
      .min(0, '대변 금액은 0 이상이어야 합니다')
      .default(0),
    description: z
      .string()
      .max(500, '거래 설명은 500자 이하여야 합니다')
      .optional(),
    memo: z.string().max(200, '적요는 200자 이하여야 합니다').optional(),
    businessPartner: z.string().max(100).optional(),
    departmentInfo: z.string().max(100).optional(),
    projectCode: z.string().max(50).optional(),
    taxType: z.nativeEnum(TaxType).optional(),
    taxAmount: z.number().min(0).optional(),
    documentType: z.nativeEnum(DocumentType).optional(),
    documentNumber: z.string().max(50).optional(),
  })
  .refine(
    (data) => {
      // 차변 또는 대변 중 정확히 하나만 > 0
      const hasDebit = data.debitAmount > 0
      const hasCredit = data.creditAmount > 0
      return (hasDebit && !hasCredit) || (!hasDebit && hasCredit)
    },
    {
      message: '차변 또는 대변 중 하나만 금액을 입력해야 합니다',
      path: ['debitAmount'],
    }
  )

/**
 * 전표 입력 폼 검증 스키마
 */
export const journalEntryFormSchema = z
  .object({
    companyId: z.number().positive('회사를 선택해주세요'),
    transactionDate: z.string().refine(
      (date) => {
        const selected = new Date(date)
        const today = new Date()
        today.setHours(23, 59, 59, 999)
        return selected <= today
      },
      { message: '거래일자는 오늘 이전이어야 합니다' }
    ),
    transactionType: z.nativeEnum(TransactionType, {
      required_error: '거래 유형을 선택해주세요',
    }),
    transactionNumber: z.string().optional(),
    inputById: z.number().optional(),
    lines: z
      .array(journalEntryLineSchema)
      .min(2, '최소 2개의 분개 항목이 필요합니다 (복식부기)'),
  })
  .refine(
    (data) => {
      const totalDebit = data.lines.reduce(
        (sum, line) => sum + line.debitAmount,
        0
      )
      const totalCredit = data.lines.reduce(
        (sum, line) => sum + line.creditAmount,
        0
      )
      return Math.abs(totalDebit - totalCredit) < 0.01 // 부동소수점 허용 오차
    },
    {
      message: '차변 합계와 대변 합계가 일치해야 합니다 (대차평형)',
      path: ['lines'],
    }
  )

export type JournalEntryLineFormData = z.infer<typeof journalEntryLineSchema>
export type JournalEntryFormData = z.infer<typeof journalEntryFormSchema>
