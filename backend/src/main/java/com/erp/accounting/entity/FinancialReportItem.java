package com.erp.accounting.entity;

import com.erp.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/**
 * 재무보고서 항목 엔티티
 * 재무보고서의 개별 항목 데이터를 관리합니다
 */
@Entity
@Table(name = "financial_report_items", indexes = {
    @Index(name = "idx_financial_report_items_report", columnList = "report_id"),
    @Index(name = "idx_financial_report_items_account", columnList = "account_id"),
    @Index(name = "idx_financial_report_items_line_number", columnList = "line_number"),
    @Index(name = "idx_financial_report_items_parent", columnList = "parent_item_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class FinancialReportItem extends BaseEntity {

    /**
     * 재무보고서
     */
    @NotNull(message = "재무보고서는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private FinancialReport report;

    /**
     * 계정과목 (해당하는 경우)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    /**
     * 항목명
     */
    @NotBlank(message = "항목명은 필수입니다")
    @Size(max = 200, message = "항목명은 200자 이하여야 합니다")
    @Column(name = "item_name", nullable = false, length = 200)
    private String itemName;

    /**
     * 항목명 (영문)
     */
    @Size(max = 300, message = "영문 항목명은 300자 이하여야 합니다")
    @Column(name = "item_name_en", length = 300)
    private String itemNameEn;

    /**
     * 항목 코드
     */
    @Size(max = 50, message = "항목 코드는 50자 이하여야 합니다")
    @Column(name = "item_code", length = 50)
    private String itemCode;

    /**
     * 라인 번호 (보고서 내 순서)
     */
    @NotNull(message = "라인 번호는 필수입니다")
    @Min(value = 1, message = "라인 번호는 1 이상이어야 합니다")
    @Column(name = "line_number", nullable = false)
    private Integer lineNumber;

    /**
     * 항목 레벨 (1: 대분류, 2: 중분류, 3: 소분류 등)
     */
    @NotNull(message = "항목 레벨은 필수입니다")
    @Min(value = 1, message = "항목 레벨은 1 이상이어야 합니다")
    @Max(value = 5, message = "항목 레벨은 5 이하여야 합니다")
    @Column(name = "item_level", nullable = false)
    private Integer itemLevel;

    /**
     * 상위 항목
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_item_id")
    private FinancialReportItem parentItem;

    /**
     * 당기 금액
     */
    @Column(name = "current_amount", precision = 15, scale = 2)
    private BigDecimal currentAmount = BigDecimal.ZERO;

    /**
     * 전기 금액
     */
    @Column(name = "previous_amount", precision = 15, scale = 2)
    private BigDecimal previousAmount = BigDecimal.ZERO;

    /**
     * 증감액
     */
    @Column(name = "change_amount", precision = 15, scale = 2)
    private BigDecimal changeAmount = BigDecimal.ZERO;

    /**
     * 증감률 (%)
     */
    @Column(name = "change_rate", precision = 5, scale = 2)
    private BigDecimal changeRate = BigDecimal.ZERO;

    /**
     * 구성비 (%)
     */
    @Column(name = "composition_ratio", precision = 5, scale = 2)
    private BigDecimal compositionRatio = BigDecimal.ZERO;

    /**
     * 항목 유형
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", length = 20)
    private ItemType itemType = ItemType.ACCOUNT;

    /**
     * 계산 공식 (합계 항목의 경우)
     */
    @Size(max = 500, message = "계산 공식은 500자 이하여야 합니다")
    @Column(name = "calculation_formula", length = 500)
    private String calculationFormula;

    /**
     * 표시 여부
     */
    @NotNull(message = "표시 여부는 필수입니다")
    @Column(name = "is_visible", nullable = false)
    private Boolean isVisible = true;

    /**
     * 굵게 표시 여부
     */
    @NotNull(message = "굵게 표시 여부는 필수입니다")
    @Column(name = "is_bold", nullable = false)
    private Boolean isBold = false;

    /**
     * 들여쓰기 레벨
     */
    @Min(value = 0, message = "들여쓰기 레벨은 0 이상이어야 합니다")
    @Column(name = "indent_level")
    private Integer indentLevel = 0;

    /**
     * 비고
     */
    @Size(max = 500, message = "비고는 500자 이하여야 합니다")
    @Column(name = "note", length = 500)
    private String note;

    /**
     * 항목 유형 열거형
     */
    public enum ItemType {
        ACCOUNT("계정과목"),
        SUBTOTAL("소계"),
        TOTAL("합계"),
        HEADER("제목"),
        SEPARATOR("구분선"),
        CALCULATED("계산항목");

        private final String description;

        ItemType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 증감 계산
     */
    public void calculateChange() {
        this.changeAmount = currentAmount.subtract(previousAmount);
        
        if (previousAmount.compareTo(BigDecimal.ZERO) != 0) {
            this.changeRate = changeAmount
                .divide(previousAmount.abs(), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
        } else {
            this.changeRate = BigDecimal.ZERO;
        }
    }

    /**
     * 구성비 계산
     */
    public void calculateCompositionRatio(BigDecimal totalAmount) {
        if (totalAmount.compareTo(BigDecimal.ZERO) != 0) {
            this.compositionRatio = currentAmount
                .divide(totalAmount.abs(), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
        } else {
            this.compositionRatio = BigDecimal.ZERO;
        }
    }

    /**
     * 증가 여부
     */
    public boolean isIncrease() {
        return changeAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * 감소 여부
     */
    public boolean isDecrease() {
        return changeAmount.compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * 합계 항목 여부
     */
    public boolean isTotalItem() {
        return itemType == ItemType.TOTAL || itemType == ItemType.SUBTOTAL;
    }

    /**
     * 계정과목 항목 여부
     */
    public boolean isAccountItem() {
        return itemType == ItemType.ACCOUNT && account != null;
    }

    /**
     * 표시용 금액 (음수인 경우 괄호 표시)
     */
    public String getFormattedCurrentAmount() {
        if (currentAmount.compareTo(BigDecimal.ZERO) < 0) {
            return "(" + currentAmount.abs().toString() + ")";
        }
        return currentAmount.toString();
    }

    /**
     * 표시용 전기 금액
     */
    public String getFormattedPreviousAmount() {
        if (previousAmount.compareTo(BigDecimal.ZERO) < 0) {
            return "(" + previousAmount.abs().toString() + ")";
        }
        return previousAmount.toString();
    }

    @PrePersist
    @PreUpdate
    private void calculateValues() {
        calculateChange();
    }
}




