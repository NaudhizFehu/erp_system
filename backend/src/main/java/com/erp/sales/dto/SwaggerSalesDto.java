package com.erp.sales.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 영업관리 Swagger DTO 정의
 * API 문서화를 위한 스키마 정의입니다
 */
public class SwaggerSalesDto {

    /**
     * 고객 등록 요청 DTO
     */
    @Schema(
        name = "CustomerCreateRequest",
        description = "고객 등록 요청 정보",
        example = """
            {
              "customerCode": "CUST001",
              "customerName": "ABC 회사",
              "customerType": "CORPORATE",
              "contactPerson": "김담당",
              "contactEmail": "kim@abc.com",
              "contactPhone": "02-1234-5678",
              "address": "서울시 강남구 테헤란로 123",
              "salesRepresentativeId": 1,
              "creditLimit": 50000000
            }
            """
    )
    public record CustomerCreateRequestSchema(
        @Schema(
            description = "고객 코드 (고유 식별자)",
            example = "CUST001",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 20
        )
        @NotBlank(message = "고객 코드는 필수 입력 항목입니다")
        @Size(max = 20, message = "고객 코드는 20자 이내여야 합니다")
        String customerCode,

        @Schema(
            description = "고객명 (회사명 또는 개인명)",
            example = "ABC 회사",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 100
        )
        @NotBlank(message = "고객명은 필수 입력 항목입니다")
        @Size(max = 100, message = "고객명은 100자 이내여야 합니다")
        String customerName,

        @Schema(
            description = "고객 유형",
            example = "CORPORATE",
            requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = {"INDIVIDUAL", "CORPORATE", "GOVERNMENT"}
        )
        @NotNull(message = "고객 유형은 필수 선택 항목입니다")
        String customerType,

        @Schema(
            description = "담당자명",
            example = "김담당",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 50
        )
        @NotBlank(message = "담당자명은 필수 입력 항목입니다")
        @Size(max = 50, message = "담당자명은 50자 이내여야 합니다")
        String contactPerson,

        @Schema(
            description = "담당자 이메일",
            example = "kim@abc.com",
            requiredMode = Schema.RequiredMode.REQUIRED,
            format = "email"
        )
        @NotBlank(message = "담당자 이메일은 필수 입력 항목입니다")
        @Email(message = "올바른 이메일 형식이 아닙니다")
        String contactEmail,

        @Schema(
            description = "담당자 전화번호",
            example = "02-1234-5678",
            requiredMode = Schema.RequiredMode.REQUIRED,
            pattern = "^\\d{2,3}-\\d{3,4}-\\d{4}$"
        )
        @NotBlank(message = "담당자 전화번호는 필수 입력 항목입니다")
        String contactPhone,

        @Schema(
            description = "주소",
            example = "서울시 강남구 테헤란로 123",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 200
        )
        @NotBlank(message = "주소는 필수 입력 항목입니다")
        @Size(max = 200, message = "주소는 200자 이내여야 합니다")
        String address,

        @Schema(
            description = "담당 영업사원 ID",
            example = "1"
        )
        Long salesRepresentativeId,

        @Schema(
            description = "신용 한도 (원)",
            example = "50000000",
            minimum = "0"
        )
        @Min(value = 0, message = "신용 한도는 0 이상이어야 합니다")
        BigDecimal creditLimit
    ) {}

    /**
     * 고객 응답 DTO
     */
    @Schema(
        name = "CustomerResponse",
        description = "고객 정보 응답",
        example = """
            {
              "id": 1,
              "customerCode": "CUST001",
              "customerName": "ABC 회사",
              "customerType": "CORPORATE",
              "contactPerson": "김담당",
              "contactEmail": "kim@abc.com",
              "contactPhone": "02-1234-5678",
              "address": "서울시 강남구 테헤란로 123",
              "status": "ACTIVE",
              "grade": "GOLD",
              "creditLimit": 50000000,
              "outstandingBalance": 5000000,
              "salesRepresentativeId": 1,
              "salesRepresentativeName": "이영업",
              "createdAt": "2023-12-01T10:30:00Z",
              "updatedAt": "2023-12-01T10:30:00Z"
            }
            """
    )
    public record CustomerResponseSchema(
        @Schema(description = "고객 ID", example = "1")
        Long id,

        @Schema(description = "고객 코드", example = "CUST001")
        String customerCode,

        @Schema(description = "고객명", example = "ABC 회사")
        String customerName,

        @Schema(description = "고객 유형", example = "CORPORATE")
        String customerType,

        @Schema(description = "담당자명", example = "김담당")
        String contactPerson,

        @Schema(description = "담당자 이메일", example = "kim@abc.com")
        String contactEmail,

        @Schema(description = "담당자 전화번호", example = "02-1234-5678")
        String contactPhone,

        @Schema(description = "주소", example = "서울시 강남구 테헤란로 123")
        String address,

        @Schema(description = "고객 상태", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "DORMANT", "BLACKLISTED"})
        String status,

