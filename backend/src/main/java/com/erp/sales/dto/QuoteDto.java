package com.erp.sales.dto;

import com.erp.sales.entity.Quote;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 견적 관련 DTO 클래스들
 */
public class QuoteDto {

    /**
     * 견적 생성 요청 DTO
     */
    public record QuoteCreateDto(
            @NotNull(message = "회사 ID는 필수입니다")
            Long companyId,

            @NotNull(message = "고객 ID는 필수입니다")
            Long customerId,

            @NotNull(message = "견적일자는 필수입니다")
            LocalDate quoteDate,

            @NotNull(message = "유효기한은 필수입니다")
            LocalDate validUntil,

            Quote.QuotePriority priority,

            @Size(max = 200, message = "제목은 200자 이내여야 합니다")
            String title,

            @Size(max = 1000, message = "설명은 1000자 이내여야 합니다")
            String description,

            // 영업 담당자 정보
            Long salesRepId,
            String salesRepName,

            @Size(max = 20, message = "영업담당자 연락처는 20자 이내여야 합니다")
            String salesRepPhone,

            @Email(message = "올바른 이메일 형식이어야 합니다")
            @Size(max = 100, message = "영업담당자 이메일은 100자 이내여야 합니다")
            String salesRepEmail,

            // 할인 정보
            @DecimalMin(value = "0", message = "할인율은 0 이상이어야 합니다")
            @DecimalMax(value = "100", message = "할인율은 100 이하여야 합니다")
            BigDecimal discountRate,

            @DecimalMin(value = "0", message = "할인금액은 0 이상이어야 합니다")
            BigDecimal discountAmount,

            @DecimalMin(value = "0", message = "세율은 0 이상이어야 합니다")
            @DecimalMax(value = "100", message = "세율은 100 이하여야 합니다")
            BigDecimal taxRate,

            // 배송 정보
            @Size(max = 200, message = "배송주소는 200자 이내여야 합니다")
            String deliveryAddress,

            LocalDate deliveryDate,

            @Size(max = 100, message = "배송방법은 100자 이내여야 합니다")
            String deliveryMethod,

            @DecimalMin(value = "0", message = "배송비는 0 이상이어야 합니다")
            BigDecimal deliveryFee,

            // 결제 정보
            @Size(max = 100, message = "결제조건은 100자 이내여야 합니다")
            String paymentTerms,

            @Size(max = 100, message = "결제방법은 100자 이내여야 합니다")
            String paymentMethod,

            // 특별 조건
            @Size(max = 1000, message = "특별조건은 1000자 이내여야 합니다")
            String specialTerms,

            @Size(max = 1000, message = "비고는 1000자 이내여야 합니다")
            String remarks,

            // 견적 항목들
            @Valid
            @NotEmpty(message = "견적 항목은 최소 1개 이상이어야 합니다")
            List<QuoteItemCreateDto> quoteItems,

            // 추가 정보
            @Size(max = 500, message = "태그는 500자 이내여야 합니다")
            String tags,

            Integer sortOrder,
            String metadata
    ) {
        public QuoteCreateDto {
            if (priority == null) priority = Quote.QuotePriority.NORMAL;
            if (discountRate == null) discountRate = BigDecimal.ZERO;
            if (discountAmount == null) discountAmount = BigDecimal.ZERO;
            if (taxRate == null) taxRate = new BigDecimal("10.00");
            if (deliveryFee == null) deliveryFee = BigDecimal.ZERO;
            if (sortOrder == null) sortOrder = 0;
        }
    }

    /**
     * 견적 항목 생성 DTO
     */
    public record QuoteItemCreateDto(
            Long productId,

            @NotBlank(message = "상품코드는 필수입니다")
            String productCode,

            @NotBlank(message = "상품명은 필수입니다")
            String productName,

            String productDescription,

            @NotNull(message = "수량은 필수입니다")
            @Min(value = 1, message = "수량은 1 이상이어야 합니다")
            Integer quantity,

            @NotBlank(message = "단위는 필수입니다")
            String unit,

            @NotNull(message = "단가는 필수입니다")
            @DecimalMin(value = "0", message = "단가는 0 이상이어야 합니다")
            BigDecimal unitPrice,

            @DecimalMin(value = "0", message = "할인율은 0 이상이어야 합니다")
            @DecimalMax(value = "100", message = "할인율은 100 이하여야 합니다")
            BigDecimal discountRate,

            @DecimalMin(value = "0", message = "할인금액은 0 이상이어야 합니다")
            BigDecimal discountAmount,

            String remarks,
            Integer sortOrder
    ) {
        public QuoteItemCreateDto {
            if (discountRate == null) discountRate = BigDecimal.ZERO;
            if (discountAmount == null) discountAmount = BigDecimal.ZERO;
            if (sortOrder == null) sortOrder = 0;
        }
    }

