package com.erp.sales.dto;

import com.erp.sales.entity.Contract;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 계약 관련 DTO 클래스들
 */
public class ContractDto {

    /**
     * 계약 생성 요청 DTO
     */
    public record ContractCreateDto(
            @NotNull(message = "회사 ID는 필수입니다")
            Long companyId,

            @NotNull(message = "고객 ID는 필수입니다")
            Long customerId,

            Long orderId,

            @NotBlank(message = "계약제목은 필수입니다")
            @Size(max = 200, message = "계약제목은 200자 이내여야 합니다")
            String title,

            @Size(max = 1000, message = "계약설명은 1000자 이내여야 합니다")
            String description,

            @NotNull(message = "계약유형은 필수입니다")
            Contract.ContractType contractType,

            @NotNull(message = "시작일자는 필수입니다")
            LocalDate startDate,

            @NotNull(message = "종료일자는 필수입니다")
            LocalDate endDate,

            LocalDate signedDate,
            LocalDate effectiveDate,

            // 담당자 정보
            Long ourRepresentativeId,

            @Size(max = 100, message = "당사 담당자명은 100자 이내여야 합니다")
            String ourRepresentativeName,

            @Size(max = 100, message = "당사 담당자 부서는 100자 이내여야 합니다")
            String ourRepresentativeDepartment,

            @Size(max = 100, message = "고객 담당자명은 100자 이내여야 합니다")
            String customerRepresentativeName,

            @Size(max = 100, message = "고객 담당자 부서는 100자 이내여야 합니다")
            String customerRepresentativeDepartment,

            @Size(max = 20, message = "고객 담당자 연락처는 20자 이내여야 합니다")
            String customerRepresentativePhone,

            @Email(message = "올바른 이메일 형식이어야 합니다")
            @Size(max = 100, message = "고객 담당자 이메일은 100자 이내여야 합니다")
            String customerRepresentativeEmail,

            // 계약 금액 정보
            @NotNull(message = "계약금액은 필수입니다")
            @DecimalMin(value = "0", message = "계약금액은 0 이상이어야 합니다")
            BigDecimal contractAmount,

            @DecimalMin(value = "0", message = "세율은 0 이상이어야 합니다")
            @DecimalMax(value = "100", message = "세율은 100 이하여야 합니다")
            BigDecimal taxRate,

            // 결제 조건
            @Size(max = 100, message = "결제조건은 100자 이내여야 합니다")
            String paymentTerms,

            @Size(max = 100, message = "결제방법은 100자 이내여야 합니다")
            String paymentMethod,

            Integer paymentCycleDays,

            // 갱신 조건
            Contract.RenewalType renewalType,
            Integer renewalPeriodMonths,
            Integer renewalNoticeDays,
            Boolean autoRenewalEnabled,

            // 계약 조건
            String termsAndConditions,
            String specialClauses,

            @Size(max = 500, message = "배송조건은 500자 이내여야 합니다")
            String deliveryTerms,

            @Size(max = 500, message = "보증조건은 500자 이내여야 합니다")
            String warrantyTerms,

            @Size(max = 500, message = "책임조건은 500자 이내여야 합니다")
            String liabilityTerms,

            @Size(max = 500, message = "해지조건은 500자 이내여야 합니다")
            String terminationTerms,

            // 첨부 파일
            @Size(max = 1000, message = "첨부파일 경로는 1000자 이내여야 합니다")
            String attachmentPaths,

            // 추가 정보
            @Size(max = 1000, message = "비고는 1000자 이내여야 합니다")
            String remarks,

            @Size(max = 500, message = "태그는 500자 이내여야 합니다")
            String tags,

            Integer sortOrder,
            String metadata
    ) {
        public ContractCreateDto {
            if (taxRate == null) taxRate = new BigDecimal("10.00");
            if (renewalType == null) renewalType = Contract.RenewalType.MANUAL;
            if (renewalNoticeDays == null) renewalNoticeDays = 30;
            if (autoRenewalEnabled == null) autoRenewalEnabled = false;
            if (sortOrder == null) sortOrder = 0;
        }
    }

