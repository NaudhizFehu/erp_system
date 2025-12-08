package com.erp.accounting.dto;

import com.erp.accounting.entity.Account;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 계정과목 수정 요청 DTO
 */
public record AccountUpdateDto(
        @NotBlank(message = "계정명은 필수입니다")
        @Size(max = 100)
        String accountName,

        @Size(max = 100)
        String accountNameEn,

        @Size(max = 255)
        String description,

        @NotNull(message = "계정 유형은 필수입니다")
        Account.AccountType accountType,

        @NotNull(message = "계정 분류는 필수입니다")
        Account.AccountCategory accountCategory,

        @NotNull(message = "차대구분은 필수입니다")
        Account.DebitCreditType debitCreditType,

        Long parentAccountId,

        @NotNull(message = "계정 레벨은 필수입니다")
        @Min(1)
        Integer accountLevel,

        @Min(0)
        Integer sortOrder,

        @NotNull(message = "사용 여부는 필수입니다")
        Boolean isActive,

        @NotNull(message = "잔액 추적 여부는 필수입니다")
        Boolean trackBalance,

        @DecimalMin(value = "0", inclusive = true)
        BigDecimal openingBalance,

        @DecimalMin(value = "0", inclusive = true)
        BigDecimal budgetAmount,

        @Size(max = 30)
        String taxCode,

        @Size(max = 50)
        String controlField1,

        @Size(max = 50)
        String controlField2
) {}