    /**
     * 견적 수정 요청 DTO
     */
    public record QuoteUpdateDto(
            LocalDate validUntil,
            Quote.QuotePriority priority,

            @Size(max = 200, message = "제목은 200자 이내여야 합니다")
            String title,

            @Size(max = 1000, message = "설명은 1000자 이내여야 합니다")
            String description,

            // 영업 담당자 정보
            Long salesRepId,
            String salesRepName,

            @Size(max = 20, message = "영업담당자 연락처는 20자 이내여야 합니다")
            String salesRepPhone,

            @Email(message = "올바른 이메일 형식이어야 합니다")
            @Size(max = 100, message = "영업담당자 이메일은 100자 이내여야 합니다")
            String salesRepEmail,

            // 할인 정보
            @DecimalMin(value = "0", message = "할인율은 0 이상이어야 합니다")
            @DecimalMax(value = "100", message = "할인율은 100 이하여야 합니다")
            BigDecimal discountRate,

            @DecimalMin(value = "0", message = "할인금액은 0 이상이어야 합니다")
            BigDecimal discountAmount,

            @DecimalMin(value = "0", message = "세율은 0 이상이어야 합니다")
            @DecimalMax(value = "100", message = "세율은 100 이하여야 합니다")
            BigDecimal taxRate,

            // 배송 정보
            @Size(max = 200, message = "배송주소는 200자 이내여야 합니다")
            String deliveryAddress,

            LocalDate deliveryDate,

            @Size(max = 100, message = "배송방법은 100자 이내여야 합니다")
            String deliveryMethod,

            @DecimalMin(value = "0", message = "배송비는 0 이상이어야 합니다")
            BigDecimal deliveryFee,

            // 결제 정보
            @Size(max = 100, message = "결제조건은 100자 이내여야 합니다")
            String paymentTerms,

            @Size(max = 100, message = "결제방법은 100자 이내여야 합니다")
            String paymentMethod,

            // 특별 조건
            @Size(max = 1000, message = "특별조건은 1000자 이내여야 합니다")
            String specialTerms,

            @Size(max = 1000, message = "비고는 1000자 이내여야 합니다")
            String remarks,

            // 추가 정보
            @Size(max = 500, message = "태그는 500자 이내여야 합니다")
            String tags,

            Integer sortOrder,
            String metadata
    ) {}

    /**
     * 견적 응답 DTO
     */
    public record QuoteResponseDto(
            Long id,
            String quoteNumber,
            Long companyId,
            String companyName,
            Long customerId,
            String customerCode,
            String customerName,
            LocalDate quoteDate,
            LocalDate validUntil,
            Quote.QuoteStatus quoteStatus,
            String quoteStatusDescription,
            Quote.QuotePriority priority,
            String priorityDescription,
            String title,
            String description,

            // 영업 담당자 정보
            Long salesRepId,
            String salesRepName,
            String salesRepPhone,
            String salesRepEmail,

            // 금액 정보
            BigDecimal subtotal,
            BigDecimal discountAmount,
            BigDecimal discountRate,
            BigDecimal taxAmount,
            BigDecimal taxRate,
            BigDecimal totalAmount,

            // 배송 정보
            String deliveryAddress,
            LocalDate deliveryDate,
            String deliveryMethod,
            BigDecimal deliveryFee,

            // 결제 정보
            String paymentTerms,
            String paymentMethod,

            // 특별 조건
            String specialTerms,
            String remarks,

            // 상태 관리
            LocalDate sentDate,
            LocalDate viewedDate,
            LocalDate respondedDate,
            LocalDate convertedDate,
            String rejectionReason,

            // 추가 정보
            String tags,
            Integer sortOrder,
            String metadata,
            String createdAt,
            String updatedAt,

            // 견적 항목들
            List<QuoteItemResponseDto> quoteItems,

            // 계산 필드
            Boolean isExpired,
            Boolean isEditable,
            Boolean isSendable,
            Boolean isConvertible,
            String quoteSummary,
            Integer totalItemCount,
            Long daysUntilExpiry
    ) {}

