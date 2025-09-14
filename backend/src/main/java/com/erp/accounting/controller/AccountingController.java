package com.erp.accounting.controller;

import com.erp.accounting.dto.*;
import com.erp.accounting.entity.Transaction;
import com.erp.accounting.service.AccountingService;
import com.erp.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 회계 관리 컨트롤러
 * 복식부기 거래 처리 및 회계 핵심 기능을 제공합니다
 */
@Slf4j
@RestController
@RequestMapping("/api/accounting")
@RequiredArgsConstructor
public class AccountingController {

    private final AccountingService accountingService;

    /**
     * 복식부기 분개 생성
     */
    @PostMapping("/journal-entries")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<List<TransactionDto>>> createJournalEntry(
            @Valid @RequestBody List<TransactionCreateDto> journalEntries) {
        try {
            log.info("복식부기 분개 생성 요청 - 항목 수: {}", journalEntries.size());
            
            List<TransactionDto> result = accountingService.createJournalEntry(journalEntries);
            
            return ResponseEntity.ok(ApiResponse.success(
                "복식부기 분개가 성공적으로 생성되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("복식부기 분개 생성 실패", e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("복식부기 분개 생성에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 단일 거래 생성
     */
    @PostMapping("/transactions")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")
    public ResponseEntity<ApiResponse<TransactionDto>> createTransaction(
            @Valid @RequestBody TransactionCreateDto transactionCreateDto) {
        try {
            log.info("거래 생성 요청 - 거래번호: {}", transactionCreateDto.transactionNumber());
            
            TransactionDto result = accountingService.createTransaction(transactionCreateDto);
            
            return ResponseEntity.ok(ApiResponse.success(
                "거래가 성공적으로 생성되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("거래 생성 실패", e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("거래 생성에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 거래 수정
     */
    @PutMapping("/transactions/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<TransactionDto>> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionCreateDto transactionUpdateDto) {
        try {
            log.info("거래 수정 요청 - ID: {}", id);
            
            TransactionDto result = accountingService.updateTransaction(id, transactionUpdateDto);
            
            return ResponseEntity.ok(ApiResponse.success(
                "거래가 성공적으로 수정되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("거래 수정 실패 - ID: {}", id, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("거래 수정에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 거래 승인
     */
    @PostMapping("/transactions/{id}/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<TransactionDto>> approveTransaction(
            @PathVariable Long id,
            @RequestParam Long approverId) {
        try {
            log.info("거래 승인 요청 - ID: {}, 승인자: {}", id, approverId);
            
            TransactionDto result = accountingService.approveTransaction(id, approverId);
            
            return ResponseEntity.ok(ApiResponse.success(
                "거래가 성공적으로 승인되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("거래 승인 실패 - ID: {}", id, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("거래 승인에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 거래 전기
     */
    @PostMapping("/transactions/{id}/post")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<TransactionDto>> postTransaction(@PathVariable Long id) {
        try {
            log.info("거래 전기 요청 - ID: {}", id);
            
            TransactionDto result = accountingService.postTransaction(id);
            
            return ResponseEntity.ok(ApiResponse.success(
                "거래가 성공적으로 전기되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("거래 전기 실패 - ID: {}", id, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("거래 전기에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 거래 취소
     */
    @PostMapping("/transactions/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<Void>> cancelTransaction(
            @PathVariable Long id,
            @RequestParam String reason,
            @RequestParam Long cancelById) {
        try {
            log.info("거래 취소 요청 - ID: {}, 취소 사유: {}", id, reason);
            
            accountingService.cancelTransaction(id, reason, cancelById);
            
            return ResponseEntity.ok(ApiResponse.success(
                "거래가 성공적으로 취소되었습니다"
            ));
        } catch (Exception e) {
            log.error("거래 취소 실패 - ID: {}", id, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("거래 취소에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 수정분개 생성
     */
    @PostMapping("/transactions/{originalId}/adjusting-entry")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<List<TransactionDto>>> createAdjustingEntry(
            @PathVariable Long originalId,
            @Valid @RequestBody TransactionCreateDto newTransaction) {
        try {
            log.info("수정분개 생성 요청 - 원거래 ID: {}", originalId);
            
            List<TransactionDto> result = accountingService.createAdjustingEntry(originalId, newTransaction);
            
            return ResponseEntity.ok(ApiResponse.success(
                "수정분개가 성공적으로 생성되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("수정분개 생성 실패 - 원거래 ID: {}", originalId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("수정분개 생성에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 계정과목 잔액 조회
     */
    @GetMapping("/accounts/{accountId}/balance")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")
    public ResponseEntity<ApiResponse<BigDecimal>> getAccountBalance(
            @PathVariable Long accountId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        try {
            log.info("계정과목 잔액 조회 요청 - 계정 ID: {}, 기준일: {}", accountId, asOfDate);
            
            BigDecimal balance = accountingService.getAccountBalance(accountId, asOfDate);
            
            return ResponseEntity.ok(ApiResponse.success(
                "계정과목 잔액을 성공적으로 조회했습니다",
                balance
            ));
        } catch (Exception e) {
            log.error("계정과목 잔액 조회 실패 - 계정 ID: {}", accountId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("계정과목 잔액 조회에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 시산표 생성
     */
    @GetMapping("/companies/{companyId}/trial-balance")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<List<TrialBalanceDto>>> generateTrialBalance(
            @PathVariable Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("시산표 생성 요청 - 회사: {}, 기간: {} ~ {}", companyId, startDate, endDate);
            
            List<TrialBalanceDto> result = accountingService.generateTrialBalance(companyId, startDate, endDate);
            
            return ResponseEntity.ok(ApiResponse.success(
                "시산표가 성공적으로 생성되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("시산표 생성 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("시산표 생성에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 총계정원장 조회
     */
    @GetMapping("/accounts/{accountId}/general-ledger")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<List<GeneralLedgerDto>>> getGeneralLedger(
            @PathVariable Long accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("총계정원장 조회 요청 - 계정 ID: {}, 기간: {} ~ {}", accountId, startDate, endDate);
            
            List<GeneralLedgerDto> result = accountingService.getGeneralLedger(accountId, startDate, endDate);
            
            return ResponseEntity.ok(ApiResponse.success(
                "총계정원장을 성공적으로 조회했습니다",
                result
            ));
        } catch (Exception e) {
            log.error("총계정원장 조회 실패 - 계정 ID: {}", accountId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("총계정원장 조회에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 회계기간 마감
     */
    @PostMapping("/companies/{companyId}/close-period")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> closeFiscalPeriod(
            @PathVariable Long companyId,
            @RequestParam Integer fiscalYear,
            @RequestParam Integer fiscalMonth) {
        try {
            log.info("회계기간 마감 요청 - 회사: {}, 연도: {}, 월: {}", companyId, fiscalYear, fiscalMonth);
            
            accountingService.closeFiscalPeriod(companyId, fiscalYear, fiscalMonth);
            
            return ResponseEntity.ok(ApiResponse.success(
                "회계기간이 성공적으로 마감되었습니다"
            ));
        } catch (Exception e) {
            log.error("회계기간 마감 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("회계기간 마감에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 회계연도 마감
     */
    @PostMapping("/companies/{companyId}/close-year")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> closeFiscalYear(
            @PathVariable Long companyId,
            @RequestParam Integer fiscalYear) {
        try {
            log.info("회계연도 마감 요청 - 회사: {}, 연도: {}", companyId, fiscalYear);
            
            accountingService.closeFiscalYear(companyId, fiscalYear);
            
            return ResponseEntity.ok(ApiResponse.success(
                "회계연도가 성공적으로 마감되었습니다"
            ));
        } catch (Exception e) {
            log.error("회계연도 마감 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("회계연도 마감에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 대차평형 검증
     */
    @GetMapping("/companies/{companyId}/balance-verification")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<BalanceVerificationDto>> verifyBalance(
            @PathVariable Long companyId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        try {
            LocalDate date = asOfDate != null ? asOfDate : LocalDate.now();
            log.info("대차평형 검증 요청 - 회사: {}, 기준일: {}", companyId, date);
            
            BalanceVerificationDto result = accountingService.verifyBalance(companyId, date);
            
            return ResponseEntity.ok(ApiResponse.success(
                "대차평형 검증이 완료되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("대차평형 검증 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("대차평형 검증에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 거래번호 자동 생성
     */
    @GetMapping("/companies/{companyId}/generate-transaction-number")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")
    public ResponseEntity<ApiResponse<String>> generateTransactionNumber(
            @PathVariable Long companyId,
            @RequestParam Transaction.TransactionType transactionType) {
        try {
            log.info("거래번호 생성 요청 - 회사: {}, 유형: {}", companyId, transactionType);
            
            String transactionNumber = accountingService.generateTransactionNumber(companyId, transactionType);
            
            return ResponseEntity.ok(ApiResponse.success(
                transactionNumber, 
                "거래번호가 성공적으로 생성되었습니다"
            ));
        } catch (Exception e) {
            log.error("거래번호 생성 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("거래번호 생성에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 거래 검색
     */
    @GetMapping("/transactions/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")
    public ResponseEntity<ApiResponse<Page<TransactionDto>>> searchTransactions(
            @RequestParam String searchTerm,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("거래 검색 요청 - 검색어: {}", searchTerm);
            
            Page<TransactionDto> result = accountingService.searchTransactions(searchTerm, pageable);
            
            return ResponseEntity.ok(ApiResponse.success(
                "거래 검색이 완료되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("거래 검색 실패 - 검색어: {}", searchTerm, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("거래 검색에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 회사별 거래 검색
     */
    @GetMapping("/companies/{companyId}/transactions/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")
    public ResponseEntity<ApiResponse<Page<TransactionDto>>> searchTransactionsByCompany(
            @PathVariable Long companyId,
            @RequestParam String searchTerm,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("회사별 거래 검색 요청 - 회사: {}, 검색어: {}", companyId, searchTerm);
            
            Page<TransactionDto> result = accountingService.searchTransactionsByCompany(companyId, searchTerm, pageable);
            
            return ResponseEntity.ok(ApiResponse.success(
                "회사별 거래 검색이 완료되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("회사별 거래 검색 실패 - 회사: {}, 검색어: {}", companyId, searchTerm, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("회사별 거래 검색에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 거래 통계
     */
    @GetMapping("/companies/{companyId}/transaction-statistics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<TransactionStatisticsDto>> getTransactionStatistics(
            @PathVariable Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("거래 통계 요청 - 회사: {}, 기간: {} ~ {}", companyId, startDate, endDate);
            
            TransactionStatisticsDto result = accountingService.getTransactionStatistics(companyId, startDate, endDate);
            
            return ResponseEntity.ok(ApiResponse.success(
                "거래 통계를 성공적으로 조회했습니다",
                result
            ));
        } catch (Exception e) {
            log.error("거래 통계 조회 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("거래 통계 조회에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 계정과목 잔액 업데이트
     */
    @PostMapping("/accounts/{accountId}/update-balance")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<Void>> updateAccountBalance(@PathVariable Long accountId) {
        try {
            log.info("계정과목 잔액 업데이트 요청 - 계정 ID: {}", accountId);
            
            accountingService.updateAccountBalance(accountId);
            
            return ResponseEntity.ok(ApiResponse.success(
                "계정과목 잔액이 성공적으로 업데이트되었습니다"
            ));
        } catch (Exception e) {
            log.error("계정과목 잔액 업데이트 실패 - 계정 ID: {}", accountId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("계정과목 잔액 업데이트에 실패했습니다: " + e.getMessage())
            );
        }
    }
}
