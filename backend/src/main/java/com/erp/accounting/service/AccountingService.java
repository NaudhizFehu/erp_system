package com.erp.accounting.service;

import com.erp.accounting.dto.*;
import com.erp.accounting.entity.Account;
import com.erp.accounting.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 회계 서비스 인터페이스
 * 복식부기 처리 및 회계 핵심 기능을 정의합니다
 */
public interface AccountingService {

    /**
     * 복식부기 거래 생성
     * @param journalEntries 분개 항목들 (차변과 대변의 합이 일치해야 함)
     * @return 생성된 거래 목록
     */
    List<TransactionDto> createJournalEntry(List<TransactionCreateDto> journalEntries);

    /**
     * 단일 거래 생성
     */
    TransactionDto createTransaction(TransactionCreateDto transactionCreateDto);

    /**
     * 거래 수정
     */
    TransactionDto updateTransaction(Long id, TransactionCreateDto transactionUpdateDto);

    /**
     * 거래 승인
     */
    TransactionDto approveTransaction(Long id, Long approverId);

    /**
     * 거래 전기 (계정과목 잔액 반영)
     */
    TransactionDto postTransaction(Long id);

    /**
     * 거래 취소
     */
    void cancelTransaction(Long id, String reason, Long cancelById);

    /**
     * 수정분개 생성 (원거래 취소 + 새 거래)
     */
    List<TransactionDto> createAdjustingEntry(Long originalTransactionId, TransactionCreateDto newTransaction);

    /**
     * 계정과목 잔액 업데이트
     */
    void updateAccountBalance(Long accountId);

    /**
     * 계정과목별 잔액 조회
     */
    BigDecimal getAccountBalance(Long accountId, LocalDate asOfDate);

    /**
     * 시산표 생성
     */
    List<TrialBalanceDto> generateTrialBalance(Long companyId, LocalDate startDate, LocalDate endDate);

    /**
     * 총계정원장 조회
     */
    List<GeneralLedgerDto> getGeneralLedger(Long accountId, LocalDate startDate, LocalDate endDate);

    /**
     * 회계기간 마감
     */
    void closeFiscalPeriod(Long companyId, Integer fiscalYear, Integer fiscalMonth);

    /**
     * 회계연도 마감
     */
    void closeFiscalYear(Long companyId, Integer fiscalYear);

    /**
     * 대차평형 검증
     */
    BalanceVerificationDto verifyBalance(Long companyId, LocalDate asOfDate);

    /**
     * 거래번호 자동 생성
     */
    String generateTransactionNumber(Long companyId, Transaction.TransactionType transactionType);

    /**
     * 거래 검색
     */
    Page<TransactionDto> searchTransactions(String searchTerm, Pageable pageable);

    /**
     * 회사별 거래 검색
     */
    Page<TransactionDto> searchTransactionsByCompany(Long companyId, String searchTerm, Pageable pageable);

    /**
     * 거래 통계
     */
    TransactionStatisticsDto getTransactionStatistics(Long companyId, LocalDate startDate, LocalDate endDate);
}