        @Schema(description = "고객 등급", example = "GOLD", allowableValues = {"BRONZE", "SILVER", "GOLD", "PLATINUM", "VIP"})
        String grade,

        @Schema(description = "신용 한도", example = "50000000")
        BigDecimal creditLimit,

        @Schema(description = "미수금 잔액", example = "5000000")
        BigDecimal outstandingBalance,

        @Schema(description = "담당 영업사원 ID", example = "1")
        Long salesRepresentativeId,

        @Schema(description = "담당 영업사원명", example = "이영업")
        String salesRepresentativeName,

        @Schema(description = "생성일시", example = "2023-12-01T10:30:00Z")
        String createdAt,

        @Schema(description = "수정일시", example = "2023-12-01T10:30:00Z")
        String updatedAt
    ) {}

    /**
     * 주문 생성 요청 DTO
     */
    @Schema(
        name = "OrderCreateRequest",
        description = "주문 생성 요청 정보",
        example = """
            {
              "customerId": 1,
              "deliveryAddress": "서울시 강남구 테헤란로 123",
              "deliveryContact": "김수령",
              "deliveryRequest": "문 앞에 놓아주세요",
              "discountAmount": 100000,
              "shippingFee": 3000,
              "orderItems": [
                {
                  "productId": 1,
                  "productName": "노트북 A형",
                  "quantity": 2,
                  "unitPrice": 1500000
                },
                {
                  "productId": 2,
                  "productName": "마우스 B형",
                  "quantity": 5,
                  "unitPrice": 50000
                }
              ]
            }
            """
    )
    public record OrderCreateRequestSchema(
        @Schema(
            description = "고객 ID",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "고객 ID는 필수 입력 항목입니다")
        Long customerId,

        @Schema(
            description = "배송 주소",
            example = "서울시 강남구 테헤란로 123",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 200
        )
        @NotBlank(message = "배송 주소는 필수 입력 항목입니다")
        @Size(max = 200, message = "배송 주소는 200자 이내여야 합니다")
        String deliveryAddress,

        @Schema(
            description = "배송지 연락처",
            example = "김수령",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 50
        )
        @NotBlank(message = "배송지 연락처는 필수 입력 항목입니다")
        @Size(max = 50, message = "배송지 연락처는 50자 이내여야 합니다")
        String deliveryContact,

        @Schema(
            description = "배송 요청사항",
            example = "문 앞에 놓아주세요",
            maxLength = 500
        )
        @Size(max = 500, message = "배송 요청사항은 500자 이내여야 합니다")
        String deliveryRequest,

        @Schema(
            description = "할인 금액 (원)",
            example = "100000",
            minimum = "0"
        )
        @Min(value = 0, message = "할인 금액은 0 이상이어야 합니다")
        BigDecimal discountAmount,

        @Schema(
            description = "배송비 (원)",
            example = "3000",
            minimum = "0"
        )
        @Min(value = 0, message = "배송비는 0 이상이어야 합니다")
        BigDecimal shippingFee,

        @Schema(
            description = "주문 상품 목록",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotEmpty(message = "주문 상품은 최소 1개 이상이어야 합니다")
        List<OrderItemCreateRequestSchema> orderItems
    ) {}

    /**
     * 주문 상품 생성 요청 DTO
     */
    @Schema(
        name = "OrderItemCreateRequest",
        description = "주문 상품 정보",
        example = """
            {
              "productId": 1,
              "productName": "노트북 A형",
              "quantity": 2,
              "unitPrice": 1500000
            }
            """
    )
    public record OrderItemCreateRequestSchema(
        @Schema(
            description = "상품 ID",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "상품 ID는 필수 입력 항목입니다")
        Long productId,

        @Schema(
            description = "상품명",
            example = "노트북 A형",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 100
        )
        @NotBlank(message = "상품명은 필수 입력 항목입니다")
        @Size(max = 100, message = "상품명은 100자 이내여야 합니다")
        String productName,

        @Schema(
            description = "주문 수량",
            example = "2",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minimum = "1"
        )
        @NotNull(message = "주문 수량은 필수 입력 항목입니다")
        @Min(value = 1, message = "주문 수량은 1 이상이어야 합니다")
        Integer quantity,

        @Schema(
            description = "단가 (원)",
            example = "1500000",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minimum = "0"
        )
        @NotNull(message = "단가는 필수 입력 항목입니다")
        @Min(value = 0, message = "단가는 0 이상이어야 합니다")
        BigDecimal unitPrice
    ) {}

    /**
     * 주문 응답 DTO
     */
    @Schema(
        name = "OrderResponse",
        description = "주문 정보 응답",
        example = """
            {
              "id": 1,
              "orderNumber": "20231201-0001",
              "customerId": 1,
              "customerName": "ABC 회사",
              "orderDate": "2023-12-01",
              "deliveryAddress": "서울시 강남구 테헤란로 123",
              "deliveryContact": "김수령",
              "deliveryRequest": "문 앞에 놓아주세요",
              "orderStatus": "PENDING",
              "paymentStatus": "PENDING",
              "totalAmount": 3250000,
              "discountAmount": 100000,
              "shippingFee": 3000,
              "orderItems": [
                {
                  "id": 1,
                  "productId": 1,
                  "productName": "노트북 A형",
                  "quantity": 2,
                  "unitPrice": 1500000,
                  "totalPrice": 3000000,
                  "fulfillmentStatus": "PENDING"
                }
              ],
              "createdAt": "2023-12-01T10:30:00Z",
              "updatedAt": "2023-12-01T10:30:00Z"
            }
            """
    )
    public record OrderResponseSchema(
        @Schema(description = "주문 ID", example = "1")
        Long id,

        @Schema(description = "주문 번호", example = "20231201-0001")
        String orderNumber,

        @Schema(description = "고객 ID", example = "1")
        Long customerId,

        @Schema(description = "고객명", example = "ABC 회사")
        String customerName,

        @Schema(description = "주문 일자", example = "2023-12-01")
        LocalDate orderDate,

        @Schema(description = "배송 주소", example = "서울시 강남구 테헤란로 123")
        String deliveryAddress,

        @Schema(description = "배송지 연락처", example = "김수령")
        String deliveryContact,

        @Schema(description = "배송 요청사항", example = "문 앞에 놓아주세요")
        String deliveryRequest,

        @Schema(description = "주문 상태", example = "PENDING", allowableValues = {"DRAFT", "PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "COMPLETED", "CANCELLED", "RETURNED"})
        String orderStatus,

        @Schema(description = "결제 상태", example = "PENDING", allowableValues = {"PENDING", "PAID", "PARTIALLY_PAID", "REFUNDED", "CANCELLED", "OVERDUE"})
        String paymentStatus,

        @Schema(description = "총 주문 금액", example = "3250000")
        BigDecimal totalAmount,

        @Schema(description = "할인 금액", example = "100000")
        BigDecimal discountAmount,

        @Schema(description = "배송비", example = "3000")
        BigDecimal shippingFee,

        @Schema(description = "주문 상품 목록")
        List<OrderItemResponseSchema> orderItems,

        @Schema(description = "생성일시", example = "2023-12-01T10:30:00Z")
        String createdAt,

        @Schema(description = "수정일시", example = "2023-12-01T10:30:00Z")
        String updatedAt
    ) {}

    /**
     * 주문 상품 응답 DTO
     */
    @Schema(
        name = "OrderItemResponse",
        description = "주문 상품 정보 응답"
    )
    public record OrderItemResponseSchema(
        @Schema(description = "주문 상품 ID", example = "1")
        Long id,

        @Schema(description = "상품 ID", example = "1")
        Long productId,

        @Schema(description = "상품명", example = "노트북 A형")
        String productName,

        @Schema(description = "주문 수량", example = "2")
        Integer quantity,

        @Schema(description = "단가", example = "1500000")
        BigDecimal unitPrice,

        @Schema(description = "총 가격", example = "3000000")
        BigDecimal totalPrice,

        @Schema(description = "이행 상태", example = "PENDING", allowableValues = {"PENDING", "FULFILLED", "BACKORDERED"})
        String fulfillmentStatus
    ) {}

    /**
     * 견적서 생성 요청 DTO
     */
    @Schema(
        name = "QuoteCreateRequest",
        description = "견적서 생성 요청 정보",
        example = """
            {
              "customerId": 1,
              "validUntil": "2023-12-31",
              "priority": "HIGH",
              "quoteItems": [
                {
                  "productId": 1,
                  "productName": "노트북 A형",
                  "quantity": 10,
                  "unitPrice": 1500000,
                  "discountRate": 5.0
                }
              ],
              "discountAmount": 500000,
              "taxRate": 10.0
            }
            """
    )
    public record QuoteCreateRequestSchema(
        @Schema(
            description = "고객 ID",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "고객 ID는 필수 입력 항목입니다")
        Long customerId,

        @Schema(
            description = "견적 유효 기한",
            example = "2023-12-31",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "견적 유효 기한은 필수 입력 항목입니다")
        LocalDate validUntil,

        @Schema(
            description = "우선순위",
            example = "HIGH",
            allowableValues = {"LOW", "MEDIUM", "HIGH", "URGENT"}
        )
        String priority,

        @Schema(
            description = "견적 상품 목록",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotEmpty(message = "견적 상품은 최소 1개 이상이어야 합니다")
        List<QuoteItemCreateRequestSchema> quoteItems,

        @Schema(
            description = "할인 금액",
            example = "500000",
            minimum = "0"
        )
        BigDecimal discountAmount,

        @Schema(
            description = "세율 (%)",
            example = "10.0",
            minimum = "0",
            maximum = "100"
        )
        Double taxRate
    ) {}

    /**
     * 견적 상품 생성 요청 DTO
     */
    @Schema(
        name = "QuoteItemCreateRequest",
        description = "견적 상품 정보"
    )
    public record QuoteItemCreateRequestSchema(
        @Schema(description = "상품 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "상품 ID는 필수 입력 항목입니다")
        Long productId,

        @Schema(description = "상품명", example = "노트북 A형", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "상품명은 필수 입력 항목입니다")
        String productName,

        @Schema(description = "수량", example = "10", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1")
        @Min(value = 1, message = "수량은 1 이상이어야 합니다")
        Integer quantity,

        @Schema(description = "단가", example = "1500000", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "0")
        @Min(value = 0, message = "단가는 0 이상이어야 합니다")
        BigDecimal unitPrice,

        @Schema(description = "할인율 (%)", example = "5.0", minimum = "0", maximum = "100")
        Double discountRate
    ) {}

    /**
     * 계약 생성 요청 DTO
     */
    @Schema(
        name = "ContractCreateRequest",
        description = "계약 생성 요청 정보",
        example = """
            {
              "contractNumber": "CON-2023-001",
              "customerId": 1,
              "contractType": "SALES",
              "startDate": "2023-12-01",
              "endDate": "2024-11-30",
              "renewalType": "AUTO",
              "renewalPeriodMonths": 12,
              "totalAmount": 100000000,
              "description": "연간 소프트웨어 라이선스 계약",
              "salesRepresentativeId": 1
            }
            """
    )
    public record ContractCreateRequestSchema(
        @Schema(description = "계약 번호", example = "CON-2023-001", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "계약 번호는 필수 입력 항목입니다")
        String contractNumber,

        @Schema(description = "고객 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "고객 ID는 필수 입력 항목입니다")
        Long customerId,

        @Schema(description = "계약 유형", example = "SALES", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"SALES", "SERVICE", "MAINTENANCE", "LEASE"})
        @NotNull(message = "계약 유형은 필수 선택 항목입니다")
        String contractType,

        @Schema(description = "계약 시작일", example = "2023-12-01", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "계약 시작일은 필수 입력 항목입니다")
        LocalDate startDate,

        @Schema(description = "계약 종료일", example = "2024-11-30", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "계약 종료일은 필수 입력 항목입니다")
        LocalDate endDate,

        @Schema(description = "갱신 유형", example = "AUTO", allowableValues = {"AUTO", "MANUAL", "NONE"})
        String renewalType,

        @Schema(description = "갱신 기간 (개월)", example = "12", minimum = "1")
        Integer renewalPeriodMonths,

        @Schema(description = "계약 총액", example = "100000000", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "0")
        @NotNull(message = "계약 총액은 필수 입력 항목입니다")
        @Min(value = 0, message = "계약 총액은 0 이상이어야 합니다")
        BigDecimal totalAmount,

        @Schema(description = "계약 설명", example = "연간 소프트웨어 라이선스 계약", maxLength = 1000)
        String description,

        @Schema(description = "담당 영업사원 ID", example = "1")
        Long salesRepresentativeId
    ) {}
}