    /**
     * 견적 항목 응답 DTO
     */
    public record QuoteItemResponseDto(
            Long id,
            Long quoteId,
            Long productId,
            Integer lineNumber,
            String productCode,
            String productName,
            String productDescription,
            Integer quantity,
            String unit,
            BigDecimal unitPrice,
            BigDecimal discountRate,
            BigDecimal discountAmount,
            BigDecimal totalPrice,
            String remarks,
            Integer sortOrder,

            // 계산 필드
            Boolean hasDiscount,
            BigDecimal discountedUnitPrice,
            String lineSummary
    ) {}

    /**
     * 견적 요약 DTO (목록용)
     */
    public record QuoteSummaryDto(
            Long id,
            String quoteNumber,
            Long customerId,
            String customerCode,
            String customerName,
            LocalDate quoteDate,
            LocalDate validUntil,
            Quote.QuoteStatus quoteStatus,
            String quoteStatusDescription,
            Quote.QuotePriority priority,
            String priorityDescription,
            BigDecimal totalAmount,
            String salesRepName,
            Integer totalItemCount,
            String createdAt,
            Boolean isExpired,
            Boolean isEditable,
            Boolean isConvertible,
            Long daysUntilExpiry
    ) {}

    /**
     * 견적 검색 DTO
     */
    public record QuoteSearchDto(
            String searchTerm,
            Long customerId,
            Quote.QuoteStatus quoteStatus,
            Quote.QuotePriority priority,
            Long salesRepId,
            LocalDate quoteDateFrom,
            LocalDate quoteDateTo,
            LocalDate validUntilFrom,
            LocalDate validUntilTo,
            BigDecimal totalAmountFrom,
            BigDecimal totalAmountTo,
            Boolean isExpired,
            Boolean isConvertible,
            String tags
    ) {}

    /**
     * 견적 통계 DTO
     */
    public record QuoteStatsDto(
            Long totalQuotes,
            Long draftQuotes,
            Long sentQuotes,
            Long acceptedQuotes,
            Long rejectedQuotes,
            Long expiredQuotes,
            Long convertedQuotes,
            BigDecimal totalQuoteAmount,
            BigDecimal averageQuoteAmount,
            BigDecimal totalConvertedAmount,
            Double conversionRate,
            Double acceptanceRate,
            Long expiringSoonQuotes
    ) {}

    /**
     * 견적 상태 변경 DTO
     */
    public record QuoteStatusChangeDto(
            @NotNull(message = "견적상태는 필수입니다")
            Quote.QuoteStatus quoteStatus,

            @Size(max = 500, message = "변경사유는 500자 이내여야 합니다")
            String reason
    ) {}

    /**
     * 견적 발송 DTO
     */
    public record QuoteSendDto(
            @Email(message = "올바른 이메일 형식이어야 합니다")
            String recipientEmail,

            @Size(max = 200, message = "제목은 200자 이내여야 합니다")
            String emailSubject,

            @Size(max = 1000, message = "메시지는 1000자 이내여야 합니다")
            String emailMessage,

            Boolean sendCopy
    ) {}

    /**
     * 견적 승인/거부 DTO
     */
    public record QuoteApprovalDto(
            @NotNull(message = "응답 여부는 필수입니다")
            Boolean accepted,

            @Size(max = 500, message = "응답 사유는 500자 이내여야 합니다")
            String responseReason,

            LocalDate responseDate
    ) {}

    /**
     * 견적서 주문 전환 DTO
     */
    public record QuoteToOrderDto(
            @NotNull(message = "주문일자는 필수입니다")
            LocalDate orderDate,

            LocalDate requiredDate,
            String deliveryAddress,
            String deliveryMemo,
            String paymentTerms,
            String specialInstructions,
            String remarks
    ) {}

    /**
     * 견적 복사 DTO
     */
    public record QuoteCopyDto(
            Long targetCustomerId,

            @NotNull(message = "견적일자는 필수입니다")
            LocalDate quoteDate,

            @NotNull(message = "유효기한은 필수입니다")
            LocalDate validUntil,

            @Size(max = 200, message = "제목은 200자 이내여야 합니다")
            String title,

            Boolean copyItems
    ) {
        public QuoteCopyDto {
            if (copyItems == null) copyItems = true;
        }
    }

    /**
     * 견적 템플릿 DTO
     */
    public record QuoteTemplateDto(
            @NotBlank(message = "템플릿명은 필수입니다")
            @Size(max = 100, message = "템플릿명은 100자 이내여야 합니다")
            String templateName,

            @Size(max = 500, message = "템플릿 설명은 500자 이내여야 합니다")
            String templateDescription,

            Long categoryId,
            Boolean isDefault,
            List<QuoteItemCreateDto> defaultItems
    ) {
        public QuoteTemplateDto {
            if (isDefault == null) isDefault = false;
        }
    }
}