    /**
     * 계약 수정 요청 DTO
     */
    public record ContractUpdateDto(
            @Size(max = 200, message = "계약제목은 200자 이내여야 합니다")
            String title,

            @Size(max = 1000, message = "계약설명은 1000자 이내여야 합니다")
            String description,

            LocalDate endDate,
            LocalDate signedDate,
            LocalDate effectiveDate,

            // 담당자 정보
            Long ourRepresentativeId,

            @Size(max = 100, message = "당사 담당자명은 100자 이내여야 합니다")
            String ourRepresentativeName,

            @Size(max = 100, message = "당사 담당자 부서는 100자 이내여야 합니다")
            String ourRepresentativeDepartment,

            @Size(max = 100, message = "고객 담당자명은 100자 이내여야 합니다")
            String customerRepresentativeName,

            @Size(max = 100, message = "고객 담당자 부서는 100자 이내여야 합니다")
            String customerRepresentativeDepartment,

            @Size(max = 20, message = "고객 담당자 연락처는 20자 이내여야 합니다")
            String customerRepresentativePhone,

            @Email(message = "올바른 이메일 형식이어야 합니다")
            @Size(max = 100, message = "고객 담당자 이메일은 100자 이내여야 합니다")
            String customerRepresentativeEmail,

            // 계약 금액 정보
            @DecimalMin(value = "0", message = "계약금액은 0 이상이어야 합니다")
            BigDecimal contractAmount,

            @DecimalMin(value = "0", message = "세율은 0 이상이어야 합니다")
            @DecimalMax(value = "100", message = "세율은 100 이하여야 합니다")
            BigDecimal taxRate,

            // 결제 조건
            @Size(max = 100, message = "결제조건은 100자 이내여야 합니다")
            String paymentTerms,

            @Size(max = 100, message = "결제방법은 100자 이내여야 합니다")
            String paymentMethod,

            Integer paymentCycleDays,

            // 갱신 조건
            Contract.RenewalType renewalType,
            Integer renewalPeriodMonths,
            Integer renewalNoticeDays,
            Boolean autoRenewalEnabled,

            // 계약 조건
            String termsAndConditions,
            String specialClauses,

            @Size(max = 500, message = "배송조건은 500자 이내여야 합니다")
            String deliveryTerms,

            @Size(max = 500, message = "보증조건은 500자 이내여야 합니다")
            String warrantyTerms,

            @Size(max = 500, message = "책임조건은 500자 이내여야 합니다")
            String liabilityTerms,

            @Size(max = 500, message = "해지조건은 500자 이내여야 합니다")
            String terminationTerms,

            // 첨부 파일
            @Size(max = 1000, message = "첨부파일 경로는 1000자 이내여야 합니다")
            String attachmentPaths,

            // 추가 정보
            @Size(max = 1000, message = "비고는 1000자 이내여야 합니다")
            String remarks,

            @Size(max = 500, message = "태그는 500자 이내여야 합니다")
            String tags,

            Integer sortOrder,
            String metadata
    ) {}

    /**
     * 계약 응답 DTO
     */
    public record ContractResponseDto(
            Long id,
            String contractNumber,
            Long companyId,
            String companyName,
            Long customerId,
            String customerCode,
            String customerName,
            Long orderId,
            String orderNumber,
            String title,
            String description,
            Contract.ContractStatus contractStatus,
            String contractStatusDescription,
            Contract.ContractType contractType,
            String contractTypeDescription,
            LocalDate startDate,
            LocalDate endDate,
            LocalDate signedDate,
            LocalDate effectiveDate,

            // 담당자 정보
            Long ourRepresentativeId,
            String ourRepresentativeName,
            String ourRepresentativeDepartment,
            String customerRepresentativeName,
            String customerRepresentativeDepartment,
            String customerRepresentativePhone,
            String customerRepresentativeEmail,

            // 계약 금액 정보
            BigDecimal contractAmount,
            BigDecimal taxAmount,
            BigDecimal taxRate,
            BigDecimal totalAmount,

            // 결제 조건
            String paymentTerms,
            String paymentMethod,
            Integer paymentCycleDays,

            // 갱신 조건
            Contract.RenewalType renewalType,
            String renewalTypeDescription,
            Integer renewalPeriodMonths,
            Integer renewalNoticeDays,
            Boolean autoRenewalEnabled,

            // 계약 조건
            String termsAndConditions,
            String specialClauses,
            String deliveryTerms,
            String warrantyTerms,
            String liabilityTerms,
            String terminationTerms,

            // 상태 관리
            LocalDateTime approvedDate,
            LocalDateTime activatedDate,
            LocalDateTime suspendedDate,
            LocalDateTime completedDate,
            LocalDateTime terminatedDate,
            String terminationReason,

            // 첨부 파일
            String attachmentPaths,

            // 추가 정보
            String remarks,
            String tags,
            Integer sortOrder,
            String metadata,
            String createdAt,
            String updatedAt,

            // 계산 필드
            Long contractDurationDays,
            Long daysUntilExpiry,
            Boolean isExpired,
            Boolean isExpiringSoon,
            Boolean needsRenewalNotice,
            String contractSummary,
            Double progress
    ) {}

    /**
     * 계약 요약 DTO (목록용)
     */
    public record ContractSummaryDto(
            Long id,
            String contractNumber,
            Long customerId,
            String customerCode,
            String customerName,
            String title,
            Contract.ContractStatus contractStatus,
            String contractStatusDescription,
            Contract.ContractType contractType,
            String contractTypeDescription,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal totalAmount,
            String ourRepresentativeName,
            String createdAt,
            Boolean isExpired,
            Boolean isExpiringSoon,
            Boolean needsRenewalNotice,
            Long daysUntilExpiry
    ) {}

