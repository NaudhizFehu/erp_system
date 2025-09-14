package com.erp.accounting.entity;

import com.erp.common.entity.BaseEntity;
import com.erp.hr.entity.Employee;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 예산 수정 이력 엔티티
 * 예산 변경 내역을 추적합니다
 */
@Entity
@Table(name = "budget_revisions", indexes = {
    @Index(name = "idx_budget_revisions_budget", columnList = "budget_id"),
    @Index(name = "idx_budget_revisions_revised_at", columnList = "revised_at"),
    @Index(name = "idx_budget_revisions_revised_by", columnList = "revised_by")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class BudgetRevision extends BaseEntity {

    /**
     * 예산
     */
    @NotNull(message = "예산은 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    /**
     * 수정 전 금액
     */
    @NotNull(message = "수정 전 금액은 필수입니다")
    @Column(name = "old_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal oldAmount;

    /**
     * 수정 후 금액
     */
    @NotNull(message = "수정 후 금액은 필수입니다")
    @Column(name = "new_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal newAmount;

    /**
     * 수정 사유
     */
    @NotBlank(message = "수정 사유는 필수입니다")
    @Size(max = 500, message = "수정 사유는 500자 이하여야 합니다")
    @Column(name = "revision_reason", nullable = false, length = 500)
    private String revisionReason;

    /**
     * 수정자
     */
    @NotNull(message = "수정자는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "revised_by", nullable = false)
    private Employee revisedBy;

    /**
     * 수정 시간
     */
    @NotNull(message = "수정 시간은 필수입니다")
    @Column(name = "revised_at", nullable = false)
    private LocalDateTime revisedAt;

    /**
     * 수정 금액 (신규 - 기존)
     */
    public BigDecimal getRevisionAmount() {
        return newAmount.subtract(oldAmount);
    }

    /**
     * 수정 비율 (%)
     */
    public BigDecimal getRevisionRate() {
        if (oldAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return getRevisionAmount()
            .divide(oldAmount, 4, BigDecimal.ROUND_HALF_UP)
            .multiply(new BigDecimal("100"));
    }

    /**
     * 증액 여부
     */
    public boolean isIncrease() {
        return newAmount.compareTo(oldAmount) > 0;
    }

    /**
     * 감액 여부
     */
    public boolean isDecrease() {
        return newAmount.compareTo(oldAmount) < 0;
    }
}




