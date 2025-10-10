package com.erp.sales.controller;

import com.erp.common.dto.ApiResponse;
import com.erp.sales.dto.CustomerDto;
import com.erp.sales.entity.Customer;
import com.erp.sales.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 고객 관리 REST Controller
 * 고객 관련 API 엔드포인트를 제공합니다
 */
@Slf4j
@RestController
@RequestMapping("/api/sales/customers")
@RequiredArgsConstructor
@Tag(name = "고객 관리", description = "고객 정보 관리 API")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "고객 생성", description = "새로운 고객을 생성합니다")
    public ResponseEntity<ApiResponse<CustomerDto.CustomerResponseDto>> createCustomer(
            @Valid @RequestBody CustomerDto.CustomerCreateDto createDto) {
        try {
            log.info("고객 생성 API 호출: {}", createDto.customerCode());
            CustomerDto.CustomerResponseDto response = customerService.createCustomer(createDto);
            return ResponseEntity.ok(ApiResponse.success("고객이 성공적으로 생성되었습니다", response));
        } catch (Exception e) {
            log.error("고객 생성 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("고객 생성에 실패했습니다: " + e.getMessage()));
        }
    }

    @PutMapping("/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "고객 수정", description = "기존 고객 정보를 수정합니다")
    public ResponseEntity<ApiResponse<CustomerDto.CustomerResponseDto>> updateCustomer(
            @Parameter(description = "고객 ID") @PathVariable Long customerId,
            @Valid @RequestBody CustomerDto.CustomerUpdateDto updateDto) {
        try {
            log.info("고객 수정 API 호출: ID={}", customerId);
            CustomerDto.CustomerResponseDto response = customerService.updateCustomer(customerId, updateDto);
            return ResponseEntity.ok(ApiResponse.success("고객 정보가 성공적으로 수정되었습니다", response));
        } catch (Exception e) {
            log.error("고객 수정 실패: ID={}, {}", customerId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("고객 수정에 실패했습니다: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "고객 삭제", description = "고객을 삭제합니다")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(
            @Parameter(description = "고객 ID") @PathVariable Long customerId) {
        try {
            log.info("고객 삭제 API 호출: ID={}", customerId);
            customerService.deleteCustomer(customerId);
            return ResponseEntity.ok(ApiResponse.success("고객이 성공적으로 삭제되었습니다"));
        } catch (Exception e) {
            log.error("고객 삭제 실패: ID={}, {}", customerId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("고객 삭제에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 고객 상세 조회 (구체적 경로를 먼저 배치)
     */
    @GetMapping("/{customerId}")
    // @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")  // 개발/테스트용으로 임시 비활성화
    @Operation(summary = "고객 상세 조회", description = "고객 상세 정보를 조회합니다")
    public ResponseEntity<ApiResponse<CustomerDto.CustomerResponseDto>> getCustomer(
            @Parameter(description = "고객 ID") @PathVariable Long customerId) {
        try {
            log.info("고객 상세 조회 API 호출: ID={}", customerId);
            CustomerDto.CustomerResponseDto response = customerService.getCustomer(customerId);
            return ResponseEntity.ok(ApiResponse.success("고객 정보 조회 성공", response));
        } catch (Exception e) {
            log.error("고객 조회 실패: ID={}, {}", customerId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("고객 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 전체 고객 목록 조회 (SUPER_ADMIN용, 구체적 경로 뒤에 배치)
     */
    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "전체 고객 목록 조회", description = "모든 회사의 고객을 조회합니다 (SUPER_ADMIN만)")
    public ResponseEntity<ApiResponse<Page<CustomerDto.CustomerSummaryDto>>> getAllCustomers(
            @PageableDefault(size = 100, sort = "createdAt") Pageable pageable) {
        try {
            log.info("전체 고객 목록 조회 요청 (SUPER_ADMIN)");
            Page<CustomerDto.CustomerSummaryDto> result = customerService.getAllCustomers(pageable);
            return ResponseEntity.ok(ApiResponse.success("전체 고객 조회 완료", result));
        } catch (Exception e) {
            log.error("전체 고객 조회 실패: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/code/{customerCode}")
    // @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")  // 개발/테스트용으로 임시 비활성화
    @Operation(summary = "고객코드로 조회", description = "고객코드로 고객 정보를 조회합니다")
    public ResponseEntity<ApiResponse<CustomerDto.CustomerResponseDto>> getCustomerByCode(
            @Parameter(description = "고객 코드") @PathVariable String customerCode) {
        try {
            log.info("고객코드 조회 API 호출: {}", customerCode);
            CustomerDto.CustomerResponseDto response = customerService.getCustomerByCode(customerCode);
            return ResponseEntity.ok(ApiResponse.success("고객 정보 조회 성공", response));
        } catch (Exception e) {
            log.error("고객코드 조회 실패: {}, {}", customerCode, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("고객 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "회사별 고객 목록 조회", description = "특정 회사의 고객 목록을 조회합니다")
    public ResponseEntity<ApiResponse<Page<CustomerDto.CustomerSummaryDto>>> getCustomersByCompany(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("회사별 고객 목록 조회 API 호출: companyId={}", companyId);
            Page<CustomerDto.CustomerSummaryDto> response = customerService.getCustomersByCompany(companyId, pageable);
            return ResponseEntity.ok(ApiResponse.success("고객 목록 조회 성공", response));
        } catch (Exception e) {
            log.error("고객 목록 조회 실패: companyId={}, {}", companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("고객 목록 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/company/{companyId}/search")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "고객 검색", description = "고객명, 코드, 이메일, 전화번호로 고객을 검색합니다")
    public ResponseEntity<ApiResponse<Page<CustomerDto.CustomerSummaryDto>>> searchCustomers(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @Parameter(description = "검색어") @RequestParam String searchTerm,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("고객 검색 API 호출: companyId={}, searchTerm={}", companyId, searchTerm);
            Page<CustomerDto.CustomerSummaryDto> response = customerService.searchCustomers(companyId, searchTerm, pageable);
            return ResponseEntity.ok(ApiResponse.success("고객 검색 성공", response));
        } catch (Exception e) {
            log.error("고객 검색 실패: companyId={}, searchTerm={}, {}", companyId, searchTerm, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("고객 검색에 실패했습니다: " + e.getMessage()));
        }
    }

    @PostMapping("/company/{companyId}/search/advanced")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "고객 고급 검색", description = "다양한 조건으로 고객을 검색합니다")
    public ResponseEntity<ApiResponse<Page<CustomerDto.CustomerSummaryDto>>> searchCustomersAdvanced(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @Valid @RequestBody CustomerDto.CustomerSearchDto searchDto,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("고객 고급 검색 API 호출: companyId={}", companyId);
            Page<CustomerDto.CustomerSummaryDto> response = customerService.searchCustomersAdvanced(companyId, searchDto, pageable);
            return ResponseEntity.ok(ApiResponse.success("고객 고급 검색 성공", response));
        } catch (Exception e) {
            log.error("고객 고급 검색 실패: companyId={}, {}", companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("고객 고급 검색에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/sales-manager/{salesManagerId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "영업담당자별 고객 조회", description = "특정 영업담당자의 고객을 조회합니다")
    public ResponseEntity<ApiResponse<Page<CustomerDto.CustomerSummaryDto>>> getCustomersBySalesManager(
            @Parameter(description = "영업담당자 ID") @PathVariable Long salesManagerId,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("영업담당자별 고객 조회 API 호출: salesManagerId={}", salesManagerId);
            Page<CustomerDto.CustomerSummaryDto> response = customerService.getCustomersBySalesManager(1L, salesManagerId, pageable);
            return ResponseEntity.ok(ApiResponse.success("영업담당자별 고객 조회 성공", response));
        } catch (Exception e) {
            log.error("영업담당자별 고객 조회 실패: salesManagerId={}, {}", salesManagerId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("영업담당자별 고객 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/company/{companyId}/type/{customerType}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "고객 유형별 조회", description = "특정 유형의 고객을 조회합니다")
    public ResponseEntity<ApiResponse<Page<CustomerDto.CustomerSummaryDto>>> getCustomersByType(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @Parameter(description = "고객 유형") @PathVariable String customerType,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("고객 유형별 조회 API 호출: companyId={}, type={}", companyId, customerType);
            Page<CustomerDto.CustomerSummaryDto> response = customerService.getCustomersByType(companyId, customerType, pageable);
            return ResponseEntity.ok(ApiResponse.success("고객 유형별 조회 성공", response));
        } catch (Exception e) {
            log.error("고객 유형별 조회 실패: companyId={}, type={}, {}", companyId, customerType, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("고객 유형별 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/company/{companyId}/status/{status}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "고객 상태별 조회", description = "특정 상태의 고객을 조회합니다")
    public ResponseEntity<ApiResponse<Page<CustomerDto.CustomerSummaryDto>>> getCustomersByStatus(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @Parameter(description = "고객 상태") @PathVariable String status,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("고객 상태별 조회 API 호출: companyId={}, status={}", companyId, status);
            Customer.CustomerStatus customerStatus = Customer.CustomerStatus.valueOf(status);
            Page<CustomerDto.CustomerSummaryDto> response = customerService.getCustomersByStatus(companyId, customerStatus, pageable);
            return ResponseEntity.ok(ApiResponse.success("고객 상태별 조회 성공", response));
        } catch (Exception e) {
            log.error("고객 상태별 조회 실패: companyId={}, status={}, {}", companyId, status, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("고객 상태별 조회에 실패했습니다: " + e.getMessage()));
        }
    }


    @GetMapping("/company/{companyId}/vip")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "VIP 고객 조회", description = "VIP 고객 목록을 조회합니다")
    public ResponseEntity<ApiResponse<List<CustomerDto.CustomerSummaryDto>>> getVipCustomers(
            @Parameter(description = "회사 ID") @PathVariable Long companyId) {
        try {
            log.info("VIP 고객 조회 API 호출: companyId={}", companyId);
            List<CustomerDto.CustomerSummaryDto> response = customerService.getVipCustomers(companyId);
            return ResponseEntity.ok(ApiResponse.success("VIP 고객 조회 성공", response));
        } catch (Exception e) {
            log.error("VIP 고객 조회 실패: companyId={}, {}", companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("VIP 고객 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/company/{companyId}/dormant")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "휴면 고객 조회", description = "휴면 고객 목록을 조회합니다")
    public ResponseEntity<ApiResponse<List<CustomerDto.CustomerSummaryDto>>> getDormantCustomers(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @Parameter(description = "휴면 기준 일수") @RequestParam(defaultValue = "90") int dormantDays) {
        try {
            log.info("휴면 고객 조회 API 호출: companyId={}, dormantDays={}", companyId, dormantDays);
            List<CustomerDto.CustomerSummaryDto> response = customerService.getDormantCustomers(companyId, dormantDays);
            return ResponseEntity.ok(ApiResponse.success("휴면 고객 조회 성공", response));
        } catch (Exception e) {
            log.error("휴면 고객 조회 실패: companyId={}, {}", companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("휴면 고객 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/company/{companyId}/outstanding")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "미수금 고객 조회", description = "미수금이 있는 고객을 조회합니다")
    public ResponseEntity<ApiResponse<Page<CustomerDto.CustomerSummaryDto>>> getCustomersWithOutstanding(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("미수금 고객 조회 API 호출: companyId={}", companyId);
            Page<CustomerDto.CustomerSummaryDto> response = customerService.getCustomersWithOutstanding(companyId, pageable);
            return ResponseEntity.ok(ApiResponse.success("미수금 고객 조회 성공", response));
        } catch (Exception e) {
            log.error("미수금 고객 조회 실패: companyId={}, {}", companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("미수금 고객 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/company/{companyId}/over-credit-limit")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "신용한도 초과 고객 조회", description = "신용한도를 초과한 고객을 조회합니다")
    public ResponseEntity<ApiResponse<List<CustomerDto.CustomerSummaryDto>>> getCustomersOverCreditLimit(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("신용한도 초과 고객 조회 API 호출: companyId={}", companyId);
            List<CustomerDto.CustomerSummaryDto> response = customerService.getCustomersOverCreditLimit(companyId);
            return ResponseEntity.ok(ApiResponse.success("신용한도 초과 고객 조회 성공", response));
        } catch (Exception e) {
            log.error("신용한도 초과 고객 조회 실패: companyId={}, {}", companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("신용한도 초과 고객 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/company/{companyId}/top")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "상위 고객 조회", description = "주문금액 기준 상위 고객을 조회합니다")
    public ResponseEntity<ApiResponse<Page<CustomerDto.CustomerSummaryDto>>> getTopCustomers(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @PageableDefault(size = 10) Pageable pageable) {
        try {
            log.info("상위 고객 조회 API 호출: companyId={}", companyId);
            Page<CustomerDto.CustomerSummaryDto> response = customerService.getTopCustomers(companyId, pageable);
            return ResponseEntity.ok(ApiResponse.success("상위 고객 조회 성공", response));
        } catch (Exception e) {
            log.error("상위 고객 조회 실패: companyId={}, {}", companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("상위 고객 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/company/{companyId}/statistics")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "고객 통계 조회", description = "고객 관련 통계 정보를 조회합니다")
    public ResponseEntity<ApiResponse<CustomerDto.CustomerStatsDto>> getCustomerStatistics(
            @Parameter(description = "회사 ID") @PathVariable Long companyId) {
        try {
            log.info("고객 통계 조회 API 호출: companyId={}", companyId);
            CustomerDto.CustomerStatsDto response = customerService.getCustomerStatistics(companyId);
            return ResponseEntity.ok(ApiResponse.success("고객 통계 조회 성공", response));
        } catch (Exception e) {
            log.error("고객 통계 조회 실패: companyId={}, {}", companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("고객 통계 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @PutMapping("/{customerId}/contact")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "고객 연락처 업데이트", description = "고객의 연락처 정보를 업데이트합니다")
    public ResponseEntity<ApiResponse<CustomerDto.CustomerResponseDto>> updateCustomerContact(
            @Parameter(description = "고객 ID") @PathVariable Long customerId,
            @Valid @RequestBody CustomerDto.CustomerContactUpdateDto contactDto) {
        try {
            log.info("고객 연락처 업데이트 API 호출: ID={}", customerId);
            CustomerDto.CustomerResponseDto response = customerService.updateCustomerContact(customerId, contactDto);
            return ResponseEntity.ok(ApiResponse.success("고객 연락처가 성공적으로 업데이트되었습니다", response));
        } catch (Exception e) {
            log.error("고객 연락처 업데이트 실패: ID={}, {}", customerId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("고객 연락처 업데이트에 실패했습니다: " + e.getMessage()));
        }
    }

    @PutMapping("/{customerId}/address")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "고객 주소 업데이트", description = "고객의 주소 정보를 업데이트합니다")
    public ResponseEntity<ApiResponse<CustomerDto.CustomerResponseDto>> updateCustomerAddress(
            @Parameter(description = "고객 ID") @PathVariable Long customerId,
            @Valid @RequestBody CustomerDto.CustomerAddressUpdateDto addressDto) {
        try {
            log.info("고객 주소 업데이트 API 호출: ID={}", customerId);
            CustomerDto.CustomerResponseDto response = customerService.updateCustomerAddress(customerId, addressDto);
            return ResponseEntity.ok(ApiResponse.success("고객 주소가 성공적으로 업데이트되었습니다", response));
        } catch (Exception e) {
            log.error("고객 주소 업데이트 실패: ID={}, {}", customerId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("고객 주소 업데이트에 실패했습니다: " + e.getMessage()));
        }
    }

    @PutMapping("/{customerId}/terms")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "고객 거래조건 업데이트", description = "고객의 거래조건을 업데이트합니다")
    public ResponseEntity<ApiResponse<CustomerDto.CustomerResponseDto>> updateCustomerTerms(
            @Parameter(description = "고객 ID") @PathVariable Long customerId,
            @Valid @RequestBody CustomerDto.CustomerTermsUpdateDto termsDto) {
        try {
            log.info("고객 거래조건 업데이트 API 호출: ID={}", customerId);
            CustomerDto.CustomerResponseDto response = customerService.updateCustomerTerms(customerId, termsDto);
            return ResponseEntity.ok(ApiResponse.success("고객 거래조건이 성공적으로 업데이트되었습니다", response));
        } catch (Exception e) {
            log.error("고객 거래조건 업데이트 실패: ID={}, {}", customerId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("고객 거래조건 업데이트에 실패했습니다: " + e.getMessage()));
        }
    }


    @PutMapping("/{customerId}/status")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "고객 상태 변경", description = "고객의 상태를 변경합니다")
    public ResponseEntity<ApiResponse<Void>> changeCustomerStatus(
            @Parameter(description = "고객 ID") @PathVariable Long customerId,
            @Valid @RequestBody CustomerDto.CustomerStatusChangeDto statusChangeDto) {
        try {
            log.info("고객 상태 변경 API 호출: ID={}", customerId);
            customerService.changeCustomerStatus(customerId, statusChangeDto);
            return ResponseEntity.ok(ApiResponse.success("고객 상태가 성공적으로 변경되었습니다"));
        } catch (Exception e) {
            log.error("고객 상태 변경 실패: ID={}, {}", customerId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("고객 상태 변경에 실패했습니다: " + e.getMessage()));
        }
    }

    @PutMapping("/{customerId}/toggle-active")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "고객 활성화 토글", description = "고객의 활성화 상태를 토글합니다")
    public ResponseEntity<ApiResponse<CustomerDto.CustomerResponseDto>> toggleCustomerActive(
            @Parameter(description = "고객 ID") @PathVariable Long customerId) {
        try {
            log.info("고객 활성화 토글 API 호출: ID={}", customerId);
            CustomerDto.CustomerResponseDto response = customerService.toggleCustomerActive(customerId);
            return ResponseEntity.ok(ApiResponse.success("고객 활성화 상태가 성공적으로 변경되었습니다", response));
        } catch (Exception e) {
            log.error("고객 활성화 토글 실패: ID={}, {}", customerId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("고객 활성화 상태 변경에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/check-duplicate/code")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "고객코드 중복 확인", description = "고객코드의 중복 여부를 확인합니다")
    public ResponseEntity<ApiResponse<Boolean>> checkCustomerCodeDuplicate(
            @Parameter(description = "회사 ID") @RequestParam Long companyId,
            @Parameter(description = "고객 코드") @RequestParam String customerCode,
            @Parameter(description = "제외할 고객 ID") @RequestParam(required = false) Long excludeCustomerId) {
        try {
            log.info("고객코드 중복 확인 API 호출: companyId={}, code={}", companyId, customerCode);
            boolean isDuplicate = customerService.isCustomerCodeDuplicate(companyId, customerCode, excludeCustomerId);
            return ResponseEntity.ok(ApiResponse.success("고객코드 중복 확인 완료", isDuplicate));
        } catch (Exception e) {
            log.error("고객코드 중복 확인 실패: companyId={}, code={}, {}", companyId, customerCode, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("고객코드 중복 확인에 실패했습니다: " + e.getMessage()));
        }
    }

    @PostMapping("/company/{companyId}/update-order-statistics")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "고객 주문 통계 일괄 업데이트", description = "모든 고객의 주문 통계를 일괄 업데이트합니다")
    public ResponseEntity<ApiResponse<Void>> updateAllCustomerOrderStatistics(
            @Parameter(description = "회사 ID") @PathVariable Long companyId) {
        try {
            log.info("고객 주문 통계 일괄 업데이트 API 호출: companyId={}", companyId);
            customerService.updateAllCustomerOrderStatistics(companyId);
            return ResponseEntity.ok(ApiResponse.success("고객 주문 통계가 성공적으로 업데이트되었습니다"));
        } catch (Exception e) {
            log.error("고객 주문 통계 업데이트 실패: companyId={}, {}", companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("고객 주문 통계 업데이트에 실패했습니다: " + e.getMessage()));
        }
    }


    @PostMapping("/company/{companyId}/convert-dormant")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "휴면 고객 자동 전환", description = "일정 기간 연락이 없는 고객을 휴면 상태로 전환합니다")
    public ResponseEntity<ApiResponse<Integer>> convertDormantCustomers(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @Parameter(description = "휴면 기준 일수") @RequestParam(defaultValue = "90") int dormantDays) {
        try {
            log.info("휴면 고객 자동 전환 API 호출: companyId={}, dormantDays={}", companyId, dormantDays);
            int convertedCount = customerService.convertDormantCustomers(companyId, dormantDays);
            return ResponseEntity.ok(ApiResponse.success("휴면 고객 전환이 완료되었습니다", convertedCount));
        } catch (Exception e) {
            log.error("휴면 고객 자동 전환 실패: companyId={}, {}", companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("휴면 고객 자동 전환에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/{customerId}/lifetime-value")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "고객 라이프타임 가치 계산", description = "고객의 라이프타임 가치를 계산합니다")
    public ResponseEntity<ApiResponse<BigDecimal>> calculateCustomerLifetimeValue(
            @Parameter(description = "고객 ID") @PathVariable Long customerId) {
        try {
            log.info("고객 라이프타임 가치 계산 API 호출: ID={}", customerId);
            BigDecimal clv = customerService.calculateCustomerLifetimeValue(customerId);
            return ResponseEntity.ok(ApiResponse.success("고객 라이프타임 가치 계산 완료", clv));
        } catch (Exception e) {
            log.error("고객 라이프타임 가치 계산 실패: ID={}, {}", customerId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("고객 라이프타임 가치 계산에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/company/{companyId}/churn-risk")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "이탈 위험 고객 분석", description = "이탈 위험이 높은 고객을 분석합니다")
    public ResponseEntity<ApiResponse<List<CustomerDto.CustomerSummaryDto>>> getChurnRiskCustomers(
            @Parameter(description = "회사 ID") @PathVariable Long companyId) {
        try {
            log.info("이탈 위험 고객 분석 API 호출: companyId={}", companyId);
            List<CustomerDto.CustomerSummaryDto> response = customerService.getChurnRiskCustomers(companyId);
            return ResponseEntity.ok(ApiResponse.success("이탈 위험 고객 분석 완료", response));
        } catch (Exception e) {
            log.error("이탈 위험 고객 분석 실패: companyId={}, {}", companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("이탈 위험 고객 분석에 실패했습니다: " + e.getMessage()));
        }
    }
}




