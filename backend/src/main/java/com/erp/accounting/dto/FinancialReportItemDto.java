package com.erp.accounting.dto;

import com.erp.accounting.entity.FinancialReportItem;
import java.math.BigDecimal;

/**
 * 재무보고서 항목 정보 응답 DTO
 * 재무보고서 항목 정보 조회 시 사용됩니다
 */
public record FinancialReportItemDto(
        Long id,
        Long reportId,
        AccountDto account,
        String itemName,
        String itemNameEn,
        String itemCode,
        Integer lineNumber,
        Integer itemLevel,
        FinancialReportItemDto parentItem,
        BigDecimal currentAmount,
        BigDecimal previousAmount,
        BigDecimal changeAmount,
        BigDecimal changeRate,
        BigDecimal compositionRatio,
        FinancialReportItem.ItemType itemType,
        String calculationFormula,
        Boolean isVisible,
        Boolean isBold,
        Integer indentLevel,
        String note,
        Boolean isIncrease,
        Boolean isDecrease,
        Boolean isTotalItem,
        Boolean isAccountItem,
        String formattedCurrentAmount,
        String formattedPreviousAmount
) {
    public FinancialReportItemDto {
        if (reportId == null) {
            throw new IllegalArgumentException("보고서 ID는 필수입니다");
        }
        if (itemName == null || itemName.trim().isEmpty()) {
            throw new IllegalArgumentException("항목명은 필수입니다");
        }
        if (lineNumber == null || lineNumber < 1) {
            throw new IllegalArgumentException("라인 번호는 1 이상이어야 합니다");
        }
        if (itemLevel == null || itemLevel < 1 || itemLevel > 5) {
            throw new IllegalArgumentException("항목 레벨은 1~5 사이여야 합니다");
        }
        if (isVisible == null) {
            throw new IllegalArgumentException("표시 여부는 필수입니다");
        }
        if (isBold == null) {
            throw new IllegalArgumentException("굵게 표시 여부는 필수입니다");
        }
    }
    
    /**
     * FinancialReportItem 엔티티로부터 FinancialReportItemDto 생성
     */
    public static FinancialReportItemDto from(FinancialReportItem item) {
        return new FinancialReportItemDto(
            item.getId(),
            item.getReport() != null ? item.getReport().getId() : null,
            item.getAccount() != null ? AccountDto.from(item.getAccount()) : null,
            item.getItemName(),
            item.getItemNameEn(),
            item.getItemCode(),
            item.getLineNumber(),
            item.getItemLevel(),
            item.getParentItem() != null ? FinancialReportItemDto.from(item.getParentItem()) : null,
            item.getCurrentAmount(),
            item.getPreviousAmount(),
            item.getChangeAmount(),
            item.getChangeRate(),
            item.getCompositionRatio(),
            item.getItemType(),
            item.getCalculationFormula(),
            item.getIsVisible(),
            item.getIsBold(),
            item.getIndentLevel(),
            item.getNote(),
            item.isIncrease(),
            item.isDecrease(),
            item.isTotalItem(),
            item.isAccountItem(),
            item.getFormattedCurrentAmount(),
            item.getFormattedPreviousAmount()
        );
    }
}




