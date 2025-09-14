package com.erp.accounting.dto;

import com.erp.common.dto.CompanyDto;
import com.erp.accounting.entity.Account;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 계정과목 정보 응답 DTO
 * 계정과목 정보 조회 시 사용됩니다
 */
public record AccountDto(
        Long id,
        String accountCode,
        String accountName,
        String accountNameEn,
        String description,
        CompanyDto company,
        Account.AccountType accountType,
        Account.AccountCategory accountCategory,
        Account.DebitCreditType debitCreditType,
        AccountDto parentAccount,
        Integer accountLevel,
        Integer sortOrder,
        Boolean isActive,
        Boolean trackBalance,
        BigDecimal currentBalance,
        BigDecimal openingBalance,
        BigDecimal budgetAmount,
        String taxCode,
        String controlField1,
        String controlField2,
        String fullPath,
        String fullCodePath,
        Boolean isLeafAccount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public AccountDto {
        if (accountCode == null || accountCode.trim().isEmpty()) {
            throw new IllegalArgumentException("계정과목 코드는 필수입니다");
        }
        if (accountName == null || accountName.trim().isEmpty()) {
            throw new IllegalArgumentException("계정과목명은 필수입니다");
        }
        if (company == null) {
            throw new IllegalArgumentException("소속 회사는 필수입니다");
        }
        if (accountType == null) {
            throw new IllegalArgumentException("계정과목 유형은 필수입니다");
        }
        if (accountCategory == null) {
            throw new IllegalArgumentException("계정과목 분류는 필수입니다");
        }
        if (debitCreditType == null) {
            throw new IllegalArgumentException("차대구분은 필수입니다");
        }
        if (accountLevel == null || accountLevel < 1) {
            throw new IllegalArgumentException("계정과목 레벨은 1 이상이어야 합니다");
        }
        if (isActive == null) {
            throw new IllegalArgumentException("사용 여부는 필수입니다");
        }
        if (trackBalance == null) {
            throw new IllegalArgumentException("잔액 추적 여부는 필수입니다");
        }
    }
    
    /**
     * Account 엔티티로부터 AccountDto 생성
     */
    public static AccountDto from(Account account) {
        return new AccountDto(
            account.getId(),
            account.getAccountCode(),
            account.getAccountName(),
            account.getAccountNameEn(),
            account.getDescription(),
            account.getCompany() != null ? CompanyDto.from(account.getCompany()) : null,
            account.getAccountType(),
            account.getAccountCategory(),
            account.getDebitCreditType(),
            account.getParentAccount() != null ? AccountDto.from(account.getParentAccount()) : null,
            account.getAccountLevel(),
            account.getSortOrder(),
            account.getIsActive(),
            account.getTrackBalance(),
            account.getCurrentBalance(),
            account.getOpeningBalance(),
            account.getBudgetAmount(),
            account.getTaxCode(),
            account.getControlField1(),
            account.getControlField2(),
            account.getFullPath(),
            account.getFullCodePath(),
            account.isLeafAccount(),
            account.getCreatedAt(),
            account.getUpdatedAt()
        );
    }
}




