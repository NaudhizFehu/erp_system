package com.erp.accounting.service.impl;

import com.erp.accounting.dto.*;
import com.erp.accounting.entity.Account;
import com.erp.accounting.entity.Transaction;
import com.erp.accounting.repository.AccountRepository;
import com.erp.accounting.repository.TransactionRepository;
import com.erp.accounting.service.AccountingService;
import com.erp.common.entity.Company;
import com.erp.common.repository.CompanyRepository;
import com.erp.common.utils.ExceptionUtils;
import com.erp.hr.entity.Employee;
import com.erp.hr.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 회계 서비스 구현체
 * 복식부기 처리 및 회계 핵심 기능을 구현합니다
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountingServiceImpl implements AccountingService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * 복식부기 거래 생성
     * 차변과 대변의 합이 일치해야 함을 검증합니다
     */
    @Override
    @Transactional
    public List<TransactionDto> createJournalEntry(List<TransactionCreateDto> journalEntries) {
        log.info("복식부기 분개 생성 시작 - 항목 수: {}", journalEntries.size());

        if (journalEntries.isEmpty()) {
            throw ExceptionUtils.businessException("분개 항목이 없습니다");
        }

        // 차변과 대변 합계 계산
        BigDecimal totalDebit = journalEntries.stream()
            .map(TransactionCreateDto::debitAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredit = journalEntries.stream()
            .map(TransactionCreateDto::creditAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 대차평형 검증
        if (totalDebit.compareTo(totalCredit) != 0) {
            throw ExceptionUtils.businessException(
                String.format("차변 합계(%s)와 대변 합계(%s)가 일치하지 않습니다", 
                    totalDebit, totalCredit));
        }

        // 거래번호 생성 (동일한 분개는 같은 번호 사용)
        String baseTransactionNumber = generateTransactionNumber(
            journalEntries.get(0).companyId(), 
            journalEntries.get(0).transactionType()
        );

        List<TransactionDto> createdTransactions = new ArrayList<>();

        for (int i = 0; i < journalEntries.size(); i++) {
            TransactionCreateDto entry = journalEntries.get(i);
            
            // 분개 순번 추가
            String transactionNumber = journalEntries.size() > 1 
                ? baseTransactionNumber + "-" + (i + 1)
                : baseTransactionNumber;

            // 거래번호 업데이트된 DTO 생성
            TransactionCreateDto updatedEntry = new TransactionCreateDto(
                transactionNumber,
                entry.companyId(),
                entry.transactionDate(),
                entry.transactionType(),
                entry.accountId(),
                entry.debitAmount(),
                entry.creditAmount(),
                entry.description(),
                entry.memo(),
                entry.businessPartner(),
                entry.departmentInfo(),
                entry.projectCode(),
                entry.taxType(),
                entry.taxAmount(),
                entry.taxInvoiceNumber(),
                entry.documentType(),
                entry.documentNumber(),
                entry.attachmentPath(),
                entry.inputById()
            );

            TransactionDto createdTransaction = createTransaction(updatedEntry);
            createdTransactions.add(createdTransaction);
        }

        log.info("복식부기 분개 생성 완료 - 생성된 거래 수: {}", createdTransactions.size());
        return createdTransactions;
    }

    /**
     * 단일 거래 생성
     */
    @Override
    @Transactional
    public TransactionDto createTransaction(TransactionCreateDto dto) {
        log.info("거래 생성 시작 - 거래번호: {}", dto.transactionNumber());

        // 엔티티 조회
        Company company = companyRepository.findById(dto.companyId())
            .orElseThrow(() -> ExceptionUtils.entityNotFoundException("회사를 찾을 수 없습니다"));

        Account account = accountRepository.findById(dto.accountId())
            .orElseThrow(() -> ExceptionUtils.entityNotFoundException("계정과목을 찾을 수 없습니다"));

        Employee inputBy = null;
        if (dto.inputById() != null) {
            inputBy = employeeRepository.findById(dto.inputById())
                .orElseThrow(() -> ExceptionUtils.entityNotFoundException("입력자를 찾을 수 없습니다"));
        }

        // 거래번호 중복 확인
        if (transactionRepository.existsByTransactionNumberAndIsDeletedFalse(dto.transactionNumber())) {
            throw ExceptionUtils.duplicateException("이미 존재하는 거래번호입니다: " + dto.transactionNumber());
        }

        // 말단 계정과목 검증
        if (!account.isLeafAccount()) {
            throw ExceptionUtils.businessException("말단 계정과목만 거래를 입력할 수 있습니다");
        }

        // 거래 엔티티 생성
        Transaction transaction = new Transaction();
        transaction.setTransactionNumber(dto.transactionNumber());
        transaction.setCompany(company);
        transaction.setTransactionDate(dto.transactionDate());
        transaction.setTransactionType(dto.transactionType());
        transaction.setAccount(account);
        transaction.setDebitAmount(dto.debitAmount());
        transaction.setCreditAmount(dto.creditAmount());
        transaction.setDescription(dto.description());
        transaction.setMemo(dto.memo());
        transaction.setBusinessPartner(dto.businessPartner());
        transaction.setDepartmentInfo(dto.departmentInfo());
        transaction.setProjectCode(dto.projectCode());
        transaction.setTaxType(dto.taxType());
        transaction.setTaxAmount(dto.taxAmount() != null ? dto.taxAmount() : BigDecimal.ZERO);
        transaction.setTaxInvoiceNumber(dto.taxInvoiceNumber());
        transaction.setDocumentType(dto.documentType());
        transaction.setDocumentNumber(dto.documentNumber());
        transaction.setAttachmentPath(dto.attachmentPath());
        transaction.setInputBy(inputBy);
        transaction.setTransactionStatus(Transaction.TransactionStatus.DRAFT);

        Transaction savedTransaction = transactionRepository.save(transaction);
        
        log.info("거래 생성 완료 - ID: {}, 거래번호: {}", savedTransaction.getId(), savedTransaction.getTransactionNumber());
        return TransactionDto.from(savedTransaction);
    }

    /**
     * 거래 수정
     */
    @Override
    @Transactional
    public TransactionDto updateTransaction(Long id, TransactionCreateDto dto) {
        log.info("거래 수정 시작 - ID: {}", id);

        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> ExceptionUtils.entityNotFoundException("거래를 찾을 수 없습니다"));

        // 전기된 거래는 수정 불가
        if (transaction.getTransactionStatus() == Transaction.TransactionStatus.POSTED) {
            throw ExceptionUtils.businessException("전기된 거래는 수정할 수 없습니다");
        }

        // 계정과목 변경 시 검증
        if (!transaction.getAccount().getId().equals(dto.accountId())) {
            Account newAccount = accountRepository.findById(dto.accountId())
                .orElseThrow(() -> ExceptionUtils.entityNotFoundException("계정과목을 찾을 수 없습니다"));
            
            if (!newAccount.isLeafAccount()) {
                throw ExceptionUtils.businessException("말단 계정과목만 거래를 입력할 수 있습니다");
            }
            
            transaction.setAccount(newAccount);
        }

        // 필드 업데이트
        transaction.setTransactionDate(dto.transactionDate());
        transaction.setTransactionType(dto.transactionType());
        transaction.setDebitAmount(dto.debitAmount());
        transaction.setCreditAmount(dto.creditAmount());
        transaction.setDescription(dto.description());
        transaction.setMemo(dto.memo());
        transaction.setBusinessPartner(dto.businessPartner());
        transaction.setDepartmentInfo(dto.departmentInfo());
        transaction.setProjectCode(dto.projectCode());
        transaction.setTaxType(dto.taxType());
        transaction.setTaxAmount(dto.taxAmount() != null ? dto.taxAmount() : BigDecimal.ZERO);
        transaction.setTaxInvoiceNumber(dto.taxInvoiceNumber());
        transaction.setDocumentType(dto.documentType());
        transaction.setDocumentNumber(dto.documentNumber());
        transaction.setAttachmentPath(dto.attachmentPath());

        Transaction savedTransaction = transactionRepository.save(transaction);
        
        log.info("거래 수정 완료 - ID: {}", savedTransaction.getId());
        return TransactionDto.from(savedTransaction);
    }

    /**
     * 거래 승인
     */
    @Override
    @Transactional
    public TransactionDto approveTransaction(Long id, Long approverId) {
        log.info("거래 승인 시작 - ID: {}, 승인자: {}", id, approverId);

        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> ExceptionUtils.entityNotFoundException("거래를 찾을 수 없습니다"));

        Employee approver = employeeRepository.findById(approverId)
            .orElseThrow(() -> ExceptionUtils.entityNotFoundException("승인자를 찾을 수 없습니다"));

        if (transaction.getTransactionStatus() != Transaction.TransactionStatus.DRAFT &&
            transaction.getTransactionStatus() != Transaction.TransactionStatus.PENDING) {
            throw ExceptionUtils.businessException("승인 대기 상태의 거래만 승인할 수 있습니다");
        }

        transaction.approve(approver);
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        log.info("거래 승인 완료 - ID: {}", savedTransaction.getId());
        return TransactionDto.from(savedTransaction);
    }

    /**
     * 거래 전기 (계정과목 잔액 반영)
     */
    @Override
    @Transactional
    public TransactionDto postTransaction(Long id) {
        log.info("거래 전기 시작 - ID: {}", id);

        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> ExceptionUtils.entityNotFoundException("거래를 찾을 수 없습니다"));

        if (transaction.getTransactionStatus() != Transaction.TransactionStatus.APPROVED) {
            throw ExceptionUtils.businessException("승인된 거래만 전기할 수 있습니다");
        }

        transaction.post();
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        log.info("거래 전기 완료 - ID: {}, 계정: {}", savedTransaction.getId(), 
                savedTransaction.getAccount().getAccountName());
        return TransactionDto.from(savedTransaction);
    }

    /**
     * 거래 취소
     */
    @Override
    @Transactional
    public void cancelTransaction(Long id, String reason, Long cancelById) {
        log.info("거래 취소 시작 - ID: {}, 취소 사유: {}", id, reason);

        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> ExceptionUtils.entityNotFoundException("거래를 찾을 수 없습니다"));

        Employee cancelBy = employeeRepository.findById(cancelById)
            .orElseThrow(() -> ExceptionUtils.entityNotFoundException("취소자를 찾을 수 없습니다"));

        transaction.cancel(reason, cancelBy);
        transactionRepository.save(transaction);
        
        log.info("거래 취소 완료 - ID: {}", transaction.getId());
    }

    /**
     * 수정분개 생성 (원거래 취소 + 새 거래)
     */
    @Override
    @Transactional
    public List<TransactionDto> createAdjustingEntry(Long originalTransactionId, TransactionCreateDto newTransaction) {
        log.info("수정분개 생성 시작 - 원거래 ID: {}", originalTransactionId);

        Transaction originalTransaction = transactionRepository.findById(originalTransactionId)
            .orElseThrow(() -> ExceptionUtils.entityNotFoundException("원거래를 찾을 수 없습니다"));

        if (originalTransaction.getTransactionStatus() != Transaction.TransactionStatus.POSTED) {
            throw ExceptionUtils.businessException("전기된 거래만 수정분개를 생성할 수 있습니다");
        }

        List<TransactionDto> result = new ArrayList<>();

        // 1. 취소분개 생성 (원거래와 반대)
        Transaction reversingEntry = originalTransaction.createReversingEntry();
        reversingEntry.setTransactionNumber(generateTransactionNumber(
            originalTransaction.getCompany().getId(), 
            Transaction.TransactionType.ADJUSTMENT
        ) + "-REV");
        
        Transaction savedReversingEntry = transactionRepository.save(reversingEntry);
        result.add(TransactionDto.from(savedReversingEntry));

        // 2. 새 거래 생성
        TransactionDto newTransactionDto = createTransaction(newTransaction);
        result.add(newTransactionDto);

        log.info("수정분개 생성 완료 - 생성된 거래 수: {}", result.size());
        return result;
    }

    /**
     * 계정과목 잔액 업데이트
     */
    @Override
    @Transactional
    public void updateAccountBalance(Long accountId) {
        log.info("계정과목 잔액 업데이트 시작 - 계정 ID: {}", accountId);

        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> ExceptionUtils.entityNotFoundException("계정과목을 찾을 수 없습니다"));

        if (!account.getTrackBalance()) {
            log.warn("잔액 추적하지 않는 계정과목입니다 - ID: {}", accountId);
            return;
        }

        // 전기된 거래의 차변/대변 합계 조회
        BigDecimal totalDebit = transactionRepository.sumDebitAmountByAccountId(accountId);
        BigDecimal totalCredit = transactionRepository.sumCreditAmountByAccountId(accountId);

        // 계정과목 유형에 따른 잔액 계산
        BigDecimal newBalance;
        if (account.getDebitCreditType() == Account.DebitCreditType.DEBIT) {
            newBalance = totalDebit.subtract(totalCredit);
        } else {
            newBalance = totalCredit.subtract(totalDebit);
        }

        account.setCurrentBalance(newBalance);
        accountRepository.save(account);

        log.info("계정과목 잔액 업데이트 완료 - 계정: {}, 잔액: {}", 
                account.getAccountName(), newBalance);
    }

    /**
     * 계정과목별 잔액 조회
     */
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getAccountBalance(Long accountId, LocalDate asOfDate) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> ExceptionUtils.entityNotFoundException("계정과목을 찾을 수 없습니다"));

        if (!account.getTrackBalance()) {
            return BigDecimal.ZERO;
        }

        LocalDate endDate = asOfDate != null ? asOfDate : LocalDate.now();
        LocalDate startDate = LocalDate.of(endDate.getYear(), 1, 1);

        BigDecimal totalDebit = transactionRepository.sumDebitAmountByAccountIdAndDateBetween(
            accountId, startDate, endDate);
        BigDecimal totalCredit = transactionRepository.sumCreditAmountByAccountIdAndDateBetween(
            accountId, startDate, endDate);

        // 기초잔액 추가
        BigDecimal balance = account.getOpeningBalance();

        if (account.getDebitCreditType() == Account.DebitCreditType.DEBIT) {
            balance = balance.add(totalDebit).subtract(totalCredit);
        } else {
            balance = balance.add(totalCredit).subtract(totalDebit);
        }

        return balance;
    }

    /**
     * 시산표 생성
     */
    @Override
    @Transactional(readOnly = true)
    public List<TrialBalanceDto> generateTrialBalance(Long companyId, LocalDate startDate, LocalDate endDate) {
        log.info("시산표 생성 시작 - 회사: {}, 기간: {} ~ {}", companyId, startDate, endDate);

        List<Object[]> trialBalanceData = transactionRepository.getTrialBalanceData(companyId, startDate, endDate);

        List<TrialBalanceDto> result = trialBalanceData.stream()
            .map(data -> {
                String accountCode = (String) data[0];
                String accountName = (String) data[1];
                String accountType = ((Account.AccountType) data[2]).name();
                BigDecimal debitAmount = (BigDecimal) data[3];
                BigDecimal creditAmount = (BigDecimal) data[4];
                BigDecimal balance = debitAmount.subtract(creditAmount);

                return new TrialBalanceDto(accountCode, accountName, accountType, 
                                         debitAmount, creditAmount, balance);
            })
            .collect(Collectors.toList());

        log.info("시산표 생성 완료 - 계정 수: {}", result.size());
        return result;
    }

    /**
     * 총계정원장 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<GeneralLedgerDto> getGeneralLedger(Long accountId, LocalDate startDate, LocalDate endDate) {
        log.info("총계정원장 조회 시작 - 계정 ID: {}, 기간: {} ~ {}", accountId, startDate, endDate);

        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> ExceptionUtils.entityNotFoundException("계정과목을 찾을 수 없습니다"));

        List<Transaction> transactions = transactionRepository.findByCompanyIdAndTransactionDateBetween(
            account.getCompany().getId(), startDate, endDate);

        List<GeneralLedgerDto> result = new ArrayList<>();
        BigDecimal runningBalance = account.getOpeningBalance();

        for (Transaction transaction : transactions) {
            if (!transaction.getAccount().getId().equals(accountId)) {
                continue;
            }

            BigDecimal debitAmount = transaction.getDebitAmount();
            BigDecimal creditAmount = transaction.getCreditAmount();

            // 잔액 계산
            if (account.getDebitCreditType() == Account.DebitCreditType.DEBIT) {
                runningBalance = runningBalance.add(debitAmount).subtract(creditAmount);
            } else {
                runningBalance = runningBalance.add(creditAmount).subtract(debitAmount);
            }

            result.add(new GeneralLedgerDto(
                transaction.getTransactionDate(),
                transaction.getTransactionNumber(),
                transaction.getDescription(),
                debitAmount,
                creditAmount,
                runningBalance
            ));
        }

        log.info("총계정원장 조회 완료 - 거래 수: {}", result.size());
        return result;
    }

    /**
     * 회계기간 마감
     */
    @Override
    @Transactional
    public void closeFiscalPeriod(Long companyId, Integer fiscalYear, Integer fiscalMonth) {
        log.info("회계기간 마감 시작 - 회사: {}, 연도: {}, 월: {}", companyId, fiscalYear, fiscalMonth);

        // 승인되지 않은 거래 확인
        List<Transaction> pendingTransactions = transactionRepository.findPendingTransactionsByCompany(companyId);
        if (!pendingTransactions.isEmpty()) {
            throw ExceptionUtils.businessException("승인되지 않은 거래가 " + pendingTransactions.size() + "건 있습니다");
        }

        // 모든 계정과목 잔액 업데이트
        List<Account> accounts = accountRepository.findTrackingBalanceAccountsByCompanyId(companyId);
        for (Account account : accounts) {
            updateAccountBalance(account.getId());
        }

        log.info("회계기간 마감 완료 - 회사: {}, 연도: {}, 월: {}", companyId, fiscalYear, fiscalMonth);
    }

    /**
     * 회계연도 마감
     */
    @Override
    @Transactional
    public void closeFiscalYear(Long companyId, Integer fiscalYear) {
        log.info("회계연도 마감 시작 - 회사: {}, 연도: {}", companyId, fiscalYear);

        // 모든 월 마감
        for (int month = 1; month <= 12; month++) {
            closeFiscalPeriod(companyId, fiscalYear, month);
        }

        // 손익 계정 마감 (이익잉여금으로 대체)
        List<Account> revenueAccounts = accountRepository.findByAccountType(Account.AccountType.REVENUE);
        List<Account> expenseAccounts = accountRepository.findByAccountType(Account.AccountType.EXPENSE);

        for (Account account : revenueAccounts) {
            if (account.getCompany().getId().equals(companyId)) {
                account.resetBalance();
                accountRepository.save(account);
            }
        }

        for (Account account : expenseAccounts) {
            if (account.getCompany().getId().equals(companyId)) {
                account.resetBalance();
                accountRepository.save(account);
            }
        }

        log.info("회계연도 마감 완료 - 회사: {}, 연도: {}", companyId, fiscalYear);
    }

    /**
     * 대차평형 검증
     */
    @Override
    @Transactional(readOnly = true)
    public BalanceVerificationDto verifyBalance(Long companyId, LocalDate asOfDate) {
        log.info("대차평형 검증 시작 - 회사: {}, 기준일: {}", companyId, asOfDate);

        BigDecimal totalAssets = accountRepository.getTotalAssetBalance(companyId);
        BigDecimal totalLiabilities = accountRepository.getTotalLiabilityBalance(companyId);
        BigDecimal totalEquity = accountRepository.getTotalEquityBalance(companyId);

        BigDecimal totalDebits = totalAssets;
        BigDecimal totalCredits = totalLiabilities.add(totalEquity);
        BigDecimal balanceDifference = totalDebits.subtract(totalCredits);

        boolean isBalanced = balanceDifference.compareTo(BigDecimal.ZERO) == 0;
        String message = isBalanced ? "대차평형이 일치합니다" : "대차평형이 일치하지 않습니다";

        log.info("대차평형 검증 완료 - 균형: {}, 차이: {}", isBalanced, balanceDifference);

        return new BalanceVerificationDto(
            asOfDate,
            totalAssets,
            totalLiabilities,
            totalEquity,
            totalDebits,
            totalCredits,
            balanceDifference,
            isBalanced,
            message
        );
    }

    /**
     * 거래번호 자동 생성
     */
    @Override
    public String generateTransactionNumber(Long companyId, Transaction.TransactionType transactionType) {
        String prefix = getTransactionPrefix(transactionType);
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // 오늘 날짜의 마지막 순번 조회
        String pattern = prefix + dateStr + "%";
        long count = transactionRepository.count();
        
        String sequence = String.format("%04d", (count % 10000) + 1);
        return prefix + dateStr + sequence;
    }

    private String getTransactionPrefix(Transaction.TransactionType transactionType) {
        return switch (transactionType) {
            case JOURNAL -> "JE";
            case SALES -> "SL";
            case PURCHASE -> "PU";
            case CASH_RECEIPT -> "CR";
            case CASH_PAYMENT -> "CP";
            case BANK_RECEIPT -> "BR";
            case BANK_PAYMENT -> "BP";
            case ADJUSTMENT -> "AJ";
            case CLOSING -> "CL";
        };
    }

    /**
     * 거래 검색
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TransactionDto> searchTransactions(String searchTerm, Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.searchTransactions(searchTerm, pageable);
        List<TransactionDto> transactionDtos = transactions.getContent().stream()
            .map(TransactionDto::from)
            .collect(Collectors.toList());
        
        return new PageImpl<>(transactionDtos, pageable, transactions.getTotalElements());
    }

    /**
     * 회사별 거래 검색
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TransactionDto> searchTransactionsByCompany(Long companyId, String searchTerm, Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.searchTransactionsByCompany(companyId, searchTerm, pageable);
        List<TransactionDto> transactionDtos = transactions.getContent().stream()
            .map(TransactionDto::from)
            .collect(Collectors.toList());
        
        return new PageImpl<>(transactionDtos, pageable, transactions.getTotalElements());
    }

    /**
     * 거래 통계
     */
    @Override
    @Transactional(readOnly = true)
    public TransactionStatisticsDto getTransactionStatistics(Long companyId, LocalDate startDate, LocalDate endDate) {
        log.info("거래 통계 조회 시작 - 회사: {}, 기간: {} ~ {}", companyId, startDate, endDate);

        // 기본 통계
        List<Transaction> transactions = transactionRepository.findByCompanyIdAndTransactionDateBetween(
            companyId, startDate, endDate);

        Long totalCount = (long) transactions.size();
        BigDecimal totalAmount = transactions.stream()
            .map(t -> t.getDebitAmount().add(t.getCreditAmount()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalDebit = transactions.stream()
            .map(Transaction::getDebitAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCredit = transactions.stream()
            .map(Transaction::getCreditAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 유형별 통계
        Map<String, Long> countByType = transactions.stream()
            .collect(Collectors.groupingBy(
                t -> t.getTransactionType().name(),
                Collectors.counting()));

        Map<String, BigDecimal> amountByType = transactions.stream()
            .collect(Collectors.groupingBy(
                t -> t.getTransactionType().name(),
                Collectors.reducing(BigDecimal.ZERO, 
                    t -> t.getDebitAmount().add(t.getCreditAmount()),
                    BigDecimal::add)));

        // 상태별 통계
        Map<String, Long> countByStatus = transactions.stream()
            .collect(Collectors.groupingBy(
                t -> t.getTransactionStatus().name(),
                Collectors.counting()));

        // 일별 통계
        Map<String, Long> dailyCounts = transactions.stream()
            .collect(Collectors.groupingBy(
                t -> t.getTransactionDate().toString(),
                Collectors.counting()));

        Map<String, BigDecimal> dailyAmounts = transactions.stream()
            .collect(Collectors.groupingBy(
                t -> t.getTransactionDate().toString(),
                Collectors.reducing(BigDecimal.ZERO,
                    t -> t.getDebitAmount().add(t.getCreditAmount()),
                    BigDecimal::add)));

        log.info("거래 통계 조회 완료 - 총 거래 수: {}", totalCount);

        return new TransactionStatisticsDto(
            startDate,
            endDate,
            totalCount,
            totalAmount,
            totalDebit,
            totalCredit,
            countByType,
            amountByType,
            countByStatus,
            dailyCounts,
            dailyAmounts
        );
    }
}