    /**
     * 계약 검색 DTO
     */
    public record ContractSearchDto(
            String searchTerm,
            Long customerId,
            Contract.ContractStatus contractStatus,
            Contract.ContractType contractType,
            Contract.RenewalType renewalType,
            Long ourRepresentativeId,
            LocalDate startDateFrom,
            LocalDate startDateTo,
            LocalDate endDateFrom,
            LocalDate endDateTo,
            LocalDate signedDateFrom,
            LocalDate signedDateTo,
            BigDecimal contractAmountFrom,
            BigDecimal contractAmountTo,
            Boolean isExpired,
            Boolean isExpiringSoon,
            Boolean needsRenewalNotice,
            String tags
    ) {}

    /**
     * 계약 통계 DTO
     */
    public record ContractStatsDto(
            Long totalContracts,
            Long activeContracts,
            Long expiredContracts,
            Long expiringSoonContracts,
            Long completedContracts,
            Long terminatedContracts,
            Long needsRenewalNoticeContracts,
            BigDecimal totalContractValue,
            BigDecimal activeContractValue,
            BigDecimal averageContractValue,
            Double averageContractDuration,
            Long autoRenewalContracts
    ) {}

    /**
     * 계약 상태 변경 DTO
     */
    public record ContractStatusChangeDto(
            @NotNull(message = "계약상태는 필수입니다")
            Contract.ContractStatus contractStatus,

            @Size(max = 500, message = "변경사유는 500자 이내여야 합니다")
            String reason
    ) {}

    /**
     * 계약 승인 DTO
     */
    public record ContractApprovalDto(
            @NotNull(message = "승인 여부는 필수입니다")
            Boolean approved,

            @Size(max = 500, message = "승인/거부 사유는 500자 이내여야 합니다")
            String reason,

            LocalDateTime approvalDate
    ) {}

    /**
     * 계약 중단 DTO
     */
    public record ContractSuspensionDto(
            @NotBlank(message = "중단사유는 필수입니다")
            @Size(max = 500, message = "중단사유는 500자 이내여야 합니다")
            String suspensionReason,

            LocalDate suspensionDate,
            LocalDate resumeDate
    ) {}

    /**
     * 계약 해지 DTO
     */
    public record ContractTerminationDto(
            @NotBlank(message = "해지사유는 필수입니다")
            @Size(max = 500, message = "해지사유는 500자 이내여야 합니다")
            String terminationReason,

            LocalDate terminationDate,
            Boolean immediateTermination
    ) {
        public ContractTerminationDto {
            if (immediateTermination == null) immediateTermination = false;
        }
    }

    /**
     * 계약 갱신 DTO
     */
    public record ContractRenewalDto(
            @NotNull(message = "새로운 종료일자는 필수입니다")
            LocalDate newEndDate,

            @Size(max = 200, message = "갱신 계약 제목은 200자 이내여야 합니다")
            String renewedTitle,

            @DecimalMin(value = "0", message = "갱신 계약금액은 0 이상이어야 합니다")
            BigDecimal renewedContractAmount,

            @Size(max = 500, message = "갱신 사유는 500자 이내여야 합니다")
            String renewalReason,

            Boolean updateTerms,
            String updatedTermsAndConditions
    ) {
        public ContractRenewalDto {
            if (updateTerms == null) updateTerms = false;
        }
    }

    /**
     * 계약 템플릿 DTO
     */
    public record ContractTemplateDto(
            @NotBlank(message = "템플릿명은 필수입니다")
            @Size(max = 100, message = "템플릿명은 100자 이내여야 합니다")
            String templateName,

            @Size(max = 500, message = "템플릿 설명은 500자 이내여야 합니다")
            String templateDescription,

            Contract.ContractType contractType,
            String defaultTermsAndConditions,
            String defaultSpecialClauses,
            String defaultDeliveryTerms,
            String defaultWarrantyTerms,
            String defaultLiabilityTerms,
            String defaultTerminationTerms,
            Boolean isDefault
    ) {
        public ContractTemplateDto {
            if (isDefault == null) isDefault = false;
        }
    }

    /**
     * 계약 알림 DTO
     */
    public record ContractAlertDto(
            Long contractId,
            String contractNumber,
            String customerName,
            String alertType,
            String alertMessage,
            LocalDate alertDate,
            Integer daysUntilExpiry,
            Boolean isUrgent
    ) {}

    /**
     * 계약 이력 DTO
     */
    public record ContractHistoryDto(
            Long contractId,
            String action,
            String description,
            LocalDateTime actionDate,
            String actionBy,
            String previousValue,
            String newValue
    ) {}
}




